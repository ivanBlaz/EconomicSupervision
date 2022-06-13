package com.devivan.economicsupervision.Adapters.MovementsAdapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.database.sqlite.SQLiteException;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.devivan.economicsupervision.Activities.MovementsActivity;
import com.devivan.economicsupervision.Activities.TransactionsActivity;
import com.devivan.economicsupervision.Objects.Account.Movements.Movement;
import com.devivan.economicsupervision.Objects.Account.Payment.Payment;
import com.devivan.economicsupervision.R;
import com.devivan.economicsupervision.System.System;
import com.devivan.economicsupervision.System.VoiceAssistant;

import java.time.LocalDate;
import java.time.format.TextStyle;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Locale;
import java.util.concurrent.Callable;
import java.util.stream.Collectors;

import static com.devivan.economicsupervision.System.System.FATAL_ERROR;
import static com.devivan.economicsupervision.System.System.account;

class DayViewHolder extends RecyclerView.ViewHolder {
    TextView txtvDayDate;
    public DayViewHolder(@NonNull View itemView) {
        super(itemView);
        //////////
        // Find //
        //////////
        // txtvDayDate
        txtvDayDate = itemView.findViewById(R.id.txtvDayDate);
    }
}

class MovementViewHolder extends RecyclerView.ViewHolder {
    ImageView btnDelete,imgvIcon,imgvLocation;
    TextView txtvConCat,txtvValue,txtvMoney,txtvTime,txtvLocation;

    public MovementViewHolder(@NonNull View itemView, MovementsAdapter adapter) {
        super(itemView);
        //////////
        // Find //
        //////////
        // btnDelete
        btnDelete = itemView.findViewById(R.id.btnDelete);
        btnDelete.setOnClickListener(v -> adapter.deleteMovement(getAdapterPosition()));

        // imgvIcon
        imgvIcon = itemView.findViewById(R.id.imgvIcon);

        // imgvLocation
        imgvLocation = itemView.findViewById(R.id.imgvLocation);

        // txtvConCat
        txtvConCat = itemView.findViewById(R.id.txtvConCat);

        // txtvValue
        txtvValue = itemView.findViewById(R.id.txtvValue);

        // txtvMoney
        txtvMoney = itemView.findViewById(R.id.txtvMoney);

        // txtvTime
        txtvTime = itemView.findViewById(R.id.txtvTime);

        // txtvLocation
        txtvLocation = itemView.findViewById(R.id.txtvLocation);
    }
}

