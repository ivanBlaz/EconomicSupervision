package com.devivan.economicsupervision.Fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.TextView;
import android.widget.Toast;

import com.devivan.economicsupervision.System.System;
import com.devivan.economicsupervision.R;
import com.devivan.economicsupervision.Activities.TransactionsActivity;
import com.ebanx.swipebtn.SwipeButton;

import static com.devivan.economicsupervision.System.System.FATAL_ERROR;
import static com.devivan.economicsupervision.System.System.account;

public class AddPaymentFragment extends Fragment {

    View v;

    AddPaymentFragment __this__;

    System system;

    String type;

    //////////////
    // Controls //
    //////////////
    public TextView txtvName;
    private TextView txtvNumsBef, txtvNumsAft;
    SwipeButton swipeBtn;

    /////////////
    // UI Data //
    /////////////
    // LookUpKey
    public String lookUpKey = null;

    // Money
    public String numsBef = "0";
    public String numsAft = "00";
    public double value = 0.0;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        v = inflater.inflate(R.layout.fragment_add_payment, container, false);
        v.startAnimation(AnimationUtils.loadAnimation(v.getContext(), R.anim.fade_in));
        ///////////////////////////////////////////////////////////////////////////////

        // Initialize __this__
        __this__ = this;

        // Get system
        system = System.getSystem(this);

        // Get type
        if (getArguments() != null) {
            type = getArguments().getString("type");
            lookUpKey = getArguments().getString("lookUpKey");
        }

        // Prepare fragment?
        if (system != null) prepareFragment();

        // Return view
        return v;
    }

    private void prepareFragment() {
        //////////
        // Find //
        //////////
        // txtvName
        txtvName = v.findViewById(R.id.txtvName);
        txtvName.setText(lookUpKey != null ? system.findContactByLookupKey(lookUpKey) : "");
        txtvName.setOnClickListener(v -> {
            system.customKeyBoardContacts(
                    __this__,
                    system.keyboardContactsView.findViewById(R.id.textView),
                    txtvName,
                    R.drawable.box_with_round_selected,
                    R.drawable.box_with_round,
                    system.keyboardContactsView.findViewById(R.id.rvContacts),
                    null,
                    null,
                    type,
                    -1,
                    null,
                    null,
                    null,
                    null
            );
        });

        // txtvNumsBef
        txtvNumsBef = v.findViewById(R.id.txtvNumsBef);
        txtvNumsBef.setOnClickListener(v -> {
            system.customKeyBoard(0, system.keyboardView.findViewById(R.id.textView), true, false, 7,
                    txtvNumsBef,
                    R.drawable.box_with_round_selected,
                    R.drawable.box_with_round,
                    () -> {
                        numsBef = txtvNumsBef.getText().toString();
                        numsAft = txtvNumsAft.getText().toString();
                        try {
                            value = Double.parseDouble(numsBef.replace(".", "") + "." + numsAft);
                        } catch (NumberFormatException ignored) {
                            system.__wait__("outside_the_system.json");
                        }
                        return null;
                    });
        });

        // txtvNumsAft
        txtvNumsAft = v.findViewById(R.id.txtvNumsAft);
        txtvNumsAft.setOnClickListener(v -> {
            system.customKeyBoard(0, system.keyboardView.findViewById(R.id.textView), false, false, 2,
                    txtvNumsAft,
                    R.drawable.box_with_round_selected,
                    R.drawable.box_with_round,
                    () -> {
                        numsBef = txtvNumsBef.getText().toString();
                        numsAft = txtvNumsAft.getText().toString();
                        try {
                            value = Double.parseDouble(numsBef.replace(".", "") + "." + numsAft);
                        } catch (NumberFormatException ignored) {
                            system.__wait__("outside_the_system.json");
                        }
                        return null;
                    });
        });

        // swipeBtn
        swipeBtn = v.findViewById(R.id.swipeBtn);
        swipeBtn.setOnStateChangeListener(active -> {
            if (active) {
                if (lookUpKey != null && value > 0) {
                    if (isVisible()) addPayment();
                    else system.toast(system.activity, System.WARNING_TOAST, system.activity.getString(R.string.It_has_not_been_possible_to_perform_this_action), Toast.LENGTH_SHORT);
                } else system.toast(system.activity, System.WARNING_TOAST, system.activity.getString(R.string.The_data_entered_is_not_correct), Toast.LENGTH_SHORT);
            }
        });
    }



    // Disable UI
    private void disableUI() {
        // Disable bottom navigation view
        if (((TransactionsActivity)system.activity).bottomNavView != null) system.enableBottomNavView(((TransactionsActivity)system.activity).bottomNavView, false);

        // Disable textViews
        txtvName.setEnabled(false);
        txtvNumsBef.setEnabled(false);
        txtvNumsAft.setEnabled(false);

        // Disable swipe button
        swipeBtn.setEnabled(false);

        // Disable back
        ((TransactionsActivity)system.activity).back = false;
    }

    // Enable UI
    private void enableUI() {
        // Disable bottom navigation view
        if (((TransactionsActivity)system.activity).bottomNavView != null) system.enableBottomNavView(((TransactionsActivity)system.activity).bottomNavView, true);

        // Disable textViews
        txtvName.setEnabled(true);
        txtvNumsBef.setEnabled(true);
        txtvNumsAft.setEnabled(true);

        // Disable swipe button
        swipeBtn.setEnabled(true);

        // Disable back
        ((TransactionsActivity)system.activity).back = true;
    }

    // try -> addPayment
    private void addPayment() {
        // Disable UI
        disableUI();

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

        // Initialize now date
        String now = System.getLocalDateTimeNow();

        // Send request
        system.newRequest(system.config.isBm(), system.accountPos(), "I " + type + " " + now.replace(" ","_") + " " + lookUpKey + "," + value, () -> {
            // Connect to SQLite
            system.connect();

            // Execute request in SQLite
            system.write().execSQL("insert into movements (accountId, date, location, type, value) values(?,?,?,?,?)",
                    new String[]{system.def, now, lookUpKey, type, String.valueOf(value)});

            ///////////////
            // UI DESIGN //
            ///////////////
            // Change should or have && display
            if (type.equals("HA")) {
                // Change have
                account.setHave(account.getHave() + value);
                ((TransactionsActivity)system.activity).bottomNavView.getMenu().findItem(R.id.nav_have).setTitle(System.moneyFormat.format(account.getHave()));
                ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

                // Have fragment
                TransactionsActivity.pos = 2;
                HaveFragment haveFragment = new HaveFragment();
                ((TransactionsActivity)system.activity).loadFragment(haveFragment);
                ///////////////////////////////////////////////////////////////////
            }
            else {
                // Change should
                account.setShould(account.getShould() + value);
                ((TransactionsActivity)system.activity).bottomNavView.getMenu().findItem(R.id.nav_should).setTitle(System.moneyFormat.format(account.getShould()));
                ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

                // Should fragment
                TransactionsActivity.pos = 0;
                ShouldFragment shouldFragment = new ShouldFragment();
                ((TransactionsActivity)system.activity).loadFragment(shouldFragment);
                /////////////////////////////////////////////////////////////////////
            }

            // Enable UI
            enableUI();
            return null;
        });
    }
}