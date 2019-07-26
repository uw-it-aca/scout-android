package com.example.axddandroid;

import android.app.Activity;
import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;

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

        public void enableInstance() {
            turbolinksView.setVisibility(View.VISIBLE);
            if (turbolinksSession.getWebView().getUrl() == null || !turbolinksSession.getWebView().getUrl().equals(url))
                turbolinksSession.activity((Activity) context)
                        .adapter(parentScoutPage)
                        .view(turbolinksView)
                        .visit(url);
        }

        public void disableInstance() {
            turbolinksView.setVisibility(View.GONE);
        }

        private void initView() {
            turbolinksView.setLayoutParams(
                    new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT));
            turbolinksView.setVisibility(View.GONE);
            parentScoutPage.parentComponent.addView(turbolinksView);
        }
    }

    private static String BASE_URL = "https://scout-test.s.uw.edu/h/seattle/";
    private Stack<PageInstance> pageInstances;
    private FrameLayout parentComponent;
    private Context context;

    public ScoutPage(Context context, FrameLayout parentComponent, String subUrl) {
        this.parentComponent = parentComponent;
        this.context = context;

        pageInstances = new Stack<>();
        stackPageInstance(BASE_URL + subUrl);
    }

    public void enable() {
        pageInstances.peek().enableInstance();
    }

    public void disable() {
        pageInstances.peek().disableInstance();
    }

    public void popPageInstance() {
        pageInstances.pop();
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

    }

    @Override
    public void visitProposedToLocationWithAction(String location, String action) {
        stackPageInstance(location);
        enable();
    }

    private void stackPageInstance(String url) {
        if (pageInstances.size() > 0)
            pageInstances.peek().disableInstance();
        pageInstances.push(new PageInstance(TurbolinksSession.getNew(context), new TurbolinksView(context), url, this));
    }
}
