package com.example.yixiangding.hw9;

/**
 * Created by yixiangding on 11/29/17.
 */

public class Formatter {
    public static double formatter(double num, int digits) {
        double factor = Math.pow(10, digits);
        return ((int) (num * factor)) / factor;
    }
}
