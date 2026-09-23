package org.example.lab1;

import java.util.List;

import static java.lang.IO.println;

public class LinearRegression {

    private final double alpha;
    private final int iterations;
    private double w0 = 0, w1 = 1;

    public LinearRegression(double alpha, int iterations) {
        this.alpha = alpha;
        this.iterations = iterations;
    }

    public double w0() {
        return w0;
    }

    public double w1() {
        return w1;
    }

    public double predict(double x) {
        return w0 + w1 * x;
    }

    public double error(Point p) {
        return predict(p.x()) - p.y();
    }

    public void fit(List<Point> train) {
        int m = train.size();
        for (int it = 1; it <= iterations; it++) {
            double dw0 = 2.0 / m * train.stream()
                    .mapToDouble(this::error)
                    .sum();
            double dw1 = 2.0 / m * train.stream()
                    .mapToDouble(p -> p.x() * error(p))
                    .sum();

            w0 -= alpha * dw0;
            w1 -= alpha * dw1;

            if (it == 1 || it % (iterations / 10) == 0) {
                println(String.format("итерация %5d:  w0 = %.10f,  w1 = %.10f,  MSE = %.10f",
                        it, w0, w1, Metrics.mse(this, train)));
            }
        }
    }
}
