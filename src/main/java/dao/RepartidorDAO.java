package dao;

import model.Repartidor;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Operaciones CRUD de la tabla "repartidores".
 * Los errores SQL se lanzan hacia la vista, que es la que
 * muestra los mensajes al usuario.
 */
public class RepartidorDAO {

    /**
     * Guarda un repartidor nuevo. El id lo genera MySQL.
     */
    public void create(Repartidor repartidor) throws SQLException {
        String sql = "INSERT INTO repartidores (nombre) VALUES (?)";

        try (Connection con = ConexionDB.conectar();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, repartidor.getNombre());
            ps.executeUpdate();
        }
    }

    /**
     * Devuelve todos los repartidores registrados.
     */
    public List<Repartidor> readAll() throws SQLException {
        List<Repartidor> lista = new ArrayList<>();
        String sql = "SELECT id, nombre FROM repartidores ORDER BY id";

        try (Connection con = ConexionDB.conectar();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Repartidor repartidor = new Repartidor(
                        rs.getInt("id"),
                        rs.getString("nombre")
                );
                lista.add(repartidor);
            }
        }
        return lista;
    }

    /**
     * Modifica el nombre de un repartidor existente.
     */
    public void update(Repartidor repartidor) throws SQLException {
        String sql = "UPDATE repartidores SET nombre = ? WHERE id = ?";

        try (Connection con = ConexionDB.conectar();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, repartidor.getNombre());
            ps.setInt(2, repartidor.getId());
            ps.executeUpdate();
        }
    }

    /**
     * Elimina un repartidor según su id.
     */
    public void delete(int id) throws SQLException {
        String sql = "DELETE FROM repartidores WHERE id = ?";

        try (Connection con = ConexionDB.conectar();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, id);
            ps.executeUpdate();
        }
    }
}