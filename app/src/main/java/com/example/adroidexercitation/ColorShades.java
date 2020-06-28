package com.example.adroidexercitation;

import android.graphics.Color;

public class ColorShades {
    private int mFromColor;
    private int mToColor;
    private float mShade;

    public ColorShades setFromColor(int fromColor) {
        this.mFromColor = fromColor;
        return this;
    }

    public ColorShades setToColor(int toColor) {
        this.mToColor = toColor;
        return this;
    }

    public ColorShades setFromColor(String fromColor) {

        this.mFromColor = Color.parseColor(fromColor);
        return this;
    }

    public ColorShades setToColor(String toColor) {
        this.mToColor = Color.parseColor(toColor);
        return this;
    }

    public ColorShades forLightShade(int color) {
        setFromColor(Color.WHITE);
        setToColor(color);
        return this;
    }

    public ColorShades forDarkShare(int color) {
        setFromColor(color);
        setToColor(Color.BLACK);
        return this;
    }

    public ColorShades setShade(float shade) {
        this.mShade = shade;
        return this;
    }

    public int generate() {
        int fromR = Color.red(mFromColor);
        int fromG = Color.green(mFromColor);
        int fromB = Color.blue(mFromColor);

        int toR = Color.red(mToColor);
        int toG = Color.green(mToColor);
        int toB = Color.blue(mToColor);

        int diffR = toR - fromR;
        int diffG = toG - fromG;
        int diffB = toB - fromB;

        int red = fromR + (int) ((diffR * mShade));
        int green = fromG + (int) ((diffG * mShade));
        int blue = fromB + (int) ((diffB * mShade));

        return Color.rgb(red, green, blue);
    }

    public int generateInverted() {
        int fromR = Color.red(mFromColor);
        int fromG = Color.green(mFromColor);
        int fromB = Color.blue(mFromColor);

        int toR = Color.red(mToColor);
        int toG = Color.green(mToColor);
        int toB = Color.blue(mToColor);

        int diffR = toR - fromR;
        int diffG = toG - fromG;
        int diffB = toB - fromB;

        int red = toR - (int) ((diffR * mShade));
        int green = toG - (int) ((diffG * mShade));
        int blue = toB - (int) ((diffB * mShade));
        return Color.rgb(red, green, blue);
    }

    public String generateInvertedString() {
        return String.format("#%06X", 0xFFFFFF & generateInverted());
    }

    public String generateString() {
        return String.format("#%06X", 0xFFFFFF & generate());
    }
}
