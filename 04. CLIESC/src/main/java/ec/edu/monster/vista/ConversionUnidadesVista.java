package ec.edu.monster.vista;

import ec.edu.monster.controlador.LoginControlador;
import ec.edu.monster.controlador.UnidadesControlador;
import ec.edu.monster.modelo.ConversionModelo;
import ec.edu.monster.modelo.TipoConversion;
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
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.Insets;
import java.awt.RenderingHints;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import javax.swing.BorderFactory;
import javax.swing.DefaultComboBoxModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;

public class ConversionUnidadesVista extends JFrame {

    private static final DecimalFormat FORMATO_NUMERICO;

    static {
        DecimalFormatSymbols simbolos = new DecimalFormatSymbols(Locale.US);
        simbolos.setDecimalSeparator('.');
        simbolos.setGroupingSeparator(',');
        FORMATO_NUMERICO = new DecimalFormat("#,##0.######", simbolos);
    }

    private final UnidadesControlador unidadesControlador;
    private final LoginControlador loginControlador;
    private final String tokenSesion;

    private final JComboBox<TipoConversion> cmbTipoConversion = new JComboBox<>(TipoConversion.values());
    private final JComboBox<String> cmbUnidadInicial = new JComboBox<>();
    private final JComboBox<String> cmbUnidadFinal = new JComboBox<>();
    private final JTextField txtValor = new JTextField();

    private final JButton btnConvertir = new JButton("Convertir");
    private final JButton btnLimpiar = new JButton("Limpiar");
    private final JButton btnCerrarSesion = new JButton("Cerrar sesion");

    private final JLabel lblResultado = new JLabel("Resultado: listo para convertir", SwingConstants.LEFT);
    private final JTextArea txtHistorial = new JTextArea();

    public ConversionUnidadesVista(
            UnidadesControlador unidadesControlador,
            LoginControlador loginControlador,
            String tokenSesion) {

        this.unidadesControlador = unidadesControlador;
        this.loginControlador = loginControlador;
        this.tokenSesion = tokenSesion;

        validarDependencias();
        configurarVentana();
        construirVista();
        conectarEventos();
        actualizarUnidadesDisponibles();
    }

    private void validarDependencias() {
        if (unidadesControlador == null) {
            throw new IllegalArgumentException("El controlador de unidades es obligatorio.");
        }
        if (loginControlador == null) {
            throw new IllegalArgumentException("El controlador de login es obligatorio.");
        }
    }

    private void configurarVentana() {
        setTitle("Cliente Escritorio GR5 - Conversion de Unidades");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setMinimumSize(new Dimension(1020, 620));
        setSize(1060, 640);
        setLocationRelativeTo(null);
    }

    private void construirVista() {
        PanelFondoDegradado panelPrincipal = new PanelFondoDegradado(
                new Color(8, 35, 62),
                new Color(13, 94, 126),
                "/ec/edu/monster/assets/fondo.jpg"
        );
        panelPrincipal.setLayout(new BorderLayout(18, 18));
        panelPrincipal.setBorder(new EmptyBorder(18, 18, 18, 18));
        setContentPane(panelPrincipal);

        JPanel panelCabecera = crearPanelTarjeta(new BorderLayout(14, 8));

        JLabel lblTitulo = new JLabel("Conversor inteligente de unidades", SwingConstants.LEFT);
        lblTitulo.setFont(new Font("Trebuchet MS", Font.BOLD, 27));
        lblTitulo.setForeground(new Color(8, 54, 93));

        JLabel lblSubtitulo = new JLabel(
            "Longitud, masa y temperatura con seguridad por token",
                SwingConstants.LEFT
        );
        lblSubtitulo.setFont(new Font("Verdana", Font.PLAIN, 13));
        lblSubtitulo.setForeground(new Color(42, 94, 129));

        JLabel lblInfoSesion = new JLabel(
                "Endpoint: " + loginControlador.getEndpointActivo() + " | Token: " + resumirToken(tokenSesion),
                SwingConstants.LEFT
        );
        lblInfoSesion.setFont(new Font("Verdana", Font.PLAIN, 12));
        lblInfoSesion.setForeground(new Color(63, 76, 86));

        JPanel panelTextosCabecera = new JPanel(new GridLayout(3, 1, 0, 2));
        panelTextosCabecera.setOpaque(false);
        panelTextosCabecera.add(lblTitulo);
        panelTextosCabecera.add(lblSubtitulo);
        panelTextosCabecera.add(lblInfoSesion);

        JLabel lblIcono = new JLabel(cargarIcono("/ec/edu/monster/assets/img-monster.png", 130, 130));
        lblIcono.setHorizontalAlignment(SwingConstants.CENTER);

        panelCabecera.add(panelTextosCabecera, BorderLayout.CENTER);
        panelCabecera.add(lblIcono, BorderLayout.EAST);

        JPanel panelCentro = new JPanel(new GridLayout(1, 2, 16, 0));
        panelCentro.setOpaque(false);
        panelCentro.add(crearPanelFormulario());
        panelCentro.add(crearPanelHistorial());

        panelPrincipal.add(panelCabecera, BorderLayout.NORTH);
        panelPrincipal.add(panelCentro, BorderLayout.CENTER);
    }

