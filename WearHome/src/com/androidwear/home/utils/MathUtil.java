package com.androidwear.home.utils;

/**
 * Provide some math method
 **/
public class MathUtil {

    /**
     * Get the value between min and max.
     */
    public static float clamp(float x, float min, float max) {
        return Math.min(max, Math.max(min, x));
    }

}
