package vista;

import dao.EntregaDAO;
import dao.PedidoDAO;
import dao.RepartidorDAO;
import model.Entrega;
import model.Pedido;
import model.Repartidor;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.format.ResolverStyle;

/**
 * Ventana para gestionar entregas: asignar un pedido a un repartidor
 * con fecha y hora, y editar, eliminar y listar las entregas.
 */
public class VentanaEntregas extends JFrame {

    // Formatos de fecha y hora que se usan en el formulario y la tabla.
    // STRICT hace que se rechacen fechas que no existen, como 31-02-2026

    private static final DateTimeFormatter FORMATO_FECHA =
            DateTimeFormatter.ofPattern("dd-MM-uuuu").withResolverStyle(ResolverStyle.STRICT);
    private static final DateTimeFormatter FORMATO_HORA = DateTimeFormatter.ofPattern("HH:mm");

    // Acceso a la base de datos

    private final EntregaDAO entregaDAO = new EntregaDAO();
    private final PedidoDAO pedidoDAO = new PedidoDAO();
    private final RepartidorDAO repartidorDAO = new RepartidorDAO();

    // Componentes de la ventana

    private JComboBox<Pedido> cmbPedido;
    private JComboBox<Repartidor> cmbRepartidor;
    private JTextField txtFecha;
    private JTextField txtHora;
    private JButton btnGuardar;
    private JButton btnEditar;
    private JButton btnEliminar;
    private JButton btnLimpiar;
    private JTable tablaEntregas;
    private DefaultTableModel modeloTabla;

    // Id de la entrega seleccionada en la tabla (-1 = ninguna)
    private int idSeleccionado = -1;

    public VentanaEntregas() {
        setTitle("Gestión de Entregas");
        setSize(650, 480);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        construirInterfaz();
        cargarCombos();
        cargarTabla();
        limpiar();
    }

