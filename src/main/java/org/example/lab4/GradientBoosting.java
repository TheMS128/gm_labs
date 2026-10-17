package org.example.lab4;

import org.example.utils.Lcg;
import org.example.utils.Metrics;
import org.example.utils.models.Point;

import java.util.ArrayList;
import java.util.List;

import static java.lang.IO.println;

public class GradientBoosting {

    private final int trees;
    private final int maxDepth;
    private final double eta;
    private final Lcg rng;
    private DecisionTree first;
    private final List<DecisionTree> next = new ArrayList<>();

    public GradientBoosting(int trees, int maxDepth, double eta, Lcg rng) {
        this.trees = trees;
        this.maxDepth = maxDepth;
        this.eta = eta;
        this.rng = rng;
    }

    public double predict(double x) {
        return first.predict(x) + eta * next.stream()
                .mapToDouble(tree -> tree.predict(x))
                .sum();
    }

    public double antigradient(Point p) {
        return 2 * (p.y() - predict(p.x()));
    }

    public void fit(List<Point> train) {
        first = new DecisionTree(maxDepth, rng);
        first.fit(train);

        for (int k = 2; k <= trees; k++) {
            List<Point> gradients = train.stream()
                    .map(p -> new Point(p.x(), antigradient(p)))
                    .toList();
            var tree = new DecisionTree(maxDepth, rng);
            tree.fit(gradients);
            next.add(tree);

            if (k % Math.max(1, trees / 10) == 0) {
                println(String.format("деревьев %5d:  RMSE на обучающей = %.6f",
                        k, Metrics.rmse(this::predict, train)));
            }
        }
    }
}
