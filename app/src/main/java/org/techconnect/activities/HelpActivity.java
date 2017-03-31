package org.techconnect.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;

import org.techconnect.R;
import org.techconnect.asynctasks.PostAppFeedbackAsyncTask;
import org.techconnect.dialogs.SendFeedbackDialogFragment;

import butterknife.Bind;
import butterknife.ButterKnife;

public class HelpActivity extends AppCompatActivity {

    @Bind(R.id.activity_help)
    RelativeLayout helpLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_help);
        ButterKnife.bind(this);
        String[] options = new String[] {"View Tutorial","Send feedback"};
        ArrayAdapter adapter = new ArrayAdapter(this,android.R.layout.simple_list_item_1,options);
        ListView listview = new ListView(this);
        listview.setAdapter(adapter);
        //Set the on item click listener
        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                switch(i) {
                    case 0:
                        Intent intent = new Intent(HelpActivity.this, IntroTutorial.class);
                        startActivity(intent);
                        break;
                    case 1:
                        onSendFeedback();
                        break;
                }
            }
        });

        helpLayout.addView(listview);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void onSendFeedback() {
        final SendFeedbackDialogFragment dialogFragment = new SendFeedbackDialogFragment();
        dialogFragment.setListener(new SendFeedbackDialogFragment.FeedbackListener() {
            @Override
            public void onYes(String text) {
                new PostAppFeedbackAsyncTask(HelpActivity.this) {
                    @Override
                    protected void onPostExecute(Boolean success) {
                        if (success) {
                            Snackbar.make(helpLayout, R.string.feedback_success, Snackbar.LENGTH_LONG).show();
                        } else {
                            Snackbar.make(helpLayout, R.string.feedback_fail, Snackbar.LENGTH_LONG).show();
                        }
                    }
                }.execute(text);
                dialogFragment.dismiss();
            }

            @Override
            public void onNo() {
                dialogFragment.dismiss();
            }
        });
        dialogFragment.show(getFragmentManager(), "sendFeedback");
    }
}
