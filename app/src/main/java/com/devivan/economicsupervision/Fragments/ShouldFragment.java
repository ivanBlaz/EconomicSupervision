package com.devivan.economicsupervision.Fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.devivan.economicsupervision.Adapters.FriendsAdapter.FriendsAdapter;
import com.devivan.economicsupervision.UtilityClasses.CustomViewPager;
import com.devivan.economicsupervision.System.System;
import com.devivan.economicsupervision.R;
import com.google.android.material.tabs.TabLayout;

import static com.devivan.economicsupervision.System.System.FATAL_ERROR;

public class ShouldFragment extends Fragment {

    public View v;

    ShouldFragment __this__;

    System system;

    public TabLayout tabLayout;

    public CustomViewPager viewPager;

    // RecyclerView + adapter
    public RecyclerView rvFriends;
    public FriendsAdapter friendsAdapter;
    /////////////////////////////////////

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        v = inflater.inflate(R.layout.fragment_have_individual, container, false);

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
        if (system != null) system.dismissDialog(System.dialog);
        if (friendsAdapter != null && friendsAdapter.mSpeechRecognizer != null) friendsAdapter.mSpeechRecognizer.stopListening();
        super.onDestroyView();
    }

    private void prepareFragment() {
        // Find
        rvFriends = v.findViewById(R.id.rvFriends);

        // Initialize adapter
        friendsAdapter = new FriendsAdapter(v.getContext(), this, system, rvFriends, "SH");

        // Set adapter
        System.setAdapter(rvFriends, friendsAdapter, false, false, new LinearLayoutManager(v.getContext(), RecyclerView.VERTICAL, false));

        // Refresh list
        if (system.doesTheDatabaseExist() && !system.isDatabaseCorrupt()) friendsAdapter.refreshList(system.getFriends("SH"));
        else { FATAL_ERROR = true; system.activity.onBackPressed(); }
    }
}