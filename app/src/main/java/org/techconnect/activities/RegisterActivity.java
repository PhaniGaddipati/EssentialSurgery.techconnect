package org.techconnect.activities;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.ScrollView;

import org.techconnect.R;
import org.techconnect.analytics.FirebaseEvents;
import org.techconnect.asynctasks.RegisterAsyncTask;
import org.techconnect.model.User;
import org.techconnect.sql.TCDatabaseHelper;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class RegisterActivity extends AppCompatActivity {

    public static final String RESULT_REGISTERED_EMAIL = "org.techconnect.register.result.useremail";
    public static final String EXTRA_EMAIL = "org.techconnect.register.emailTextView";
    public static final String EXTRA_PASSWORD = "org.techconnect.register.password";

    @Bind(R.id.activity_register)
    CoordinatorLayout coordinatorLayout;
    @Bind(R.id.register_form)
    ScrollView registerForm;
    @Bind(R.id.register_progress)
    ProgressBar progressBar;
    @Bind(R.id.name)
    EditText nameEditText;
    @Bind(R.id.email)
    EditText emailEditText;
    @Bind(R.id.organization)
    EditText orgEditText;
    @Bind(R.id.expertises)
    EditText expertisesEditText;
    @Bind(R.id.password)
    EditText passwordEditText;
    @Bind(R.id.confirm_password)
    EditText confirmPasswordEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        ButterKnife.bind(this);

        if (getIntent() != null && getIntent().hasExtra(EXTRA_EMAIL)) {
            emailEditText.setText(getIntent().getStringExtra(EXTRA_EMAIL));
        }
        if (getIntent() != null && getIntent().hasExtra(EXTRA_PASSWORD)) {
            passwordEditText.setText(getIntent().getStringExtra(EXTRA_PASSWORD));
        }
        if (savedInstanceState != null) {
            nameEditText.setText(savedInstanceState.getString("name"));
            emailEditText.setText(savedInstanceState.getString("emailTextView"));
            orgEditText.setText(savedInstanceState.getString("org"));
            expertisesEditText.setText(savedInstanceState.getString("skills"));
            passwordEditText.setText(savedInstanceState.getString("password"));
            confirmPasswordEditText.setText(savedInstanceState.getString("cpassword"));
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("name", nameEditText.getText().toString());
        outState.putString("emailTextView", emailEditText.getText().toString());
        outState.putString("org", orgEditText.getText().toString());
        outState.putString("skills", expertisesEditText.getText().toString());
        outState.putString("password", passwordEditText.getText().toString());
        outState.putString("cpassword", confirmPasswordEditText.getText().toString());
    }

    @OnClick(R.id.register_button)
    public void onRegister() {
        if (validate()) {
            showProgress(true);
            final String locale = getResources().getConfiguration().locale.getCountry();
            final String name = nameEditText.getText().toString().trim();
            final String email = emailEditText.getText().toString().trim();
            final String org = orgEditText.getText().toString().trim();
            final String password = passwordEditText.getText().toString();

            String expertises = expertisesEditText.getText().toString();
            final String[] skillsArr;
            if (TextUtils.isEmpty(expertises.trim())) {
                skillsArr = new String[0];
            } else {
                skillsArr = expertises.split(",");
                for (int i = 0; i < skillsArr.length; i++) {
                    skillsArr[i] = skillsArr[i].trim();
                }
            }

            new RegisterAsyncTask(locale, name, email, org, password, skillsArr) {

                @Override
                protected void onPostExecute(User user) {
                    showProgress(false);
                    if (user == null) {
                        Snackbar.make(coordinatorLayout, R.string.failed_register, Snackbar.LENGTH_LONG).show();
                        FirebaseEvents.logRegistrationFailed(RegisterActivity.this);
                    } else {
                        // Store the user, probably need it later
                        FirebaseEvents.logRegistrationSuccess(RegisterActivity.this);
                        TCDatabaseHelper.get(RegisterActivity.this).upsertUser(user);
                        Intent intent = new Intent();
                        intent.putExtra(RESULT_REGISTERED_EMAIL, user.getEmail());
                        setResult(Activity.RESULT_OK, intent);
                        finish();
                    }
                }

                @Override
                protected void onCancelled() {
                    showProgress(false);
                }
            }.execute();
        }
    }

    private boolean validate() {
        nameEditText.setError(null);
        emailEditText.setError(null);
        orgEditText.setError(null);
        expertisesEditText.setError(null);
        passwordEditText.setError(null);
        confirmPasswordEditText.setError(null);

        String name = nameEditText.getText().toString();
        String email = emailEditText.getText().toString();
        String org = orgEditText.getText().toString();
        String password = passwordEditText.getText().toString();
        String cPassword = confirmPasswordEditText.getText().toString();

        boolean valid = true;

        if (TextUtils.isEmpty(name.trim())) {
            nameEditText.setError(getString(R.string.field_required));
            valid = false;
        }
        if (TextUtils.isEmpty(email.trim())) {
            emailEditText.setError(getString(R.string.field_required));
            valid = false;
        }
        if (TextUtils.isEmpty(org.trim())) {
            orgEditText.setError(getString(R.string.field_required));
            valid = false;
        }

        if (TextUtils.isEmpty(password.trim())) {
            passwordEditText.setError(getString(R.string.field_required));
            valid = false;
        } else if (password.trim().length() < 5) {
            passwordEditText.setError(getString(R.string.password_length));
            valid = false;
        }
        if (!cPassword.equals(password)) {
            confirmPasswordEditText.setError(getString(R.string.passwords_must_match));
            valid = false;
        }

        return valid;
    }

    /**
     * Shows the progress_spinner UI and hides the login form.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress_spinner spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            registerForm.setVisibility(show ? View.GONE : View.VISIBLE);
            registerForm.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    registerForm.setVisibility(show ? View.GONE : View.VISIBLE);
                }
            });

            progressBar.setVisibility(show ? View.VISIBLE : View.GONE);
            progressBar.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    progressBar.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            progressBar.setVisibility(show ? View.VISIBLE : View.GONE);
            registerForm.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }
}
