package com.devivan.economicsupervision.Activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import android.content.Context;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.util.SparseBooleanArray;
import android.widget.TextView;

import com.devivan.economicsupervision.System.System;
import com.devivan.economicsupervision.Fragments.CategoryFragment;
import com.devivan.economicsupervision.Fragments.ConceptFragment;
import com.devivan.economicsupervision.R;

import java.util.ArrayList;
import java.util.List;

public class ConCatActivity extends AppCompatActivity {

    public System system;

    ConCatActivity __this__;

    public boolean back = true;

    // Fragments
    public ConceptFragment conceptFragment;
    public CategoryFragment categoryFragment;
    /////////////////////////////////////////

    // UI
    public TextView btnGoToConCat;

    // Data
    public static SparseBooleanArray conCat = new SparseBooleanArray(); // 0-> con | 1-> cat | 2-> goTo
    public static int pos;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.activity_con_cat);

        // Initialize __this__
        __this__ = this;

        // Get system
        system = System.getSystem(__this__);

        // Prepare activity?
        if (system != null) system.prepareActivity(__this__);
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (system != null && !System.FATAL_ERROR) system.start();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (system != null && !System.FATAL_ERROR && !System.going) system.stop();
        System.going = false;
    }

    @Override
    protected void onResume() {
        super.onResume();
        System.hideSystemUI(this);
    }

    @Override
    public void onBackPressed() {
        if (system != null && (back || System.FATAL_ERROR)) {
            if (system.isNetworkAvailable()) {
                // Can back
                back = true;

                // Initialize booleans
                boolean conOK = conceptFragment.isVisible() && conceptFragment.txtvConcept != null;
                boolean catOK = categoryFragment.isVisible() && categoryFragment.rvCategories != null;
                //////////////////////////////////////////////////////////////////////////////////////

                // Go back
                if ((isGoCon() || isCat()) && catOK) catBack();
                else if ((isGoCat() || isCon()) && conOK) conBack();
                ////////////////////////////////////////////////////
            } else {
                system.tryToConnectToTheInternet(() -> {
                    onBackPressed();
                    return null;
                });
            }
        }
    }

    @Override
    protected void onDestroy() {
        if (system != null) system.destroy();
        super.onDestroy();
    }

    // Concept
    public static boolean isCon() {
        return conCat.get(0);
    }

    public static boolean isGoCon() {
        return conCat.get(0) && conCat.get(2);
    }

    public static void setCon(boolean goTo) {
        conCat.put(0, true);
        conCat.put(1, false);
        conCat.put(2, goTo);
    }

    public void conBack() {
        System.going = true;
        super.onBackPressed();
        this.overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_right);
    }

    // Category
    public static boolean isCat() {
        return conCat.get(1);
    }

    public static boolean isGoCat() {
        return conCat.get(1) && conCat.get(2);
    }

    public static void setCat(boolean goTo) {
        conCat.put(0, false);
        conCat.put(1, true);
        conCat.put(2, goTo);
    }

    public void catBack() {
        System.going = true;
        super.onBackPressed();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_left);
    }

    // ViewPagerAdapter
    public static class ViewPagerAdapter extends FragmentPagerAdapter {

        private final List<Fragment> fragments = new ArrayList<>();
        private final List<String> fragmentTitle = new ArrayList<>();

        public ViewPagerAdapter(@NonNull FragmentManager fm, int behavior) {
            super(fm, behavior);
        }

        public void addFragment(Fragment fragment, String title){
            fragments.add(fragment);
            fragmentTitle.add(title);
        }

        @NonNull
        @Override
        public Fragment getItem(int position) {
            return fragments.get(position);
        }

        @Override
        public int getCount() {
            return fragments.size();
        }

        @Nullable
        @Override
        public CharSequence getPageTitle(int position) {
            return fragmentTitle.get(position);
        }
    }
}