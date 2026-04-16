package ec.edu.monster.vista.componentes;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.geom.Ellipse2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import javax.imageio.ImageIO;
import javax.swing.JPanel;

public class PanelFondoDegradado extends JPanel {

    private final Color colorSuperior;
    private final Color colorInferior;
    private BufferedImage imagenDecorativa;

    public PanelFondoDegradado(Color colorSuperior, Color colorInferior, String recursoImagen) {
        this.colorSuperior = colorSuperior;
        this.colorInferior = colorInferior;
        this.imagenDecorativa = cargarImagen(recursoImagen);
        setOpaque(true);
    }

    @Override
    protected void paintComponent(Graphics graphics) {
        super.paintComponent(graphics);

        Graphics2D grafico = (Graphics2D) graphics.create();
        grafico.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        GradientPaint degradado = new GradientPaint(
                0,
                0,
                colorSuperior,
                0,
                getHeight(),
                colorInferior
        );
        grafico.setPaint(degradado);
        grafico.fillRect(0, 0, getWidth(), getHeight());

        dibujarFormas(grafico);
        dibujarImagen(grafico);

        grafico.dispose();
    }

    private void dibujarFormas(Graphics2D grafico) {
        grafico.setColor(new Color(255, 255, 255, 26));
        grafico.fill(new Ellipse2D.Double(-90, -70, 280, 240));

        grafico.setColor(new Color(11, 132, 179, 45));
        grafico.fill(new Ellipse2D.Double(getWidth() - 260, 40, 300, 300));

        grafico.setColor(new Color(180, 255, 220, 35));
        grafico.fill(new Ellipse2D.Double(getWidth() - 430, getHeight() - 280, 420, 340));
    }

    private void dibujarImagen(Graphics2D grafico) {
        if (imagenDecorativa == null) {
            return;
        }

        int anchoObjetivo = Math.max(220, getWidth() / 3);
        int altoObjetivo = (int) (anchoObjetivo * (imagenDecorativa.getHeight() / (double) imagenDecorativa.getWidth()));

        Image imagenEscalada = imagenDecorativa.getScaledInstance(anchoObjetivo, altoObjetivo, Image.SCALE_SMOOTH);
        int posicionX = getWidth() - anchoObjetivo - 20;
        int posicionY = getHeight() - altoObjetivo - 10;

        grafico.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.18f));
        grafico.drawImage(imagenEscalada, posicionX, posicionY, null);
        grafico.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1.0f));
    }

    private BufferedImage cargarImagen(String recursoImagen) {
        if (recursoImagen == null || recursoImagen.isBlank()) {
            return null;
        }

        try (var flujo = getClass().getResourceAsStream(recursoImagen)) {
            if (flujo == null) {
                return null;
            }
            return ImageIO.read(flujo);
        } catch (IOException ex) {
            return null;
        }
    }
}
