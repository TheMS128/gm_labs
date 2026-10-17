package org.example.lab3;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static java.awt.Color.*;
import static java.awt.image.BufferedImage.TYPE_INT_RGB;

public class Plot {

    private static final int SIZE = 1000;
    private static final int PAD = 50;

    private static double xMin, xMax, yMin, yMax;

    public static void save(List<Point> train, List<Point> test,
                            KnnRegression model, File file) throws IOException {
        xMin = Stream.concat(train.stream(), test.stream())
                .mapToDouble(Point::x)
                .min()
                .orElseThrow();
        xMax = Stream.concat(train.stream(), test.stream())
                .mapToDouble(Point::x)
                .max()
                .orElseThrow();
        yMin = Stream.concat(train.stream(), test.stream())
                .mapToDouble(Point::y)
                .min()
                .orElseThrow();
        yMax = Stream.concat(train.stream(), test.stream())
                .mapToDouble(Point::y)
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

        drawPoints(g, train, BLUE);
        drawPoints(g, test, ORANGE);

        g.setColor(RED);
        IntStream.range(PAD, SIZE - PAD)
                .forEach(col -> g.drawLine(
                        col, py(model.predict(x(col))),
                        col + 1, py(model.predict(x(col + 1)))));

        g.setColor(BLUE);
        g.drawString("обучающая выборка", PAD + 10, PAD + 20);
        g.setColor(ORANGE);
        g.drawString("тестовая выборка", PAD + 10, PAD + 40);
        g.setColor(RED);
        g.drawString("линия регрессии", PAD + 10, PAD + 60);

        ImageIO.write(img, "png", file);
    }

    private static void drawPoints(Graphics2D g, List<Point> points, Color color) {
        g.setColor(color);
        points.forEach(p -> g.fillOval(px(p.x()) - 4, py(p.y()) - 4, 8, 8));
    }

    private static double x(int col) {
        return xMin + (double) (col - PAD) / (SIZE - 2 * PAD) * (xMax - xMin);
    }

    private static int px(double x) {
        return (int) (PAD + (x - xMin) / (xMax - xMin) * (SIZE - 2 * PAD));
    }

    private static int py(double y) {
        return (int) (SIZE - PAD - (y - yMin) / (yMax - yMin) * (SIZE - 2 * PAD));
    }
}
