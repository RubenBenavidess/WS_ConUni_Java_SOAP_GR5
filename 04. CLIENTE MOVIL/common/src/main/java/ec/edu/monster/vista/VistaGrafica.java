package ec.edu.monster.vista;

import com.codename1.ui.*;
import com.codename1.ui.layouts.*;
import com.codename1.ui.list.DefaultListModel;
import java.io.IOException;
import com.codename1.ui.spinner.Picker;
import com.codename1.ui.Display;

public class VistaGrafica {
    private final int COLOR_FONDO = 0x0b3b5b; 
    private final int COLOR_PANEL = 0xeaf1f8; 
    private final int COLOR_TEXTO = 0x0d3b5e; 

    // Componentes expuestos para que el Controlador los escuche
    public TextField txtUsuario = new TextField("", "Usuario", 20, TextField.ANY);
    public TextField txtClave = new TextField("", "Contraseña", 20, TextField.PASSWORD);
    public Button btnIngresar = new Button("Ingresar");
    public Button btnPruebas = new Button("🛠️ Diagnóstico de Red");
    
    // Componentes de la pantalla de Diagnóstico
    public TextField txtUrl = new TextField("", "http://...", 50, TextField.URL);
    public Button btnProbar = new Button("Hacer Ping al Servidor");
    public Label lblEstado = new Label("Estado: Esperando...");
    public TextArea txtRespuesta = new TextArea(5, 20);
    private Form formPruebasGuardado;
    
    public Picker cbTipo = new Picker();
    public TextField txtValor = new TextField("", "Valor a convertir", 20, TextArea.DECIMAL);
    public Picker cbOrigen = new Picker();
    public Picker cbDestino = new Picker();
    public Button btnConvertir = new Button("Convertir Ahora");
    public Label lblResultado = new Label("Resultado aparecerá aquí");
    public Container panelHistorial = new Container(BoxLayout.y());
    private Form formLoginGuardado;
    private Form formConversorGuardado;

    public Form construirPantallaLogin() {
        // MAGIA: Si el formulario ya existe, no lo volvemos a armar, solo lo devolvemos
        if (formLoginGuardado != null) {
            return formLoginGuardado;
        }

        formLoginGuardado = new Form("Iniciar Sesión", BoxLayout.y());
        formLoginGuardado.getUnselectedStyle().setBgColor(COLOR_FONDO);
        
        Container panelCentral = new Container(BoxLayout.y());
        aplicarEstiloPanel(panelCentral);

        try {
            Image imgSullivan = Image.createImage("/sullivan_movil.jpg"); 
            int alto = 500;
            int ancho = (imgSullivan.getWidth() * alto) / imgSullivan.getHeight(); 
            Label lblImagen = new Label(imgSullivan.scaled(ancho, alto));
            lblImagen.getUnselectedStyle().setAlignment(Component.CENTER);
            panelCentral.add(lblImagen);
        } catch (IOException e) { /* Ignorar error de imagen en vista */ }

        Label lblTitulo = new Label("Acceso Monster");
        lblTitulo.getUnselectedStyle().setFgColor(COLOR_TEXTO);
        lblTitulo.getUnselectedStyle().setFont(Font.createSystemFont(Font.FACE_SYSTEM, Font.STYLE_BOLD, Font.SIZE_LARGE));
        lblTitulo.getUnselectedStyle().setAlignment(Component.CENTER);
        
        btnIngresar.getUnselectedStyle().setMarginTop(15);
        btnPruebas.getUnselectedStyle().setBgTransparency(0);
        btnPruebas.getUnselectedStyle().setFgColor(0x555555);

        panelCentral.addAll(lblTitulo, new Label("Usuario:"), txtUsuario, new Label("Contraseña:"), txtClave, btnIngresar, btnPruebas);
        formLoginGuardado.add(panelCentral);
        
        return formLoginGuardado;
    }

