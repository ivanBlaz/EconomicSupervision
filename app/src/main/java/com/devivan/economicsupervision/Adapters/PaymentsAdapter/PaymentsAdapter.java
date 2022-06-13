package com.devivan.economicsupervision.Adapters.PaymentsAdapter;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.LruCache;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.devivan.economicsupervision.Adapters.FriendsAdapter.FriendsAdapter;
import com.devivan.economicsupervision.BuildConfig;
import com.devivan.economicsupervision.Objects.Account.Friend.Friend;
import com.devivan.economicsupervision.Objects.Account.Payment.Payment;
import com.devivan.economicsupervision.System.System;
import com.devivan.economicsupervision.Fragments.HaveFragment;
import com.devivan.economicsupervision.Fragments.ShouldFragment;
import com.devivan.economicsupervision.R;

import java.io.ByteArrayOutputStream;
import java.util.List;
import java.util.Objects;

import static com.devivan.economicsupervision.System.System.FATAL_ERROR;
import static com.devivan.economicsupervision.System.System.account;
import static com.devivan.economicsupervision.System.System.getSystem;

class MyDiffUtilCallback extends DiffUtil.Callback {
    private final System system;
    private final List<Payment> oldPayments;
    private final List<Payment> newPayments;

    public MyDiffUtilCallback(System system, List<Payment> oldPayments, List<Payment> newPayments) {
        this.system = system;
        this.oldPayments = oldPayments;
        this.newPayments = newPayments;
    }

    @Override
    public int getOldListSize() {
        return oldPayments != null ? oldPayments.size() : 0;
    }

    @Override
    public int getNewListSize() {
        return newPayments != null ? newPayments.size() : 0;
    }

    @Override
    public boolean areItemsTheSame(int oldPosition, int newPosition) {
        return oldPosition == newPosition;
    }

    @Override
    public boolean areContentsTheSame(int oldPosition, int newPosition) {
        Payment newPayment = newPayments.get(newPosition);
        Payment oldPayment = oldPayments.get(oldPosition);

        if (newPayment == null && oldPayment == null) return newPayments.stream().filter(Objects::nonNull).mapToDouble(Payment::getValue).sum() == oldPayments.stream().filter(Objects::nonNull).mapToDouble(Payment::getValue).sum();
        else return newPayment.compareTo(oldPayment) == 0;
    }

    @Nullable
    @Override
    public Object getChangePayload(int oldItemPosition, int newItemPosition) {
        Payment newPayment = newPayments.get(newItemPosition);
        Payment oldPayment = oldPayments.get(oldItemPosition);

        Bundle bundle = new Bundle();

        if (newPayment == null && oldPayment == null) {
            double oldSum = oldPayments.stream().filter(Objects::nonNull).mapToDouble(Payment::getValue).sum();
            double newSum = newPayments.stream().filter(Objects::nonNull).mapToDouble(Payment::getValue).sum();
            if (newSum != oldSum) bundle.putDouble("sum", newSum);
        } else {
            if (!newPayment.getDate().equals(oldPayment.getDate()) || newPayment.getGroupId() != oldPayment.getGroupId()) {
                // Get time ago
                String timeAgo = system.getTimeAgo(newPayment.getDate());

                // Get group name
                String groupName = system.getGroupName(newPayment.getGroupId());

                // Put time_group
                bundle.putString("time_group", timeAgo + (groupName != null ? " (" + groupName + ")": ""));
            }

            if (newPayment.getValue() != oldPayment.getValue()) bundle.putDouble("value", newPayment.getValue());
        }

        if (bundle.size() == 0)
            return null;

        return bundle;
    }
}

class SumPaymentsViewHolder extends RecyclerView.ViewHolder {

    ImageView btnSend;
    TextView txtvName;
    TextView txtvValue;

    public SumPaymentsViewHolder(@NonNull View itemView, RecyclerView recyclerView) {
        super(itemView);
        //////////
        // Find //
        //////////
        // btnSend
        btnSend = itemView.findViewById(R.id.btnSend);
        btnSend.setOnClickListener(v -> sharePayments(recyclerView));

        // txtvName
        txtvName = itemView.findViewById(R.id.txtvName);

        // txtvValue
        txtvValue = itemView.findViewById(R.id.txtvValue);
        //////////////////////////////////////////////////
    }

