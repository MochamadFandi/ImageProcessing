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
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfFloat;
import org.opencv.core.MatOfInt;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;


public class ResultActivity extends AppCompatActivity {
    protected static final String TAG = null;

    static {
        OpenCVLoader.initDebug();
    }

    private Bitmap imageBmp, grayBmp, histBmp, thresBmp, closingBmp, resultBmp, outputBmp;
    private Mat gray, finalResult;
    private String imagePath;
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
        Segmentation();
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

                ByteArrayOutputStream histStream = new ByteArrayOutputStream();
                histBmp.compress(Bitmap.CompressFormat.PNG, 100, histStream);
                byte[] histArray = histStream.toByteArray();

                ByteArrayOutputStream thresholdStream = new ByteArrayOutputStream();
                thresBmp.compress(Bitmap.CompressFormat.PNG, 100, thresholdStream);
                byte[] thresholdArray = thresholdStream.toByteArray();

                ByteArrayOutputStream closingStream = new ByteArrayOutputStream();
                closingBmp.compress(Bitmap.CompressFormat.PNG, 100, closingStream);
                byte[] closingArray = closingStream.toByteArray();

                ByteArrayOutputStream resultStream = new ByteArrayOutputStream();
                outputBmp.compress(Bitmap.CompressFormat.PNG, 100, resultStream);
                byte[] resultArray = resultStream.toByteArray();

                Intent move = new Intent(ResultActivity.this, DetailActivity.class);
                move.putExtra("SRC_PATH", imagePath);
                move.putExtra("ORIGINAL_DATA", originalArray);
                move.putExtra("GRAY_DATA", grayArray);
                move.putExtra("HIST_DATA", histArray);
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

    private void Segmentation() {
        Mat srcMat = new Mat(512, 384, CvType.CV_8UC3);
        gray = new Mat();

        Utils.bitmapToMat(imageBmp, srcMat);
        Imgproc.cvtColor(srcMat, gray, Imgproc.COLOR_RGBA2GRAY);
        grayBmp = Bitmap.createBitmap(imageBmp.getWidth(), imageBmp.getHeight(), Bitmap.Config.RGB_565);
        Utils.matToBitmap(gray, grayBmp);

        grayscaleHistogram();

        Mat threshold = new Mat();
        Imgproc.threshold(gray, threshold, 0, 255, Imgproc.THRESH_BINARY_INV + Imgproc.THRESH_OTSU);
        thresBmp = Bitmap.createBitmap(imageBmp.getWidth(), imageBmp.getHeight(), Bitmap.Config.RGB_565);
        Utils.matToBitmap(threshold, thresBmp);

        Mat closing = new Mat();
        Mat kernel = Mat.ones(5, 5, CvType.CV_8U);
        Imgproc.morphologyEx(threshold, closing, Imgproc.MORPH_CLOSE, kernel, new Point(-1, -1), 3);
        closingBmp = Bitmap.createBitmap(imageBmp.getWidth(), imageBmp.getHeight(), Bitmap.Config.RGB_565);
        Utils.matToBitmap(closing, closingBmp);

        Mat result = new Mat();
        Core.subtract(closing, gray, result);
        Core.subtract(closing, result, result);

        resultBmp = Bitmap.createBitmap(imageBmp.getWidth(), imageBmp.getHeight(), Bitmap.Config.RGB_565);
        Utils.matToBitmap(result, resultBmp);

        makeBlackTransparent();

        Glide.with(ResultActivity.this).asBitmap().load(outputBmp).into(ivAfter);
    }

    private void grayscaleHistogram() {

        List<Mat> srcList = new ArrayList<>();
        srcList.add(gray);
        MatOfInt channels = new MatOfInt(0);
        Mat grayHist = new Mat();
        int histSize = 256;
        MatOfFloat histRange = new MatOfFloat(0, 256);

        Imgproc.calcHist(srcList, channels, new Mat(), grayHist, new MatOfInt(histSize), histRange);
        int histW = 512, histH = 400;
        int binW = (int) Math.round((double) histW / histSize);
        Mat histImage = new Mat(histH, histW, CvType.CV_8UC3, new Scalar(0, 0, 0));

        Core.normalize(grayHist, grayHist, 0, histImage.rows(), Core.NORM_MINMAX);
        float[] bHistData = new float[(int) (grayHist.total() * grayHist.channels())];
        grayHist.get(0, 0, bHistData);

        for (int i = 1; i < histSize; i++) {
            Imgproc.line(histImage, new Point(binW * (i - 1), histH - Math.round(bHistData[i - 1])),
                    new Point(binW * (i), histH - Math.round(bHistData[i])), new Scalar(255, 0, 0), 2);
        }
        histBmp = Bitmap.createBitmap(histImage.cols(), histImage.rows(), Bitmap.Config.ARGB_8888);
        Utils.matToBitmap(histImage, histBmp);
    }

    private void makeBlackTransparent() {
        // convert image to matrix
        Mat src = new Mat(imageBmp.getWidth(), imageBmp.getHeight(), CvType.CV_8UC4);
        Utils.bitmapToMat(resultBmp, src);

        // init new matrices
        finalResult = new Mat(imageBmp.getWidth(), imageBmp.getHeight(), CvType.CV_8UC4);
        Mat tmp = new Mat(imageBmp.getWidth(), imageBmp.getHeight(), CvType.CV_8UC4);
        Mat alpha = new Mat(imageBmp.getWidth(), imageBmp.getHeight(), CvType.CV_8UC4);

        // convert image to grayscale
        Imgproc.cvtColor(src, tmp, Imgproc.COLOR_BGR2GRAY);

        // threshold the image to create alpha channel with complete transparency in black background region and zero transparency in foreground object region.
        Imgproc.threshold(tmp, alpha, 0, 255, Imgproc.THRESH_BINARY);

        // split the original image into three single channel.
        List<Mat> rgb = new ArrayList<>(3);
        Core.split(src, rgb);

        // Create the final result by merging three single channel and alpha(BGRA order)
        List<Mat> rgba = new ArrayList<>(4);
        rgba.add(rgb.get(0));
        rgba.add(rgb.get(1));
        rgba.add(rgb.get(2));
        rgba.add(alpha);
        Core.merge(rgba, finalResult);

        // convert matrix to output bitmap
        outputBmp = Bitmap.createBitmap(imageBmp.getWidth(), imageBmp.getHeight(), Bitmap.Config.ARGB_8888);
        Utils.matToBitmap(finalResult, outputBmp);
    }

    private void Classification() {

        Mat gl = Mat.zeros(256, 256, CvType.CV_64F);
        Mat glt = gl.clone();

        for (int y = 0; y < finalResult.rows(); y++) {
            for (int x = 0; x < finalResult.cols()-1; x++) {

                int i = (int) finalResult.get(y, x)[0];
                int j = (int) finalResult.get(y, x + 1)[0];

                double[] count = gl.get(i, j);
                count[0]++;
                gl.put(i, j, count);
            }
        }

        //GLCM Transpose
        Core.transpose(gl, glt);

        //Symmetric Matrix
        Core.add(gl, glt, gl);

        //Matrix Normalization
        Scalar sum = Core.sumElems(gl);
        Core.divide(gl, sum, gl);


    }


}
