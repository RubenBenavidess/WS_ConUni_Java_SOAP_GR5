package ec.edu.monster.controlador; // Cambia esto si usaste otro paquete

import ec.edu.monster.web.ClienteSOAP;
import java.io.IOException;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@WebServlet(name = "LoginServlet", urlPatterns = {"/LoginServlet"})
public class LoginServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        // 1. Recibir los datos del formulario (Vista)
        String usr = request.getParameter("usuario");
        String pwd = request.getParameter("contrasenia");
        String mensajeError = "";

        try {
            // 2. Hablar con el Modelo (ClienteSOAP)
            String tokenGenerado = ClienteSOAP.login(usr, pwd);
            
            if (tokenGenerado != null && !tokenGenerado.contains("Error") && !tokenGenerado.contains("Respuesta no válida")) {
                // 3a. Éxito: Guardar sesión y redirigir
                HttpSession session = request.getSession();
                session.setAttribute("tokenGlobal", tokenGenerado);
                response.sendRedirect("conversor.jsp"); 
                return;
            } else {
                mensajeError = "Credenciales incorrectas.";
            }
        } catch (Exception e) {
            mensajeError = "Falla de conexión: " + e.getMessage();
        }

        // 3b. Fallo: Enviar el error de vuelta a la Vista
        request.setAttribute("mensajeError", mensajeError);
        request.getRequestDispatcher("index.jsp").forward(request, response);
    }
}