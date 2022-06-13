package com.devivan.economicsupervision.Activities;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;

import com.airbnb.lottie.LottieAnimationView;
import com.devivan.economicsupervision.System.System;
import com.devivan.economicsupervision.R;
import com.devivan.economicsupervision.UtilityClasses.SizeAdapter;

import java.util.ArrayList;


public class MainActivity extends AppCompatActivity {
    // Calculator screen
    public TextView txtvCalc, txtvResult, txtvName;
    public View divider;
    ////////////////////

    // Calculator keys
    public LottieAnimationView btn0, btn1, btn2, btn3, btn4, btn5, btn6, btn7, btn8, btn9, btnBack;
    public LottieAnimationView lottieArrowTop, lottieArrowBottom, lottieArrowLeft, lottieArrowRight;
    public ImageView btnMinus, btnPoint, btnPlus;
    /////////////////////////////////////////////

    System system;

    public boolean back = true;

    @SuppressLint("StaticFieldLeak")
    private static MainActivity __this__;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        // Prepare or refresh activity? /////////////
        if (__this__ == null) prepareActivity(); /**/
        else refreshActivity();                  /**/
        /////////////////////////////////////////////

    }

    @Override
    protected void onResume() {
        super.onResume();

        // Hide system ui
        System.hideSystemUI(this);

         // Resume system
        if (system != null) system.resume();
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
        if (ConCatActivity.isGoCon() || ConCatActivity.isGoCat()) {
            if (ConCatActivity.isGoCon()) ConCatActivity.setCon(false);
            else ConCatActivity.setCat(false);
        } else System.going = false;
    }

    @Override
    public void onBackPressed() {
        if (system != null && (back && !System.FATAL_ERROR)) system.bye();
    }

    @Override
    protected void onDestroy() {
        if (system != null) system.destroy();
        super.onDestroy();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (!System.FATAL_ERROR) {
            switch (requestCode) {
                case 0: // [ NO PERMITS REQUIRED ] | SIGN UP
                    // Dismiss dialog
                    system.dismissDialog(system.infoDialog);

                    // Start to register
                    system.startSignUp();
                    break;
                case 1: // [ READ_CONTACTS | RECORD_AUDIO ] | TRANSACTIONS
                    if (grantResults[0] == PackageManager.PERMISSION_DENIED ||
                            grantResults[1] == PackageManager.PERMISSION_DENIED) {
                        // Permissions
                        String contacts = permissions[0];
                        String audio = permissions[1];
                        /////////////////////////////////

                        //////////////////////////////////
                        // User rejected the permission //
                        //////////////////////////////////
                        boolean showRationale = shouldShowRequestPermissionRationale(contacts) &&
                                shouldShowRequestPermissionRationale(audio);


                        if (!showRationale && system.infoDialog != null) {
                            // Ask for permissions manually
                            system.allowPermissionsManually(this, requestCode);
                        } else {
                            // Explain to the user that they need to allow permissions to continue
                            system.explainTheReasonForThePermissions(requestCode);
                        }
                    } else {
                        // Dismiss dialog
                        system.dismissDialog(system.infoDialog);

                        // Go to TransactionsActivity
                        system.tryToConnectToTheInternet(() -> {
                            if (system.doesTheDatabaseExist() && !system.isDatabaseCorrupt()) {
                                System.waiting = false;
                                System.going = true;
                                Intent i = new Intent(__this__, TransactionsActivity.class);
                                system.putData(i);
                                i.putExtra("system", system);
                                startActivity(i);
                                overridePendingTransition(R.anim.slide_in_top, R.anim.slide_out_top);
                            } else {
                                system.dismissDialog(system.changeAccountMoneyDialog);
                                System.FATAL_ERROR = true;
                                system.restartApp();
                            }
                            return null;
                        });
                    }
                    break;
                case 2: // [ ACCESS_FINE_LOCATION ] | CATEGORIES
                    if (grantResults[0] == PackageManager.PERMISSION_DENIED) {
                        // Permissions
                        String location = permissions[0];
                        /////////////////////////////////

                        //////////////////////////////////
                        // User rejected the permission //
                        //////////////////////////////////
                        boolean showRationale = shouldShowRequestPermissionRationale(location);

                        if (!showRationale && system.infoDialog != null) {
                            // Ask for permissions manually
                            system.allowPermissionsManually(this, requestCode);
                        } else {
                            // Explain to the user that they need to allow permissions to continue
                            system.explainTheReasonForThePermissions(requestCode);
                        }
                    } else {
                        // Dismiss dialog
                        system.dismissDialog(system.infoDialog);

                        // Go to CategoriesActivity -> 1
                        if (system.doesTheDatabaseExist() && !system.isDatabaseCorrupt()) {
                            system.tryToConnectToTheInternet(() -> {
                                System.waiting = false;
                                System.going = true;
                                Intent i = new Intent(__this__, ConCatActivity.class);
                                system.putData(i);
                                i.putExtra("system", system);
                                ConCatActivity.setCat(false);
                                startActivity(i);
                                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_right);
                                return null;
                            });
                        } else {
                            system.dismissDialog(system.changeAccountMoneyDialog);
                            System.FATAL_ERROR = true;
                            system.restartApp();
                        }
                    }
                    break;
                case 3: // [ ACCESS_FINE_LOCATION ] | CONCEPTS
                    if (grantResults[0] == PackageManager.PERMISSION_DENIED) {
                        // Permissions
                        String location = permissions[0];
                        /////////////////////////////////

                        //////////////////////////////////
                        // User rejected the permission //
                        //////////////////////////////////
                        boolean showRationale = shouldShowRequestPermissionRationale(location);

                        if (!showRationale && system.infoDialog != null) {
                            // Ask for permissions manually
                            system.allowPermissionsManually(this, requestCode);
                        } else {
                            // Explain to the user that they need to allow permissions to continue
                            system.explainTheReasonForThePermissions(requestCode);
                        }
                    } else {
                        // Dismiss dialog
                        system.dismissDialog(system.infoDialog);

                        // Go to ConceptsActivity -> 0
                        if (system.doesTheDatabaseExist() && !system.isDatabaseCorrupt()) {
                            system.tryToConnectToTheInternet(() -> {
                                System.waiting = false;
                                System.going = true;
                                Intent i = new Intent(__this__, ConCatActivity.class);
                                system.putData(i);
                                i.putExtra("system", system);
                                ConCatActivity.setCon(false);
                                startActivity(i);
                                overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_left);
                                return null;
                            });
                        } else {
                            system.dismissDialog(system.changeAccountMoneyDialog);
                            System.FATAL_ERROR = true;
                            system.restartApp();
                        }
                    }
                    break;
                case 4: // [ RECORD_AUDIO ] | MOVEMENTS
                    if (grantResults[0] == PackageManager.PERMISSION_DENIED) {
                        // Permissions
                        String location = permissions[0];
                        /////////////////////////////////

                        //////////////////////////////////
                        // User rejected the permission //
                        //////////////////////////////////
                        boolean showRationale = shouldShowRequestPermissionRationale(location);

                        if (!showRationale && system.infoDialog != null) {
                            // Ask for permissions manually
                            system.allowPermissionsManually(this, requestCode);
                        } else {
                            // Explain to the user that they need to allow permissions to continue
                            system.explainTheReasonForThePermissions(requestCode);
                        }
                    } else {
                        // Dismiss dialog
                        system.dismissDialog(system.infoDialog);

                        // Go to MovementsActivity
                        if (system.doesTheDatabaseExist() && !system.isDatabaseCorrupt()) {
                            system.tryToConnectToTheInternet(() -> {
                                System.waiting = false;
                                System.going = true;
                                Intent i = new Intent(__this__, MovementsActivity.class);
                                system.putData(i);
                                i.putExtra("system", system);
                                ConCatActivity.setCon(false);
                                startActivity(i);
                                overridePendingTransition(R.anim.slide_out_bottom, R.anim.slide_in_bottom);
                                return null;
                            });
                        } else {
                            system.dismissDialog(system.changeAccountMoneyDialog);
                            System.FATAL_ERROR = true;
                            system.restartApp();
                        }
                    }
                    break;
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case System.payPalRequestCode:
                // Premium
                if (resultCode == RESULT_OK) {
                    system.setPremium();
                    system.toast(this, System.CONFIRMATION_TOAST, getString(R.string.Thank_you_very_much_for_trusting_Premium), Toast.LENGTH_SHORT);
                }
                else system.toast(this, System.WARNING_TOAST, getString(R.string.Something_went_wrong), Toast.LENGTH_SHORT);
                break;
            case System.payPalDonationRequestCode:
                // Donation
                if (resultCode == RESULT_OK) system.toast(this, System.CONFIRMATION_TOAST, getString(R.string.Thank_you_very_much_for_your_donation), Toast.LENGTH_SHORT);
                else system.toast(this, System.WARNING_TOAST, getString(R.string.Something_went_wrong), Toast.LENGTH_SHORT);
                break;
        }
    }

    private void prepareActivity() {
        // Initialize __this__
        __this__ = this;

        // Change ThreadPolicy
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        ///////////////////////////////////

        // Customize screen
        Window window = getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(ContextCompat.getColor(this, R.color.black));
        //////////////////////////////////////////////////////////////////////////////

        // Set content view
        setContentView(R.layout.waiting_for_authentication);
        ////////////////////////////////////////////////////

        // Controls
        ImageView imgvWFAAstronaut = findViewById(R.id.imgvWFAAstronaut);
        ImageView imgvWFAPoint = findViewById(R.id.imgvWFAPoint);
        ImageView imgvWFATop = findViewById(R.id.imgvWFATop);
        ImageView imgvWFABottom = findViewById(R.id.imgvWFABottom);
        ImageView imgvWFALeft = findViewById(R.id.imgvWFALeft);
        ImageView imgvWFARight = findViewById(R.id.imgvWFARight);
        TextView txtvWFAAction = findViewById(R.id.txtvWFAAction);
        TextView txtvWFAAppName = findViewById(R.id.txtvWFAAppName);
        LottieAnimationView lottieLoading = findViewById(R.id.lottieLoading);
        /////////////////////////////////////////////////////////////////////

        // Down -> Should · Transactions · Have
        Animation moveDown = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.move_down);
        imgvWFATop.setAnimation(moveDown);
        moveDown.start();
        /////////////////

        // Down listener
        moveDown.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) { }

            @SuppressLint("SetTextI18n")
            @Override
            public void onAnimationEnd(Animation animation) {
                // Should · Transactions · Have
                imgvWFABottom.setVisibility(View.VISIBLE);
                txtvWFAAction.setAnimation(AnimationUtils.loadAnimation(getApplicationContext(), R.anim.bounce));
                txtvWFAAction.setText(getString(R.string.Transactions));
                ////////////////////////////////////////////////////////

                // Up -> Categories · Concepts
                Animation moveUp = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.move_up);
                imgvWFATop.setAnimation(moveUp);
                moveUp.start();
                ///////////////

                // Up listener
                moveUp.setAnimationListener(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) { }

                    @Override
                    public void onAnimationEnd(Animation animation) {
                        // Categories · Concepts
                        txtvWFAAction.setAnimation(AnimationUtils.loadAnimation(getApplicationContext(), R.anim.bounce));
                        txtvWFAAction.setText(getString(R.string.Movements));
                        /////////////////////////////////////////////////////////////////////////////////////////////

                        // Right -> Categories -> Expense & Income
                        Animation moveRight = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.move_right);
                        imgvWFALeft.setAnimation(moveRight);
                        ////////////////////////////////////

                        // Right listener
                        moveRight.setAnimationListener(new Animation.AnimationListener() {
                            @Override
                            public void onAnimationStart(Animation animation) { }

                            @Override
                            public void onAnimationEnd(Animation animation) {
                                // Expense · Income
                                imgvWFARight.setVisibility(View.VISIBLE);
                                txtvWFAAction.setAnimation(AnimationUtils.loadAnimation(getApplicationContext(), R.anim.bounce));
                                txtvWFAAction.setText(getString(R.string.Categories));
                                //////////////////////////////////////////////////////

                                // Left -> Concepts -> Invert · Evert
                                Animation moveLeft = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.move_left);
                                imgvWFALeft.setAnimation(moveLeft);
                                ///////////////////////////////////

                                // Left listener
                                moveLeft.setAnimationListener(new Animation.AnimationListener() {
                                    @Override
                                    public void onAnimationStart(Animation animation) { }

                                    @Override
                                    public void onAnimationEnd(Animation animation) {
                                        // Invert · Evert
                                        txtvWFAAction.setAnimation(AnimationUtils.loadAnimation(getApplicationContext(), R.anim.bounce));
                                        txtvWFAAction.setText(getString(R.string.Concepts));
                                        ////////////////////////////////////////////////////

                                        // Bounce listener
                                        txtvWFAAction.getAnimation().setAnimationListener(new Animation.AnimationListener() {
                                            @Override
                                            public void onAnimationStart(Animation animation) { }

                                            @Override
                                            public void onAnimationEnd(Animation animation) {
                                                // Fade out
                                                Animation fadeOut = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fade_out);
                                                txtvWFAAction.setAnimation(fadeOut);
                                                imgvWFALeft.setAnimation(fadeOut);
                                                imgvWFATop.setAnimation(fadeOut);
                                                imgvWFARight.setAnimation(fadeOut);
                                                imgvWFABottom.setAnimation(fadeOut);
                                                imgvWFAPoint.setAnimation(fadeOut);
                                                ///////////////////////////////////

                                                // Fade out listener
                                                fadeOut.setAnimationListener(new Animation.AnimationListener() {
                                                    @Override
                                                    public void onAnimationStart(Animation animation) { }

                                                    @Override
                                                    public void onAnimationEnd(Animation animation) {
                                                        // Visibility -> GONE
                                                        txtvWFAAction.setVisibility(View.GONE);
                                                        imgvWFALeft.setVisibility(View.GONE);
                                                        imgvWFATop.setVisibility(View.GONE);
                                                        imgvWFARight.setVisibility(View.GONE);
                                                        imgvWFABottom.setVisibility(View.GONE);
                                                        imgvWFAPoint.setVisibility(View.GONE);
                                                        //////////////////////////////////////

                                                        // Fade in
                                                        Animation fadeIn = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fade_in);
                                                        imgvWFAAstronaut.setAnimation(fadeIn);
                                                        txtvWFAAppName.setAnimation(fadeIn);
                                                        ////////////////////////////////////

                                                        // Visibility -> VISIBLE
                                                        imgvWFAAstronaut.setVisibility(View.VISIBLE);
                                                        txtvWFAAppName.setVisibility(View.VISIBLE);
                                                        lottieLoading.setVisibility(View.VISIBLE);
                                                        //////////////////////////////////////////

                                                        // Fade in listener
                                                        fadeIn.setAnimationListener(new Animation.AnimationListener() {
                                                            @Override
                                                            public void onAnimationStart(Animation animation) { }

                                                            @Override
                                                            public void onAnimationEnd(Animation animation) {
                                                                // Start system
                                                                system = new System(__this__);
                                                                system.prepareActivity(__this__);
                                                                system.startListening();
                                                                ////////////////////////
                                                            }

                                                            @Override
                                                            public void onAnimationRepeat(Animation animation) { }
                                                        });
                                                    }

                                                    @Override
                                                    public void onAnimationRepeat(Animation animation) { }
                                                });
                                            }

                                            @Override
                                            public void onAnimationRepeat(Animation animation) { }
                                        });
                                    }

                                    @Override
                                    public void onAnimationRepeat(Animation animation) { }
                                });
                            }

                            @Override
                            public void onAnimationRepeat(Animation animation) { }
                        });
                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) { }
                });
            }

            @Override
            public void onAnimationRepeat(Animation animation) { }
        });
    }

    public void refreshActivity() {
        // Initialize __this__
        __this__ = this;

        // Change ThreadPolicy
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        ///////////////////////////////////

        // Customize screen
        Window window = getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(ContextCompat.getColor(this, R.color.black));
        //////////////////////////////////////////////////////////////////////////////

        // Set content view
        setContentView(R.layout.waiting_for_authentication);
        ////////////////////////////////////////////////////

        // Controls
        ImageView imgvWFAAstronaut = findViewById(R.id.imgvWFAAstronaut);
        ImageView imgvWFAPoint = findViewById(R.id.imgvWFAPoint);
        ImageView imgvWFATop = findViewById(R.id.imgvWFATop);
        ImageView imgvWFABottom = findViewById(R.id.imgvWFABottom);
        ImageView imgvWFALeft = findViewById(R.id.imgvWFALeft);
        ImageView imgvWFARight = findViewById(R.id.imgvWFARight);
        TextView txtvWFAAction = findViewById(R.id.txtvWFAAction);
        TextView txtvWFAAppName = findViewById(R.id.txtvWFAAppName);
        LottieAnimationView lottieLoading = findViewById(R.id.lottieLoading);
        /////////////////////////////////////////////////////////////////////

        // Visibility -> GONE
        txtvWFAAction.setVisibility(View.GONE);
        imgvWFALeft.setVisibility(View.GONE);
        imgvWFATop.setVisibility(View.GONE);
        imgvWFARight.setVisibility(View.GONE);
        imgvWFABottom.setVisibility(View.GONE);
        imgvWFAPoint.setVisibility(View.GONE);
        //////////////////////////////////////

        // Fade in
        Animation fadeIn = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fade_in);
        imgvWFAAstronaut.setAnimation(fadeIn);
        txtvWFAAppName.setAnimation(fadeIn);
        ////////////////////////////////////

        // Visibility -> VISIBLE
        imgvWFAAstronaut.setVisibility(View.VISIBLE);
        txtvWFAAppName.setVisibility(View.VISIBLE);
        lottieLoading.setVisibility(View.VISIBLE);
        //////////////////////////////////////////

        // Fade in listener
        fadeIn.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) { }

            @Override
            public void onAnimationEnd(Animation animation) {
                // Start system
                system = new System(__this__);
                system.prepareActivity(__this__);
                system.startListening();
                ////////////////////////
            }

            @Override
            public void onAnimationRepeat(Animation animation) { }
        });
    }

    public void resizeCalculatorButtons() {
        DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
        float dpHeight = displayMetrics.heightPixels / displayMetrics.density;
        float dpWidth = displayMetrics.widthPixels / displayMetrics.density;

        float extraHeight = 20;
        float extraWidth = extraHeight / 3;
        if (System.fullScreen) {
            int resourceId = getResources().getIdentifier("navigation_bar_height", "dimen", "android");
            if (resourceId > 0) extraHeight -= SizeAdapter.getDp(this, getResources().getDimensionPixelSize(resourceId));

            //resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
            //if (resourceId > 0) extraHeight -= SizeAdapter.getDp(this, getResources().getDimensionPixelSize(resourceId));
        }

        int cols = 3;
        int rows = 4;

        float hDp = 307 + extraHeight;
        float wDp = 40 + extraWidth;

        float _h_ = dpHeight - hDp;
        float _w_ = dpWidth - wDp;

        float h = _h_ / rows;
        float w = _w_ / cols;

        h = SizeAdapter.getPx(this, h);
        w = SizeAdapter.getPx(this, w);

        try {
            SizeAdapter.changeHW(h, w, btn1, btn2, btn3, btn4, btn5, btn6, btn7, btn8, btn9, btn0);
            SizeAdapter.changeHW(h, w, btnMinus, btnPlus);
        } catch (Exception ignored) { }
    }
}
