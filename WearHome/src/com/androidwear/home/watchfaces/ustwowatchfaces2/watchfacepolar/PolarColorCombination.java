package com.androidwear.home.watchfaces.ustwowatchfaces2.watchfacepolar;

public class PolarColorCombination {

    public PolarColorCombination(int i, int j) {
        mFaceColor = i;
        mRingColor = j;
    }

    public int getFaceColor() {
        return mFaceColor;
    }

    public int getRingColor() {
        return mRingColor;
    }

    private int mFaceColor;
    private int mRingColor;
}