    private JPanel crearPanelFormulario() {
        JPanel panelFormulario = crearPanelTarjeta(new GridBagLayout());

        JLabel lblTipo = crearEtiquetaCampo("Tipo de conversion");
        JLabel lblValor = crearEtiquetaCampo("Valor");
        JLabel lblUnidadInicial = crearEtiquetaCampo("Unidad inicial");
        JLabel lblUnidadFinal = crearEtiquetaCampo("Unidad final");

        estilizarCombo(cmbTipoConversion);
        estilizarCombo(cmbUnidadInicial);
        estilizarCombo(cmbUnidadFinal);
        estilizarCampo(txtValor);

        estilizarBotonPrincipal(btnConvertir);
        estilizarBotonSecundario(btnLimpiar);
        estilizarBotonSecundario(btnCerrarSesion);

        lblResultado.setFont(new Font("Trebuchet MS", Font.BOLD, 15));
        lblResultado.setForeground(new Color(8, 77, 118));

        GridBagConstraints restricciones = new GridBagConstraints();
        restricciones.insets = new Insets(8, 10, 8, 10);
        restricciones.fill = GridBagConstraints.HORIZONTAL;
        restricciones.anchor = GridBagConstraints.WEST;

        restricciones.gridx = 0;
        restricciones.gridy = 0;
        restricciones.gridwidth = 2;
        panelFormulario.add(crearTituloPanel("Panel de conversion"), restricciones);

        restricciones.gridy = 1;
        restricciones.gridwidth = 1;
        panelFormulario.add(lblTipo, restricciones);

        restricciones.gridy = 2;
        restricciones.weightx = 1;
        panelFormulario.add(cmbTipoConversion, restricciones);

        restricciones.gridy = 3;
        restricciones.weightx = 0;
        panelFormulario.add(lblValor, restricciones);

        restricciones.gridy = 4;
        restricciones.weightx = 1;
        panelFormulario.add(txtValor, restricciones);

        restrictionsRow(panelFormulario, restricciones, 5, lblUnidadInicial, cmbUnidadInicial);
        restrictionsRow(panelFormulario, restricciones, 7, lblUnidadFinal, cmbUnidadFinal);

        restricciones.gridy = 9;
        restricciones.gridx = 0;
        restricciones.gridwidth = 1;
        restricciones.weightx = 0.5;
        panelFormulario.add(btnConvertir, restricciones);

        restricciones.gridx = 1;
        panelFormulario.add(btnLimpiar, restricciones);

        restricciones.gridx = 0;
        restricciones.gridy = 10;
        restricciones.gridwidth = 2;
        restricciones.weightx = 0;
        panelFormulario.add(lblResultado, restricciones);

        restricciones.gridy = 11;
        panelFormulario.add(btnCerrarSesion, restricciones);

        return panelFormulario;
    }

