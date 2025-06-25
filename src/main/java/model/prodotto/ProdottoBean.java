package model.prodotto;

import java.io.Serializable;
//import java.math.BigDecimal;

public class ProdottoBean implements Serializable {
    private static final long serialVersionUID = 1L;

    private Long id;
    private String nome;
    private String descrizione;
    private String taglia;
    private String colore;
    private String codiceColore; // NUOVO CAMPO AGGIUNTO
    private String categoria;
    private float prezzo;
    private int iva;
    private int disponibilita;
    private boolean personalizzabile;
    private String imgPath;
    private String publisher;
    private Long gruppo;

    // Costruttore completo aggiornato
    public ProdottoBean(Long id, String nome, String descrizione, String taglia,
                        String colore, String codiceColore, String categoria, float prezzo, int iva, int disponibilita,
                        boolean personalizzabile, String imgPath, String publisher, Long gruppo) {
        this.id = id;
        this.nome = nome;
        this.descrizione = descrizione;
        this.taglia = taglia;
        this.colore = colore;
        this.codiceColore = codiceColore; // Assegna il nuovo campo
        this.categoria = categoria;
        this.prezzo = prezzo;
        this.iva = iva;
        this.disponibilita = disponibilita;
        this.personalizzabile = personalizzabile;
        this.imgPath = imgPath;
        this.publisher = publisher;
        this.gruppo = gruppo;
    }

    public ProdottoBean() {
        // Costruttore vuoto, inizializza codiceColore a null o valore predefinito
        this.codiceColore = null;
    }

    // Getter e Setter per codiceColore
    public String getCodiceColore() {
        return codiceColore;
    }

    public void setCodiceColore(String codiceColore) {
        this.codiceColore = codiceColore;
    }

    // --- Metodi esistenti (getters e setters) ---
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

    public int getIva() {
        return iva;
    }

    public void setIva(int iva) {
        this.iva = iva;
    }

    public int getDisponibilita() {
        return disponibilita;
    }

    public void setDisponibilita(int disponibilita) {
        this.disponibilita = disponibilita;
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
}