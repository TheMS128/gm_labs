package org.example.lab2;

import java.util.List;

public class Metrics {

    public static double bce(LogisticRegression model, List<LabeledPoint> points) {
        return points.stream()
                .mapToDouble(p -> p.k() == 1 ? -Math.log(model.predict(p)) : -Math.log(1 - model.predict(p)))
                .sum();
    }

    public static double accuracy(LogisticRegression model, List<LabeledPoint> points) {
        return points.stream()
                .mapToInt(p -> model.classify(p) == p.k() ? 1 : 0)
                .average()
                .orElse(0);
    }
}
