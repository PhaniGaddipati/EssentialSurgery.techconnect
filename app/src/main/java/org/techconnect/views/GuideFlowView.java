package org.techconnect.views;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.support.design.widget.TabLayout;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.sothree.slidinguppanel.SlidingUpPanelLayout;
import com.squareup.picasso.Picasso;

import org.techconnect.R;
import org.techconnect.activities.ImageViewActivity;
import org.techconnect.misc.ResourceHandler;
import org.techconnect.model.Vertex;
import org.techconnect.model.session.Session;
import org.techconnect.model.session.SessionListener;

import java.io.File;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by Phani on 1/27/2016.
 * <p/>
 * The primary flowchart view. This view shows the question, details, attachments, etc.
 * This flow view updates its content based on the Flowchart_old object given.
 */
public class GuideFlowView extends LinearLayout implements View.OnClickListener {

    @Bind(R.id.question_textView)
    TextView questionTextView;
    @Bind(R.id.details_textView)
    TextView detailsTextView;
    @Bind(R.id.options_linearLayout)
    LinearLayout optionsLinearLayout;
    @Bind(R.id.back_button)
    Button backButton;
    @Bind(R.id.imageViewLinearLayout)
    LinearLayout imageLinearLayout;
    @Bind(R.id.img_preview_hint)
    TextView imgPreviewHintTextView;
    @Bind(R.id.slidingCommentLayout)
    SlidingUpPanelLayout slidingCommentLayout;
    @Bind(R.id.tabContainer)
    FrameLayout tabContainer;
    @Bind(R.id.commentsResourcesTabbedView)
    CommentsResourcesTabbedView commentsResourcesTabbedView;
    @Bind(R.id.controlButton)
    ImageButton controlButton;

    private Session session;
    private SessionListener listener;

    public GuideFlowView(Context context) {
        super(context);
    }

    public GuideFlowView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public GuideFlowView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void setSession(Session session, SessionListener listener) {
        this.listener = listener;
        this.session = session;
        updateViews();
    }

    private void updateViews() {
        Vertex curr_step = session.getCurrentVertex();
        questionTextView.setText(curr_step.getName());
        detailsTextView.setText(curr_step.getDetails());

        optionsLinearLayout.removeAllViews();
        updateImageThumbnails(curr_step);
        updateOptions();
        backButton.setEnabled(session.hasPrevious());

        //tabContainer.removeAllViews();
        //commentsResourcesTabbedView = (CommentsResourcesTabbedView) LayoutInflater.from(getContext())
                //.inflate(R.layout.comments_resources_tabbed_view, tabContainer, false);
        commentsResourcesTabbedView.setItems(curr_step, curr_step.getResources(),
                session.getFlowchart().getId());
        //tabContainer.addView(commentsResourcesTabbedView);

    }


