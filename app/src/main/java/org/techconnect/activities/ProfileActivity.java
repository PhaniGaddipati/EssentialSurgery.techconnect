package org.techconnect.activities;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import org.techconnect.R;
import org.techconnect.analytics.FirebaseEvents;
import org.techconnect.asynctasks.UpdateUserAsyncTask;
import org.techconnect.misc.auth.AuthManager;
import org.techconnect.model.User;
import org.techconnect.sql.TCDatabaseHelper;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ProfileActivity extends AppCompatActivity {
    //Do all of the butterknife binding
    @Bind(R.id.profile_work)
    TextView org;
    @Bind(R.id.profile_email)
    TextView emailTextView;
    @Bind(R.id.skills_table)
    TableLayout skills_table;

    //All of the editable text fields
    @Bind(R.id.edit_work_layout)
    TextInputLayout edit_org_layout;
    @Bind(R.id.edit_work_text)
    EditText edit_org;

    //All of the edit buttons
    @Bind(R.id.edit_work_button)
    ImageButton editWork;
    @Bind(R.id.edit_skill_button)
    ImageButton editSkill;
    @Bind(R.id.save_button)
    Button saveButton;
    @Bind(R.id.discard_button)
    Button discardButton;

    //Intent requests
    public static int SKILL_REQUEST = 0;

    //Extra types
    public static String EXTRA_SKILL = "org.techconnect.profileactivity.skill";

    private List<ImageButton> row_buttons;
    private User head_user; //In cases without editing, this is only user needed
    private User temp_user; //In cases with editing, need temporary user to store changes until committed

    private boolean isEditable;
    private boolean isEditing = true; //True = work, false = skills

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        ButterKnife.bind(this);

        //Here, we access the current User from the Database, create a temporary user in case we need to update
        head_user = getIntent().getExtras().getParcelable("user");
        FirebaseEvents.logViewProfile(this, head_user);

        //Setup whether the user can edit this profile
        isEditable = AuthManager.get(this).hasAuth() && head_user.get_id().equals(AuthManager.get(this).getAuth().getUserId());

        //Only setup the temp user and skills in case where user is actual user
        if (isEditable) {
            try {
                temp_user = head_user.clone();
            } catch (CloneNotSupportedException e) {
                Log.e("Profile", e.getMessage());
            }
        } else {
            editWork.setVisibility(View.GONE);
            editSkill.setVisibility(View.GONE);
        }

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        setTitle(head_user.getName());
        setupProfile();
    }

    //This comes from the Options Menu on the upper right
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_profile, menu);
        MenuItem item = menu.findItem(R.id.action_settings);
        item.setVisible(false);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            return true;
        } else if (id == android.R.id.home) {
            onBackPressed();
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        if (saveButton.getVisibility() == View.VISIBLE) {
            new AlertDialog.Builder(this)
                    .setTitle(R.string.save_changes)
                    .setMessage(R.string.save_changes_msg)
                    .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.dismiss();
                            updateUser();
                        }
                    })
                    .setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.dismiss();
                            finish();
                        }
                    }).show();
        } else {
            super.onBackPressed();
        }
    }

    @OnClick(R.id.profile_email)
    public void onEmailClicked() {
        /* For the sake of testing, do not start a new activity to email the person in the profile
        FirebaseEvents.logEmailClicked(this, head_user);
        Intent emailIntent = new Intent(Intent.ACTION_SENDTO, Uri.parse("mailto:" + emailTextView.getText().toString()));
        startActivity(Intent.createChooser(emailIntent, "Email"));
        */
        Toast.makeText(this,"Send an email",Toast.LENGTH_SHORT).show();
    }

    private TableRow onRowAddRequest() {
        final TableRow toAdd;
        final ImageButton icon;
        final EditText add_skill;
        final TextView skill_text;

        toAdd = (TableRow) getLayoutInflater().inflate(R.layout.tablerow_skill, null, false);
        icon = (ImageButton) toAdd.findViewById(R.id.skill_icon);
        icon.setImageResource(R.drawable.ic_add_box_black_24dp);
        add_skill = (EditText) toAdd.findViewById(R.id.edit_skill_text);
        skill_text = (TextView) toAdd.findViewById(R.id.skill_text);
        skill_text.setText("Add Skill");

        add_skill.setVisibility(View.GONE);
        skill_text.setVisibility(View.VISIBLE);

        icon.setOnClickListener(new View.OnClickListener() {
            boolean adding = true; //Initially, adding a new row

            @Override
            public void onClick(View view) {
                if (adding) {
                    adding = false;
                    icon.setImageResource(R.drawable.ic_close_black_24dp);
                    //User wants to add something
                    Intent intent = new Intent(ProfileActivity.this, SelectSkillActivity.class);
                    startActivityForResult(intent,SKILL_REQUEST);
                    onRowAddRequest();
                } else {
                    //We want to delete the entire row that it belongs to
                    skills_table.removeViewAt(row_buttons.indexOf(icon));
                    row_buttons.remove(icon);
                }
            }
        });

        row_buttons.add(icon);
        skills_table.addView(toAdd);
        return toAdd;
    }

    @OnClick(R.id.save_button)
    public void updateUser() {
        //Use the temp_user object to write any user changes to the database
        final Context context = this;

        //Update organization if necessary
        if (isEditing) {
            org.setVisibility(View.VISIBLE);
            if (edit_org.getText().toString().trim().length() > 0) {
                org.setText(edit_org.getText());
                temp_user.setOrganization(edit_org.getText().toString());
            }
            edit_org_layout.setVisibility(View.GONE);
        } else { //Run through the rows of the skills view and add to tmp skills
            ArrayList<String> skills = new ArrayList<String>();
            for (int i = 0; i < skills_table.getChildCount() - 1; i++) {
                TableRow r = (TableRow) skills_table.getChildAt(i);
                TextView skillText = (TextView) r.findViewById(R.id.skill_text);
                //If text is present, add to skills
                if (skillText.getText().toString().trim().length() > 0) {
                    skills.add(skillText.getText().toString().trim());
                }
            }
            
            temp_user.setExpertises(skills);
        }

        new UpdateUserAsyncTask(context) {
            ProgressDialog pd;

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                pd = ProgressDialog.show(ProfileActivity.this, getString(R.string.save_changes), null, true, false);
            }

            @Override
            protected void onPostExecute(User u) {
                pd.dismiss();
                pd = null;
                if (u != null) {
                    //Log.d("Update User", u.getEmail());
                    TCDatabaseHelper.get(context).upsertUser(u);
                    for (String s: u.getExpertises()) {
                        Log.d("Update user",s);
                    }
                    head_user = TCDatabaseHelper.get(context).getUser(temp_user.get_id());
                    skills_table.removeAllViews(); //Clear out all previous rows
                    org.setVisibility(View.VISIBLE);
                    setupProfile();
                } else {
                    new AlertDialog.Builder(context)
                            .setTitle(R.string.error)
                            .setMessage(R.string.failed_update_user)
                            .show();
                    discardUserChanges();

                }
            }
        }.execute(temp_user);

        //Restore the edit and save buttons
        editWork.setVisibility(View.VISIBLE);
        editSkill.setVisibility(View.VISIBLE);
        saveButton.setVisibility(View.GONE);
        discardButton.setVisibility(View.GONE);


    }

    @OnClick(R.id.discard_button)
    public void discardUserChanges() {
        //Want to restore the original user (head_user)
        skills_table.removeAllViews(); //Clear out all previous rows
        org.setVisibility(View.VISIBLE);
        edit_org_layout.setVisibility(View.GONE);
        setupProfile();
        try {
            temp_user = head_user.clone();
        } catch (CloneNotSupportedException e) {
            Log.e("Profile", e.getMessage());
        }
        //Restore the edit and save buttons
        editWork.setVisibility(View.VISIBLE);
        editSkill.setVisibility(View.VISIBLE);
        saveButton.setVisibility(View.GONE);
        discardButton.setVisibility(View.GONE);
    }


    private void setupProfile() {
        //Add organization, emailTextView, and skills to the list below
        org.setText(head_user.getOrganization());
        emailTextView.setText(head_user.getEmail());

        //Create all rows from list of skills in profile
        row_buttons = new ArrayList<ImageButton>(); //Store reference of where buttons are

        if (head_user.getExpertises().size() == 0) {
            //Current User lists no skills
            TableRow toAdd = (TableRow) getLayoutInflater().inflate(R.layout.tablerow_skill, null, false);
            ImageButton row_button = (ImageButton) toAdd.findViewById(R.id.skill_icon);
            TextView toAddText = (TextView) toAdd.findViewById(R.id.skill_text);

            //Make the button invisible
            row_button.setVisibility(View.INVISIBLE);
            row_button.setClickable(false);
            //Chance the text to be appropriate to having no skills
            toAddText.setText(R.string.no_skills);
            toAddText.setTextColor(Color.GRAY);
            toAddText.setVisibility(View.VISIBLE);
            //Still need to cancel out the edit text view
            TextInputLayout addSkill = (TextInputLayout) toAdd.findViewById(R.id.edit_skill_layout);
            addSkill.setVisibility(View.GONE);
            skills_table.addView(toAdd);
        } else {
            for (int i = 0; i < head_user.getExpertises().size(); i++) {
                TableRow toAdd = (TableRow) getLayoutInflater().inflate(R.layout.tablerow_skill, null, false);
                final ImageButton row_button = (ImageButton) toAdd.findViewById(R.id.skill_icon);
                row_buttons.add(row_button);
                row_button.setTag(i); //View that the button belongs to

                //Don't know if I should set the click listener every time, but doing it for now
                row_button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        //We want to delete the entire row that it belongs to
                        skills_table.removeViewAt(row_buttons.indexOf(row_button));
                        row_buttons.remove(row_button);
                    }
                });
                row_button.setClickable(false);

                TextInputLayout addSkill = (TextInputLayout) toAdd.findViewById(R.id.edit_skill_layout);
                addSkill.setVisibility(View.GONE);
                TextView toAddText = (TextView) toAdd.findViewById(R.id.skill_text);
                toAddText.setText(head_user.getExpertises().get(i));
                toAddText.setVisibility(View.VISIBLE);

                skills_table.addView(toAdd);
            }
        }

        //Register the edit text fields for changing account info
        edit_org.setText(head_user.getOrganization());
    }

    public void updateEditViews(View v) {
        if (v.getId() == R.id.edit_work_button) {
            org.setVisibility(View.GONE);
            edit_org_layout.setVisibility(View.VISIBLE);
            editWork.setVisibility(View.GONE);
            editSkill.setVisibility(View.GONE);
            saveButton.setVisibility(View.VISIBLE);
            discardButton.setVisibility(View.VISIBLE);
            isEditing = true;
        } else if (v.getId() == R.id.edit_skill_button) {
            if (temp_user.getExpertises().size() == 0) {
                skills_table.removeViewAt(0);//Remove dummy row
            }
            for (ImageButton button : row_buttons) {
                button.setClickable(true);
                button.setImageResource(R.drawable.ic_close_black_24dp);
            }
            onRowAddRequest();
            editWork.setVisibility(View.GONE);
            editSkill.setVisibility(View.GONE);
            saveButton.setVisibility(View.VISIBLE);
            discardButton.setVisibility(View.VISIBLE);
            isEditing = false;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == SKILL_REQUEST) {
            if (resultCode == SelectSkillActivity.SKILL_SELECT) {
                Log.d(getClass().toString(),data.getStringExtra(EXTRA_SKILL));
                //Update the textview of the row
                TableRow target = (TableRow) skills_table.getChildAt(skills_table.getChildCount() - 2);
                ((TextView) target.findViewById(R.id.skill_text)).setText(data.getStringExtra(EXTRA_SKILL));
            } else {
                //Delete the 2nd to last row of the skills table
                skills_table.removeView(skills_table.getChildAt(skills_table.getChildCount() - 2));
            }
        }
    }
}
