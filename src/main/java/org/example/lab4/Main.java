package org.example.lab4;

import org.example.utils.Console;
import org.example.utils.Dataset;
import org.example.utils.Lcg;
import org.example.utils.Metrics;
import org.example.utils.Plot;
import org.example.utils.TargetFunction;
import org.example.utils.models.Point;
import org.example.utils.models.Sample;

import java.io.File;
import java.util.List;

import static java.lang.IO.println;

public class Main {

    static final int N = 100;
    static final int M = 80;
    static final double X_MIN = 0, X_MAX = 5;
    static final double MU = 0, SIGMA = 0.3;

    static final long SEED = 42;
    static final int TREES = 40;
    static final int MAX_DEPTH = 2;
    static final double ETA = 0.5;

    static void main() {
        Console.useUtf8();
        var rng = new Lcg(SEED);

        int number = (int) rng.uniform(1, TargetFunction.values().length + 1);
        TargetFunction f = TargetFunction.values()[number - 1];
        List<Point> points = Dataset.points(N, X_MIN, X_MAX, x -> f.applyAsDouble(x) + rng.normal(MU, SIGMA));
        Sample<Point> sample = Dataset.split(points, M, rng);

        println("N = %d,  f(x) = %s".formatted(number, f.formula()));
        println("Модель: деревьев %d, глубина %d, темп обучения %.2f".formatted(TREES, MAX_DEPTH, ETA));

        var model = new GradientBoosting(TREES, MAX_DEPTH, ETA, rng);
        model.fit(sample.train());

        println("RMSE на обучающей = %.10f".formatted(Metrics.rmse(model::predict, sample.train())));
        println("RMSE на тестовой  = %.10f".formatted(Metrics.rmse(model::predict, sample.test())));

        Plot.regression(sample.train(), sample.test(), model::predict, new File("boosting.png"));
    }
}
