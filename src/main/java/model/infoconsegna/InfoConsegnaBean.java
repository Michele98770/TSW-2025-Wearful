package model.infoconsegna;

import java.io.Serializable;

public class InfoConsegnaBean implements Serializable {
    private static final long serialVersionUID = 1L;

    private Long id;
    private String citta;
    private int cap;
    private String via;
    private String altro;
    private String destinatario;
    private String idUtente;


    public InfoConsegnaBean(Long id, String citta, int cap, String via,
                        String altro, String destinatario, String idUtente) {
        this.id = id;
        this.citta = citta;
        this.cap = cap;
        this.via = via;
        this.altro = altro;
        this.destinatario = destinatario;
        this.idUtente = idUtente;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCitta() {
        return citta;
    }

    public void setCitta(String citta) {
        this.citta = citta;
    }

    public int getCap() {
        return cap;
    }

    public void setCap(int cap) {
        this.cap = cap;
    }

    public String getVia() {
        return via;
    }

    public void setVia(String via) {
        this.via = via;
    }

    public String getAltro() {
        return altro;
    }

    public void setAltro(String altro) {
        this.altro = altro;
    }

    public String getDestinatario() {
        return destinatario;
    }

    public void setDestinatario(String destinatario) {
        this.destinatario = destinatario;
    }

    public String getIdUtente() {
        return idUtente;
    }

    public void setIdUtente(String idUtente) {
        this.idUtente = idUtente;
    }

    @Override
    public String toString() {
        return "InfoConsegna{" + "id=" + id + ", citta=" + citta +
                ", destinatario=" + destinatario + '}';
    }
}