package org.techconnect.fragments;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v4.widget.SwipeRefreshLayout;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;

import org.centum.techconnect.R;
import org.techconnect.activities.GuideActivity;
import org.techconnect.activities.MainActivity;
import org.techconnect.adapters.FlowchartCursorAdapter;
import org.techconnect.misc.auth.AuthManager;
import org.techconnect.sql.TCDatabaseHelper;
import org.techconnect.views.GuideListItemView;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class  GuidesFragment extends Fragment
        implements SwipeRefreshLayout.OnRefreshListener,
        View.OnClickListener,
        TextWatcher,
        LoaderManager.LoaderCallbacks<Cursor> {

    private static final int GUIDE_LOADER = 0;

    @Bind(R.id.swipe_refresh_layout)
    SwipeRefreshLayout refreshLayout;
    @Bind(R.id.guides_listView)
    ListView guidesListView;
    @Bind(R.id.search_editText)
    EditText searchEditText;
    @Bind(R.id.clear_search_imageView)
    ImageView clearSearchImageView;
    @Bind(R.id.download_guides_button)
    Button downloadGuidesButton;
    @Bind(R.id.no_guides_layout)
    ViewGroup noGuidesLayout;
    @Bind(R.id.search_linearLayout)
    LinearLayout searchLinearLayout;

    private FlowchartCursorAdapter adapter;

    public GuidesFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_guides, container, false);
        ButterKnife.bind(this, view);
        getLoaderManager().initLoader(GUIDE_LOADER, null, this);
        adapter = new FlowchartCursorAdapter(getContext());
        refreshLayout.setOnRefreshListener(this);
        guidesListView.setAdapter(adapter);
        searchEditText.addTextChangedListener(this);
        clearSearchImageView.setOnClickListener(this);
        guidesListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                GuideListItemView guideView = ((GuideListItemView) view);
                Intent intent = new Intent(getContext(), GuideActivity.class);
                // Get the non-stub chart and open
                intent.putExtra(GuideActivity.EXTRA_CHART,
                        TCDatabaseHelper.get(getActivity()).getChart(guideView.getFlowChart().getId()));
                //If the user is logged in, we'd like to send that information to the GuideActivity
                if (AuthManager.get(getActivity()).hasAuth()) {
                    intent.putExtra(GuideActivity.EXTRA_USER,
                            TCDatabaseHelper.get(getActivity()).getUser(AuthManager.get(getActivity()).getAuth().getUserId()));
                }
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
        return view;
    }


    @Override
    public void onResume() {
        super.onResume();
        if (getActivity() != null) {
            getActivity().setTitle(R.string.guides);

        }
    }

    public void onRefresh() {
        if (getActivity() != null) {

            getLoaderManager().restartLoader(GUIDE_LOADER, null, this);
        }
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.clear_search_imageView) {
            searchEditText.setText(null);
        }
    }

    @OnClick(R.id.download_guides_button)
    public void onDownloadGuides() {
        if (getActivity() != null && getActivity() instanceof MainActivity) {
            ((MainActivity) getActivity()).setCurrentFragment(MainActivity.FRAGMENT_CATALOG);
        }
    }

    @Override
    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
    }

    @Override
    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
    }

    @Override
    public void afterTextChanged(Editable editable) {
        onRefresh();
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        if (id == GUIDE_LOADER) {
            return TCDatabaseHelper.get(getActivity()).getAllFlowchartsCursorLoader(searchEditText.getText().toString());
        }
        return null;
    }

    @Override
    public void onLoadFinished(android.support.v4.content.Loader<Cursor> loader, Cursor data) {
        adapter.swapCursor(data);
        if (data.getCount() == 0 && TextUtils.isEmpty(searchEditText.getText().toString())) {
            searchLinearLayout.setVisibility(View.GONE);
            noGuidesLayout.setVisibility(View.VISIBLE);
            guidesListView.setVisibility(View.GONE);
        } else {
            searchLinearLayout.setVisibility(View.VISIBLE);
            noGuidesLayout.setVisibility(View.GONE);
            guidesListView.setVisibility(View.VISIBLE);
        }
        refreshLayout.setRefreshing(false);
    }

    @Override
    public void onLoaderReset(android.support.v4.content.Loader<Cursor> loader) {

    }
}
