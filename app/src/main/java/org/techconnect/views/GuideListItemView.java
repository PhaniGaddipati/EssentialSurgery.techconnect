package org.techconnect.views;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.ResultReceiver;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import org.techconnect.R;
import org.techconnect.misc.CircleTransform;
import org.techconnect.misc.ResourceHandler;
import org.techconnect.model.FlowChart;
import org.techconnect.services.TCService;
import org.techconnect.sql.TCDatabaseHelper;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by Phani on 10/23/2016.
 */

public class GuideListItemView extends LinearLayout implements View.OnClickListener {

    @Bind(R.id.guide_imageView)
    ImageView guideImageView;
    @Bind(R.id.name_textView)
    TextView nameTextView;
    @Bind(R.id.description_textView)
    TextView descriptionTextView;
    @Bind(R.id.downloadImageView)
    ImageView downloadImageView;
    @Bind(R.id.downloadProgressBar)
    ProgressBar downloadProgressBar;

    private FlowChart flowChart = null;
    private boolean showDownload = false;

    public GuideListItemView(Context context) {
        super(context);
    }

    public GuideListItemView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public GuideListItemView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        ButterKnife.bind(this);
        updateViews();
    }

    public void setShowDownload(boolean showDownload) {
        this.showDownload = showDownload;
    }

    private void updateViews() {
        downloadImageView.setOnClickListener(null);
        downloadImageView.setVisibility(GONE);
        downloadProgressBar.setVisibility(GONE);
        if (this.flowChart == null) {
            nameTextView.setText("");
            descriptionTextView.setText("");
            Picasso.with(getContext())
                    .load(R.drawable.flowchart_icon)
                    .fit()
                    .transform(new CircleTransform())
                    .into(guideImageView);
        } else {
            nameTextView.setText(flowChart.getName());
            descriptionTextView.setText(flowChart.getDescription());
            if (showDownload) {
                downloadImageView.setVisibility(VISIBLE);
                if (TCDatabaseHelper.get(getContext()).getChart(flowChart.getId()) == null) {
                    downloadImageView.setImageResource(R.drawable.ic_file_download_black_48dp);
                } else {
                    downloadImageView.setOnClickListener(null);
                    downloadImageView.setImageResource(R.drawable.ic_done_black_48dp);
                }
                downloadImageView.setOnClickListener(this);
            }
            if (flowChart.getImage() != null && !TextUtils.isEmpty(flowChart.getImage())) {
                if (ResourceHandler.get(getContext()).hasStringResource(flowChart.getImage())) {
                    // Load offline image
                    Picasso.with(getContext())
                            .load(getContext().getFileStreamPath(
                                    ResourceHandler.get(getContext()).getStringResource(flowChart.getImage())))
                            .fit()
                            .error(R.drawable.flowchart_icon)
                            .transform(new CircleTransform())
                            .into(guideImageView);
                } else {
                    // Try to load from online
                    Picasso.with(getContext())
                            .load(flowChart.getImage())
                            .fit()
                            .error(R.drawable.flowchart_icon)
                            .transform(new CircleTransform())
                            .into(guideImageView);
                }
            } else {
                Picasso.with(getContext())
                        .load(R.drawable.flowchart_icon)
                        .fit()
                        .transform(new CircleTransform())
                        .into(guideImageView);
            }
        }
    }

    public FlowChart getFlowChart() {
        return flowChart;
    }

    public void setFlowChart(FlowChart flowChart) {
        this.flowChart = flowChart;
        updateViews();
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.downloadImageView) {
            onDownload();
        }
    }

    private void onDownload() {
        downloadImageView.setOnClickListener(null);
        downloadImageView.setVisibility(GONE);
        downloadProgressBar.setVisibility(VISIBLE);
        TCService.startLoadCharts(getContext(), new String[]{flowChart.getId()}, new ResultReceiver(new Handler()) {
            @Override
            protected void onReceiveResult(int resultCode, Bundle resultData) {
                super.onReceiveResult(resultCode, resultData);
                updateViews();
            }
        });
    }
}
