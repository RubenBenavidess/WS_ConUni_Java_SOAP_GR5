package ec.edu.monster;

import ec.edu.monster.vista.LoginVista;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

public class Main {

    private Main() {
    }

    public static void main(String[] args) {
        configurarTema();
        SwingUtilities.invokeLater(() -> new LoginVista().setVisible(true));
    }

    private static void configurarTema() {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ex) {
            // Si no se puede aplicar el tema del sistema, se usa el predeterminado.
        }
    }
}
