package vista;

import dao.PedidoDAO;
import model.Pedido;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.SQLException;

/**
 * Ventana para gestionar pedidos: registrar, editar, eliminar y listar.
 */
public class VentanaPedidos extends JFrame {

    // Valores permitidos (iguales a los ENUM de la tabla "pedidos")
    private static final String[] TIPOS = {"COMIDA", "ENCOMIENDA", "EXPRESS"};
    private static final String[] ESTADOS = {"PENDIENTE", "EN_REPARTO", "ENTREGADO"};

    // Acceso a la base de datos
    private final PedidoDAO pedidoDAO = new PedidoDAO();

    // Componentes de la ventana
    private JTextField txtDireccion;
    private JComboBox<String> cmbTipo;
    private JComboBox<String> cmbEstado;
    private JButton btnGuardar;
    private JButton btnEditar;
    private JButton btnEliminar;
    private JButton btnLimpiar;
    private JTable tablaPedidos;
    private DefaultTableModel modeloTabla;

    // Id del pedido seleccionado en la tabla (-1 = ninguno)
    private int idSeleccionado = -1;

    public VentanaPedidos() {
        setTitle("Gestión de Pedidos");
        setSize(600, 450);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        construirInterfaz();
        cargarTabla();
    }

    /**
     * Crea los componentes y los ubica en la ventana.
     */
    private void construirInterfaz() {
        // Formulario: etiquetas a la izquierda, campos a la derecha
        JPanel panelFormulario = new JPanel(new GridLayout(0, 2, 5, 5));
        panelFormulario.setBorder(BorderFactory.createEmptyBorder(10, 10, 5, 10));

        txtDireccion = new JTextField();
        cmbTipo = new JComboBox<>(TIPOS);
        cmbEstado = new JComboBox<>(ESTADOS);

        panelFormulario.add(new JLabel("Dirección:"));
        panelFormulario.add(txtDireccion);
        panelFormulario.add(new JLabel("Tipo:"));
        panelFormulario.add(cmbTipo);
        panelFormulario.add(new JLabel("Estado:"));
        panelFormulario.add(cmbEstado);

        // Botones
        JPanel panelBotones = new JPanel(new FlowLayout());
        btnGuardar = new JButton("Guardar");
        btnEditar = new JButton("Editar");
        btnEliminar = new JButton("Eliminar");
        btnLimpiar = new JButton("Limpiar");
        panelBotones.add(btnGuardar);
        panelBotones.add(btnEditar);
        panelBotones.add(btnEliminar);
        panelBotones.add(btnLimpiar);

        // Formulario y botones juntos en la parte superior
        JPanel panelSuperior = new JPanel(new BorderLayout());
        panelSuperior.add(panelFormulario, BorderLayout.NORTH);
        panelSuperior.add(panelBotones, BorderLayout.SOUTH);

        // Tabla (las celdas no se pueden editar directamente)
        modeloTabla = new DefaultTableModel(new Object[]{"ID", "Dirección", "Tipo", "Estado"}, 0) {
            @Override
            public boolean isCellEditable(int fila, int columna) {
                return false;
            }
        };
        tablaPedidos = new JTable(modeloTabla);
        tablaPedidos.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tablaPedidos.getColumnModel().getColumn(0).setMaxWidth(60);

        // Ubicar todo en la ventana
        setLayout(new BorderLayout());
        add(panelSuperior, BorderLayout.NORTH);
        add(new JScrollPane(tablaPedidos), BorderLayout.CENTER);

        // Eventos
        btnGuardar.addActionListener(e -> guardar());
        btnEditar.addActionListener(e -> editar());
        btnEliminar.addActionListener(e -> eliminar());
        btnLimpiar.addActionListener(e -> limpiar());
        tablaPedidos.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                seleccionarFila();
            }
        });
    }

    /**
     * Lee los pedidos desde la base de datos y los muestra en la tabla.
     */
    private void cargarTabla() {
        modeloTabla.setRowCount(0);
        try {
            for (Pedido pedido : pedidoDAO.readAll()) {
                modeloTabla.addRow(new Object[]{
                        pedido.getId(),
                        pedido.getDireccion(),
                        pedido.getTipo(),
                        pedido.getEstado()
                });
            }
        } catch (SQLException e) {
            mostrarError("No se pudieron cargar los pedidos", e);
        }
    }

    /**
     * Copia los datos de la fila seleccionada al formulario.
     */
    private void seleccionarFila() {
        int fila = tablaPedidos.getSelectedRow();
        if (fila == -1) {
            return;
        }
        idSeleccionado = (int) modeloTabla.getValueAt(fila, 0);
        txtDireccion.setText((String) modeloTabla.getValueAt(fila, 1));
        cmbTipo.setSelectedItem(modeloTabla.getValueAt(fila, 2));
        cmbEstado.setSelectedItem(modeloTabla.getValueAt(fila, 3));
    }

    /**
     * Revisa que el formulario esté bien llenado.
     * Devuelve true si los datos son válidos.
     */
    private boolean validarFormulario() {
        String direccion = txtDireccion.getText().trim();

        if (direccion.isEmpty()) {
            mostrarAviso("La dirección es obligatoria.");
            txtDireccion.requestFocus();
            return false;
        }
        if (direccion.length() > 100) {
            mostrarAviso("La dirección no puede superar los 100 caracteres.");
            txtDireccion.requestFocus();
            return false;
        }
        return true;
    }

    /**
     * Crea un objeto Pedido con los datos del formulario.
     */
    private Pedido leerFormulario() {
        String direccion = txtDireccion.getText().trim();
        String tipo = (String) cmbTipo.getSelectedItem();
        String estado = (String) cmbEstado.getSelectedItem();
        return new Pedido(direccion, tipo, estado);
    }

    private void guardar() {
        if (!validarFormulario()) {
            return;
        }
        try {
            pedidoDAO.create(leerFormulario());
            mostrarInfo("Pedido registrado correctamente.");
            limpiar();
            cargarTabla();
        } catch (SQLException e) {
            mostrarError("No se pudo registrar el pedido", e);
        }
    }

    private void editar() {
        if (idSeleccionado == -1) {
            mostrarAviso("Seleccione un pedido de la tabla para editarlo.");
            return;
        }
        if (!validarFormulario()) {
            return;
        }
        try {
            Pedido pedido = leerFormulario();
            pedido.setId(idSeleccionado);
            pedidoDAO.update(pedido);
            mostrarInfo("Pedido actualizado correctamente.");
            limpiar();
            cargarTabla();
        } catch (SQLException e) {
            mostrarError("No se pudo actualizar el pedido", e);
        }
    }

    private void eliminar() {
        if (idSeleccionado == -1) {
            mostrarAviso("Seleccione un pedido de la tabla para eliminarlo.");
            return;
        }
        int respuesta = JOptionPane.showConfirmDialog(
                this,
                "¿Seguro que desea eliminar el pedido seleccionado?",
                "Confirmar eliminación",
                JOptionPane.YES_NO_OPTION
        );
        if (respuesta != JOptionPane.YES_OPTION) {
            return;
        }
        try {
            pedidoDAO.delete(idSeleccionado);
            mostrarInfo("Pedido eliminado correctamente.");
            limpiar();
            cargarTabla();
        } catch (SQLException e) {
            // 1451 = MySQL no deja borrar porque otra tabla lo usa (llave foránea)
            if (e.getErrorCode() == 1451) {
                mostrarAviso("No se puede eliminar: el pedido tiene entregas registradas.");
            } else {
                mostrarError("No se pudo eliminar el pedido", e);
            }
        }
    }

    /**
     * Deja el formulario vacío y sin ningún pedido seleccionado.
     */
    private void limpiar() {
        txtDireccion.setText("");
        cmbTipo.setSelectedIndex(0);
        cmbEstado.setSelectedIndex(0);
        idSeleccionado = -1;
        tablaPedidos.clearSelection();
    }

    // ===== Mensajes al usuario =====

    private void mostrarInfo(String mensaje) {
        JOptionPane.showMessageDialog(this, mensaje, "Información", JOptionPane.INFORMATION_MESSAGE);
    }

    private void mostrarAviso(String mensaje) {
        JOptionPane.showMessageDialog(this, mensaje, "Atención", JOptionPane.WARNING_MESSAGE);
    }

    private void mostrarError(String mensaje, SQLException e) {
        JOptionPane.showMessageDialog(
                this,
                mensaje + ".\nDetalle: " + e.getMessage(),
                "Error",
                JOptionPane.ERROR_MESSAGE
        );
    }
}