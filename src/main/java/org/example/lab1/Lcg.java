package org.example.lab1;

import static java.lang.Math.PI;

public class Lcg {

    private static final long A = 1103515245, C = 12345, MOD = 2147483648L;

    private long state;

    public Lcg(long seed) {
        this.state = seed;
    }

    public double uniform() {
        state = (A * state + C) % MOD;
        return (double) state / MOD;
    }

    public double uniform(double lo, double hi) {
        return lo + (hi - lo) * uniform();
    }

    public double normal(double mu, double sigma) {
        double u1 = 1 - uniform();
        double u2 = uniform();
        return mu + sigma * Math.sqrt(-2 * Math.log(u1)) * Math.cos(2 * PI * u2);
    }
}
