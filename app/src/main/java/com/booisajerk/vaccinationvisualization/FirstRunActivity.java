package com.booisajerk.vaccinationvisualization;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;

import com.github.paolorotolo.appintro.AppIntro;
import com.github.paolorotolo.appintro.AppIntroFragment;

public class FirstRunActivity extends AppIntro {
    @SuppressLint("ResourceType")
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addSlide(AppIntroFragment.newInstance(getResources().getString(R.string.first_run_title_one), getResources().getString(R.string.first_run_description_one), R.drawable.first_run_one, getResources().getColor(R.color.backgroundFirstRun)));
        addSlide(AppIntroFragment.newInstance(getResources().getString(R.string.first_run_title_two), getResources().getString(R.string.first_run_description_two), R.drawable.first_run_two, getResources().getColor(R.color.backgroundFirstRun)));
        addSlide(AppIntroFragment.newInstance(getResources().getString(R.string.first_run_title_three), getResources().getString(R.string.first_run_description_three), R.drawable.first_run_three, getResources().getColor(R.color.backgroundFirstRun)));

        setBarColor(Color.parseColor("#364C74"));
        setSeparatorColor(Color.parseColor("#364C74"));
        showStatusBar(false);
        showSkipButton(true);
        setProgressButtonEnabled(true);
    }

    @Override
    public void onSkipPressed(Fragment currentFragment) {
        super.onSkipPressed(currentFragment);
        Intent intent = new Intent(FirstRunActivity.this, FilterActivity.class);
        startActivity(intent);
    }

    @Override
    public void onDonePressed(Fragment currentFragment) {
        super.onDonePressed(currentFragment);

        Intent intent = new Intent(FirstRunActivity.this, FilterActivity.class);
        startActivity(intent);
    }
}
