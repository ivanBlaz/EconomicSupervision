package com.devivan.economicsupervision.Adapters.TransactionsAdapter;

import android.annotation.SuppressLint;
import android.database.Cursor;
import android.speech.tts.Voice;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.devivan.economicsupervision.Activities.MovementsActivity;
import com.devivan.economicsupervision.Activities.TransactionsActivity;
import com.devivan.economicsupervision.Objects.Account.Friend.Friend;
import com.devivan.economicsupervision.Objects.Account.Group.Group;
import com.devivan.economicsupervision.Objects.Account.Movements.Movement;
import com.devivan.economicsupervision.Objects.Account.Payment.Payment;
import com.devivan.economicsupervision.R;
import com.devivan.economicsupervision.System.System;
import com.devivan.economicsupervision.System.VoiceAssistant;
import com.google.android.gms.dynamic.IFragmentWrapper;

import java.time.LocalDate;
import java.time.format.TextStyle;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.Callable;
import java.util.stream.Collectors;

import static com.devivan.economicsupervision.System.System.FATAL_ERROR;
import static com.devivan.economicsupervision.System.System.account;

public class TransactionsAdapter extends RecyclerView.Adapter<TransactionsAdapter.ViewHolder> {

    System system;
    public ArrayList<Object> objects;

    public TransactionsAdapter(System system, ArrayList<Object> objects) {
        this.system = system;
        this.objects = objects;
    }

    ///////////////////////
    // Adapter utilities //
    ///////////////////////

    // try -> deleteTransaction
    private void deleteTransaction(int pos) {
        // Disable UI
        disableUI();

        // Try to connect to the internet
        system.tryToConnectToTheInternet(() -> {
            if (system.doesTheDatabaseExist() && !system.isDatabaseCorrupt()) letDeleteTransaction(pos);
            else { FATAL_ERROR = true; system.activity.onBackPressed(); }
            return null;
        });
    }

    // let -> deleteTransaction
    private void letDeleteTransaction(int pos) {
        //////////
        // DATA //
        //////////
        // Get movement
        Payment p = (Payment) objects.get(pos);

        // Send request
        system.newRequest(system.config.isBm(), system.accountPos(), "D M " + p.getId(), () -> {
            // Connect to SQLite
            system.connect();

            // Execute request in SQLite
            system.write().execSQL("delete from movements where id = ?", new String[]{String.valueOf(p.getId())});

            // Change money
            account.setMoney(account.getMoney() - p.getValue());
            system.changeMoney();
            /////////////////////

            // Month benefits or expenses?
            if (p.getValue() > 0) account.setMonthBenefits(account.getMonthBenefits() - p.getValue());
            else account.setMonthExpenses(account.getMonthExpenses() + Math.abs(p.getValue()));
            ///////////////////////////////////////////////////////////////////////////////////

            ////////
            // UI //
            ////////
            // Display changes
            VoiceAssistant.fragment.displayMoney();

            // Change transact and display
            account.setTransact(account.getTransact() - p.getValue());
            ((TransactionsActivity)system.activity).bottomNavView.getMenu().findItem(R.id.nav_transaction).setTitle(System.moneyFormat.format(account.getTransact()));
            //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

            // Decrease offset
            VoiceAssistant.offset--;

            // Remove transaction
            objects.remove(pos);

            // Notify data set change
            notifyDataSetChanged();

            // Display
            VoiceAssistant.display();

            // Enable UI
            enableUI();
            return null;
        });
    }

    // try -> deleteTransactions
    private void deleteTransactions(int pos) {
        // Disable UI
        disableUI();

        // Try to connect to the internet
        system.tryToConnectToTheInternet(() -> {
            if (system.doesTheDatabaseExist() && !system.isDatabaseCorrupt()) letDeleteTransactions(pos);
            else { FATAL_ERROR = true; system.activity.onBackPressed(); }
            return null;
        });
    }