    private void restrictionsRow(
            JPanel panel,
            GridBagConstraints restricciones,
            int filaEtiqueta,
            JLabel etiqueta,
            JComboBox<String> combo) {
        restricciones.gridx = 0;
        restricciones.gridy = filaEtiqueta;
        restricciones.gridwidth = 2;
        restricciones.weightx = 0;
        panel.add(etiqueta, restricciones);

        restricciones.gridy = filaEtiqueta + 1;
        restricciones.weightx = 1;
        panel.add(combo, restricciones);
    }

    private JPanel crearPanelHistorial() {
        JPanel panelHistorial = crearPanelTarjeta(new BorderLayout(10, 10));
        panelHistorial.add(crearTituloPanel("Historial de conversiones"), BorderLayout.NORTH);

        txtHistorial.setFont(new Font("Consolas", Font.PLAIN, 13));
        txtHistorial.setEditable(false);
        txtHistorial.setLineWrap(true);
        txtHistorial.setWrapStyleWord(true);
        txtHistorial.setBackground(new Color(250, 253, 255));
        txtHistorial.setForeground(new Color(22, 49, 66));
        txtHistorial.setBorder(new EmptyBorder(8, 8, 8, 8));

        JScrollPane scrollHistorial = new JScrollPane(txtHistorial);
        scrollHistorial.setBorder(BorderFactory.createLineBorder(new Color(173, 206, 224), 1));

        panelHistorial.add(scrollHistorial, BorderLayout.CENTER);
        return panelHistorial;
    }

