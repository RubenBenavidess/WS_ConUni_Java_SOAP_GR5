package ec.edu.monster.ws;

import ec.edu.monster.servicios.ServicioConversor;
import ec.edu.monster.utilidades.*;
import ec.edu.monster.utilidades.enums.*;
import ec.edu.monster.mapeadores.UnidadMapper;
import ec.edu.monster.seguridad.AdministradorTokens;

import jakarta.jws.WebService;
import jakarta.jws.WebMethod;
import jakarta.jws.WebParam;
import jakarta.jws.HandlerChain;

@WebService(serviceName = "WSConversorUnidades")
@HandlerChain(file = "manejador.xml")
public class WSConversorUnidades {

    private final ServicioConversor<UnidadLongitud> servicioLongitud =
            new ServicioConversor<>(new ConversorLongitud());

    private final ServicioConversor<UnidadMasa> servicioMasa =
            new ServicioConversor<>(new ConversorMasa());

    private final ServicioConversor<UnidadTemperatura> servicioTemperatura =
            new ServicioConversor<>(new ConversorTemperatura());

    @WebMethod(operationName = "login")
    public String login(
            @WebParam(name = "usuario") String usuario,
            @WebParam(name = "contrasenia") String contrasenia) {
        
        if ("Monster".equals(usuario) && "Monster9".equals(contrasenia)) {
            return AdministradorTokens.generarToken();
        }
        throw new RuntimeException("Credenciales incorrectas");
    }
   
    @WebMethod(operationName = "convertirLongitud")
    public double convertirLongitud(
            @WebParam(name = "valor") double valor,
            @WebParam(name = "unidadInicial") String unidadInicial,
            @WebParam(name = "unidadFinal") String unidadFinal) {

        UnidadLongitud origen = UnidadMapper.toLongitud(unidadInicial);
        UnidadLongitud destino = UnidadMapper.toLongitud(unidadFinal);

        return servicioLongitud.convertir(valor, origen, destino);
    }
    
    @WebMethod(operationName = "convertirMasa")
    public double convertirMasa(
            @WebParam(name = "valor") double valor,
            @WebParam(name = "unidadInicial") String unidadInicial,
            @WebParam(name = "unidadFinal") String unidadFinal) {

        UnidadMasa origen = UnidadMapper.toMasa(unidadInicial);
        UnidadMasa destino = UnidadMapper.toMasa(unidadFinal);

        return servicioMasa.convertir(valor, origen, destino);
    }

    @WebMethod(operationName = "convertirTemperatura")
    public double convertirTemperatura(
            @WebParam(name = "valor") double valor,
            @WebParam(name = "unidadInicial") String unidadInicial,
            @WebParam(name = "unidadFinal") String unidadFinal) {

        UnidadTemperatura origen = UnidadMapper.toTemperatura(unidadInicial);
        UnidadTemperatura destino = UnidadMapper.toTemperatura(unidadFinal);

        return servicioTemperatura.convertir(valor, origen, destino);
    }
}