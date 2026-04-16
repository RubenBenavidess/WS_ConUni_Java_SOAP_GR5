package ec.edu.monster.modelo;

import java.util.Arrays;

public enum TipoConversion {
    LONGITUD("Longitud", new String[]{"MILIMETRO", "CENTIMETRO", "METRO", "KILOMETRO", "YARDA"}),
    MASA("Masa", new String[]{"MILIGRAMO", "GRAMO", "KILOGRAMO", "TONELADA", "ONZA"}),
    TEMPERATURA("Temperatura", new String[]{"CELSIUS", "FAHRENHEIT", "KELVIN", "RANKINE"});

    private final String nombreVisible;
    private final String[] unidadesDisponibles;

    TipoConversion(String nombreVisible, String[] unidadesDisponibles) {
        this.nombreVisible = nombreVisible;
        this.unidadesDisponibles = unidadesDisponibles;
    }

    public String getNombreVisible() {
        return nombreVisible;
    }

    public String[] getUnidadesDisponibles() {
        return Arrays.copyOf(unidadesDisponibles, unidadesDisponibles.length);
    }

    @Override
    public String toString() {
        return nombreVisible;
    }
}
