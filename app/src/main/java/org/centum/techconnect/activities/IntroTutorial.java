package org.centum.techconnect.activities;

import android.os.Bundle;
import android.support.v4.app.Fragment;

import com.github.paolorotolo.appintro.AppIntro;
import com.github.paolorotolo.appintro.AppIntroFragment;

import org.centum.techconnect.R;

public class IntroTutorial extends AppIntro {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_intro_tutorial);
        //Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        //setSupportActionBar(toolbar);
        setDepthAnimation();
        showSkipButton(true);
        setProgressIndicator();
        setBarColor(getResources().getColor(R.color.colorPrimary));


        int white = getResources().getColor(android.R.color.white);
        int black = getResources().getColor(android.R.color.black);
        int primeGreen = getResources().getColor(R.color.colorPrimary);
        int darkGreen = getResources().getColor(R.color.colorPrimaryDark);
        addSlide(AppIntroFragment.newInstance("Welcome to Tech Connect", "This is a quick tutorial on how to use this app", R.drawable.tech_connect_app_icon, white, black, black));
        addSlide(AppIntroFragment.newInstance("Self Help Homescreen", "This is the self help start screen", R.drawable.home, darkGreen));
        addSlide(AppIntroFragment.newInstance("Enter Room and Device", "Enter the name of the room the device is in and select the type of device from the drop-down menu", R.drawable.expanded, darkGreen));
        addSlide(AppIntroFragment.newInstance("Add Notes and Start", "Add any additional notes then press \"Start Session\" to start", R.drawable.addnotes, darkGreen));
        addSlide(AppIntroFragment.newInstance("Work Through Troubleshooting Steps", "You will be guided through a series of questions. Answer each question based on how the device is responding", R.drawable.options, darkGreen));
        addSlide(AppIntroFragment.newInstance("Read Skills Tutorials", "If a repair step requires knowledge that you do not have, helpful resources will be provided for your reference", R.drawable.helpoptions, darkGreen));
        addSlide(AppIntroFragment.newInstance("Finish Repair", "At some point, you will completely repair the device. Success!", R.drawable.success, darkGreen));
        addSlide(AppIntroFragment.newInstance("Additional Features", "When you tap the expanded menu button at the upper left, you will be able to access other features such as reports and syncing", R.drawable.swipe, darkGreen));




    }

    @Override
    public void onSkipPressed(Fragment currentFragment) {
        super.onSkipPressed(currentFragment);
        // Do something when users tap on Skip button.
        finish();

    }

    @Override
    public void onDonePressed(Fragment currentFragment) {
        super.onDonePressed(currentFragment);
        // Do something when users tap on Done button.
        finish();
    }

}
