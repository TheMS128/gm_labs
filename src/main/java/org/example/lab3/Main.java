package org.example.lab3;

import org.example.Lcg;

import java.io.File;
import java.io.FileDescriptor;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.util.Comparator;
import java.util.List;
import java.util.function.DoubleUnaryOperator;
import java.util.stream.DoubleStream;
import java.util.stream.IntStream;

import static java.lang.IO.println;
import static java.nio.charset.StandardCharsets.UTF_8;

public class Main {

    static final List<DoubleUnaryOperator> FUNCTIONS = List.of(
            x -> x * x,
            x -> -x * x * x,
            x -> Math.exp(x),
            x -> 2 * Math.log(1 + x),
            x -> 3 * Math.sin(x));
    static final List<String> NAMES = List.of(
            "x^2", "-x^3", "e^x", "2ln(1 + x)", "3sin(x)");

    static final int N = 100;
    static final int M = 80;
    static final double X_MIN = 0, X_MAX = 5;
    static final double MU = 0, SIGMA = 0.3;

    static final long SEED = 42;
    static final int K = 9;
    static final double H = 0.2;

    static void main() throws Exception {
        System.setOut(new PrintStream(new FileOutputStream(FileDescriptor.out), true, UTF_8));

        var rng = new Lcg(SEED);

        int number = (int) rng.uniform(1, FUNCTIONS.size() + 1);
        DoubleUnaryOperator f = FUNCTIONS.get(number - 1);

        double step = (X_MAX - X_MIN) / (N - 1);
        List<Point> points = IntStream.range(0, N)
                .mapToDouble(i -> X_MIN + i * step)
                .mapToObj(x -> new Point(x, f.applyAsDouble(x) + rng.normal(MU, SIGMA)))
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

        var model = new KnnRegression(K, H);
        model.fit(train);

        println(String.format("N = %d,  f(x) = %s", number, NAMES.get(number - 1)));
        println(String.format("Модель: %dNN, ширина окна h = %.2f", K, H));
        println(String.format("RMSE на обучающей = %.10f", Metrics.rmse(model, train)));
        println(String.format("RMSE на тестовой  = %.10f", Metrics.rmse(model, test)));

        Plot.save(train, test, model, new File("knn.png"));
    }
}
