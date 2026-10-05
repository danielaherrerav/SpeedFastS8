package vista;

import model.GestorPedidos;
import model.Pedido;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;

public class VentanaListaPedidos extends JFrame{

    private JTable tablaPedidos;
    private JButton btnActualizar;
    private JPanel panelPrincipal;

    private GestorPedidos gestorPedidos;

    private DefaultTableModel modeloTabla;

    public VentanaListaPedidos(GestorPedidos gestorPedidos) {

        this.gestorPedidos = gestorPedidos;

        setTitle("Lista de Pedidos");
        setSize(600, 400);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        setContentPane(panelPrincipal);

        crearTabla();

        cargarPedidos();

        btnActualizar.addActionListener(e -> cargarPedidos());
    }

    private void crearTabla() {

        modeloTabla = new DefaultTableModel();

        modeloTabla.addColumn("ID");
        modeloTabla.addColumn("Dirección");
        modeloTabla.addColumn("Tipo");
        modeloTabla.addColumn("Repartidor");

        tablaPedidos.setModel(modeloTabla);
    }

    private void cargarPedidos() {

        modeloTabla.setRowCount(0);

        for (Pedido pedido : gestorPedidos.getPedidos()) {

            modeloTabla.addRow(new Object[]{
                    pedido.getId(),
                    pedido.getDireccionEntrega(),
                    pedido.getTipo(),
                    pedido.getRepartidor()
            });
        }
    }

}

