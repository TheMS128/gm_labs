package org.example.lab4.models;

import org.example.utils.models.Point;

import java.util.List;

public record Node(List<Point> points, Node left, Node right) {

    public boolean isLeaf() {
        return left == null;
    }
}
