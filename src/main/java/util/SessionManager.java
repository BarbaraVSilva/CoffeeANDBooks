package util;

import model.Usuario;
import java.util.Date;

public class SessionManager {
    private static Usuario usuarioLogado;
    private static Date horaLogin;

    public static void setUsuario(Usuario u) {
        usuarioLogado = u;
        horaLogin = new Date();
    }

    public static Usuario getUsuario() {
        return usuarioLogado;
    }

    public static String getTempoSessao() {
        if (horaLogin == null) return "00:00:00";
        long diff = new Date().getTime() - horaLogin.getTime();
        long seconds = (diff / 1000) % 60;
        long minutes = (diff / (1000 * 60)) % 60;
        long hours = (diff / (1000 * 60 * 60)) % 24;
        return String.format("%02d:%02d:%02d", hours, minutes, seconds);
    }
    
    public static boolean isAdmin() {
        return usuarioLogado != null && "ADMIN".equals(usuarioLogado.getRole());
    }

    public static void logout() {
        usuarioLogado = null;
        horaLogin = null;
    }
}