    private void updateOptions() {
        for (int i = 0; i < optionsLinearLayout.getChildCount(); i++) {
            optionsLinearLayout.getChildAt(i).setOnClickListener(null);
        }
        optionsLinearLayout.removeAllViews();
        //Now, want to use
        for (String opt : session.getCurrentOptions()) {
            final String option = opt;
            Button button = new Button(getContext());
            button.setTransformationMethod(null);
            button.setText(option);
            button.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {
                    advanceFlow(option);
                }
            });
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            int dp4 = (int) (4 * Resources.getSystem().getDisplayMetrics().density);
            params.setMargins(0, dp4, 0, dp4);
            button.setLayoutParams(params);
            optionsLinearLayout.addView(button);
        }
    }


    private void updateImageThumbnails(Vertex curr_step) {
        for (int i = 0; i < imageLinearLayout.getChildCount(); i++) {
            imageLinearLayout.getChildAt(i).setOnClickListener(null);
        }
        imageLinearLayout.removeAllViews();
        if (curr_step.hasImages()) {
            imageLinearLayout.setVisibility(VISIBLE);
            List<String> images = curr_step.getImages();
            for (String url : images) {
                if (ResourceHandler.get(getContext()).hasStringResource(url)) {
                    final File file = getContext().getFileStreamPath(ResourceHandler.get(getContext()).getStringResource(url));
                    ImageView imageView = new ImageView(getContext());
                    imageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
                    int pixels = (int) (getResources().getDimension(R.dimen.flow_view_img_preview_width_dp));
                    imageView.setMaxWidth(pixels);
                    imageView.setAdjustViewBounds(true);
                    imageLinearLayout.addView(imageView);
                    imageView.setVisibility(VISIBLE);
                    Picasso.with(getContext())
                            .load(file)
                            .placeholder(R.drawable.progress_animation)
                            .into(imageView);
                    imageView.setOnClickListener(new OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Intent intent = new Intent(getContext(), ImageViewActivity.class);
                            intent.putExtra(ImageViewActivity.EXTRA_PATH, file.getAbsolutePath());
                            getContext().startActivity(intent);
                        }
                    });
                }
            }
            imgPreviewHintTextView.setVisibility(VISIBLE);
        } else {
            imageLinearLayout.setVisibility(GONE);
            imgPreviewHintTextView.setVisibility(INVISIBLE);
        }
    }

    /**
     * Proceed to the next question, or end session if none.
     *
     * @param option
     */
    private void advanceFlow(String option) {
        session.selectOption(option); //Progress to the next vertex
        //This is a little risky, but I think that it works with the existing structure of the graph
        if (!session.getCurrentVertex().hasOutEdges()) {
            if (listener != null) {
                listener.onSessionComplete();
            }
        } else { //Not the end, need to update the views
            updateViews();
        }
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        ButterKnife.bind(this);
        backButton.setOnClickListener(this);
        controlButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (slidingCommentLayout.getPanelState() == SlidingUpPanelLayout.PanelState.EXPANDED) {
                    slidingCommentLayout.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);
                    controlButton.setImageDrawable(getResources().getDrawable(R.drawable.ic_keyboard_arrow_up_black_24dp));
                } else {
                    slidingCommentLayout.setPanelState(SlidingUpPanelLayout.PanelState.EXPANDED);
                    controlButton.setImageDrawable(getResources().getDrawable(R.drawable.ic_keyboard_arrow_down_black_24dp));
                }
            }
        });
        slidingCommentLayout.setTouchEnabled(true);//Want total control in my hands
        slidingCommentLayout.setFadeOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                slidingCommentLayout.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);
                controlButton.setImageDrawable(getResources().getDrawable(R.drawable.ic_keyboard_arrow_up_black_24dp));
            }
        });

        slidingCommentLayout.addPanelSlideListener(new SlidingUpPanelLayout.PanelSlideListener() {
            @Override
            public void onPanelSlide(View panel, float slideOffset) {

            }

            @Override
            public void onPanelStateChanged(View panel, SlidingUpPanelLayout.PanelState previousState, SlidingUpPanelLayout.PanelState newState) {
                if (newState == SlidingUpPanelLayout.PanelState.EXPANDED) {
                    //No swipe, make the button look nice
                    controlButton.setImageDrawable(getResources().getDrawable(R.drawable.ic_keyboard_arrow_down_black_24dp));
                } else if (newState == SlidingUpPanelLayout.PanelState.COLLAPSED) {
                    controlButton.setImageDrawable(getResources().getDrawable(R.drawable.ic_keyboard_arrow_up_black_24dp));
                } else if (newState == SlidingUpPanelLayout.PanelState.DRAGGING) {
                    switch(previousState) {
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
                if (slidingCommentLayout.getPanelState() == SlidingUpPanelLayout.PanelState.COLLAPSED) {
                    slidingCommentLayout.setPanelState(SlidingUpPanelLayout.PanelState.EXPANDED);
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
                if (slidingCommentLayout.getPanelState() == SlidingUpPanelLayout.PanelState.COLLAPSED) {
                    slidingCommentLayout.setPanelState(SlidingUpPanelLayout.PanelState.EXPANDED);
                }
            }
        });

    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.back_button) {
            goBack();
        }
    }

    public boolean goBack() {
        if (session.hasPrevious()) {
            session.goBack();
            updateViews();
            return true;
        }
        return false;
    }

    public boolean closeResourceMenu() {
        if (slidingCommentLayout.getPanelState() == SlidingUpPanelLayout.PanelState.EXPANDED) {
            slidingCommentLayout.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);
            return true;
        } else {
            return false;
        }
    }
}
