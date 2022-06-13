package com.devivan.economicsupervision.Activities.LogIn;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.airbnb.lottie.LottieAnimationView;
import com.devivan.economicsupervision.R;
import com.devivan.economicsupervision.System.System;

public class LogInStep1_sms_Activity extends AppCompatActivity {

    System system;

    LogInStep1_sms_Activity __this__;

    public ConstraintLayout clActivityLogInStep1Sms;

    public LottieAnimationView btnSignUp;
    public TextView txtvSignUp;
    public ImageView btnSignUpO;

    public TextView txtvWaitingToSMS;

    public TextView txtvVerificationCode;

    public TextView txtvResendVerificationCode;

    public LottieAnimationView lottieLoading, lottieSms, lottieNext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_in_step1_sms);

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

    public void onDismiss() {
        system.verificationCode = txtvVerificationCode.getText().toString().replace(" ", "").replace("_", "");
    }
}