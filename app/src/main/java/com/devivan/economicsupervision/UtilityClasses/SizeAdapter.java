package com.devivan.economicsupervision.UtilityClasses;

import android.content.Context;
import android.util.DisplayMetrics;
import android.widget.Button;
import android.widget.ImageView;

import androidx.constraintlayout.widget.ConstraintLayout;

import com.airbnb.lottie.LottieAnimationView;
import com.devivan.economicsupervision.System.System;

import java.util.ArrayList;

public class SizeAdapter {

    public static float getPx(Context context, float dp) {
        float density = context.getResources().getDisplayMetrics().density;
        return dp * density;
    }

    public static float getDp(Context context, float px) {
        float density = context.getResources().getDisplayMetrics().density;
        return px / density;
    }

    public static void changeHW(float h, float w, LottieAnimationView... lottieAnimationViews) {
        for (LottieAnimationView lottieAnimationView : lottieAnimationViews) {
            ConstraintLayout.LayoutParams layoutParams = (ConstraintLayout.LayoutParams) lottieAnimationView.getLayoutParams();
            layoutParams.height = (int) h;
            layoutParams.width = (int) w;
            lottieAnimationView.setLayoutParams(layoutParams);
        }
    }

    public static void changeHW(float h, float w, ImageView... imageViews) {
        for (ImageView imageView : imageViews) {
            ConstraintLayout.LayoutParams layoutParams = (ConstraintLayout.LayoutParams) imageView.getLayoutParams();
            layoutParams.height = (int) h;
            layoutParams.width = (int) w;
            imageView.setLayoutParams(layoutParams);
        }
    }

    public static void changeHW(Context context, ArrayList<Button> buttons) {
        float hw = getHW(context, 10);
        for (Button button : buttons) {
            ConstraintLayout.LayoutParams layoutParams = (ConstraintLayout.LayoutParams) button.getLayoutParams();
            layoutParams.height = (int) hw;
            layoutParams.width = (int) hw;
            button.setLayoutParams(layoutParams);
        }
    }

    private static float getHW(Context context, int cols) {
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        float dpWidth = displayMetrics.widthPixels / displayMetrics.density;

        float wDp = 11 * 5;

        float _w_ = dpWidth - wDp;

        float w = _w_ / cols;

        w = getPx(context, w);

        return w;
    }
}
