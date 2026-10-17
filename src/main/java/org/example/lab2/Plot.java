package org.example.lab2;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.stream.Stream;

import static java.awt.Color.*;
import static java.awt.image.BufferedImage.TYPE_INT_RGB;

public class Plot {

    private static final int SIZE = 1000;
    private static final int PAD = 50;

    private static double xMin, xMax, yMin, yMax;

    public static void save(List<LabeledPoint> train, List<LabeledPoint> test,
                            LogisticRegression model, File file) throws IOException {
        xMin = Stream.concat(train.stream(), test.stream())
                .mapToDouble(LabeledPoint::x)
                .min()
                .orElseThrow();
        xMax = Stream.concat(train.stream(), test.stream())
                .mapToDouble(LabeledPoint::x)
                .max()
                .orElseThrow();
        yMin = Stream.concat(train.stream(), test.stream())
                .mapToDouble(LabeledPoint::y)
                .min()
                .orElseThrow();
        yMax = Stream.concat(train.stream(), test.stream())
                .mapToDouble(LabeledPoint::y)
                .max()
                .orElseThrow();

        var img = new BufferedImage(SIZE, SIZE, TYPE_INT_RGB);
        var g = img.createGraphics();
        g.setColor(WHITE);
        g.fillRect(0, 0, SIZE, SIZE);

        g.setColor(BLACK);
        g.drawRect(PAD, PAD, SIZE - 2 * PAD, SIZE - 2 * PAD);
        g.drawString(String.format("x = %.1f", xMin), PAD, SIZE - PAD + 20);
        g.drawString(String.format("x = %.1f", xMax), SIZE - PAD - 50, SIZE - PAD + 20);
        g.drawString(String.format("y = %.1f", yMin), 5, SIZE - PAD);
        g.drawString(String.format("y = %.1f", yMax), 5, PAD + 10);

        g.setStroke(new BasicStroke(2));
        drawPoints(g, train, BLUE);
        drawPoints(g, test, ORANGE);

        g.setColor(RED);
        g.drawLine(px(xMin), py(model.lineY(xMin)), px(xMax), py(model.lineY(xMax)));

        g.setColor(BLUE);
        g.drawString("обучающая выборка", PAD + 10, PAD + 20);
        g.setColor(ORANGE);
        g.drawString("тестовая выборка", PAD + 10, PAD + 40);
        g.setColor(RED);
        g.drawString("разделяющая линия", PAD + 10, PAD + 60);
        g.setColor(BLACK);
        drawMark(g, PAD + 15, PAD + 76, 1);
        g.drawString("k = 1", PAD + 30, PAD + 80);
        drawMark(g, PAD + 15, PAD + 96, 0);
        g.drawString("k = 0", PAD + 30, PAD + 100);

        ImageIO.write(img, "png", file);
    }

    private static void drawPoints(Graphics2D g, List<LabeledPoint> points, Color color) {
        g.setColor(color);
        points.forEach(p -> drawMark(g, px(p.x()), py(p.y()), p.k()));
    }

    private static void drawMark(Graphics2D g, int cx, int cy, int k) {
        if (k == 1) {
            g.drawOval(cx - 4, cy - 4, 8, 8);
        } else {
            g.drawLine(cx - 4, cy - 4, cx + 4, cy + 4);
            g.drawLine(cx - 4, cy + 4, cx + 4, cy - 4);
        }
    }

    private static int px(double x) {
        return (int) (PAD + (x - xMin) / (xMax - xMin) * (SIZE - 2 * PAD));
    }

    private static int py(double y) {
        return (int) (SIZE - PAD - (y - yMin) / (yMax - yMin) * (SIZE - 2 * PAD));
    }
}
