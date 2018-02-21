package org.techconnect.asynctasks;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;

import org.techconnect.R;
import org.techconnect.sql.TCDatabaseHelper;

/**
 * Created by doranwalsten on 2/8/17.
 */

public class ExportResponsesAsyncTask extends AsyncTask<String, Void, Integer> {

    private String message = null;
    private Context context;

    public ExportResponsesAsyncTask(Context context) {
        this.context = context;
    }


    @Override
    protected Integer doInBackground(String... strings) {
        //Get generated string
        String resp = message;
        if (resp == null) {
            if (strings.length > 0) {
                String r = TCDatabaseHelper.get(context).writeResponsesToString(strings[0]);
                resp = String.format("%s\n%s", context.getString(R.string.exportResponse_autofill), r);
            } else {
                resp = context.getString(R.string.exportResponse_autofill);
            }
        }

        if (resp != null) {
            //Send email based on String arguments
            Intent emailIntent = new Intent(Intent.ACTION_SEND);
            emailIntent.putExtra(Intent.EXTRA_TEXT, resp);
            emailIntent.putExtra(Intent.EXTRA_SUBJECT, context.getString(R.string.exportResponse_subject));
            emailIntent.setType("text/plain");
            context.startActivity(Intent.createChooser(emailIntent, "Select App"));
            return 1;
        } else {
            return 0;
        }
    }
}
