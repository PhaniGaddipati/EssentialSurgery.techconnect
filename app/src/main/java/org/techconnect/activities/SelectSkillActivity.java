package org.techconnect.activities;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import org.techconnect.R;
import org.techconnect.adapters.SkillListAdapter;
import org.techconnect.sql.TCDatabaseHelper;

import java.util.ArrayList;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;

public class SelectSkillActivity extends AppCompatActivity implements TextWatcher {

    @Bind(R.id.search_editText)
    EditText searchEditText;
    @Bind(R.id.clear_search_imageView)
    ImageView clearSearchImageView;
    @Bind(R.id.content_linearLayout)
    LinearLayout contentLinearLayout;
    @Bind(R.id.skillListView)
    ListView skillListView;
    private ArrayList<String> devices;
    private SkillListAdapter mAdapter;

    //Results
    public static final int SKILL_SELECT = 0;
    public static final int SKILL_CANCEL = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_skill);
        ButterKnife.bind(this);
        searchEditText.addTextChangedListener(this);

        Map<String,String> map = TCDatabaseHelper.get(this).getChartNamesAndIDs();
        devices = new ArrayList<String>();
        ArrayList<String> items = new ArrayList<String>();
        for (String key: map.keySet()) {
            devices.add(key);
            items.add(key);
        }
        mAdapter = new SkillListAdapter(this,items);
        skillListView.setAdapter(mAdapter);
        setTitle("Select Skill");

        //Set the click listener
        final Context context = this;
        clearSearchImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                searchEditText.setText(null);
            }
        });
        skillListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                //If not the last item, return raw text
                if (i < skillListView.getChildCount() - 1) {
                    Intent result = new Intent();
                    result.putExtra(ProfileActivity.EXTRA_SKILL,mAdapter.getItem(i));
                    setResult(SKILL_SELECT,result);
                    finish();
                } else {
                    //Open up a dialog window to enter text
                    final EditText editText = new EditText(context);
                    final AlertDialog dialog = new AlertDialog.Builder(context).setTitle("Enter Skill").setView(editText)
                            .setPositiveButton(getString(android.R.string.yes), null)
                            .setNegativeButton(getString(android.R.string.cancel), new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    dialogInterface.dismiss();
                                }
                            }).create();

                    dialog.setOnShowListener(new DialogInterface.OnShowListener() {
                        @Override
                        public void onShow(DialogInterface dialogInterface) {
                            Button button = ((AlertDialog) dialog).getButton(AlertDialog.BUTTON_POSITIVE);
                            button.setOnClickListener(new View.OnClickListener() {

                                @Override
                                public void onClick(View view) {
                                    if (TextUtils.isEmpty(editText.getText().toString())) {
                                        Log.d(getClass().toString(),"HERE");
                                        editText.setError("Must enter text");
                                    } else {
                                        Intent result = new Intent();
                                        result.putExtra(ProfileActivity.EXTRA_SKILL, editText.getText().toString());
                                        setResult(SKILL_SELECT, result);
                                        finish();
                                        dialog.dismiss();
                                    }
                                }
                            });
                        }
                    });
                    editText.setSingleLine(true);
                    editText.setImeOptions(EditorInfo.IME_ACTION_DONE);
                    editText.setOnEditorActionListener( new TextView.OnEditorActionListener() {
                        @Override
                        public boolean onEditorAction(TextView view, int actionId, KeyEvent event) {
                            if (event==null) {
                                if (actionId==EditorInfo.IME_ACTION_DONE) {
                                    // Capture soft enters in a singleLine EditText that is the last EditText.
                                    Log.d("Login", "First");
                                    if (TextUtils.isEmpty(editText.getText().toString())) {
                                        Log.d(getClass().toString(),"HERE");
                                        editText.setError("Must enter text");
                                    } else {
                                        Intent result = new Intent();
                                        result.putExtra(ProfileActivity.EXTRA_SKILL, editText.getText().toString());
                                        setResult(SKILL_SELECT, result);
                                        finish();
                                        dialog.dismiss();
                                    }
                                } else return false;  // Let system handle all other null KeyEvents
                            }
                            return true;   // Consume the event
                        }
                    });
                    editText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                        @Override
                        public void onFocusChange(View v, boolean hasFocus) {
                            if (hasFocus) {
                                try {
                                    dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
                                } catch (NullPointerException e) {
                                    Log.e(getClass().toString(),e.getMessage());
                                }
                            }
                        }
                    });

                    dialog.show();
                }
            }
        });
    }

    @Override
    public void onBackPressed() {
        Intent result = new Intent();
        setResult(SKILL_CANCEL, result);
        super.onBackPressed();
    }

    @Override
    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

    }

    @Override
    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        mAdapter.filter(charSequence.toString());
    }

    @Override
    public void afterTextChanged(Editable editable) {

    }
}
