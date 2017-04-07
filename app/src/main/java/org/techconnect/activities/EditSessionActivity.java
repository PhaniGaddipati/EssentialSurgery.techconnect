package org.techconnect.activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

import org.techconnect.R;
import org.techconnect.model.session.Session;
import org.techconnect.sql.TCDatabaseHelper;

import butterknife.Bind;
import butterknife.ButterKnife;

public class EditSessionActivity extends AppCompatActivity {

    @Bind(R.id.department_editText)
    EditText departmentEditText;
    @Bind(R.id.manufacturer_editText)
    EditText manufacturerEditText;
    @Bind(R.id.model_editText)
    EditText modelEditText;
    @Bind(R.id.serial_editText)
    EditText serialEditText;

    @Bind(R.id.problemEditText)
    EditText problemEditText;
    @Bind(R.id.solutionEditText)
    EditText solutionEditText;
    @Bind(R.id.notesEditText)
    EditText notesEditText;

    public static String EXTRA_SESSION = "org.techconnect.sessionactivity.session"; //Extra with the session to edit

    //Intent result codes
    public static int SESSION_SAME = 0;
    public static int SESSION_CHANGE = 1;

    private Session session;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_session);
        ButterKnife.bind(this);

        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        if (getIntent().hasExtra(EXTRA_SESSION)) {
            //extract the session
            session = getIntent().getParcelableExtra(EXTRA_SESSION);
        } else {
            //Report some sort of error and finish
            Log.e(getClass().toString(),"No Session Attached");
            Toast.makeText(this,"Error in accessing session",Toast.LENGTH_SHORT).show();
            finish();
        }

        updateViews();
    }

    private void updateViews() {
        //Setup the EditText fields with the currently entered information
        if (session != null) {
            departmentEditText.setText(session.getDepartment());
            manufacturerEditText.setText(session.getManufacturer());
            modelEditText.setText(session.getModelNumber());
            serialEditText.setText(session.getSerialNumber());

            problemEditText.setText(session.getProblem());
            solutionEditText.setText(session.getSolution());
            notesEditText.setText(session.getNotes());
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.activity_editsession_toolbar, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case android.R.id.home:
                //Ask if discard changes
                if (isEdited()) {
                    new AlertDialog.Builder(this)
                            .setMessage("Discard your changes?")
                            .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    dialogInterface.dismiss();
                                    finish();
                                }
                            })
                            .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    dialogInterface.dismiss();
                                }
                            }).show();
                } else {
                    finish();
                }
                break;
            case R.id.acceptChanges:
                //save changes
                saveChanges();
                Intent result = new Intent();
                result.putExtra(SessionActivity.EXTRA_SESSION,session);
                setResult(SESSION_CHANGE,result);
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void saveChanges() {
        //Get text from each field,update session, upsert in database
        session.setDepartment(departmentEditText.getText().toString());
        session.setManufacturer(manufacturerEditText.getText().toString());
        session.setModelNumber(modelEditText.getText().toString());
        session.setSerialNumber(serialEditText.getText().toString());

        session.setProblem(problemEditText.getText().toString());
        session.setSolution(solutionEditText.getText().toString());
        session.setNotes(notesEditText.getText().toString());

        TCDatabaseHelper.get(this).upsertSession(session); //Update session in database
    }

    /**
     * Lame helper method to determine if any of the fields changed
     * @return
     */
    private boolean isEdited() {
        if (!session.getDepartment().equals(departmentEditText.getText().toString())) {
            return true;
        } else if (!session.getManufacturer().equals(manufacturerEditText.getText().toString())) {
            return true;
        } else if (!session.getModelNumber().equals(modelEditText.getText().toString())) {
            return true;
        } else if (!session.getSerialNumber().equals(serialEditText.getText().toString())) {
            return true;
        } else if (!session.getProblem().equals(problemEditText.getText().toString())) {
            return true;
        } else if(!session.getSolution().equals(solutionEditText.getText().toString())) {
            return true;
        } else if (!session.getNotes().equals(notesEditText.getText().toString())) {
            return true;
        } else {
            return false;
        }
    }
}
