package org.techconnect.asynctasks;

import android.content.Context;
import android.os.AsyncTask;

import org.techconnect.model.User;
import org.techconnect.network.TCNetworkHelper;
import org.techconnect.sql.TCDatabaseHelper;

import java.io.IOException;
import java.util.List;

/**
 * Created by doranwalsten on 11/13/16.
 */

public class SearchUsersAsyncTask extends AsyncTask<Void, List<User>, List<User>> {

    private Context context;
    private String query;
    private int limit;
    private int skip;

    public SearchUsersAsyncTask(Context context, String query, int limit, int skip) {
        this.context = context;
        this.query = query;
        this.limit = limit;
        this.skip = skip;
    }

    /**
     * Returns search results, or null if it could not be completed.
     *
     * @param voids
     * @return
     */
    @Override
    protected List<User> doInBackground(Void... voids) {
        try {
            List<User> users = new TCNetworkHelper().searchUsers(query, limit, skip);
            for (User user : users) {
                TCDatabaseHelper.get(context).upsertUser(user);
            }
            return users;
        } catch (IOException ex) {
            return null;
        }
    }
}

