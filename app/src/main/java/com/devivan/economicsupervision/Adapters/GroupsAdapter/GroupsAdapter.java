package com.devivan.economicsupervision.Adapters.GroupsAdapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.devivan.economicsupervision.Objects.Account.Friend.Friend;
import com.devivan.economicsupervision.Objects.Account.Group.Group;
import com.devivan.economicsupervision.UtilityClasses.CustomViewPager;
import com.devivan.economicsupervision.System.System;
import com.devivan.economicsupervision.Fragments.HaveFragment;
import com.devivan.economicsupervision.Fragments.HaveGroupFragment;
import com.devivan.economicsupervision.R;
import com.devivan.economicsupervision.Activities.TransactionsActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.tabs.TabLayout;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static com.devivan.economicsupervision.System.System.account;

class MyDiffUtilCallback extends DiffUtil.Callback {
    private final System system;
    private final List<Group> oldGroups;
    private final List<Group> newGroups;

    public MyDiffUtilCallback(System system, List<Group> oldGroups, List<Group> newGroups) {
        this.system = system;
        this.oldGroups = oldGroups;
        this.newGroups = newGroups;
    }

    @Override
    public int getOldListSize() {
        return oldGroups != null ? oldGroups.size() : 0;
    }

    @Override
    public int getNewListSize() {
        return newGroups != null ? newGroups.size() : 0;
    }

    @Override
    public boolean areItemsTheSame(int oldPosition, int newPosition) {
        return oldPosition == newPosition;
    }

    @Override
    public boolean areContentsTheSame(int oldPosition, int newPosition) {
        int result = newGroups.get(newPosition).compareTo(oldGroups.get(oldPosition));
        return result == 0;
    }

    @Nullable
    @Override
    public Object getChangePayload(int oldItemPosition, int newItemPosition) {
        Group newGroup = newGroups.get(newItemPosition);
        Group oldGroup = oldGroups.get(oldItemPosition);

        Bundle bundle = new Bundle();

        if (!newGroup.getName().equals(oldGroup.getName()))
            bundle.putString("name", newGroup.getName());

        if (newGroup.getDate().equals(oldGroup.getDate()) || newGroup.getFriends() != oldGroup.getFriends()) {
            // Get time ago
            String timeAgo = system.getTimeAgo(newGroup.getDate());

            // Get participants
            String participants = "(" + ((newGroup.friends != null ? newGroup.friends.size() : 0) + 1) + " " + system.activity.getString(R.string.participants) + ")";

            // Put time_members
            bundle.putString("time_members", timeAgo + " " + participants);

            if (newGroup.getFriends() != oldGroup.getFriends())
                bundle.putParcelable("group", newGroup);
        }

        double oldNotPaid = oldGroup.getTotal() - oldGroup.getPaid();
        double newNotPaid = newGroup.getTotal() - newGroup.getPaid();
        if (newNotPaid != oldNotPaid)
            bundle.putDouble("value", newNotPaid);

        double oldMoney = oldGroup.getTotal() - oldGroup.getPaid() + account.getMoney();
        double newMoney = newGroup.getTotal() - newGroup.getPaid() + account.getMoney();
        if (newMoney != oldMoney)
            bundle.putDouble("money", newMoney);

        if (bundle.size() == 0)
            return null;

        return bundle;
    }
}

public class GroupsAdapter extends RecyclerView.Adapter<GroupsAdapter.ViewHolder> {

    // Data
    Context context;
    System system;
    public ArrayList<Group> groups;
    ////////////////////////

    // UI
    Fragment fragment;
    RecyclerView rvGroups;
    BottomNavigationView bottomNavView;
    CustomViewPager viewPager;
    TabLayout tabLayout;
    SlidingUpPanelLayout slidingUpPanelLayout;
    //////////////////////////////////////////

    public GroupsAdapter(Context context, Fragment fragment, System system, RecyclerView rvGroups) {
        this.context = context;
        this.fragment = fragment;
        this.system = system;
        this.rvGroups = rvGroups;
        this.groups = new ArrayList<>();

        //////////////
        // Controls //
        //////////////
        // Bottom navigation view
        bottomNavView = fragment.getActivity() != null ? ((TransactionsActivity) fragment.getActivity()).bottomNavView : null;
        //
        // View pager & tab layout [ Have ]
        viewPager = fragment.getParentFragment() != null ? ((HaveFragment) fragment.getParentFragment()).viewPager : null;
        tabLayout = fragment.getParentFragment() != null ? ((HaveFragment) fragment.getParentFragment()).tabLayout : null;
        //
        // Slide up panel layout
        slidingUpPanelLayout = ((HaveGroupFragment) fragment).slideUpNewGroup;
    }

