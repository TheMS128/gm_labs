package org.example.lab3;

import org.example.utils.models.Point;

import java.util.Comparator;
import java.util.List;

import static java.lang.Math.PI;

public class KnnRegression {

    private final int k;
    private final double h;
    private List<Point> train = List.of();

    public KnnRegression(int k, double h) {
        this.k = k;
        this.h = h;
    }

    public void fit(List<Point> train) {
        this.train = train;
    }

    public static double distance(double x1, double x2) {
        return Math.sqrt((x1 - x2) * (x1 - x2));
    }

    public static double kernel(double z) {
        return 1 / Math.sqrt(2 * PI) * Math.exp(-2 * z * z);
    }

    public double weight(double x, Point p) {
        return kernel(distance(x, p.x()) / h);
    }

    public List<Point> neighbors(double x) {
        return train.stream()
                .sorted(Comparator.comparingDouble(p -> distance(x, p.x())))
                .limit(k)
                .toList();
    }

    public double predict(double x) {
        List<Point> nearest = neighbors(x);
        double numerator = nearest.stream()
                .mapToDouble(p -> p.y() * weight(x, p))
                .sum();
        double denominator = nearest.stream()
                .mapToDouble(p -> weight(x, p))
                .sum();
        return numerator / denominator;
    }
}
