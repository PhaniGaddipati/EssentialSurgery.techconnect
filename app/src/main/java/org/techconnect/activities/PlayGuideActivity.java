package org.techconnect.activities;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.ResultReceiver;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ScrollView;

import org.techconnect.R;
import org.techconnect.analytics.FirebaseEvents;
import org.techconnect.model.FlowChart;
import org.techconnect.model.session.Session;
import org.techconnect.model.session.SessionListener;
import org.techconnect.services.TCService;
import org.techconnect.sql.TCDatabaseHelper;
import org.techconnect.views.GuideFlowView;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class PlayGuideActivity extends AppCompatActivity implements
        SessionListener,
        DialogInterface.OnDismissListener {

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
    @Bind(R.id.problem_editText)
    EditText problemEditText;
    @Bind(R.id.solution_editText)
    EditText solutionEditText;
    @Bind(R.id.notes_editText)
    EditText notesEditText;

    private GuideFlowView flowView;
    private FlowChart flowChart = null;
    private Session session;
    private Menu mOptionsMenu;
    private int currentLayout = 1;

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
        if (session == null) {
            session = new Session(flowChart); //Make a session based on flowchart, but no info on the device yet
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
        mOptionsMenu = menu;
        getMenuInflater().inflate(R.menu.activity_play_guide, mOptionsMenu);
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
        if (currentLayout == LAYOUT_FLOW) {
            if (session != null && flowView.goBack()) {
                return;
            }
            if (session != null) {
                onEndSession();
            }
        } else if (currentLayout == LAYOUT_INFO && !session.isFinished()) {
            //We need to ask if you'd like to quit without saving, this time just leave
            new AlertDialog.Builder(this)
                    .setTitle("Quit Session")
                    .setMessage("Would you like to quit this session without saving?")
                    .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                            finish();
                        }
                    })
                    .setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    }).show();
        } else if (currentLayout == LAYOUT_INFO && session.isFinished()) {
            //We need to ask if you'd like to return to the troubleshooting session
            new AlertDialog.Builder(this)
                    .setTitle("Return to Troubleshooting")
                    .setMessage("Would you like to return to troubleshooting instead of finishing this session?")
                    .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                            session.setFinished(false);
                            session.goBack();
                            updateViews();
                        }
                    })
                    .setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    }).show();
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
        if (!session.getManufacturer().equals("")) { //This is a previously resumed session
            TCDatabaseHelper.get(this).upsertSession(session);//Write to the SQL Database
            finish(); //End the activity
        } else { //This is a newly paused session
            setVisibleLayout(LAYOUT_INFO);
            setTitle(flowChart.getName());
            currentLayout = LAYOUT_INFO;
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
            currentLayout = LAYOUT_ERROR;
            syncButton.setEnabled(true);
        } else if (session == null) {
            setVisibleLayout(LAYOUT_FLOW);
            setTitle(flowChart.getName());
            currentLayout = LAYOUT_FLOW;
            //setVisibleLayout(LAYOUT_INFO);
            //setTitle(flowChart.getName());
        } else {
            setVisibleLayout(LAYOUT_FLOW);
            setTitle(flowChart.getName());
        }
    }

    @OnClick(R.id.start_button)
    protected void onSaveSession() {
        if (flowChart != null) {
            //Need to check that all fields are filled out
            /*
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
            */
                session.setManufacturer(manufacturerEditText.getText().toString());
                session.setDepartment(departmentEditText.getText().toString());
                session.setModelNumber(modelEditText.getText().toString());
                session.setSerialNumber(serialEditText.getText().toString());
                session.setProblem(problemEditText.getText().toString());
                session.setSolution(solutionEditText.getText().toString());
                session.setNotes(notesEditText.getText().toString());
                session.setCreatedDate(System.currentTimeMillis());
                flowView.setSession(session, this);
                //Force close the keyboard if open
                View view = this.getCurrentFocus();
                if (view != null) {
                    InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                }
                //Save the session to the database and close the activity
                TCDatabaseHelper.get(this).upsertSession(session);
                //Show the feedback dialog
                /*
                GuideFeedbackDialogFragment frag = GuideFeedbackDialogFragment.newInstance(session);
                frag.setOnDismissListener(this);
                frag.show(getFragmentManager(), "guide_feedback");// Fragment will terminate the activity
                */
                finish();
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
        currentLayout = layout;
        flowContainer.setVisibility(layout == LAYOUT_FLOW ? View.VISIBLE : View.GONE);
        sessionInfoLayout.setVisibility(layout == LAYOUT_INFO ? View.VISIBLE : View.GONE);
        errorLayout.setVisibility(layout == LAYOUT_ERROR ? View.VISIBLE : View.GONE);

        if (layout == LAYOUT_INFO) {
            //hide the cancel button in menu
            mOptionsMenu.findItem(R.id.end_session).setVisible(false);
        } else {
            //Make sure that the cancel button is visible
            if (mOptionsMenu != null) {
                mOptionsMenu.findItem(R.id.end_session).setVisible(true);
            }
        }
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
            final DialogInterface.OnDismissListener dismissListener = this;
            if (!session.isFinished()) {
                final AlertDialog follow = new AlertDialog.Builder(this)
                        .setTitle(R.string.save_session)
                        .setMessage(R.string.save_session_msg)
                        .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                saveSession();
                                dialog.dismiss();
                            }
                        })
                        .setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                /*
                                GuideFeedbackDialogFragment frag = GuideFeedbackDialogFragment.newInstance(session);
                                frag.setOnDismissListener(new DialogInterface.OnDismissListener() {
                                    @Override
                                    public void onDismiss(DialogInterface dialogInterface) {

                                    }
                                });
                                frag.show(getFragmentManager(), "guide_feedback");
                                */
                                dialog.dismiss();
                                finish();
                            }
                        }).create();
                new AlertDialog.Builder(this)
                        .setTitle("Quit Session")
                        .setMessage("Would you like to quit this session?")
                        .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                                follow.show();
                            }
                        })
                        .setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        }).show();
            } else {
                saveSession();
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

    @Override
    public void onDismiss(DialogInterface dialogInterface) {
        //This means that the dialog box that came up was exited, meaning that the session is no
        //longer finished
        if (!session.isFinished()) {
            //We only care in the event that the user quits while still in the middle of the session at this point
            session.goBack();//Go back to previous question
        }
    }
}

