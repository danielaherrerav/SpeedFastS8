package dao;

import java.sql.Connection;
import java.sql.SQLException;

public class PruebaConexion {

    public static void main(String[] args) {
        try (Connection con = ConexionDB.conectar()) {
            System.out.println("Conexión exitosa a: " + con.getCatalog());
        } catch (SQLException e) {
            System.out.println("Error al conectar: " + e.getMessage());
        }
    }
}