    private void sharePayments(RecyclerView recyclerView) {
        String msg = null;
        String minDate, maxDate;
        if (recyclerView != null) {
            PaymentsAdapter paymentsAdapter = (PaymentsAdapter) recyclerView.getAdapter();
            if (paymentsAdapter != null) {
                 minDate = paymentsAdapter.friendsAdapter.system.parseLocalDateTimeToString(paymentsAdapter.friendsAdapter.system.getMinDate(paymentsAdapter.friend.payments)).split(" ")[0];
                 maxDate = paymentsAdapter.friendsAdapter.system.parseLocalDateTimeToString(paymentsAdapter.friendsAdapter.system.getMaxDate(paymentsAdapter.friend.payments)).split(" ")[0];
                 if (minDate != null && maxDate != null) {
                     if (minDate.equals(maxDate)) msg = minDate.equals(maxDate) ? minDate : minDate + " - " + maxDate;
                 }
                Bitmap bitmap = recyclerView != null ? getScreenshotFromRecyclerView(recyclerView) : null;
                if (bitmap != null) share(bitmap, msg, paymentsAdapter.friendsAdapter.system);
            }
        }
    }

    public Bitmap getScreenshotFromRecyclerView(RecyclerView view) {
        RecyclerView.Adapter adapter = view.getAdapter();
        Bitmap bigBitmap = null;
        if (adapter != null) {
            int size = adapter.getItemCount();
            int height = 0;
            Paint paint = new Paint();
            int iHeight = 0;
            final int maxMemory = (int) (Runtime.getRuntime().maxMemory() / 1024);

            // Use 1/8th of the available memory for this memory cache.
            final int cacheSize = maxMemory / 8;
            LruCache<String, Bitmap> bitmaCache = new LruCache<>(cacheSize);
            for (int i = 0; i < size; i++) {
                RecyclerView.ViewHolder holder = adapter.createViewHolder(view, adapter.getItemViewType(i));
                adapter.onBindViewHolder(holder, i);
                holder.itemView.measure(View.MeasureSpec.makeMeasureSpec(view.getWidth(), View.MeasureSpec.EXACTLY),
                        View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
                holder.itemView.layout(0, 0, holder.itemView.getMeasuredWidth(), holder.itemView.getMeasuredHeight());
                holder.itemView.setDrawingCacheEnabled(true);
                holder.itemView.buildDrawingCache();
                Bitmap drawingCache = holder.itemView.getDrawingCache();
                if (drawingCache != null) bitmaCache.put(String.valueOf(i), drawingCache);
                height += holder.itemView.getMeasuredHeight();
            }

            bigBitmap = Bitmap.createBitmap(view.getMeasuredWidth(), height, Bitmap.Config.ARGB_8888);
            Canvas bigCanvas = new Canvas(bigBitmap);
            bigCanvas.drawColor(view.getContext().getColor(R.color.colorApp));

            for (int i = 0; i < size; i++) {
                Bitmap bitmap = bitmaCache.get(String.valueOf(i));
                bigCanvas.drawBitmap(bitmap, 0f, iHeight, paint);
                iHeight += bitmap.getHeight();
                bitmap.recycle();
            }

        }
        return bigBitmap;
    }

    private void share(Bitmap bitmap, String string, System system) {
        // WhatsApp package string
        String pack = "com.whatsapp";
        try {
            // Build image URI
            ByteArrayOutputStream bytes = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
            String path = MediaStore.Images.Media.insertImage(itemView.getContext().getContentResolver(), bitmap, System.getLocalDateTimeNow(), null);
            Uri imageUri = Uri.parse(path);
            ///////////////////////////////

            // Send WhatsApp
            Intent waIntent = new Intent(Intent.ACTION_SEND);
            waIntent.setType("image/*");
            waIntent.setPackage(pack);
            waIntent.putExtra(android.content.Intent.EXTRA_STREAM, imageUri);
            if (string != null) waIntent.putExtra(Intent.EXTRA_TEXT, string);
            itemView.getContext().startActivity(Intent.createChooser(waIntent, null));
            ///////////////////////////////////////////////////////////////////////////////
        } catch (Exception e) {
            system.activity.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + pack)));
            system.toast(system.activity, System.WARNING_TOAST, system.activity.getString(R.string.WhatsApp_is_not_installed_on_this_device), Toast.LENGTH_SHORT); }
    }
}

