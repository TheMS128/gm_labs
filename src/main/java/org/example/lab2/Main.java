package org.example.lab2;

import org.example.utils.Console;
import org.example.utils.Dataset;
import org.example.utils.Lcg;
import org.example.utils.Metrics;
import org.example.utils.Plot;
import org.example.utils.models.LabeledPoint;
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
    static final double ALPHA = 0.0001;
    static final int ITERATIONS = 1_000_000;

    static void main() {
        Console.useUtf8();
        var rng = new Lcg(SEED);

        double a = rng.uniform(AB_MIN, AB_MAX);
        double b = rng.uniform(AB_MIN, AB_MAX);
        List<LabeledPoint> points = Dataset.grid(N, X_MIN, X_MAX)
                .mapToObj(x -> point(x, a, b, rng))
                .toList();
        Sample<LabeledPoint> sample = Dataset.split(points, M, rng);

        var model = new LogisticRegression(ALPHA, ITERATIONS);
        model.fit(sample.train());

        println();
        println("Истинные: a = %.6f,  b = %.6f".formatted(a, b));
        println("Модель:   w0 = %.6f  w1 = %.6f  w2 = %.6f".formatted(model.w0(), model.w1(), model.w2()));
        println("Линия:    y = %.6f·x %+.6f".formatted(-model.w1() / model.w2(), -model.w0() / model.w2()));
        println("BCE на обучающей = %.6f".formatted(Metrics.bce(model::predict, sample.train())));
        println("Accuracy на тестовой = %.2f".formatted(Metrics.accuracy(model::classify, sample.test())));

        Plot.classification(sample.train(), sample.test(), model::lineY, new File("classification.png"));
    }

    private static LabeledPoint point(double x, double a, double b, Lcg rng) {
        int k = (int) rng.uniform(0, 2);
        double y = a * x + b + Math.pow(-1, k + 1) + rng.normal(MU, SIGMA);
        return new LabeledPoint(x, y, k);
    }
}
