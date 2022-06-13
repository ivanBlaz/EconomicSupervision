package com.devivan.economicsupervision.Fragments;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AbsListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
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

import static com.devivan.economicsupervision.System.System.CONFIRMATION_TOAST;
import static com.devivan.economicsupervision.System.System.FATAL_ERROR;
import static com.devivan.economicsupervision.System.System.INFO_TOAST;
import static com.devivan.economicsupervision.System.System.account;
import static com.devivan.economicsupervision.System.System.moneyFormat;
import static com.sothree.slidinguppanel.SlidingUpPanelLayout.PanelState.COLLAPSED;
import static com.sothree.slidinguppanel.SlidingUpPanelLayout.PanelState.DRAGGING;
import static com.sothree.slidinguppanel.SlidingUpPanelLayout.PanelState.EXPANDED;

public class TransactionsFragment extends Fragment {

    public View v;

    TransactionsFragment __this__;

    System system;

    //////////////
    // Controls //
    //////////////
    TextView txtvFilter;
    public TextView txtvBalance, txtvBalanceMoney, txtvInflowCount,txtvInflow, txtvOutflowCount, txtvOutflow;
    LottieAnimationView lottieVoiceAssistant, lottieFilter;
    public SlidingUpPanelLayout slidingUpPanelLayout;
    public RecyclerView rvTransactions;
    FloatingActionButton btnVoiceAssistantHelp;

    //////////
    // Data //
    //////////
    boolean isScrolling;
    int currentItems, totalItems, scrollOutItems;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        v = inflater.inflate(R.layout.fragment_transactions, container, false);

        // Initialize __this__
        __this__ = this;

        // Get system
        system = System.getSystem(this);

        // Prepare fragment?
        if (system != null) prepareFragment();

