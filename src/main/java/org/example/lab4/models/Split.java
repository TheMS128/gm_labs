package org.example.lab4.models;

import org.example.utils.models.Point;

import java.util.List;

public record Split(List<Point> left, List<Point> right) {

    public boolean isProper() {
        return !left.isEmpty() && !right.isEmpty();
    }
}
