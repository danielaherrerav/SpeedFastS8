package vista;

import dao.RepartidorDAO;
import model.Repartidor;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.SQLException;

/**
 * Ventana para gestionar repartidores: registrar, editar, eliminar y listar.
 */
public class VentanaRepartidores extends JFrame {

    // Acceso a la base de datos
    private final RepartidorDAO repartidorDAO = new RepartidorDAO();

    // Componentes de la ventana
    private JTextField txtNombre;
    private JButton btnGuardar;
    private JButton btnEditar;
    private JButton btnEliminar;
    private JButton btnLimpiar;
    private JTable tablaRepartidores;
    private DefaultTableModel modeloTabla;

    // Id del repartidor seleccionado en la tabla (-1 = ninguno)
    private int idSeleccionado = -1;

    public VentanaRepartidores() {
        setTitle("Gestión de Repartidores");
        setSize(500, 400);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        construirInterfaz();
        cargarTabla();
    }

    /**
     * Crea los componentes y los ubica en la ventana.
     */
    private void construirInterfaz() {
        // Formulario
        JPanel panelFormulario = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panelFormulario.add(new JLabel("Nombre:"));
        txtNombre = new JTextField(25);
        panelFormulario.add(txtNombre);

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
        modeloTabla = new DefaultTableModel(new Object[]{"ID", "Nombre"}, 0) {
            @Override
            public boolean isCellEditable(int fila, int columna) {
                return false;
            }
        };
        tablaRepartidores = new JTable(modeloTabla);
        tablaRepartidores.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);


        setLayout(new BorderLayout());
        add(panelSuperior, BorderLayout.NORTH);
        add(new JScrollPane(tablaRepartidores), BorderLayout.CENTER);

        // Eventos
        btnGuardar.addActionListener(e -> guardar());
        btnEditar.addActionListener(e -> editar());
        btnEliminar.addActionListener(e -> eliminar());
        btnLimpiar.addActionListener(e -> limpiar());
        tablaRepartidores.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                seleccionarFila();
            }
        });
    }

    /**
     * Lee los repartidores desde la base de datos y los muestra en la tabla.
     */
    private void cargarTabla() {
        modeloTabla.setRowCount(0);
        try {
            for (Repartidor repartidor : repartidorDAO.readAll()) {
                modeloTabla.addRow(new Object[]{
                        repartidor.getId(),
                        repartidor.getNombre()
                });
            }
        } catch (SQLException e) {
            mostrarError("No se pudieron cargar los repartidores", e);
        }
    }

    /**
     * Copia los datos de la fila seleccionada al formulario.
     */
    private void seleccionarFila() {
        int fila = tablaRepartidores.getSelectedRow();
        if (fila == -1) {
            return;
        }
        idSeleccionado = (int) modeloTabla.getValueAt(fila, 0);
        txtNombre.setText((String) modeloTabla.getValueAt(fila, 1));
    }

    /**
     * Revisa que el formulario esté bien llenado.
     * Devuelve true si los datos son válidos.
     */
    private boolean validarFormulario() {
        String nombre = txtNombre.getText().trim();

        if (nombre.isEmpty()) {
            mostrarAviso("El nombre es obligatorio.");
            txtNombre.requestFocus();
            return false;
        }
        if (nombre.length() > 100) {
            mostrarAviso("El nombre no puede superar los 100 caracteres.");
            txtNombre.requestFocus();
            return false;
        }
        return true;
    }

    private void guardar() {
        if (!validarFormulario()) {
            return;
        }
        try {
            repartidorDAO.create(new Repartidor(txtNombre.getText().trim()));
            mostrarInfo("Repartidor registrado correctamente.");
            limpiar();
            cargarTabla();
        } catch (SQLException e) {
            mostrarError("No se pudo registrar el repartidor", e);
        }
    }

    private void editar() {
        if (idSeleccionado == -1) {
            mostrarAviso("Seleccione un repartidor de la tabla para editarlo.");
            return;
        }
        if (!validarFormulario()) {
            return;
        }
        try {
            repartidorDAO.update(new Repartidor(idSeleccionado, txtNombre.getText().trim()));
            mostrarInfo("Repartidor actualizado correctamente.");
            limpiar();
            cargarTabla();
        } catch (SQLException e) {
            mostrarError("No se pudo actualizar el repartidor", e);
        }
    }

    private void eliminar() {
        if (idSeleccionado == -1) {
            mostrarAviso("Seleccione un repartidor de la tabla para eliminarlo.");
            return;
        }
        int respuesta = JOptionPane.showConfirmDialog(
                this,
                "¿Seguro que desea eliminar al repartidor seleccionado?",
                "Confirmar eliminación",
                JOptionPane.YES_NO_OPTION
        );
        if (respuesta != JOptionPane.YES_OPTION) {
            return;
        }
        try {
            repartidorDAO.delete(idSeleccionado);
            mostrarInfo("Repartidor eliminado correctamente.");
            limpiar();
            cargarTabla();
        } catch (SQLException e) {
            // 1451 = MySQL no deja borrar porque otra tabla lo usa (llave foránea)
            if (e.getErrorCode() == 1451) {
                mostrarAviso("No se puede eliminar: el repartidor tiene entregas registradas.");
            } else {
                mostrarError("No se pudo eliminar el repartidor", e);
            }
        }
    }

    /**
     * Deja el formulario vacío y sin ningún repartidor seleccionado.
     */
    private void limpiar() {
        txtNombre.setText("");
        idSeleccionado = -1;
        tablaRepartidores.clearSelection();
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