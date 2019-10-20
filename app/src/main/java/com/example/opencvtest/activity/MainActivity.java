package com.example.opencvtest.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.view.View;
import android.widget.Toast;

import com.example.opencvtest.R;
import com.example.opencvtest.data.Slide;
import com.example.opencvtest.adapter.SliderPagerAdapter;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;


public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private List<Slide> imageSlider;
    private ViewPager sliderPager;
    private TabLayout tlIndicator;
    private CardView cvIdentification;
    private CardView cvInformation;
    private CardView cvHelp;
    private CardView cvAbout;
    private boolean doubleBackToExitPressedOnce = false;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
        imageSlider = new ArrayList<>();
        imageSlider.add(new Slide(R.drawable.kurma_ajwa, "Kurma Ajwa"));
        imageSlider.add(new Slide(R.drawable.kurma_sukari, "Kurma Sukari"));
        imageSlider.add(new Slide(R.drawable.kurma_nour, "Kurma Deglet Nour"));

        SliderPagerAdapter adapter = new SliderPagerAdapter(this, imageSlider);
        sliderPager.setAdapter(adapter);
        tlIndicator.setupWithViewPager(sliderPager, true);


        Timer timer = new Timer();
        timer.scheduleAtFixedRate(new SliderTimer(), 4000, 6000);


        cvAbout.setOnClickListener(this);
        cvHelp.setOnClickListener(this);
        cvInformation.setOnClickListener(this);
        cvIdentification.setOnClickListener(this);
    }

    private void initView() {
        sliderPager = findViewById(R.id.slider_pager);
        tlIndicator = findViewById(R.id.tl_indicator);
        cvIdentification = findViewById(R.id.cv_identification);
        cvInformation = findViewById(R.id.cv_information);
        cvHelp = findViewById(R.id.cv_help);
        cvAbout = findViewById(R.id.cv_about);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.cv_about:
                Intent move = new Intent(MainActivity.this, AboutActivity.class);
                startActivity(move);
                break;
            case R.id.cv_help:
                Intent move1 = new Intent(MainActivity.this, HelpActivity.class);
                startActivity(move1);
                break;

            case R.id.cv_information:
                Intent move2 = new Intent(MainActivity.this, InformationActivity.class);
                startActivity(move2);
                break;

            case R.id.cv_identification:
                Intent move3 = new Intent(MainActivity.this, IdentificationActivity.class);
                startActivity(move3);
                break;
        }
    }

    class SliderTimer extends TimerTask {

        @Override
        public void run() {
            MainActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (sliderPager.getCurrentItem() < imageSlider.size() - 1) {
                        sliderPager.setCurrentItem(sliderPager.getCurrentItem() + 1);
                    } else
                        sliderPager.setCurrentItem(0);

                }
            });

        }
    }

    @Override
    public void onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            super.onBackPressed();
            return;
        }

        this.doubleBackToExitPressedOnce = true;
        Toast.makeText(this, "Please Click Back Again to Exit", Toast.LENGTH_SHORT).show();

        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                doubleBackToExitPressedOnce=false;
            }
        }, 2000);
    }


}
