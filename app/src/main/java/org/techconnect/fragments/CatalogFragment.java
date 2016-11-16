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
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;

import org.centum.techconnect.R;
import org.techconnect.activities.GuideActivity;
import org.techconnect.adapters.FlowchartAdapter;
import org.techconnect.adapters.FlowchartCursorAdapter;
import org.techconnect.asynctasks.GetCatalogAsyncTask;
import org.techconnect.model.FlowChart;
import org.techconnect.views.GuideListItemView;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by Phani on 11/16/2016.
 */

public class CatalogFragment extends Fragment implements TextWatcher, View.OnClickListener {

    @Bind(R.id.swipe_refresh_layout)
    SwipeRefreshLayout refreshLayout;
    @Bind(R.id.session_info_layout)
    ViewGroup contentLayout;
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
        adapter = new FlowchartAdapter(getActivity(), flowCharts);
        guidesListView.setAdapter(adapter);
        failedContentLayout.setVisibility(View.GONE);
        progressBar.setVisibility(View.GONE);
        contentLayout.setVisibility(View.VISIBLE);
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
