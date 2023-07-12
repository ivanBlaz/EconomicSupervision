package com.devivan.economicsupervision.Fragments;

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
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.airbnb.lottie.LottieAnimationView;
import com.devivan.economicsupervision.Adapters.FriendsOfGroupAdapter.FriendsOfGroupAdapter;
import com.devivan.economicsupervision.Adapters.GroupsAdapter.GroupsAdapter;
import com.devivan.economicsupervision.System.VoiceAssistant;
import com.devivan.economicsupervision.UtilityClasses.CustomViewPager;
import com.devivan.economicsupervision.System.System;
import com.devivan.economicsupervision.R;
import com.devivan.economicsupervision.Activities.TransactionsActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.tabs.TabLayout;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Random;

import static com.devivan.economicsupervision.System.System.CONFIRMATION_TOAST;
import static com.devivan.economicsupervision.System.System.FATAL_ERROR;
import static com.devivan.economicsupervision.System.System.INFO_TOAST;
import static com.devivan.economicsupervision.System.System.account;
import static com.sothree.slidinguppanel.SlidingUpPanelLayout.PanelState.ANCHORED;
import static com.sothree.slidinguppanel.SlidingUpPanelLayout.PanelState.COLLAPSED;
import static com.sothree.slidinguppanel.SlidingUpPanelLayout.PanelState.DRAGGING;
import static com.sothree.slidinguppanel.SlidingUpPanelLayout.PanelState.EXPANDED;

public class HaveGroupFragment extends Fragment {

    public View v;

    HaveGroupFragment __this__;

    public System system;

    // RecyclerView + adapter
    RecyclerView rvGroups;
    public GroupsAdapter groupsAdapter;
    ///////////////////////////////////

    // Sliding up panel layout
    public SlidingUpPanelLayout slideUpNewGroup;

    // New group TextView
    TextView txtvNewGroup;

    // Voice assistant lottie
    LottieAnimationView lottieVoiceAssistant;

    // FriendsOfGroup RecyclerView + adapter
    public RecyclerView rvFriendsOfGroup;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        v = inflater.inflate(R.layout.fragment_have_group, container, false);

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
        // Initialize speech
        initSpeech();

        // New group TextView
        txtvNewGroup = v.findViewById(R.id.txtvNewGroup);

        // Voice assistant lottie
        lottieVoiceAssistant = v.findViewById(R.id.lottieVoiceAssistant);

        // FriendsOfGroup RecyclerView
        rvFriendsOfGroup = v.findViewById(R.id.rvFriendsOfGroup);

