package com.example.opencvtest.activity;

import android.Manifest;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.opencvtest.R;



public class IdentificationActivity extends AppCompatActivity {

    private static final int CAMERA_REQUEST = 1007;
    private static final int GALLERY_REQUEST = 107;
    private static final int PERMISSION_REQUEST = 107;
    private Uri imageURI;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_identification);
        ImageView ivBack = findViewById(R.id.iv_back);
        CardView cvCamera = findViewById(R.id.cv_camera);
        CardView cvGallery = findViewById(R.id.cv_gallery);
        checkingPermission();

        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent move = new Intent(IdentificationActivity.this, MainActivity.class);
                startActivity(move);
            }
        });

        cvCamera.setOnClickListener(new View.OnClickListener() {

            public void onClick(View view) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (checkSelfPermission(Manifest.permission.CAMERA) == PackageManager.PERMISSION_DENIED ||
                            checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED) {
                        String[] cameraPermission = {Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE};
                        requestPermissions(cameraPermission, PERMISSION_REQUEST);
                    } else {
                        openCamera();
                    }
                } else {
                    openCamera();
                }
            }
        });

        cvGallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED) {
                        String[] galleryPermission = {Manifest.permission.READ_EXTERNAL_STORAGE};
                        requestPermissions(galleryPermission, PERMISSION_REQUEST);
                    } else {
                        openGallery();
                    }
                } else {
                    openGallery();
                }
            }
        });
    }

    private void checkingPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.CAMERA) == PackageManager.PERMISSION_DENIED ||
                    checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED) {
                String[] cameraPermission = {Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE};
                requestPermissions(cameraPermission, PERMISSION_REQUEST);
            }
        }
    }

    private void openGallery() {
        Intent gallery = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(gallery, GALLERY_REQUEST);
    }

    private void openCamera() {
        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.TITLE, "New Picture");
        values.put(MediaStore.Images.Media.DESCRIPTION, "From App");
        imageURI = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);


        Intent camera = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        camera.putExtra(MediaStore.EXTRA_OUTPUT, imageURI);
        startActivityForResult(camera, CAMERA_REQUEST);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == PERMISSION_REQUEST) {
            if (grantResults.length >0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                Toast.makeText(this, "Permission Granted", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Permission Denied", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CAMERA_REQUEST && resultCode == RESULT_OK) {

            String[] imagePathColumn = {MediaStore.Images.Media.DATA};
            Cursor cursor = getContentResolver().query(imageURI, imagePathColumn, null, null, null);
            assert cursor != null;
            cursor.moveToFirst();
            int columnIndex = cursor.getColumnIndex(imagePathColumn[0]);
            String imagePath = cursor.getString(columnIndex);
            cursor.close();
            Intent camera = new Intent(IdentificationActivity.this, PreviewActivity.class);
            camera.putExtra("SRC_PATH", imagePath);
            startActivity(camera);
        } else if (requestCode == GALLERY_REQUEST && resultCode == RESULT_OK) {

            assert data != null;
            imageURI = data.getData();
            String[] imagePathColumn = {MediaStore.Images.Media.DATA};
            assert imageURI != null;
            Cursor cursor = getContentResolver().query(imageURI, imagePathColumn, null, null, null);
            assert cursor != null;
            cursor.moveToFirst();
            int columnIndex = cursor.getColumnIndex(imagePathColumn[0]);
            String imagePath = cursor.getString(columnIndex);
            cursor.close();
            Intent gallery = new Intent(IdentificationActivity.this, PreviewActivity.class);
            gallery.putExtra("SRC_PATH", imagePath);
            startActivity(gallery);

        }

    }



}
