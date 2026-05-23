package util;

import java.sql.DriverManager;
import java.sql.SQLException;

/**
 *
 * @author Aluno
 */
public class Banco {
      //criar atributos
    public static String bancoDados, usuario, senha, servidor;
    public static int porta;

    //variavel de conexao
    public static java.sql.Connection conexao = null;
    
    //define valores padrão
    static {
        //mysql e mariaDB
        bancoDados = "coffeebooks_db";
        usuario = "root";
        senha = ""; // Default empty password for Coffee&Books db
        servidor = "localhost";
        porta = 3306;
        /*
        //sqlServer
        bancoDados = "Loja";
        usuario = "sa";
        senha = "123456";
        servidor = "localhost";
        porta = 1433;
        */
    }
    
    public static void conectar() throws SQLException {
        // First, check driver class and establish connection to server root to ensure the database exists
        String rootUrl = "jdbc:mariadb://" + servidor + ":" + porta + "/";
        try {
            Class.forName("org.mariadb.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            rootUrl = "jdbc:mysql://" + servidor + ":" + porta + "/";
        }

        try (java.sql.Connection rootConn = DriverManager.getConnection(rootUrl, usuario, senha);
             java.sql.Statement stmt = rootConn.createStatement()) {
            stmt.executeUpdate("CREATE DATABASE IF NOT EXISTS " + bancoDados);
        } catch (Exception e) {
            // Log warning, but proceed to main connection (it will fail anyway if DB is missing and root failed)
            System.err.println("Aviso: Falha ao verificar/criar banco de dados no servidor: " + e.getMessage());
        }

        // Now connect to the specific database
        String url = "jdbc:mariadb://" + servidor + ":" + porta + "/" + bancoDados;
        try {
            Class.forName("org.mariadb.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            url = "jdbc:mysql://" + servidor + ":" + porta + "/" + bancoDados;
        }

        conexao = DriverManager.getConnection(url, usuario, senha);
    }
    
    public static void desconectar() throws SQLException {
        if (conexao != null && !conexao.isClosed()) {
            conexao.close();
        }
    }

    public static java.sql.Connection obterConexao() 
                throws SQLException {
        if (conexao == null || conexao.isClosed()) {
            conectar();
        }
        return conexao;
    }

}
