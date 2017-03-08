package org.techconnect.asynctasks;

import android.os.AsyncTask;
import android.util.Log;

import org.techconnect.model.FlowChart;
import org.techconnect.model.UserAuth;
import org.techconnect.network.TCNetworkHelper;

import java.io.IOException;

/**
 * Created by doranwalsten on 1/31/17.
 */

public class PostVoteAsyncTask extends AsyncTask<Void, Void, FlowChart> {

    private String flowchart_id;
    private String vote;
    private UserAuth auth;
    private boolean clear;

    public PostVoteAsyncTask(String flowchart_id, String vote, UserAuth auth, boolean clear) {
        this.flowchart_id = flowchart_id;
        this.vote = vote;
        this.auth = auth;
        this.clear = clear;
    }


    @Override
    protected FlowChart doInBackground(Void... voids) {
        TCNetworkHelper helper = new TCNetworkHelper();
        try {
            return helper.postFeedback(flowchart_id,vote,auth,clear);
        } catch (IOException e) {
            Log.e(getClass().toString(),e.getMessage());
            return null;
        }

    }
}
