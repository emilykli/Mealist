package com.example.mealist.Access;

import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Shader;
import android.text.TextPaint;
import android.widget.TextView;

public class TextGradient {
    public static final String blue = "#ff2856f5"; // lygmy blue
    public static final String purple = "#ff2d0e6b"; // lygmy purple

    public static final int[] COLOR_GRADIENT = {Color.parseColor(blue), Color.parseColor(purple)};

    public static void setGradient(TextView textView) {
        TextPaint paint = textView.getPaint();
        float width = paint.measureText(textView.getText().toString());

        Shader textShader = new LinearGradient(0, 0, width, textView.getTextSize(),
                COLOR_GRADIENT, null, Shader.TileMode.CLAMP);

        textView.getPaint().setShader(textShader);
    }
}
