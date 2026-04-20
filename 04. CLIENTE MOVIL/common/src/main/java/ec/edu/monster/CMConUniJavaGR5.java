package ec.edu.monster;

import ec.edu.monster.modelo.ModeloSOAP;
import ec.edu.monster.vista.VistaGrafica;
import com.codename1.components.ToastBar;
import static com.codename1.ui.CN.*;
import com.codename1.system.Lifecycle;
import com.codename1.ui.*;
import com.codename1.ui.events.ActionEvent;

public class CMConUniJavaGR5 extends Lifecycle {
    
    // Instancias de MVC
    private ModeloSOAP modelo;
    private VistaGrafica vista;

    // Unidades
    private final String[] lenUnidades = {"MILIMETRO", "CENTIMETRO", "METRO", "KILOMETRO"};
    private final String[] masUnidades = {"MILIGRAMO", "GRAMO", "KILOGRAMO", "ONZA", "LIBRA"};
    private final String[] temUnidades = {"CELSIUS", "FAHRENHEIT", "KELVIN"};

    @Override
    public void runApp() {
        
        aplicarEstilosGlobalesDesplegables();
        
        modelo = new ModeloSOAP();
        vista = new VistaGrafica();
        iniciarControlador();
        
        // Arrancamos mostrando el Login
        vista.construirPantallaLogin().show();
    }

    private void iniciarControlador() {
        // --- EVENTOS DEL LOGIN ---
        vista.btnIngresar.addActionListener(e -> {
            String user = vista.txtUsuario.getText();
            String pass = vista.txtClave.getText();

            if(user.isEmpty() || pass.isEmpty()){
                ToastBar.showErrorMessage("Llene todos los campos");
                return;
            }

            scheduleBackgroundTask(() -> {
                String respuesta = modelo.enviarPeticionLogin(user, pass);
                callSerially(() -> {
                    if (respuesta.contains("Error") || respuesta.contains("No se pudo")) {
                        Dialog.show("Detalle del Servidor", respuesta, "OK", null);
                    } else {
                        modelo.setTokenGlobal(respuesta); // Guardamos token en el Modelo
                        prepararConversor();
                    }
                });
            });
        });
        
        // --- EVENTOS DEL DIAGNÓSTICO ---
        vista.btnPruebas.addActionListener(e -> prepararPruebas());

        vista.btnProbar.addActionListener(e -> {
            String urlPrueba = vista.txtUrl.getText();
            vista.lblEstado.setText("Estado: Conectando...");
            vista.lblEstado.getUnselectedStyle().setFgColor(0x333333);
            vista.txtRespuesta.setText("");
            
            scheduleBackgroundTask(() -> {
                int codigo = modelo.probarConexion(urlPrueba);
                
                callSerially(() -> {
                    if (codigo == 200) {
                        vista.lblEstado.setText("Estado: ¡CONEXIÓN EXITOSA!");
                        vista.lblEstado.getUnselectedStyle().setFgColor(0x008000); // Verde
                        modelo.setUrlWS(urlPrueba); // Actualizamos la URL en el modelo
                        vista.txtRespuesta.setText("El servidor respondió correctamente (HTTP 200). La nueva IP ha sido guardada.");
                    } else if (codigo == 0) {
                        vista.lblEstado.setText("Estado: ERROR DE RED (HTTP 0)");
                        vista.lblEstado.getUnselectedStyle().setFgColor(0xFF0000); // Rojo
                        vista.txtRespuesta.setText("El teléfono no puede alcanzar esa IP. Verifica el Wi-Fi.");
                    } else {
                        vista.lblEstado.setText("Estado: ERROR DEL SERVIDOR");
                        vista.lblEstado.getUnselectedStyle().setFgColor(0xFF8C00); // Naranja
                        vista.txtRespuesta.setText("HTTP Code: " + codigo + "\nRevisa la ruta de Payara.");
                    }
                    vista.lblEstado.getComponentForm().revalidate();
                });
            });
        });

        // --- EVENTOS DEL CONVERSOR ---
        vista.cbTipo.addActionListener(e -> actualizarUnidades());

        vista.btnConvertir.addActionListener(e -> {
            // Cambiamos getSelectedItem() por getSelectedString()
            String tipo = vista.cbTipo.getSelectedString();
            String valor = vista.txtValor.getText();
            String origen = vista.cbOrigen.getSelectedString();
            String destino = vista.cbDestino.getSelectedString();

            if(valor.isEmpty()) {
                ToastBar.showErrorMessage("Ingrese un valor");
                return;
            }

            scheduleBackgroundTask(() -> {
                String resultado = modelo.enviarPeticionConversor(tipo, valor, origen, destino);
                callSerially(() -> {
                    if (resultado.startsWith("Error")) {
                        vista.lblResultado.setText(resultado);
                    } else {
                        String textoRes = resultado + " " + destino;
                        vista.lblResultado.setText("Resultado: " + textoRes);
                        vista.agregarAlHistorial(valor + " " + origen + " = " + textoRes);
                        vista.lblResultado.getComponentForm().revalidate(); 
                    }
                });
            });
        });
    }

    private void prepararConversor() {
        Command cmdSalir = new Command("Salir") {
            @Override
            public void actionPerformed(ActionEvent evt) {
                modelo.setTokenGlobal(""); 
                vista.txtUsuario.setText("");
                vista.txtClave.setText("");
                vista.construirPantallaLogin().showBack();
            }
        };
        
        // 1. PRIMERO armamos la pantalla (Esto configura los Pickers como Texto)
        Form formConversor = vista.construirPantallaConversor(cmdSalir);
        
        // 2. LUEGO actualizamos las unidades iniciales
        actualizarUnidades(); 
        
        // 3. FINALMENTE mostramos la pantalla en el celular
        formConversor.show();
    }
    
    private void prepararPruebas() {
        Command cmdVolver = new Command("Volver") {
            @Override
            public void actionPerformed(ActionEvent evt) {
                vista.construirPantallaLogin().showBack();
            }
        };
        // Le pasamos la URL actual que tiene el modelo para que la ponga en la caja de texto
        vista.construirPantallaPruebas(cmdVolver, modelo.getUrlWS()).show();
    }

    private void actualizarUnidades() {
        // Cambiamos getSelectedItem() por getSelectedString()
        String tipoSel = vista.cbTipo.getSelectedString();
        
        if ("Longitud".equals(tipoSel)) vista.actualizarListas(lenUnidades);
        else if ("Masa".equals(tipoSel)) vista.actualizarListas(masUnidades);
        else if ("Temperatura".equals(tipoSel)) vista.actualizarListas(temUnidades);
    }
    
    private void aplicarEstilosGlobalesDesplegables() {
        // 1. Forzar el fondo del contenedor de la lista a blanco sólido
        com.codename1.ui.plaf.Style estiloPopup = com.codename1.ui.plaf.UIManager.getInstance().getComponentStyle("ComboBoxPopup");
        estiloPopup.setBgTransparency(255);
        estiloPopup.setBgColor(0xffffff);

        // 2. Forzar cada opción de la lista (Items) a fondo blanco y letra negra
        com.codename1.ui.plaf.Style estiloItem = com.codename1.ui.plaf.UIManager.getInstance().getComponentStyle("ComboBoxItem");
        estiloItem.setBgTransparency(255);
        estiloItem.setBgColor(0xffffff);
        estiloItem.setFgColor(0x000000); // Texto negro
        
        // Un poco de espacio para que sea fácil tocarlas con el dedo
        estiloItem.setPadding(10, 10, 5, 5); 
    }
}