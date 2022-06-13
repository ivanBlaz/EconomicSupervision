package com.devivan.economicsupervision.Activities;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.util.SparseBooleanArray;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.airbnb.lottie.LottieAnimationView;
import com.devivan.economicsupervision.R;
import com.devivan.economicsupervision.System.System;
import com.devivan.economicsupervision.System.VoiceAssistant;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;
import java.util.Objects;

import static com.devivan.economicsupervision.System.System.FATAL_ERROR;
import static com.devivan.economicsupervision.System.System.account;
import static com.devivan.economicsupervision.System.System.moneyFormat;
import static com.sothree.slidinguppanel.SlidingUpPanelLayout.PanelState.COLLAPSED;

public class MovementsActivity extends AppCompatActivity {

    System system;

    MovementsActivity __this__;

    public boolean back = true;

    //////////////
    // Controls //
    //////////////
    public TextView txtvFilter, txtvBalance, txtvBalanceMoney, txtvProfits, txtvLosses;
    public ImageView imgvProfits, imgvLosses;
    public LottieAnimationView lottieVoiceAssistant, lottieFilter;
    public SlidingUpPanelLayout slidingUpPanelLayout;
    public RecyclerView rvMovements;
    public FloatingActionButton btnVoiceAssistantHelp;

    //////////
    // Data //
    //////////
    public boolean isScrolling;
    public int currentItems, totalItems, scrollOutItems;
    public static SparseBooleanArray animations = new SparseBooleanArray();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.activity_movements);

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
                back = true;
                System.going = true;
                super.onBackPressed();
                overridePendingTransition(R.anim.slide_in_top, R.anim.slide_out_top);
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

    @SuppressLint("SetTextI18n")
    public void displayMoney() {
        // txtvBalance
        txtvBalance = findViewById(R.id.txtvBalance);
        String month = Calendar.getInstance().getDisplayName(Calendar.MONTH, Calendar.LONG, Locale.getDefault());
        txtvBalance.setText(Objects.requireNonNull(month).substring(0, 1).toUpperCase() + month.substring(1).toLowerCase() + " [" + account.getCurrency() + "]");

        // txtvBalanceMoney
        txtvBalanceMoney = findViewById(R.id.txtvBalanceMoney);
        txtvBalanceMoney.setText(moneyFormat.format(account.getMoney()));

        // imgvProfits
        imgvProfits = findViewById(R.id.imgvProfits);
        imgvProfits.setImageResource(Math.abs(account.getMonthBenefits()) >= Math.abs(account.getMonthExpenses()) ? R.drawable.income_up : R.drawable.income_down);

        // txtvProfits
        txtvProfits = findViewById(R.id.txtvProfits);
        txtvProfits.setText(moneyFormat.format(account.getMonthBenefits()));

        // imgvLosses
        imgvLosses = findViewById(R.id.imgvLosses);
        imgvLosses.setImageResource(Math.abs(account.getMonthExpenses()) > Math.abs(account.getMonthBenefits()) ? R.drawable.expenses_up : R.drawable.expenses_down);

        // txtvLosses
        txtvLosses = findViewById(R.id.txtvLosses);
        txtvLosses.setText(moneyFormat.format(account.getMonthExpenses()));
    }

    // Speech
    SpeechRecognizer mSpeechRecognizer;
    Intent mSpeechRecognizerIntent;
    public void initSpeech() {
        mSpeechRecognizer = SpeechRecognizer.createSpeechRecognizer(system.activity);

        mSpeechRecognizerIntent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        mSpeechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        mSpeechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE,
                Locale.getDefault());

        // VOICE RECOGNITION
        mSpeechRecognizer.setRecognitionListener(new RecognitionListener() {
            @Override
            public void onReadyForSpeech(Bundle params) {

            }

            @Override
            public void onBeginningOfSpeech() {

            }


            @Override
            public void onRmsChanged(float rmsdB) {

            }

            @Override
            public void onBufferReceived(byte[] buffer) {

            }

            @Override
            public void onEndOfSpeech() {
                closeSpeech();
            }

            @Override
            public void onError(int error) {
                if (error == 7) closeSpeech();
            }

            @Override
            public void onResults(Bundle results) {
                // Getting all the matches
                ArrayList<String> matches = results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);

                // Displaying the first match
                if (matches != null) {
                    // Get first match
                    String s = matches.get(0);

                    if (system.doesTheDatabaseExist() && !system.isDatabaseCorrupt()) {
                        VoiceAssistant.filterData(system, rvMovements, null, s);
                    } else { FATAL_ERROR = true; system.activity.onBackPressed(); }
                }
            }

            @Override
            public void onPartialResults(Bundle partialResults) {

            }

            @Override
            public void onEvent(int eventType, Bundle params) {

            }
        });
    }

    public void openSpeech() {
        // Restart speech
        restartSpeech();
    }

    public void closeSpeech() {
        // Zoom out animation
        Animation zoom_out = AnimationUtils.loadAnimation(system.activity, R.anim.zoom_out_value);
        lottieVoiceAssistant.startAnimation(zoom_out);
        //////////////////////////////////////////////

        // Stop speech
        stopSpeech();

        // Collapse slide up panel layout
        slidingUpPanelLayout.setPanelState(COLLAPSED);
    }

    private void restartSpeech() {
        Animation zoom_in = AnimationUtils.loadAnimation(system.activity, R.anim.zoom_in_value);
        lottieVoiceAssistant.startAnimation(zoom_in);
        mSpeechRecognizer.stopListening();
        zoom_in.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                mSpeechRecognizer.startListening(mSpeechRecognizerIntent);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
    }

    private void stopSpeech() {
        mSpeechRecognizer.stopListening();
    }
}
