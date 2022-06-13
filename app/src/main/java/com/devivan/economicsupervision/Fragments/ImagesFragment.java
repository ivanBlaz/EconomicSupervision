package com.devivan.economicsupervision.Fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.devivan.economicsupervision.Adapters.AttributionAdapter.DesignersAdapter;
import com.devivan.economicsupervision.Objects.Designer.Designer;
import com.devivan.economicsupervision.R;
import com.devivan.economicsupervision.System.System;
import com.devivan.economicsupervision.UtilityClasses.Designers;

import java.util.ArrayList;

public class ImagesFragment extends Fragment {

    View v;

    ImagesFragment __this__;

    ImageView btnFlatIcon;

    // RecyclerView + Adapter
    RecyclerView rvImages;
    DesignersAdapter designersAdapter;
    //////////////////////////////////

    // Data
    ArrayList<Designer> designers;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        v = inflater.inflate(R.layout.fragment_images, container, false);

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
        designers = System.getDesigners(getContext(), true);

        ////////
        // UI //
        ////////
        // btnFlatIcon
        btnFlatIcon = v.findViewById(R.id.btnFlatIcon);
        btnFlatIcon.setOnClickListener(v -> System.loadUrl(getContext(), Designers.flatIconUrl));

        // rvAnimations
        rvImages = v.findViewById(R.id.rvImages);

        // Initialize adapter
        designersAdapter = new DesignersAdapter(true, designers);

        // Set adapter
        System.setAdapter(rvImages, designersAdapter, true, false, new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false));
    }
}