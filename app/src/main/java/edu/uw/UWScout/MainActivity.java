package edu.uw.UWScout;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {

    final String[] campus_options = {"Seattle", "Bothell", "Tacoma"};

    private ScoutLocation location;
    private ScoutPage[] scoutPages;
    private BottomNavigationView navView;

    public Menu menu;

    ScoutPage currentPage = null;

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

        navView = findViewById(R.id.nav_view);

        navView.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        setTitle("Discover");

        SharedPreferences sharedPreferences = this.getPreferences(Context.MODE_PRIVATE);
        String selectedCampus = sharedPreferences.getString("selectedCampus", campus_options[0]);

        location = new ScoutLocation(this);

        scoutPages = new ScoutPage[4];
        scoutPages[0] = new ScoutPage(this, findViewById(R.id.main_frame), selectedCampus, "", location);
        scoutPages[1] = new ScoutPage(this, findViewById(R.id.main_frame), selectedCampus, "food/", location);
        scoutPages[2] = new ScoutPage(this, findViewById(R.id.main_frame), selectedCampus, "study/", location);
        scoutPages[3] = new ScoutPage(this, findViewById(R.id.main_frame), selectedCampus, "tech/", location);
    }

    public void submitFilters(View view) {
        currentPage.submitFilters();
    }

    void dialog(){
        int defaultCampus = 0;

        final SharedPreferences sharedPreferences = this.getPreferences(Context.MODE_PRIVATE);
        if (sharedPreferences.contains("selectedCampus")) {
            String temp = sharedPreferences.getString("selectedCampus", "");
            for (int i = 0; i < campus_options.length; i++) {
                if (campus_options[i].equals(temp)) {
                    defaultCampus = i;
                    break;
                }
            }
        }

        final Context context = this;
        AlertDialog.Builder alt_bld = new AlertDialog.Builder(this);
        alt_bld.setTitle("Select a Campus");
        alt_bld.setSingleChoiceItems(campus_options, defaultCampus, new DialogInterface
                .OnClickListener() {
            public void onClick(DialogInterface dialog, final int item) {
                SharedPreferences.Editor spEditor = sharedPreferences.edit();
                if (sharedPreferences.contains("selectedCampus")) {
                    spEditor.remove("selectedCampus");
                }
                spEditor.putString("selectedCampus", campus_options[item]);
                spEditor.apply();
                ((Activity) context).runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        scoutPages[0].disable();
                        scoutPages[1].disable();
                        scoutPages[2].disable();
                        scoutPages[3].disable();
                        scoutPages[0] = new ScoutPage(context, findViewById(R.id.main_frame), campus_options[item], "", location);
                        scoutPages[1] = new ScoutPage(context, findViewById(R.id.main_frame), campus_options[item], "food/", location);
                        scoutPages[2] = new ScoutPage(context, findViewById(R.id.main_frame), campus_options[item], "study/", location);
                        scoutPages[3] = new ScoutPage(context, findViewById(R.id.main_frame), campus_options[item], "tech/", location);
                        switchToPage(scoutPages[0]);
                    }
                });
                dialog.dismiss();

            }
        });
        AlertDialog alert = alt_bld.create();
        alert.show();
    }

    @Override
    protected void onResume() {
        location.setUpLocationListeners();
        if (currentPage != null) {
            currentPage.enable(true);
        }
        super.onResume();
    }

    @Override
    protected void onPause() {
        location.killLocationListners();
        super.onPause();
    }

    @Override
    public void onBackPressed() {
        if (currentPage.popPageInstance())
            currentPage.enable(false);
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
        switchToPage(scoutPages[0]);
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
        } else if (id == R.id.action_campus) {
            dialog();
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