        // Panel for voice assistant
        slideUpNewGroup = v.findViewById(R.id.slidingUpNewGroup);
        final boolean[] exceedsLimit = {false};
        slideUpNewGroup.addPanelSlideListener(new SlidingUpPanelLayout.PanelSlideListener() {
            @Override
            public void onPanelSlide(View panel, float slideOffset) {
                // txtvNewGroup
                txtvNewGroup.setAlpha(1 - slideOffset);

                // lottieVoiceAssistant
                lottieVoiceAssistant.setAlpha(slideOffset);
            }

            @Override
            public void onPanelStateChanged(View panel, SlidingUpPanelLayout.PanelState previousState, SlidingUpPanelLayout.PanelState newState) {
                if (previousState == DRAGGING && newState == ANCHORED && started) {
                    // Set state collapsed
                    slideUpNewGroup.setPanelState(COLLAPSED);
                }
                else if (newState == EXPANDED) {
                    if (system.doesTheVoiceAssistantSupportTheCurrentLanguage(false)) {
                        if (System.device.isPremium()) {
                            if (!exceedsLimit[0]) {
                                if (!FATAL_ERROR && !started) openSpeech();
                                else if (previousState == DRAGGING) slideUpNewGroup.setPanelState(COLLAPSED);
                            } else slideUpNewGroup.setPanelState(COLLAPSED);
                        } else slideUpNewGroup.setPanelState(COLLAPSED);
                    } else slideUpNewGroup.setPanelState(COLLAPSED);
                }
                else if (newState == COLLAPSED) {
                    // New group has been build?
                    if (hasNewGroup) ((TransactionsActivity) system.activity).loadFragment(new HaveFragment());

                    // Set not started
                    started = false;

                    // Change visibility
                    lottieVoiceAssistant.setAlpha(1.0f);
                    rvFriendsOfGroup.setVisibility(View.GONE);
                    //////////////////////////////////////////

                    // Enable UI
                    system.enableUI(disabler, rvGroups, bottomNavigationView, viewPager, tabLayout);


                    // Enable back
                    ((TransactionsActivity) system.activity).back = true;

                    // Set slide up panel layout touchable
                    slideUpNewGroup.setTouchEnabled(true);

                } else if (previousState == COLLAPSED && newState == DRAGGING) {
                    // Disable UI
                    disableUI();

                    if (system.doesTheVoiceAssistantSupportTheCurrentLanguage(true)) {
                        if (System.device.isPremium()) {
                            exceedsLimit[0] = system.exceedsTheMaxLimitOf("gp", true);
                            if (exceedsLimit[0]) slideUpNewGroup.setPanelState(COLLAPSED);
                        } else {
                            system.toast(system.activity, CONFIRMATION_TOAST, getString(R.string.Go_premium_and_you_can_enjoy_this_option), Toast.LENGTH_SHORT);
                            slideUpNewGroup.setPanelState(COLLAPSED);
                        }
                    }
                }
            }
        });


        // Find
        rvGroups = v.findViewById(R.id.rvGroups);

        // Initialize adapter
        groupsAdapter = new GroupsAdapter(v.getContext(), this, system, rvGroups);

        // Set adapter
        System.setAdapter(rvGroups, groupsAdapter, false, false, new LinearLayoutManager(v.getContext(), RecyclerView.VERTICAL, false));

