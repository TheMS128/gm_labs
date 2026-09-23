package org.example.lab1;

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
    static final double ALPHA = 0.05;
    static final int ITERATIONS = 1000;

    static void main() throws Exception {
        System.setOut(new PrintStream(new FileOutputStream(FileDescriptor.out), true, UTF_8));

        var rng = new Lcg(SEED);

        double a = rng.uniform(AB_MIN, AB_MAX);
        double b = rng.uniform(AB_MIN, AB_MAX);

        double step = (X_MAX - X_MIN) / (N - 1);
        List<Point> points = IntStream.range(0, N)
                .mapToDouble(i -> X_MIN + i * step)
                .mapToObj(x -> new Point(x, a * x + b + rng.normal(MU, SIGMA)))
                .toList();

        List<Double> lot = DoubleStream.generate(rng::uniform)
                .limit(N)
                .boxed()
                .toList();
        List<Point> shuffled = IntStream.range(0, N)
                .boxed()
                .sorted(Comparator.comparingDouble(lot::get))
                .map(points::get)
                .toList();
        List<Point> train = shuffled.subList(0, M);
        List<Point> test = shuffled.subList(M, N);

        var model = new LinearRegression(ALPHA, ITERATIONS);
        model.fit(train);

        double trainMse = Metrics.mse(model, train);
        double testRmse = Metrics.rmse(model, test);

        println("");
        println(String.format("Истинные: a = %.10f,  b = %.10f", a, b));
        println(String.format("Модель:   w1 = %.10f  w0 = %.10f", model.w1(), model.w0()));
        println(String.format("MSE на обучающей = %.10f", trainMse));
        println(String.format("RMSE на тестовой = %.10f", testRmse));

        Plot.save(train, test, model, new File("regression.png"));
    }
}
