package org.techconnect.views;

import android.content.Context;
import android.support.design.widget.TabLayout;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import org.techconnect.R;
import org.techconnect.model.Commentable;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by Phani on 10/25/2016.
 */

public class CommentsResourcesTabbedView extends LinearLayout implements TabLayout.OnTabSelectedListener {

    @Bind(R.id.tab_layout)
    TabLayout tabLayout;
    @Bind(R.id.tabContentContainer)
    FrameLayout tabContentContainer;

    private CommentThreadView commentThreadView;
    private ResourcesView resourcesView;

    private Commentable commentable;
    private List<String> resources = new ArrayList<>(0);

    private String chartId;

    public CommentsResourcesTabbedView(Context context) {
        super(context);
        //inflate(getContext(), R.layout.comments_resources_tabbed_view, this);
        //init();
    }

    public CommentsResourcesTabbedView(Context context, AttributeSet attrs) {
        super(context, attrs);
        //inflate(getContext(), R.layout.comments_resources_tabbed_view, this);
        //init();
    }

    public CommentsResourcesTabbedView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        //inflate(getContext(), R.layout.comments_resources_tabbed_view, this);
        //init();
    }

    private void init() {
        inflate(getContext(), R.layout.comments_resources_tabbed_view, this);
        ButterKnife.bind(this);

    }


    public void setItems(Commentable commentable, List<String> resources, String chartId) {
        this.commentable = commentable;
        this.resources = resources;
        this.chartId = chartId;
        updateViews();
    }

    public void updateViews() {
        try {
            //Set the resources text to whatever the # of resources is
            tabLayout.getTabAt(0)
                    .setText(String.format("%s (%d)", getResources().getString(R.string.resources), resources.size()));
        } catch (NullPointerException e) {
            Log.e(getClass().toString(), e.getMessage());
        }
        tabContentContainer.removeAllViews();
        commentThreadView = (CommentThreadView) LayoutInflater.from(getContext()).inflate(R.layout.comment_thread_view, tabContentContainer, false);
        resourcesView = (ResourcesView) LayoutInflater.from(getContext()).inflate(R.layout.resources_view, tabContentContainer, false);
        commentThreadView.setComments(chartId, commentable);
        resourcesView.setResources(resources);
        tabContentContainer.addView(resourcesView);
        tabLayout.addOnTabSelectedListener(this);
    }


    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        ButterKnife.bind(this);
    }


    @Override
    public void onTabSelected(TabLayout.Tab tab) {
        tabContentContainer.removeAllViews();
        if (tab.getPosition() == 1) {
            tabContentContainer.addView(commentThreadView);
        } else {
            tabContentContainer.addView(resourcesView);
        }
    }

    @Override
    public void onTabUnselected(TabLayout.Tab tab) {

    }

    @Override
    public void onTabReselected(TabLayout.Tab tab) {

    }

    public void setVisibleTab(int position) {
        tabContentContainer.removeAllViews();
        if (position == 1) {
            tabContentContainer.addView(commentThreadView);
        } else {
            tabContentContainer.addView(resourcesView);
        }
    }
}
