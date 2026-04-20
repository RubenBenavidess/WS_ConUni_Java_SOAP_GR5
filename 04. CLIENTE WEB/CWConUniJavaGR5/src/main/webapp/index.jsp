<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%
    // Recuperar el mensaje de error enviado por el Servlet (si existe)
    String mensajeError = (String) request.getAttribute("mensajeError");
    if (mensajeError == null) {
        mensajeError = "";
    }
%>
<!DOCTYPE html>
<html>
<head>
    <title>Login - Monster Web</title>
    <style>
        /* Fondo principal imitando el azul oscuro del sistema de escritorio */
        body { 
            font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif; 
            background: linear-gradient(135deg, #0b2239 0%, #154360 100%);
            display: flex; 
            justify-content: center; 
            align-items: center; 
            height: 100vh; 
            margin: 0; 
        }
        
        /* Contenedor principal con bordes redondeados y sombra */
        .login-box { 
            background: #e9f0f6; /* Color gris/celeste claro del panel */
            border-radius: 15px; 
            box-shadow: 0 10px 25px rgba(0,0,0,0.5); 
            display: flex;
            width: 750px;
            max-width: 95%;
            max-height: 460px;
            overflow: hidden;
        }

        /* Columna izquierda para la imagen */
        .img-panel {
            flex: 1;
            display: flex;
            justify-content: center;
            align-items: center;
            padding: 5px;
        }

        .img-panel img {
            max-width: 100%;
            border-radius: 5px;
            margin: 0 10px 0 50px;
            height: auto;
        }

        /* Columna derecha para el formulario */
        .form-panel {
            flex: 1.2;
            padding: 40px;
            display: flex;
            flex-direction: column;
            justify-content: center;
        }

        /* Títulos */
        .form-panel h2 {
            color: #15537A;
            text-align: center;
            margin: 0 0 1px 0;
            font-size: 24px;
        }

        .form-panel h3 {
            color: #15537A;
            text-align: center;
            margin: 0 0 10px 0;
            font-size: 16px;
        }

        /* Etiquetas de los inputs */
        label {
            color: #0b2239;
            font-weight: bold;
            display: block;
            margin-bottom: 8px;
            font-size: 15px;
        }

        /* Cajas de texto */
        input[type="text"], input[type="password"] { 
            width: 100%; 
            padding: 10px; 
            margin-bottom: 20px; 
            border: 1px solid #b3c6d6; 
            border-radius: 2px; 
            box-sizing: border-box;
            font-size: 14px;
        }

        /* Botón azul principal */
        button { 
            background-color: #005b8f; 
            color: white; 
            border: none; 
            padding: 12px; 
            cursor: pointer; 
            font-size: 16px;
            width: 100%;
            font-weight: bold;
            margin-top: 10px;
        }
        
        button:hover { 
            background-color: #00466e; 
        }

        /* Mensajes de estado y error */
        .error { 
            color: #d93025; 
            font-size: 14px; 
            text-align: center;
            margin-bottom: 15px;
            min-height: 20px;
        }

        .status {
            color: #005b8f;
            text-align: center;
            font-size: 13px;
            margin-top: 20px;
        }

        /* Diseño responsivo para pantallas pequeñas (móviles) */
        @media (max-width: 600px) {
            .login-box {
                flex-direction: column;
            }
            .img-panel {
                padding: 20px 20px 0 20px;
            }
            .img-panel img {
                max-width: 150px;
            }
        }
    </style>
</head>
<body>
    <div class="login-box">
        <div class="img-panel">
            <img src="assets/sullivan_computer.jpg" alt="Personajes Monsters">
        </div>

        <div class="form-panel">
            <h2>Sistema de Conversion<br>de Unidades</h2>
            <h3>GRUPO #5</h3>
            
            <p class="error"><%= mensajeError %></p>
            
            <form action="LoginServlet" method="POST">
                <label for="usuario">Usuario</label>
                <input type="text" id="usuario" name="usuario" required>
                
                <label for="contrasenia">Contrasenia</label>
                <input type="password" id="contrasenia" name="contrasenia" required>
                
                <button type="submit">Iniciar sesion</button>
            </form>

            <p class="status">Servidor web listo para conexiones.</p>
        </div>
    </div>
</body>
</html>