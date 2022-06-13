package com.devivan.economicsupervision.Adapters.FriendsAdapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.airbnb.lottie.LottieAnimationView;
import com.devivan.economicsupervision.Fragments.HaveIndividualFragment;
import com.devivan.economicsupervision.Objects.Account.Friend.Friend;
import com.devivan.economicsupervision.Objects.Account.Payment.Payment;
import com.devivan.economicsupervision.System.VoiceAssistant;
import com.devivan.economicsupervision.UtilityClasses.CustomViewPager;
import com.devivan.economicsupervision.System.System;
import com.devivan.economicsupervision.Fragments.HaveFragment;
import com.devivan.economicsupervision.Fragments.ShouldFragment;
import com.devivan.economicsupervision.R;
import com.devivan.economicsupervision.Activities.TransactionsActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

import static com.devivan.economicsupervision.System.System.FATAL_ERROR;
import static com.devivan.economicsupervision.System.System.INFO_TOAST;
import static com.devivan.economicsupervision.System.System.account;

class MyDiffUtilCallback extends DiffUtil.Callback {
    private final List<Friend> oldFriends;
    private final List<Friend> newFriends;

    public MyDiffUtilCallback(List<Friend> oldFriends, List<Friend> newFriends) {
        this.oldFriends = oldFriends;
        this.newFriends = newFriends;
    }

    @Override
    public int getOldListSize() {
        return oldFriends != null ? oldFriends.size() : 0;
    }

    @Override
    public int getNewListSize() {
        return newFriends != null ? newFriends.size() : 0;
    }

    @Override
    public boolean areItemsTheSame(int oldPosition, int newPosition) {
        return oldPosition == newPosition;
    }

    @Override
    public boolean areContentsTheSame(int oldPosition, int newPosition) {
        int result = newFriends.get(newPosition).compareTo(oldFriends.get(oldPosition));
        return result == 0;
    }

    @Nullable
    @Override
    public Object getChangePayload(int oldItemPosition, int newItemPosition) {
        Friend newFriend = newFriends.get(newItemPosition);
        Friend oldFriend = oldFriends.get(oldItemPosition);

        Bundle bundle = new Bundle();

        if (!newFriend.getLookUpKey().equals(oldFriend.getLookUpKey()))
            bundle.putString("lookUpKey", newFriend.getLookUpKey());

        if (newFriend.getPayments() != oldFriend.getPayments())
            bundle.putParcelableArrayList("payments", newFriend.payments);

        double oldMoney = oldFriend.payments.stream().filter(Objects::nonNull).mapToDouble(Payment::getValue).sum() + account.getMoney();
        double newMoney = newFriend.payments.stream().filter(Objects::nonNull).mapToDouble(Payment::getValue).sum() + account.getMoney();
        if (newMoney != oldMoney)
            bundle.putDouble("money", newMoney);

        if (bundle.size() == 0)
            return null;

        return bundle;
    }
}

public class FriendsAdapter extends RecyclerView.Adapter<FriendsAdapter.ViewHolder> {

    // Data
    public Context context;
    public System system;
    public ArrayList<Friend> friends;
    public String type;
    SparseBooleanArray visibility = new SparseBooleanArray();
    /////////////////////////////////////////////////////////

    // UI
    public Fragment fragment;
    public RecyclerView rvFriends;
    public BottomNavigationView bottomNavView;
    public CustomViewPager viewPager;
    public TabLayout tabLayout;
    ///////////////////////////

    // Constructor
    public FriendsAdapter(Context context, Fragment fragment, System system, RecyclerView rvFriends, String type) {
        this.context = context;
        this.fragment = fragment;
        this.system = system;
        this.rvFriends = rvFriends;
        this.type = type;
        this.friends = new ArrayList<>();

        //////////////
        // Controls //
        //////////////
        // Bottom navigation view
        bottomNavView = fragment.getActivity() != null ? ((TransactionsActivity) fragment.getActivity()).bottomNavView : null;
        //
        // View pager & tab layout [ Have ]
        if (type.equals("HA")) {
            viewPager = fragment.getParentFragment() != null ? ((HaveFragment) fragment.getParentFragment()).viewPager : null;
            tabLayout = fragment.getParentFragment() != null ? ((HaveFragment) fragment.getParentFragment()).tabLayout : null;
        }
        //
        // View pager & tab layout [ Should ]
        else if (type.equals("SH")) {
            viewPager = fragment.getParentFragment() != null ? ((ShouldFragment) fragment.getParentFragment()).viewPager : null;
            tabLayout = fragment.getParentFragment() != null ? ((ShouldFragment) fragment.getParentFragment()).tabLayout : null;
        }

    }

