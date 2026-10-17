package org.example.lab2;

import java.util.List;

import static java.lang.IO.println;

public class LogisticRegression {

    private final double alpha;
    private final int iterations;
    private double w0 = 0, w1 = 0, w2 = 0;

    public LogisticRegression(double alpha, int iterations) {
        this.alpha = alpha;
        this.iterations = iterations;
    }

    public double w0() {
        return w0;
    }

    public double w1() {
        return w1;
    }

    public double w2() {
        return w2;
    }

    public double linear(LabeledPoint p) {
        return w0 + w1 * p.x() + w2 * p.y();
    }

    public static double sigmoid(double z) {
        return 1 / (1 + Math.exp(-z));
    }

    public double predict(LabeledPoint p) {
        return sigmoid(linear(p));
    }

    public int classify(LabeledPoint p) {
        return predict(p) >= 0.5 ? 1 : 0;
    }

    public double error(LabeledPoint p) {
        return predict(p) - p.k();
    }

    public double lineY(double x) {
        return -(w0 + w1 * x) / w2;
    }

    public void fit(List<LabeledPoint> train) {
        for (int it = 1; it <= iterations; it++) {
            double dw0 = train.stream()
                    .mapToDouble(this::error)
                    .sum();
            double dw1 = train.stream()
                    .mapToDouble(p -> p.x() * error(p))
                    .sum();
            double dw2 = train.stream()
                    .mapToDouble(p -> p.y() * error(p))
                    .sum();

            w0 -= alpha * dw0;
            w1 -= alpha * dw1;
            w2 -= alpha * dw2;

            if (it == 1 || it % (iterations / 10) == 0) {
                println(String.format("итерация %6d:  w0 = %.6f,  w1 = %.6f,  w2 = %.6f,  BCE = %.6f",
                        it, w0, w1, w2, Metrics.bce(this, train)));
            }
        }
    }
}
