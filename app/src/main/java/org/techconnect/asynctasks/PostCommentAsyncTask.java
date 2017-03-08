package org.techconnect.asynctasks;

import android.content.Context;
import android.os.AsyncTask;

import org.techconnect.analytics.FirebaseEvents;
import org.techconnect.misc.auth.AuthManager;
import org.techconnect.model.Comment;
import org.techconnect.network.TCNetworkHelper;
import org.techconnect.sql.TCDatabaseHelper;

import java.io.IOException;

/**
 * Created by Phani on 10/27/2016.
 */

public class PostCommentAsyncTask extends AsyncTask<Void, Void, Comment> {

    private Context context;
    private String chartId;
    private Comment comment;

    public PostCommentAsyncTask(Context context, String chartId, Comment comment) {
        this.context = context;
        this.chartId = chartId;
        this.comment = comment;
    }

    @Override
    protected Comment doInBackground(Void... voids) {
        TCNetworkHelper tc = new TCNetworkHelper();
        try {
            Comment postedComment = tc.comment(chartId, comment, AuthManager.get(context).getAuth());
            if (comment.getNodeId() != null) {
                TCDatabaseHelper.get(context).insertComment(postedComment, comment.getNodeId(), Comment.PARENT_TYPE_VERTEX);
            } else {
                TCDatabaseHelper.get(context).insertComment(postedComment, chartId, Comment.PARENT_TYPE_CHART);
            }
            FirebaseEvents.logPostComment(context);
            return postedComment;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
