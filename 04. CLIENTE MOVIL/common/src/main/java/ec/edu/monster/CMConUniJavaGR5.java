package ec.edu.monster;

import com.codename1.components.SpanLabel;
import com.codename1.components.ToastBar;
import static com.codename1.ui.CN.*;
import com.codename1.system.Lifecycle;
import com.codename1.ui.*;
import com.codename1.ui.layouts.*;
import com.codename1.ui.list.DefaultListModel;
import com.codename1.io.*;
import java.io.IOException;
import java.io.OutputStream;

public class CMConUniJavaGR5 extends Lifecycle {
    
    // Variable global para guardar el token
    private String tokenGlobal = "";
    // URL de tu Web Service (Mantenemos la IP de tu red local)
    private String urlWS = "http://192.168.18.9:8080/04_SERVIDOR/WSConversorUnidades";

    // Paleta de colores para igualar a la web
    private final int COLOR_FONDO = 0x0b3b5b; // Azul oscuro
    private final int COLOR_PANEL = 0xeaf1f8; // Celeste claro
    private final int COLOR_TEXTO = 0x0d3b5e; // Azul texto

    // Listas de unidades
    private final String[] lenUnidades = {"MILIMETRO", "CENTIMETRO", "METRO", "KILOMETRO"};
    private final String[] masUnidades = {"MILIGRAMO", "GRAMO", "KILOGRAMO", "ONZA", "LIBRA"};
    private final String[] temUnidades = {"CELSIUS", "FAHRENHEIT", "KELVIN"};

    @Override
    public void runApp() {
        mostrarPantallaLogin();
    }

    // =========================================================
    // 1. PANTALLA DE LOGIN (Con Imagen)
    // =========================================================
    private void mostrarPantallaLogin() {
        Form formLogin = new Form("Iniciar Sesión", BoxLayout.y());
        formLogin.getUnselectedStyle().setBgColor(COLOR_FONDO);
        formLogin.getUnselectedStyle().setBgTransparency(255);
        
        Container panelCentral = new Container(BoxLayout.y());
        aplicarEstiloPanel(panelCentral);

        // --- Cargar y mostrar la imagen centrada ---
        try {
            Image imgSullivan = Image.createImage("/sullivan_movil.jpg"); 
            
            // Escalamos a 120px de alto para el login
            int altoDeseado = 500;
            int anchoDeseado = (imgSullivan.getWidth() * altoDeseado) / imgSullivan.getHeight(); 
            Image imgEscalada = imgSullivan.scaled(anchoDeseado, altoDeseado);
            
            Label lblImagen = new Label(imgEscalada);
            lblImagen.getUnselectedStyle().setAlignment(Component.CENTER); // Centramos la imagen
            lblImagen.getUnselectedStyle().setMarginBottom(10);
            
            panelCentral.add(lblImagen);
            
        } catch (IOException e) {
            System.out.println("No se pudo cargar la imagen en el Login");
        }
        // -------------------------------------------

        Label lblTitulo = new Label("Acceso Monster");
        lblTitulo.getUnselectedStyle().setFgColor(COLOR_TEXTO);
        lblTitulo.getUnselectedStyle().setFont(Font.createSystemFont(Font.FACE_SYSTEM, Font.STYLE_BOLD, Font.SIZE_LARGE));
        lblTitulo.getUnselectedStyle().setAlignment(Component.CENTER);
        
        TextField txtUsuario = new TextField("", "Usuario", 20, TextField.ANY);
        TextField txtClave = new TextField("", "Contraseña", 20, TextField.PASSWORD);
        Button btnIngresar = new Button("Ingresar");
        btnIngresar.getUnselectedStyle().setMarginTop(15); // Espacio antes del botón

        panelCentral.addAll(
            lblTitulo, 
            new Label("Usuario:"), txtUsuario, 
            new Label("Contraseña:"), txtClave, 
            btnIngresar
        );
        
        // --- Botón de Diagnóstico ---
        Button btnPruebas = new Button("🛠️ Diagnóstico de Red");
        // Le damos estilo de enlace de texto para que no parezca un botón principal
        btnPruebas.getUnselectedStyle().setBgTransparency(0);
        btnPruebas.getUnselectedStyle().setFgColor(0x555555);
        btnPruebas.getUnselectedStyle().setAlignment(Component.CENTER);
        btnPruebas.getUnselectedStyle().setFont(Font.createSystemFont(Font.FACE_SYSTEM, Font.STYLE_PLAIN, Font.SIZE_SMALL));
        
        btnPruebas.addActionListener(ev -> mostrarPantallaPruebas());
        
        panelCentral.add(btnPruebas);
        
        formLogin.add(panelCentral);

        btnIngresar.addActionListener(e -> {
            String user = txtUsuario.getText();
            String pass = txtClave.getText();

            if(user.isEmpty() || pass.isEmpty()){
                ToastBar.showErrorMessage("Llene todos los campos");
                return;
            }

            CN.scheduleBackgroundTask(() -> {
                String respuesta = enviarPeticionLogin(user, pass);
                CN.callSerially(() -> {
                    if (respuesta.contains("Error") || respuesta.contains("No se pudo")) {
                        Dialog.show("Detalle del Servidor", respuesta, "OK", null);
                    } else {
                        tokenGlobal = respuesta; 
                        mostrarPantallaConversor(); 
                    }
                });
            });
        });

        formLogin.show();
    }

