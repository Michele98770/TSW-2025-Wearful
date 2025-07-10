package model.utente;

import java.io.Serializable;

public class UtenteBean implements Serializable {
    private static final long serialVersionUID = 1L;

    private String email;
    private String username;
    private String telefono;
    private String password;
    private boolean isAdmin;


    public UtenteBean(String email, String username, String telefono, String password, boolean isAdmin) {
        this.email = email;
        this.username = username;
        this.telefono = telefono;
        this.password = password;
        this.isAdmin = isAdmin;
    }

    public UtenteBean() {

    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public boolean isAdmin() {
        return isAdmin;
    }

    public void setAdmin(boolean isAdmin) {
        this.isAdmin = isAdmin;
    }

    @Override
    public String toString() {
        return "Utente{" + "email=" + email + ", username=" + username +
                ", isAdmin=" + isAdmin + '}';
    }
}