package control;

import com.itextpdf.io.image.ImageData;
import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Image;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.properties.HorizontalAlignment;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.UnitValue;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import model.infoconsegna.InfoConsegnaBean;
import model.infoconsegna.InfoConsegnaDAO;
import model.ordine.OrdineBean;
import model.ordine.OrdineDAO;
import model.orderitem.OrderItemBean;
import model.orderitem.OrderItemDAO;
import model.utente.UtenteBean;
import model.utente.UtenteDAO;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.List;

@WebServlet("/FatturaServlet")
public class FatturaServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String idOrdineStr = request.getParameter("id_ordine");
        if (idOrdineStr == null || idOrdineStr.trim().isEmpty()) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "ID Ordine mancante.");
            return;
        }

        try {
            long idOrdine = Long.parseLong(idOrdineStr);

            OrdineDAO ordineDAO = new OrdineDAO();
            OrderItemDAO orderItemDAO = new OrderItemDAO();
            UtenteDAO utenteDAO = new UtenteDAO();
            InfoConsegnaDAO infoConsegnaDAO = new InfoConsegnaDAO();

            OrdineBean ordine = ordineDAO.doRetrieveByKey(idOrdine);
            if (ordine == null) {
                response.sendError(HttpServletResponse.SC_NOT_FOUND, "Ordine non trovato.");
                return;
            }

            UtenteBean utente = utenteDAO.doRetrieveByKey(ordine.getIdUtente());
            InfoConsegnaBean infoConsegna = infoConsegnaDAO.doRetrieveByKey(ordine.getInfoConsegna());
            List<OrderItemBean> items = orderItemDAO.doRetrieveByOrdine(idOrdine);

            response.setContentType("application/pdf");
            String fileName = String.format("Fattura_Wearful_%d.pdf", ordine.getId());
            response.setHeader("Content-Disposition", "attachment; filename=\"" + fileName + "\"");

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            PdfWriter writer = new PdfWriter(baos);
            PdfDocument pdf = new PdfDocument(writer);
            Document document = new Document(pdf, PageSize.A4);
            document.setMargins(36, 36, 36, 36);

            try {
                String logoPath = getServletContext().getRealPath("/img/wide_logo.png");
                if (logoPath != null) {
                    ImageData imageData = ImageDataFactory.create(logoPath);
                    Image logo = new Image(imageData);
                    logo.setWidth(150f);
                    logo.setHorizontalAlignment(HorizontalAlignment.CENTER);
                    logo.setMarginBottom(20);
                    document.add(logo);
                } else {
                    System.err.println("Impossibile trovare il percorso del logo: /img/wide_logo.png");
                }
            } catch (Exception e) {
                System.err.println("Errore durante il caricamento del logo. La generazione della fattura continua.");
                e.printStackTrace();
            }

            document.add(new Paragraph("Fattura")
                    .setTextAlignment(TextAlignment.CENTER)
                    .setBold()
                    .setFontSize(22)
                    .setMarginBottom(30));

            Table infoTable = new Table(UnitValue.createPercentArray(new float[]{1, 1}))
                    .useAllAvailableWidth();

            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
            String datiFattura = String.format("Numero Fattura: %d\nData: %s",
                    ordine.getId(),
                    sdf.format(ordine.getDataOrdine()));
            infoTable.addCell(createStyledCell("Dati Fattura", datiFattura));

            String datiCliente = String.format("Username: %s\nEmail: %s\n\nIndirizzo di Spedizione:\n%s\n%s, %s",
                    utente.getUsername(),
                    utente.getEmail(),
                    infoConsegna.getVia(),
                    infoConsegna.getCitta(),
                    infoConsegna.getCap());
            infoTable.addCell(createStyledCell("Destinatario", datiCliente));
            document.add(infoTable);

            document.add(new Paragraph("Dettaglio Prodotti").setBold().setMarginTop(25).setMarginBottom(10));
            Table productTable = new Table(UnitValue.createPercentArray(new float[]{2, 4, 1, 2, 1, 2}))
                    .useAllAvailableWidth();

            productTable.addHeaderCell(createHeaderCell("ID Prodotto"));
            productTable.addHeaderCell(createHeaderCell("Prodotto"));
            productTable.addHeaderCell(createHeaderCell("Qtà"));
            productTable.addHeaderCell(createHeaderCell("Prezzo Unit."));
            productTable.addHeaderCell(createHeaderCell("IVA"));
            productTable.addHeaderCell(createHeaderCell("Subtotale"));

            double subtotaleComplessivo = 0;
            double totaleIva = 0;

            for (OrderItemBean item : items) {
                double prezzoUnitario = item.getPrezzo();
                int quantita = item.getQuantita();
                double subtotalItem = prezzoUnitario * quantita;
                double ivaItem = subtotalItem * (item.getIva() / 100.0);

                subtotaleComplessivo += subtotalItem;
                totaleIva += ivaItem;

                productTable.addCell(String.valueOf(item.getIdProdotto())).setTextAlignment(TextAlignment.CENTER);
                productTable.addCell(item.getNome()).setTextAlignment(TextAlignment.CENTER);
                productTable.addCell(String.valueOf(quantita)).setTextAlignment(TextAlignment.CENTER);
                productTable.addCell(String.format("€ %.2f", prezzoUnitario)).setTextAlignment(TextAlignment.RIGHT);
                productTable.addCell(String.format("%d%%", item.getIva())).setTextAlignment(TextAlignment.CENTER);
                productTable.addCell(String.format("€ %.2f", subtotalItem)).setTextAlignment(TextAlignment.RIGHT);
            }
            document.add(productTable);

            Table totalsTable = new Table(UnitValue.createPercentArray(new float[]{7, 2}))
                    .useAllAvailableWidth()
                    .setMarginTop(20);

            totalsTable.addCell(createTotalCell("Imponibile:"));
            totalsTable.addCell(createTotalValueCell(String.format("€ %.2f", subtotaleComplessivo)));
            totalsTable.addCell(createTotalCell("Totale IVA:"));
            totalsTable.addCell(createTotalValueCell(String.format("€ %.2f", totaleIva)));
            totalsTable.addCell(createTotalCell("TOTALE FATTURA:").setBold().setFontSize(14));
            totalsTable.addCell(createTotalValueCell(String.format("€ %.2f", subtotaleComplessivo + totaleIva)).setBold().setFontSize(14));
            document.add(totalsTable);

            document.close();

            response.setContentLength(baos.size());
            baos.writeTo(response.getOutputStream());
            response.getOutputStream().flush();

        } catch (NumberFormatException e) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "ID Ordine non valido.");
        } catch (SQLException e) {
            e.printStackTrace();
            throw new ServletException("Errore di accesso al database", e);
        } catch (Exception e) {
            e.printStackTrace();
            throw new ServletException("Errore durante la generazione del PDF", e);
        }
    }

    private Cell createHeaderCell(String text) {
        return new Cell().add(new Paragraph(text).setBold()).setTextAlignment(TextAlignment.CENTER);
    }

    private Cell createStyledCell(String title, String content) {
        Cell cell = new Cell();
        cell.add(new Paragraph(title).setBold().setFontSize(14).setMarginBottom(5));
        cell.add(new Paragraph(content));
        cell.setBorder(null);
        return cell;
    }

    private Cell createTotalCell(String text) {
        return new Cell().add(new Paragraph(text)).setTextAlignment(TextAlignment.RIGHT).setBorder(null);
    }

    private Cell createTotalValueCell(String text) {
        return new Cell().add(new Paragraph(text)).setTextAlignment(TextAlignment.RIGHT).setBorder(null);
    }
}