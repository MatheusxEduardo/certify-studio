package io.github.matheusxeduardo.certifystudio.pdfbox;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.GradientPaint;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.Ellipse2D;
import java.awt.image.BufferedImage;

public final class CertificateBackgroundRenderer {

    private static final Color LIGHT_BACKGROUND = new Color(248, 250, 252);
    private static final Color LIGHT_BLUE = new Color(224, 242, 254);
    private static final Color DARK_BLUE = new Color(15, 76, 129);

    private CertificateBackgroundRenderer() {
    }

    public static BufferedImage render(int width, int height) {
        BufferedImage image = new BufferedImage(
                width,
                height,
                BufferedImage.TYPE_INT_RGB
        );

        Graphics2D graphics = image.createGraphics();

        try {
            configureRendering(graphics);
            drawBaseGradient(graphics, width, height);
            drawDecorativeShapes(graphics, width, height);

            return image;
        } finally {
            graphics.dispose();
        }
    }

    private static void configureRendering(Graphics2D graphics) {
        graphics.setRenderingHint(
                RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON
        );

        graphics.setRenderingHint(
                RenderingHints.KEY_RENDERING,
                RenderingHints.VALUE_RENDER_QUALITY
        );
    }

    private static void drawBaseGradient(
            Graphics2D graphics,
            int width,
            int height
    ) {
        GradientPaint gradient = new GradientPaint(
                0,
                0,
                LIGHT_BACKGROUND,
                width,
                height,
                LIGHT_BLUE
        );

        graphics.setPaint(gradient);
        graphics.fillRect(0, 0, width, height);
    }

    private static void drawDecorativeShapes(
            Graphics2D graphics,
            int width,
            int height
    ) {
        graphics.setComposite(
                AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.08f)
        );

        graphics.setColor(DARK_BLUE);

        graphics.fill(new Ellipse2D.Double(
                -width * 0.12,
                -height * 0.20,
                width * 0.42,
                width * 0.42
        ));

        graphics.fill(new Ellipse2D.Double(
                width * 0.78,
                height * 0.58,
                width * 0.38,
                width * 0.38
        ));

        graphics.setComposite(AlphaComposite.SrcOver);
    }
}