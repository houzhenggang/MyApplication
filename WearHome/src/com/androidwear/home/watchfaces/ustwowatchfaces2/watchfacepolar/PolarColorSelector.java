package com.androidwear.home.watchfaces.ustwowatchfaces2.watchfacepolar;

import android.graphics.Color;
import java.util.*;

public class PolarColorSelector {

    public static PolarColorCombination getNextColorCombination() {
        if (mCombos.size() == 0) {
            populateCombos();
        }
        PolarColorCombination polarcolorcombination;
        for (polarcolorcombination = (PolarColorCombination) mCombos
                .get(mRandom.nextInt(mCombos.size())); mLastCombo != null
                && mLastCombo.getFaceColor() == polarcolorcombination
                        .getFaceColor()
                && mLastCombo.getRingColor() == polarcolorcombination
                        .getRingColor(); polarcolorcombination = (PolarColorCombination) mCombos
                .get(mRandom.nextInt(mCombos.size()))) {
        }
        mCombos.remove(polarcolorcombination);
        return polarcolorcombination;
    }

    private static void populateCombos() {
        mCombos.clear();
        int i = Color.argb(255, 103, 68, 12);
        int j = Color.argb(255, 255, 148, 0);
        mCombos.add(new PolarColorCombination(i, j));
        mCombos.add(new PolarColorCombination(j, i));
        int k = Color.argb(255, 190, 46, 162);
        int l = Color.argb(255, 255, 194, 93);
        mCombos.add(new PolarColorCombination(k, l));
        mCombos.add(new PolarColorCombination(l, k));
        int i1 = Color.argb(255, 173, 231, 137);
        int j1 = Color.argb(255, 23, 129, 123);
        mCombos.add(new PolarColorCombination(i1, j1));
        mCombos.add(new PolarColorCombination(j1, i1));
        int k1 = Color.argb(255, 249, 92, 47);
        int l1 = Color.argb(255, 255, 237, 202);
        mCombos.add(new PolarColorCombination(k1, l1));
        mCombos.add(new PolarColorCombination(l1, k1));
        int i2 = Color.argb(255, 255, 147, 219);
        int j2 = Color.argb(255, 37, 11, 153);
        mCombos.add(new PolarColorCombination(i2, j2));
        mCombos.add(new PolarColorCombination(j2, i2));
        int k2 = Color.argb(255, 0, 82, 226);
        int l2 = Color.argb(255, 240, 247, 98);
        mCombos.add(new PolarColorCombination(k2, l2));
        mCombos.add(new PolarColorCombination(l2, k2));
        int i3 = Color.argb(255, 194, 245, 255);
        int j3 = Color.argb(255, 133, 48, 255);
        mCombos.add(new PolarColorCombination(i3, j3));
        mCombos.add(new PolarColorCombination(j3, i3));
        int k3 = Color.argb(255, 65, 200, 94);
        int l3 = Color.argb(255, 193, 255, 250);
        mCombos.add(new PolarColorCombination(k3, l3));
        mCombos.add(new PolarColorCombination(l3, k3));
        int i4 = Color.argb(255, 61, 42, 140);
        int j4 = Color.argb(255, 250, 100, 221);
        mCombos.add(new PolarColorCombination(i4, j4));
        mCombos.add(new PolarColorCombination(j4, i4));
        int k4 = Color.argb(255, 103, 48, 139);
        int l4 = Color.argb(255, 100, 250, 203);
        mCombos.add(new PolarColorCombination(k4, l4));
        mCombos.add(new PolarColorCombination(l4, k4));
        int i5 = Color.argb(255, 235, 57, 57);
        int j5 = Color.argb(255, 255, 195, 147);
        mCombos.add(new PolarColorCombination(i5, j5));
        mCombos.add(new PolarColorCombination(j5, i5));
        int k5 = Color.argb(255, 83, 88, 109);
        int l5 = Color.argb(255, 192, 240, 53);
        mCombos.add(new PolarColorCombination(k5, l5));
        mCombos.add(new PolarColorCombination(l5, k5));
        int i6 = Color.argb(255, 174, 67, 126);
        int j6 = Color.argb(255, 195, 209, 157);
        mCombos.add(new PolarColorCombination(i6, j6));
        mCombos.add(new PolarColorCombination(j6, i6));
        int k6 = Color.argb(255, 156, 199, 162);
        int l6 = Color.argb(255, 107, 60, 141);
        mCombos.add(new PolarColorCombination(k6, l6));
        mCombos.add(new PolarColorCombination(l6, k6));
        int i7 = Color.argb(255, 246, 246, 246);
        int j7 = Color.argb(255, 3, 206, 161);
        mCombos.add(new PolarColorCombination(i7, j7));
        mCombos.add(new PolarColorCombination(j7, i7));
        int k7 = Color.argb(255, 176, 178, 238);
        int l7 = Color.argb(255, 200, 255, 255);
        mCombos.add(new PolarColorCombination(k7, l7));
        mCombos.add(new PolarColorCombination(l7, k7));
        int i8 = Color.argb(255, 206, 247, 215);
        int j8 = Color.argb(255, 137, 118, 88);
        mCombos.add(new PolarColorCombination(i8, j8));
        mCombos.add(new PolarColorCombination(j8, i8));
        int k8 = Color.argb(255, 212, 212, 228);
        int l8 = Color.argb(255, 255, 105, 105);
        mCombos.add(new PolarColorCombination(k8, l8));
        mCombos.add(new PolarColorCombination(l8, k8));
        int i9 = Color.argb(255, 238, 236, 215);
        int j9 = Color.argb(255, 69, 193, 215);
        mCombos.add(new PolarColorCombination(i9, j9));
        mCombos.add(new PolarColorCombination(j9, i9));
        int k9 = Color.argb(255, 16, 126, 202);
        int l9 = Color.argb(255, 248, 199, 234);
        mCombos.add(new PolarColorCombination(k9, l9));
        mCombos.add(new PolarColorCombination(l9, k9));
        int i10 = Color.argb(255, 16, 126, 202);
        int j10 = Color.argb(255, 248, 199, 234);
        mCombos.add(new PolarColorCombination(i10, j10));
        mCombos.add(new PolarColorCombination(j10, i10));
        int k10 = Color.argb(255, 124, 209, 211);
        int l10 = Color.argb(255, 226, 253, 89);
        mCombos.add(new PolarColorCombination(k10, l10));
        mCombos.add(new PolarColorCombination(l10, k10));
        int i11 = Color.argb(255, 124, 209, 211);
        int j11 = Color.argb(255, 226, 253, 89);
        mCombos.add(new PolarColorCombination(i11, j11));
        mCombos.add(new PolarColorCombination(j11, i11));
    }

    private static List<PolarColorCombination> mCombos = new ArrayList<PolarColorCombination>();
    private static PolarColorCombination mLastCombo = null;
    private static Random mRandom = new Random();

}
