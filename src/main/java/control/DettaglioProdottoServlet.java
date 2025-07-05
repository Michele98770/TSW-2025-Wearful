package control;

import model.prodotto.ProdottoBean;
import model.prodotto.ProdottoDAO;
import model.gruppoprodotti.GruppoProdottiDAO;
import control.JsonUtil;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Comparator;

@WebServlet("/DettaglioProdottoServlet")
public class DettaglioProdottoServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    private ProdottoDAO prodottoDAO;
    private GruppoProdottiDAO gruppoProdottiDAO; // NUOVO: Istanza per GruppoProdottiDAO

    public void init() throws ServletException {
        super.init();
        prodottoDAO = new ProdottoDAO();
        gruppoProdottiDAO = new GruppoProdottiDAO();
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String idParam = request.getParameter("id");
        if (idParam == null || idParam.isEmpty()) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "ID prodotto non specificato.");
            return;
        }

        try {
            Long productId = Long.parseLong(idParam);
            ProdottoBean mainProduct = prodottoDAO.doRetrieveByKey(productId);

            if (mainProduct == null) {
                response.sendError(HttpServletResponse.SC_NOT_FOUND, "Prodotto non trovato.");
                return;
            }

            List<ProdottoBean> allVariants = prodottoDAO.doRetrieveByGruppo(mainProduct.getGruppo());

            Set<String> availableColors = new HashSet<>();
            Map<String, List<String>> sizesByColor = new HashMap<>();
            Map<String, Map<String, ProdottoBean>> productsByColorAndSize = new HashMap<>();

            for (ProdottoBean variant : allVariants) {
                availableColors.add(variant.getColore());
                sizesByColor.computeIfAbsent(variant.getColore(), k -> new ArrayList<>()).add(variant.getTaglia());
                productsByColorAndSize.computeIfAbsent(variant.getColore(), k -> new HashMap<>())
                        .put(variant.getTaglia(), variant);
            }

            Map<String, List<String>> sortedSizesByColor = new HashMap<>();
            for (Map.Entry<String, List<String>> entry : sizesByColor.entrySet()) {
                List<String> sizes = entry.getValue();
                sizes.sort(Comparator.comparingInt(this::getSizeOrder));
                sortedSizesByColor.put(entry.getKey(), sizes);
            }

            String selectedColor = mainProduct.getColore();
            String selectedSize = mainProduct.getTaglia();

            String productGroupName = "Nome Gruppo Sconosciuto"; // Valore di default
            try {
                String retrievedGroupName = gruppoProdottiDAO.doRetrieveByKey(mainProduct.getGruppo()).getNome();
                if (retrievedGroupName != null && !retrievedGroupName.isEmpty()) {
                    productGroupName = retrievedGroupName;
                }
            } catch (SQLException e) {
                System.err.println("Errore SQL nel recupero nome gruppo: " + e.getMessage());
                e.printStackTrace();
                request.setAttribute("errorMessage", "Errore nel caricamento del nome del gruppo.");
            }


            String jsonProductsString = JsonUtil.convertProductsToJson(productsByColorAndSize);

            request.setAttribute("mainProduct", mainProduct);
            request.setAttribute("allVariants", allVariants);
            request.setAttribute("availableColors", availableColors);
            request.setAttribute("sizesByColor", sortedSizesByColor);
            request.setAttribute("productsByColorAndSize", productsByColorAndSize);
            request.setAttribute("selectedColor", selectedColor);
            request.setAttribute("selectedSize", selectedSize);
            request.setAttribute("productGroupName", productGroupName); // NUOVO: Attributo per il nome del gruppo
            request.setAttribute("jsonProducts", jsonProductsString); // NUOVO: Attributo per la stringa JSON

            request.getRequestDispatcher("/dettaglioProdotto.jsp").forward(request, response);

        } catch (NumberFormatException e) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "ID prodotto non valido.");
        } catch (SQLException e) {
            System.err.println("Errore SQL nella DettaglioProdottoServlet: " + e.getMessage());
            e.printStackTrace();
            request.setAttribute("errorMessage", "Errore nel caricamento dei dettagli del prodotto.");
            request.getRequestDispatcher("/error.jsp").forward(request, response); // Potresti avere una pagina di errore generica
        }
    }


    private int getSizeOrder(String size) {
        switch (size.toUpperCase()) {
            case "XXS": return 1;
            case "XS":  return 2;
            case "S":   return 3;
            case "M":   return 4;
            case "L":   return 5;
            case "XL":  return 6;
            case "XXL": return 7;
            default:    return 99;
        }
    }
}