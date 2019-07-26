package com.example.axddandroid;

import android.os.Bundle;
import android.util.Log;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.annotation.NonNull;

import android.view.MenuItem;
import android.widget.FrameLayout;

public class MainActivity extends AppCompatActivity {

    ScoutPage currentPage = null;
    ScoutPage[] scoutPages;

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.discover:
                    switchToPage(scoutPages[0]);
                    return true;
                case R.id.food:
                    switchToPage(scoutPages[1]);
                    return true;
                case R.id.study:
                    switchToPage(scoutPages[2]);
                    return true;
                case R.id.tech:
                    switchToPage(scoutPages[3]);
                    return true;
            }
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        BottomNavigationView navView = findViewById(R.id.nav_view);

        navView.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        setTitle("Discover");

        scoutPages = new ScoutPage[4];
        scoutPages[0] = new ScoutPage(this, (FrameLayout) findViewById(R.id.main_frame), "");
        scoutPages[1] = new ScoutPage(this, (FrameLayout) findViewById(R.id.main_frame), "food");
        scoutPages[2] = new ScoutPage(this, (FrameLayout) findViewById(R.id.main_frame), "study");
        scoutPages[3] = new ScoutPage(this, (FrameLayout) findViewById(R.id.main_frame), "tech");


        switchToPage(scoutPages[0]);
    }

    @Override
    protected void onRestart() {
        super.onRestart();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    public void finish() {
        super.finish();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return false;
    }

    private void switchToPage(ScoutPage page) {
        if (currentPage != null)
            currentPage.disable();
        currentPage = page;
        currentPage.enable();
    }
}
