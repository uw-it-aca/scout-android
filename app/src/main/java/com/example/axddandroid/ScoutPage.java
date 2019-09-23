package com.example.axddandroid;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.net.Uri;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.webkit.JavascriptInterface;
import android.widget.FrameLayout;

import android.view.animation.TranslateAnimation;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.basecamp.turbolinks.TurbolinksAdapter;
import com.basecamp.turbolinks.TurbolinksSession;
import com.basecamp.turbolinks.TurbolinksView;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
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

            ((MainActivity) context).menu.getItem(0).setVisible(false);
            ((MainActivity) context).menu.getItem(1).setVisible(false);
            ((AppCompatActivity) context).getSupportActionBar().setDisplayHomeAsUpEnabled(false);

            if (url.matches(".*/filter/.*"))
                ((AppCompatActivity) context).findViewById(R.id.filter_submit).setVisibility(View.VISIBLE);
            else {
                ((AppCompatActivity) context).findViewById(R.id.filter_submit).setVisibility(View.GONE);
                if (pageInstances.size() > 1)
                    ((AppCompatActivity) context).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
                else {
                    if (url.matches(".*/(food|study|tech)/((?!((filter|[0-9]+)/|\\?h=true))).*"))
                        ((MainActivity) context).menu.getItem(1).setVisible(true);
                    else if (pageInstances.size() == 1)
                        ((MainActivity) context).menu.getItem(0).setVisible(true);
                }
            }

            Log.d("Filter Status", filterParams);
            if (force || turbolinksSession.getWebView().getUrl() == null || !turbolinksSession.getWebView().getUrl().equals(url + filterParams)) {
                new Thread() {
                    public void run() {
                        if (!isOnline()) {
                            ((MainActivity) context).runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    turbolinksSession.activity((Activity) context)
                                            .adapter(parentScoutPage)
                                            .view(turbolinksView);
                                    Toast toast = Toast.makeText(context, "No internet", Toast.LENGTH_SHORT);
                                    toast.setText("No internet");
                                    toast.show();
                                }
                            });
                        }
                        else
                            ((MainActivity) context).runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    ((AppCompatActivity) context).setTitle("");
                                    turbolinksSession.activity((Activity) context)
                                            .adapter(parentScoutPage)
                                            .view(turbolinksView)
                                            .visit(url + filterParams);
                                    turbolinksSession.setPullToRefreshEnabled(true);
                                }
                            });
                    }
                }.start();
            }
            else
                ((Activity) context).setTitle(pageInstances.peek().turbolinksSession.getWebView().getTitle());
        }

        void disableInstance() {

            // R to L animation
            /*
            TranslateAnimation animate = new TranslateAnimation(0,-turbolinksView.getWidth(),0,0);
            animate.setDuration(500);
            animate.setFillAfter(true);
            turbolinksView.startAnimation(animate);
            */

            // L to R animation
            /*
            TranslateAnimation animate = new TranslateAnimation(0,turbolinksView.getWidth(),0,0);
            animate.setDuration(500);
            animate.setFillAfter(true);
            turbolinksView.startAnimation(animate);
            */

            // check if advance vs restore??

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

    private String base_url;
    private Stack<PageInstance> pageInstances;
    private FrameLayout parentComponent;
    private Context context;
    private boolean inFilterMode = false;
    private String filterParams = "";

    public static ScoutLocation location = null;

    ScoutPage(Context context, FrameLayout parentComponent, String campus, String subUrl) {
        this.parentComponent = parentComponent;
        this.context = context;
        this.base_url = context.getResources().getString(R.string.baseUrl);
        if (location == null)
            location = new ScoutLocation(context);

        pageInstances = new Stack<>();
        this.base_url += campus.toLowerCase() + "/" + subUrl;
        stackPageInstance(base_url);
    }

    void enable(boolean force) {
        pageInstances.peek().enableInstance(force);
    }

    void disable() {
        pageInstances.peek().disableInstance();
    }

    boolean popPageInstance() {
        if (pageInstances.size() > 1) {
            pageInstances.peek().disableInstance();
            pageInstances.pop();
            return true;
        }
        return false;
    }

    void launchFilter() {
        stackPageInstance(base_url + "filter/");
        enable(false);
        inFilterMode = true;
        filterParams = "";
    }

    void submitFilters() {
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
        if (((MainActivity) context).currentPage == this) {
            ((Activity) context).setTitle(pageInstances.peek().turbolinksSession.getWebView().getTitle());
        }
    }

    @Override
    public void visitProposedToLocationWithAction(String location, String action) {
        if (pageInstances.peek().turbolinksSession.getWebView().getUrl().matches(".*/[0-9]+/.*")) {
            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(location));
            ((Activity) context).startActivity(browserIntent);
        }
        else {
            stackPageInstance(location);
            enable(false);
            ((Activity) context).setTitle("");
        }

    }

    @JavascriptInterface
    public void setParams(String param) {
        Log.d("ScoutBridgeCall", "With params: " + param + " and inFilterMode is " + inFilterMode);
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

    public boolean isOnline() {
        try {
            int timeoutMs = 1500;
            Socket sock = new Socket();
            SocketAddress sockaddr = new InetSocketAddress("8.8.8.8", 53);

            sock.connect(sockaddr, timeoutMs);
            sock.close();

            return true;
        } catch (IOException e) { return false; }
    }


}
