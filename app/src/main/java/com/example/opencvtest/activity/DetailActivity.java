package com.example.opencvtest.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;

import com.example.opencvtest.R;
import com.example.opencvtest.adapter.DetailItemAdapter;
import com.example.opencvtest.data.DetailItem;

import java.util.ArrayList;
import java.util.List;

public class DetailActivity extends AppCompatActivity {

    private List<DetailItem> detail =new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        RecyclerView rvDetail = findViewById(R.id.rv_detail);
        ImageView ivBack = findViewById(R.id.iv_back);

        byte[] byteArray = getIntent().getByteArrayExtra("BITMAP DATA");
        Bitmap resultBmp = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);


        detail.add(new DetailItem("Original Image", resultBmp));
        detail.add(new DetailItem("Grayscale", resultBmp));
        detail.add(new DetailItem("Filter Median", resultBmp));
        detail.add(new DetailItem("Watershed Segmentation", resultBmp));
        detail.add(new DetailItem("GLCM", resultBmp));

        DetailItemAdapter detailItemAdapter = new DetailItemAdapter(this, detail);
        rvDetail.setLayoutManager(new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false));
        rvDetail.setAdapter(detailItemAdapter);

        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent move = new Intent(DetailActivity.this, ResultActivity.class);
                startActivity(move);
            }
        });





    }
}
