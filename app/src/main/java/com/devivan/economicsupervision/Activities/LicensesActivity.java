package com.devivan.economicsupervision.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.util.SparseBooleanArray;
import android.widget.TextView;

import com.devivan.economicsupervision.Fragments.AnimationsFragment;
import com.devivan.economicsupervision.Fragments.CategoryFragment;
import com.devivan.economicsupervision.Fragments.ConceptFragment;
import com.devivan.economicsupervision.Fragments.ImagesFragment;
import com.devivan.economicsupervision.Fragments.SoftwareFragment;
import com.devivan.economicsupervision.R;
import com.devivan.economicsupervision.System.System;

public class LicensesActivity extends AppCompatActivity {

    public System system;

    LicensesActivity __this__;

    public boolean back = true;

    // Fragments
    public ImagesFragment imagesFragment;
    public AnimationsFragment animationsFragment;
    public SoftwareFragment softwareFragment;
    /////////////////////////////////////////

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.activity_licenses);

        // Initialize __this__
        __this__ = this;

        // Get system
        system = System.getSystem(__this__);

        // Prepare activity?
        if (system != null) system.prepareActivity(__this__);
    }

    @Override
    protected void onResume() {
        super.onResume();
        System.hideSystemUI(this);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.zoom_in, R.anim.zoom_out);
    }
}