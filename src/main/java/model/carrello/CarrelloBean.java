package model.carrello;


import java.io.Serializable;

public class CarrelloBean implements Serializable {
    private static final long serialVersionUID = 1L;

    private Long id;
    private String idUtente; // FK a Utente


    public CarrelloBean(Long id, String idUtente) {
        this.id = id;
        this.idUtente = idUtente;
    }


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getIdUtente() {
        return idUtente;
    }

    public void setIdUtente(String idUtente) {
        this.idUtente = idUtente;
    }

    @Override
    public String toString() {
        return "Carrello{" + "id=" + id + ", idUtente=" + idUtente + '}';
    }
}