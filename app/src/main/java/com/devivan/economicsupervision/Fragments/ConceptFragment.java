package com.devivan.economicsupervision.Fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.devivan.economicsupervision.System.System;
import com.devivan.economicsupervision.R;

public class ConceptFragment extends Fragment {

    View v;

    ConceptFragment __this__;

    public System system;

    // TextView
    public TextView txtvConcept;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        v = inflater.inflate(R.layout.fragment_concept, container, false);

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
        txtvConcept = v.findViewById(R.id.txtvConcept);
        txtvConcept.setText("");
        //txtvConcept.setEnabled(false);

        // Click listener
        txtvConcept.setOnClickListener(v -> system.customKeyBoardConcepts(this));
        txtvConcept.performClick();
        ///////////////////////////
    }
}