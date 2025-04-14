package  model.prodotto;

import java.io.Serializable;
//import java.math.BigDecimal;

public class ProdottoBean implements Serializable {
    private static final long serialVersionUID = 1L;


    private Long id;
    private String nome;
    private String descrizione;
    private String taglia;
    private String colore;
    private String categoria;
    private float prezzo;
    private int iva;
    private boolean personalizzabile;
    private String imgPath;
    private String publisher;
    private Long gruppo;


    public ProdottoBean(Long id, String nome, String descrizione, String taglia,
                    String colore, String categoria, float prezzo, int iva,
                    boolean personalizzabile, String imgPath, String publisher, Long gruppo) {
        this.id = id;
        this.nome = nome;
        this.descrizione = descrizione;
        this.taglia = taglia;
        this.colore = colore;
        this.categoria = categoria;
        this.prezzo = prezzo;
        this.iva = iva;
        this.personalizzabile = personalizzabile;
        this.imgPath = imgPath;
        this.publisher = publisher;
        this.gruppo = gruppo;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getDescrizione() {
        return descrizione;
    }

    public void setDescrizione(String descrizione) {
        this.descrizione = descrizione;
    }

    public String getTaglia() {
        return taglia;
    }

    public void setTaglia(String taglia) {
        this.taglia = taglia;
    }

    public String getColore() {
        return colore;
    }

    public void setColore(String colore) {
        this.colore = colore;
    }

    public String getCategoria() {
        return categoria;
    }

    public void setCategoria(String categoria) {
        this.categoria = categoria;
    }

    public float getPrezzo() {
        return prezzo;
    }

    public void setPrezzo(float prezzo) {
        this.prezzo = prezzo;
    }

    public boolean isPersonalizzabile() {
        return personalizzabile;
    }

    public void setPersonalizzabile(boolean personalizzabile) {
        this.personalizzabile = personalizzabile;
    }

    public String getImgPath() {
        return imgPath;
    }

    public void setImgPath(String imgPath) {
        this.imgPath = imgPath;
    }

    public String getPublisher() {
        return publisher;
    }

    public void setPublisher(String publisher) {
        this.publisher = publisher;
    }

    public Long getGruppo() {
        return gruppo;
    }

    public void setGruppo(Long gruppo) {
        this.gruppo = gruppo;
    }

    public int getIva() {
        return iva;
    }

    public void setIva(int iva) {
        this.iva = iva;
    }


    public float getPrezzoConIva() {
        return prezzo+(((float)(iva)/100) * prezzo);
    }

    @Override
    public String toString() {
        return "Prodotto{" + "id=" + id + ", nome=" + nome + ", prezzo=" + prezzo + '}';
    }
}