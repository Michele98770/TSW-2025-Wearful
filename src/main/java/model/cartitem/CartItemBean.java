package model.cartitem;

import java.io.Serializable;

public class CartItemBean implements Serializable {
    private static final long serialVersionUID = 1L;

    private Long id;
    private Long idProdotto; // FK a Prodotto
    private Long idCarrello; // FK a Carrello
    private int quantita;
    private boolean personalizzato;
    private String imgPath;


    public CartItemBean(Long id, Long idProdotto, Long idCarrello,
                    int quantita, boolean personalizzato, String imgPath) {
        this.id = id;
        this.idProdotto = idProdotto;
        this.idCarrello = idCarrello;
        this.quantita = quantita;
        this.personalizzato = personalizzato;
        this.imgPath = imgPath;
    }

    // Getters e Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getIdProdotto() {
        return idProdotto;
    }

    public void setIdProdotto(Long idProdotto) {
        this.idProdotto = idProdotto;
    }

    public Long getIdCarrello() {
        return idCarrello;
    }

    public void setIdCarrello(Long idCarrello) {
        this.idCarrello = idCarrello;
    }

    public int getQuantita() {
        return quantita;
    }

    public void setQuantita(int quantita) {
        this.quantita = quantita;
    }

    public boolean isPersonalizzato() {
        return personalizzato;
    }

    public void setPersonalizzato(boolean personalizzato) {
        this.personalizzato = personalizzato;
    }

    public String getImgPath() {
        return imgPath;
    }

    public void setImgPath(String imgPath) {
        this.imgPath = imgPath;
    }

    @Override
    public String toString() {
        return "CartItem{" + "id=" + id + ", idProdotto=" + idProdotto +
                ", quantita=" + quantita + '}';
    }
}