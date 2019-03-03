package com.example.acer.memesnetwork;

import android.app.ActionBar;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;

import com.example.acer.memesnetwork.fragments.NewFragment;

import java.util.ArrayList;
import java.util.List;

public class MainActivity2 extends AppCompatActivity {
    private ViewPagerAdapter demoCollectionPagerAdapter;
    private ViewPager viewPager;
    private TabLayout tabLayout;
    private List<Fragment> fragmentList = new ArrayList<>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

        addFragments();
        demoCollectionPagerAdapter =
                new ViewPagerAdapter(getSupportFragmentManager(), fragmentList);
        viewPager = findViewById(R.id.pager);
        viewPager.setAdapter(demoCollectionPagerAdapter);
        tabLayout = findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);
        // when first start, pause other fragments
        for(int i=0;i<fragmentList.size();i++){
            fragmentList.get(i).onPause();
        }
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {

            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                // Here's your instance
                fragmentList.get(position).onResume();

                for(int i=0;i<fragmentList.size();i++){
                   if(i!=position){
                       fragmentList.get(i).onPause();
                   }
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });


    }

    private void addFragments() {
        fragmentList.add(new NewFragment());
      //  fragmentList.add(new NewFragment());
    }

    class ViewPagerAdapter extends FragmentStatePagerAdapter {
        private List<Fragment> fragmentList;

        public ViewPagerAdapter(FragmentManager fm, List<Fragment> fragmentList) {
            super(fm);
            this.fragmentList = fragmentList;
        }

        @Override
        public Fragment getItem(int position) {
            return fragmentList.get(position);
        }

        @Override
        public int getCount() {
            return 1;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return "New";

                default:
                    return null;
            }
        }
    }
}