    // let -> deleteTransactions
    private void letDeleteTransactions(int pos) {
        //////////
        // DATA //
        //////////
        // Get friend
        Friend f = (Friend) objects.get(pos);

        // Send request
        system.newRequest(system.config.isBm(), system.accountPos(), "D TR " + f.getLookUpKey(), () -> {
            // Connect to SQLite
            system.connect();

            // Execute request in SQLite
            system.write().execSQL("delete from movements where type = 'TR' and location = ? and accountId = ?", new String[]{f.getLookUpKey(), system.def});

            // Calculate sum of payments
            double prof = f.getPayments().stream().filter(p -> p.getValue() > 0).mapToDouble(Payment::getValue).sum();
            double loss = f.getPayments().stream().filter(p -> p.getValue() < 0).mapToDouble(Payment::getValue).sum();
            double sum = f.getPayments().stream().mapToDouble(Payment::getValue).sum();

            // Change money
            account.setMoney(account.getMoney() - sum);
            system.changeMoney();
            /////////////////////

            // Month benefits and expenses
            account.setMonthBenefits(account.getMonthBenefits() - prof);
            account.setMonthExpenses(account.getMonthExpenses() + Math.abs(loss));
            //////////////////////////////////////////////////////////////////////

            ////////
            // UI //
            ////////
            // Change transact
            account.setTransact(account.getTransact() - sum);
            ((TransactionsActivity)system.activity).bottomNavView.getMenu().findItem(R.id.nav_transaction).setTitle(System.moneyFormat.format(account.getTransact()));
            //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

            // Display changes
            VoiceAssistant.fragment.displayMoney();

            // Decrease offset
            VoiceAssistant.offset -= f.getPayments().size();

            // Remove friend
            objects.remove(pos);

            // Notify data set change
            notifyDataSetChanged();

            // Display
            VoiceAssistant.display();

            // Enable UI
            enableUI();
            return null;
        });
    }

    // try -> deleteGroup
    private void deleteGroup(int pos) {
        // Disable UI
        disableUI();

        // Try to connect to the internet
        system.tryToConnectToTheInternet(() -> {
            if (system.doesTheDatabaseExist() && !system.isDatabaseCorrupt()) letDeleteGroup(pos);
            else { FATAL_ERROR = true; system.activity.onBackPressed(); }
            return null;
        });
    }

    // let -> deleteGroup
    private void letDeleteGroup(int pos) {
        //////////
        // DATA //
        //////////
        // Get group
        Group g = (Group) objects.get(pos);

        // Send request
        system.newRequest(system.config.isBm(), system.accountPos(), "D GP " + g.getId(), () -> {
            // Connect to SQLite
            system.connect();

            // Execute requests
            system.write().execSQL("delete from movements where conceptId = ? and type in('HA','TR') and accountId = ?", new String[]{String.valueOf(g.getId()), system.def});
            system.write().execSQL("delete from movements where id = ?", new String[]{String.valueOf(g.getId())});

            // Change money
            account.setMoney(account.getMoney() - g.getPaid());
            system.changeMoney();
            /////////////////////

            // Month benefits
            account.setMonthBenefits(account.getMonthBenefits() - Math.abs(g.getPaid()));

            ////////
            // UI //
            ////////
            // Change transact
            account.setTransact(account.getTransact() - g.getPaid());
            ((TransactionsActivity)system.activity).bottomNavView.getMenu().findItem(R.id.nav_transaction).setTitle(System.moneyFormat.format(account.getTransact()));
            //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

            // Display changes
            VoiceAssistant.fragment.displayMoney();

            // Decrease offset
            VoiceAssistant.offset -= g.friends.stream().mapToInt(p -> p.payments.size()).sum();

            // Remove friend
            objects.remove(pos);

            // Notify data set change
            notifyDataSetChanged();

            // Display
            VoiceAssistant.display();

            // Enable UI
            enableUI();
            return null;
        });
    }

    ////////
    // UI //
    ////////
    RecyclerView.OnItemTouchListener disabler;

