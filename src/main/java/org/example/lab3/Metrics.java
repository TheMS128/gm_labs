package org.example.lab3;

import java.util.List;

public class Metrics {

    public static double mse(KnnRegression model, List<Point> points) {
        return points.stream()
                .mapToDouble(model::error)
                .map(e -> e * e)
                .average()
                .orElse(0);
    }

    public static double rmse(KnnRegression model, List<Point> points) {
        return Math.sqrt(mse(model, points));
    }
}
