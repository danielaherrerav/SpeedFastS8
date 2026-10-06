package dao;

import model.Pedido;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Operaciones CRUD de la tabla "pedidos".
 * Los errores SQL se lanzan hacia la vista, que es la que
 * muestra los mensajes al usuario.
 */
public class PedidoDAO {

    /**
     * Guarda un pedido nuevo. El id lo genera MySQL.
     */
    public void create(Pedido pedido) throws SQLException {
        String sql = "INSERT INTO pedidos (direccion, tipo, estado) VALUES (?, ?, ?)";

        try (Connection con = ConexionDB.conectar();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, pedido.getDireccion());
            ps.setString(2, pedido.getTipo());
            ps.setString(3, pedido.getEstado());
            ps.executeUpdate();
        }
    }

    /**
     * Devuelve todos los pedidos registrados.
     */
    public List<Pedido> readAll() throws SQLException {
        List<Pedido> lista = new ArrayList<>();
        String sql = "SELECT id, direccion, tipo, estado FROM pedidos ORDER BY id";

        try (Connection con = ConexionDB.conectar();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Pedido pedido = new Pedido(
                        rs.getInt("id"),
                        rs.getString("direccion"),
                        rs.getString("tipo"),
                        rs.getString("estado")
                );
                lista.add(pedido);
            }
        }
        return lista;
    }

    /**
     * Modifica la dirección, el tipo y el estado de un pedido existente.
     */
    public void update(Pedido pedido) throws SQLException {
        String sql = "UPDATE pedidos SET direccion = ?, tipo = ?, estado = ? WHERE id = ?";

        try (Connection con = ConexionDB.conectar();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, pedido.getDireccion());
            ps.setString(2, pedido.getTipo());
            ps.setString(3, pedido.getEstado());
            ps.setInt(4, pedido.getId());
            ps.executeUpdate();
        }
    }

    /**
     * Elimina un pedido según su id.
     */
    public void delete(int id) throws SQLException {
        String sql = "DELETE FROM pedidos WHERE id = ?";

        try (Connection con = ConexionDB.conectar();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, id);
            ps.executeUpdate();
        }
    }
}