package org.techconnect.activities;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.centum.techconnect.R;
import org.techconnect.analytics.FirebaseEvents;
import org.techconnect.asynctasks.LogoutAsyncTask;
import org.techconnect.asynctasks.PostAppFeedbackAsyncTask;
import org.techconnect.dialogs.SendFeedbackDialogFragment;
import org.techconnect.fragments.CatalogFragment;
import org.techconnect.fragments.DirectoryFragment;
import org.techconnect.fragments.GuidesFragment;
import org.techconnect.fragments.RepairHistoryFragment;
import org.techconnect.misc.ResourceHandler;
import org.techconnect.misc.auth.AuthListener;
import org.techconnect.misc.auth.AuthManager;
import org.techconnect.model.User;
import org.techconnect.model.UserAuth;
import org.techconnect.sql.TCDatabaseHelper;

import java.util.ArrayList;
import java.util.Stack;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Entry activity.
 */
public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    public static final int FRAGMENT_CATALOG = 0;
    public static final int FRAGMENT_GUIDES = 1;
    public static final int FRAGMENT_HISTORY = 2;
    public static final int FRAGMENT_DIRECTORY = 3;
    private static final int PERMISSIONS_REQUEST_READ_STORAGE = 1;
    private static final String SHOWN_TUTORIAL = "org.techconnect.prefs.shownturotial";
    private static final String USER_LEARNED_DRAWER = "org.techconnect.prefs.shownturotial.learneddrawer";
    private static final String ASKED_PERMISSION = "org.techconnect.prefs.shownturotial.askedpermission";
    private final Fragment[] FRAGMENTS = new Fragment[]{new CatalogFragment(),
            new GuidesFragment(), new RepairHistoryFragment(), new DirectoryFragment()};
    private final int[] FRAGMENT_MENU_IDS = new int[]{R.id.nav_catalog, R.id.nav_guides, R.id.nav_repair_history, R.id.call_dir};

    @Bind(R.id.coordinatorLayout)
    CoordinatorLayout coordinatorLayout;
    @Bind(R.id.drawer_layout)
    DrawerLayout drawerLayout;
    @Bind(R.id.nav_view)
    NavigationView navigationView;
    @Bind(R.id.loading_banner)
    RelativeLayout loadingLayout;
    @Bind(R.id.permission_layout)
    LinearLayout permissionLayout;
    @Bind(R.id.main_fragment_container)
    FrameLayout fragmentContainer;
    TextView headerTextView;
    MenuItem logoutMenuItem;
    MenuItem loginMenuItem;
    MenuItem viewProfileMenuItem;

    private int currentFragment = -1;
    private boolean showedLogin = false;
    private boolean userLearnedDrawer = false;
    private Stack<Integer> fragStack = new Stack<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        FirebaseEvents.logAppOpen(this);
        ResourceHandler.get(this);
        TCDatabaseHelper.get(this);
        ButterKnife.bind(this);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        userLearnedDrawer = getSharedPreferences(MainActivity.class.getName(), MODE_PRIVATE).getBoolean(USER_LEARNED_DRAWER, false);
        loadingLayout.setVisibility(View.GONE);
        // Lock until we have permission (in check permissions)
        drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close) {
            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                if (!userLearnedDrawer) {
                    // The user manually opened the drawer; store this flag to prevent auto-showing
                    // the navigation drawer automatically in the future.
                    userLearnedDrawer = true;
                    getSharedPreferences(MainActivity.class.getName(), MODE_PRIVATE).edit()
                            .putBoolean(USER_LEARNED_DRAWER, true).apply();
                }
            }
        };


        drawerLayout.addDrawerListener(toggle);
        //Set MenuItem properties for profile-related options
        logoutMenuItem = navigationView.getMenu().findItem(R.id.logout);
        loginMenuItem = navigationView.getMenu().findItem(R.id.login);
        viewProfileMenuItem = navigationView.getMenu().findItem(R.id.profile);
        headerTextView = (TextView) navigationView.getHeaderView(0).findViewById(R.id.headerTextView);
        toggle.syncState();
        updateNav();
        AuthManager.get(this).addAuthListener(new AuthListener() {

            @Override
            public void onLoginSucces(UserAuth auth) {
                updateNav();
            }

            @Override
            public void onLogout() {
                updateNav();
            }
        });

        navigationView.setNavigationItemSelectedListener(this);

        int numCharts = TCDatabaseHelper.get(this).getNumFlowcharts();
        int fragToOpen = numCharts > 0 ? FRAGMENT_GUIDES : FRAGMENT_CATALOG;
        if (savedInstanceState != null) {
            fragToOpen = savedInstanceState.getInt("frag", fragToOpen);
            showedLogin = savedInstanceState.getBoolean("shown_login");
            ArrayList<Integer> stack = savedInstanceState.getIntegerArrayList("frag_stack");
            fragStack = new Stack<>();
            for (Integer i : stack) {
                fragStack.push(i);
            }
        }
        currentFragment = -1;
        setCurrentFragment(fragToOpen);
    }


    private void updateNav() {
        boolean loggedIn = AuthManager.get(this).hasAuth();
        User user;
        if (loggedIn && (user = TCDatabaseHelper.get(this).getUser(AuthManager.get(this).getAuth().getUserId())) != null) {
            headerTextView.setText(user.getName());
            loginMenuItem.setVisible(false);
            logoutMenuItem.setVisible(true);
            viewProfileMenuItem.setVisible(true);
        } else {
            headerTextView.setText(R.string.app_name);
            loginMenuItem.setVisible(true);
            logoutMenuItem.setVisible(false);
            viewProfileMenuItem.setVisible(false);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        boolean hasPermissions = checkPermissions();
        boolean showedIntro = getSharedPreferences(MainActivity.class.getName(), MODE_PRIVATE)
                .getBoolean(SHOWN_TUTORIAL, false);
        if (!showedIntro) {
            // Show tutorial
            getSharedPreferences(MainActivity.class.getName(), MODE_PRIVATE)
                    .edit()
                    .putBoolean(SHOWN_TUTORIAL, true)
                    .apply();
            //startActivity(new Intent(MainActivity.this, IntroTutorial.class));
        } else if (!showedLogin && !AuthManager.get(this).hasAuth() && hasPermissions) {
            onShowLogin();
        } else if (hasPermissions && !userLearnedDrawer) {
            drawerLayout.openDrawer(Gravity.LEFT);
        }
        updateNav();
    }

    @OnClick(R.id.grant_permission_btn)
    public void askForPermission() {
        if (!checkPermissions()) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                        PERMISSIONS_REQUEST_READ_STORAGE);
            }
        }
    }

    private boolean checkPermissions() {
        boolean havePermission = PackageManager.PERMISSION_GRANTED ==
                ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE);
        if (havePermission) {
            drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNDEFINED);
            permissionLayout.setVisibility(View.GONE);
            fragmentContainer.setVisibility(View.VISIBLE);
        } else {
            if (!getSharedPreferences(getClass().getName(), MODE_PRIVATE).getBoolean(ASKED_PERMISSION, false)) {
                getSharedPreferences(getClass().getName(), MODE_PRIVATE)
                        .edit()
                        .putBoolean(ASKED_PERMISSION, true)
                        .apply();
                askForPermission();
            }
        }
        return havePermission;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (currentFragment > -1) {
            outState.putInt("frag", currentFragment);
        }
        outState.putIntegerArrayList("frag_stack", new ArrayList<>(fragStack));
        outState.putBoolean("shown_login", showedLogin);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            if (fragStack.size() > 0) {
                setCurrentFragment(fragStack.pop());
                fragStack.pop(); // don't wanna store this change
            } else {
                super.onBackPressed();
            }
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();
        int newFragIndex = -1;
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        for (int i = 0; i < FRAGMENT_MENU_IDS.length; i++) {
            if (id == FRAGMENT_MENU_IDS[i]) {
                newFragIndex = i;
            }
        }
        if (newFragIndex == -1) {
            if (id == R.id.nav_view_tut) {
                startActivity(new Intent(this, IntroTutorial.class));
                drawer.closeDrawer(GravityCompat.START);
                return true;
            } else if (id == R.id.login) {
                onShowLogin();
                return true;
            } else if (id == R.id.logout) {
                onLogout();
                return true;
            } else if (id == R.id.post_feedback) {
                drawer.closeDrawer(GravityCompat.START);
                onSendFeedback();
                return true;
            } else if (id == R.id.profile) {
                onViewProfile();
                return true;
            }
        }
        drawer.closeDrawer(GravityCompat.START);
        setCurrentFragment(newFragIndex);
        return true;
    }

    private void onSendFeedback() {
        final SendFeedbackDialogFragment dialogFragment = new SendFeedbackDialogFragment();
        dialogFragment.setListener(new SendFeedbackDialogFragment.FeedbackListener() {
            @Override
            public void onYes(String text) {
                new PostAppFeedbackAsyncTask(MainActivity.this) {
                    @Override
                    protected void onPostExecute(Boolean success) {
                        if (success) {
                            Snackbar.make(coordinatorLayout, R.string.feedback_success, Snackbar.LENGTH_LONG).show();
                        } else {
                            Snackbar.make(coordinatorLayout, R.string.feedback_fail, Snackbar.LENGTH_LONG).show();
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

    private void onShowLogin() {
        showedLogin = true;
        startActivity(new Intent(MainActivity.this, LoginActivity.class));
    }

    private void onLogout() {
        if (AuthManager.get(this).hasAuth()) {
            new LogoutAsyncTask() {
                ProgressDialog pd;

                @Override
                protected void onPreExecute() {
                    super.onPreExecute();
                    pd = ProgressDialog.show(MainActivity.this, getString(R.string.logging_out), null, true, false);
                }

                @Override
                protected void onPostExecute(Void aVoid) {
                    AuthManager.get(null).setAuth(null);
                    pd.dismiss();
                    pd = null;
                    onShowLogin();
                }
            }.execute(AuthManager.get(this).getAuth());
        }
    }

    private void onViewProfile() {
        //Only visible when user is actually logged in
        User user = TCDatabaseHelper.get(this).getUser(AuthManager.get(this).getAuth().getUserId());
        Intent intent = new Intent(MainActivity.this, ProfileActivity.class);
        intent.putExtra("user", user);
        startActivity(intent);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }

    public void setCurrentFragment(int frag) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        if (this.currentFragment != frag || this.currentFragment == -1) {
            if (this.currentFragment != -1) {
                // Not the first fragment
                fragStack.push(currentFragment);
            }
            fragmentManager.beginTransaction()
                    .replace(R.id.main_fragment_container, FRAGMENTS[frag])
                    .commit();
            this.currentFragment = frag;
            navigationView.getMenu().findItem(FRAGMENT_MENU_IDS[frag]).setChecked(true);
        }
    }
}


