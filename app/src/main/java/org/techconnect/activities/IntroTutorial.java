package org.techconnect.activities;

import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.Window;
import android.view.WindowManager;

import com.github.paolorotolo.appintro.AppIntro;
import com.github.paolorotolo.appintro.AppIntroFragment;

import org.techconnect.R;
import org.techconnect.analytics.FirebaseEvents;

public class IntroTutorial extends AppIntro {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FirebaseEvents.logTutorialBegin(this);
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
                R.drawable.home_guides, darkGreen));
        addSlide(AppIntroFragment.newInstance(getString(R.string.repair_history),
                getString(R.string.tutorial_msg_history_home),
                R.drawable.history_date_list, darkGreen)); //Need to update
        addSlide(AppIntroFragment.newInstance(getString(R.string.contact_an_expert),
                getString(R.string.tutorial_msg_contact_home),
                R.drawable.contact_email, darkGreen));
        addSlide(AppIntroFragment.newInstance(getString(R.string.tutorial_title_final),
                getString(R.string.tutorial_msg_final),
                R.drawable.tech_connect_app_icon, white,black, black));
    }

    @Override
    public void onSkipPressed(Fragment currentFragment) {
        super.onSkipPressed(currentFragment);
        FirebaseEvents.logTutorialSkip(this);
        finish();
    }

    @Override
    public void onDonePressed(Fragment currentFragment) {
        super.onDonePressed(currentFragment);
        FirebaseEvents.logTutorialFinish(this);
        finish();
    }

}
