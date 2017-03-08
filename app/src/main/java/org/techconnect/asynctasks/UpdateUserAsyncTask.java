package org.techconnect.asynctasks;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import org.techconnect.misc.auth.AuthManager;
import org.techconnect.model.User;
import org.techconnect.network.TCNetworkHelper;

import java.io.IOException;

/**
 * Created by Phani on 10/31/2016.
 */

public class UpdateUserAsyncTask extends AsyncTask<User, Void, User> {

    private Context context;

    public UpdateUserAsyncTask(Context context) {
        this.context = context;
    }

    @Override
    protected User doInBackground(User... users) {
        TCNetworkHelper helper = new TCNetworkHelper();
        if (users.length > 0) {
            try {
                Log.d("Update User",users[0].getEmail());
                return helper.updateUser(users[0], AuthManager.get(context).getAuth());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }
}
