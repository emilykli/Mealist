package com.example.mealist.Access;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;

import com.example.mealist.AddRecipe.Recipe;
import com.example.mealist.MakeMealPlan.MakePlanFragment;
import com.example.mealist.GroceryList.ListFragment;
import com.example.mealist.Home.HomeFragment;
import com.example.mealist.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.android.material.navigation.NavigationView;
import com.parse.LogOutCallback;
import com.parse.ParseException;
import com.parse.ParseUser;

public class MainActivity extends AppCompatActivity {
    public static final String TAG = "MainActivity";

    private DrawerLayout mDrawer;
    private Toolbar toolbar;
    private NavigationView mNvDrawer;

    final FragmentManager mFragmentManager = getSupportFragmentManager();
    private BottomNavigationView mBottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        toolbar = (Toolbar) findViewById(R.id.toolbarLayout);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mDrawer = findViewById(R.id.drawer_layout);
        mNvDrawer = findViewById(R.id.nvDrawer);
        setupDrawerContent(mNvDrawer);

        mBottomNavigationView = findViewById(R.id.bottomNavigation);
        setupBottomNavigationView();
        mBottomNavigationView.setSelectedItemId(R.id.miHome);

        if (getIntent().getExtras() != null) {
            String dateString = getIntent().getStringExtra("dateString");

            String breakfastArray = getIntent().getStringExtra("breakfastArray");
            String lunchArray = getIntent().getStringExtra("lunchArray");
            String dinnerArray = getIntent().getStringExtra("dinnerArray");

            Recipe breakfast = getIntent().getParcelableExtra("breakfastClickedRecipe");
            Recipe lunch = getIntent().getParcelableExtra("lunchClickedRecipe");
            Recipe dinner = getIntent().getParcelableExtra("dinnerClickedRecipe");

            Fragment fragment = new MakePlanFragment();
            Bundle arguments = new Bundle();

            arguments.putString("dateString", dateString);

            if (breakfast != null) {
                arguments.putParcelable("breakfastRecipe", breakfast);
            }
            if (lunch != null) {
                arguments.putParcelable("lunchRecipe", lunch);
            }
            if (dinner != null) {
                arguments.putParcelable("dinnerRecipe", dinner);
            }
            arguments.putString("breakfastArray", breakfastArray);
            arguments.putString("lunchArray", lunchArray);
            arguments.putString("dinnerArray", dinnerArray);

            fragment.setArguments(arguments);
            Log.i(TAG, fragment.getArguments().toString());
            mBottomNavigationView.setSelectedItemId(R.id.miMakePlan);
            mFragmentManager.beginTransaction().replace(R.id.flContainer, fragment).commit();
        }
    }

    private void setupDrawerContent(NavigationView navigationView) {
        navigationView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(MenuItem menuItem) {
                        selectDrawerItem(menuItem);
                        return true;
                    }
                });
    }

    private void selectDrawerItem(MenuItem menuItem) {
        menuItem.setChecked(true);
        mDrawer.closeDrawers();
    }


    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()) {
            case android.R.id.home:
                mDrawer.openDrawer(GravityCompat.START);
                return true;
        }

        if (item.getItemId() == R.id.miLogout) {
            ParseUser.logOutInBackground(new LogOutCallback() {
                @Override
                public void done(ParseException e) {
                    if (e != null) {
                        Log.e(TAG, "Issue with logging out", e);
                        return;
                    }
                    else {
                        goLoginActivity();
                    }
                }
            });
        }

        return super.onOptionsItemSelected(item);
    }

    public void goLoginActivity() {
        Intent i = new Intent(this, LoginActivity.class);
        startActivity(i);
        finish();
    }

    private void setupBottomNavigationView() {
        mBottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                Fragment fragment;
                switch (menuItem.getItemId()) {
                    case R.id.miList:
                        fragment = new ListFragment();
                        break;
                    case R.id.miHome:
                        fragment = new HomeFragment();
                        break;
                    case R.id.miMakePlan:
                    default:
                        fragment = new MakePlanFragment();
                        break;
                }
                mFragmentManager.beginTransaction().replace(R.id.flContainer, fragment).commit();
                return true;
            }
        });
    }
}