package org.techconnect.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;

import org.techconnect.R;
import org.techconnect.activities.GuideActivity;
import org.techconnect.activities.MainActivity;
import org.techconnect.adapters.FlowchartAdapter;
import org.techconnect.adapters.FlowchartCursorAdapter;
import org.techconnect.asynctasks.GetCatalogAsyncTask;
import org.techconnect.misc.auth.AuthManager;
import org.techconnect.model.FlowChart;
import org.techconnect.sql.TCDatabaseHelper;
import org.techconnect.views.GuideListItemView;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by Phani on 11/1 6/2016.
 */

public class CatalogFragment extends Fragment implements TextWatcher, View.OnClickListener {

    @Bind(R.id.swipe_refresh_layout)
    SwipeRefreshLayout refreshLayout;
    @Bind(R.id.session_info_layout)
    ViewGroup contentLayout;
    @Bind(R.id.noNewGuidesLayout)
    LinearLayout noNewGuidesLayout;
    @Bind(R.id.progressBar)
    ProgressBar progressBar;
    @Bind(R.id.try_again_button)
    Button tryAgainButton;
    @Bind(R.id.failedContentLayout)
    ViewGroup failedContentLayout;
    @Bind(R.id.guides_listView)
    ListView guidesListView;

    private ListAdapter adapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_catalog, container, false);
        ButterKnife.bind(this, view);
        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                loadCatalog();
            }
        });
        failedContentLayout.setVisibility(View.GONE);
        contentLayout.setVisibility(View.GONE);
        progressBar.setVisibility(View.VISIBLE);
        tryAgainButton.setOnClickListener(this);
        guidesListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                GuideListItemView guideView = ((GuideListItemView) view);
                Intent intent = new Intent(getActivity(), GuideActivity.class);
                intent.putExtra(GuideActivity.EXTRA_CHART, guideView.getFlowChart());
                //If the user is logged in, we'd like to send that information to the GuideActivity
                if (AuthManager.get(getActivity()).hasAuth()) {
                    intent.putExtra(GuideActivity.EXTRA_USER,
                            TCDatabaseHelper.get(getActivity()).getUser(AuthManager.get(getActivity()).getAuth().getUserId()));
                }
                intent.putExtra(GuideActivity.EXTRA_ALLOW_REFRESH, false);
                startActivity(intent);
            }
        });
        guidesListView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView absListView, int i) {

            }

            @Override
            public void onScroll(AbsListView absListView, int firstVisibleItem, int i1, int i2) {
                int topRowVerticalPosition =
                        (guidesListView == null || guidesListView.getChildCount() == 0) ?
                                0 : guidesListView.getChildAt(0).getTop();
                refreshLayout.setEnabled(firstVisibleItem == 0 && topRowVerticalPosition >= 0);
            }
        });
        loadCatalog();
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (getActivity() != null) {
            getActivity().setTitle(R.string.guide_catalog);
            //Update the flowchart associated with the GuideView
            loadCatalog();
        }
    }

    @OnClick(R.id.view_offline_guides)
    public void onViewOfflineGuides() {
        if (getActivity() != null && getActivity() instanceof MainActivity) {
            ((MainActivity) getActivity()).setCurrentFragment(MainActivity.FRAGMENT_GUIDES);
        }
    }

    private void loadCatalog() {
        contentLayout.setVisibility(View.GONE);
        failedContentLayout.setVisibility(View.GONE);
        progressBar.setVisibility(View.VISIBLE);
        refreshLayout.setRefreshing(false);
        new GetCatalogAsyncTask() {
            @Override
            protected void onPostExecute(FlowChart[] flowCharts) {
                progressBar.setVisibility(View.GONE);
                if (flowCharts == null) {
                    failedContentLayout.setVisibility(View.VISIBLE);
                } else {
                    setCatalog(flowCharts);
                }
            }
        }.execute();
    }

    private void setCatalog(FlowChart[] flowCharts) {
        //First, check to see if we already have that flowchart downloaded
        ArrayList<FlowChart> newCharts = new ArrayList<FlowChart>();
        for (FlowChart f : flowCharts) {
            if (!TCDatabaseHelper.get(getActivity()).hasChart(f.getId())) {
                newCharts.add(f);
            }
        }
        FlowChart[] newChartsArray = newCharts.toArray(new FlowChart[newCharts.size()]);
        adapter = new FlowchartAdapter(getActivity(),newChartsArray);
        guidesListView.setAdapter(adapter);
        failedContentLayout.setVisibility(View.GONE);
        progressBar.setVisibility(View.GONE);
        //If no new guides, let the user know
        if (newChartsArray.length == 0) {
            noNewGuidesLayout.setVisibility(View.VISIBLE);
            contentLayout.setVisibility(View.GONE);
        } else {
            noNewGuidesLayout.setVisibility(View.GONE);
            contentLayout.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

    }

    @Override
    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        ((FlowchartCursorAdapter) guidesListView.getAdapter()).getFilter().filter(charSequence);
    }

    @Override
    public void afterTextChanged(Editable editable) {

    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.try_again_button) {
            loadCatalog();
        }
    }

}