    ///////////////////////
    // Adapter utilities //
    ///////////////////////

    // refreshList
    public void refreshList(ArrayList<Friend> friends) {
        // Calculate data diff
        DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(new MyDiffUtilCallback(this.friends, friends));
        diffResult.dispatchUpdatesTo(this);

        // Refresh list
        this.friends.clear();
        this.friends.addAll(friends);

        if (friends.size() > 0) displayRecyclerView();
        else displayNewPaymentLayout();
    }

    private void displayNewPaymentLayout() {
        if (fragment instanceof ShouldFragment) {
            // Change visibility
            ShouldFragment sf = (ShouldFragment)fragment;
            sf.v.findViewById(R.id.clNewPayment).setVisibility(View.VISIBLE);
            sf.v.findViewById(R.id.rvFriends).setVisibility(View.GONE);

            // Get activity
            TransactionsActivity activity = ((TransactionsActivity) system.activity);

            // Set text
            ((TextView) sf.v.findViewById(R.id.txtvNewPayment)).setText(context.getString(R.string.new_debt));

            // Animations?
            if (!TransactionsActivity.animations.get(0)) {
                TransactionsActivity.animations.put(0, true);
                Animation slideInLeft = AnimationUtils.loadAnimation(context, R.anim.slide_in_left);
                Animation slideInRight = AnimationUtils.loadAnimation(context, R.anim.slide_in_right);
                sf.v.findViewById(R.id.txtvNewPayment).setAnimation(slideInLeft);
                sf.v.findViewById(R.id.imgvNewPayment).setAnimation(slideInRight);
                sf.v.findViewById(R.id.imgvPlusNewPayment).setVisibility(View.GONE);
                slideInLeft.setAnimationListener(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {

                    }

                    @Override
                    public void onAnimationEnd(Animation animation) {
                        Animation zoomIn = AnimationUtils.loadAnimation(context, R.anim.zoom_in);
                        sf.v.findViewById(R.id.imgvPlusNewPayment).setVisibility(View.VISIBLE);
                        sf.v.findViewById(R.id.imgvPlusNewPayment).setAnimation(zoomIn);
                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {

                    }
                });
            }

            // Get layout + click listener
            ConstraintLayout layout = sf.v.findViewById(R.id.clNewPayment);
            layout.setOnClickListener(v -> activity.bottomNavView.setSelectedItemId(R.id.nav_should));
        } else if (fragment instanceof HaveIndividualFragment) {
            // Change visibility
            HaveIndividualFragment hif = (HaveIndividualFragment)fragment;
            hif.v.findViewById(R.id.clNewPayment).setVisibility(View.VISIBLE);
            hif.v.findViewById(R.id.rvFriends).setVisibility(View.GONE);

            // Get activity
            TransactionsActivity activity = ((TransactionsActivity) system.activity);

            // Set text
            ((TextView) hif.v.findViewById(R.id.txtvNewPayment)).setText(context.getString(R.string.new_payment_credit));

            // Animations?
            if (!TransactionsActivity.animations.get(2)) {
                TransactionsActivity.animations.put(2, true);
                Animation slideInLeft = AnimationUtils.loadAnimation(context, R.anim.slide_in_left);
                Animation slideInRight = AnimationUtils.loadAnimation(context, R.anim.slide_in_right);
                hif.v.findViewById(R.id.txtvNewPayment).setAnimation(slideInLeft);
                hif.v.findViewById(R.id.imgvNewPayment).setAnimation(slideInRight);
                hif.v.findViewById(R.id.imgvPlusNewPayment).setVisibility(View.GONE);
                slideInLeft.setAnimationListener(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {

                    }

                    @Override
                    public void onAnimationEnd(Animation animation) {
                        Animation zoomIn = AnimationUtils.loadAnimation(context, R.anim.zoom_in);
                        hif.v.findViewById(R.id.imgvPlusNewPayment).setVisibility(View.VISIBLE);
                        hif.v.findViewById(R.id.imgvPlusNewPayment).setAnimation(zoomIn);
                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {

                    }
                });
            }

            // Get layout + click listener
            ConstraintLayout layout = hif.v.findViewById(R.id.clNewPayment);
            layout.setOnClickListener(v -> activity.bottomNavView.setSelectedItemId(R.id.nav_have));
        }
    }

