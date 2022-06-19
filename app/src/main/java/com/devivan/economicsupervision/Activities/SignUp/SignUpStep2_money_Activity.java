package com.devivan.economicsupervision.Activities.SignUp;

import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.airbnb.lottie.LottieAnimationView;
import com.devivan.economicsupervision.R;
import com.devivan.economicsupervision.System.System;

public class SignUpStep2_money_Activity extends AppCompatActivity {

    System system;

    SignUpStep2_money_Activity __this__;

    public ConstraintLayout clActivitySignUpStep2Money;

    public TextView txtvNumsBef, txtvNumsAft;

    public LottieAnimationView lottieBack, lottieNext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up_step2_money);

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
        system.numsBef = txtvNumsBef.getText().toString();
        system.numsAft = txtvNumsAft.getText().toString();
        try {
            system.money = Double.parseDouble(system.numsBef.replace(".", "") + "." + system.numsAft);
        } catch (NumberFormatException ignored) {
            system.__wait__("outside_the_system.json");
        }
    }
}