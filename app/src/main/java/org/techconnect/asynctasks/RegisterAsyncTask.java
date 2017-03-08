package org.techconnect.asynctasks;

import android.os.AsyncTask;

import org.techconnect.model.User;
import org.techconnect.network.TCNetworkHelper;

import java.io.IOException;

/**
 * Created by Phani on 10/31/2016.
 */

public class RegisterAsyncTask extends AsyncTask<Void, Void, User> {
    private final String country;
    private final String name;
    private final String email;
    private final String org;
    private final String password;
    private final String[] skillsArr;

    public RegisterAsyncTask(String country, String name, String email, String org, String password, String[] skillsArr) {
        this.country = country;
        this.name = name;
        this.email = email;
        this.org = org;
        this.password = password;
        this.skillsArr = skillsArr;
    }

    @Override
    protected User doInBackground(Void... voids) {
        try {
            return new TCNetworkHelper().register(email, password, country, name, org, skillsArr);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}
