package vista;
import model.GestorPedidos;
import javax.swing.*;
import model.Pedido;

public class VentanaPrincipal extends JFrame {
    private JLabel lblSPEEDFAST;
    private JPanel ventanaPrincipal;
    private JButton btnRegistrar;
    private JButton btnListar;
    private JButton btnEntrega;

    private GestorPedidos gestorPedidos;

    public VentanaPrincipal(GestorPedidos gestorPedidos) {

        this.gestorPedidos = gestorPedidos;

        setTitle("SpeedFast");
        setSize(500, 300);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        setContentPane(ventanaPrincipal);

        // Botón Registrar Pedido
        btnRegistrar.addActionListener(e -> {

            VentanaRegistroPedido ventanaRegistro =
                    new VentanaRegistroPedido(gestorPedidos);

            ventanaRegistro.setVisible(true);
        });

        // Botón Listar Pedidos
        btnListar.addActionListener(e -> {

            VentanaListaPedidos ventanaLista =
                    new VentanaListaPedidos(gestorPedidos);

            ventanaLista.setVisible(true);
        });

        // Botón Asignar / Iniciar Entrega
        btnEntrega.addActionListener(e -> {

            String idTexto = JOptionPane.showInputDialog(
                    this,
                    "Ingrese el ID del pedido:"
            );

            if (idTexto == null) {
                return;
            }

            if (idTexto.isEmpty()) {

                JOptionPane.showMessageDialog(
                        this,
                        "Debe ingresar un ID."
                );

                return;
            }

            int id;

            try {

                id = Integer.parseInt(idTexto);

            } catch (NumberFormatException ex) {

                JOptionPane.showMessageDialog(
                        this,
                        "El ID debe ser un número."
                );

                return;
            }

            Pedido pedidoEncontrado = null;

            for (Pedido pedido : gestorPedidos.getPedidos()) {

                if (pedido.getId() == id) {

                    pedidoEncontrado = pedido;
                    break;
                }
            }

            if (pedidoEncontrado == null) {

                JOptionPane.showMessageDialog(
                        this,
                        "No se encontró un pedido con ese ID.");

                return;
            }

            String nombreRepartidor = JOptionPane.showInputDialog(
                    this,
                    "Ingrese el nombre del repartidor:");

            if (nombreRepartidor == null || nombreRepartidor.isEmpty()) {

                JOptionPane.showMessageDialog(
                        this,
                        "Debe ingresar el nombre del repartidor.");

                return;
            }

            pedidoEncontrado.setRepartidor(nombreRepartidor);

            JOptionPane.showMessageDialog(
                    this,
                    "Pedido #" + pedidoEncontrado.getId()
                            + " asignado a "
                            + nombreRepartidor
                            + "\nEntrega iniciada.");
        });
    }
}