    public Form construirPantallaConversor(Command cmdSalir) {
        // MAGIA: Si ya existe, lo reutilizamos
        if (formConversorGuardado != null) {
            return formConversorGuardado;
        }

        formConversorGuardado = new Form("Conversor Inteligente", BoxLayout.y());
        formConversorGuardado.getUnselectedStyle().setBgColor(COLOR_FONDO);
        formConversorGuardado.setScrollableY(true);
        formConversorGuardado.getToolbar().setBackCommand(cmdSalir); // Tu nuevo botón de salir nativo

        Container panelControles = new Container(BoxLayout.y());
        aplicarEstiloPanel(panelControles);
        
        // --- NUEVA CONFIGURACIÓN DEL PICKER ---
        cbTipo.setType(Display.PICKER_TYPE_STRINGS);
        cbTipo.setStrings("Longitud", "Masa", "Temperatura");
        cbTipo.setSelectedString("Longitud");

        cbOrigen.setType(Display.PICKER_TYPE_STRINGS);
        cbDestino.setType(Display.PICKER_TYPE_STRINGS);

        // --- LOS ESTILOS SE MANTIENEN EXACTAMENTE IGUAL ---
        cbTipo.setUIID("TextField");
        cbOrigen.setUIID("TextField");
        cbDestino.setUIID("TextField");
        
        lblResultado.getUnselectedStyle().setFgColor(0x0056b3);

        panelControles.addAll(new Label("Tipo de conversión:"), cbTipo, new Label("Valor:"), txtValor, new Label("De:"), cbOrigen, new Label("A:"), cbDestino, btnConvertir, lblResultado);

        aplicarEstiloPanel(panelHistorial);
        Label lblHistorialTitulo = new Label("Historial de conversiones:");
        lblHistorialTitulo.getUnselectedStyle().setFgColor(COLOR_TEXTO);
        lblHistorialTitulo.getUnselectedStyle().setFont(Font.createSystemFont(Font.FACE_SYSTEM, Font.STYLE_BOLD, Font.SIZE_MEDIUM));
        panelHistorial.add(lblHistorialTitulo);

        formConversorGuardado.addAll(panelControles, panelHistorial);
        
        return formConversorGuardado;
    }

    public void actualizarListas(String[] unidades) {
        cbOrigen.setStrings(unidades);
        cbOrigen.setSelectedString(unidades[0]); // Selecciona el primero por defecto
        
        cbDestino.setStrings(unidades);
        cbDestino.setSelectedString(unidades[0]);
    }

    public void agregarAlHistorial(String texto) {
        Label item = new Label("• " + texto);
        item.getUnselectedStyle().setFgColor(COLOR_TEXTO);
        panelHistorial.add(item);
    }

    private void aplicarEstiloPanel(Container c) {
        c.getUnselectedStyle().setBgColor(COLOR_PANEL);
        c.getUnselectedStyle().setBgTransparency(255);
        c.getUnselectedStyle().setMargin(5, 5, 10, 10);
        c.getUnselectedStyle().setPadding(10, 10, 10, 10);
        c.getStyle().setBorder(com.codename1.ui.plaf.Border.createRoundBorder(15, COLOR_PANEL));
    }
    
    public Form construirPantallaPruebas(Command cmdVolver, String urlActual) {
        if (formPruebasGuardado != null) {
            // Si ya existe, solo actualizamos los textos y la devolvemos
            txtUrl.setText(urlActual);
            lblEstado.setText("Estado: Esperando...");
            lblEstado.getUnselectedStyle().setFgColor(0x333333);
            txtRespuesta.setText("El código HTTP aparecerá aquí");
            return formPruebasGuardado;
        }

        formPruebasGuardado = new Form("Diagnóstico de Red", BoxLayout.y());
        formPruebasGuardado.getUnselectedStyle().setBgColor(0xF0F0F0);
        formPruebasGuardado.getToolbar().setBackCommand(cmdVolver);

        Container panel = new Container(BoxLayout.y());
        panel.getUnselectedStyle().setMargin(10, 10, 10, 10);
        panel.getUnselectedStyle().setPadding(10, 10, 10, 10);

        Label lblInstruccion = new Label("URL del Servidor Payara:");
        lblInstruccion.getUnselectedStyle().setFgColor(0x000000);

        txtUrl.setText(urlActual);
        txtUrl.getUnselectedStyle().setFgColor(0x000000);
        
        btnProbar.getUnselectedStyle().setMarginTop(15);
        
        lblEstado.getUnselectedStyle().setFgColor(0x333333);
        
        txtRespuesta.setEditable(false);
        txtRespuesta.setText("El código HTTP aparecerá aquí");
        txtRespuesta.getUnselectedStyle().setFgColor(0x000000);

        panel.addAll(lblInstruccion, txtUrl, btnProbar, lblEstado, txtRespuesta);
        formPruebasGuardado.add(panel);

        return formPruebasGuardado;
    }
}