    /**
     * Crea los componentes y los ubica en la ventana.
     */
    private void construirInterfaz() {
        // Formulario: etiquetas a la izquierda, campos a la derecha
        JPanel panelFormulario = new JPanel(new GridLayout(0, 2, 5, 5));
        panelFormulario.setBorder(BorderFactory.createEmptyBorder(10, 10, 5, 10));

        cmbPedido = new JComboBox<>();
        cmbRepartidor = new JComboBox<>();
        txtFecha = new JTextField();
        txtHora = new JTextField();

        panelFormulario.add(new JLabel("Pedido:"));
        panelFormulario.add(cmbPedido);
        panelFormulario.add(new JLabel("Repartidor:"));
        panelFormulario.add(cmbRepartidor);
        panelFormulario.add(new JLabel("Fecha (dd-mm-aaaa):"));
        panelFormulario.add(txtFecha);
        panelFormulario.add(new JLabel("Hora (hh:mm):"));
        panelFormulario.add(txtHora);

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
        modeloTabla = new DefaultTableModel(
                new Object[]{"ID", "ID Pedido", "ID Repartidor", "Fecha", "Hora"}, 0) {
            @Override
            public boolean isCellEditable(int fila, int columna) {
                return false;
            }
        };
        tablaEntregas = new JTable(modeloTabla);
        tablaEntregas.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tablaEntregas.getColumnModel().getColumn(0).setMaxWidth(60);

        // Ubicar todo en la ventana
        setLayout(new BorderLayout());
        add(panelSuperior, BorderLayout.NORTH);
        add(new JScrollPane(tablaEntregas), BorderLayout.CENTER);

        // Eventos
        btnGuardar.addActionListener(e -> guardar());
        btnEditar.addActionListener(e -> editar());
        btnEliminar.addActionListener(e -> eliminar());
        btnLimpiar.addActionListener(e -> limpiar());
        tablaEntregas.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                seleccionarFila();
            }
        });
    }

    /**
     * Llena los combos con los pedidos y repartidores de la base de datos.
     * Cada combo guarda el objeto completo y muestra su toString() ("id - texto").
     */
    private void cargarCombos() {
        cmbPedido.removeAllItems();
        cmbRepartidor.removeAllItems();
        try {
            for (Pedido pedido : pedidoDAO.readAll()) {
                cmbPedido.addItem(pedido);
            }
            for (Repartidor repartidor : repartidorDAO.readAll()) {
                cmbRepartidor.addItem(repartidor);
            }
        } catch (SQLException e) {
            mostrarError("No se pudieron cargar los pedidos y repartidores", e);
        }
    }

    /**
     * Lee las entregas desde la base de datos y las muestra en la tabla.
     */
    private void cargarTabla() {
        modeloTabla.setRowCount(0);
        try {
            for (Entrega entrega : entregaDAO.readAll()) {
                modeloTabla.addRow(new Object[]{
                        entrega.getId(),
                        entrega.getIdPedido(),
                        entrega.getIdRepartidor(),
                        entrega.getFecha().format(FORMATO_FECHA),
                        entrega.getHora().format(FORMATO_HORA)
                });
            }
        } catch (SQLException e) {
            mostrarError("No se pudieron cargar las entregas", e);
        }
    }

    /**
     * Copia los datos de la fila seleccionada al formulario.
     */
    private void seleccionarFila() {
        int fila = tablaEntregas.getSelectedRow();
        if (fila == -1) {
            return;
        }
        idSeleccionado = (int) modeloTabla.getValueAt(fila, 0);
        seleccionarPedidoEnCombo((int) modeloTabla.getValueAt(fila, 1));
        seleccionarRepartidorEnCombo((int) modeloTabla.getValueAt(fila, 2));
        txtFecha.setText((String) modeloTabla.getValueAt(fila, 3));
        txtHora.setText((String) modeloTabla.getValueAt(fila, 4));
    }

    /**
     * Busca en el combo el pedido con ese id y lo deja seleccionado.
     */
    private void seleccionarPedidoEnCombo(int idPedido) {
        for (int i = 0; i < cmbPedido.getItemCount(); i++) {
            if (cmbPedido.getItemAt(i).getId() == idPedido) {
                cmbPedido.setSelectedIndex(i);
                return;
            }
        }
    }

    /**
     * Busca en el combo el repartidor con ese id y lo deja seleccionado.
     */
    private void seleccionarRepartidorEnCombo(int idRepartidor) {
        for (int i = 0; i < cmbRepartidor.getItemCount(); i++) {
            if (cmbRepartidor.getItemAt(i).getId() == idRepartidor) {
                cmbRepartidor.setSelectedIndex(i);
                return;
            }
        }
    }

    /**
     * Revisa el formulario y, si los datos son válidos, crea con ellos
     * un objeto Entrega. Si hay algún error, muestra un aviso y devuelve null.
     */
    private Entrega leerFormulario() {
        Pedido pedido = (Pedido) cmbPedido.getSelectedItem();
        Repartidor repartidor = (Repartidor) cmbRepartidor.getSelectedItem();

        if (pedido == null) {
            mostrarAviso("Debe seleccionar un pedido. Si la lista está vacía, registre pedidos primero.");
            return null;
        }
        if (repartidor == null) {
            mostrarAviso("Debe seleccionar un repartidor. Si la lista está vacía, registre repartidores primero.");
            return null;
        }

        LocalDate fecha;
        try {
            fecha = LocalDate.parse(txtFecha.getText().trim(), FORMATO_FECHA);
        } catch (DateTimeParseException e) {
            mostrarAviso("La fecha no es válida. Use el formato dd-mm-aaaa, por ejemplo 05-10-2026.");
            txtFecha.requestFocus();
            return null;
        }

        LocalTime hora;
        try {
            hora = LocalTime.parse(txtHora.getText().trim(), FORMATO_HORA);
        } catch (DateTimeParseException e) {
            mostrarAviso("La hora no es válida. Use el formato hh:mm, por ejemplo 14:30.");
            txtHora.requestFocus();
            return null;
        }

        return new Entrega(pedido.getId(), repartidor.getId(), fecha, hora);
    }

    private void guardar() {
        Entrega entrega = leerFormulario();
        if (entrega == null) {
            return;
        }
        try {
            entregaDAO.create(entrega);
            mostrarInfo("Entrega registrada correctamente.");
            limpiar();
            cargarTabla();
        } catch (SQLException e) {
            mostrarError("No se pudo registrar la entrega", e);
        }
    }

    private void editar() {
        if (idSeleccionado == -1) {
            mostrarAviso("Seleccione una entrega de la tabla para editarla.");
            return;
        }
        Entrega entrega = leerFormulario();
        if (entrega == null) {
            return;
        }
        try {
            entrega.setId(idSeleccionado);
            entregaDAO.update(entrega);
            mostrarInfo("Entrega actualizada correctamente.");
            limpiar();
            cargarTabla();
        } catch (SQLException e) {
            mostrarError("No se pudo actualizar la entrega", e);
        }
    }

    private void eliminar() {
        if (idSeleccionado == -1) {
            mostrarAviso("Seleccione una entrega de la tabla para eliminarla.");
            return;
        }
        int respuesta = JOptionPane.showConfirmDialog(
                this,
                "¿Seguro que desea eliminar la entrega seleccionada?",
                "Confirmar eliminación",
                JOptionPane.YES_NO_OPTION
        );
        if (respuesta != JOptionPane.YES_OPTION) {
            return;
        }
        try {
            entregaDAO.delete(idSeleccionado);
            mostrarInfo("Entrega eliminada correctamente.");
            limpiar();
            cargarTabla();
        } catch (SQLException e) {
            mostrarError("No se pudo eliminar la entrega", e);
        }
    }

    /**
     * Deja el formulario con la fecha y hora actuales
     * y sin ninguna entrega seleccionada.
     */
    private void limpiar() {
        if (cmbPedido.getItemCount() > 0) {
            cmbPedido.setSelectedIndex(0);
        }
        if (cmbRepartidor.getItemCount() > 0) {
            cmbRepartidor.setSelectedIndex(0);
        }
        txtFecha.setText(LocalDate.now().format(FORMATO_FECHA));
        txtHora.setText(LocalTime.now().format(FORMATO_HORA));
        idSeleccionado = -1;
        tablaEntregas.clearSelection();
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