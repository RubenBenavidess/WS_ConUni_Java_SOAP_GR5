package ec.edu.monster.controlador;

import java.io.IOException;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@WebServlet(name = "LogoutServlet", urlPatterns = {"/LogoutServlet"})
public class LogoutServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        // 1. Obtener la sesión actual, pero sin crear una nueva (false)
        HttpSession session = request.getSession(false);
        
        // 2. Si la sesión existe, la destruimos
        if (session != null) {
            session.invalidate(); // Esto borra el "tokenGlobal" y la memoria de sesión
        }
        
        // 3. Redirigir al usuario de vuelta a la página de Login
        response.sendRedirect("index.jsp");
    }
}