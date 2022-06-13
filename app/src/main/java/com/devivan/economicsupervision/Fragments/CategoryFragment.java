package com.devivan.economicsupervision.Fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.devivan.economicsupervision.Adapters.CategoriesAdapter.CategoryAdapter;
import com.devivan.economicsupervision.System.System;
import com.devivan.economicsupervision.R;

import static com.devivan.economicsupervision.System.System.FATAL_ERROR;

public class CategoryFragment extends Fragment {

    View v;

    CategoryFragment __this__;

    public System system;

    // RecyclerView + adapter
    public RecyclerView rvCategories;
    public CategoryAdapter categoryAdapter;
    ///////////////////////////////////////

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        v = inflater.inflate(R.layout.fragment_category, container, false);

        // Initialize __this__
        __this__ = this;

        // Get system
        system = System.getSystem(this);

        // Prepare fragment?
        if (system != null) prepareFragment();

        // Return view
        return v;
    }

    private void prepareFragment() {
        // Find
        rvCategories = v.findViewById(R.id.rvCategories);

        if (system.doesTheDatabaseExist() && !system.isDatabaseCorrupt()) {

            // Initialize adapter
            categoryAdapter = new CategoryAdapter(this, system.getCategories());

            // Set adapter
            System.setAdapter(rvCategories, categoryAdapter, true, false, new LinearLayoutManager(v.getContext(), RecyclerView.VERTICAL, false));

        } else { FATAL_ERROR = true; system.activity.onBackPressed(); }
    }
}