    private JPanel crearPanelTarjeta(BorderLayout layout) {
        JPanel panel = new JPanel(layout) {
            @Override
            protected void paintComponent(Graphics graphics) {
                Graphics2D grafico = (Graphics2D) graphics.create();
                grafico.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                grafico.setColor(new Color(248, 253, 255, 235));
                grafico.fillRoundRect(0, 0, getWidth(), getHeight(), 30, 30);
                grafico.setColor(new Color(164, 203, 224, 185));
                grafico.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 30, 30);
                grafico.dispose();
                super.paintComponent(graphics);
            }
        };
        panel.setOpaque(false);
        panel.setBorder(new EmptyBorder(18, 18, 18, 18));
        return panel;
    }

    private JPanel crearPanelTarjeta(GridBagLayout layout) {
        JPanel panel = new JPanel(layout) {
            @Override
            protected void paintComponent(Graphics graphics) {
                Graphics2D grafico = (Graphics2D) graphics.create();
                grafico.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                grafico.setColor(new Color(248, 253, 255, 235));
                grafico.fillRoundRect(0, 0, getWidth(), getHeight(), 30, 30);
                grafico.setColor(new Color(164, 203, 224, 185));
                grafico.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 30, 30);
                grafico.dispose();
                super.paintComponent(graphics);
            }
        };
        panel.setOpaque(false);
        panel.setBorder(new EmptyBorder(18, 18, 18, 18));
        return panel;
    }

    private JLabel crearTituloPanel(String texto) {
        JLabel etiqueta = new JLabel(texto, SwingConstants.LEFT);
        etiqueta.setFont(new Font("Trebuchet MS", Font.BOLD, 20));
        etiqueta.setForeground(new Color(9, 62, 100));
        return etiqueta;
    }

    private JLabel crearEtiquetaCampo(String texto) {
        JLabel etiqueta = new JLabel(texto, SwingConstants.LEFT);
        etiqueta.setFont(new Font("Verdana", Font.BOLD, 13));
        etiqueta.setForeground(new Color(27, 66, 91));
        return etiqueta;
    }

    private void conectarEventos() {
        cmbTipoConversion.addActionListener(evento -> actualizarUnidadesDisponibles());
        btnConvertir.addActionListener(evento -> ejecutarConversion());
        btnLimpiar.addActionListener(evento -> limpiarFormulario());
        btnCerrarSesion.addActionListener(evento -> cerrarSesion());
        txtValor.addActionListener(evento -> ejecutarConversion());
    }

    private void actualizarUnidadesDisponibles() {
        TipoConversion tipoSeleccionado = (TipoConversion) cmbTipoConversion.getSelectedItem();
        if (tipoSeleccionado == null) {
            return;
        }

        String[] unidades = tipoSeleccionado.getUnidadesDisponibles();
        cmbUnidadInicial.setModel(new DefaultComboBoxModel<>(unidades));
        cmbUnidadFinal.setModel(new DefaultComboBoxModel<>(unidades));

        if (cmbUnidadFinal.getItemCount() > 1) {
            cmbUnidadFinal.setSelectedIndex(1);
        }
    }

    private void ejecutarConversion() {
        try {
            double valor = parsearValor(txtValor.getText());
            TipoConversion tipo = (TipoConversion) cmbTipoConversion.getSelectedItem();
            String unidadInicial = (String) cmbUnidadInicial.getSelectedItem();
            String unidadFinal = (String) cmbUnidadFinal.getSelectedItem();

            if (tipo == null || unidadInicial == null || unidadFinal == null) {
                throw new IllegalArgumentException("Debe completar todos los datos de conversion.");
            }

            ConversionModelo modelo = new ConversionModelo(tipo, valor, unidadInicial, unidadFinal);
            double resultado = unidadesControlador.convertir(modelo);

            String mensajeResultado = "Resultado: "
                    + formatear(valor) + " " + unidadInicial
                    + " = " + formatear(resultado) + " " + unidadFinal;

            lblResultado.setText(mensajeResultado);
            registrarHistorial(mensajeResultado);
        } catch (SOAPFaultException ex) {
            mostrarError(ex.getFault().getFaultString());
        } catch (Exception ex) {
            mostrarError(ex.getMessage());
        }
    }

    private void limpiarFormulario() {
        txtValor.setText("");
        lblResultado.setText("Resultado: listo para convertir");
        txtValor.requestFocusInWindow();
    }

    private void cerrarSesion() {
        int opcion = JOptionPane.showConfirmDialog(
                this,
                "Desea cerrar la sesion actual?",
                "Confirmacion",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE
        );

        if (opcion != JOptionPane.YES_OPTION) {
            return;
        }

        loginControlador.cerrarSesion();
        new LoginVista().setVisible(true);
        dispose();
    }

    private void registrarHistorial(String mensajeResultado) {
        String selloTiempo = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        txtHistorial.append("[" + selloTiempo + "] " + mensajeResultado + System.lineSeparator());
        txtHistorial.setCaretPosition(txtHistorial.getDocument().getLength());
    }

    private double parsearValor(String texto) {
        if (texto == null || texto.trim().isBlank()) {
            throw new IllegalArgumentException("Debe ingresar un valor numerico.");
        }

        try {
            return Double.parseDouble(texto.trim().replace(',', '.'));
        } catch (NumberFormatException ex) {
            throw new IllegalArgumentException("Valor numerico invalido: " + texto);
        }
    }

    private String formatear(double numero) {
        return FORMATO_NUMERICO.format(numero);
    }

    private String resumirToken(String token) {
        if (token == null || token.isBlank()) {
            return "sin token";
        }
        if (token.length() <= 12) {
            return token;
        }
        return token.substring(0, 8) + "..." + token.substring(token.length() - 4);
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

    private void estilizarCombo(JComboBox<?> combo) {
        combo.setFont(new Font("Verdana", Font.PLAIN, 14));
        combo.setBorder(BorderFactory.createLineBorder(new Color(142, 183, 206), 1));
        combo.setBackground(Color.WHITE);
    }

    private void estilizarBotonPrincipal(JButton boton) {
        boton.setFont(new Font("Trebuchet MS", Font.BOLD, 15));
        boton.setBackground(new Color(23, 121, 166));
        boton.setForeground(Color.WHITE);
        boton.setFocusPainted(false);
        boton.setBorder(new EmptyBorder(10, 12, 10, 12));
    }

    private void estilizarBotonSecundario(JButton boton) {
        boton.setFont(new Font("Trebuchet MS", Font.BOLD, 14));
        boton.setBackground(new Color(236, 247, 253));
        boton.setForeground(new Color(18, 79, 112));
        boton.setFocusPainted(false);
        boton.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(141, 185, 208), 1),
                new EmptyBorder(9, 12, 9, 12)
        ));
    }

    private void mostrarError(String mensaje) {
        JOptionPane.showMessageDialog(this, mensaje, "Error", JOptionPane.ERROR_MESSAGE);
    }
}
