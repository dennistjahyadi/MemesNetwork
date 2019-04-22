package com.dovoo.memesnetwork;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.dovoo.memesnetwork.activities.ProfileActivity;
import com.dovoo.memesnetwork.activities.SectionActivity;
import com.dovoo.memesnetwork.fragments.NewestMemesFragment;
import com.dovoo.memesnetwork.utils.GlobalFunc;
import com.dovoo.memesnetwork.utils.SharedPreferenceUtils;
import com.dovoo.memesnetwork.utils.Utils;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;


import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private final int RC_SIGN_IN = 1;
    private final int ACTIVITY_RESULT_SECTION = 2;
    private GoogleSignInClient mGoogleSignInClient;
    private List<Fragment> fragmentList = new ArrayList<>();
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private LinearLayout linBtnSection;
    private TextView tvBtnProfile;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // Configure sign-in to request the user's ID, email address, and basic
        // profile. ID and basic profile are included in DEFAULT_SIGN_IN.
        if(GlobalFunc.mGoogleSignInClient==null) {
            GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                    .requestIdToken(Utils.clientId)
                    .requestEmail()
                    .build();
            // Build a GoogleSignInClient with the options specified by gso.
            GlobalFunc.mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
        }
        init();
    }

    private void init() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);
        ActionBar actionbar = getSupportActionBar();
        actionbar.setDisplayShowHomeEnabled(true);
        addFragments();
        drawerLayout = findViewById(R.id.drawer_layout);
        drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);

        linBtnSection = findViewById(R.id.linBtnSection);
        tvBtnProfile = findViewById(R.id.tvBtnProfile);
        navigationView = findViewById(R.id.nav_view);

        tvBtnProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Boolean isUserLoggedIn = SharedPreferenceUtils.getPrefs(getApplicationContext()).getBoolean(SharedPreferenceUtils.PREFERENCES_USER_IS_LOGIN, false);
                if (isUserLoggedIn) {
                    Intent i = new Intent(getApplicationContext(),ProfileActivity.class);
                    startActivity(i);
                }else{
                    Intent i = new Intent(getApplicationContext(),LoginActivity.class);
                    startActivity(i);
                }
            }
        });
        linBtnSection.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(), SectionActivity.class);
                startActivityForResult(i, ACTIVITY_RESULT_SECTION);
            }
        });
        //setupNavigationDrawer(toolbar);

        firstLayout();
    }

    private void setupNavigationDrawer(Toolbar toolbar){
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        toggle.setDrawerIndicatorEnabled(false);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            toggle.getDrawerArrowDrawable().setColor(getColor(R.color.white));
        } else {
            toggle.getDrawerArrowDrawable().setColor(getResources().getColor(R.color.white));
        }
        drawerLayout.addDrawerListener(toggle);
        toggle.setDrawerIndicatorEnabled(true);
        toggle.syncState();

        navigationView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(MenuItem menuItem) {
                        // set item as selected to persist highlight
                        menuItem.setChecked(true);
                        Fragment fragment = null;
                        FragmentManager fragmentManager = getSupportFragmentManager(); // For A
                        int itemId = menuItem.getItemId();
                        switch (itemId) {
                            case R.id.newest:
                                fragment = new NewestMemesFragment();
                                break;
                            case R.id.favorite:
                                break;
                        }
                        if (fragment != null) {
                            fragmentManager.beginTransaction()
                                    .replace(R.id.content_frame, fragment)
                                    .commit();
                        }

                        // close drawer when item is tapped
                        drawerLayout.closeDrawers();

                        // Add code here to update the UI based on the item selected
                        // For example, swap UI fragments here

                        return true;
                    }
                });
    }

    @Override
    protected void onResume() {
        super.onResume();
        checkUserLoggedIn();
    }

    private void checkUserLoggedIn(){
        Boolean isUserLoggedIn = SharedPreferenceUtils.getPrefs(getApplicationContext()).getBoolean(SharedPreferenceUtils.PREFERENCES_USER_IS_LOGIN, false);
        View headerView = navigationView.getHeaderView(0);

        ConstraintLayout clSignedIn = headerView.findViewById(R.id.clSignedIn);
        ConstraintLayout clSignedOut = headerView.findViewById(R.id.clSignedOut);
        ImageView ivProfile = headerView.findViewById(R.id.ivProfile);
        TextView navUsername = headerView.findViewById(R.id.tvName);
        if (isUserLoggedIn) {
            clSignedIn.setVisibility(View.VISIBLE);
            clSignedOut.setVisibility(View.GONE);
            String username = SharedPreferenceUtils.getPrefs(getApplicationContext()).getString(SharedPreferenceUtils.PREFERENCES_USER_NAME,"");
            if(username.equals("")){
                // run choose username activity
            }
            navUsername.setText(username);

        }else{
            clSignedIn.setVisibility(View.GONE);
            clSignedOut.setVisibility(View.VISIBLE);
        }
    }

    private void firstLayout() {
        Fragment fragment = new NewestMemesFragment();
        FragmentManager fragmentManager = getSupportFragmentManager(); // For A
        if (fragment != null) {
            fragmentManager.beginTransaction()
                    .replace(R.id.content_frame, fragment)
                    .commit();
        }
        navigationView.getMenu().getItem(0).setChecked(true);

    }

    private void addFragments() {
        fragmentList.add(new NewestMemesFragment());
        //  fragmentList.add(new NewestMemesFragment());
    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == ACTIVITY_RESULT_SECTION && resultCode == RESULT_OK) {
            String section = data.getStringExtra("name");
            Fragment fragment = new NewestMemesFragment();
            Bundle arguments = new Bundle();
            arguments.putString("section", section);
            fragment.setArguments(arguments);
            FragmentManager fragmentManager = getSupportFragmentManager(); // For A
            if (fragment != null) {
                fragmentManager.beginTransaction()
                        .replace(R.id.content_frame, fragment)
                        .commit();
            }

        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                drawerLayout.openDrawer(GravityCompat.START);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}