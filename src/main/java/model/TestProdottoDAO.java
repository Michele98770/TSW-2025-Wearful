package model;

import model.prodotto.ProdottoBean;
import model.prodotto.ProdottoDAO;

import java.sql.SQLException;
import java.util.List;

public class TestProdottoDAO {
    public static void main(String[] args) {
        ProdottoDAO prodottoDAO = new ProdottoDAO();
        try {
            List<ProdottoBean> prodotti = prodottoDAO.doRetrieveAll();
            System.out.println("Prodotti trovati: " + prodotti.size());
            for (ProdottoBean p : prodotti) {
                System.out.println(p.getNome() + " - " + p.getPrezzo());
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}