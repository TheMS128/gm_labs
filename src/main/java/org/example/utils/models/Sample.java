package org.example.utils.models;

import java.util.List;

public record Sample<T>(List<T> train, List<T> test) {
}
