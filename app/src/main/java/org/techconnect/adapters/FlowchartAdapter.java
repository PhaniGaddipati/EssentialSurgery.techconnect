package org.techconnect.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import org.techconnect.R;
import org.techconnect.model.FlowChart;
import org.techconnect.views.GuideListItemView;

/**
 * Created by Phani on 10/23/2016.
 */

public class FlowchartAdapter extends BaseAdapter {

    private FlowChart[] flowCharts;
    private Context context;

    public FlowchartAdapter(Context context, FlowChart[] flowCharts) {
        this.flowCharts = flowCharts;
        this.context = context;
    }

    @Override
    public int getCount() {
        return flowCharts.length;
    }

    @Override
    public FlowChart getItem(int i) {
        return flowCharts[i];
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        GuideListItemView lView;
        if (view == null) {
            lView = (GuideListItemView) LayoutInflater.from(context).inflate(R.layout.guide_list_item_view, viewGroup, false);
        } else {
            lView = (GuideListItemView) view;
        }
        lView.setShowDownload(true);
        lView.setFlowChart(getItem(i));
        return lView;
    }
}
