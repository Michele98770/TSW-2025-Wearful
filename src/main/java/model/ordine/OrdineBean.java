package model.ordine;

import java.io.Serializable;
import java.util.Date;

public class OrdineBean implements Serializable {
    private static final long serialVersionUID = 1L;

    private Long id;
    private String idUtente;
    private Long infoConsegna;
    private Date dataOrdine;

    public OrdineBean(Long id, String idUtente, Long infoConsegna, Date dataOrdine) {
        this.id = id;
        this.idUtente = idUtente;
        this.infoConsegna = infoConsegna;
        this.dataOrdine = dataOrdine;
    }

    public OrdineBean() {

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

    public Long getInfoConsegna() {
        return infoConsegna;
    }

    public void setInfoConsegna(Long infoConsegna) {
        this.infoConsegna = infoConsegna;
    }

    public Date getDataOrdine() {
        return dataOrdine;
    }

    public void setDataOrdine(Date dataOrdine) {
        this.dataOrdine = dataOrdine;
    }

    @Override
    public String toString() {
        return "Ordine{" + "id=" + id + ", dataOrdine=" + dataOrdine + '}';
    }
}