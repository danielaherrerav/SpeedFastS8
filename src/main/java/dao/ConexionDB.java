package dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Clase encargada de abrir conexiones con la base de datos MySQL.
 */
public class ConexionDB {

    // Datos de conexión
    private static final String URL = "jdbc:mysql://localhost:3306/speedfast_db";
    private static final String USER = "root";
    private static final String PASSWORD = "Dario310516.";

    /**
     * Abre y devuelve una conexión nueva con la base de datos.
     */
    public static Connection conectar() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }
}
