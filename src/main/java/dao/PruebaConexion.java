package dao;

import model.Repartidor;

import java.sql.Connection;
import java.sql.SQLException;

public class PruebaConexion {

    public static void main(String[] args) {
        RepartidorDAO dao = new RepartidorDAO();

        try {
            dao.create(new Repartidor("Juan Pérez"));
            dao.create(new Repartidor("María López"));

            for (Repartidor r : dao.readAll()) {
                System.out.println(r);
            }
        } catch (SQLException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }
}