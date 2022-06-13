package com.devivan.economicsupervision.Activities.SignUp;

import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.airbnb.lottie.LottieAnimationView;
import com.devivan.economicsupervision.R;
import com.devivan.economicsupervision.System.System;
import com.hbb20.CountryCodePicker;

public class SignUpStep3_phoneNumber_Activity extends AppCompatActivity {

    System system;

    SignUpStep3_phoneNumber_Activity __this__;

    public static String originatingAddress;

    public ConstraintLayout clActivitySignUpStep3PhoneNumber;

    public LottieAnimationView btnLogIn;
    public TextView txtvLogIn;
    public ImageView btnLogInO;

    public TextView txtvPhoneNumber;

    public CountryCodePicker countryCodePicker;

    public CheckBox cbAutoSendSMS;

    public TextView btnLicenseTerms, btnPrivacyPolicy;

    public LottieAnimationView lottieNext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up_step3_phone_number);

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
        system.phoneNumber = txtvPhoneNumber.getText().toString();
        system.countryISO = countryCodePicker.getSelectedCountryNameCode().toUpperCase();
        originatingAddress = countryCodePicker.getSelectedCountryCodeWithPlus() + system.phoneNumber;
    }
}