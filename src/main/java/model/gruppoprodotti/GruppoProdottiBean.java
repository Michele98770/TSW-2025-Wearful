package model.gruppoprodotti;

import java.io.Serializable;

public class GruppoProdottiBean implements Serializable {
    private static final long serialVersionUID = 1L;

    private Long id;
    private String nome;


    public GruppoProdottiBean(Long id, String nome) {
        this.id = id;
        this.nome = nome;
    }

    public GruppoProdottiBean() {

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

    @Override
    public String toString() {
        return "GruppoProdotti{" + "id=" + id + ", nome=" + nome + '}';
    }
}