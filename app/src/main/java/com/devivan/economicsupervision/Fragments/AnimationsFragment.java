package com.devivan.economicsupervision.Fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.devivan.economicsupervision.Adapters.AttributionAdapter.DesignersAdapter;
import com.devivan.economicsupervision.Objects.Designer.Designer;
import com.devivan.economicsupervision.R;
import com.devivan.economicsupervision.System.System;
import com.devivan.economicsupervision.UtilityClasses.Designers;

import java.util.ArrayList;

public class AnimationsFragment extends Fragment {

    View v;

    AnimationsFragment __this__;

    ImageView btnLottieFiles;

    // RecyclerView + Adapter
    RecyclerView rvAnimations;
    DesignersAdapter designersAdapter;
    //////////////////////////////////////

    // Data
    ArrayList<Designer> designers;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        v = inflater.inflate(R.layout.fragment_animations, container, false);

        // Initialize __this__
        __this__ = this;

        // Prepare fragment
        prepareFragment();

        // Return view
        return v;
    }

    private void prepareFragment() {
        //////////
        // Data //
        //////////
        // Fill list
        designers = System.getDesigners(getContext(), false);

        ////////
        // UI //
        ////////
        // btnLottieFiles
        btnLottieFiles = v.findViewById(R.id.btnLottieFiles);
        btnLottieFiles.setOnClickListener(v -> System.loadUrl(getContext(), Designers.lottieFilesUrl));

        // rvAnimations
        rvAnimations = v.findViewById(R.id.rvAnimations);

        // Initialize adapter
        designersAdapter = new DesignersAdapter(false, designers);

        // Set adapter
        System.setAdapter(rvAnimations, designersAdapter, true, false, new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false));
    }
}