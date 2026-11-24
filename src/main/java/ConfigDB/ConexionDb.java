package ConfigDB;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConexionDb {
    private static final String URL="jdbc:mysql://localhost:3307/Candidaturas";
    private static final String USER="user";
    private static final String PASSWORD="pwd";
    public static Connection obtenerConexion() throws SQLException {
        return DriverManager.getConnection(URL,USER,PASSWORD);

        }
}
