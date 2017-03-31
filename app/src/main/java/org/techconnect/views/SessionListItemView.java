package org.techconnect.views;

import android.content.Context;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import android.widget.CheckBox;
import android.widget.Checkable;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import org.techconnect.R;
import org.techconnect.misc.CircleTransform;
import org.techconnect.misc.ResourceHandler;
import org.techconnect.model.FlowChart;
import org.techconnect.model.session.Session;

import java.text.SimpleDateFormat;
import java.util.Date;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by dwalsten on 11/23/16.
 */

public class SessionListItemView extends LinearLayout implements Checkable {

    //Add all of the Butterknife Binds
    @Bind(R.id.device_imageView)
    ImageView devImageView;
    @Bind(R.id.sessionCheckbox)
    CheckBox sessionCheckbox;
    @Bind(R.id.title_textView)
    TextView titleTextView;
    @Bind(R.id.numbers_textView)
    TextView numbersTextView;
    @Bind(R.id.date_textView)
    TextView dateTextView;

    private Session session;
    private boolean checklist = false; //true: part of checklist, false: not part of checklist

    public SessionListItemView(Context context) {
        super(context);
    }

    public SessionListItemView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public SessionListItemView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public Session getSession() {
        return this.session;
    }

    public void setSession(Session s) {
        this.session = s;
        updateViews();
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        ButterKnife.bind(this);
        updateViews();
    }


    /**
     * Use this mehod to update the fields on the textviews based on the session
     */
    private void updateViews() {
        if (session != null) {
            if (session.getModelNumber() == null || TextUtils.isEmpty(session.getModelNumber()) ||
                    session.getSerialNumber() == null || TextUtils.isEmpty(session.getSerialNumber())) {
                //Nothing for now
            } else {
                numbersTextView.setText(String.format("%s, %s", session.getModelNumber(), session.getSerialNumber()));
            }

            if (session.getManufacturer() == null || TextUtils.isEmpty(session.getManufacturer())) {
                titleTextView.setText(session.getDeviceName());
            } else {
                titleTextView.setText(String.format("%s, %s", session.getDeviceName(), session.getManufacturer()));
            }


            dateTextView.setText(new SimpleDateFormat("MM/dd/yyyy, HH:mm:ss").format(new Date(session.getCreatedDate())));

            if (!checklist) {
                devImageView.setVisibility(View.VISIBLE);
                sessionCheckbox.setVisibility(View.GONE);
                //Used the stored Flowchart to get the associated image needed for the image view
                if (session.hasChart()) {
                    FlowChart flowChart = session.getFlowchart();
                    if (flowChart.getImage() != null && !TextUtils.isEmpty(flowChart.getImage())) {
                        if (ResourceHandler.get(getContext()).hasStringResource(flowChart.getImage())) {
                            // Load offline image
                            Picasso.with(getContext())
                                    .load(getContext().getFileStreamPath(
                                            ResourceHandler.get(getContext()).getStringResource(flowChart.getImage())))
                                    .fit()
                                    .error(R.drawable.flowchart_icon)
                                    .transform(new CircleTransform())
                                    .into(devImageView);
                        } else {
                            // Try to load from online
                            Picasso.with(getContext())
                                    .load(flowChart.getImage())
                                    .fit()
                                    .error(R.drawable.flowchart_icon)
                                    .transform(new CircleTransform())
                                    .into(devImageView);
                        }
                    } else {
                        Picasso.with(getContext())
                                .load(R.drawable.flowchart_icon)
                                .fit()
                                .transform(new CircleTransform())
                                .into(devImageView);
                    }
                    dateTextView.setVisibility(View.VISIBLE);
                    devImageView.setVisibility(View.VISIBLE);
                } else {
                    Picasso.with(getContext())
                            .load(R.drawable.flowchart_icon)
                            .fit()
                            .transform(new CircleTransform())
                            .into(devImageView);
                }
            } else {
                devImageView.setVisibility(View.GONE);
                sessionCheckbox.setVisibility(View.VISIBLE);
            }
        } else {
            numbersTextView.setText("No Session Attached");
            dateTextView.setVisibility(View.GONE);
            devImageView.setVisibility(View.GONE);
        }
    }


    public boolean isChecklist() {
        return checklist;
    }

    public void setChecklist(boolean checklist) {
        this.checklist = checklist;
    }

    public boolean isChecked() {
        return sessionCheckbox.isChecked();
    }

    @Override
    public void toggle() {
        if (sessionCheckbox != null) {
            sessionCheckbox.toggle();
        }
    }

    public void setChecked(boolean b) {
        sessionCheckbox.setChecked(b);
        sessionCheckbox.refreshDrawableState();
    }
}
