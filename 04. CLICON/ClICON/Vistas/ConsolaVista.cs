using System;
using System.Collections.Generic;
using System.Text;

namespace ClICON.Vistas
{
    internal class ConsolaVista
    {
        public readonly String[] UNIDADES_LONGITUD = {
            "MILIMETRO", "CENTIMETRO", "METRO", "KILOMETRO", "YARDA"
        };

        public readonly String[] UNIDADES_MASA = {
            "MILIGRAMO", "GRAMO", "KILOGRAMO", "TONELADA", "ONZA"
        };

        public readonly String[] UNIDADES_TEMPERATURA = {
            "CELSIUS", "FAHRENHEIT", "KELVIN", "RANKINE"
        };

        public void MostrarMensaje(string mensaje) => Console.WriteLine(mensaje);

        public void MostrarError(string mensaje)
        {
            Console.ForegroundColor = ConsoleColor.Red;
            Console.WriteLine($"[ERROR] {mensaje}");
            Console.ResetColor();
        }

        public void MostrarExito(string mensaje)
        {
            Console.ForegroundColor = ConsoleColor.Green;
            Console.WriteLine($"[ÉXITO] {mensaje}");
            Console.ResetColor();
        }

        public string PedirDato(string prompt)
        {
            Console.Write($"{prompt}: ");
            return Console.ReadLine()?.Trim();
        }

        public double PedirNumero(string prompt)
        {
            double numero;
            while (true)
            {
                Console.Write($"{prompt}: ");
                if (double.TryParse(Console.ReadLine(), out numero))
                {
                    return numero;
                }
                MostrarError("Por favor, ingresa un número válido.");
            }
        }

        public string MostrarMenuPrincipal()
        {
            Console.WriteLine("\n=== MENÚ PRINCIPAL ===");
            Console.WriteLine("1. Convertir Longitud");
            Console.WriteLine("2. Convertir Masa");
            Console.WriteLine("3. Convertir Temperatura");
            Console.WriteLine("4. Salir");
            return PedirDato("Seleccione una opción");
        }
        public void MensajeBienvenida()
        {
            Console.ForegroundColor = ConsoleColor.Cyan;

            // El @ le dice a C# que respete todos los espacios y barras invertidas
            Console.WriteLine(@"
╔────────────────────────────────────────────────────────────╗
│    ____            _   _       _     ____  ____  ____      │
│   / ___|___  _ __ | | | |_ __ (_)   / ___||  _ \| ___|     │
│  | |   / _ \| '_ \| | | | '_ \| |  | |  _ | |_) |___ \     │
│  | |__| (_) | | | | |_| | | | | |  | |_| ||  _ < ___) |    │
│   \____\___/|_| |_|\___/|_| |_|_|   \____||_| \_\____/     │
╚────────────────────────────────────────────────────────────╝");

            // Restaura el color normal para el resto del programa
            Console.ResetColor();
        }

        public string PedirUnidadDeLista(string tipo, string[] unidades)
        {
            Console.WriteLine($"\nSeleccione la unidad {tipo}:");
            for (int i = 0; i < unidades.Length; i++)
            {
                Console.WriteLine($"{i + 1}. {unidades[i]}");
            }

            while (true)
            {
                Console.Write("Opción: ");
                if (int.TryParse(Console.ReadLine(), out int opcion) && opcion >= 1 && opcion <= unidades.Length)
                {
                    return unidades[opcion - 1]; // Retorna el string exacto del array
                }
                MostrarError($"Por favor, ingrese un número entre 1 y {unidades.Length}.");
            }
        }

    }
}
