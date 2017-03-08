package org.techconnect.asynctasks;

import android.content.Context;
import android.os.AsyncTask;

import org.techconnect.misc.auth.AuthManager;
import org.techconnect.network.TCNetworkHelper;

import java.io.IOException;

/**
 * Created by Phani on 11/13/2016.
 */

public class PostAppFeedbackAsyncTask extends AsyncTask<String, Void, Boolean> {

    private Context context;

    public PostAppFeedbackAsyncTask(Context context) {
        this.context = context;
    }

    @Override
    protected Boolean doInBackground(String... strings) {
        try {
            if (strings.length > 0) {
                if (AuthManager.get(context).hasAuth()) {
                    return new TCNetworkHelper().postAppFeedback(AuthManager.get(context).getAuth().getUserId(), strings[0]);
                } else {
                    return new TCNetworkHelper().postAppFeedback(null, strings[0]);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }
}