    private void displayRecyclerView() {
        if (fragment instanceof ShouldFragment) {
            ShouldFragment sf = (ShouldFragment)fragment;
            sf.v.findViewById(R.id.clNewPayment).setVisibility(View.GONE);
            sf.v.findViewById(R.id.rvFriends).setVisibility(View.VISIBLE);
        } else if (fragment instanceof HaveIndividualFragment) {
            HaveIndividualFragment hif = (HaveIndividualFragment)fragment;
            hif.v.findViewById(R.id.clNewPayment).setVisibility(View.GONE);
            hif.v.findViewById(R.id.rvFriends).setVisibility(View.VISIBLE);
        }
    }

    // displayFriendName
    private void displayFriendName(ViewHolder holder, String lookUpKey) {
        // Set name
        holder.txtvName.setText(system.findContactByLookupKey(lookUpKey));
    }

    // displayPaymentsInfo
    @SuppressLint("SetTextI18n")
    private void displayPaymentsInfo(ViewHolder holder, ArrayList<Payment> payments) {
        // Set pays
        holder.txtvShowValues.setText(payments.size() - 1 + " " + context.getString(type.equals("HA") ? R.string.have_payments : R.string.debts));

        // Set time ago
        holder.txtvDate.setText(system.getTimeAgo(payments));

        // Sum of values
        double sumOfValues = system.getSumOfValues(payments);

        // Set value
        holder.txtvValue.setText(System.moneyFormat.format(sumOfValues));

        // Set money
        holder.txtvMoney.setText(System.moneyFormat.format(account.getMoney() + sumOfValues));
    }

    ////////
    // UI //
    ////////
    public RecyclerView.OnItemTouchListener disabler;

    // Disable UI
    private void disableUI() {
        // Initialize RecyclerViewDisabler
        if (disabler == null) disabler = new System.RecyclerViewDisabler();

        // Disable recyclerView scroll
        rvFriends.addOnItemTouchListener(disabler);

        // Disable bottom navigation view
        if (bottomNavView != null) system.enableBottomNavView(bottomNavView, false);

        // Disable pagers pagination
        if (viewPager != null && tabLayout != null) system.enablePagersPagination(viewPager, tabLayout, false);

        // Disable back
        ((TransactionsActivity)system.activity).back = false;
    }

    // Enable UI
    private void enableUI() {
        // Enable recyclerView scroll
        rvFriends.removeOnItemTouchListener(disabler);

        // Enable bottom navigation view
        if (bottomNavView != null) system.enableBottomNavView(bottomNavView, true);

        // Enable pagers pagination
        if (viewPager != null && tabLayout != null) system.enablePagersPagination(viewPager, tabLayout, true);

        // Enable back
        ((TransactionsActivity)system.activity).back = true;

        // Reset variables
        attempts = 0;
        pos = -1;
        name = null;
        value = 0;
        //////////
    }

    // try -> payPayments
    private void payPayments(int pos) {
        // Disable UI
        disableUI();

        // Try to connect to the internet
        system.tryToConnectToTheInternet(() -> {
            if (system.doesTheDatabaseExist() && !system.isDatabaseCorrupt()) letPayPayments(pos);
            else { FATAL_ERROR = true; system.activity.onBackPressed(); }
            return null;
        });
    }

