package com.example.acer.memesnetwork;

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
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.acer.memesnetwork.fragments.NewestMemesFragment;
import com.example.acer.memesnetwork.utils.SharedPreferenceUtils;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class MainActivity2 extends AppCompatActivity {
    private final int RC_SIGN_IN = 1;
    private GoogleSignInClient mGoogleSignInClient;
    private List<Fragment> fragmentList = new ArrayList<>();
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        // Configure sign-in to request the user's ID, email address, and basic
        // profile. ID and basic profile are included in DEFAULT_SIGN_IN.
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();
        // Build a GoogleSignInClient with the options specified by gso.
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
        init();
    }

    private void init() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionbar = getSupportActionBar();
        actionbar.setDisplayShowHomeEnabled(true);
        addFragments();

        drawerLayout = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            toggle.getDrawerArrowDrawable().setColor(getColor(R.color.white));
        } else {
            toggle.getDrawerArrowDrawable().setColor(getResources().getColor(R.color.white));
        }
        drawerLayout.addDrawerListener(toggle);
        toggle.setDrawerIndicatorEnabled(true);
        toggle.syncState();
        navigationView = findViewById(R.id.nav_view);

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
        String userData = SharedPreferenceUtils.getPrefs(getApplicationContext()).getString(SharedPreferenceUtils.PREFERENCES_USER_DATA, "");
        View headerView = navigationView.getHeaderView(0);

        ConstraintLayout clSignedIn = headerView.findViewById(R.id.clSignedIn);
        ConstraintLayout clSignedOut = headerView.findViewById(R.id.clSignedOut);
        ImageView ivProfile = headerView.findViewById(R.id.ivProfile);
        TextView navUsername = headerView.findViewById(R.id.tvName);
        if (!userData.equals("")) {
            clSignedIn.setVisibility(View.VISIBLE);
            clSignedOut.setVisibility(View.GONE);
            try {
                JSONObject jsonObject = new JSONObject(userData);

               //   navUsername.setText(jsonObject.getString("name"));

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }else{
            clSignedIn.setVisibility(View.GONE);
            clSignedOut.setVisibility(View.VISIBLE);
        }

        firstLayout();
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
    protected void onStart() {
        super.onStart();
        // Check for existing Google Sign In account, if the user is already signed in
        // the GoogleSignInAccount will be non-null.
        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);
        updateUI(account);

    }

    private void updateUI(GoogleSignInAccount account) {
        if (account != null) {
            // user is log in
            SharedPreferenceUtils.setPrefs(getApplicationContext(), SharedPreferenceUtils.PREFERENCES_USER_LOGIN, account.getEmail());
        } else {
            // user not log in
            SharedPreferenceUtils.removeAllPrefs(getApplicationContext());
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