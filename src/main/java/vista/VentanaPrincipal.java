package vista;

import javax.swing.*;
import java.awt.*;

/**
 * Menú principal de SpeedFast. Desde aquí se abren
 * las ventanas de gestión de cada entidad.
 */
public class VentanaPrincipal extends JFrame {

    public VentanaPrincipal() {
        setTitle("SpeedFast - Menú principal");
        setSize(400, 300);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // Título
        JLabel lblTitulo = new JLabel("SPEEDFAST", SwingConstants.CENTER);
        lblTitulo.setFont(new Font("SansSerif", Font.BOLD, 26));
        lblTitulo.setBorder(BorderFactory.createEmptyBorder(20, 0, 10, 0));

        // Botones del menú
        JButton btnRepartidores = new JButton("Gestión de Repartidores");
        btnRepartidores.addActionListener(e -> new VentanaRepartidores().setVisible(true));

        JButton btnPedidos = new JButton("Gestión de Pedidos");
        btnPedidos.addActionListener(e -> new VentanaPedidos().setVisible(true));

        // Panel con los botones uno debajo del otro
        JPanel panelBotones = new JPanel(new GridLayout(0, 1, 10, 10));
        panelBotones.setBorder(BorderFactory.createEmptyBorder(10, 60, 30, 60));
        panelBotones.add(btnRepartidores);

        panelBotones.add(btnPedidos);

        setLayout(new BorderLayout());
        add(lblTitulo, BorderLayout.NORTH);
        add(panelBotones, BorderLayout.CENTER);
    }
}