package com.example.opencvtest.activity;

import android.content.Intent;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;

import com.example.opencvtest.R;
import com.example.opencvtest.adapter.ViewPagerAdapter;
import com.example.opencvtest.fragment.AjwaFragment;
import com.example.opencvtest.fragment.DegletNourFragment;
import com.example.opencvtest.fragment.SukariFragment;

public class InformationActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_information);

        Toolbar tbInfo = findViewById(R.id.tb_info);
        setSupportActionBar(tbInfo);
        ActionBar actionBar = getSupportActionBar();
        assert actionBar != null;
        actionBar.setDisplayShowTitleEnabled(false);

        ImageView ivBack = findViewById(R.id.iv_back);
        TabLayout tlInfo = findViewById(R.id.tl_info);
        ViewPager vpInfo = findViewById(R.id.vp_info);

        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.AddFragment(new AjwaFragment(), getString(R.string.ajwa));
        adapter.AddFragment(new SukariFragment(), getString(R.string.sukari));
        adapter.AddFragment(new DegletNourFragment(), getString(R.string.deglet_nour));
        vpInfo.setAdapter(adapter);
        tlInfo.setupWithViewPager(vpInfo);

        TabLayout.Tab tab = tlInfo.getTabAt(getIntent().getIntExtra("TAB ID",0));
        assert tab != null;
        tab.select();

        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent move = new Intent(InformationActivity.this, MainActivity.class);
                startActivity(move);

            }
        });




    }
}
