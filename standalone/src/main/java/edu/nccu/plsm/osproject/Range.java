package edu.nccu.plsm.osproject;

import java.security.SecureRandom;
import java.util.Random;

public class Range {

    private static final Random random = new SecureRandom();
    private int max;
    private int min;

    public Range(long min, long max) {
        if (min > max || min > Integer.MAX_VALUE || max > Integer.MAX_VALUE) {
            throw new IllegalArgumentException();
        }
        this.max = (int) max;
        this.min = (int) min;
    }

    public synchronized int getMax() {
        return max;
    }

    public synchronized int getMin() {
        return min;
    }

    public synchronized void setRange(int max, int min) {
        if (max < min) {
            throw new IllegalArgumentException();
        }
        this.max = max;
        this.min = min;
    }

    public synchronized int rand() {
        if (max == min) {
            return max;
        }
        return random.nextInt((max - min) + 1) + min;
    }

}