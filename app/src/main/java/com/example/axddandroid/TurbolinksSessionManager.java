package com.example.axddandroid;

import android.app.Activity;
import android.content.Context;
import android.location.Location;
import android.util.Log;
import android.view.View;
import android.webkit.JavascriptInterface;

import com.basecamp.turbolinks.TurbolinksAdapter;
import com.basecamp.turbolinks.TurbolinksSession;
import com.basecamp.turbolinks.TurbolinksView;

public class TurbolinksSessionManager {
    private Context context;
    private TurbolinksSession turbolinksSession;
    private ScoutBridgeInterface scoutBridgeInterface;
    private TurbolinksAdapter turbolinksAdapter;
    private TurbolinksView turbolinksView;

    public TurbolinksSessionManager(Context context, TurbolinksAdapter turbolinksAdapter, ScoutBridgeInterface scoutBridgeInterface, TurbolinksView turbolinksView) {
        this.context = context;
        turbolinksSession = TurbolinksSession.getNew(this.context);
        turbolinksSession.addJavascriptInterface(this, "scoutBridge");
        turbolinksSession.setDebugLoggingEnabled(true);

        this.scoutBridgeInterface = scoutBridgeInterface;
        this.turbolinksAdapter = turbolinksAdapter;
        this.turbolinksView = turbolinksView;
    }

    public void visit(String url) {
        if (turbolinksSession.getWebView().getUrl() == null || !turbolinksSession.getWebView().getUrl().equals(url)) {
            turbolinksSession.activity((Activity) context)
                    .adapter(turbolinksAdapter)
                    .view(turbolinksView)
                    .visit(url);
        }
    }

    public void setViewGone() {
        turbolinksView.setVisibility(View.GONE);
    }

    public void setViewVisible() {
        turbolinksView.setVisibility(View.VISIBLE);
    }

    @JavascriptInterface
    public boolean setParams(String params) {
        Log.d("MainSetParams", "Called with params: " + params);
        return scoutBridgeInterface.def(turbolinksSession, params);
    }
}
