package ec.edu.monster.vista;

import ec.edu.monster.controlador.LoginControlador;
import ec.edu.monster.modelo.LoginModelo;
import ec.edu.monster.vista.componentes.PanelFondoDegradado;
import jakarta.xml.ws.soap.SOAPFaultException;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Image;
import java.awt.Insets;
import java.awt.RenderingHints;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.plaf.basic.BasicButtonUI;
import javax.swing.border.EmptyBorder;

public class LoginVista extends JFrame {

    private final JTextField txtUsuario = new JTextField();
    private final JPasswordField txtContrasenia = new JPasswordField();
    private final JButton btnIngresar = new JButton("Iniciar sesion");
    private final JLabel lblEstado = new JLabel("Conectando al servidor...", SwingConstants.LEFT);
    private final JLabel lblEndpoint = new JLabel("Endpoint: no disponible", SwingConstants.LEFT);

    private LoginControlador loginControlador;

    public LoginVista() {
        configurarVentana();
        construirVista();
        conectarEventos();
        inicializarControlador();
    }

    private void configurarVentana() {
        setTitle("Cliente Escritorio GR5 - Inicio de sesion");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setMinimumSize(new Dimension(760, 470));
        setSize(820, 500);
        setLocationRelativeTo(null);
    }

    private void construirVista() {
        PanelFondoDegradado panelPrincipal = new PanelFondoDegradado(
                new Color(12, 31, 56),
                new Color(23, 84, 134),
                null
        );
        panelPrincipal.setLayout(new GridBagLayout());
        panelPrincipal.setBorder(new EmptyBorder(24, 24, 24, 24));
        setContentPane(panelPrincipal);

        JPanel tarjeta = crearPanelTarjeta(new BorderLayout(20, 12));
        tarjeta.setPreferredSize(new Dimension(700, 360));

        JLabel lblImagen = new JLabel(cargarIcono("/ec/edu/monster/assets/img-monster.jpeg", 230, 230));
        lblImagen.setHorizontalAlignment(SwingConstants.CENTER);
        tarjeta.add(lblImagen, BorderLayout.WEST);

        JPanel panelFormulario = new JPanel(new GridBagLayout());
        panelFormulario.setOpaque(false);
        tarjeta.add(panelFormulario, BorderLayout.CENTER);

        GridBagConstraints restricciones = new GridBagConstraints();
        restricciones.insets = new Insets(5, 6, 5, 6);
        restricciones.anchor = GridBagConstraints.WEST;
        restricciones.fill = GridBagConstraints.HORIZONTAL;

        JLabel lblTitulo = new JLabel(
            "<html><div style='text-align:center; width:320px;'>Sistema de Conversion<br>de Unidades</div></html>",
            SwingConstants.CENTER
        );
        lblTitulo.setFont(new Font("Trebuchet MS", Font.BOLD, 23));
        lblTitulo.setForeground(new Color(12, 52, 89));
        lblTitulo.setBorder(new EmptyBorder(0, 8, 0, 8));

        JLabel lblSubtitulo = new JLabel("GRUPO #5", SwingConstants.CENTER);
        lblSubtitulo.setFont(new Font("Trebuchet MS", Font.BOLD, 16));
        lblSubtitulo.setForeground(new Color(38, 85, 118));

        JLabel lblUsuario = new JLabel("Usuario");
        lblUsuario.setFont(new Font("Verdana", Font.BOLD, 14));
        lblUsuario.setForeground(new Color(21, 53, 77));

        JLabel lblContrasenia = new JLabel("Contrasenia");
        lblContrasenia.setFont(new Font("Verdana", Font.BOLD, 14));
        lblContrasenia.setForeground(new Color(21, 53, 77));

        estilizarCampo(txtUsuario);
        estilizarCampo(txtContrasenia);
        estilizarBotonPrincipal(btnIngresar);

        lblEstado.setFont(new Font("Verdana", Font.PLAIN, 13));
        lblEstado.setForeground(new Color(30, 106, 138));

        lblEndpoint.setFont(new Font("Verdana", Font.PLAIN, 12));
        lblEndpoint.setForeground(new Color(60, 72, 80));

        restricciones.gridx = 0;
        restricciones.gridy = 0;
        restricciones.gridwidth = 2;
        restricciones.anchor = GridBagConstraints.CENTER;
        restricciones.insets = new Insets(2, 6, 4, 6);
        panelFormulario.add(lblTitulo, restricciones);

        restricciones.gridy = 1;
        restricciones.insets = new Insets(0, 6, 10, 6);
        panelFormulario.add(lblSubtitulo, restricciones);

        restricciones.gridy = 2;
        restricciones.insets = new Insets(5, 6, 5, 6);
        restricciones.gridwidth = 1;
        restricciones.anchor = GridBagConstraints.WEST;
        panelFormulario.add(lblUsuario, restricciones);

        restricciones.gridy = 3;
        restricciones.weightx = 1;
        panelFormulario.add(txtUsuario, restricciones);

        restricciones.gridy = 4;
        restricciones.weightx = 0;
        panelFormulario.add(lblContrasenia, restricciones);

        restricciones.gridy = 5;
        restricciones.weightx = 1;
        panelFormulario.add(txtContrasenia, restricciones);

        restricciones.gridy = 6;
        restricciones.weightx = 0;
        panelFormulario.add(btnIngresar, restricciones);

        restricciones.gridy = 7;
        panelFormulario.add(lblEstado, restricciones);

        restricciones.gridy = 8;
        panelFormulario.add(lblEndpoint, restricciones);

        GridBagConstraints restriccionesTarjeta = new GridBagConstraints();
        restriccionesTarjeta.gridx = 0;
        restriccionesTarjeta.gridy = 0;
        restriccionesTarjeta.weightx = 1;
        restriccionesTarjeta.weighty = 1;
        restriccionesTarjeta.fill = GridBagConstraints.NONE;
        panelPrincipal.add(tarjeta, restriccionesTarjeta);

        getRootPane().setDefaultButton(btnIngresar);
    }

