package com.example.mealist.Access;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.example.mealist.Add.AddFragment;
import com.example.mealist.GroceryList.ListFragment;
import com.example.mealist.Home.HomeFragment;
import com.example.mealist.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.parse.LogOutCallback;
import com.parse.ParseException;
import com.parse.ParseUser;

public class MainActivity extends AppCompatActivity {
    public static final String TAG = "MainActivity";

    final FragmentManager mFragmentManager = getSupportFragmentManager();
    private BottomNavigationView mBottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mBottomNavigationView = findViewById(R.id.bottomNavigation);
        setupBottomNavigationView();
        mBottomNavigationView.setSelectedItemId(R.id.miHome);
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_top_navigation, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
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
                    case R.id.miAdd:
                    default:
                        fragment = new AddFragment();
                        break;
                }
                mFragmentManager.beginTransaction().replace(R.id.flContainer, fragment).commit();
                return true;
            }
        });
    }
}