    // Disable UI
    public void disableUI() {
        // Initialize RecyclerViewDisabler
        if (disabler == null) disabler = new System.RecyclerViewDisabler();

        // Disable recyclerView scroll
        if (VoiceAssistant.fragment.rvTransactions != null) VoiceAssistant.fragment.rvTransactions.addOnItemTouchListener(disabler);

        // Disable bottom navigation view
        if (((TransactionsActivity) system.activity).bottomNavView != null) system.enableBottomNavView(((TransactionsActivity) system.activity).bottomNavView, false);

        // Disable sliding up panel layout
        if (VoiceAssistant.fragment.slidingUpPanelLayout != null) VoiceAssistant.fragment.slidingUpPanelLayout.setTouchEnabled(false);

        // Disable back
        ((TransactionsActivity)system.activity).back = false;
    }

    // Enable UI
    public void enableUI() {
        // Enable recyclerView scroll
        if (VoiceAssistant.fragment.rvTransactions != null && disabler != null) VoiceAssistant.fragment.rvTransactions.removeOnItemTouchListener(disabler);

        // Enable bottom navigation view
        if (((TransactionsActivity) system.activity).bottomNavView != null) system.enableBottomNavView(((TransactionsActivity) system.activity).bottomNavView, true);

        // Enable sliding up panel layout
        if (VoiceAssistant.fragment.slidingUpPanelLayout != null) VoiceAssistant.fragment.slidingUpPanelLayout.setTouchEnabled(true);

        // Enable back
        ((TransactionsActivity)system.activity).back = true;
    }

