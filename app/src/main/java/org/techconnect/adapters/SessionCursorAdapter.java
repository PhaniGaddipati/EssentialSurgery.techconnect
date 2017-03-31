package org.techconnect.adapters;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;

import org.techconnect.R;
import org.techconnect.sql.TCDatabaseHelper;
import org.techconnect.views.SessionListItemView;

/**
 * Created by dwalsten on 11/23/16.
 */

public class SessionCursorAdapter extends CursorAdapter {

    boolean isChecklist = false;
    public SessionCursorAdapter(Context context, boolean isChecklist) {
        super(context, null, false); //Want it to start as null
        this.isChecklist = isChecklist;
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup viewGroup) {
        return LayoutInflater.from(context).inflate(R.layout.session_list_item_view, viewGroup, false);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        SessionListItemView sessionListItemView = (SessionListItemView) view;
        sessionListItemView.setChecklist(isChecklist);
        sessionListItemView.setSession(TCDatabaseHelper.get(context).getSessionFromCursor(cursor));

    }
}
