package com.example.opencvtest.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.example.opencvtest.R;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.android.Utils;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;


public class ResultActivity extends AppCompatActivity {
    protected static final String TAG = null;
    private Bitmap imageBmp, resultBmp;
    private ImageView ivAfter;
    Mat rgb = new Mat();
    Mat srcMat = new Mat();
    Mat threeChannel = new Mat();


    static {
        OpenCVLoader.initDebug();// Handle initialization error
    }

    private BaseLoaderCallback mLoaderCallback = new BaseLoaderCallback(this) {
        @Override
        public void onManagerConnected(int status) {
            if (status == LoaderCallbackInterface.SUCCESS) {
                Log.i(TAG, "OpenCV loaded successfully");
            } else {
                super.onManagerConnected(status);
            }
        }
    };

    @Override
    public void onResume() {
        super.onResume();
        OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_2_4_6, this, mLoaderCallback);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);
        ImageView ivBefore = findViewById(R.id.iv_before);
        ImageView ivBack = findViewById(R.id.iv_back);
        ImageView ivHome = findViewById(R.id.iv_home);
        Button btnDetail = findViewById(R.id.btn_detail);
        ivAfter = findViewById(R.id.iv_after);

        BitmapFactory.Options mBitmapOptions = new BitmapFactory.Options();
        mBitmapOptions.inScaled = true;
        mBitmapOptions.inSampleSize = 4;

        String imagePath = getIntent().getStringExtra("WATER_SEGMENT");
        imageBmp = BitmapFactory.decodeFile(imagePath, mBitmapOptions);
        Glide.with(ResultActivity.this).asBitmap().load(imageBmp).into(ivBefore);
        watershed3();


        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent move = new Intent(ResultActivity.this, IdentificationActivity.class);
                startActivity(move);
            }
        });

        ivHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent move = new Intent(ResultActivity.this, MainActivity.class);
                startActivity(move);
            }
        });

        btnDetail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                resultBmp.compress(Bitmap.CompressFormat.PNG, 100, stream);
                byte[] byteArray = stream.toByteArray();

                Intent move = new Intent(ResultActivity.this, DetailActivity.class);
                move.putExtra("BITMAP DATA", byteArray);
                startActivity(move);
            }
        });


    }

    private void watershed1() {
        Utils.bitmapToMat(imageBmp, srcMat);
        Imgproc.cvtColor(srcMat, rgb, Imgproc.COLOR_RGBA2RGB);

        Imgproc.cvtColor(rgb, threeChannel, Imgproc.COLOR_RGB2GRAY);
        Imgproc.threshold(threeChannel, threeChannel, 100, 255, Imgproc.THRESH_OTSU);

        Mat fg = new Mat(rgb.size(), CvType.CV_8U);
        Imgproc.erode(threeChannel, fg, new Mat(), new Point(-1, -1), 2);

        Mat bg = new Mat(rgb.size(), CvType.CV_8U);
        Imgproc.dilate(threeChannel, bg, new Mat(), new Point(-1, -1), 3);
        Imgproc.threshold(bg, bg, 1, 128, Imgproc.THRESH_BINARY_INV);

        Mat markers = new Mat(rgb.size(), CvType.CV_8U, new Scalar(0));
        Core.add(fg, bg, markers);

        Mat marker_tempo = new Mat();
        markers.convertTo(marker_tempo, CvType.CV_32SC1);

        Imgproc.watershed(rgb, marker_tempo);
        marker_tempo.convertTo(markers, CvType.CV_8U);

        resultBmp = Bitmap.createBitmap(imageBmp.getWidth(), imageBmp.getHeight(), Bitmap.Config.RGB_565);

        Imgproc.applyColorMap(markers, markers, 4);
        Utils.matToBitmap(markers, resultBmp);

        Glide.with(ResultActivity.this).asBitmap().load(resultBmp).into(ivAfter);


    }


    private void watershed2() {
        Utils.bitmapToMat(imageBmp, srcMat);
        Imgproc.cvtColor(srcMat, srcMat, Imgproc.COLOR_BGRA2GRAY);
        Imgproc.threshold(srcMat, threeChannel, 0, 255, Imgproc.THRESH_BINARY_INV + Imgproc.THRESH_OTSU);

        Mat kernel = Mat.ones(3, 3, CvType.CV_8U);
        Imgproc.morphologyEx(srcMat, srcMat, Imgproc.MORPH_OPEN, kernel, new Point(-1, -1), 2);

        Mat bg = new Mat(srcMat.size(), CvType.CV_8U);
        Imgproc.dilate(srcMat, bg, kernel, new Point(-1, -1), 3);

        Mat fg = new Mat(srcMat.size(), CvType.CV_8U);
        Imgproc.distanceTransform(srcMat, fg, Imgproc.DIST_L2, 5);
        Imgproc.threshold(fg, fg, 0.7 * fg.total(), 255, 0);


        Bitmap resultBmp = Bitmap.createBitmap(imageBmp.getWidth(), imageBmp.getHeight(), Bitmap.Config.ARGB_8888);
        Utils.matToBitmap(srcMat, resultBmp);


        Glide.with(ResultActivity.this).asBitmap().load(resultBmp).into(ivAfter);
    }

    private void watershed3() {
        Utils.bitmapToMat(imageBmp, srcMat);
        byte[] srcData = new byte[(int) (srcMat.total() * srcMat.channels())];
        srcMat.get(0, 0, srcData);
        for (int i = 0; i < srcMat.rows(); i++) {
            for (int j = 0; j < srcMat.cols(); j++) {
                if (srcData[(i * srcMat.cols() + j) * 3] == (byte) 255 && srcData[(i * srcMat.cols() + j) * 3 + 1] == (byte) 255
                        && srcData[(i * srcMat.cols() + j) * 3 + 2] == (byte) 255) {
                    srcData[(i * srcMat.cols() + j) * 3] = 0;
                    srcData[(i * srcMat.cols() + j) * 3 + 1] = 0;
                    srcData[(i * srcMat.cols() + j) * 3 + 2] = 0;
                }
            }
        }
        srcMat.put(0, 0, srcData);

        Imgproc.cvtColor(srcMat, srcMat, Imgproc.COLOR_RGBA2RGB);


        Mat kernel = new Mat(3, 3, CvType.CV_32F);

        float[] kernelData = new float[(int) (kernel.total() * kernel.channels())];
        kernelData[0] = 1;
        kernelData[1] = 1;
        kernelData[2] = 1;
        kernelData[3] = 1;
        kernelData[4] = -8;
        kernelData[5] = 1;
        kernelData[6] = 1;
        kernelData[7] = 1;
        kernelData[8] = 1;
        kernel.put(0, 0, kernelData);

        Mat imgLaplacian = new Mat();
        Imgproc.filter2D(srcMat, imgLaplacian, CvType.CV_32F, kernel);
        Mat sharp = new Mat();
        srcMat.convertTo(sharp, CvType.CV_32F);
        Mat imgResult = new Mat();
        Core.subtract(sharp, imgLaplacian, imgResult);

        imgResult.convertTo(imgResult, CvType.CV_8UC3);
        imgLaplacian.convertTo(imgLaplacian, CvType.CV_8UC3);

        Mat bw = new Mat();
        Imgproc.cvtColor(imgResult, bw, Imgproc.COLOR_RGB2GRAY);
        Imgproc.threshold(bw, bw, 40, 255, Imgproc.THRESH_BINARY | Imgproc.THRESH_OTSU);

        Mat dist = new Mat();
        Imgproc.distanceTransform(bw, dist, Imgproc.DIST_L2, 3);

        Core.normalize(dist, dist, 0.0, 1.0, Core.NORM_MINMAX);
        Mat distDisplayScaled = new Mat();
        Core.multiply(dist, new Scalar(255), distDisplayScaled);
        Mat distDisplay = new Mat();
        distDisplayScaled.convertTo(distDisplay, CvType.CV_8U);

        Imgproc.threshold(dist, dist, 0.4, 1.0, Imgproc.THRESH_BINARY);

        Mat kernel1 = Mat.ones(3, 3, CvType.CV_8U);
        Imgproc.dilate(dist, dist, kernel1);
        Mat distDisplay2 = new Mat();
        dist.convertTo(distDisplay2, CvType.CV_8U);
        Core.multiply(distDisplay2, new Scalar(255), distDisplay2);

        Mat dist_8u = new Mat();
        dist.convertTo(dist_8u, CvType.CV_8U);

        List<MatOfPoint> contours = new ArrayList<>();
        Mat hierarchy = new Mat();
        Imgproc.findContours(dist_8u, contours, hierarchy, Imgproc.RETR_EXTERNAL, Imgproc.CHAIN_APPROX_SIMPLE);

        Mat markers = Mat.zeros(dist.size(), CvType.CV_32S);

        for (int i = 0; i < contours.size(); i++) {
            Imgproc.drawContours(markers, contours, i, new Scalar(i + 1), -1);
        }

        Mat markersScaled = new Mat();
        markers.convertTo(markersScaled, CvType.CV_32F);
        Core.normalize(markersScaled, markersScaled, 0.0, 255.0, Core.NORM_MINMAX);
        Imgproc.circle(markersScaled, new Point(5, 5), 3, new Scalar(255, 255, 255), -1);
        Mat markersDisplay = new Mat();
        markersScaled.convertTo(markersDisplay, CvType.CV_8U);
        Imgproc.circle(markers, new Point(5, 5), 3, new Scalar(255, 255, 255), -1);

        Imgproc.watershed(imgResult, markers);

        Mat mark = Mat.zeros(markers.size(), CvType.CV_8U);
        markers.convertTo(mark, CvType.CV_8UC1);
        Core.bitwise_not(mark, mark);

        Random rng = new Random(12345);
        List<Scalar> colors = new ArrayList<>(contours.size());
        for (int i = 0; i < contours.size(); i++) {
            int b = rng.nextInt(256);
            int g = rng.nextInt(256);
            int r = rng.nextInt(256);
            colors.add(new Scalar(b, g, r));
        }
        // Create the result image
        Mat dst = Mat.zeros(markers.size(), CvType.CV_8UC3);
        byte[] dstData = new byte[(int) (dst.total() * dst.channels())];
        dst.get(0, 0, dstData);
        // Fill labeled objects with random colors
        int[] markersData = new int[(int) (markers.total() * markers.channels())];
        markers.get(0, 0, markersData);
        for (int i = 0; i < markers.rows(); i++) {
            for (int j = 0; j < markers.cols(); j++) {
                int index = markersData[i * markers.cols() + j];
                if (index > 0 && index <= contours.size()) {
                    dstData[(i * dst.cols() + j) * 3] = (byte) colors.get(index - 1).val[0];
                    dstData[(i * dst.cols() + j) * 3 + 1] = (byte) colors.get(index - 1).val[1];
                    dstData[(i * dst.cols() + j) * 3 + 2] = (byte) colors.get(index - 1).val[2];
                } else {
                    dstData[(i * dst.cols() + j) * 3] = 0;
                    dstData[(i * dst.cols() + j) * 3 + 1] = 0;
                    dstData[(i * dst.cols() + j) * 3 + 2] = 0;
                }
            }
        }
        dst.put(0, 0, dstData);

        Bitmap resultBmp = Bitmap.createBitmap(imageBmp.getWidth(), imageBmp.getHeight(), Bitmap.Config.ARGB_8888);
        Utils.matToBitmap(dst, resultBmp);


        Glide.with(ResultActivity.this).asBitmap().load(resultBmp).into(ivAfter);
    }


}
