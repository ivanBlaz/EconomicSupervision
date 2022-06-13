package com.devivan.economicsupervision.Fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.devivan.economicsupervision.UtilityClasses.CustomViewPager;
import com.devivan.economicsupervision.System.System;
import com.devivan.economicsupervision.R;
import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.List;

public class HaveFragment extends Fragment {

    View v;

    System system;

    public TabLayout tabLayout;

    // View pager + adapter
    public CustomViewPager viewPager;
    public ViewPagerAdapter viewPagerAdapter;
    //////////////////////////////////

    // Child fragments
    public HaveIndividualFragment haveIndividualFragment;
    public HaveGroupFragment haveGroupFragment;
    ///////////////////////////////////////////

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        v = inflater.inflate(R.layout.fragment_have, container, false);

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
        super.onDestroyView();
    }

    public void prepareFragment() {
        // Find
        tabLayout = v.findViewById(R.id.indGrpTabLayout);
        viewPager = v.findViewById(R.id.indGrpViewPager);
        /////////////////////////////////////////////////

        // Initialize view pager adapter
        viewPagerAdapter = new ViewPagerAdapter(getChildFragmentManager(), 0);

        // Initialize fragments
        haveIndividualFragment = new HaveIndividualFragment();
        haveGroupFragment = new HaveGroupFragment();
        ////////////////////////////////////////////

        // Set extras to fragments
        Bundle bundle = new Bundle();
        bundle.putParcelable("system", system);
        haveIndividualFragment.setArguments(bundle);
        haveGroupFragment.setArguments(bundle);
        ///////////////////////////////////////

        // Add fragments to adapter
        viewPagerAdapter.addFragment(haveIndividualFragment, getString(R.string.Friends));
        viewPagerAdapter.addFragment(haveGroupFragment, getString(R.string.Groups));
        ////////////////////////////////////////////////////////////////////////////

        // Set adapter
        viewPager.setAdapter(viewPagerAdapter);
        viewPager.setNestedScrollingEnabled(false);
        tabLayout.setupWithViewPager(viewPager, true);
        /////////////////////////////////////////////////////////

        // On page change listener
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                viewPagerAdapter.getItem(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        if (HaveGroupFragment.hasNewGroup) {
            // Set not started
            HaveGroupFragment.hasNewGroup = false;

            // Change current item
            viewPager.setCurrentItem(1);
        }
    }

    static class ViewPagerAdapter extends FragmentPagerAdapter {

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