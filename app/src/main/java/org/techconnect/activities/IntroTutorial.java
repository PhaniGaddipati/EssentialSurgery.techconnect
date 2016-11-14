package org.techconnect.activities;

import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.Window;
import android.view.WindowManager;

import com.github.paolorotolo.appintro.AppIntro;
import com.github.paolorotolo.appintro.AppIntroFragment;
import com.google.firebase.analytics.FirebaseAnalytics;

import org.centum.techconnect.R;

public class IntroTutorial extends AppIntro {

    private FirebaseAnalytics firebaseAnalytics;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        firebaseAnalytics = FirebaseAnalytics.getInstance(this);
        firebaseAnalytics.logEvent(FirebaseAnalytics.Event.TUTORIAL_BEGIN, null);
        setDepthAnimation();
        showSkipButton(true);
        setProgressIndicator();
        setBarColor(getResources().getColor(R.color.colorPrimary));
        Window window = this.getWindow();
        // clear FLAG_TRANSLUCENT_STATUS flag:
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

        // add FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS flag to the window
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);

        // finally change the color
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.setStatusBarColor(getResources().getColor(R.color.colorPrimaryDark));
        }


        int white = getResources().getColor(android.R.color.white);
        int black = getResources().getColor(android.R.color.black);
        int darkGreen = getResources().getColor(R.color.colorPrimaryDark);
        addSlide(AppIntroFragment.newInstance(getString(R.string.tutorial_title_welcome),
                getString(R.string.tutorial_msg_welcome),
                R.drawable.tech_connect_app_icon, white, black, black));
        addSlide(AppIntroFragment.newInstance(getString(R.string.tutorial_title_guides_home),
                getString(R.string.tutorial_msg_guides_home),
                R.drawable.tutorial_home_guides, darkGreen));
        addSlide(AppIntroFragment.newInstance(getString(R.string.tutorial_title_device_home),
                getString(R.string.tutorial_msg_device_home),
                R.drawable.tutorial_guide_esu, darkGreen));
        addSlide(AppIntroFragment.newInstance(getString(R.string.tutorial_title_step),
                getString(R.string.tutorial_msg_step),
                R.drawable.tutorial_step, darkGreen));
        addSlide(AppIntroFragment.newInstance(getString(R.string.tutorial_title_step_details),
                getString(R.string.tutorial_msg_step_details),
                R.drawable.tutorial_step_detail, darkGreen));
        addSlide(AppIntroFragment.newInstance(getString(R.string.tutorial_title_hamburger),
                getString(R.string.tutorial_msg_hamburger),
                R.drawable.tutorial_hamburger, darkGreen));
        addSlide(AppIntroFragment.newInstance(getString(R.string.tutorial_title_profile),
                getString(R.string.tutorial_msg_profile),
                R.drawable.tutorial_profile, darkGreen));
        addSlide(AppIntroFragment.newInstance(getString(R.string.tutorial_title_directory),
                getString(R.string.tutorial_msg_directory),
                R.drawable.tutorial_directory, darkGreen));
        addSlide(AppIntroFragment.newInstance(getString(R.string.tutorial_title_directory_skill),
                getString(R.string.tutorial_msg_directory_skill),
                R.drawable.tutorial_directory_skill, darkGreen));
    }

    @Override
    public void onSkipPressed(Fragment currentFragment) {
        super.onSkipPressed(currentFragment);
        firebaseAnalytics.logEvent("tutorial_skip", null);
        finish();
    }

    @Override
    public void onDonePressed(Fragment currentFragment) {
        super.onDonePressed(currentFragment);
        firebaseAnalytics.logEvent(FirebaseAnalytics.Event.TUTORIAL_COMPLETE, null);
        finish();
    }

}