    // =========================================================
    // 2. PANTALLA DEL CONVERSOR
    // =========================================================
    private void mostrarPantallaConversor() {
        Form formConversor = new Form("Conversor Inteligente", BoxLayout.y());
        formConversor.getUnselectedStyle().setBgColor(COLOR_FONDO);
        formConversor.getUnselectedStyle().setBgTransparency(255);
        formConversor.setScrollableY(true);

        // --- 1. Panel de Cabecera (con la Imagen) ---
        Container panelCabecera = new Container(new BorderLayout());
        aplicarEstiloPanel(panelCabecera);
        
        // Textos de la cabecera
        Container textosCabecera = new Container(BoxLayout.y());
        Label lblTituloCabecera = new Label("Conversor Inteligente");
        lblTituloCabecera.getUnselectedStyle().setFgColor(COLOR_TEXTO);
        lblTituloCabecera.getUnselectedStyle().setFont(Font.createSystemFont(Font.FACE_SYSTEM, Font.STYLE_BOLD, Font.SIZE_LARGE));
        
        Label lblSubtitulo = new Label("Longitud, masa y temperatura");
        lblSubtitulo.getUnselectedStyle().setFgColor(COLOR_TEXTO);
        lblSubtitulo.getUnselectedStyle().setFont(Font.createSystemFont(Font.FACE_SYSTEM, Font.STYLE_PLAIN, Font.SIZE_SMALL));
        
        textosCabecera.addAll(lblTituloCabecera, lblSubtitulo);
        panelCabecera.add(BorderLayout.CENTER, textosCabecera);

        // Cargar y mostrar la imagen de Sullivan
        // Cargar y mostrar la imagen de Sullivan
        try {
            // Usamos tu nueva imagen
            Image imgSullivan = Image.createImage("/sullivan_movil.jpg"); 
            
            // FORMA SEGURA DE ESCALAR: Definimos píxeles exactos (ej. 150px de alto)
            int altoDeseado = 150;
            // Calculamos el ancho manteniendo la proporción original
            int anchoDeseado = (imgSullivan.getWidth() * altoDeseado) / imgSullivan.getHeight(); 
            
            // Escalamos
            Image imgEscalada = imgSullivan.scaled(anchoDeseado, altoDeseado);
            
            Label lblImagen = new Label(imgEscalada);
            // Quitamos márgenes por si están empujando la imagen fuera
            lblImagen.getUnselectedStyle().setMargin(0, 0, 0, 0);
            lblImagen.getUnselectedStyle().setPadding(0, 0, 0, 0);
            
            panelCabecera.add(BorderLayout.EAST, lblImagen);
            
            // Esto nos confirmará en la consola que sí se dibujó con un tamaño real
            System.out.println("EXITO: Imagen cargada y escalada a " + anchoDeseado + "x" + altoDeseado);
            
        } catch (IOException e) {
            System.out.println("===== ERROR CARGANDO LA IMAGEN =====");
            e.printStackTrace();
            System.out.println("====================================");
        }

        // --- 2. Panel de Controles ---
        Container panelControles = new Container(BoxLayout.y());
        aplicarEstiloPanel(panelControles);

        String[] tipos = {"Longitud", "Masa", "Temperatura"};
        ComboBox<String> cbTipo = new ComboBox<>(tipos);
        TextField txtValor = new TextField("", "Valor a convertir", 20, TextArea.DECIMAL);
        ComboBox<String> cbOrigen = new ComboBox<>(lenUnidades); 
        ComboBox<String> cbDestino = new ComboBox<>(lenUnidades);
        
        cbTipo.getAllStyles().setFgColor(0x000000);
        cbOrigen.getAllStyles().setFgColor(0x000000);
        cbDestino.getAllStyles().setFgColor(0x000000);
        txtValor.getAllStyles().setFgColor(0x000000);
        
        cbTipo.getAllStyles().setAlignment(Component.CENTER);
        cbOrigen.getAllStyles().setAlignment(Component.CENTER);
        cbDestino.getAllStyles().setAlignment(Component.CENTER);
        
        Button btnConvertir = new Button("Convertir Ahora");
        Label lblResultado = new Label("Resultado aparecerá aquí");
        lblResultado.getUnselectedStyle().setFgColor(0x0056b3); // Color azul de resultado

        // Lógica de ComboBox dependientes
        cbTipo.addActionListener(e -> {
            String tipoSel = cbTipo.getSelectedItem();
            if ("Longitud".equals(tipoSel)) {
                cbOrigen.setModel(new DefaultListModel<>(lenUnidades));
                cbDestino.setModel(new DefaultListModel<>(lenUnidades));
            } else if ("Masa".equals(tipoSel)) {
                cbOrigen.setModel(new DefaultListModel<>(masUnidades));
                cbDestino.setModel(new DefaultListModel<>(masUnidades));
            } else if ("Temperatura".equals(tipoSel)) {
                cbOrigen.setModel(new DefaultListModel<>(temUnidades));
                cbDestino.setModel(new DefaultListModel<>(temUnidades));
            }
        });

        panelControles.addAll(
            new Label("Tipo de conversión:"), cbTipo,
            new Label("Valor a convertir:"), txtValor,
            new Label("Unidad Inicial:"), cbOrigen,
            new Label("Unidad Final:"), cbDestino,
            btnConvertir,
            lblResultado
        );

        // --- 3. Panel de Historial ---
        Container panelHistorial = new Container(BoxLayout.y());
        aplicarEstiloPanel(panelHistorial);
        Label lblHistorialTitulo = new Label("Historial de conversiones:");
        lblHistorialTitulo.getUnselectedStyle().setFgColor(COLOR_TEXTO);
        lblHistorialTitulo.getUnselectedStyle().setFont(Font.createSystemFont(Font.FACE_SYSTEM, Font.STYLE_BOLD, Font.SIZE_MEDIUM));
        panelHistorial.add(lblHistorialTitulo);

        // Armar el Form principal
        formConversor.addAll(panelCabecera, panelControles, panelHistorial);
        formConversor.getToolbar().addCommandToLeftBar("Salir", null, ev -> mostrarPantallaLogin());

        // Acción del botón
        btnConvertir.addActionListener(e -> {
            String tipo = cbTipo.getSelectedItem();
            String valor = txtValor.getText();
            String origen = cbOrigen.getSelectedItem();
            String destino = cbDestino.getSelectedItem();

            if(valor.isEmpty()) {
                ToastBar.showErrorMessage("Ingrese un valor");
                return;
            }

            CN.scheduleBackgroundTask(() -> {
                String resultado = enviarPeticionConversor(tipo, valor, origen, destino);
                CN.callSerially(() -> {
                    if (resultado.startsWith("Error")) {
                        lblResultado.setText(resultado);
                    } else {
                        String textoRes = resultado + " " + destino;
                        lblResultado.setText("Resultado: " + textoRes);
                        
                        Label itemHistorial = new Label("• " + valor + " " + origen + " = " + textoRes);
                        itemHistorial.getUnselectedStyle().setFgColor(COLOR_TEXTO);
                        panelHistorial.add(itemHistorial);
                        
                        formConversor.revalidate(); 
                    }
                });
            });
        });

        formConversor.show();
    }

