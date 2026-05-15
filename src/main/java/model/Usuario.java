package model;

import java.util.Date;

public class Usuario {
    private int id;
    private String username;
    private String password;
    private String role;
    private Date dataUltimaSenha;

    public Usuario() {}

    public Usuario(int id, String username, String password, String role, Date dataUltimaSenha) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.role = role;
        this.dataUltimaSenha = dataUltimaSenha;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }
    public Date getDataUltimaSenha() { return dataUltimaSenha; }
    public void setDataUltimaSenha(Date dataUltimaSenha) { this.dataUltimaSenha = dataUltimaSenha; }
    
    public boolean isSenhaExpirada() {
        if (dataUltimaSenha == null) return true;
        long diff = new Date().getTime() - dataUltimaSenha.getTime();
        long days = diff / (1000 * 60 * 60 * 24);
        return days >= 90;
    }
}
