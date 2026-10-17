package org.example.lab4;

import java.util.List;

public class Metrics {

    public static double mse(GradientBoosting model, List<Point> points) {
        return points.stream()
                .mapToDouble(model::error)
                .map(e -> e * e)
                .average()
                .orElse(0);
    }

    public static double rmse(GradientBoosting model, List<Point> points) {
        return Math.sqrt(mse(model, points));
    }
}
