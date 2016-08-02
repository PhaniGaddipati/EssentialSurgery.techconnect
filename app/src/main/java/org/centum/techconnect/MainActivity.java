package org.centum.techconnect;

import android.app.Dialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

<<<<<<< HEAD
import org.centum.techconnect.activities.IntroTutorial;
=======
import org.centum.techconnect.activities.CallActivity;
>>>>>>> upstream/master
import org.centum.techconnect.fragments.ReportsFragment;
import org.centum.techconnect.fragments.SelfHelpFragment;
import org.centum.techconnect.resources.ResourceHandler;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Entry activity.
 */
public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private static final int FRAGMENT_SELF_HELP = 0;
    private static final int FRAGMENT_LOGS = 1;
    private static final int TUTORIAL_ACTIVITY = 2;
    @Bind(R.id.nav_view)
    NavigationView navigationView;

    private Fragment[] fragments = new Fragment[]{new SelfHelpFragment(), new ReportsFragment()};
    private String[] fragmentTitles;
    private int fragment = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ResourceHandler.get(this);
        ButterKnife.bind(this);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        fragmentTitles = getResources().getStringArray(R.array.fragment_titles);
        navigationView.setNavigationItemSelectedListener(this);
        if (savedInstanceState == null) {
            loadResources(FRAGMENT_SELF_HELP);
        } else {
            loadResources(savedInstanceState.getInt("frag", FRAGMENT_SELF_HELP));
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt("frag", fragment);
    }

    /**
     * Loads the resources in the background, while the splash is showing.
     * All missing resources are downloaded only, so subsequent runs should
     * just use the cached stuff.
     * <p/>
     * Splash shown for a min of 2000ms
     *
     * @param fragToOpen
     */
    private void loadResources(final int fragToOpen) {
        new AsyncTask<Void, Void, Void>() {

            Dialog dialog;

            @Override
            protected void onPreExecute() {
                dialog = new Dialog(MainActivity.this, android.R.style.Theme_Material_Light_NoActionBar_Fullscreen);
                dialog.setContentView(R.layout.loading_layout);
                dialog.setCancelable(false);
                dialog.show();
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                Intent intent = new Intent(MainActivity.this, IntroTutorial.class);
                new CountDownTimer(2000,100) {
                    public void onFinish() {
                        dialog.dismiss();
                    }

                    public void onTick(long millisUntilFinish) {

                    }
                }.start();
                startActivity(intent);
                setFragment(fragToOpen);
            }

            @Override
            protected Void doInBackground(Void... voids) {
                long time = System.currentTimeMillis();
                ResourceHandler.get().loadResources();
                if ((System.currentTimeMillis() - time) < 2000) {
                    try {
                        Thread.sleep(2000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                return null;
            }

        }.execute();
    }





    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else if (fragment == FRAGMENT_SELF_HELP) {
            if (!((SelfHelpFragment) fragments[FRAGMENT_SELF_HELP]).onBack()) {
                // Fragment didn't consume back event
                super.onBackPressed();
            }
        } else {
            super.onBackPressed();
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();
        int newFrag = -1;
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (id == R.id.nav_self_help) {
            newFrag = FRAGMENT_SELF_HELP;
        } else if (id == R.id.nav_reports) {
            newFrag = FRAGMENT_LOGS;
        } else if (id == R.id.call_dir) {
            startActivity(new Intent(this, CallActivity.class));
            drawer.closeDrawer(GravityCompat.START);
            return true;
        } else if (id == R.id.nav_refresh) {
            ResourceHandler.get().clear();
            new AlertDialog.Builder(this).setTitle("Sync")
                    .setMessage("Resources will be synced on next app start")
                    .setPositiveButton(android.R.string.ok, null)
                    .show();
            return true;
        }

        drawer.closeDrawer(GravityCompat.START);
        setFragment(newFrag);
        return true;
    }

    private void setFragment(int frag) {
        if (this.fragment != frag || this.fragment == -1) {
            this.fragment = frag;
            FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction()
                    .replace(R.id.main_fragment_container, fragments[frag])
                    .commit();
            setTitle(fragmentTitles[frag]);
            navigationView.getMenu().getItem(frag).setChecked(true);
        }
    }
}
