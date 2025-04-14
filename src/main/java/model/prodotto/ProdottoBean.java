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

    // Getters e Setters (abbreviato per brevità)
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


    public int getIva() {
        return iva;
    }

    public void setIva(int iva) {
        this.iva = iva;
    }

    // Metodo per calcolare il prezzo con IVA
    public float getPrezzoConIva() {
        return prezzo+(((float)(iva)/100) * prezzo);
    }

    @Override
    public String toString() {
        return "Prodotto{" + "id=" + id + ", nome=" + nome + ", prezzo=" + prezzo + '}';
    }
}