package vista;
import model.GestorPedidos;
import model.Pedido;
import javax.swing.*;

public class VentanaRegistroPedido extends JFrame {
    private JLabel lblID;
    private JTextField txtID;
    private JTextField txtDireccion;
    private JComboBox comboTipo;
    private JButton btnGuardar;
    private JPanel panelPrincipal;

    private GestorPedidos gestorPedidos;

    public VentanaRegistroPedido(GestorPedidos gestorPedidos) {

        this.gestorPedidos = gestorPedidos;
        setTitle("Registrar Pedido");
        setSize(400, 350);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        setContentPane(panelPrincipal);

        btnGuardar.addActionListener(e -> guardarPedido());

    }
    private void guardarPedido() {

        // Obtener los datos del formulario
        String idTexto = txtID.getText();
        String direccion = txtDireccion.getText();
        String tipo = comboTipo.getSelectedItem().toString();

        // Validar ID
        if (idTexto.isEmpty()) {

            JOptionPane.showMessageDialog(
                    null,
                    "Debe ingresar un ID."
            );

            return;
        }

        // Validar dirección
        if (direccion.isEmpty()) {

            JOptionPane.showMessageDialog(
                    null,
                    "Debe ingresar una dirección."
            );

            return;
        }

        // Convertir ID de texto a número
        int id;

        try {

            id = Integer.parseInt(idTexto);

        } catch (NumberFormatException e) {

            JOptionPane.showMessageDialog(
                    null,
                    "El ID debe ser un número."
            );

            return;
        }

        // Crear el pedido
        Pedido pedido = new Pedido(
                id,
                direccion,
                tipo
        );

        // Agregarlo a la lista
        gestorPedidos.agregarPedido(pedido);

        // Mostrar confirmación
        JOptionPane.showMessageDialog(
                null,
                "Pedido registrado correctamente."
        );

        // Limpiar los campos
        txtID.setText("");
        txtDireccion.setText("");
    }
}


