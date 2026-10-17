package org.example.lab4;

import org.example.Lcg;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class DecisionTree {

    private final int maxDepth;
    private final Lcg rng;
    private Node root;

    public DecisionTree(int maxDepth, Lcg rng) {
        this.maxDepth = maxDepth;
        this.rng = rng;
    }

    public void fit(List<Point> train) {
        root = build(train, 0);
    }

    public double predict(double x) {
        return predict(root, x);
    }

    public static double mean(List<Point> points) {
        return points.stream()
                .mapToDouble(Point::y)
                .average()
                .orElse(0);
    }

    public static double informativeness(List<Point> points) {
        double mean = mean(points);
        return points.stream()
                .mapToDouble(p -> (p.y() - mean) * (p.y() - mean))
                .average()
                .orElse(0);
    }

    public static double gain(List<Point> points, Split split) {
        return points.size() * informativeness(points)
                - split.left().size() * informativeness(split.left())
                - split.right().size() * informativeness(split.right());
    }

    private boolean stop(List<Point> points, int depth) {
        return depth == maxDepth || points.size() < 2;
    }

    private Split randomSplit(List<Point> points) {
        Map<Boolean, List<Point>> parts = points.stream()
                .collect(Collectors.partitioningBy(p -> rng.uniform() < 0.5));
        return new Split(parts.get(true), parts.get(false));
    }

    private Split bestSplit(List<Point> points) {
        return Stream.generate(() -> randomSplit(points))
                .filter(Split::isProper)
                .limit(points.size())
                .max(Comparator.comparingDouble(split -> gain(points, split)))
                .orElseThrow();
    }

    private Node build(List<Point> points, int depth) {
        if (stop(points, depth)) {
            return new Node(points, null, null);
        }
        Split split = bestSplit(points);
        return new Node(points, build(split.left(), depth + 1), build(split.right(), depth + 1));
    }

    private static Point nearest(List<Point> points, double x) {
        return points.stream()
                .min(Comparator.comparingDouble(p -> Math.abs(p.x() - x)))
                .orElseThrow();
    }

    private double predict(Node node, double x) {
        if (node.isLeaf()) {
            return mean(node.points());
        }
        Point nearest = nearest(node.points(), x);
        Node next = node.left().points().contains(nearest) ? node.left() : node.right();
        return predict(next, x);
    }
}