public class MovementsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    System system;
    public ArrayList<Object> objects;

    public MovementsAdapter(System system, ArrayList<Object> objects) {
        this.system = system;
        this.objects = objects;
    }

    ///////////////////////
    // Adapter utilities //
    ///////////////////////

    // try -> deleteMovement
    public void deleteMovement(int pos) {
        // Disable UI
        disableUI();

        // Try to connect to the internet
        system.tryToConnectToTheInternet(() -> {
            if (system.doesTheDatabaseExist() && !system.isDatabaseCorrupt()) letDeleteMovement(pos);
            else { FATAL_ERROR = true; system.activity.onBackPressed(); }
            return null;
        });
    }

    // let -> deleteMovement
    private void letDeleteMovement(int pos) {
        //////////
        // DATA //
        //////////
        // Get movement
        Movement m = (Movement) objects.get(pos);

        // Send request
        system.newRequest(system.config.isBm(), system.accountPos(), "D M " + m.getId(), () -> {
            // Connect to SQLite
            system.connect();

            // Execute request in SQLite
            system.write().execSQL("delete from movements where id = ?", new String[]{String.valueOf(m.getId())});

            // Change money
            account.setMoney(account.getMoney() - m.getValue());
            system.changeMoney();
            /////////////////////

            // Month benefits or expenses?
            if (m.getValue() > 0) account.setMonthBenefits(account.getMonthBenefits() - m.getValue());
            else account.setMonthExpenses(account.getMonthExpenses() + Math.abs(m.getValue()));
            ///////////////////////////////////////////////////////////////////////////////////

            ////////
            // UI //
            ////////
            // Display changes
            ((MovementsActivity) system.activity).displayMoney();

            // Decrease offset
            VoiceAssistant.offset--;

            // Remove necessary movements
            objects.remove(pos);
            if (objects.stream().filter(o -> o instanceof Movement).noneMatch(o -> ((Movement) o).getDate().split(" ")[0].equals(m.getDate().split(" ")[0])))
                objects.removeIf(o -> o instanceof String && ((String)o).split(" ")[0].equals(m.getDate().split(" ")[0]));
            else
                objects.set(
                /* Index */ objects.indexOf(objects.stream().filter(o -> o instanceof String && objects.indexOf(o) < pos).findFirst().get()),
                /* Value */ ((Movement)objects.stream().filter(o -> o instanceof Movement).filter(o -> ((Movement) o).getDate().split(" ")[0].equals(m.getDate().split(" ")[0])).findFirst().get()).getDate()
                );

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
        ((MovementsActivity) system.activity).rvMovements.addOnItemTouchListener(disabler);

        // Disable sliding up panel layout
        ((MovementsActivity) system.activity).slidingUpPanelLayout.setTouchEnabled(false);

        // Disable back
        ((MovementsActivity)system.activity).back = false;
    }

    // Enable UI
    public void enableUI() {
        // Enable recyclerView scroll
        if (disabler != null) ((MovementsActivity) system.activity).rvMovements.removeOnItemTouchListener(disabler);

        // Enable sliding up panel layout
        ((MovementsActivity) system.activity).slidingUpPanelLayout.setTouchEnabled(true);

        // Enable back
        ((MovementsActivity)system.activity).back = true;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (VoiceAssistant.isDate(viewType)) return new DayViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.line_day, parent, false));
        else return new MovementViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.line_movement, parent, false), this);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof DayViewHolder) bindDayViewHolder((DayViewHolder) holder, (String) objects.get(position));
        else bindMovementViewHolder((MovementViewHolder) holder, (Movement) objects.get(position), position);
    }

    @SuppressLint("SetTextI18n")
    private void bindDayViewHolder(DayViewHolder holder, String date) {
        String dayOfWeek = LocalDate.parse(date.split(" ")[0]).getDayOfWeek().getDisplayName(TextStyle.FULL, Locale.getDefault());
        String timeAgo = System.getLocalDateTimeNow().split(" ")[0].equals(date.split(" ")[0]) ?
            system.activity.getString(R.string.Today)
            :
            system.getTimeAgo(date.split(" ")[0] + " 00:00:00");
        holder.txtvDayDate.setText(dayOfWeek.substring(0, 1).toUpperCase() + dayOfWeek.substring(1).toLowerCase() + " " + date.split(" ")[0] + " (" + timeAgo + ")");
    }

    @SuppressLint({"Recycle", "SetTextI18n"})
    private void bindMovementViewHolder(MovementViewHolder holder, Movement movement, int position) {
        // UI
        holder.txtvValue.setTextColor(system.activity.getColor(movement.getValue() > 0 ? R.color.colorIncome : R.color.colorExpense));

        // Name + Image
        if (movement.getSubCategoryId() != null) {
            holder.txtvConCat.setText(VoiceAssistant.getCategoryName(movement.getSubCategoryId()));
            holder.imgvIcon.setImageResource(system.activity.getResources().obtainTypedArray(R.array.imgCategories)
                    .getResourceId(Integer.parseInt(movement.getSubCategoryId().split("#")[0]),-1));
        } else if (movement.getConceptId() != -1) {
            holder.txtvConCat.setText(VoiceAssistant.getConceptName(movement.getConceptId()));
            holder.imgvIcon.setImageResource(movement.getType().equals("I") ? R.drawable.expense : R.drawable.income);
        }

        // txtvTime
        holder.txtvTime.setText(movement.getDate().split(" ")[1]);

        // Get location
        String location = movement.getLocation() != null ? movement.getLocation().replaceAll("·",", ") : null;

        // txtvLocation
        if (location != null) {
            holder.imgvLocation.setVisibility(View.VISIBLE);
            holder.txtvLocation.setVisibility(View.VISIBLE);
            holder.txtvLocation.setText(location);
            holder.txtvConCat.setText(holder.txtvConCat.getText().toString() + " (" + system.getTimeAgo(movement.getDate()) + ")");
        }
        else {
            holder.txtvTime.setText(holder.txtvTime.getText().toString() + " (" + system.getTimeAgo(movement.getDate()) + ")");
            holder.imgvLocation.setVisibility(View.INVISIBLE);
            holder.txtvLocation.setVisibility(View.INVISIBLE);
        }

        // txtvValue
        holder.txtvValue.setText(System.moneyFormat.format(movement.getValue()));

        // Calculate money
        double money = System.account.getMoney() + objects.stream().filter(o -> o instanceof Movement && objects.indexOf(o) < position).mapToDouble(o -> -((Movement)o).getValue()).sum();

        // txtvMoney
        holder.txtvMoney.setText(System.moneyFormat.format(money));
    }

    @Override
    public int getItemCount() {
        return objects != null ? objects.size() : 0;
    }

    @Override
    public int getItemViewType(int position) {
        return VoiceAssistant.getViewType(objects.get(position));
    }
}