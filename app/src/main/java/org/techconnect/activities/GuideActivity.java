package org.techconnect.activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.ResultReceiver;
import android.support.design.widget.TabLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.sothree.slidinguppanel.SlidingUpPanelLayout;
import com.squareup.picasso.Picasso;

import org.techconnect.R;
import org.techconnect.analytics.FirebaseEvents;
import org.techconnect.misc.ResourceHandler;
import org.techconnect.model.FlowChart;
import org.techconnect.model.User;
import org.techconnect.services.TCService;
import org.techconnect.sql.TCDatabaseHelper;
import org.techconnect.views.CommentsResourcesTabbedView;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class GuideActivity extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener {

    public static final String EXTRA_CHART = "org.techconnect.guideactivity.flowchart";
    public static final String EXTRA_USER = "org.techconnect.guideactivity.user";
    public static final String EXTRA_ALLOW_REFRESH = "org.techconnect.guideactivity.allow_refresh";

    //Request types
    private static final int LOGIN_REQUEST = 0;

    @Bind(R.id.content_linearLayout)
    LinearLayout contentLinearLayout;
    @Bind(R.id.button)
    Button button;
    @Bind(R.id.header_imageView)
    ImageView headerImageView;
    @Bind(R.id.swipe_refresh_layout)
    SwipeRefreshLayout swipeRefreshLayout;
    @Bind(R.id.description_textView)
    TextView descriptionTextView;
    @Bind(R.id.scrollView)
    ScrollView scrollView;
    @Bind(R.id.slidingCommentLayout)
    SlidingUpPanelLayout slidingUpPanelLayout;
    @Bind(R.id.tabContainer)
    FrameLayout tabContatiner;
    @Bind(R.id.commentsResourcesTabbedView)
    CommentsResourcesTabbedView commentsResourcesTabbedView;
    @Bind(R.id.controlButton)
    ImageButton controlButton;

    private FlowChart flowChart;
    private User user;
    private boolean inDB = true;
    private boolean downloadingChart = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_guide);
        ButterKnife.bind(this);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        controlButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (slidingUpPanelLayout.getPanelState() == SlidingUpPanelLayout.PanelState.EXPANDED) {
                    slidingUpPanelLayout.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);
                    controlButton.setImageDrawable(getResources().getDrawable(R.drawable.ic_keyboard_arrow_up_black_24dp));
                } else {
                    slidingUpPanelLayout.setPanelState(SlidingUpPanelLayout.PanelState.EXPANDED);
                    controlButton.setImageDrawable(getResources().getDrawable(R.drawable.ic_keyboard_arrow_down_black_24dp));
                }
            }
        });
        slidingUpPanelLayout.setFadeOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (scrollView.getScrollY() == 0) {
                    swipeRefreshLayout.setEnabled(true);
                }
                slidingUpPanelLayout.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);
                controlButton.setImageDrawable(getResources().getDrawable(R.drawable.ic_keyboard_arrow_up_black_24dp));
            }
        });

        slidingUpPanelLayout.addPanelSlideListener(new SlidingUpPanelLayout.PanelSlideListener() {
            @Override
            public void onPanelSlide(View panel, float slideOffset) {

            }

            @Override
            public void onPanelStateChanged(View panel, SlidingUpPanelLayout.PanelState previousState, SlidingUpPanelLayout.PanelState newState) {
                if (newState == SlidingUpPanelLayout.PanelState.EXPANDED) {
                    //No swipe, make the button look nice
                    swipeRefreshLayout.setEnabled(false);
                    controlButton.setImageDrawable(getResources().getDrawable(R.drawable.ic_keyboard_arrow_down_black_24dp));
                } else if (newState == SlidingUpPanelLayout.PanelState.COLLAPSED) {
                    if (scrollView.getScrollY() == 0) {
                        swipeRefreshLayout.setEnabled(true);
                    }
                    controlButton.setImageDrawable(getResources().getDrawable(R.drawable.ic_keyboard_arrow_up_black_24dp));
                } else if (newState == SlidingUpPanelLayout.PanelState.DRAGGING) {
                    switch (previousState) {
                        case COLLAPSED:
                            controlButton.setImageDrawable(getResources().getDrawable(R.drawable.ic_keyboard_arrow_down_black_24dp));
                            break;
                        case EXPANDED:
                            controlButton.setImageDrawable(getResources().getDrawable(R.drawable.ic_keyboard_arrow_up_black_24dp));
                            break;
                    }
                }
            }
        });

        final TabLayout tabLayout = (TabLayout) commentsResourcesTabbedView.findViewById(R.id.tab_layout);
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                commentsResourcesTabbedView.setVisibleTab(tab.getPosition());
                if (slidingUpPanelLayout.getPanelState() == SlidingUpPanelLayout.PanelState.COLLAPSED) {
                    slidingUpPanelLayout.setPanelState(SlidingUpPanelLayout.PanelState.EXPANDED);
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
                if (slidingUpPanelLayout.getPanelState() == SlidingUpPanelLayout.PanelState.COLLAPSED) {
                    slidingUpPanelLayout.setPanelState(SlidingUpPanelLayout.PanelState.EXPANDED);
                }
            }
        });

        View ruler = new View(this);
        ruler.setBackgroundColor(getResources().getColor(android.R.color.darker_gray));
        contentLinearLayout.addView(ruler,
                new ViewGroup.LayoutParams(ViewGroup.LayoutParams.FILL_PARENT, 2));


        swipeRefreshLayout.setOnRefreshListener(this);

        scrollView.getViewTreeObserver().addOnScrollChangedListener(new ViewTreeObserver.OnScrollChangedListener() {
            @Override
            public void onScrollChanged() {
                if (scrollView.getScrollY() == 0 && slidingUpPanelLayout.getPanelState() != SlidingUpPanelLayout.PanelState.EXPANDED) {
                    swipeRefreshLayout.setEnabled(true);
                } else {
                    swipeRefreshLayout.setEnabled(false);
                }
            }
        });


        if (getIntent() != null && getIntent().hasExtra(EXTRA_CHART)) {
            flowChart = getIntent().getParcelableExtra(EXTRA_CHART);
            FirebaseEvents.logViewGuide(this, flowChart);
            checkDBForFlowchart();
        }
        if (getIntent() != null && getIntent().hasExtra(EXTRA_ALLOW_REFRESH)) {
            swipeRefreshLayout.setEnabled(getIntent().getBooleanExtra(EXTRA_ALLOW_REFRESH, true));
        }

        if (getIntent() != null && getIntent().hasExtra(EXTRA_USER)) {
            user = getIntent().getParcelableExtra(EXTRA_USER);
        } else {
            user = null;//confirm that it is null
        }

    }

    private void checkDBForFlowchart() {
        inDB = false;
        if (flowChart != null) {
            if (TCDatabaseHelper.get(this).hasChart(flowChart.getId())) {
                inDB = true;
            }
        }
        updateViews();
    }

    @OnClick(R.id.button)
    protected void onFabAction() {
        if (!downloadingChart) {
            if (inDB) {
                onPlay();
            } else {
                onRefresh();
            }
        }
    }

    private void updateViews() {
        if (flowChart != null) {
            setTitle(flowChart.getName());
        }
        if (inDB) {
            button.setText(R.string.start_session);
        } else {
            button.setText(R.string.download);
        }
        descriptionTextView.setText(flowChart.getDescription());
        commentsResourcesTabbedView.setItems(flowChart, flowChart.getResources(), flowChart.getId());
        updateHeaderImage();
    }

    private void updateHeaderImage() {
        if (flowChart.getImage() != null && !TextUtils.isEmpty(flowChart.getImage())) {
            if (ResourceHandler.get(this).hasStringResource(flowChart.getImage())) {
                // Load offline image
                Picasso.with(this)
                        .load(getFileStreamPath(
                                ResourceHandler.get(this).getStringResource(flowChart.getImage())))
                        .placeholder(R.drawable.progress_animation)
                        .into(headerImageView);
            } else {
                // Load online image
                Picasso.with(this)
                        .load(flowChart.getImage())
                        .placeholder(R.drawable.progress_animation)
                        .into(headerImageView);
            }
        } else {
            headerImageView.setVisibility(View.GONE);
        }
    }

    private void onPlay() {
        if (flowChart != null) {
            final Intent intent = new Intent(this, PlayGuideActivity.class);
            intent.putExtra(PlayGuideActivity.EXTRA_CHART_ID, flowChart.getId());
            new AlertDialog.Builder(this)
                    .setTitle("Investigational Application")
                    .setMessage(R.string.disclaimer)
                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.dismiss();
                            startActivity(intent);
                        }
                    }).show();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.activity_guide_toolbar, menu);
        return true;

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            case R.id.delete_guide:
                new AlertDialog.Builder(this)
                        .setTitle("Delete Guide")
                        .setMessage("Would you like to delete this guide?")
                        .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                //Delete guide from SQL?
                                TCDatabaseHelper.get(getApplicationContext()).deleteChart(flowChart);
                                dialogInterface.dismiss();
                                finish();
                            }
                        })
                        .setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.dismiss();
                            }
                        }).show();
        }
        return super.onOptionsItemSelected(item);
    }

    @OnClick(R.id.header_imageView)
    protected void onHeaderImageAction() {
        Intent intent = new Intent(this, ImageViewActivity.class);
        if (ResourceHandler.get(this).hasStringResource(flowChart.getImage())) {
            intent.putExtra(ImageViewActivity.EXTRA_PATH, getFileStreamPath(
                    ResourceHandler.get(this).getStringResource(flowChart.getImage())).getAbsolutePath());
        } else {
            intent.putExtra(ImageViewActivity.EXTRA_URL, flowChart.getImage());
        }
        startActivity(intent);
    }

    @Override
    public void onRefresh() {
        if (flowChart != null && !downloadingChart) {
            swipeRefreshLayout.setRefreshing(true);
            downloadingChart = true;
            button.setText(R.string.updating);
            button.setEnabled(false);
            TCService.startLoadCharts(this, new String[]{flowChart.getId()}, new ResultReceiver(new Handler()) {
                @Override
                protected void onReceiveResult(int resultCode, Bundle resultData) {
                    super.onReceiveResult(resultCode, resultData);
                    downloadingChart = false;
                    button.setEnabled(true);
                    checkDBForFlowchart();
                    swipeRefreshLayout.setRefreshing(false);
                    updateViews();
                }
            });
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        updateViews();
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == LOGIN_REQUEST) {
            if (resultCode == LoginActivity.LOGIN_SUCCESS) {
                //Need to update the user so it's no longer null
                setUser((User) data.getParcelableExtra(EXTRA_USER));
            }
            //In theory, do not need to send another message that login failed
        }
    }

    @Override
    public void onBackPressed() {
        if (slidingUpPanelLayout.getPanelState() == SlidingUpPanelLayout.PanelState.EXPANDED) {
            slidingUpPanelLayout.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);
        } else {
            super.onBackPressed();
        }
    }
}
