package com.devivan.economicsupervision.Fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.text.method.ScrollingMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.devivan.economicsupervision.R;
import com.devivan.economicsupervision.System.System;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;

public class SoftwareFragment extends Fragment {

    View v;

    SoftwareFragment __this__;

    TextView txtvLicense;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        v = inflater.inflate(R.layout.fragment_software, container, false);

        // Initialize __this__
        __this__ = this;

        // Prepare fragment
        prepareFragment();

        // Return view
        return v;
    }


    private void prepareFragment() {
        ////////////////////
        // Find & Display //
        ////////////////////
        // txtvLicense
        txtvLicense = v.findViewById(R.id.txtvLicense);
        txtvLicense.setMovementMethod(new ScrollingMovementMethod());
        if (getContext() != null) txtvLicense.setText(System.readLicense(getContext()));
    }

}