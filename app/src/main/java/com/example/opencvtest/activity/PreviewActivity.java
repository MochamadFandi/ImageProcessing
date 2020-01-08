package com.example.opencvtest.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import androidx.exifinterface.media.ExifInterface;

import com.bumptech.glide.Glide;
import com.example.opencvtest.R;
import com.github.chrisbanes.photoview.PhotoView;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Objects;

public class PreviewActivity extends AppCompatActivity {
    private String imagePath;
    private Bitmap imageBmp, rotatedBmp;
    private ProgressDialog progressDialog;

    public static Bitmap rotateImage(Bitmap source, float angle) {
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        return Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(),
                matrix, true);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preview);
        ImageView ivPreview = findViewById(R.id.iv_preview);
        ImageView ivBack = findViewById(R.id.iv_back);
        Button btnProcess = findViewById(R.id.btn_process);

        imagePath = getIntent().getStringExtra("SRC_PATH");

        decodeFilePath();
        checkRotation();
        Glide.with(PreviewActivity.this).asBitmap().load(rotatedBmp).into(ivPreview);

        ivPreview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder mBuilder = new AlertDialog.Builder(PreviewActivity.this);
                View mView = getLayoutInflater().inflate(R.layout.photo_view, null);
                PhotoView photoView = mView.findViewById(R.id.photo_view);
                Glide.with(PreviewActivity.this).asBitmap().load(rotatedBmp).into(photoView);
                mBuilder.setView(mView);
                final AlertDialog mDialog = mBuilder.create();
                mDialog.show();
            }
        });


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


                progressDialog = new ProgressDialog(PreviewActivity.this);
                progressDialog.show();
                progressDialog.setContentView(R.layout.progress_dialog);
                Objects.requireNonNull(progressDialog.getWindow()).setBackgroundDrawableResource(android.R.color.transparent);


                ByteArrayOutputStream originalStream = new ByteArrayOutputStream();
                rotatedBmp.compress(Bitmap.CompressFormat.PNG, 100, originalStream);
                byte[] originalArray = originalStream.toByteArray();


                Intent water = new Intent(PreviewActivity.this, ResultActivity.class);
                water.putExtra("SRC_PATH", originalArray);
                startActivity(water);
            }
        });

    }

    private void decodeFilePath() {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(imagePath, options);
        final int REQUIRED_SIZE = 200;
        int scale = 1;
        while (options.outWidth / scale / 2 >= REQUIRED_SIZE
                && options.outHeight / scale / 2 >= REQUIRED_SIZE)
            scale *= 2;
        options.inSampleSize = scale;
        options.inJustDecodeBounds = false;
        imageBmp = BitmapFactory.decodeFile(imagePath, options);

    }

    private void checkRotation() {
        ExifInterface ei = null;
        try {
            ei = new ExifInterface(imagePath);
        } catch (IOException e) {
            e.printStackTrace();
        }
        assert ei != null;
        int orientation = ei.getAttributeInt(ExifInterface.TAG_ORIENTATION,
                ExifInterface.ORIENTATION_UNDEFINED);

        switch (orientation) {

            case ExifInterface.ORIENTATION_ROTATE_90:
                rotatedBmp = rotateImage(imageBmp, 90);
                break;

            case ExifInterface.ORIENTATION_ROTATE_180:
                rotatedBmp = rotateImage(imageBmp, 180);
                break;

            case ExifInterface.ORIENTATION_ROTATE_270:
                rotatedBmp = rotateImage(imageBmp, 270);
                break;

            case ExifInterface.ORIENTATION_NORMAL:
            default:
                rotatedBmp = imageBmp;
        }
    }


}
