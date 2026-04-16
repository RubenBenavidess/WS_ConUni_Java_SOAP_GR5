package ec.edu.monster.modelo;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class TipoConversionTest {

    @Test
    void debeExponerNombreVisibleYToString() {
        assertEquals("Longitud", TipoConversion.LONGITUD.getNombreVisible());
        assertEquals("Longitud", TipoConversion.LONGITUD.toString());
        assertEquals("Masa", TipoConversion.MASA.getNombreVisible());
        assertEquals("Temperatura", TipoConversion.TEMPERATURA.getNombreVisible());
    }

    @Test
    void debeIncluirRankineEnTemperatura() {
        assertArrayEquals(
                new String[]{"CELSIUS", "FAHRENHEIT", "KELVIN", "RANKINE"},
                TipoConversion.TEMPERATURA.getUnidadesDisponibles()
        );
    }

    @Test
    void debeRetornarCopiaDefensivaDeUnidades() {
        String[] unidades = TipoConversion.TEMPERATURA.getUnidadesDisponibles();
        unidades[0] = "OTRA";

        assertEquals("CELSIUS", TipoConversion.TEMPERATURA.getUnidadesDisponibles()[0]);
    }
}
