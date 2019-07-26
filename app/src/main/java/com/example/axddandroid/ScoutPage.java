package com.example.axddandroid;

import android.app.Activity;
import android.content.Context;
import android.location.Location;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.webkit.JavascriptInterface;
import android.widget.FrameLayout;

import androidx.appcompat.app.AppCompatActivity;

import com.basecamp.turbolinks.TurbolinksAdapter;
import com.basecamp.turbolinks.TurbolinksSession;
import com.basecamp.turbolinks.TurbolinksView;

import java.util.Stack;

public class ScoutPage implements TurbolinksAdapter {

    private class PageInstance {
        private TurbolinksSession turbolinksSession;
        private TurbolinksView turbolinksView;
        private String url;
        private ScoutPage parentScoutPage;

        public PageInstance(TurbolinksSession turbolinksSession, TurbolinksView turbolinksView, String url, ScoutPage parentScoutPage) {
            this.turbolinksSession = turbolinksSession;
            this.turbolinksView = turbolinksView;
            this.url = url;
            this.parentScoutPage = parentScoutPage;

            initView();
        }

        public void enableInstance(boolean force) {
            turbolinksView.setVisibility(View.VISIBLE);

            Log.d("Filter Status", filterParams);
            if (force || turbolinksSession.getWebView().getUrl() == null || !turbolinksSession.getWebView().getUrl().equals(url + filterParams))
                turbolinksSession.activity((Activity) context)
                        .adapter(parentScoutPage)
                        .view(turbolinksView)
                        .visit(url + filterParams);
            else
                ((Activity) context).setTitle(pageInstances.peek().turbolinksSession.getWebView().getTitle());
        }

        public void disableInstance() {
            turbolinksView.setVisibility(View.GONE);
        }

        private void initView() {
            turbolinksSession.setDebugLoggingEnabled(true);
            turbolinksView.setLayoutParams(
                    new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT));
            turbolinksView.setVisibility(View.GONE);
            parentScoutPage.parentComponent.addView(turbolinksView);
        }
    }

    public static ScoutLocation location = null;
    private String base_url = "https://scout-test.s.uw.edu/h/seattle/";
    private Stack<PageInstance> pageInstances;
    private FrameLayout parentComponent;
    private Context context;
    private boolean inFilterMode = false;
    private String filterParams = "";

    public ScoutPage(Context context, FrameLayout parentComponent, String subUrl) {
        this.parentComponent = parentComponent;
        this.context = context;
        if (location == null)
            location = new ScoutLocation(context);

        pageInstances = new Stack<>();
        this.base_url += subUrl;
        stackPageInstance(base_url);
    }

    public void enable(boolean force) {
        pageInstances.peek().enableInstance(force);
    }

    public void disable() {
        pageInstances.peek().disableInstance();
    }

    public boolean popPageInstance() {
        if (pageInstances.size() > 1) {
            pageInstances.peek().disableInstance();
            pageInstances.pop();
            enable(false);
            return true;
        }
        return false;
    }

    public void launchFilter() {
        stackPageInstance(base_url + "filter/");
        enable(false);
        inFilterMode = true;
        filterParams = "";
    }

    public void submitFiltes() {
        inFilterMode = false;
        pageInstances.peek().turbolinksSession.runJavascript("document.querySelectorAll('a')[0].click");
        popPageInstance();
    }

    @Override
    public void onPageFinished() {

    }

    @Override
    public void onReceivedError(int errorCode) {

    }

    @Override
    public void pageInvalidated() {

    }

    @Override
    public void requestFailedWithStatusCode(int statusCode) {

    }

    @Override
    public void visitCompleted() {
        ((Activity) context).setTitle(pageInstances.peek().turbolinksSession.getWebView().getTitle());

        if (pageInstances.peek().turbolinksSession.getWebView().getUrl().matches(".*/[0-9]+/.*"))
            ((AppCompatActivity) context).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        else
            ((AppCompatActivity) context).getSupportActionBar().setDisplayHomeAsUpEnabled(false);
    }

    @Override
    public void visitProposedToLocationWithAction(String location, String action) {
        stackPageInstance(location);
        enable(false);
        ((Activity) context).setTitle("");
    }

    @JavascriptInterface
    public void setParams(String param) {
        Log.d("ScoutBridgeCall", param);
        if (param.equals("renderWebview")) {
            Location physicalLocation = location.getLocation();
            pageInstances.peek().turbolinksSession.runJavascript(
                    "Geolocation.getNativeLocation",
                    "" + (physicalLocation == null ? "" : physicalLocation.getLatitude()),
                    "" + (physicalLocation == null ? "" : physicalLocation.getLongitude())
            );
        } else {
            if (!inFilterMode) {
                filterParams = (param.equals("") ? "" : "?") + param;
                ((Activity) context).runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        enable(false);
                    }
                });
            }
        }
    }

    public void stackPageInstance(String url) {
        if (pageInstances.size() > 0)
            pageInstances.peek().disableInstance();
        pageInstances.push(new PageInstance(TurbolinksSession.getNew(context), new TurbolinksView(context), url, this));
        pageInstances.peek().turbolinksSession.addJavascriptInterface(this, "scoutBridge");
    }
}
