package com.example.opencvtest.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;

import com.example.opencvtest.R;
import com.example.opencvtest.adapter.DetailItemAdapter;
import com.example.opencvtest.data.DetailItem;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class DetailActivity extends AppCompatActivity {

    private List<DetailItem> detail = new ArrayList<>();
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        RecyclerView rvDetail = findViewById(R.id.rv_detail);
        ImageView ivBack = findViewById(R.id.iv_back);



        final byte[] originalArray = getIntent().getByteArrayExtra("ORIGINAL_DATA");
        Bitmap originalBmp = BitmapFactory.decodeByteArray(originalArray, 0, originalArray.length);

        byte[] grayArray = getIntent().getByteArrayExtra("GRAY_DATA");
        Bitmap grayBmp = BitmapFactory.decodeByteArray(grayArray, 0, grayArray.length);

        byte[] histArray = getIntent().getByteArrayExtra("HIST_DATA");
        Bitmap histBmp = BitmapFactory.decodeByteArray(histArray, 0, histArray.length);

        byte[] thresholdArray = getIntent().getByteArrayExtra("THRESHOLD_DATA");
        Bitmap thresholdBmp = BitmapFactory.decodeByteArray(thresholdArray, 0, thresholdArray.length);

        byte[] morphologyArray = getIntent().getByteArrayExtra("MORPHOLOGY_DATA");
        Bitmap closingBmp = BitmapFactory.decodeByteArray(morphologyArray, 0, morphologyArray.length);

        byte[] resultArray = getIntent().getByteArrayExtra("RESULT_DATA");
        Bitmap resultBmp = BitmapFactory.decodeByteArray(resultArray, 0, resultArray.length);


        detail.add(new DetailItem("Original Image", originalBmp));
        detail.add(new DetailItem("Grayscale", grayBmp));
        detail.add(new DetailItem("Threshold Otsu & Binary Inv", thresholdBmp));
        detail.add(new DetailItem("Morphology Closing", closingBmp));
        detail.add(new DetailItem("Segmentation", resultBmp));
        detail.add(new DetailItem("Histogram of Segmented Image", histBmp));


        DetailItemAdapter detailItemAdapter = new DetailItemAdapter(this, detail);
        rvDetail.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        rvDetail.setAdapter(detailItemAdapter);

        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                progressDialog = new ProgressDialog(DetailActivity.this);
                progressDialog.show();
                progressDialog.setContentView(R.layout.progress_dialog);
                Objects.requireNonNull(progressDialog.getWindow()).setBackgroundDrawableResource(android.R.color.transparent);

                Intent move = new Intent(DetailActivity.this, ResultActivity.class);
                move.putExtra("SRC_PATH", originalArray);
                startActivity(move);
            }
        });


    }

}
