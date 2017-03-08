package org.techconnect.activities;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import org.centum.techconnect.R;
import org.techconnect.adapters.SessionCursorAdapter;
import org.techconnect.sql.TCDatabaseHelper;
import org.techconnect.views.SessionListItemView;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by doranwalsten on 1/22/17.
 */

public class SessionListActivity extends AppCompatActivity implements
        LoaderManager.LoaderCallbacks<Cursor>  {

    //All of the binds

    //Loader options
    //private static final int SESSION_LOADER = 0;
    //Loader Types
    public static final int SESSION_DATE_LOADER = 0;
    public static final int SESSION_DEVICE_LOADER = 1;
    public static final int SESSION_ACTIVE_LOADER = 2;
    public static final int SESSION_FINISHED_LOADER = 3;

    //Extra types
    public static final String EXTRA_TITLE = "org.techconnect.sessionlistactivity.title";
    public static final String EXTRA_ARGS = "org.techconnect.sessionlistactivity.args";
    public static final String EXTRA_LOADER = "org.techconnect.sessionlistactivity.loader";

    //Intent Request Types
    private static final int VIEW_SESSION_REQUEST = 0;

    private String currentTitle = "Empty";
    private Bundle currentArgs = null;
    private int currentLoader = 0;

    @Bind(R.id.progressBar)
    ProgressBar progressBar;
    @Bind(R.id.content_linearLayout)
    LinearLayout contentLinearLayout;
    @Bind(R.id.session_ListView)
    ListView sessionListView;
    @Bind(R.id.emptyTextView)
    TextView emptyTextView;

    private SessionCursorAdapter adapter;
    private boolean isLoading = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sessionlist);
        ButterKnife.bind(this);

        //CursorLoader loader = TCDatabaseHelper.get(this.getContext()).getActiveSessionsCursorLoader();
        adapter = new SessionCursorAdapter(this);
        sessionListView.setAdapter(adapter);
        sessionListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                SessionListItemView sessionView = ((SessionListItemView) view);
                Intent intent = new Intent(getBaseContext(), SessionActivity.class);
                // Get the non-stub chart and open
                intent.putExtra(SessionActivity.EXTRA_SESSION,
                        sessionView.getSession()); //Maybe? Not sure if this is a good idea
                startActivityForResult(intent,VIEW_SESSION_REQUEST);
            }
        });

        //Get data from the intent
        if (getIntent().hasExtra(EXTRA_TITLE)) {
            setCurrentTitle(getIntent().getStringExtra(EXTRA_TITLE));
        }
        if (getIntent().hasExtra(EXTRA_ARGS)) {
            setCurrentArgs(getIntent().getBundleExtra(EXTRA_ARGS));
        }
        if (getIntent().hasExtra(EXTRA_LOADER)) {
            setCurrentLoader(getIntent().getIntExtra(EXTRA_LOADER,0));
        }

        //Initiate the loader
        getSupportLoaderManager().initLoader(currentLoader,currentArgs,this);

        //Show the back arrow
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onResume() {
        Log.d("Resume Session", "Resume Fragment");
        super.onResume();
        setTitle(currentTitle);
    }



    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        if (id == SESSION_DATE_LOADER) {
            Log.d("SessionList Activity", "Initiate Cursor Loader for DATE");
            return TCDatabaseHelper.get(this).getSessionsFromDateCursorLoader(args.getString("date"));
        } else if (id == SESSION_DEVICE_LOADER) {
            Log.d("SessionList Activity", "Initiate Cursor Loader for DEVICE");
            return TCDatabaseHelper.get(this).getSessionsFromChartCursorLoader(args.getString("id"));
        } else if (id == SESSION_ACTIVE_LOADER) {
            Log.d("SessionList Activity", "Initiate Cursor Loader for ACTIVE");
            return TCDatabaseHelper.get(this).getActiveSessionsCursorLoader();
        } else if (id == SESSION_FINISHED_LOADER) {
            return TCDatabaseHelper.get(this).getFinishedSessionsCursorLoader();
        }
        return null;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        adapter.swapCursor(data);
        adapter.notifyDataSetChanged();
        Log.d("Resume Session", String.format("Update Adapter, %d", adapter.getCount()));

        Runnable r;
        if (adapter.getCount() == 0) { //Do data
            r = new Runnable() {
                @Override
                public void run() {
                    emptyTextView.setVisibility(View.VISIBLE);
                    sessionListView.setVisibility(View.GONE);
                    progressBar.setVisibility(View.GONE);
                }
            };
        } else {
            r = new Runnable() {
                @Override
                public void run() {
                    emptyTextView.setVisibility(View.GONE);
                    sessionListView.setVisibility(View.VISIBLE);
                    progressBar.setVisibility(View.GONE);
                }
            };
        }

        Handler h = new Handler();
        h.postDelayed(r, 500);



    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }
    public int getCurrentLoader() {
        return currentLoader;
    }

    public void setCurrentLoader(int currentLoader) {
        this.currentLoader = currentLoader;
    }

    public String getCurrentTitle() {
        return currentTitle;
    }

    public void setCurrentTitle(String currentTitle) {
        this.currentTitle = currentTitle;
    }

    public Bundle getCurrentArgs() {
        return currentArgs;
    }

    public void setCurrentArgs(Bundle currentArgs) {
        this.currentArgs = currentArgs;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == VIEW_SESSION_REQUEST) {
            if (resultCode == Activity.RESULT_CANCELED) {
                //Need to update listview
                progressBar.setVisibility(View.VISIBLE);
                sessionListView.setVisibility(View.GONE);
                getSupportLoaderManager().restartLoader(currentLoader,currentArgs,this);
            }
        }
    }
}