class PaymentViewHolder extends RecyclerView.ViewHolder {

    CheckBox cbPaid;
    TextView txtvName;
    TextView txtvValue;
    ImageView btnDelete;

    @SuppressLint("SetTextI18n")
    public PaymentViewHolder(PaymentsAdapter paymentsAdapter, @NonNull View itemView) {
        super(itemView);
        //////////
        // Find //
        //////////
        // cbPaid
        cbPaid = itemView.findViewById(R.id.cbPaid);
        cbPaid.setOnCheckedChangeListener((buttonView, isChecked) -> { if (isChecked) paymentsAdapter.payPayment(getAdapterPosition()); });

        // txtvName
        txtvName = itemView.findViewById(R.id.txtvName);

        // txtvValue
        txtvValue = itemView.findViewById(R.id.txtvValue);

        // btnDelete
        btnDelete = itemView.findViewById(R.id.btnDelete);
        btnDelete.setOnClickListener(view -> paymentsAdapter.deletePayment(getAdapterPosition()));
        //////////////////////////////////////////////////////////////////////////////////////////
    }
}

public class PaymentsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    // Data
    Friend friend;
    int pos;
    ////////

    // UI
    FriendsAdapter friendsAdapter;
    RecyclerView rvPayments;
    Fragment fragment;
    //////////////////

    // View types
    final int VIEW_TYPE_SUM_PAYMENTS = 0;
    final int VIEW_TYPE_PAYMENT = 1;
    ////////////////////////////////

    public PaymentsAdapter(FriendsAdapter friendsAdapter, RecyclerView rvPayments, int pos) {
        this.friendsAdapter = friendsAdapter;
        this.rvPayments = rvPayments;
        this.fragment = friendsAdapter.type.equals("HA") ? ((HaveFragment)friendsAdapter.fragment.getParentFragment()) : ((ShouldFragment)friendsAdapter.fragment.getParentFragment());
        friend = friendsAdapter.friends.get(pos);
        this.pos = pos;
    }

    ///////////////////////
    // Adapter utilities //
    ///////////////////////
    // getFriend
    public Friend getFriend() {
        if (pos < friendsAdapter.friends.size())
            if (friendsAdapter.friends.get(pos).getLookUpKey().equals(friend.getLookUpKey()))
                return friendsAdapter.friends.get(pos);
        return null;
    }

    // refreshFriend
    public void refreshFriend(Friend friend) {
        if (friend != null) {
            // Calculate data diff
            DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(new MyDiffUtilCallback(friendsAdapter.system, this.friend.payments, friend.payments));
            diffResult.dispatchUpdatesTo(this);

            // Refresh list
            this.friend.payments.clear();
            this.friend.payments.addAll(friend.payments);
        } else friendsAdapter.system.dismissDialog(friendsAdapter.system.recyclerDialog);
    }

    ////////
    // UI //
    ////////
    RecyclerView.OnItemTouchListener disabler;

    // Disable UI
    private void disableUI() {
        // Initialize RecyclerViewDisabler
        if (disabler == null) disabler = new System.RecyclerViewDisabler();

        // Disable recyclerView scroll
        rvPayments.addOnItemTouchListener(disabler);

        // Dialog not cancelable
        friendsAdapter.system.recyclerDialog.setCancelable(false);
    }

    // Enable UI
    private void enableUI() {
        // Enable recyclerView scroll
        rvPayments.removeOnItemTouchListener(disabler);

        // Dialog cancelable
        friendsAdapter.system.recyclerDialog.setCancelable(true);
    }

    // try -> payPayment
    public void payPayment(int pos) {
        // Disable UI
        disableUI();

        // Try to connect to the internet
        friendsAdapter.system.tryToConnectToTheInternet(() -> {
            if (friendsAdapter.system.doesTheDatabaseExist() && !friendsAdapter.system.isDatabaseCorrupt()) letPayPayment(pos);
            else { FATAL_ERROR = true; friendsAdapter.system.activity.onBackPressed(); }
            return null;
        });
    }

    // let -> payPayment
    private void letPayPayment(int pos) {
        //////////
        // DATA //
        //////////
        // Get payment
        Payment payment = friend.getPayments().get(pos);

        // Initialize now date
        String now = System.getLocalDateTimeNow();

        // Send request
        friendsAdapter.system.newRequest(friendsAdapter.system.config.isBm(), friendsAdapter.system.accountPos(), "U " + friendsAdapter.type + " " + now.replace(" ","_") + " " + payment.getId(), () -> {
            // Connect to SQLite
            friendsAdapter.system.connect();

            // Execute request in SQLite
            friendsAdapter.system.write().execSQL("update movements set type = 'TR', date = ? where id = ?",
                    new String[]{now, String.valueOf(payment.getId())});

            // Set money
            account.setMoney(account.getMoney() + payment.getValue());

            // Change money in SQLite
            friendsAdapter.system.changeMoney();
            ////////////////////////////////////

            // Month benefits or expenses?
            if (payment.getValue() > 0) account.setMonthBenefits(account.getMonthBenefits() + payment.getValue());
            else account.setMonthExpenses(account.getMonthExpenses() + payment.getValue());
            ///////////////////////////////////////////////////////////////////////////////

            ///////////////
            // UI DESIGN //
            ///////////////
            // Change transact && display
            account.setTransact(account.getTransact() + payment.getValue());
            friendsAdapter.bottomNavView.getMenu().findItem(R.id.nav_transaction).setTitle(System.moneyFormat.format(account.getTransact()));
            /////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

            // Change should or have && display
            if (friendsAdapter.type.equals("HA")) {
                account.setHave(account.getHave() - payment.getValue());
                friendsAdapter.bottomNavView.getMenu().findItem(R.id.nav_have).setTitle(System.moneyFormat.format(account.getHave()));
            }
            else {
                account.setShould(account.getShould() - payment.getValue());
                friendsAdapter.bottomNavView.getMenu().findItem(R.id.nav_should).setTitle(System.moneyFormat.format(account.getShould()));
            }

            // Refresh list -> friends
            friendsAdapter.refreshList(friendsAdapter.system.getFriends(friendsAdapter.type));

            // Refresh list -> payments
            refreshFriend(getFriend());

            // Refresh list -> groups
            if (friendsAdapter.type.equals("HA") && payment.getGroupId() != -1) ((HaveFragment) fragment).haveGroupFragment.groupsAdapter.refreshList(friendsAdapter.system.getGroups(false));

            // Enable UI
            enableUI();
            return null;
        });
    }

    // try -> deletePayment
    public void deletePayment(int pos) {
        // Disable UI
        disableUI();

        // Try to connect to the internet
        friendsAdapter.system.tryToConnectToTheInternet(() -> {
            if (friendsAdapter.system.doesTheDatabaseExist() && !friendsAdapter.system.isDatabaseCorrupt()) letDeletePayment(pos);
            else { FATAL_ERROR = true; friendsAdapter.system.activity.onBackPressed(); }
            return null;
        });
    }

    private void letDeletePayment(int pos) {
        //////////
        // DATA //
        //////////
        // Get friend
        Payment payment = friend.getPayments().get(pos);

        // Send request
        friendsAdapter.system.newRequest(friendsAdapter.system.config.isBm(), friendsAdapter.system.accountPos(), "D " + friendsAdapter.type + " " + payment.getId(), () -> {
            // Connect to SQLite
            friendsAdapter.system.connect();

            // Execute request in SQLite
            friendsAdapter.system.write().execSQL("delete from movements where id = ?", new String[]{String.valueOf(payment.getId())});

            ///////////////
            // UI DESIGN //
            ///////////////
            // Change should or have && display
            if (friendsAdapter.type.equals("HA")) {
                account.setHave(account.getHave() - payment.getValue());
                friendsAdapter.bottomNavView.getMenu().findItem(R.id.nav_have).setTitle(System.moneyFormat.format(account.getHave()));
            }
            else {
                account.setShould(account.getShould() - payment.getValue());
                friendsAdapter.bottomNavView.getMenu().findItem(R.id.nav_should).setTitle(System.moneyFormat.format(account.getShould()));
            }

            // Refresh list -> friends
            friendsAdapter.refreshList(friendsAdapter.system.getFriends(friendsAdapter.type));

            // Refresh list -> payments
            refreshFriend(getFriend());

            // Refresh list -> groups
            if (friendsAdapter.type.equals("HA") && payment.getGroupId() != -1) ((HaveFragment) fragment).haveGroupFragment.groupsAdapter.refreshList(friendsAdapter.system.getGroups(false));

            // Enable UI
            enableUI();
            return null;
        });
    }
    //////////////////////////////////////////////////////////

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return viewType == VIEW_TYPE_SUM_PAYMENTS ?
                new SumPaymentsViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.line_sum_payments, parent, false), rvPayments)
                :
                new PaymentViewHolder(this, LayoutInflater.from(parent.getContext()).inflate(R.layout.line_payment, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder.getItemViewType() == VIEW_TYPE_SUM_PAYMENTS) bindSumPayments((SumPaymentsViewHolder) holder);
        else bindPayment((PaymentViewHolder) holder, friend.payments.get(position));
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position, @NonNull List<Object> payloads) {
        if (payloads.isEmpty())
        super.onBindViewHolder(holder, position, payloads);
        else {
            if (holder instanceof PaymentViewHolder) {
                ((PaymentViewHolder) holder).cbPaid.setChecked(false);
                ((PaymentViewHolder) holder).txtvValue.setTextColor(friendsAdapter.context.getColor(friendsAdapter.type.equals("HA") ? R.color.colorIncome : R.color.colorExpense));

            } else {
                ((SumPaymentsViewHolder) holder).txtvValue.setTextColor(friendsAdapter.context.getColor(friendsAdapter.type.equals("HA") ? R.color.colorIncome : R.color.colorExpense));
            }
            Bundle bundle = (Bundle) payloads.get(0);
            for (String key : bundle.keySet()) {
                if (key.equals("time_group")) if (holder instanceof PaymentViewHolder) {
                    ((PaymentViewHolder) holder).txtvName.setText(bundle.getString("time_group"));
                }
                if (key.equals("value")) if (holder instanceof PaymentViewHolder) {
                    ((PaymentViewHolder) holder).txtvValue.setText(System.moneyFormat.format(bundle.getDouble("value")));
                }
                if (key.equals("sum")) if (holder instanceof SumPaymentsViewHolder) {
                    ((SumPaymentsViewHolder) holder).txtvValue.setText(System.moneyFormat.format(bundle.getDouble("sum")));
                }
            }
        }
    }

    private void bindSumPayments(SumPaymentsViewHolder holder) {
        ///////////////
        // UI Design //
        ///////////////
        holder.txtvValue.setTextColor(friendsAdapter.context.getColor(friendsAdapter.type.equals("HA") ? R.color.colorIncome : R.color.colorExpense));
        //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

        /////////////
        // UI Data //
        /////////////
        // Set title
        holder.txtvName.setText(friendsAdapter.context.getString(friendsAdapter.type.equals("HA") ? R.string.to_receive : R.string.to_pay));

        // Set sum of values
        holder.txtvValue.setText(System.moneyFormat.format(friendsAdapter.system.getSumOfValues(friend.payments)));
    }

    @SuppressLint("SetTextI18n")
    private void bindPayment(PaymentViewHolder holder, Payment payment) {
        ///////////////
        // UI Design //
        ///////////////
        holder.cbPaid.setChecked(false);
        holder.txtvValue.setTextColor(friendsAdapter.context.getColor(friendsAdapter.type.equals("HA") ? R.color.colorIncome : R.color.colorExpense));
        //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

        /////////////
        // UI Data //
        /////////////
        // Get time ago
        String timeAgo = friendsAdapter.system.getTimeAgo(payment.getDate());

        // Get group name
        String groupName = friendsAdapter.system.getGroupName(payment.getGroupId());

        // Set time ago and group name
        holder.txtvName.setText(timeAgo + (groupName != null ? " (" + groupName + ")": ""));

        // Set value
        holder.txtvValue.setText(System.moneyFormat.format(payment.getValue()));
    }

    @Override
    public int getItemViewType(int position) {
        return friend.payments.get(position) == null ? VIEW_TYPE_SUM_PAYMENTS : VIEW_TYPE_PAYMENT;
    }

    @Override
    public int getItemCount() {
        return friend.payments != null ? friend.payments.size() : 0;
    }
}
