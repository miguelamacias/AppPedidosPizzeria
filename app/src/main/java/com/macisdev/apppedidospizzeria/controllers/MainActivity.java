package com.macisdev.apppedidospizzeria.controllers;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;
import com.macisdev.apppedidospizzeria.R;

public class MainActivity extends AppCompatActivity implements PizzasListFragment.PizzasListInterface,
        StartersListFragment.StartersListInterface, DrinksListFragment.DrinksListInterface {

    private static int currentTab = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Slide functionality
        SectionsPageAdapter pagerAdapter = new SectionsPageAdapter(getSupportFragmentManager());
        ViewPager pager = findViewById(R.id.pager);
        pager.setAdapter(pagerAdapter);
        pager.setCurrentItem(currentTab);

        pager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                currentTab = position;
            }

            @Override
            public void onPageSelected(int position) {
                currentTab = position;
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        //Tabs functionality
        TabLayout tabs = findViewById(R.id.tabs);
        tabs.setupWithViewPager(pager);
    }

    //launch the summary activity from the floating button
    public void goToSummary(View v) {
        startActivity(new Intent(this, OrderSummaryActivity.class));
    }

    @Override
    public void productClicked(int id) {
        Intent intent = new Intent(MainActivity.this, ProductDetailsActivity.class);
        intent.putExtra(ProductDetailsActivity.PRODUCT_ID_KEY, id);
        startActivity(intent);
    }

    //inner class to handle the tabs and slide navigation
    private class SectionsPageAdapter extends FragmentPagerAdapter {
        public SectionsPageAdapter(FragmentManager fm) {
            super(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
        }

        @Override
        public int getCount() {
            return 4;
        }

        @NonNull
        @Override
        //Specify the fragment to return at every position
        public Fragment getItem(int position) {
            switch (position) {
                case 1:
                    return new PizzasListFragment();
                case 2:
                    return new StartersListFragment();
                case 3:
                    return new DrinksListFragment();
                default: //also case 0:
                    return new TopFragment();
            }

        }

        @Nullable
        @Override
        //Adds a title to every tab
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return getResources().getString(R.string.home);
                case 1:
                    return getResources().getString(R.string.pizzas_menu);
                case 2:
                    return getResources().getString(R.string.appetizers);
                case 3:
                    return getResources().getString(R.string.drinks_menu);
            }
            return null;
        }
    }
}