    // let -> payPayments
    private void letPayPayments(int pos) {
        //////////
        // DATA //
        //////////
        // Get friend
        Friend friend = friends.get(pos);

        // Initialize now date
        String now = System.getLocalDateTimeNow();

        // Send request
        system.newRequest(system.config.isBm(), system.accountPos(), "U " + type + " " + now.replace(" ","_") + " " + friend.getLookUpKey(), () -> {
            // Connect to SQLite
            system.connect();

            // Execute request in SQLite
            system.write().execSQL("update movements set type = 'TR', date = ? " +
                            "where type = ? and location = ? and accountId = ?",
                    new String[]{now, type, friend.getLookUpKey(), system.def});

            // Get sum of values
            double sumOfValues = system.getSumOfValues(friend.getPayments());

            // Set money
            account.setMoney(account.getMoney() + sumOfValues);

            // Change money in SQLite
            system.changeMoney();
            /////////////////////

            // Month benefits or expenses?
            if (sumOfValues > 0) account.setMonthBenefits(account.getMonthBenefits() + sumOfValues);
            else account.setMonthExpenses(account.getMonthExpenses() + sumOfValues);
            ////////////////////////////////////////////////////////////////////////

            ///////////////
            // UI DESIGN //
            ///////////////
            // Change transact && display
            account.setTransact(account.getTransact() + sumOfValues);
            bottomNavView.getMenu().findItem(R.id.nav_transaction).setTitle(System.moneyFormat.format(account.getTransact()));
            //////////////////////////////////////////////////////////////////////////////////////////////////////////////////

            // Change should or have && display
            if (type.equals("HA")) {
                account.setHave(account.getHave() - sumOfValues);
                bottomNavView.getMenu().findItem(R.id.nav_have).setTitle(System.moneyFormat.format(account.getHave()));
            }
            else {
                account.setShould(account.getShould() - sumOfValues);
                bottomNavView.getMenu().findItem(R.id.nav_should).setTitle(System.moneyFormat.format(account.getShould()));
            }

            // Refresh list -> friends
            refreshList(system.getFriends(type));

            // Refresh list -> groups
            if (type.equals("HA") && friend.payments.stream().anyMatch(p -> p != null && p.getGroupId() != -1))
                if (fragment.getParentFragment() != null) {
                    ((HaveFragment) fragment.getParentFragment()).haveGroupFragment.groupsAdapter.refreshList(system.getGroups(false));
                }

            // Enable UI
            enableUI();
            return null;
        });
    }

    // try -> deletePayments
    private void deletePayments(int pos) {
        // Disable UI
        disableUI();

        // Try to connect to the internet
        system.tryToConnectToTheInternet(() -> {
            if (system.doesTheDatabaseExist() && !system.isDatabaseCorrupt()) letDeletePayments(pos);
            else { FATAL_ERROR = true; system.activity.onBackPressed(); }
            return null;
        });
    }

    // let -> deletePayments
    private void letDeletePayments(int pos) {
        //////////
        // DATA //
        //////////
        // Get friend
        Friend friend = friends.get(pos);

        // Send request
        system.newRequest(system.config.isBm(), system.accountPos(), "D " + type + " " + friend.getLookUpKey(), () -> {
            // Connect to SQLite
            system.connect();

            // Execute request in SQLite
            system.write().execSQL("delete from movements where type = ? and location = ? and accountId = ?", new String[]{type, friend.getLookUpKey(), system.def});

            // Get sum of values
            double sumOfValues = system.getSumOfValues(friend.getPayments());

            ///////////////
            // UI DESIGN //
            ///////////////
            // Change should or have && display
            if (type.equals("HA")) {
                account.setHave(account.getHave() - sumOfValues);
                bottomNavView.getMenu().findItem(R.id.nav_have).setTitle(System.moneyFormat.format(account.getHave()));
            }
            else {
                account.setShould(account.getShould() - sumOfValues);
                bottomNavView.getMenu().findItem(R.id.nav_should).setTitle(System.moneyFormat.format(account.getShould()));
            }

            // Refresh list -> friends
            refreshList(system.getFriends(type));

            // Refresh list -> groups
            if (type.equals("HA") && friend.payments.stream().anyMatch(p -> p != null && p.getGroupId() != -1))
                if (fragment.getParentFragment() != null) {
                    ((HaveFragment) fragment.getParentFragment()).haveGroupFragment.groupsAdapter.refreshList(system.getGroups(false));
                }

            // Enable UI
            enableUI();
            return null;
        });
    }