    private JPanel crearPanelTarjeta(BorderLayout layout) {
        JPanel panel = new JPanel(layout) {
            @Override
            protected void paintComponent(Graphics graphics) {
                Graphics2D grafico = (Graphics2D) graphics.create();
                grafico.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                grafico.setColor(new Color(246, 252, 255, 232));
                grafico.fillRoundRect(0, 0, getWidth(), getHeight(), 36, 36);
                grafico.setColor(new Color(151, 195, 219, 190));
                grafico.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 36, 36);
                grafico.dispose();
                super.paintComponent(graphics);
            }
        };
        panel.setOpaque(false);
        panel.setBorder(new EmptyBorder(24, 24, 24, 24));
        return panel;
    }

    private void conectarEventos() {
        btnIngresar.addActionListener(evento -> intentarLogin());
        txtContrasenia.addActionListener(evento -> intentarLogin());
    }

    private void inicializarControlador() {
        try {
            loginControlador = new LoginControlador();
            lblEstado.setText("Servidor detectado correctamente.");
            lblEndpoint.setText("Endpoint: " + loginControlador.getEndpointActivo());
        } catch (Exception ex) {
            lblEstado.setText("No fue posible conectar al servidor SOAP.");
            lblEndpoint.setText("Detalle: " + ex.getMessage());
            btnIngresar.setEnabled(false);
            mostrarError(ex.getMessage());
        }
    }

    private void intentarLogin() {
        String usuario = txtUsuario.getText().trim();
        String contrasenia = new String(txtContrasenia.getPassword());

        if (usuario.isBlank() || contrasenia.isBlank()) {
            mostrarAviso("Debe ingresar usuario y contrasenia.");
            return;
        }

        if (loginControlador == null) {
            mostrarError("No hay conexion con el servidor.");
            return;
        }

        btnIngresar.setEnabled(false);
        lblEstado.setText("Validando credenciales...");

        try {
            String token = loginControlador.autenticar(new LoginModelo(usuario, contrasenia));
            lblEstado.setText("Inicio de sesion exitoso.");
            abrirVentanaConversion(token);
        } catch (SOAPFaultException ex) {
            lblEstado.setText("Credenciales invalidas.");
            mostrarError(ex.getFault().getFaultString());
        } catch (Exception ex) {
            lblEstado.setText("No se pudo iniciar sesion.");
            mostrarError(ex.getMessage());
        } finally {
            btnIngresar.setEnabled(true);
        }
    }

    private void abrirVentanaConversion(String tokenSesion) {
        ConversionUnidadesVista vistaConversion = new ConversionUnidadesVista(
                loginControlador.crearControladorUnidades(),
                loginControlador,
                tokenSesion
        );
        vistaConversion.setVisible(true);
        dispose();
    }

    private ImageIcon cargarIcono(String recurso, int ancho, int alto) {
        var url = getClass().getResource(recurso);
        if (url == null) {
            return null;
        }

        Image imagen = new ImageIcon(url).getImage().getScaledInstance(ancho, alto, Image.SCALE_SMOOTH);
        return new ImageIcon(imagen);
    }

    private void estilizarCampo(JTextField campo) {
        campo.setFont(new Font("Verdana", Font.PLAIN, 14));
        campo.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(142, 183, 206), 1),
                new EmptyBorder(8, 10, 8, 10)
        ));
    }

    private void estilizarBotonPrincipal(JButton boton) {
        boton.setUI(new BasicButtonUI());
        boton.setFont(new Font("Trebuchet MS", Font.BOLD, 16));
        boton.setBackground(new Color(5, 104, 157));
        boton.setForeground(Color.WHITE);
        boton.setOpaque(true);
        boton.setContentAreaFilled(true);
        boton.setBorderPainted(true);
        boton.setFocusPainted(false);
        boton.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(3, 72, 109), 2),
                new EmptyBorder(10, 18, 10, 18)
        ));
    }

    private void mostrarAviso(String mensaje) {
        JOptionPane.showMessageDialog(this, mensaje, "Aviso", JOptionPane.INFORMATION_MESSAGE);
    }

    private void mostrarError(String mensaje) {
        JOptionPane.showMessageDialog(this, mensaje, "Error", JOptionPane.ERROR_MESSAGE);
    }
}
