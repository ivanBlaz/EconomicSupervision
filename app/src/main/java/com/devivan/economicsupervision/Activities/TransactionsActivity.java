package com.devivan.economicsupervision.Activities;

import android.annotation.SuppressLint;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.util.SparseBooleanArray;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;

import com.airbnb.lottie.LottieAnimationView;
import com.devivan.economicsupervision.System.System;
import com.devivan.economicsupervision.Fragments.AddPaymentFragment;
import com.devivan.economicsupervision.Fragments.HaveFragment;
import com.devivan.economicsupervision.Fragments.ShouldFragment;
import com.devivan.economicsupervision.Fragments.TransactionsFragment;
import com.devivan.economicsupervision.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class TransactionsActivity extends AppCompatActivity {

    System system;

    TransactionsActivity __this__;

    public boolean back = true;

    public BottomNavigationView bottomNavView;

    // Voice Assistant
    public ConstraintLayout clVoiceAssistant;
    public LottieAnimationView lottieVoiceAssistant;
    ////////////////////////////////////////////////

    public static int pos = -1;
    public static SparseBooleanArray animations = new SparseBooleanArray();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.activity_transactions);

        // Initialize __this__
        __this__ = this;

        // Get system
        system = System.getSystem(__this__);

        // Prepare activity?
        if (system != null) system.prepareActivity(__this__);
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (system != null && !System.FATAL_ERROR) system.start();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (system != null && !System.FATAL_ERROR && !System.going) system.stop();
        System.going = false;
    }

    @Override
    protected void onResume() {
        super.onResume();
        System.hideSystemUI(this);
    }

    @Override
    public void onBackPressed() {
        if (system != null && (back || System.FATAL_ERROR)) {
            if (system.isNetworkAvailable()) {
                pos = -1;
                back = true;
                System.going = true;
                super.onBackPressed();
                overridePendingTransition(R.anim.slide_out_bottom, R.anim.slide_in_bottom);
            } else {
                system.tryToConnectToTheInternet(() -> {
                    onBackPressed();
                    return null;
                });
            }
        }
    }

    @Override
    protected void onDestroy() {
        if (system != null) system.destroy();
        super.onDestroy();
    }

    public boolean loadFragment(Fragment fragment) {
        if (system != null && fragment != null) {
            system.putData(fragment);
            Bundle bundle = new Bundle();
            bundle.putParcelable("system", system);
            fragment.setArguments(bundle);
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.transactionsFragmentContainer, fragment)
                    .commit();
            return true;
        }
        return false;
    }

    @SuppressLint("NonConstantResourceId")
    public final BottomNavigationView.OnNavigationItemSelectedListener navListener =
            menuItem -> {
                Fragment selectedFragment = null;
                switch (menuItem.getItemId()) {
                    case R.id.nav_should:
                        if (pos != 0) { pos = 0; selectedFragment = new ShouldFragment(); } else { addPayment("SH", null); }
                        break;
                    case R.id.nav_transaction:
                        if (pos != 1) { pos = 1; selectedFragment = new TransactionsFragment(); }
                        break;
                    case R.id.nav_have:
                        if (pos != 2) { pos = 2; selectedFragment = new HaveFragment(); } else addPayment("HA", null);
                        break;
                }
                return loadFragment(selectedFragment);
            };

    public void addPayment(String type, String lookUpKey) {
        if (!system.exceedsTheMaxLimitOf(type.toLowerCase(), true)) {
            // pos = -1
            pos = -1;

            // Initialize AddPaymentFragment
            AddPaymentFragment addPaymentFragment = new AddPaymentFragment();

            // Set arguments
            Bundle bundle = new Bundle();
            bundle.putParcelable("system", system);
            bundle.putString("type", type);
            bundle.putString("lookUpKey", lookUpKey);
            addPaymentFragment.setArguments(bundle);

            // Display fragment
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.transactionsFragmentContainer, addPaymentFragment)
                    .commit();
        }
    }
}