        // Refresh list
        if (system.doesTheDatabaseExist() && !system.isDatabaseCorrupt()) groupsAdapter.refreshList(system.getGroups(false));
        else { FATAL_ERROR = true; system.activity.onBackPressed(); }
    }

    public void disableUI() {
        // Get activity and fragment
        TransactionsActivity activity = ((TransactionsActivity) system.activity);
        HaveFragment fragment = ((HaveFragment)getParentFragment());
        ////////////////////////////////////////////////////////////

        // Disable back
        activity.back = false;

        // Get UI controls to disable
        if (disabler == null) disabler = new System.RecyclerViewDisabler();
        bottomNavigationView = activity.bottomNavView;
        viewPager = fragment != null ? fragment.viewPager : null;
        tabLayout = fragment != null ? fragment.tabLayout : null;
        /////////////////////////////////////////////////////////

        // Disable UI
        system.disableUI(disabler, rvGroups, bottomNavigationView, viewPager, tabLayout);
    }


    //////////////////////////////////////////
    // Voice Assistant -> Add Group Payment //
    //////////////////////////////////////////
    // Min & max
    final int minValBef = 1, minValAft = 1, minMem = 2;
    final int maxValBef = 1000, maxValAft = 99, maxMem = 32;

    // Data
    double value = 0;
    int count = 0;
    ArrayList<String> names;
    int attempts = 0;
    private void initData() {
        value = 0;
        count = 0;
        names = null;
    }

    // UI
    System.RecyclerViewDisabler disabler;
    BottomNavigationView bottomNavigationView;
    CustomViewPager viewPager;
    TabLayout tabLayout;
    ////////////////////

    // Slide up layout [ open or close ] speech
    public static boolean hasNewGroup = false;
    boolean started = false;
    private void openSpeech() {
        // Set started
        started = true;

        // Enable voice assistant
        lottieVoiceAssistant.setEnabled(true);
        lottieVoiceAssistant.setOnClickListener(v -> closeSpeech());

        // Slide up layout non-touchable
        slideUpNewGroup.setTouchEnabled(false);

        // Reset attempts
        attempts = 0;

        // Init other data
        initData();

        // Toast as 'tutorial'
        system.toast(system.activity, INFO_TOAST, getString(R.string.Who___How_many_owe_you) + " " +
                getString(R.string.between) + " " +
                getString(R.string.Value_to_pay), Toast.LENGTH_SHORT);

        // Restart speech
        restartSpeech();
    }

    @SuppressLint("ClickableViewAccessibility")
    public void closeSpeech() {
        // Zoom out animation
        Animation zoom_out = AnimationUtils.loadAnimation(system.activity, R.anim.zoom_out_value);
        lottieVoiceAssistant.startAnimation(zoom_out);
        //////////////////////////////////////////////

        // Stop speech
        stopSpeech();

        // Collapse slide up panel layout
        slideUpNewGroup.setPanelState(COLLAPSED);
    }

    // Speech
    SpeechRecognizer mSpeechRecognizer;
    Intent mSpeechRecognizerIntent;
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

    private void initSpeech() {
        mSpeechRecognizer = SpeechRecognizer.createSpeechRecognizer(v.getContext());

        mSpeechRecognizerIntent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        mSpeechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        mSpeechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE,
                Locale.getDefault());

        // VOICE RECOGNITION
        mSpeechRecognizer.setRecognitionListener(new RecognitionListener() {
            @Override
            public void onReadyForSpeech(Bundle bundle) {

            }

            @Override
            public void onBeginningOfSpeech() {

            }

            @Override
            public void onRmsChanged(float v) {

            }

            @Override
            public void onBufferReceived(byte[] bytes) {

            }

            @Override
            public void onEndOfSpeech() {

            }

            @Override
            public void onError(int i) {
                if (!isVisible() || slideUpNewGroup.getPanelState() != EXPANDED) {
                    stopSpeech();
                    return;
                }
                // Message error?
                if (i == 7) {
                    if (value == 0 && (count == 0 && names == null)) {
                        if (attempts < 3) {
                            // Display example
                            displayExampleSentence();

                            // Restart speech
                            restartSpeech();
                            attempts++;
                        } else {
                            // Close speech
                            closeSpeech();
                        }
                    } else {
                        if (attempts < 3) {
                            // Accept or cancel
                            system.toast(system.activity, INFO_TOAST,
                                    getString(R.string.accept) + " " +
                                            getString(R.string.or) + " " +
                                            getString(R.string.cancel)
                                    , Toast.LENGTH_SHORT);

                            // Restart speech
                            restartSpeech();
                            attempts++;
                        } else {
                            // Close speech
                            closeSpeech();
                        }
                    }
                }
            }

            @Override
            public void onResults(Bundle bundle) {
                if (!isVisible() || slideUpNewGroup.getPanelState() != EXPANDED) {
                    stopSpeech();
                    return;
                }
                // Getting all the matches
                ArrayList<String> matches = bundle.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);

                // Displaying the first match
                if (matches != null) {
                    // Get first match
                    String s = matches.get(0);

                    if (value > 0 && (count > 0 || names != null)) {
                        // It's correct?
                        boolean yes = VoiceAssistant.itsCorrect(s);

                        if (yes) {
                            // Names or count?
                            if (names != null) {
                                ///////////
                                // Names //
                                ///////////
                                buildGroup(value, names);
                            } else {
                                ///////////
                                // Count //
                                ///////////
                                buildGroup(value, count);
                            }
                            stopSpeech();
                        } else {
                            boolean cancel = VoiceAssistant.itsCancel(s);

                            if (cancel) {
                                // Close speech
                                closeSpeech();
                            } else {
                                // Open speech
                                openSpeech();
                            }
                        }
                    } else {
                        value = VoiceAssistant.getGroupValue(s);
                        if (value > 0) {
                            count = VoiceAssistant.getNumberOfParticipants(s);
                            if (count >= System.LIMIT_MIN_GP_PARTICIPANTS + 1 && count <= System.LIMIT_MAX_GP_PARTICIPANTS) {
                                count -= 1;
                                double val = value / count;
                                if (val < System.MIN_VALUE) {
                                    initData();
                                    system.toast(system.activity, INFO_TOAST, getString(R.string.The_minimum_value_per_person_is_X)
                                                    .replace("X", System.moneyFormat.format(System.MIN_VALUE))
                                            , Toast.LENGTH_SHORT);
                                } else if (val > System.MAX_VALUE) {
                                    initData();
                                    system.toast(system.activity, INFO_TOAST, getString(R.string.The_maximum_value_per_person_is_X)
                                                    .replace("X", System.moneyFormat.format(System.MAX_VALUE))
                                            , Toast.LENGTH_SHORT);
                                } else {
                                    system.toast(system.activity, INFO_TOAST, getString(R.string.friends_and_you_are_responsible_for_paying)
                                                    .replace("Y", String.valueOf(count))
                                                    .replace("X,XX", System.moneyFormat.format(value))
                                            , Toast.LENGTH_SHORT);
                                }
                            } else {
                                count = 0;
                                names = VoiceAssistant.getParticipantNames(s);
                                if (names != null && names.size() >= System.LIMIT_MIN_GP_PARTICIPANTS && names.size() <= System.LIMIT_MAX_GP_PARTICIPANTS) {
                                    double val = value / names.size();
                                    if (val < System.MIN_VALUE) {
                                        initData();
                                        system.toast(system.activity, INFO_TOAST, getString(R.string.The_minimum_value_per_person_is_X)
                                                        .replace("X", System.moneyFormat.format(System.MIN_VALUE))
                                                , Toast.LENGTH_SHORT);
                                    } else if (val > System.MAX_VALUE) {
                                        initData();
                                        system.toast(system.activity, INFO_TOAST, getString(R.string.The_maximum_value_per_person_is_X)
                                                        .replace("X", System.moneyFormat.format(System.MAX_VALUE))
                                                , Toast.LENGTH_SHORT);
                                    } else {
                                        system.toast(system.activity, INFO_TOAST, getString(R.string.and_you_are_responsible_for_paying)
                                                        .replace("Y", VoiceAssistant.arrayJoin(names, ", ", true))
                                                        .replace("X,XX", System.moneyFormat.format(value))
                                                , Toast.LENGTH_SHORT);
                                    }
                                } else {
                                    initData();
                                    system.toast(system.activity, INFO_TOAST, getString(R.string.The_group_must_be_made_up_of_at_least_2_friends_and_you)
                                            , Toast.LENGTH_SHORT);
                                }
                            }
                            // Restart speech
                            restartSpeech();
                        } else {
                            // Display example sentence
                            displayExampleSentence();

                            // Restart speech
                            restartSpeech();
                        }
                    }
                }
            }

            @Override
            public void onPartialResults(Bundle bundle) {

            }

            @Override
            public void onEvent(int i, Bundle bundle) {

            }
        });
    }

    /////////////////////////////
    // Group builder functions //
    /////////////////////////////
    // value and names
    private void buildGroup(double value, ArrayList<String> names) {
        // UI
        lottieVoiceAssistant.setEnabled(false);
        lottieVoiceAssistant.setOnClickListener(null);
        lottieVoiceAssistant.setAlpha(0.0f);
        rvFriendsOfGroup.setVisibility(View.VISIBLE);
        /////////////////////////////////////////////

        // Initialize hashMap
        HashMap<String, String> map = new HashMap<>();
        for (String name : names) map.put(name, null);

        // Initialize adapter
        FriendsOfGroupAdapter friendsOfGroupAdapter = new FriendsOfGroupAdapter(this, value, map);

        // Disable touch items of recyclerView
        System.RecyclerViewDisabler disabler = new System.RecyclerViewDisabler();
        rvFriendsOfGroup.addOnItemTouchListener(disabler);

        // Prepare recyclerView
        System.setAdapter(rvFriendsOfGroup, friendsOfGroupAdapter, false, false, new LinearLayoutManager(getActivity(), RecyclerView.VERTICAL, false));
        rvFriendsOfGroup.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (newState == 0) {
                    LinearLayoutManager llm = (LinearLayoutManager) recyclerView.getLayoutManager();
                    if (llm != null) {
                        if (llm.findFirstCompletelyVisibleItemPosition() != friendsOfGroupAdapter.pos) recyclerView.smoothScrollToPosition(friendsOfGroupAdapter.pos);
                        else {
                            friendsOfGroupAdapter.next = false;
                            Objects.requireNonNull(recyclerView.getItemAnimator()).setChangeDuration(0);
                            friendsOfGroupAdapter.notifyItemChanged(friendsOfGroupAdapter.pos);
                        }
                    }
                }
            }
        });
    }

    // value and count
    private void buildGroup(double value, int count) {
        // UI
        lottieVoiceAssistant.setEnabled(false);
        lottieVoiceAssistant.setOnClickListener(null);
        lottieVoiceAssistant.setAlpha(0.0f);
        rvFriendsOfGroup.setVisibility(View.VISIBLE);
        /////////////////////////////////////////////

        // Initialize arrayList
        ArrayList<String> lookUpKeys = new ArrayList<>();
        for (int i = 0; i < count; i++) lookUpKeys.add(i, null);

        // Initialize adapter
        FriendsOfGroupAdapter friendsOfGroupAdapter = new FriendsOfGroupAdapter(this, value, lookUpKeys);

        // Disable touch items of recyclerView
        System.RecyclerViewDisabler disabler = new System.RecyclerViewDisabler();
        rvFriendsOfGroup.addOnItemTouchListener(disabler);

        // Prepare recyclerView
        System.setAdapter(rvFriendsOfGroup, friendsOfGroupAdapter, false, false, new LinearLayoutManager(getActivity(), RecyclerView.VERTICAL, false));
        rvFriendsOfGroup.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (newState == 0) {
                    LinearLayoutManager llm = (LinearLayoutManager) recyclerView.getLayoutManager();
                    if (llm != null) {
                        if (llm.findFirstCompletelyVisibleItemPosition() != friendsOfGroupAdapter.pos) recyclerView.smoothScrollToPosition(friendsOfGroupAdapter.pos);
                        else {
                            friendsOfGroupAdapter.next = false;
                            Objects.requireNonNull(recyclerView.getItemAnimator()).setChangeDuration(0);
                            friendsOfGroupAdapter.notifyItemChanged(friendsOfGroupAdapter.pos);
                        }
                    }
                }
            }
        });
    }

    // try -> newGroup
    public void newGroup(double value, ArrayList<String> lookUpKeys, String groupName) {
        // Try to connect to the internet
        system.tryToConnectToTheInternet(() -> {
            if (system.doesTheDatabaseExist() && !system.isDatabaseCorrupt()) letNewGroup(value, lookUpKeys, groupName);
            else { FATAL_ERROR = true; system.activity.onBackPressed(); }
            return null;
        });
    }

    // let -> newGroup
    public void letNewGroup(double value, ArrayList<String> lookUpKeys, String groupName) {
        // Initialize now date
        String now = System.getLocalDateTimeNow();

        // Calculate val
        double val = value / (lookUpKeys.size() + 1);

        // Initialize lookUpKeys StringBuilder
        StringBuilder lookUpKeysStringBuilder = new StringBuilder();

        // Set lookUpKeys StringBuilder
        for (String lookUpKey : lookUpKeys) lookUpKeysStringBuilder.append(lookUpKey).append(",");
        lookUpKeysStringBuilder = new StringBuilder(lookUpKeysStringBuilder.substring(0, lookUpKeysStringBuilder.length() - 1));
        ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

        // Send request
        system.newRequest(system.config.isBm(), system.accountPos(), "I GP " + now.replace(" ","_") + " " + groupName.replace(" ","_") + "," + val + "," + lookUpKeysStringBuilder.toString(), () -> {
            // Connect to SQLite
            system.connect();

            // Execute request in SQLite -> insert group payment
            system.write().execSQL("insert into movements (accountId, date, location, type, value) values(?,?,?,?,?)",
                    new String[]{system.def, now, groupName, "GP", String.valueOf(val)});

            // Get max id of 'movements' table
            int maxId = system.getMaxId("movements");

            // Execute request in SQLite -> insert all haves
            for (String lookUpKey : lookUpKeys)
                system.write().execSQL("insert into movements (accountId, conceptId, date, location, type, value) values(?,?,?,?,?,?)",
                        new String[]{system.def, String.valueOf(maxId), now, lookUpKey, "HA", String.valueOf(val)});

            ///////////////
            // UI DESIGN //
            ///////////////
            // Change have && display
            account.setHave(account.getHave() + (value - val));
            ((TransactionsActivity)system.activity).bottomNavView.getMenu().findItem(R.id.nav_have).setTitle(System.moneyFormat.format(account.getHave()));

            // Refresh list -> groups
            groupsAdapter.refreshList(system.getGroups(false));

            // Refresh list -> friends
            if (getParentFragment() != null) {
                ((HaveFragment)getParentFragment()).haveIndividualFragment.friendsAdapter.refreshList(system.getFriends("HA"));
            }

            // Close speech
            closeSpeech();
            return null;
        });
    }

    private void displayExampleSentence() {
        // Get random value
        final int randomValBef = new Random().nextInt((maxValBef - minValBef) + 1) + minValBef;
        final int randomValAft = new Random().nextInt((maxValAft - minValAft) + 1) + minValAft;
        String value = System.moneyFormat.format(Double.parseDouble(randomValBef + "." + randomValAft));

        // Name or count example?
        int i = new Random().nextInt((2 - 1) + 1) + 1;
        if (i == 1) {
            ///////////
            // Names //
            ///////////

            // Calc random members
            final int randomMem = new Random().nextInt(((maxMem / 4) - minMem) + 1) + minMem;

            // Get random names
            List<String> names = new ArrayList<>(Arrays.asList(getResources().getStringArray(R.array.names)));
            StringBuilder s = new StringBuilder();
            for (int x = 0; x < randomMem; x++) {
                int randomIndex = new Random().nextInt(names.size());
                s.append(names.remove(randomIndex)).append(", ");
            }

            // Build example
            s = new StringBuilder(s.substring(0, s.length() - 2));
            int start = s.lastIndexOf(",");
            //
            String strStart = s.substring(0, start);
            String strMiddle = " " + getString(R.string.and) + " ";
            String strEnd = s.substring(start + 2);
            //
            s = new StringBuilder();
            s.append(getString(R.string.Example)).append(" : \"")
            .append(value).append(" ").append(getString(R.string.between)).append(" ").append(strStart).append(strMiddle).append(strEnd).append("\"");

            // Display example
            system.toast(system.activity, INFO_TOAST, s.toString(), Toast.LENGTH_SHORT);
        } else {
            ///////////
            // Count //
            ///////////

            // Calc random members
            final int randomMem = new Random().nextInt((maxMem - minMem) + 1) + minMem;

            // Build example
            StringBuilder s = new StringBuilder();
            s.append(getString(R.string.Example)).append(" : \"")
            .append(value).append(" ").append(getString(R.string.between)).append(" ").append(randomMem).append("\"");;

            // Display example
            system.toast(system.activity, INFO_TOAST, s.toString(), Toast.LENGTH_SHORT);
        }
    }
}