    // Método auxiliar para dar diseño de "tarjeta" a los contenedores
    private void aplicarEstiloPanel(Container c) {
        c.getUnselectedStyle().setBgColor(COLOR_PANEL);
        c.getUnselectedStyle().setBgTransparency(255);
        c.getUnselectedStyle().setMargin(5, 5, 10, 10);
        c.getUnselectedStyle().setPadding(10, 10, 10, 10);
        // Borde redondeado (opcional dependiendo del soporte en tu CN1, pero añade un toque visual)
        c.getStyle().setBorder(com.codename1.ui.plaf.Border.createRoundBorder(15, COLOR_PANEL));
    }

    // =========================================================
    // 3. MÉTODOS DE CONEXIÓN AL SERVIDOR
    // =========================================================
    
    private String enviarPeticionLogin(String usuario, String contrasenia) {
        String soapRequest = 
            "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:ws=\"http://ws.monster.edu.ec/\">" +
            "   <soapenv:Header/>" +
            "   <soapenv:Body>" +
            "      <ws:login>" +
            "         <usuario>" + usuario + "</usuario>" +
            "         <contrasenia>" + contrasenia + "</contrasenia>" +
            "      </ws:login>" +
            "   </soapenv:Body>" +
            "</soapenv:Envelope>";
            
        return ejecutarRequestSOAP(soapRequest);
    }

