package org.example.lab1;

import org.example.utils.Console;
import org.example.utils.Dataset;
import org.example.utils.Lcg;
import org.example.utils.Metrics;
import org.example.utils.Plot;
import org.example.utils.models.Point;
import org.example.utils.models.Sample;

import java.io.File;
import java.util.List;

import static java.lang.IO.println;

public class Main {

    static final int N = 100;
    static final int M = 80;
    static final double AB_MIN = 1, AB_MAX = 10;
    static final double X_MIN = 0, X_MAX = 5;
    static final double MU = 0, SIGMA = 0.3;

    static final long SEED = 42;
    static final double ALPHA = 0.001;
    static final int ITERATIONS = 1000;

    static void main() {
        Console.useUtf8();
        var rng = new Lcg(SEED);

        double a = rng.uniform(AB_MIN, AB_MAX);
        double b = rng.uniform(AB_MIN, AB_MAX);
        List<Point> points = Dataset.points(N, X_MIN, X_MAX, x -> a * x + b + rng.normal(MU, SIGMA));
        Sample<Point> sample = Dataset.split(points, M, rng);

        var model = new LinearRegression(ALPHA, ITERATIONS);
        model.fit(sample.train());

        println();
        println("Истинные: a = %.10f,  b = %.10f".formatted(a, b));
        println("Модель:   w1 = %.10f  w0 = %.10f".formatted(model.w1(), model.w0()));
        println("MSE на обучающей = %.10f".formatted(Metrics.mse(model::predict, sample.train())));
        println("RMSE на тестовой = %.10f".formatted(Metrics.rmse(model::predict, sample.test())));

        Plot.regression(sample.train(), sample.test(), model::predict, new File("regression.png"));
    }
}
