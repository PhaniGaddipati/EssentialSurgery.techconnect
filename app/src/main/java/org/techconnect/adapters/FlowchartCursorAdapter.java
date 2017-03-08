package org.techconnect.adapters;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;

import org.centum.techconnect.R;
import org.techconnect.sql.TCDatabaseHelper;
import org.techconnect.views.GuideListItemView;

/**
 * Created by Phani on 10/23/2016.
 */

public class FlowchartCursorAdapter extends CursorAdapter {

    public FlowchartCursorAdapter(Context context) {
        super(context, TCDatabaseHelper.get(context).getAllFlowchartsCursor(), false);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup viewGroup) {
        return LayoutInflater.from(context).inflate(R.layout.guide_list_item_view, viewGroup, false);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        GuideListItemView listItemView = ((GuideListItemView) view);
        listItemView.setFlowChart(TCDatabaseHelper.get(context).getChartStubFromCursor(cursor));
    }


}
