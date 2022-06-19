package com.devivan.economicsupervision.Activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.widget.TextViewOnReceiveContentListener;

import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.airbnb.lottie.LottieAnimationView;
import com.devivan.economicsupervision.R;
import com.devivan.economicsupervision.System.System;

public class LogInOrSignUpActivity extends AppCompatActivity {

    System system;

    LogInOrSignUpActivity __this__;

    // Authentication
    public LottieAnimationView lottieAuthentication;
    public TextView txtvAuthentication;

    // Log In
    public TextView txtvLogIn;
    public ImageView btnLogInO;
    public LottieAnimationView lottieLogIn;
    public View dividerLogIn;
    public TextView txtvInfoLogIn;

    // Separator
    public View dividerSeparator;

    // Sign Up
    public TextView txtvSignUp;
    public ImageView btnSignUpO;
    public LottieAnimationView lottieSignUp;
    public View dividerSignUp;
    public View dividerSignUp1;
    public TextView txtvInfoSignUp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_in_or_sign_up);

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
        //super.onBackPressed();
    }
}