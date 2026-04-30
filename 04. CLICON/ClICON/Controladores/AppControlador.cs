using System;
using System.ServiceModel;
using System.Threading.Tasks;
using ClICON.Modelos;
using ClICON.Vistas;

namespace ClICON.Controladores
{
    internal class AppControlador
    {
        private readonly ConversorModelo _modelo;
        private readonly ConsolaVista _vista;

        public AppControlador(ConversorModelo modelo, ConsolaVista vista)
        {
            _modelo = modelo;
            _vista = vista;
        }

        public async Task IniciarAppAsync()
        {
            _vista.MensajeBienvenida();
            _vista.MostrarMensaje("=== SISTEMA DE CONVERSION DE UNIDADES - CLIENTE CONSOLA GR5 ===");

            bool autenticado = false;
            while (!autenticado)
            {
                string usuario = _vista.PedirDato("Usuario");
                string contrasenia = _vista.PedirDato("Contraseña");

                autenticado = await _modelo.IniciarSesionAsync(usuario, contrasenia);

                if (autenticado)
                    _vista.MostrarExito("Login exitoso. Token obtenido.");
                else
                    _vista.MostrarError("Credenciales incorrectas o servidor no disponible.");
            }

            bool salir = false;
            while (!salir)
            {
                string opcion = _vista.MostrarMenuPrincipal();

                switch (opcion)
                {
                    case "1":
                        await ProcesarConversionAsync("Longitud", _vista.UNIDADES_LONGITUD);
                        break;
                    case "2":
                        await ProcesarConversionAsync("Masa", _vista.UNIDADES_MASA);
                        break;
                    case "3":
                        await ProcesarConversionAsync("Temperatura", _vista.UNIDADES_TEMPERATURA);
                        break;
                    case "4":
                        salir = true;
                        _vista.MostrarMensaje("¡Hasta luego!");
                        break;
                    default:
                        _vista.MostrarError("Opción no válida.");
                        break;
                }
            }
        }

        private async Task ProcesarConversionAsync(string tipo, string[] unidadesPermitidas)
        {
            _vista.MostrarMensaje($"\n--- Conversión de {tipo.ToUpper()} ---");

            double valor = _vista.PedirNumero("Ingrese el valor a convertir");

            // Usamos la nueva función de la vista pasándole el array
            string origen = _vista.PedirUnidadDeLista("inicial", unidadesPermitidas);
            string destino = _vista.PedirUnidadDeLista("final", unidadesPermitidas);

            try
            {
                double resultado = 0;

                if (tipo == "Longitud")
                    resultado = await _modelo.ConvertirLongitudAsync(valor, origen, destino);
                else if (tipo == "Masa")
                    resultado = await _modelo.ConvertirMasaAsync(valor, origen, destino);
                else if (tipo == "Temperatura")
                    resultado = await _modelo.ConvertirTemperaturaAsync(valor, origen, destino);

                string mensaje = string.Format("Resultado: {0:F6} {1} = {2:F6} {3}", valor, origen, resultado, destino);
                _vista.MostrarExito(mensaje);
            }
            catch (FaultException ex)
            {
                _vista.MostrarError($"Error del servidor: {ex.Message}");
            }
            catch (Exception ex)
            {
                _vista.MostrarError($"Error inesperado: {ex.Message}");
            }
        }
    }
}