    // Método actualizado: Ahora recibe el TIPO y arma el XML dinámicamente
    private String enviarPeticionConversor(String tipo, String valor, String origen, String destino) {
        
        String operacionSOAP = "";
        switch (tipo) {
            case "Longitud": operacionSOAP = "convertirLongitud"; break;
            case "Masa": operacionSOAP = "convertirMasa"; break;
            case "Temperatura": operacionSOAP = "convertirTemperatura"; break;
        }

        String soapRequest = 
            "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:ws=\"http://ws.monster.edu.ec/\">" +
            "   <soapenv:Header>" +
            "      <ws:token>" + tokenGlobal + "</ws:token>" +
            "   </soapenv:Header>" +
            "   <soapenv:Body>" +
            "      <ws:" + operacionSOAP + ">" +
            "         <valor>" + valor + "</valor>" +
            "         <unidadInicial>" + origen + "</unidadInicial>" +
            "         <unidadFinal>" + destino + "</unidadFinal>" +
            "      </ws:" + operacionSOAP + ">" +
            "   </soapenv:Body>" +
            "</soapenv:Envelope>";

        return ejecutarRequestSOAP(soapRequest);
    }

    private String ejecutarRequestSOAP(String xmlSOAP) {
        ConnectionRequest req = new ConnectionRequest() {
            @Override
            protected void buildRequestBody(OutputStream os) throws IOException {
                os.write(xmlSOAP.getBytes("UTF-8"));
            }
        };

        req.setUrl(urlWS);
        req.setPost(true);
        req.setContentType("text/xml; charset=utf-8");
        
        // 1. Evita que salga el popup de error genérico que te estaba saliendo
        req.setFailSilently(true); 
        
        // 2. Cabecera obligatoria en algunos servidores JAX-WS para clientes móviles
        //req.addRequestHeader("SOAPAction", "\"\""); 

        NetworkManager.getInstance().addToQueueAndWait(req);

        if (req.getResponseCode() == 200) {
            String response = new String(req.getResponseData());
            return extraerDato(response, "<return>", "</return>");
        } else {
            // 3. Ahora el label nos dirá exactamente QUÉ está fallando
            return "Error HTTP: " + req.getResponseCode();
        }
    }

