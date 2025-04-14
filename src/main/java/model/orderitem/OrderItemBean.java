package model.orderitem;

import java.io.Serializable;


public class OrderItemBean implements Serializable {
    private static final long serialVersionUID = 1L;


    private Long id;
    private String nome;
    private Long idProdotto; // FK a Prodotto
    private Long idOrdine; // FK a Ordine
    private float prezzo;
    private int quantita;
    private int iva;

    public OrderItemBean(Long id, String nome, Long idProdotto, Long idOrdine,
                     float prezzo, int quantita, int iva) {
        this.id = id;
        this.nome = nome;
        this.idProdotto = idProdotto;
        this.idOrdine = idOrdine;
        this.prezzo = prezzo;
        this.quantita = quantita;
        this.iva = iva;
    }

    // Getters e Setters
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

    public Long getIdProdotto() {
        return idProdotto;
    }

    public void setIdProdotto(Long idProdotto) {
        this.idProdotto = idProdotto;
    }

    public Long getIdOrdine() {
        return idOrdine;
    }

    public void setIdOrdine(Long idOrdine) {
        this.idOrdine = idOrdine;
    }

    public float getPrezzo() {
        return prezzo;
    }

    public void setPrezzo(float prezzo) {
        this.prezzo = prezzo;
    }

    public int getQuantita() {
        return quantita;
    }

    public void setQuantita(int quantita) {
        this.quantita = quantita;
    }

    public int getIva() {
        return iva;
    }

    public void setIva(int iva) {
        this.iva = iva;
    }


    public float getTotale() {
        return (prezzo*quantita);
    }

    public float getTotaleConIva() {
        return getTotale() + (getTotale() * ((float)(iva)/100));
    }

    @Override
    public String toString() {
        return "OrderItem{" + "id=" + id + ", nome=" + nome + ", quantita=" + quantita + '}';
    }
}