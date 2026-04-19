<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@page import="ec.edu.monster.web.ClienteSOAP"%>
<%@page import="java.util.ArrayList"%>
<%@page import="java.util.List"%>
<%
    // 1. Verificamos la seguridad (¿Hay token guardado?)
    String tokenActivo = (String) session.getAttribute("tokenGlobal");
    if (tokenActivo == null || tokenActivo.isEmpty()) {
        response.sendRedirect("index.jsp");
        return;
    }

    // 2. Gestionar el Historial en la sesión
    List<String> historial = (List<String>) session.getAttribute("historialConversiones");
    if (historial == null) {
        historial = new ArrayList<>();
        session.setAttribute("historialConversiones", historial);
    }

    // 3. Procesar conversión
    String resultado = "listo para convertir";
    String val = "";
    String tipo = "";

    if ("POST".equalsIgnoreCase(request.getMethod())) {
        tipo = request.getParameter("tipo");
        val = request.getParameter("valor");
        String ori = request.getParameter("origen");
        String des = request.getParameter("destino");
        
        try {
            // Nota: Debes actualizar tu ClienteSOAP para que reciba el 'tipo' 
            // y decida a qué método del Web Service llamar (convertirLongitud, convertirMasa, etc.)
            String resServicio = ClienteSOAP.convertir(tokenActivo, tipo, val, ori, des);
            resultado = resServicio + " " + des;
            
            // Agregar al historial
            historial.add(val + " " + ori + " = " + resultado);
            
        } catch (Exception e) {
            resultado = "Error: " + e.getMessage();
        }
    }
%>
<!DOCTYPE html>
<html>
<head>
    <title>Conversor Inteligente GR5</title>
    <style>
        * { box-sizing: border-box; font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif; }
        body { 
            background-color: #0b3b5b; /* Azul oscuro del fondo */
            margin: 0; 
            padding: 20px; 
            color: #0d3b5e;
        }
        
        .panel {
            background-color: #eaf1f8; /* Color celeste claro de las tarjetas */
            border-radius: 10px;
            padding: 20px;
            margin-bottom: 20px;
            box-shadow: 0 4px 6px rgba(0,0,0,0.3);
        }

        /* Cabecera */
        .header-container {
            display: flex;
            justify-content: space-between;
            align-items: center;
        }
        .header-info h1 { margin: 0 0 10px 0; font-size: 24px; color: #0d3b5e; }
        .header-info p { margin: 5px 0; font-size: 14px; }
        .header-info .endpoint { font-size: 12px; color: #555; margin-top: 15px; }
        .header-img img { max-height: 100px; border-radius: 8px; }

        /* Columnas principales */
        .main-content {
            display: flex;
            gap: 20px;
        }
        
        .col-izq, .col-der {
            flex: 1;
        }

        /* Formulario */
        label { display: block; font-weight: bold; margin-top: 15px; margin-bottom: 5px; font-size: 14px; }
        select, input[type="number"] { 
            width: 100%; 
            padding: 8px; 
            border: 1px solid #ccc; 
            border-radius: 4px; 
            background-color: #f9f9f9;
        }
        
        .botones {
            display: flex;
            gap: 10px;
            margin-top: 20px;
        }
        button { 
            flex: 1;
            padding: 10px; 
            border: 1px solid #ccc; 
            border-radius: 4px; 
            cursor: pointer; 
            font-weight: bold;
            background-color: #e0e0e0;
            color: #333;
        }
        button:hover { background-color: #d0d0d0; }
        
        .resultado { margin-top: 20px; font-weight: bold; }

        /* Historial */
        .historial-caja {
            background-color: white;
            border: 1px solid #ccc;
            height: 350px;
            padding: 10px;
            overflow-y: auto;
            border-radius: 4px;
            font-family: monospace;
        }
    </style>
</head>
<body>

    <div class="panel header-container">
        <div class="header-info">
            <h1>Conversor inteligente de unidades</h1>
            <p>Longitud, masa y temperatura con seguridad por token</p>
            <div class="endpoint">
                Endpoint: http://localhost:8080/04.%20SERVIDOR/WSConversorUnidades | Token: <%= tokenActivo.substring(0, Math.min(tokenActivo.length(), 15)) %>...
            </div>
        </div>
        <div class="header-img">
            <img src="assets/sullivan_computer.jpg" alt="Monster">
        </div>
    </div>

    <div class="main-content">
        
        <div class="panel col-izq">
            <form method="POST">
                <label>Tipo de conversion</label>
                <select name="tipo" id="tipo" onchange="actualizarUnidades()">
                    <option value="Longitud" <%= "Longitud".equals(tipo) ? "selected" : "" %>>Longitud</option>
                    <option value="Masa" <%= "Masa".equals(tipo) ? "selected" : "" %>>Masa</option>
                    <option value="Temperatura" <%= "Temperatura".equals(tipo) ? "selected" : "" %>>Temperatura</option>
                </select>

                <label>Valor</label>
                <input type="number" step="any" name="valor" value="<%= val %>" required>

                <label>Unidad inicial</label>
                <select name="origen" id="origen"></select>

                <label>Unidad final</label>
                <select name="destino" id="destino"></select>

                <div class="botones">
                    <button type="submit">Convertir</button>
                    <button type="button" onclick="window.location.href='conversor.jsp'">Limpiar</button>
                </div>
            </form>

            <div class="resultado">Resultado: <%= resultado %></div>
        </div>

        <div class="panel col-der">
            <h2 style="margin-top:0; font-size: 18px; color: #0d3b5e;">Historial de conversiones</h2>
            <div class="historial-caja">
                <% for(int i = historial.size() - 1; i >= 0; i--) { %>
                    <div><%= historial.get(i) %></div>
                <% } %>
            </div>
        </div>

    </div>

    <script>
        const unidades = {
            "Longitud": ["MILIMETRO", "CENTIMETRO", "METRO", "KILOMETRO"],
            "Masa": ["MILIGRAMO", "GRAMO", "KILOGRAMO", "ONZA", "LIBRA"],
            "Temperatura": ["CELSIUS", "FAHRENHEIT", "KELVIN"]
        };

        function actualizarUnidades() {
            const tipo = document.getElementById("tipo").value;
            const selectOrigen = document.getElementById("origen");
            const selectDestino = document.getElementById("destino");
            
            // Limpiar opciones actuales
            selectOrigen.innerHTML = "";
            selectDestino.innerHTML = "";

            // Llenar con las nuevas opciones
            unidades[tipo].forEach(unidad => {
                selectOrigen.add(new Option(unidad, unidad));
                selectDestino.add(new Option(unidad, unidad));
            });
        }

        // Ejecutar al cargar la página para poblar los selects
        window.onload = actualizarUnidades;
    </script>
</body>
</html>