        // Return view
        return v;
    }

    @Override
    public void onDestroyView() {
        if (mSpeechRecognizer != null) mSpeechRecognizer.stopListening();
        super.onDestroyView();
    }

    private void prepareFragment() {
        // Init speech
        initSpeech();

        //////////
        // Find //
        //////////
        // txtvBalance
        txtvBalance = v.findViewById(R.id.txtvBalance);

        // txtvBalanceMoney
        txtvBalanceMoney = v.findViewById(R.id.txtvBalanceMoney);

        // txtvInflowCount
        txtvInflowCount = v.findViewById(R.id.txtvInflowCount);

        // txtvInflow
        txtvInflow = v.findViewById(R.id.txtvInflow);

        // txtvOutflowCount
        txtvOutflowCount = v.findViewById(R.id.txtvOutflowCount);

        // txtvOutflow
        txtvOutflow = v.findViewById(R.id.txtvOutflow);

        // txtvFilter
        txtvFilter = v.findViewById(R.id.txtvFilter);

        // lottieFilter
        lottieFilter = v.findViewById(R.id.lottieFilter);
        lottieFilter.setVisibility(View.GONE);
        lottieFilter.addAnimatorListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                lottieFilter.setVisibility(View.GONE);
                txtvFilter.setText(system.activity.getString(R.string.Filter));
            }
        });

        // lottieVoiceAssistant
        lottieVoiceAssistant = v.findViewById(R.id.lottieVoiceAssistant);
        lottieVoiceAssistant.setOnClickListener(v -> closeSpeech());

        // btnVoiceAssistantHelp
        btnVoiceAssistantHelp = v.findViewById(R.id.btnVoiceAssistantHelp);
        btnVoiceAssistantHelp.setOnClickListener(v -> system.showRecyclerViewDialog(false));

        // slidingUpPanelLayout
        slidingUpPanelLayout = v.findViewById(R.id.slidingUpTransactions);
        slidingUpPanelLayout.addPanelSlideListener(new SlidingUpPanelLayout.PanelSlideListener() {
            @Override
            public void onPanelSlide(View panel, float slideOffset) {
                txtvFilter.setAlpha(1 - slideOffset);
                lottieVoiceAssistant.setAlpha(slideOffset);
            }

            @Override
            public void onPanelStateChanged(View panel, SlidingUpPanelLayout.PanelState previousState, SlidingUpPanelLayout.PanelState newState) {
                if (newState == EXPANDED) {
                    if (system.doesTheVoiceAssistantSupportTheCurrentLanguage(false)) {
                        if (System.device.isPremium()) {
                            if (!FATAL_ERROR) openSpeech();
                            else if (previousState == DRAGGING) slidingUpPanelLayout.setPanelState(COLLAPSED);
                        } else slidingUpPanelLayout.setPanelState(COLLAPSED);
                    } else slidingUpPanelLayout.setPanelState(COLLAPSED);
                } else if (previousState == COLLAPSED && newState == DRAGGING) {

                    if (system.doesTheVoiceAssistantSupportTheCurrentLanguage(true)) {
                        if (!System.device.isPremium()) {
                            system.toast(system.activity, CONFIRMATION_TOAST, getString(R.string.Go_premium_and_you_can_enjoy_this_option), Toast.LENGTH_SHORT);
                            slidingUpPanelLayout.setPanelState(COLLAPSED);
                        }
                    }
                } else if (newState == COLLAPSED) closeSpeech();
            }
        });

        // rvTransactions
        rvTransactions = v.findViewById(R.id.rvTransactions);
        rvTransactions.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (newState == AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL) {
                    isScrolling = true;
                    Animation zoom_out = AnimationUtils.loadAnimation(system.activity, R.anim.zoom_out);
                    zoom_out.setDuration(100);
                    btnVoiceAssistantHelp.startAnimation(zoom_out);
                    zoom_out.setAnimationListener(new Animation.AnimationListener() {
                        @Override
                        public void onAnimationStart(Animation animation) {

                        }

                        @Override
                        public void onAnimationEnd(Animation animation) {
                            btnVoiceAssistantHelp.setVisibility(View.GONE);
                        }

                        @Override
                        public void onAnimationRepeat(Animation animation) {

                        }
                    });
                } else if (newState == AbsListView.OnScrollListener.SCROLL_STATE_IDLE) {
                    Animation zoom_in = AnimationUtils.loadAnimation(system.activity, R.anim.zoom_in);
                    zoom_in.setDuration(100);
                    btnVoiceAssistantHelp.setVisibility(View.VISIBLE);
                    btnVoiceAssistantHelp.startAnimation(zoom_in);
                }
            }

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                LinearLayoutManager llm = (LinearLayoutManager) recyclerView.getLayoutManager();
                assert llm != null;
                currentItems = llm.getChildCount();
                totalItems = llm.getItemCount();
                scrollOutItems = llm.findFirstVisibleItemPosition();

                if (isScrolling && (currentItems + scrollOutItems == totalItems) && (VoiceAssistant.isPayment(VoiceAssistant.tGroupBy) || VoiceAssistant.isGroup(VoiceAssistant.tGroupBy))) {
                    if (system.doesTheDatabaseExist() && !system.isDatabaseCorrupt()) {
                        isScrolling = false;
                        VoiceAssistant.nextData();
                        lottieFilter.setVisibility(View.VISIBLE);
                        lottieFilter.playAnimation();
                        txtvFilter.setText(system.activity.getString(R.string.Filtering));
                    } else { FATAL_ERROR = true; system.activity.onBackPressed(); }
                }
            }
        });

        // Filter data
        if (system.doesTheDatabaseExist() && !system.isDatabaseCorrupt()) {
            VoiceAssistant.filterData(system, rvTransactions, this, null);
            displayMoney();
        } else { FATAL_ERROR = true; system.activity.onBackPressed(); }
    }

    @SuppressLint("SetTextI18n")
    public void displayMoney() {
        // txtvBalance
        String month = Calendar.getInstance().getDisplayName(Calendar.MONTH, Calendar.LONG, Locale.getDefault());
        txtvBalance.setText(Objects.requireNonNull(month).substring(0, 1).toUpperCase() + month.substring(1).toLowerCase() + " [" + account.getCurrency() + "]");

        // txtvBalanceMoney
        txtvBalanceMoney.setText(moneyFormat.format(account.getMoney()));

        // txtvInflowCount | txtvOutflowCount
        VoiceAssistant.displayFlowOfMoney(txtvInflowCount, txtvInflow, true);
        VoiceAssistant.displayFlowOfMoney(txtvOutflowCount, txtvOutflow, false);
        ////////////////////////////////////////////////////////////////////////////
    }

    // Speech
    SpeechRecognizer mSpeechRecognizer;
    Intent mSpeechRecognizerIntent;
    private void initSpeech() {
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
                        VoiceAssistant.filterData(system, rvTransactions, TransactionsFragment.this, s);
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

    private void openSpeech() {
        // Restart speech
        restartSpeech();
    }

    private void closeSpeech() {
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