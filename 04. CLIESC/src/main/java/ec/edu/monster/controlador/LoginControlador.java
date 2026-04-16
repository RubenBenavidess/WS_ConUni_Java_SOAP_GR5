package ec.edu.monster.controlador;

import ec.edu.monster.ClienteWSConversor;
import ec.edu.monster.modelo.LoginModelo;

public class LoginControlador {

    private final ClienteWSConversor clienteWs;

    public LoginControlador() {
        this.clienteWs = new ClienteWSConversor();
    }

    public String autenticar(LoginModelo loginModelo) {
        if (loginModelo == null) {
            throw new IllegalArgumentException("Los datos de inicio de sesion son obligatorios.");
        }
        if (loginModelo.getUsuario() == null || loginModelo.getUsuario().isBlank()) {
            throw new IllegalArgumentException("El usuario es obligatorio.");
        }
        if (loginModelo.getContrasenia() == null || loginModelo.getContrasenia().isBlank()) {
            throw new IllegalArgumentException("La contrasenia es obligatoria.");
        }

        return clienteWs.login(loginModelo.getUsuario(), loginModelo.getContrasenia());
    }

    public UnidadesControlador crearControladorUnidades() {
        return new UnidadesControlador(clienteWs);
    }

    public String getEndpointActivo() {
        return clienteWs.getEndpointActivo();
    }

    public String getTokenSesion() {
        return clienteWs.getTokenSesion();
    }

    public void cerrarSesion() {
        clienteWs.cerrarSesion();
    }
}
