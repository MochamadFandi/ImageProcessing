package com.example.opencvtest.activity;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.view.View;
import android.widget.ImageView;

import com.example.opencvtest.R;


public class IdentificationActivity extends AppCompatActivity {

    private static final int CAMERA_REQUEST = 1007;
    private static final int GALLERY_REQUEST = 107;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_identification);
        ImageView ivBack = findViewById(R.id.iv_back);
        CardView cvCamera = findViewById(R.id.cv_camera);
        CardView cvGallery = findViewById(R.id.cv_gallery);

        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent move = new Intent(IdentificationActivity.this, MainActivity.class);
                startActivity(move);
            }
        });

        cvCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent camera = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(camera, CAMERA_REQUEST);

            }
        });

        cvGallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent gallery = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(gallery, GALLERY_REQUEST);
            }
        });
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        Uri imageURI;
        if (requestCode == CAMERA_REQUEST && resultCode == RESULT_OK){
            assert data != null;
            imageURI = data.getData();
            String[] imagePathColumn = {MediaStore.Images.Media.DATA};
            assert imageURI != null;
            Cursor cursor = getContentResolver().query(imageURI,imagePathColumn,null,null, null);
            assert cursor != null;
            cursor.moveToFirst();
            int columnIndex = cursor.getColumnIndex(imagePathColumn[0]);
            String imagePath = cursor.getString(columnIndex);
            cursor.close();
            Intent camera = new Intent(IdentificationActivity.this, PreviewActivity.class);
            camera.putExtra("IMAGE_DATA", imagePath);
            startActivity(camera);
        } else if (requestCode == GALLERY_REQUEST && resultCode == RESULT_OK){
            assert data != null;
            imageURI = data.getData();
            String[] imagePathColumn = {MediaStore.Images.Media.DATA};
            assert imageURI != null;
            Cursor cursor = getContentResolver().query(imageURI, imagePathColumn,null,null,null);
            assert cursor != null;
            cursor.moveToFirst();
            int columnIndex = cursor.getColumnIndex(imagePathColumn[0]);
            String imagePath = cursor.getString(columnIndex);
            cursor.close();
            Intent gallery = new Intent(IdentificationActivity.this, PreviewActivity.class);
            gallery.putExtra("IMAGE_DATA", imagePath);
            startActivity(gallery);

        }

    }
}