    ////////////////////////////////////
    // Voice Assistant -> Add Payment //
    ////////////////////////////////////
    int attempts = 0;
    public SpeechRecognizer mSpeechRecognizer;
    public Intent mSpeechRecognizerIntent;
    public void initData() {
        pos = -1;
        name = null;
        value = 0;
        attempts = 0;
    }

    public void initSpeech(RecognitionListener recognitionListener) {
        mSpeechRecognizer = SpeechRecognizer.createSpeechRecognizer(system.activity);

        mSpeechRecognizerIntent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        mSpeechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        mSpeechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE,
                Locale.getDefault());

        // VOICE ASSISTANT
        mSpeechRecognizer.setRecognitionListener(recognitionListener);
    }

    public void stopSpeech(boolean closeDialog) {
        mSpeechRecognizer.stopListening();
        if (closeDialog) {
            // Set null listener
            mSpeechRecognizer.setRecognitionListener(null);

            // Hide layout
            system.hideLayout(clVoiceAssistant);
        }
    }

    public void startSpeech() {
        system.showLayout(clVoiceAssistant);
        system.toast(system.activity, INFO_TOAST, context.getString(type.equals("HA") ? R.string.How_much_does_he_owe_you : R.string.How_much_do_you_owe).replace("NAME", "\"" + name + "\""), Toast.LENGTH_SHORT);
        restartSpeech();
    }

    private void restartSpeech() {
        Animation zoom_in = AnimationUtils.loadAnimation(lottieVoiceAssistant.getContext(), R.anim.zoom_in_value);
        mSpeechRecognizer.stopListening();
        lottieVoiceAssistant.startAnimation(zoom_in);
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


    int pos = -1;
    String name = null;
    public double value = 0;
    public ConstraintLayout clVoiceAssistant;
    public LottieAnimationView lottieVoiceAssistant;
    @SuppressLint("InflateParams")
    private void buildPayment(int pos) {
        // Disable UI
        disableUI();

        if (!system.exceedsTheMaxLimitOf(type.toLowerCase(), true)) {

            // Set pos
            this.pos = pos;

            // Initialize friend
            Friend friend = friends.get(pos);

            // Set name
            name = system.findContactByLookupKey(friend.getLookUpKey());

            // Initialize value
            value = 0;

            // Get Voice Assistant controls
            clVoiceAssistant = ((TransactionsActivity) system.activity).clVoiceAssistant;
            lottieVoiceAssistant = ((TransactionsActivity) system.activity).lottieVoiceAssistant;
            lottieVoiceAssistant.setOnClickListener(v -> {
                // Stop speech
                stopSpeech(true);

                // Init data
                initData();

                // Enable UI
                enableUI();
            });

            RecognitionListener recognitionListener = new RecognitionListener() {
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
                    if (!fragment.isVisible() || (pos < 0 || pos > friends.size()) || name == null) {
                        // Stop speech
                        stopSpeech(true);

                        // Init data
                        initData();

                        // Enable UI
                        enableUI();
                        return;
                    }
                    // Message error?
                    if (i == 7) {
                        if (value > 0) {
                            if (attempts < 3) {
                                // Accept or Cancel
                                system.toast(system.activity, INFO_TOAST,
                                        context.getString(R.string.accept) + " " +
                                                context.getString(R.string.or) + " " +
                                                context.getString(R.string.cancel)
                                        , Toast.LENGTH_SHORT);

                                // Restart speech
                                restartSpeech();
                                attempts++;
                            } else {
                                // Stop speech
                                stopSpeech(true);

                                // Init data
                                initData();

                                // Enable UI
                                enableUI();
                            }
                        } else {
                            // Stop speech
                            stopSpeech(true);

                            // Init data
                            initData();

                            // Enable UI
                            enableUI();
                        }
                    }
                }

                @Override
                public void onResults(Bundle bundle) {
                    if (!fragment.isVisible() || (pos < 0 || pos > friends.size()) || name == null) {
                        // Stop speech
                        stopSpeech(true);

                        // Init data
                        initData();

                        // Enable UI
                        enableUI();
                        return;
                    }
                    // Getting all the matches
                    ArrayList<String> matches = bundle.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);

                    // Displaying the first match
                    if (matches != null) {
                        // Get first match
                        String s = matches.get(0);
                        if (value > 0) {
                            // It's correct?
                            boolean yes = VoiceAssistant.itsCorrect(s);
                            if (yes) {
                                // Add payment?
                                // Stop speech + close dialog
                                stopSpeech(true);

                                // Add payment
                                addPayment();
                            } else {
                                boolean cancel = VoiceAssistant.itsCancel(s);

                                if (cancel) {
                                    // Stop speech
                                    stopSpeech(true);

                                    // Init data
                                    initData();

                                    // Enable UI
                                    enableUI();
                                } else {
                                    value = 0;
                                    attempts = 0;
                                    system.toast(system.activity, INFO_TOAST, context.getString(type.equals("HA") ? R.string.How_much_does_he_owe_you : R.string.How_much_do_you_owe).replace("NAME", "\"" + name + "\""), Toast.LENGTH_SHORT);

                                    // Restart speech
                                    restartSpeech();
                                }
                            }
                        } else {
                            // Set value
                            value = VoiceAssistant.getValue(s, false);

                            if (value > 0 && value <= 9999999.99)
                                system.toast(system.activity, INFO_TOAST, context.getString(type.equals("HA") ? R.string.Owes_you : R.string.You_owe_to)
                                        .replace("NAME", "\"" + name + "\"")
                                        .replace("X,XX", System.moneyFormat.format(value)), Toast.LENGTH_SHORT);
                            else
                                system.toast(system.activity, INFO_TOAST, context.getString(type.equals("HA") ? R.string.How_much_does_he_owe_you : R.string.How_much_do_you_owe).replace("NAME", "\"" + name + "\""), Toast.LENGTH_SHORT);

                            // Restart speech
                            restartSpeech();
                        }
                    }
                }

                @Override
                public void onPartialResults(Bundle bundle) {

                }

                @Override
                public void onEvent(int i, Bundle bundle) {

                }
            };
            initSpeech(recognitionListener);

            // Start speech
            startSpeech();

        } else enableUI();
    }

    // try -> addPayment
    private void addPayment() {
        // Try to connect to the internet
        system.tryToConnectToTheInternet(() -> {
            if (system.doesTheDatabaseExist() && !system.isDatabaseCorrupt()) letAddPayment();
            else { FATAL_ERROR = true; system.activity.onBackPressed(); }
            return null;
        });
    }


    // let -> addPayment
    private void letAddPayment() {
        // Change value
        value = type.equals("SH") ? -value : value;

        // Initialize friend
        Friend friend = friends.get(pos);

        // Initialize now date
        String now = System.getLocalDateTimeNow();

        // Send request
        system.newRequest(system.config.isBm(), system.accountPos(), "I " + type + " " + now.replace(" ","_") + " " + friend.getLookUpKey() + "," + value, () -> {
            // Connect to SQLite
            system.connect();

            // Execute request in SQLite
            system.write().execSQL("insert into movements (accountId, date, location, type, value) values(?,?,?,?,?)",
                    new String[]{system.def, now, friend.getLookUpKey(), type, String.valueOf(value)});

            ///////////////
            // UI DESIGN //
            ///////////////
            // Change should or have && display
            if (type.equals("HA")) {
                account.setHave(account.getHave() + value);
                bottomNavView.getMenu().findItem(R.id.nav_have).setTitle(System.moneyFormat.format(account.getHave()));
            }
            else {
                account.setShould(account.getShould() + value);
                bottomNavView.getMenu().findItem(R.id.nav_should).setTitle(System.moneyFormat.format(account.getShould()));
            }

            // Refresh list -> friends
            refreshList(system.getFriends(type));

            // Init data
            initData();

            // Enable UI
            enableUI();
            return null;
        });
    }
    /////////////////////////////////////////////

    @NonNull
    @Override
    public FriendsAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.line_friend, parent, false);
        return new ViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ///////////////
        // UI Design //
        ///////////////
        holder.cbPaid.setChecked(false);
        holder.txtvName.setBackgroundColor(context.getColor(android.R.color.transparent));
        holder.txtvValue.setTextColor(context.getColor(type.equals("HA") ? R.color.colorIncome : R.color.colorExpense));
        holder.btnShowOptions.setImageResource(visibility.get(position) ? R.drawable.ic_keyboard_arrow_up_50 : R.drawable.ic_keyboard_arrow_down_50);
        holder.txtvDeleteValues.setText(context.getString(type.equals("HA") ? R.string.delete_payment_credits : R.string.delete_debts));
        holder.txtvNewValue.setText(context.getString(type.equals("HA") ? R.string.new_payment_credit : R.string.new_debt));
        holder.clOptions.setVisibility(visibility.get(position) ? View.VISIBLE : View.GONE);
        ////////////////////////////////////////////////////////////////////////////////////

        /////////////
        // UI Data //
        /////////////
        // Initialize friend
        Friend friend = friends.get(position);

        // Display friend name
        displayFriendName(holder, friend.getLookUpKey());

        // Display payments info
        displayPaymentsInfo(holder, friend.payments);
        /////////////////////////////////////////////
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position, @NonNull List<Object> payloads) {
        if (payloads.isEmpty())
        super.onBindViewHolder(holder, position, payloads);
        else {
            ///////////////
            // UI Design //
            ///////////////
            holder.cbPaid.setChecked(false);
            holder.txtvValue.setTextColor(context.getColor(type.equals("HA") ? R.color.colorIncome : R.color.colorExpense));
            holder.btnShowOptions.setImageResource(visibility.get(position) ? R.drawable.ic_keyboard_arrow_up_50 : R.drawable.ic_keyboard_arrow_down_50);
            holder.txtvDeleteValues.setText(context.getString(type.equals("HA") ? R.string.delete_payment_credits : R.string.delete_debts));
            holder.txtvNewValue.setText(context.getString(type.equals("HA") ? R.string.new_payment_credit : R.string.new_debt));
            holder.clOptions.setVisibility(visibility.get(position) ? View.VISIBLE : View.GONE);
            ////////////////////////////////////////////////////////////////////////////////////
            Bundle bundle = (Bundle) payloads.get(0);
            for (String key : bundle.keySet()) {
                if (key.equals("lookUpKey")) displayFriendName(holder, bundle.getString("lookUpKey"));
                if (key.equals("payments")) displayPaymentsInfo(holder, bundle.getParcelableArrayList("payments"));
                if (key.equals("money")) holder.txtvMoney.setText(System.moneyFormat.format(bundle.getDouble("money")));
            }
        }
    }

    @Override
    public int getItemCount() {
        return friends != null ? friends.size() : 0;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        CheckBox cbPaid;
        ImageView btnShowOptions;
        ImageView btnDeleteValues;
        TextView txtvDeleteValues;
        ImageView btnShowValues;
        TextView txtvShowValues;
        ImageView btnNewValue;
        TextView txtvNewValue;
        TextView txtvName;
        TextView txtvDate;
        TextView txtvValue;
        TextView txtvMoney;
        ConstraintLayout clOptions;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            // cbPaid
            cbPaid = itemView.findViewById(R.id.cbPaid);
            cbPaid.setOnCheckedChangeListener((buttonView, isChecked) -> {
                if ((getAdapterPosition() > -1 && getAdapterPosition() < friends.size()) && isChecked)
                    payPayments(getAdapterPosition());
            });

            // btnShowOptions
            btnShowOptions = itemView.findViewById(R.id.btnShowOptions);
            btnShowOptions.setOnClickListener(v -> {
                if (getAdapterPosition() > -1 && getAdapterPosition() < friends.size()) {
                    if (clOptions.getVisibility() == View.VISIBLE) {
                        clOptions.setVisibility(View.GONE);
                        btnShowOptions.setImageResource(R.drawable.ic_keyboard_arrow_down_50);
                        visibility.put(getAdapterPosition(), false);
                    } else {
                        clOptions.setVisibility(View.VISIBLE);
                        btnShowOptions.setImageResource(R.drawable.ic_keyboard_arrow_up_50);
                        clOptions.startAnimation(AnimationUtils.loadAnimation(context, R.anim.fade_in));
                        visibility.put(getAdapterPosition(), true);
                    }
                }
            });

            // btnDeleteValues
            btnDeleteValues = itemView.findViewById(R.id.btnDeleteValues);
            btnDeleteValues.setOnClickListener(view -> {
                if (getAdapterPosition() > -1 && getAdapterPosition() < friends.size())
                    deletePayments(getAdapterPosition());
            });

            // txtvDeleteValues
            txtvDeleteValues = itemView.findViewById(R.id.txtvDeleteValues);
            txtvDeleteValues.setOnClickListener(v -> {
                if (getAdapterPosition() > -1 && getAdapterPosition() < friends.size())
                    deletePayments(getAdapterPosition());
            });

            // btnShowValues
            btnShowValues = itemView.findViewById(R.id.btnShowValues);
            btnShowValues.setOnClickListener(v -> {
                if (getAdapterPosition() > -1 && getAdapterPosition() < friends.size())
                    system.showRecyclerViewDialog(FriendsAdapter.this, getAdapterPosition());
            });

            // txtvShowValues
            txtvShowValues = itemView.findViewById(R.id.txtvShowValues);
            txtvShowValues.setOnClickListener(view -> {
                if (getAdapterPosition() > -1 && getAdapterPosition() < friends.size())
                    system.showRecyclerViewDialog(FriendsAdapter.this, getAdapterPosition());
            });

            // btnNewValue
            btnNewValue = itemView.findViewById(R.id.btnNewValue);
            btnNewValue.setOnClickListener(v -> {
                if (getAdapterPosition() > -1 && getAdapterPosition() < friends.size()) {
                    if (system.doesTheVoiceAssistantSupportTheCurrentLanguage(true)) buildPayment(getAdapterPosition());
                }
            });
            btnNewValue.setOnLongClickListener(v -> {
                if (getAdapterPosition() > -1 && getAdapterPosition() < friends.size()) {
                    ((TransactionsActivity) system.activity).addPayment(type, friends.get(getAdapterPosition()).getLookUpKey());
                }
                return true;
            });

            // txtvNewValue
            txtvNewValue = itemView.findViewById(R.id.txtvNewValue);
            txtvNewValue.setOnClickListener(view -> {
                if (getAdapterPosition() > -1 && getAdapterPosition() < friends.size()) {
                    if (system.doesTheVoiceAssistantSupportTheCurrentLanguage(true)) buildPayment(getAdapterPosition());
                }
            });
            txtvNewValue.setOnLongClickListener(v -> {
                if (getAdapterPosition() > -1 && getAdapterPosition() < friends.size()) {
                    ((TransactionsActivity) system.activity).addPayment(type, friends.get(getAdapterPosition()).getLookUpKey());
                }
                return true;
            });

            // txtvName
            txtvName = itemView.findViewById(R.id.txtvName);
            txtvName.setOnClickListener(v -> {
                if (getAdapterPosition() > -1 && getAdapterPosition() < friends.size()) {
                    system.customKeyBoardContacts(
                            fragment,
                            system.keyboardContactsView.findViewById(R.id.textView),
                            txtvName,
                            R.drawable.box_with_round_selected,
                            android.R.color.transparent,
                            system.keyboardContactsView.findViewById(R.id.rvContacts),
                            rvFriends,
                            FriendsAdapter.this,
                            type,
                            getAdapterPosition(),
                            friends,
                            bottomNavView,
                            viewPager,
                            tabLayout
                    );
                }
            });

            // txtvDate
            txtvDate = itemView.findViewById(R.id.txtvDate);

            // txtvValue
            txtvValue = itemView.findViewById(R.id.txtvValue);

            // txtvMoney
            txtvMoney = itemView.findViewById(R.id.txtvMoney);

            // clOptions
            clOptions = itemView.findViewById(R.id.clOptions);
        }
    }
}
