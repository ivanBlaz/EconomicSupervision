package com.devivan.economicsupervision.Activities.SignUp;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.airbnb.lottie.LottieAnimationView;
import com.devivan.economicsupervision.System.System;
import com.devivan.economicsupervision.R;
import com.hbb20.CountryCodePicker;

import java.io.File;

public class SignUpStep0_name_Activity extends AppCompatActivity {

    System system;

    SignUpStep0_name_Activity __this__;

    public ConstraintLayout clActivitySignUpStep0Name;

    public TextView txtvName;

    public LottieAnimationView lottieBack, lottieNext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up_step0_name);

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
            this.overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == System.payPalRequestCode) {
            // Data recovery
            if (resultCode == Activity.RESULT_OK) {
                // Disable UI
                lottieNext.setOnClickListener(null);
                txtvName.setOnClickListener(null);
                //////////////////////////////////

                // Transfer data
                system.transferData();
            }
            else system.toast(this, System.WARNING_TOAST, getString(R.string.Something_went_wrong), Toast.LENGTH_SHORT);
        }
    }

    public void onDismiss() {
        system.name = txtvName.getText().toString();
    }
}