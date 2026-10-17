package org.example.lab2;

import org.example.Lcg;

import java.io.File;
import java.io.FileDescriptor;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.util.Comparator;
import java.util.List;
import java.util.stream.DoubleStream;
import java.util.stream.IntStream;

import static java.lang.IO.println;
import static java.nio.charset.StandardCharsets.UTF_8;

public class Main {

    static final int N = 100;
    static final int M = 80;
    static final double AB_MIN = 1, AB_MAX = 10;
    static final double X_MIN = 0, X_MAX = 5;
    static final double MU = 0, SIGMA = 0.3;

    static final long SEED = 42;
    static final double ALPHA = 0.0001;
    static final int ITERATIONS = 1_000_000;

    static void main() throws Exception {
        System.setOut(new PrintStream(new FileOutputStream(FileDescriptor.out), true, UTF_8));

        var rng = new Lcg(SEED);

        double a = rng.uniform(AB_MIN, AB_MAX);
        double b = rng.uniform(AB_MIN, AB_MAX);

        double step = (X_MAX - X_MIN) / (N - 1);
        List<LabeledPoint> points = IntStream.range(0, N)
                .mapToDouble(i -> X_MIN + i * step)
                .mapToObj(x -> {
                    int k = (int) rng.uniform(0, 2);
                    double y = a * x + b + Math.pow(-1, k + 1) + rng.normal(MU, SIGMA);
                    return new LabeledPoint(x, y, k);
                })
                .toList();

        List<Double> lot = DoubleStream.generate(rng::uniform)
                .limit(N)
                .boxed()
                .toList();
        List<LabeledPoint> shuffled = IntStream.range(0, N)
                .boxed()
                .sorted(Comparator.comparingDouble(lot::get))
                .map(points::get)
                .toList();
        List<LabeledPoint> train = shuffled.subList(0, M);
        List<LabeledPoint> test = shuffled.subList(M, N);

        var model = new LogisticRegression(ALPHA, ITERATIONS);
        model.fit(train);

        double trainBce = Metrics.bce(model, train);
        double testAccuracy = Metrics.accuracy(model, test);

        println("");
        println(String.format("Истинные: a = %.6f,  b = %.6f", a, b));
        println(String.format("Модель:   w0 = %.6f  w1 = %.6f  w2 = %.6f", model.w0(), model.w1(), model.w2()));
        println(String.format("Линия:    y = %.6f·x %+.6f", -model.w1() / model.w2(), -model.w0() / model.w2()));
        println(String.format("BCE на обучающей = %.6f", trainBce));
        println(String.format("Accuracy на тестовой = %.2f", testAccuracy));

        Plot.save(train, test, model, new File("classification.png"));
    }
}
