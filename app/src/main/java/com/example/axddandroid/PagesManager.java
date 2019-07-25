package com.example.axddandroid;

import android.app.Activity;
import android.content.Context;
import android.location.Location;
import android.webkit.JavascriptInterface;

import androidx.appcompat.app.AppCompatActivity;

import com.basecamp.turbolinks.TurbolinksAdapter;
import com.basecamp.turbolinks.TurbolinksSession;
import com.basecamp.turbolinks.TurbolinksView;

import java.util.Stack;

public class PagesManager implements TurbolinksAdapter {
    private TurbolinksSessionManager turbolinksSessions[];
    private ScoutLocation scoutLocation;
    private Context mainContext;
    private String BASE_URL;
    private Stack<String>[] urls;
    private int activeTab = -1;

    public PagesManager(Context mainContext, String BASE_URL) {
        turbolinksSessions = new TurbolinksSessionManager[6];
        scoutLocation = new ScoutLocation(mainContext);
        this.mainContext = mainContext;
        this.BASE_URL = BASE_URL;

        initUrls();
        turbolinksSessions[0] = new TurbolinksSessionManager(mainContext, this, new ScoutBridge(), (TurbolinksView) ((Activity) mainContext).findViewById(R.id.turbolinks_view_discover));
        turbolinksSessions[1] = new TurbolinksSessionManager(mainContext, this, new ScoutBridge(), (TurbolinksView) ((Activity) mainContext).findViewById(R.id.turbolinks_view_food));
        turbolinksSessions[2] = new TurbolinksSessionManager(mainContext, this, new ScoutBridge(), (TurbolinksView) ((Activity) mainContext).findViewById(R.id.turbolinks_view_study));
        turbolinksSessions[3] = new TurbolinksSessionManager(mainContext, this, new ScoutBridge(), (TurbolinksView) ((Activity) mainContext).findViewById(R.id.turbolinks_view_tech));
        //turbolinksSessions[4] = new TurbolinksSessionManager(mainContext, this, new ScoutBridge(), (TurbolinksView) ((Activity) mainContext).findViewById(R.id.turbolinks_view_filter));
        //turbolinksSessions[5] = new TurbolinksSessionManager(mainContext, this, new ScoutBridge(), (TurbolinksView) ((Activity) mainContext).findViewById(R.id.turbolinks_view_detail));
    }

    private void initUrls() {
        urls = new Stack[4];
        urls[0] = new Stack<>();
        urls[0].add(BASE_URL);
        urls[1] = new Stack<>();
        urls[1].add(BASE_URL  + "food/");
        urls[2] = new Stack<>();
        urls[2].add(BASE_URL  + "study/");
        urls[3] = new Stack<>();
        urls[3].add(BASE_URL  + "tech/");
    }

    public void ActivatePage(int tabIndex) {
        if (activeTab != -1) {
            turbolinksSessions[activeTab].setViewGone();
        }
        activeTab = tabIndex;
        turbolinksSessions[activeTab].setViewVisible();
        turbolinksSessions[tabIndex].visit(urls[tabIndex].peek());
    }

    @Override
    public void onPageFinished() {

    }

    @Override
    public void onReceivedError(int errorCode) {
        if (errorCode == 404) {
            turbolinksSessions[activeTab].visit(BASE_URL + "error");
        }
    }

    @Override
    public void pageInvalidated() {

    }

    @Override
    public void requestFailedWithStatusCode(int statusCode) {
        if (statusCode == 404) {
            turbolinksSessions[activeTab].visit(BASE_URL + "error");
        }
    }

    @Override
    public void visitCompleted() {

    }

    @Override
    public void visitProposedToLocationWithAction(String location, String action) {
        ((Activity) mainContext).setTitle(TurbolinksSession.getDefault(mainContext).getWebView().getTitle());

        if (location.matches(".*[0-9]+.*")) {
            ((AppCompatActivity) mainContext).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        } else {
            ((AppCompatActivity) mainContext).getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        }
    }

    private class ScoutBridge implements ScoutBridgeInterface {
        public boolean def(TurbolinksSession turbolinksSession, String param) {
            if (param.equals("renderWebview")) {
                Location physicalLocation = scoutLocation.getLocation();
                turbolinksSession.runJavascript(
                        "Geolocation.getNativeLocation",
                        "" + (physicalLocation == null ? "" : physicalLocation.getLatitude()),
                        "" + (physicalLocation == null ? "" : physicalLocation.getLongitude())
                );
                return true;
            }
            return false;
        }
    }

}
