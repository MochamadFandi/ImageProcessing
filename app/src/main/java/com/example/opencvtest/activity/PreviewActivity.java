package com.example.opencvtest.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.example.opencvtest.R;

public class PreviewActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preview);
        ImageView ivPreview = findViewById(R.id.iv_preview);
        ImageView ivBack = findViewById(R.id.iv_back);
        Button btnProcess = findViewById(R.id.btn_process);

        final String imagePath = getIntent().getStringExtra("SRC_PATH");
        Bitmap imageBmp = BitmapFactory.decodeFile(imagePath);
        Glide.with(PreviewActivity.this).asBitmap().load(imageBmp).into(ivPreview);



        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent move = new Intent(PreviewActivity.this, IdentificationActivity.class);
                startActivity(move);
            }
        });

        btnProcess.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent water = new Intent(PreviewActivity.this, ResultActivity.class);
                water.putExtra("SRC_PATH", imagePath);
                startActivity(water);
            }
        });

    }
}
