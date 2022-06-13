package com.devivan.economicsupervision.Adapters.FriendsOfGroupAdapter;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.devivan.economicsupervision.Fragments.HaveGroupFragment;
import com.devivan.economicsupervision.R;
import com.devivan.economicsupervision.System.VoiceAssistant;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.stream.Collectors;

public class FriendsOfGroupAdapter extends RecyclerView.Adapter<FriendsOfGroupAdapter.ViewHolder> {

    HaveGroupFragment fragment;
    double value;
    public HashMap<String, String> map;
    public ArrayList<String> lookUpKeys;

    public int pos = 0;
    public boolean next = false;
    public boolean names;

    public FriendsOfGroupAdapter(HaveGroupFragment fragment, double value, HashMap<String, String> map) {
        this.fragment = fragment;
        this.value = value;
        this.map = map;
        this.names = true;
    }

    public FriendsOfGroupAdapter(HaveGroupFragment fragment, double value, ArrayList<String> lookUpKeys) {
        this.fragment = fragment;
        this.value = value;
        this.lookUpKeys = lookUpKeys;
        this.names = false;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.line_friend_of_group, parent, false);
        return new ViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        // Declare string
        String string;

        // Declare size
        int size;

        // Initialize string and size
        if (names) {
            string = map.get(map.keySet().toArray()[position].toString()) == null ? map.keySet().toArray()[position].toString() : map.get(map.keySet().toArray()[position].toString());
            size = Arrays.stream(map.keySet().toArray()).filter(s -> !s.equals(".")).toArray().length;
        } else {
            string = lookUpKeys.get(position);
            size = lookUpKeys.stream().filter(s -> s == null || !s.equals(".")).toArray().length;
        }

        // Group name view type?
        if (string != null && string.equals(".")) {
            // UI Design
            holder.txtvGroupName.setVisibility(View.VISIBLE);
            holder.txtvIndex.setVisibility(View.GONE);
            holder.imgvFriend.setImageResource(R.drawable.group_of_users);

            // UI Data
            holder.txtvName.setText("");
        } else {
            // UI Design
            holder.txtvGroupName.setVisibility(View.GONE);
            holder.txtvIndex.setVisibility(View.VISIBLE);
            holder.imgvFriend.setImageResource(R.drawable.individual_user);

            // UI Data
            holder.txtvIndex.setText((position + 1) + " / " + size);
            if (names) holder.txtvName.setText(VoiceAssistant.isName(string) ? string : fragment.system.findContactByLookupKey(string));
            else holder.txtvName.setText(string == null ? "" : fragment.system.findContactByLookupKey(string));
        }

        // Perform click
        if (pos == position) holder.itemView.performClick();
    }

    @Override
    public int getItemCount() {
        if (lookUpKeys != null) return lookUpKeys.size();
        else return map != null ? map.size() : 0;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView txtvIndex, txtvGroupName, txtvName;
        ImageView imgvFriend;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            txtvIndex = itemView.findViewById(R.id.txtvIndex);
            txtvGroupName = itemView.findViewById(R.id.txtvGroupName);
            txtvName = itemView.findViewById(R.id.txtvName);
            imgvFriend = itemView.findViewById(R.id.imgvFriend);
            itemView.setOnClickListener(v -> {
                String string = null;
                if (!names) string = lookUpKeys.get(getAdapterPosition());
                if (((names && map.containsKey(".")) || (!names && string != null && string.equals("."))) && !next) {
                    ///////////////////
                    // Name of group //
                    ///////////////////
                    fragment.system.customKeyBoard(2, fragment.system.keyboardView.findViewById(R.id.textView), false, true, 20,
                            txtvName,
                            R.drawable.box_with_round_selected,
                            R.drawable.box_with_round,
                            () -> {
                                if (fragment.isVisible()) {
                                    // New group has been built
                                    HaveGroupFragment.hasNewGroup = true;

                                    // Get group name + build or close speech
                                    String txt = txtvName.getText().toString().trim();
                                    if (txt.length() > 0) fragment.newGroup(value, lookUpKeys.stream().filter(l -> !l.equals(".")).collect(Collectors.toCollection(ArrayList::new)), txt);
                                    else fragment.closeSpeech();
                                }
                                return null;
                            });
                } else if ((map != null && !map.containsKey(".")) || (lookUpKeys != null && !lookUpKeys.contains("."))) {
                    /////////////////////////
                    // LookUpKey of friend //
                    /////////////////////////
                    fragment.system.customKeyBoardContactsGP(
                            fragment,
                            (TextView) fragment.system.keyboardContactsView.findViewById(R.id.textView),
                            txtvName,
                            R.drawable.box_with_round_selected,
                            R.drawable.box_with_round,
                            fragment.system.keyboardContactsView.findViewById(R.id.rvContacts),
                            fragment.rvFriendsOfGroup,
                            FriendsOfGroupAdapter.this,
                            getAdapterPosition());
                }
            });
        }
    }
}