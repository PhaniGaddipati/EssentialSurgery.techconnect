package org.techconnect.activities;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.ResultReceiver;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ScrollView;

import org.centum.techconnect.R;
import org.techconnect.analytics.FirebaseEvents;
import org.techconnect.dialogs.GuideFeedbackDialogFragment;
import org.techconnect.model.FlowChart;
import org.techconnect.model.session.Session;
import org.techconnect.model.session.SessionListener;
import org.techconnect.services.TCService;
import org.techconnect.sql.TCDatabaseHelper;
import org.techconnect.views.GuideFlowView;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class PlayGuideActivity extends AppCompatActivity implements SessionListener {

    public static final String EXTRA_CHART_ID = "org.techconnect.playguide.chartid";
    public static final String EXTRA_SESSION = "org.techconnect.playguide.session"; //Resuming from session
    private static final String STATE_SESSION = "session";
    private static final int LAYOUT_INFO = 0;
    private static final int LAYOUT_FLOW = 1;
    private static final int LAYOUT_ERROR = 2;

    @Bind(R.id.flowContainer)
    FrameLayout flowContainer;
    @Bind(R.id.errorLayout)
    LinearLayout errorLayout;
    @Bind(R.id.session_info_layout)
    ScrollView sessionInfoLayout;

    @Bind(R.id.sync_button)
    Button syncButton;
    @Bind(R.id.manufacturer_editText)
    EditText manufacturerEditText;
    @Bind(R.id.department_editText)
    EditText departmentEditText;
    @Bind(R.id.model_editText)
    EditText modelEditText;
    @Bind(R.id.serial_editText)
    EditText serialEditText;
    @Bind(R.id.notes_editText)
    EditText notesEditText;

    private GuideFlowView flowView;
    private FlowChart flowChart = null;
    private Session session;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play_guide);
        ButterKnife.bind(this);
        flowView = (GuideFlowView) LayoutInflater.from(this).inflate(R.layout.guide_flow_view, flowContainer, false);
        flowContainer.addView(flowView);
        loadFlowchart();
        if (flowChart != null) {
            FirebaseEvents.logStartSession(this, flowChart);
        }
        if (savedInstanceState != null && savedInstanceState.containsKey(STATE_SESSION)) {
            this.session = savedInstanceState.getParcelable(STATE_SESSION);
            flowView.setSession(session, this);
        }
        updateViews();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable(STATE_SESSION, session);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_play_guide, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.end_session) {
            onEndSession();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        if (session != null && flowView.goBack()) {
            return;
        }
        if (session != null) {
            onEndSession();
        } else {
            super.onBackPressed();
        }
    }

    private void onEndSession() {
        if (flowChart != null) {
            FirebaseEvents.logEndSessionEarly(PlayGuideActivity.this, flowChart);
        }
        endSession();
    }

    private void saveSession() {
        if (session != null) {
            TCDatabaseHelper.get(this).upsertSession(session);//Write to the SQL Database
        }
    }

    private void loadFlowchart() {
        if (getIntent() != null && getIntent().hasExtra(EXTRA_CHART_ID)) {
            flowChart = TCDatabaseHelper.get(this).getChart(getIntent().getStringExtra(EXTRA_CHART_ID));
        }

        if (getIntent() != null && getIntent().hasExtra(EXTRA_SESSION)) {
            session = (Session) getIntent().getParcelableExtra(EXTRA_SESSION);//Leverage the parcelable aspect of session
            flowChart = session.getFlowchart();
            flowView.setSession(session, this);

        }
    }

    private void updateViews() {
        if (flowChart == null) {
            setVisibleLayout(LAYOUT_ERROR);
            syncButton.setEnabled(true);
        } else if (session == null) {
            setVisibleLayout(LAYOUT_INFO);
            setTitle(flowChart.getName());
        } else {
            setVisibleLayout(LAYOUT_FLOW);
            setTitle(flowChart.getName());
        }
    }

    @OnClick(R.id.start_button)
    protected void onStartSession() {
        if (flowChart != null) {
            //Need to check that all fields are filled out
            if (TextUtils.isEmpty(departmentEditText.getText().toString())) {
                departmentEditText.setError(getString(R.string.required_fields));
                departmentEditText.requestFocus();
            } else if (TextUtils.isEmpty(manufacturerEditText.getText().toString())) {
                manufacturerEditText.setError(getString(R.string.required_fields));
                manufacturerEditText.requestFocus();
            } else if (TextUtils.isEmpty(modelEditText.getText().toString())) {
                modelEditText.setError(getString(R.string.required_fields));
                modelEditText.requestFocus();
            } else if (TextUtils.isEmpty(serialEditText.getText().toString())) {
                serialEditText.setError(getString(R.string.required_fields));
                serialEditText.requestFocus();
            } else { //All necessary entries are filled
                session = new Session(flowChart);
                session.setManufacturer(manufacturerEditText.getText().toString());
                session.setDepartment(departmentEditText.getText().toString());
                session.setModelNumber(modelEditText.getText().toString());
                session.setSerialNumber(serialEditText.getText().toString());
                session.setNotes(notesEditText.getText().toString());
                session.setCreatedDate(System.currentTimeMillis());
                flowView.setSession(session, this);
                updateViews();
            }
        }
    }

    @OnClick(R.id.sync_button)
    protected void onSync() {
        // Ideally this should never happen
        syncButton.setEnabled(false);
        if (getIntent() != null && getIntent().hasExtra(EXTRA_CHART_ID)) {
            TCService.startLoadCharts(this, new String[]{getIntent().getStringExtra(EXTRA_CHART_ID)},
                    new ResultReceiver(new Handler()) {
                        @Override
                        protected void onReceiveResult(int resultCode, Bundle resultData) {
                            loadFlowchart();
                        }
                    });
        }
    }

    private void setVisibleLayout(int layout) {
        flowContainer.setVisibility(layout == LAYOUT_FLOW ? View.VISIBLE : View.GONE);
        sessionInfoLayout.setVisibility(layout == LAYOUT_INFO ? View.VISIBLE : View.GONE);
        errorLayout.setVisibility(layout == LAYOUT_ERROR ? View.VISIBLE : View.GONE);
    }

    @Override
    public void onSessionComplete() {
        //Set finished, set time finished
        session.setFinished(true);
        session.setFinishedDate(System.currentTimeMillis());
        endSession();
        if (flowChart != null) {
            FirebaseEvents.logSessionComplete(this, flowChart);
        }
    }

    private void endSession() {
        if (session != null) {
            if (!session.isFinished()) {
                new AlertDialog.Builder(this)
                        .setTitle(R.string.save_session)
                        .setMessage(R.string.save_session_msg)
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                saveSession();
                                dialog.dismiss();
                                finish();
                            }
                        })
                        .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                GuideFeedbackDialogFragment frag = GuideFeedbackDialogFragment.newInstance(session);
                                frag.setOnDismissListener(new DialogInterface.OnDismissListener() {
                                    @Override
                                    public void onDismiss(DialogInterface dialog) {

                                        finish();
                                    }
                                });
                                frag.show(getFragmentManager(), "guide_feedback");
                                dialog.dismiss();
                            }
                        }).show();
            } else {
                saveSession();
                GuideFeedbackDialogFragment.newInstance(session).show(getFragmentManager(), "guide_feedback");
            }
            FirebaseEvents.logSessionDuration(this, session);
        } else {
            finish();
        }
    }

    @Override
    public void onSessionPaused() {
        // TODO store the progress_spinner made somewhere
    }
}

