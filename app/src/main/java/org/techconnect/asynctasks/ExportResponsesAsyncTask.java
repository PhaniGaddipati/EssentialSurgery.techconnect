package org.techconnect.asynctasks;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;

import org.techconnect.R;
import org.techconnect.model.session.Session;
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
        int sub_id = R.string.exportResponse_subject;
        if (resp == null) {
            if (strings.length > 0) {
                Session sesh = TCDatabaseHelper.get(context).getSession(strings[0]);
                String r = TCDatabaseHelper.get(context).writeResponsesToString(strings[0]);
                int msg_id = sesh.isFinished() ? R.string.exportResponse_autofill_finished : R.string.exportResponse_autofill;
                sub_id = sesh.isFinished() ? R.string.exportResponse_subject_finished : R.string.exportResponse_subject;
                resp = String.format("%s\n%s", context.getString(msg_id), r);
            } else {
                resp = context.getString(R.string.exportResponse_autofill);
            }
        }

        if (resp != null) {
            //Send email based on String arguments
            String uriText =
                    "mailto:" + Uri.encode(context.getString(R.string.techassist_email)) +
                            "?subject=" + Uri.encode(context.getString(sub_id)) +
                            "&body=" + Uri.encode(resp);

            Intent emailIntent = new Intent(Intent.ACTION_SEND, Uri.parse(uriText));
            emailIntent.putExtra(Intent.EXTRA_TEXT, resp);
            emailIntent.putExtra(Intent.EXTRA_SUBJECT, context.getString(sub_id));
            emailIntent.putExtra(Intent.EXTRA_EMAIL, new String[]{context.getString(R.string.techassist_email)});
            emailIntent.setType("text/plain");
            context.startActivity(Intent.createChooser(emailIntent, "Select App"));
            return 1;
        } else {
            return 0;
        }
    }
}