    ///////////////////////
    // Adapter utilities //
    ///////////////////////

    // refreshList
    public void refreshList(ArrayList<Group> groups) {
        // Calculate data diff
        DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(new MyDiffUtilCallback(system, this.groups, groups));
        diffResult.dispatchUpdatesTo(this);

        // Refresh list
        this.groups.clear();
        this.groups.addAll(groups);

        // Display
        if (groups.size() > 0) displayRecyclerView();
        else displayNewGroupLayout();
    }

    private void displayNewGroupLayout() {
        // Change visibility
        HaveGroupFragment hgf = (HaveGroupFragment) fragment;
        hgf.v.findViewById(R.id.clNewGroup).setVisibility(View.VISIBLE);
        hgf.v.findViewById(R.id.rvGroups).setVisibility(View.GONE);

        // Set text
        ((TextView) hgf.v.findViewById(R.id.txtvNewPayment)).setText(context.getString(R.string.new_group));

        // Animations?
        if (!TransactionsActivity.animations.get(3)) {
            TransactionsActivity.animations.put(3, true);
            Animation slideInLeft = AnimationUtils.loadAnimation(context, R.anim.slide_in_left);
            Animation slideInRight = AnimationUtils.loadAnimation(context, R.anim.slide_in_right);
            hgf.v.findViewById(R.id.txtvNewPayment).setAnimation(slideInLeft);
            hgf.v.findViewById(R.id.imgvNewPayment).setAnimation(slideInRight);
            hgf.v.findViewById(R.id.imgvPlusNewPayment).setVisibility(View.GONE);
            slideInLeft.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {

                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    Animation zoomIn = AnimationUtils.loadAnimation(context, R.anim.zoom_in);
                    hgf.v.findViewById(R.id.imgvPlusNewPayment).setVisibility(View.VISIBLE);
                    hgf.v.findViewById(R.id.imgvPlusNewPayment).setAnimation(zoomIn);
                }

                @Override
                public void onAnimationRepeat(Animation animation) {

                }
            });
        }

        // Get layout + click listener
        ConstraintLayout layout = hgf.v.findViewById(R.id.clNewGroup);
        layout.setOnClickListener(v -> hgf.slideUpNewGroup.setPanelState(SlidingUpPanelLayout.PanelState.EXPANDED));
    }

    private void displayRecyclerView() {
        HaveGroupFragment hgf = (HaveGroupFragment) fragment;
        hgf.v.findViewById(R.id.clNewGroup).setVisibility(View.GONE);
        hgf.v.findViewById(R.id.rvGroups).setVisibility(View.VISIBLE);
    }

    // getToPayMembers
    private String getToPayMembers(ArrayList<Friend> friends) {
        // Initialize members StringBuilder
        StringBuilder members = new StringBuilder();
        if (friends != null && friends.size() > 0  && friends.stream().anyMatch(f -> !f.payments.get(0).isTransaction())) {
            for (Friend friend : friends.stream().filter(f -> !f.payments.get(0).isTransaction()).collect(Collectors.toCollection(ArrayList::new))) {
                members.append(system.findContactByLookupKey(friend.getLookUpKey())).append(", ");
            }
            members = new StringBuilder(members.substring(0, members.length() - 2));
            int start = members.lastIndexOf(", ");
            if (start != -1) {
                String strStart = members.substring(0, start);
                String strMiddle = " " + context.getString(R.string.and) + " ";
                String strEnd = members.substring(start + 2);
                members = new StringBuilder();
                members.append(strStart).append(strMiddle).append(strEnd);
            }
        } else members.append(context.getString(R.string.nobody));
        return members.toString();
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
            members.append(" ").append(context.getString(R.string.and)).append(" ").append(context.getString(R.string.me));
        } else members.append(context.getString(R.string.me));
        return members.toString();
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
        rvGroups.addOnItemTouchListener(disabler);

        // Disable bottom navigation view
        if (bottomNavView != null) system.enableBottomNavView(bottomNavView, false);

        // Disable pagers pagination
        if (viewPager != null && tabLayout != null) system.enablePagersPagination(viewPager, tabLayout, false);

        // Disable slide up panel layout
        slidingUpPanelLayout.setTouchEnabled(false);

        // Disable back
        ((TransactionsActivity) system.activity).back = false;
    }

    // Enable UI
    private void enableUI() {
        // Enable recyclerView scroll
        rvGroups.removeOnItemTouchListener(disabler);

        // Enable bottom navigation view
        if (bottomNavView != null) system.enableBottomNavView(bottomNavView, true);

        // Enable pagers pagination
        if (viewPager != null && tabLayout != null) system.enablePagersPagination(viewPager, tabLayout, true);

        // Dialog cancelable
        if (system.keyboardDialog != null) system.keyboardDialog.setCancelable(true);

        // Enable slide up panel layout
        slidingUpPanelLayout.setTouchEnabled(true);

        // Enable back
        ((TransactionsActivity) system.activity).back = true;
    }

    // try -> changeGroupName
    private void changeGroupName(int pos, String newName) {
        // Try to connect to the internet
        system.tryToConnectToTheInternet(() -> {
            if (system.doesTheDatabaseExist() && !system.isDatabaseCorrupt()) letChangeGroupName(pos, newName);
            else { System.FATAL_ERROR = true; system.activity.onBackPressed(); }
            return null;
        });
    }

    // let -> changeGroupName
    private void letChangeGroupName(int pos, String newName) {
        // Initialize group
        Group group = groups.get(pos);

        // Initialize old name
        String oldName = group.getName();

        // Are names different?
        if (!oldName.equals(newName)) {
            // Set dialog non-cancelable
            system.keyboardDialog.setCancelable(false);

            ///////////////////////
            // Change group name //
            ///////////////////////
            // Send request
            system.newRequest(system.config.isBm(), system.accountPos(), "U GP " + group.getId() + " " + newName.replace(" ","_"), () -> {
                // Connect to SQLite
                system.connect();

                // Execute request in SQLite
                system.write().execSQL("update movements set location = ? where id = ?", new String[]{newName, String.valueOf(group.getId())});

                // Change name
                group.setName(newName);

                // Refresh friends
                if (fragment.getParentFragment() != null) {
                    ((HaveFragment)fragment.getParentFragment()).haveIndividualFragment.friendsAdapter.refreshList(system.getFriends("HA"));
                }

                // Notify
                notifyItemChanged(pos);

                // Enable UI
                enableUI();
                return null;
            });
        } else {
            // Notify
            notifyItemChanged(pos);

            // Enable UI
            enableUI();
        }
    }

    // try -> deleteGroup
    private void deleteGroup(int pos) {
        // Disable UI
        disableUI();

        // Try to connect to the internet
        system.tryToConnectToTheInternet(() -> {
            if (system.doesTheDatabaseExist() && !system.isDatabaseCorrupt()) letDeleteGroup(pos);
            else { System.FATAL_ERROR = true; system.activity.onBackPressed(); }
            return null;
        });
    }

    // let -> deleteGroup
    private void letDeleteGroup(int pos) {
        //////////
        // DATA //
        //////////
        // Initialize group
        Group group = groups.get(pos);

        // Send request
        system.newRequest(system.config.isBm(), system.accountPos(), "D GP " + group.getId(), () -> {
            // Connect to SQLite
            system.connect();

            // Execute request in SQLite
            system.write().execSQL("delete from movements where id = ? or (type in('HA','TR') and conceptId = ? and accountId = ?)",
                    new String[]{String.valueOf(group.getId()), String.valueOf(group.getId()), system.def});

            // Update money
            account.setMoney(account.getMoney() - group.getPaid());
            system.changeMoney();
            /////////////////////

            ///////////////
            // UI DESIGN //
            ///////////////
            // Change have && display
            account.setHave(account.getHave() - (group.getTotal() - group.getPaid()));
            bottomNavView.getMenu().findItem(R.id.nav_have).setTitle(System.moneyFormat.format(account.getHave()));
            ///////////////////////////////////////////////////////////////////////////////////////////////////////

            // Change transact & display
            account.setTransact(account.getTransact() - group.getPaid());
            bottomNavView.getMenu().findItem(R.id.nav_transaction).setTitle(System.moneyFormat.format(account.getTransact()));
            //////////////////////////////////////////////////////////////////////////////////////////////////////////////////

            // Refresh list -> groups
            refreshList(system.getGroups(false));

            // Refresh list -> friends
            if (fragment.getParentFragment() != null) {
                ((HaveFragment)fragment.getParentFragment()).haveIndividualFragment.friendsAdapter.refreshList(system.getFriends("HA"));
            }

            // Enable UI
            enableUI();
            return null;
        });
    }

    @NonNull
    @Override
    public GroupsAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.line_group_payment, parent, false);
        return new ViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull GroupsAdapter.ViewHolder holder, int position) {
        ///////////////
        // UI Design //
        ///////////////
        holder.txtvName.setBackgroundColor(context.getColor(android.R.color.transparent));

        /////////////
        // UI Data //
        /////////////
        // Initialize group
        Group group = groups.get(position);

        // Set name
        holder.txtvName.setText(group.getName());

        // Set time ago and members size
        holder.txtvDate.setText(system.getTimeAgo(group.getDate()) + " (" + ((group.friends != null ? group.friends.size() : 0) + 1) + " " + context.getString(R.string.participants) + ")");

        // Initialize not paid value
        double notPaid = group.getTotal() - group.getPaid();

        // Set value
        holder.txtvValue.setTextColor(context.getColor(notPaid >= 0 ? R.color.colorIncome : R.color.colorExpense));
        holder.txtvValue.setText(System.moneyFormat.format(notPaid));

        // Set money
        holder.txtvMoney.setText(System.moneyFormat.format(System.account.getMoney() + notPaid));

        ////////////////
        // Percentage //
        ////////////////
        holder.txtvPercentage.setText(System.moneyFormat.format(group.getPaid() + group.getValue()) + "/" + System.moneyFormat.format(group.getTotal() + group.getValue()));
        holder.pbProgress.setProgress((int) (((double)group.getPaid() + group.getValue()) / (((double)group.getTotal() + group.getValue())) * 100));

        /////////////
        // Friends //
        /////////////
        // To pay
        holder.txtvToPay.setText(context.getString(R.string.to_pay) + " : " + getToPayMembers(group.friends));

        // Paid
        holder.txtvPaid.setText(context.getString(R.string.paid) + " : " + getPaidMembers(group.friends));
        ///////////////////////////////////////////////////////////////////////////////////////////////////////////////
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position, @NonNull List<Object> payloads) {
        if (payloads.isEmpty())
        super.onBindViewHolder(holder, position, payloads);
        else {
            Bundle bundle = (Bundle) payloads.get(0);
            for (String key : bundle.keySet()) {
                if (key.equals("name")) holder.txtvName.setText(bundle.getString("name"));
                if (key.equals("time_members")) holder.txtvDate.setText(bundle.getString("time_members"));
                if (key.equals("value")) holder.txtvValue.setText(System.moneyFormat.format(bundle.getDouble("value")));
                if (key.equals("money")) holder.txtvMoney.setText(System.moneyFormat.format(bundle.getDouble("money")));
                if (key.equals("group")) {
                    // Get group
                    Group group = (Group) bundle.getParcelable("group");

                    ////////////////
                    // Percentage //
                    ////////////////
                    holder.txtvPercentage.setText(System.moneyFormat.format(group.getPaid() + group.getValue()) + "/" + System.moneyFormat.format(group.getTotal() + group.getValue()));
                    holder.pbProgress.setProgress((int) (((double)group.getPaid() + group.getValue()) / (((double)group.getTotal() + group.getValue())) * 100));

                    /////////////
                    // Friends //
                    /////////////
                    holder.txtvToPay.setText(context.getString(R.string.to_pay) + " : " + getToPayMembers(group.friends));
                    holder.txtvPaid.setText(context.getString(R.string.paid) + " : " + getPaidMembers(group.friends));
                }
            }
        }
    }

    @Override
    public int getItemCount() {
        return groups != null ? groups.size() : 0;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        ImageView btnDelete;
        TextView txtvName;
        TextView txtvDate;
        TextView txtvValue;
        TextView txtvMoney;
        TextView txtvToPay;
        TextView txtvPaid;
        TextView txtvPercentage;
        ProgressBar pbProgress;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            // btnDelete
            btnDelete = itemView.findViewById(R.id.btnDelete);
            btnDelete.setOnClickListener(v -> {
                deleteGroup(getAdapterPosition());
            });

            // txtvName
            txtvName = itemView.findViewById(R.id.txtvName);
            txtvName.setOnClickListener(view -> {
                // Disable UI
                disableUI();

                // Show keyboard
                system.customKeyBoard(2, system.keyboardView.findViewById(R.id.textView), false, true, 20,
                        txtvName,
                        R.drawable.box_with_round_selected,
                        R.drawable.box_with_round,
                        () -> {
                            // Get text
                            String txt = txtvName.getText().toString().trim();

                            if (fragment.isVisible() && txt.length() > 0 && !txt.equals(groups.get(getAdapterPosition()).getName())) changeGroupName(getAdapterPosition(), txt);
                            else {
                                // Notify
                                notifyItemChanged(getAdapterPosition());

                                // Enable UI
                                enableUI();
                            }
                            return null;
                        });
            });

            // txtvDate
            txtvDate = itemView.findViewById(R.id.txtvDate);

            // txtvValue
            txtvValue = itemView.findViewById(R.id.txtvValue);

            // txtvMoney
            txtvMoney = itemView.findViewById(R.id.txtvMoney);

            // txtvToPay
            txtvToPay = itemView.findViewById(R.id.txtvToPay);

            // txtvPaid
            txtvPaid = itemView.findViewById(R.id.txtvPaid);

            // txtvPercentage
            txtvPercentage = itemView.findViewById(R.id.txtvPercentage);

            // pbProgress
            pbProgress = itemView.findViewById(R.id.pbProgress);
        }
    }
}
