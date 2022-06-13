package com.devivan.economicsupervision.Activities.SignUp;

import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.airbnb.lottie.LottieAnimationView;
import com.devivan.economicsupervision.R;
import com.devivan.economicsupervision.System.System;

public class SignUpStep1_currency_Activity extends AppCompatActivity {

    System system;

    SignUpStep1_currency_Activity __this__;

    public ConstraintLayout clActivitySignUpStep1Currency;

    public LottieAnimationView btnLogIn;
    public TextView txtvLogIn;
    public ImageView btnLogInO;

    public TextView txtvCurrency;

    public LottieAnimationView lottieNext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up_step1_currency);

        // Initialize __this__
        __this__ = this;

        // Get system
        system = getIntent().getParcelableExtra("system");

        // Prepare activity?
        if (system != null) system.prepareActivity(__this__);
    }

    @Override
    protected void onResume() {
        super.onResume();
        System.hideSystemUI(this);

    }

    @Override
    public void onBackPressed() {
        if (lottieNext.isEnabled()) {
            super.onBackPressed();
            this.overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_right);
        }
    }

    public void onDismiss() {
        system.currency = txtvCurrency.getText().toString().toUpperCase();
    }
}