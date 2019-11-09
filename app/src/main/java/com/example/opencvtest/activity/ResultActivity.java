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
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.opencvtest.R;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.android.Utils;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfFloat;
import org.opencv.core.MatOfInt;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;

import java.io.ByteArrayOutputStream;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;


public class ResultActivity extends AppCompatActivity {
    protected static final String TAG = null;
    private Bitmap imageBmp, grayBmp, thresBmp, closingBmp, resultBmp;
    private Mat result;
    private String imagePath;


    static {
        OpenCVLoader.initDebug();
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
    private ImageView ivBack;
    private ImageView ivHome;
    private ImageView ivBefore;
    private ImageView ivAfter;
    private TextView tvType;
    private TextView tvEnergy;
    private TextView tvContrast;
    private TextView tvCorrelation;
    private TextView tvEntropy;
    private TextView tvHomogeneity;
    private Button btnDetail;

    @Override
    public void onResume() {
        super.onResume();
        OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_2_4_6, this, mLoaderCallback);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);
        initView();

        imagePath = getIntent().getStringExtra("SRC_PATH");
        decodeFilePath();
        Glide.with(ResultActivity.this).asBitmap().load(imageBmp).into(ivBefore);
        Process();
        Classification();


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
                ByteArrayOutputStream originalStream = new ByteArrayOutputStream();
                imageBmp.compress(Bitmap.CompressFormat.PNG, 100, originalStream);
                byte[] originalArray = originalStream.toByteArray();

                ByteArrayOutputStream grayStream = new ByteArrayOutputStream();
                grayBmp.compress(Bitmap.CompressFormat.PNG, 100, grayStream);
                byte[] grayArray = grayStream.toByteArray();

                ByteArrayOutputStream thresholdStream = new ByteArrayOutputStream();
                thresBmp.compress(Bitmap.CompressFormat.PNG, 100, thresholdStream);
                byte[] thresholdArray = thresholdStream.toByteArray();

                ByteArrayOutputStream closingStream = new ByteArrayOutputStream();
                closingBmp.compress(Bitmap.CompressFormat.PNG, 100, closingStream);
                byte[] closingArray = closingStream.toByteArray();

                ByteArrayOutputStream resultStream = new ByteArrayOutputStream();
                resultBmp.compress(Bitmap.CompressFormat.PNG, 100, resultStream);
                byte[] resultArray = resultStream.toByteArray();

                Intent move = new Intent(ResultActivity.this, DetailActivity.class);
                move.putExtra("SRC_PATH", imagePath);
                move.putExtra("ORIGINAL_DATA", originalArray);
                move.putExtra("GRAY_DATA", grayArray);
                move.putExtra("THRESHOLD_DATA", thresholdArray);
                move.putExtra("MORPHOLOGY_DATA", closingArray);
                move.putExtra("RESULT_DATA", resultArray);
                startActivity(move);
            }
        });


    }

    private void initView() {
        ivBack = findViewById(R.id.iv_back);
        ivHome = findViewById(R.id.iv_home);
        ivBefore = findViewById(R.id.iv_before);
        ivAfter = findViewById(R.id.iv_after);
        tvType = findViewById(R.id.tv_Type);
        tvEnergy = findViewById(R.id.tv_energy);
        tvContrast = findViewById(R.id.tv_contrast);
        tvCorrelation = findViewById(R.id.tv_correlation);
        tvEntropy = findViewById(R.id.tv_entropy);
        tvHomogeneity = findViewById(R.id.tv_homogeneity);
        btnDetail = findViewById(R.id.btn_detail);
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


    private void Process() {
        Mat gray = new Mat();
        Mat srcMat = new Mat();

        Utils.bitmapToMat(imageBmp, srcMat);
        Imgproc.cvtColor(srcMat, gray, Imgproc.COLOR_RGBA2GRAY);
        grayBmp = Bitmap.createBitmap(imageBmp.getWidth(), imageBmp.getHeight(), Bitmap.Config.RGB_565);
        Utils.matToBitmap(gray, grayBmp);

        Mat threshold = new Mat();
        Imgproc.threshold(gray, threshold, 0, 255, Imgproc.THRESH_BINARY_INV + Imgproc.THRESH_OTSU);
        thresBmp = Bitmap.createBitmap(imageBmp.getWidth(), imageBmp.getHeight(), Bitmap.Config.RGB_565);
        Utils.matToBitmap(threshold, thresBmp);

        Mat closing = new Mat();
        Mat kernel = Mat.ones(5, 5, CvType.CV_8U);
        Imgproc.morphologyEx(threshold, closing, Imgproc.MORPH_CLOSE, kernel, new Point(-1, -1), 3);
        closingBmp = Bitmap.createBitmap(imageBmp.getWidth(), imageBmp.getHeight(), Bitmap.Config.RGB_565);
        Utils.matToBitmap(closing, closingBmp);

        result = new Mat(srcMat.size(), CvType.CV_8UC3, new Scalar(255, 255, 255));
        gray.copyTo(result, closing);

        resultBmp = Bitmap.createBitmap(imageBmp.getWidth(), imageBmp.getHeight(), Bitmap.Config.RGB_565);
        Utils.matToBitmap(result, resultBmp);

        Glide.with(ResultActivity.this).asBitmap().load(resultBmp).into(ivAfter);
    }

    private void Classification() {

        List<Mat> srcList = new ArrayList<>();
        srcList.add(result);
        MatOfInt channels = new MatOfInt(0);
        Mat grayHist = new Mat();
        MatOfInt histSize = new MatOfInt(256);
        MatOfFloat histRange = new MatOfFloat(0, 256);

        Imgproc.calcHist(srcList, channels, new Mat(), grayHist, histSize, histRange);

        double S = 0;
        final double[] pixel = new double[256];
        for (int i = 0; i < 256; i++) {
            double[] histValues = grayHist.get(i, 0);
            for (double histValue : histValues) {
                pixel[i] = histValue;
                S += histValue;
            }
        }

        DecimalFormat df = new DecimalFormat("#,#####");
        df.setRoundingMode(RoundingMode.HALF_UP);

        DecimalFormat bf = new DecimalFormat("#,##");
        bf.setRoundingMode(RoundingMode.HALF_UP);

        double pb;
        Double enTemp;
        double hitungentropy = 0;

        for (int i = 0; i < 256; i++) {
            pb = pixel[i] / S;
            pb = Double.parseDouble(df.format(pb));

            enTemp = pb * (Math.log(pb) / Math.log(2));
            if (enTemp.isNaN()) {
                enTemp = (double) 0;
            }
            enTemp = Double.parseDouble(df.format(enTemp));

            hitungentropy += -(enTemp);
            hitungentropy = Double.parseDouble(df.format(hitungentropy));
            tvEntropy.setText(bf.format(hitungentropy));
        }


    }



}
