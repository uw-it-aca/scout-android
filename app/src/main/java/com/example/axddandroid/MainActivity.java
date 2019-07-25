package com.example.axddandroid;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.basecamp.turbolinks.TurbolinksAdapter;
import com.basecamp.turbolinks.TurbolinksSession;
import com.basecamp.turbolinks.TurbolinksView;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.annotation.NonNull;

import android.view.MenuItem;
import java.util.Stack;


public class MainActivity extends AppCompatActivity{
    private int tabIndex;
    private PagesManager pagesManager;

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.discover:
                    tabIndex = 0;
                    updateView();
                    return true;
                case R.id.food:
                    tabIndex = 1;
                    updateView();
                    return true;
                case R.id.study:
                    tabIndex = 2;
                    updateView();
                    return true;
                case R.id.tech:
                    tabIndex = 3;
                    updateView();
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
        //mTextMessage = findViewById(R.id.message);

        navView.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        pagesManager = new PagesManager(this, "https://scout-test.s.uw.edu/h/seattle/");

        // For this demo app, we force debug logging on. You will only want to do
        // this for debug builds of your app (it is off by default)
        setTitle("Discover");

        tabIndex = 0;
        updateView();
    }

    @Override
    protected void onRestart() {
        super.onRestart();

        // Since the webView is shared between activities, we need to tell Turbolinks
        // to load the location from the previous activity upon restarting
        pagesManager.ActivatePage(tabIndex);
    }

    @Override
    public void onBackPressed() {
        /*if (urls[tabIndex].size() > 1) {
            urls[tabIndex].pop();
            updateView();
        } else {*/
            super.onBackPressed();
        //}
    }

    @Override
    public void finish() {
        super.finish();
        Log.d("DetailActivity", "finish Called");
    }

    // The starting point for any href clicked inside a Turbolinks enabled site. In a simple case
    // you can just open another activity, or in more complex cases, this would be a good spot for
    // routing logic to take you to the right place within your app.

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return false;
    }

    // -----------------------------------------------------------------------
    // Private
    // -----------------------------------------------------------------------

    // Simply forwards to an error page, but you could alternatively show your own native screen
    // or do whatever other kind of error handling you want.

    private void updateView() {
        pagesManager.ActivatePage(tabIndex);
    }
}