    private String extraerDato(String xml, String tagInicio, String tagFin) {
        int start = xml.indexOf(tagInicio);
        if (start != -1) {
            start += tagInicio.length();
            int end = xml.indexOf(tagFin, start);
            if (end != -1) {
                return xml.substring(start, end);
            }
        }
        return "No se pudo procesar la respuesta";
    }
    
    // =========================================================
    // PANTALLA DE DIAGNÓSTICO Y PRUEBAS DE RED
    // =========================================================
    private void mostrarPantallaPruebas() {
        Form formPruebas = new Form("Diagnóstico de Red", BoxLayout.y());
        formPruebas.getUnselectedStyle().setBgColor(0xF0F0F0); // Color de fondo gris claro
        
        // Botón para volver atrás
        formPruebas.getToolbar().addCommandToLeftBar("Volver", null, ev -> mostrarPantallaLogin());

        Container panel = new Container(BoxLayout.y());
        panel.getUnselectedStyle().setMargin(10, 10, 10, 10);
        panel.getUnselectedStyle().setPadding(10, 10, 10, 10);

        Label lblInstruccion = new Label("URL del Servidor Payara:");
        lblInstruccion.getUnselectedStyle().setFgColor(0x000000);

        // Campo para editar la URL en tiempo real
        TextField txtUrl = new TextField(urlWS, "http://...", 50, TextField.URL);
        txtUrl.getUnselectedStyle().setFgColor(0x000000);
        
        Button btnProbar = new Button("Hacer Ping al Servidor");
        btnProbar.getUnselectedStyle().setMarginTop(15);
        
        Label lblEstado = new Label("Estado: Esperando...");
        lblEstado.getUnselectedStyle().setFgColor(0x333333);
        
        TextArea txtRespuesta = new TextArea(5, 20);
        txtRespuesta.setEditable(false);
        txtRespuesta.setText("El código HTTP aparecerá aquí");
        txtRespuesta.getUnselectedStyle().setFgColor(0x000000);

        panel.addAll(lblInstruccion, txtUrl, btnProbar, lblEstado, txtRespuesta);
        formPruebas.add(panel);

        // Lógica de la prueba
        btnProbar.addActionListener(e -> {
            String urlPrueba = txtUrl.getText();
            lblEstado.setText("Estado: Conectando...");
            txtRespuesta.setText("");
            
            CN.scheduleBackgroundTask(() -> {
                ConnectionRequest req = new ConnectionRequest();
                // Añadimos ?wsdl para forzar una respuesta GET simple del servidor
                req.setUrl(urlPrueba + "?wsdl"); 
                req.setPost(false); // Es un simple GET de prueba
                req.setFailSilently(true); // Queremos ver el error crudo

                NetworkManager.getInstance().addToQueueAndWait(req);

                CN.callSerially(() -> {
                    int codigo = req.getResponseCode();
                    if (codigo == 200) {
                        lblEstado.setText("Estado: ¡CONEXIÓN EXITOSA!");
                        lblEstado.getUnselectedStyle().setFgColor(0x008000); // Verde
                        // Guardamos la nueva URL si funcionó
                        urlWS = urlPrueba; 
                        txtRespuesta.setText("El servidor respondió correctamente (HTTP 200). La nueva IP ha sido guardada para esta sesión.");
                    } else if (codigo == 0) {
                        lblEstado.setText("Estado: ERROR DE RED (HTTP 0)");
                        lblEstado.getUnselectedStyle().setFgColor(0xFF0000); // Rojo
                        txtRespuesta.setText("El teléfono no puede alcanzar esa IP. Verifica que estés en el mismo Wi-Fi y sin datos móviles.");
                    } else {
                        lblEstado.setText("Estado: ERROR DEL SERVIDOR");
                        lblEstado.getUnselectedStyle().setFgColor(0xFF8C00); // Naranja
                        txtRespuesta.setText("HTTP Code: " + codigo + "\nLa PC responde, pero el servidor devolvió error. Revisa la ruta de Payara.");
                    }
                    formPruebas.revalidate();
                });
            });
        });

        formPruebas.show();
    }
}