    @NonNull
    @Override
    public TransactionsAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.line_transaction, parent, false);
        return new ViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull TransactionsAdapter.ViewHolder holder, int position) {
        if (VoiceAssistant.isGroup(holder.getItemViewType())) {
            // Group
            Group g = (Group) objects.get(position);

            // Calculate money
            double money = System.account.getMoney() + objects.stream().filter(o -> objects.indexOf(o) < position).mapToDouble(o -> ((Group)o).getValue()).sum();

            // Time ago
            String timeAgo = getDateAndTimeAgo(g.getDate());

            // UI
            holder.txtvParticipants.setVisibility(View.VISIBLE);
            holder.imgvIcon.setImageResource(R.drawable.group_of_users);
            holder.txtvValue.setTextColor(system.activity.getColor(R.color.colorExpense));

            // DATA
            holder.txtvName.setText(g.getName());
            holder.txtvDate.setText(timeAgo);
            holder.txtvValue.setText(System.moneyFormat.format(-g.getValue()));
            holder.txtvMoney.setText(System.moneyFormat.format(money));
            holder.txtvParticipants.setText((g.friends.size() + 1) + " " + system.activity.getString(R.string.participants) + ": " + getPaidMembers(g.friends));
        } else if (VoiceAssistant.isFriend(holder.getItemViewType())) {
            // Friend
            Friend f = (Friend) objects.get(position);

            // Calculate money
            double money = System.account.getMoney() + objects.stream().filter(o -> objects.indexOf(o) < position).mapToDouble(o -> -((Friend)o).payments.stream().mapToDouble(Payment::getValue).sum()).sum();

            // Time ago
            String timeAgo = getDateAndTimeAgo(system.parseLocalDateTimeToString(system.getMaxDate(f.payments)));

            // Sum value
            double sumOfValue = f.payments.stream().mapToDouble(Payment::getValue).sum();

            // UI
            holder.txtvParticipants.setVisibility(View.GONE);
            holder.imgvIcon.setImageResource(R.drawable.individual_user);
            holder.txtvValue.setTextColor(system.activity.getColor(sumOfValue > 0 ? R.color.colorIncome : R.color.colorExpense));

            // DATA
            holder.txtvName.setText(VoiceAssistant.findContactByLookupKey(f.getLookUpKey()));
            holder.txtvDate.setText(timeAgo);
            holder.txtvValue.setText(System.moneyFormat.format(sumOfValue));
            holder.txtvMoney.setText(System.moneyFormat.format(money));
        } else {
            // Payment
            Payment p = (Payment) objects.get(position);

            // Calculate money
            double money = System.account.getMoney() + objects.stream().filter(o -> objects.indexOf(o) < position).mapToDouble(o -> -((Payment)o).getValue()).sum();

            // Time ago
            String timeAgo = getDateAndTimeAgo(p.getDate());

            // Group name
            String groupName = system.getGroupName(p.getGroupId());

            // UI
            holder.txtvParticipants.setVisibility(View.GONE);
            holder.imgvIcon.setImageResource(p.getValue() > 0 ? R.drawable.ic_trending_up_50 : R.drawable.ic_trending_down_50);
            holder.txtvValue.setTextColor(system.activity.getColor(p.getValue() > 0 ? R.color.colorIncome : R.color.colorExpense));

            // DATA
            holder.txtvName.setText(VoiceAssistant.findContactByLookupKey(p.getLookUpKey()));
            holder.txtvDate.setText(timeAgo + (groupName != null ? " (" + groupName + ")" : ""));
            holder.txtvValue.setText(System.moneyFormat.format(p.getValue()));
            holder.txtvMoney.setText(System.moneyFormat.format(money));
        }
    }

    private String getDateAndTimeAgo(String date) {
        String dayOfWeek = LocalDate.parse(date.split(" ")[0]).getDayOfWeek().getDisplayName(TextStyle.FULL, Locale.getDefault());
        return dayOfWeek.substring(0, 1).toUpperCase() + dayOfWeek.substring(1).toLowerCase() + " " + date + " (" + system.getTimeAgo(date) + ")";
    }

    // getPaidMembers
    private String getPaidMembers(ArrayList<Friend> friends) {
        // Initialize members StringBuilder
        StringBuilder members = new StringBuilder();
        if (friends != null && friends.size() > 0  && friends.stream().anyMatch(f -> f.payments.get(0).isTransaction())) {
            for (Friend friend : friends.stream().filter(f -> f.payments.get(0).isTransaction()).collect(Collectors.toCollection(ArrayList::new))) {
                members.append(system.findContactByLookupKey(friend.getLookUpKey()))
                        .append(" (").append(system.getTimeAgo(friend.getPayments().get(0).getDate())).append("), ");
            }
            members = new StringBuilder(members.substring(0, members.length() - 2));
            members.append(" ").append(system.activity.getString(R.string.and)).append(" ").append(system.activity.getString(R.string.me));
        } else members.append(system.activity.getString(R.string.me));
        return members.toString();
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position, @NonNull List<Object> payloads) {
        super.onBindViewHolder(holder, position, payloads);
    }

    @Override
    public int getItemCount() {
        return objects != null ? objects.size() : 0;
    }

    @Override
    public int getItemViewType(int position) {
        return VoiceAssistant.getViewType(objects.get(position));
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imgvIcon, btnDelete;
        TextView txtvName, txtvValue, txtvMoney, txtvDate, txtvParticipants;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            //////////
            // Find //
            //////////
            // btnDelete
            btnDelete = itemView.findViewById(R.id.btnDelete);
            btnDelete.setOnClickListener(v -> {
                if (objects.get(getAdapterPosition()) instanceof Group) deleteGroup(getAdapterPosition());
                else if (objects.get(getAdapterPosition()) instanceof Friend) deleteTransactions(getAdapterPosition());
                else deleteTransaction(getAdapterPosition());
            });

            // imgvIcon
            imgvIcon = itemView.findViewById(R.id.imgvIcon);

            // txtvName
            txtvName = itemView.findViewById(R.id.txtvName);

            // txtvValue
            txtvValue = itemView.findViewById(R.id.txtvValue);

            // txtvMoney
            txtvMoney = itemView.findViewById(R.id.txtvMoney);

            // txtvDate
            txtvDate = itemView.findViewById(R.id.txtvDate);

            // txtvParticipants
            txtvParticipants = itemView.findViewById(R.id.txtvParticipants);

        }
    }
}
