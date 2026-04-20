package ec.edu.monster.modelo;

import com.codename1.io.ConnectionRequest;
import com.codename1.io.NetworkManager;
import java.io.IOException;
import java.io.OutputStream;

public class ModeloSOAP {
    private String tokenGlobal = "";
    private String urlWS = "http://192.168.18.9:8080/04_SERVIDOR/WSConversorUnidades";

    public String getTokenGlobal() { return tokenGlobal; }
    public void setTokenGlobal(String tokenGlobal) { this.tokenGlobal = tokenGlobal; }
    
    public String getUrlWS() { return urlWS; }
    public void setUrlWS(String urlWS) { this.urlWS = urlWS; }

    public String enviarPeticionLogin(String usuario, String contrasenia) {
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

    public String enviarPeticionConversor(String tipo, String valor, String origen, String destino) {
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
        req.setFailSilently(true); 

        NetworkManager.getInstance().addToQueueAndWait(req);

        if (req.getResponseCode() == 200) {
            String response = new String(req.getResponseData());
            return extraerDato(response, "<return>", "</return>");
        } else {
            return "Error HTTP: " + req.getResponseCode();
        }
    }

    private String extraerDato(String xml, String tagInicio, String tagFin) {
        int start = xml.indexOf(tagInicio);
        if (start != -1) {
            start += tagInicio.length();
            int end = xml.indexOf(tagFin, start);
            if (end != -1) return xml.substring(start, end);
        }
        return "No se pudo procesar la respuesta";
    }
    
    // Método para diagnosticar la red
    public int probarConexion(String urlPrueba) {
        ConnectionRequest req = new ConnectionRequest();
        req.setUrl(urlPrueba + "?wsdl"); // Añadimos ?wsdl para forzar un GET
        req.setPost(false);
        req.setFailSilently(true);
        
        NetworkManager.getInstance().addToQueueAndWait(req);
        
        return req.getResponseCode();
    }
}