package com.example.axddandroid;

import android.os.Bundle;
import android.util.Log;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.annotation.NonNull;

import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;

public class MainActivity extends AppCompatActivity {

    private ScoutPage currentPage = null;
    private ScoutPage[] scoutPages;
    private BottomNavigationView navView;
    private Menu menu;

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.discover:
                    menu.getItem(0).setVisible(false);
                    menu.getItem(1).setVisible(false);
                    switchToPage(scoutPages[0]);
                    menu.getItem(0).setVisible(true);
                    return true;
                case R.id.food:
                    menu.getItem(0).setVisible(false);
                    menu.getItem(1).setVisible(false);
                    switchToPage(scoutPages[1]);
                    menu.getItem(1).setVisible(true);
                    return true;
                case R.id.study:
                    menu.getItem(0).setVisible(false);
                    menu.getItem(1).setVisible(false);
                    switchToPage(scoutPages[2]);
                    menu.getItem(1).setVisible(true);
                    return true;
                case R.id.tech:
                    menu.getItem(0).setVisible(false);
                    menu.getItem(1).setVisible(false);
                    switchToPage(scoutPages[3]);
                    menu.getItem(1).setVisible(true);
                    return true;
            }
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        navView = findViewById(R.id.nav_view);

        navView.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        setTitle("Discover");

        scoutPages = new ScoutPage[4];
        scoutPages[0] = new ScoutPage(this, (FrameLayout) findViewById(R.id.main_frame), "");
        scoutPages[1] = new ScoutPage(this, (FrameLayout) findViewById(R.id.main_frame), "food/");
        scoutPages[2] = new ScoutPage(this, (FrameLayout) findViewById(R.id.main_frame), "study/");
        scoutPages[3] = new ScoutPage(this, (FrameLayout) findViewById(R.id.main_frame), "tech/");

        findViewById(R.id.filter_submit).setVisibility(View.GONE);
        switchToPage(scoutPages[0]);
    }

    public void submitFilters(View view) {
        currentPage.submitFiltes();
        findViewById(R.id.filter_submit).setVisibility(View.GONE);
    }

    @Override
    protected void onRestart() {
        super.onRestart();
    }

    @Override
    public void onBackPressed() {
        if (currentPage.popPageInstance())
            getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        else
            super.onBackPressed();
    }

    @Override
    public void finish() {
        super.finish();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode == 0 && currentPage.location != null) {
            currentPage.location.setUpLocationListeners();
            currentPage.enable(true);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_scout, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        this.menu = menu;
        menu.getItem(0).setVisible(true);
        menu.getItem(1).setVisible(false);
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.

        int id = item.getItemId();

        if (id == R.id.action_filter) {
            currentPage.launchFilter();
            findViewById(R.id.filter_submit).setVisibility(View.VISIBLE);
        } else if (id == R.id.action_campus) {
            //showCampusChooser();
        } else if (id == android.R.id.home)
            onBackPressed();

        return super.onOptionsItemSelected(item);
    }

    private void switchToPage(ScoutPage page) {
        if (currentPage != null)
            currentPage.disable();
        currentPage = page;
        currentPage.enable(false);
    }
}
