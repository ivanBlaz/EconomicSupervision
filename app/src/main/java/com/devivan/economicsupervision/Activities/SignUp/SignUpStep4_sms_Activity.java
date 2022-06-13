package com.devivan.economicsupervision.Activities.SignUp;

import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.airbnb.lottie.LottieAnimationView;
import com.devivan.economicsupervision.System.System;
import com.devivan.economicsupervision.R;


public class SignUpStep4_sms_Activity extends AppCompatActivity {

    System system;

    SignUpStep4_sms_Activity __this__;

    public ConstraintLayout clActivitySignUpStep4Sms;

    public TextView txtvWaitingToSMS;

    public TextView txtvVerificationCode;

    public TextView txtvResendVerificationCode;

    public LottieAnimationView lottieLoading, lottieSms, lottieNext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up_step4_sms);

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