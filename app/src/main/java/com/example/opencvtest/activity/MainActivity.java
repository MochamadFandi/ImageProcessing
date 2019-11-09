package com.example.opencvtest.activity;

import android.content.Intent;
import android.os.Bundle;
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
    private static final int TIME_DELAY = 2000;
    private static long back_pressed;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
        Slider();

        cvAbout.setOnClickListener(this);
        cvHelp.setOnClickListener(this);
        cvInformation.setOnClickListener(this);
        cvIdentification.setOnClickListener(this);
    }

    private void Slider() {
        imageSlider = new ArrayList<>();
        imageSlider.add(new Slide(R.drawable.kurma_ajwa2, "Kurma Ajwa",0));
        imageSlider.add(new Slide(R.drawable.kurma_sukari3, "Kurma Sukari",1));
        imageSlider.add(new Slide(R.drawable.kurma_nour, "Kurma Deglet Nour",2));

        SliderPagerAdapter adapter = new SliderPagerAdapter(this, imageSlider);
        sliderPager.setAdapter(adapter);
        tlIndicator.setupWithViewPager(sliderPager, true);


        Timer timer = new Timer();
        timer.scheduleAtFixedRate(new SliderTimer(), 4000, 6000);
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
                Intent moveAbout = new Intent(MainActivity.this, AboutActivity.class);
                startActivity(moveAbout);
                break;
            case R.id.cv_help:
                Intent moveHelp = new Intent(MainActivity.this, HelpActivity.class);
                startActivity(moveHelp);
                break;

            case R.id.cv_information:
                Intent moveInfo = new Intent(MainActivity.this, InformationActivity.class);
                startActivity(moveInfo);
                break;

            case R.id.cv_identification:
                Intent moveIdent = new Intent(MainActivity.this, IdentificationActivity.class);
                startActivity(moveIdent);
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
        if (back_pressed + TIME_DELAY > System.currentTimeMillis()) {
            Intent intent = new Intent(Intent.ACTION_MAIN);
            intent.addCategory(Intent.CATEGORY_HOME);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            finish();

        } else {
            Toast.makeText(this, "Press once again to exit", Toast.LENGTH_SHORT).show();
        } back_pressed = System.currentTimeMillis();


    }


}
