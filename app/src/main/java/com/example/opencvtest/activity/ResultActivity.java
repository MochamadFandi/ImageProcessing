package com.example.opencvtest.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.opencvtest.R;
import com.example.opencvtest.adapter.SliderResultAdapter;
import com.example.opencvtest.data.Result;
import com.github.chrisbanes.photoview.PhotoView;

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
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;


public class ResultActivity extends AppCompatActivity {
    protected static final String TAG = null;

    static {
        OpenCVLoader.initDebug();
    }
    double energy, entropy, correlation, contrast, homogeneity = 0.0;

    double meanx_0, meany_0, stdevx_0, stdevy_0, energy_0, entropy_0, contrast_0, correlation_0, homogeneity_0 = 0.0;
    double meanx_45, meany_45, stdevx_45, stdevy_45, energy_45, entropy_45, contrast_45, correlation_45, homogeneity_45 = 0.0;
    double meanx_90, meany_90, stdevx_90, stdevy_90, energy_90, entropy_90, contrast_90, correlation_90, homogeneity_90 = 0.0;
    double meanx_135, meany_135, stdevx_135, stdevy_135, energy_135, entropy_135, contrast_135, correlation_135, homogeneity_135 = 0.0;

    private Bitmap imageBmp, grayBmp, histBmp, thresBmp, closingBmp, resultBmp, outputBmp;
    private Mat finalResult;
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
    private TextView tvType;
    private ImageView ivAfter;
    private ViewPager vpResult;
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

        byte[] originalArray = getIntent().getByteArrayExtra("SRC_PATH");
        imageBmp = BitmapFactory.decodeByteArray(originalArray, 0, originalArray.length);

        Glide.with(ResultActivity.this).asBitmap().load(imageBmp).into(ivBefore);
        Segmentation();
        calcGLCM_0();
        sliderResult();

        ivBefore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder mBuilder = new AlertDialog.Builder(ResultActivity.this);
                View mView = getLayoutInflater().inflate(R.layout.photo_view, null);
                PhotoView photoView = mView.findViewById(R.id.photo_view);
                Glide.with(ResultActivity.this).asBitmap().load(imageBmp).into(photoView);
                mBuilder.setView(mView);
                final AlertDialog mDialog = mBuilder.create();
                mDialog.show();
            }
        });

        ivAfter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder mBuilder = new AlertDialog.Builder(ResultActivity.this);
                View mView = getLayoutInflater().inflate(R.layout.photo_view, null);
                PhotoView photoView = mView.findViewById(R.id.photo_view);
                Glide.with(ResultActivity.this).asBitmap().load(outputBmp).into(photoView);
                mBuilder.setView(mView);
                final AlertDialog mDialog = mBuilder.create();
                mDialog.show();
            }
        });


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
        tvType = findViewById(R.id.tv_type);
        vpResult = findViewById(R.id.vp_result);
        btnDetail = findViewById(R.id.btn_detail);
    }


    private void Segmentation() {
        Mat srcMat = new Mat(512, 384, CvType.CV_8UC3);
        Mat gray = new Mat();

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

        Mat result = new Mat(srcMat.size(), CvType.CV_8UC3, new Scalar(255, 255, 255));
        gray.copyTo(result, closing);

        resultBmp = Bitmap.createBitmap(imageBmp.getWidth(), imageBmp.getHeight(), Bitmap.Config.RGB_565);
        Utils.matToBitmap(result, resultBmp);

        makeBlackTransparent();
        grayscaleHistogram();

        Glide.with(ResultActivity.this).asBitmap().load(outputBmp).into(ivAfter);
    }

    private void grayscaleHistogram() {

        List<Mat> srcList = new ArrayList<>();
        srcList.add(finalResult);
        MatOfInt channels = new MatOfInt(0);
        Mat grayHist = new Mat();
        int histSize = 256;
        MatOfFloat histRange = new MatOfFloat(0, 256);

        Imgproc.calcHist(srcList, channels, new Mat(), grayHist, new MatOfInt(histSize), histRange);
        int histW = 512, histH = 400;
        int binW = (int) Math.round((double) histW / histSize);
        Mat histImage = new Mat(histH, histW, CvType.CV_8UC3, new Scalar(0, 0, 0));

        Core.normalize(grayHist, grayHist, 0, histImage.rows(), Core.NORM_MINMAX);
        float[] grayHistData = new float[(int) (grayHist.total() * grayHist.channels())];
        grayHist.get(0, 0, grayHistData);

        for (int i = 1; i < histSize; i++) {
            Imgproc.line(histImage, new Point(binW * (i - 1), histH - Math.round(grayHistData[i - 1])),
                    new Point(binW * (i), histH - Math.round(grayHistData[i])), new Scalar(220, 220, 220), 2);
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
        Mat kernel = Mat.ones(3, 3, CvType.CV_8U);
        Imgproc.morphologyEx(alpha, alpha, Imgproc.MORPH_CLOSE, kernel, new Point(-1, -1), 3);

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

    private void calcGLCM_0() {


        Mat gl = Mat.zeros(256, 256, CvType.CV_64F);
        Mat glt = gl.clone();
        Mat gl_45 = Mat.zeros(256, 256, CvType.CV_64F);
        Mat glt_45 = gl_45.clone();
        Mat gl_90 = Mat.zeros(256, 256, CvType.CV_64F);
        Mat glt_90 = gl_90.clone();
        Mat gl_135 = Mat.zeros(256, 256, CvType.CV_64F);
        Mat glt_135 = gl_135.clone();

        //Create GLCM d= 1 , angle = 0
        for (int y = 0; y < finalResult.rows(); y++) {
            for (int x = 0; x < finalResult.cols() - 1; x++) {

                int i = (int) finalResult.get(y, x)[0];
                int j = (int) finalResult.get(y, x + 1)[0];

                double[] count = gl.get(i, j);
                count[0]++;
                gl.put(i, j, count);
            }
        }

        //Create GLCM d= 1 , angle = 45
        for (int y = 0; y < finalResult.rows() - 1; y++) {
            for (int x = 0; x < finalResult.cols() - 1; x++) {

                int i = (int) finalResult.get(y + 1, x)[0];
                int j = (int) finalResult.get(y, x + 1)[0];

                double[] count = gl_45.get(i, j);
                count[0]++;
                gl_45.put(i, j, count);
            }
        }

        //Create GLCM d= 1 , angle = 90
        for (int y = 0; y < finalResult.rows() - 1; y++) {
            for (int x = 0; x < finalResult.cols(); x++) {

                int i = (int) finalResult.get(y + 1, x)[0];
                int j = (int) finalResult.get(y, x)[0];

                double[] count = gl_90.get(i, j);
                count[0]++;
                gl_90.put(i, j, count);
            }
        }

        //Create GLCM d= 1 , angle = 135
        for (int y = 0; y < finalResult.rows() - 1; y++) {
            for (int x = 0; x < finalResult.cols() - 1; x++) {

                int i = (int) finalResult.get(y + 1, x + 1)[0];
                int j = (int) finalResult.get(y, x)[0];

                double[] count = gl_135.get(i, j);
                count[0]++;
                gl_135.put(i, j, count);
            }
        }

        //GLCM Transpose
        Core.transpose(gl, glt);
        Core.transpose(gl_45, glt_45);
        Core.transpose(gl_90, glt_90);
        Core.transpose(gl_135, glt_135);


        //Symmetric Matrix
        Core.add(gl, glt, gl);
        Core.add(gl_45, glt_45, gl_45);
        Core.add(gl_90, glt_90, gl_90);
        Core.add(gl_135, glt_135, gl_135);


        //Matrix Normalization
        Scalar sum = Core.sumElems(gl);
        Core.divide(gl, sum, gl);

        Scalar sum_45 = Core.sumElems(gl_45);
        Core.divide(gl_45, sum_45, gl_45);

        Scalar sum_90 = Core.sumElems(gl_90);
        Core.divide(gl_90, sum_90, gl_90);

        Scalar sum_135 = Core.sumElems(gl_135);
        Core.divide(gl_135, sum_135, gl_135);

        double[] px = new double[256];
        double[] py = new double[256];

        double[] px_45 = new double[256];
        double[] py_45 = new double[256];

        double[] px_90 = new double[256];
        double[] py_90 = new double[256];

        double[] px_135 = new double[256];
        double[] py_135 = new double[256];


        for (int i = 0; i < 256; i++) {
            px[i] = 0.0;
            py[i] = 0.0;
            px_45[i] = 0.0;
            py_45[i] = 0.0;
            px_90[i] = 0.0;
            py_90[i] = 0.0;
            px_135[i] = 0.0;
            py_135[i] = 0.0;
        }

        // sum the glcm rows to Px(i)
        for (int i = 0; i < 256; i++) {
            for (int j = 0; j < 256; j++) {
                px[i] += gl.get(i, j)[0];
                px_45[i] += gl_45.get(i, j)[0];
                px_90[i] += gl_90.get(i, j)[0];
                px_135[i] += gl_135.get(i, j)[0];

            }
        }

        // sum the glcm rows to Py(j)
        for (int j = 0; j < 256; j++) {
            for (int i = 0; i < 256; i++) {
                py[j] += gl.get(i, j)[0];
                py_45[j] += gl_45.get(i, j)[0];
                py_90[j] += gl_90.get(i, j)[0];
                py_135[j] += gl_135.get(i, j)[0];

            }
        }

        // calculate meanx and meany
        for (int i = 0; i < 256; i++) {
            meanx_0 += (i * px[i]);
            meany_0 += (i * py[i]);
            meanx_45 += (i * px_45[i]);
            meany_45 += (i * py_45[i]);
            meanx_90 += (i * px_90[i]);
            meany_90 += (i * py_90[i]);
            meanx_135 += (i * px_135[i]);
            meany_135 += (i * py_135[i]);
        }

        // calculate stdevx and stdevy
        for (int i = 0; i < 256; i++) {
            stdevx_0 += ((Math.pow((i - meanx_0), 2)) * px[i]);
            stdevy_0 += ((Math.pow((i - meany_0), 2)) * py[i]);
            stdevx_45 += ((Math.pow((i - meanx_45), 2)) * px_45[i]);
            stdevy_45 += ((Math.pow((i - meany_45), 2)) * py_45[i]);
            stdevx_90 += ((Math.pow((i - meanx_90), 2)) * px_90[i]);
            stdevy_90 += ((Math.pow((i - meany_90), 2)) * py_90[i]);
            stdevx_135 += ((Math.pow((i - meanx_135), 2)) * px_135[i]);
            stdevy_135 += ((Math.pow((i - meany_135), 2)) * py_135[i]);
        }

        for (int i = 0; i < 256; i++) {
            for (int j = 0; j < 256; j++) {
                energy_0 += Math.pow(gl.get(i, j)[0], 2);
                energy_45 += Math.pow(gl_45.get(i, j)[0], 2);
                energy_90 += Math.pow(gl_90.get(i, j)[0], 2);
                energy_135 += Math.pow(gl_135.get(i, j)[0], 2);

            }
        }

        for (int i = 0; i < 256; i++) {
            for (int j = 0; j < 256; j++) {
                contrast_0 += Math.pow(i - j, 2) * (gl.get(i, j)[0]);
                contrast_45 += Math.pow(i - j, 2) * (gl_45.get(i, j)[0]);
                contrast_90 += Math.pow(i - j, 2) * (gl_90.get(i, j)[0]);
                contrast_135 += Math.pow(i - j, 2) * (gl_135.get(i, j)[0]);

            }
        }

        for (int i = 0; i < 256; i++) {
            for (int j = 0; j < 256; j++) {
                correlation_0 += ((((i - meanx_0) * (j - meany_0)) / (stdevx_0 * stdevy_0)) * gl.get(i, j)[0]);
                correlation_45 += ((((i - meanx_45) * (j - meany_45)) / (stdevx_45 * stdevy_45)) * gl_45.get(i, j)[0]);
                correlation_90 += ((((i - meanx_90) * (j - meany_90)) / (stdevx_90 * stdevy_90)) * gl_90.get(i, j)[0]);
                correlation_135 += ((((i - meanx_135) * (j - meany_135)) / (stdevx_135 * stdevy_135)) * gl_135.get(i, j)[0]);

            }
        }

        for (int i = 0; i < 256; i++) {
            for (int j = 0; j < 256; j++) {
                if (gl.get(i, j)[0] != 0) {
                    entropy_0 = entropy_0 - (gl.get(i, j)[0] * (Math.log(gl.get(i, j)[0])));
                }
                if (gl_45.get(i, j)[0] != 0) {
                    entropy_45 = entropy_45 - (gl_45.get(i, j)[0] * (Math.log(gl_45.get(i, j)[0])));
                }
                if (gl_90.get(i, j)[0] != 0) {
                    entropy_90 = entropy_90 - (gl_90.get(i, j)[0] * (Math.log(gl_90.get(i, j)[0])));
                }
                if (gl_135.get(i, j)[0] != 0) {
                    entropy_135 = entropy_135 - (gl_135.get(i, j)[0] * (Math.log(gl_135.get(i, j)[0])));
                }
            }
        }

        for (int i = 0; i < 256; i++) {
            for (int j = 0; j < 256; j++) {
                homogeneity_0 += gl.get(i, j)[0] / (1.0 + Math.abs(i - j));
                homogeneity_45 += gl_45.get(i, j)[0] / (1.0 + Math.abs(i - j));
                homogeneity_90 += gl_90.get(i, j)[0] / (1.0 + Math.abs(i - j));
                homogeneity_135 += gl_135.get(i, j)[0] / (1.0 + Math.abs(i - j));

            }
        }

        DecimalFormat df = new DecimalFormat("#.#####");
        df.setRoundingMode(RoundingMode.HALF_UP);

        energy = (energy_0 + energy_45 + energy_90 + energy_135)/4;
        contrast = (contrast_0 + contrast_45 + contrast_90 + contrast_135)/4;
        correlation = (correlation_0 + correlation_45 + correlation_90 + correlation_135)/4;
        entropy = (entropy_0 + entropy_45 + entropy_90 + entropy_135)/4;
        homogeneity = (homogeneity_0 + homogeneity_45 + homogeneity_90 + homogeneity_135)/4;

        double temp = Math.pow(10,5);
        energy = Math.round(energy*temp)/temp;
        contrast = Math.round(contrast*temp)/temp;
        correlation = Math.round(correlation*temp)/temp;
        entropy = Math.round(entropy*temp)/temp;
        homogeneity = Math.round(homogeneity*temp)/temp;



//        if (0.84866 <= energy && energy <= 0.88002
//                && 22.2745 <= contrast && contrast <= 53.9689
//                && 0.00545 <= correlation && correlation <= 0.01511
//                && 0.47145 <= entropy && entropy <= 0.63202
//                && 0.94605 <= homogeneity && homogeneity <= 0.96528){
//            tvType.setText(R.string.kurma_ajwa);

        if (0.84249 <= energy && energy <= 0.8785
                && 17.28219 <= contrast && contrast <= 32.19345
                && 0.00525 <= correlation && correlation <= 0.0083
                && 0.45955 <= entropy && entropy <= 0.59319
                && 0.95559 <= homogeneity && homogeneity <= 0.97126){
            tvType.setText(R.string.kurma_ajwa);

        } else if (0.76988 <= energy && energy <= 0.85026
                && 41.32878 <= contrast && contrast <= 71.24144
                && 0.0009 <= correlation && correlation <= 0.0017
                && 0.59077 <= entropy && entropy <= 0.94806
                && 0.92887 <= homogeneity && homogeneity <= 0.96504){
            tvType.setText(R.string.kurma_sukari);

        } else if (0.77158 <= energy && energy <= 0.8675
                && 22.07009 <= contrast && contrast <= 54.6511
                && 0.00181 <= correlation && correlation <= 0.00572
                && 0.52781 <= entropy && entropy <= 0.84949
                && 0.94404 <= homogeneity && homogeneity <= 0.97311){
            tvType.setText(R.string.kurma_deglet);

        } else {
            tvType.setText(R.string.non_kurma);
        }


//
//        if (0.84908 <= energy_0 && energy_0 <= 0.88026
////                && 0.84781 <= energy_45 && energy_45 <= 0.87868
////                && 0.85052 <= energy_90 && energy_90 <= 0.88253
////                && 0.84722 <= energy_135 && energy_135 <= 0.87861
////                && 20.00154 <= contrast_0 && contrast_0 <= 46.34079
////                && 26.39752 <= contrast_45 && contrast_45 <= 66.81616
////                && 15.29707 <= contrast_90 && contrast_90 <= 39.69864
////                && 25.1904 <= contrast_135 && contrast_135 <= 63.0199
//                && 0.00562 <= correlation_0 && correlation_0 <= 0.01545
////                && 0.00516 <= correlation_45 && correlation_45 <= 0.01433
////                && 0.00577 <= correlation_90 && correlation_90 <= 0.01616
////                && 0.00524 <= correlation_135 && correlation_135 <= 0.01456
//                && 0.46667 <= entropy_0 && entropy_0 <= 0.62153
////                && 0.48125 <= entropy_45 && entropy_45 <= 0.64636
////                && 0.45317 <= entropy_90 && entropy_90 <= 0.61733
////                && 0.481 <= entropy_135 && entropy_135 <= 0.64286
//                && 0.94874 <= homogeneity_0 && homogeneity_0 <= 0.9675
////                && 0.94259 <= homogeneity_45 && homogeneity_45 <= 0.96287
////                && 0.94947 <= homogeneity_90 && homogeneity_90 <= 0.96888
////                && 0.94339 <= homogeneity_135 && homogeneity_135 <= 0.96311
//        ){
//            tvType.setText(R.string.kurma_ajwa);
//
//        } else if (0.77164 <= energy_0 && energy_0 <= 0.86761
////                && 0.77084 <= energy_45 && energy_45 <= 0.86692
////                && 0.77305 <= energy_90 && energy_90 <= 0.8685
////                && 0.77077 <= energy_135 && energy_135 <= 0.86695
////                && 22.65874 <= contrast_0 && contrast_0 <= 51.44436
////                && 25.21647 <= contrast_45 && contrast_45 <= 64.53497
////                && 14.53984 <= contrast_90 && contrast_90 <= 35.57596
////                && 25.86529 <= contrast_135 && contrast_135 <= 67.72767
//                && 0.00182 <= correlation_0 && correlation_0 <= 0.00574
////                && 0.00179 <= correlation_45 && correlation_45 <= 0.00549
////                && 0.00185 <= correlation_90 && correlation_90 <= 0.00617
////                && 0.00179 <= correlation_135 && correlation_135 <= 0.00546
//                && 0.52925 <= entropy_0 && entropy_0 <= 0.85315
////                && 0.53727 <= entropy_45 && entropy_45 <= 0.86619
////                && 0.49875 <= entropy_90 && entropy_90 <= 0.81439
////                && 0.53742 <= entropy_135 && entropy_135 <= 0.86422
//                && 0.94254 <= homogeneity_0 && homogeneity_0 <= 0.97265
////                && 0.93991 <= homogeneity_45 && homogeneity_45 <= 0.96973
////                && 0.95512 <= homogeneity_90 && homogeneity_90 <= 0.98012
////                && 0.93859 <= homogeneity_135 && homogeneity_135 <= 0.96995
//        ){
//            tvType.setText(R.string.kurma_deglet);
//
//        } else if (0.77039 <= energy_0 && energy_0 <= 0.85065
////                && 0.76935 <= energy_45 && energy_45 <= 0.84978
////                && 0.77063 <= energy_90 && energy_90 <= 0.85097
////                && 0.76914 <= energy_135 && energy_135 <= 0.84965
////                && 34.70511 <= contrast_0 && contrast_0 <= 57.1147
////                && 48.57973 <= contrast_45 && contrast_45 <= 86.09579
////                && 30.88417 <= contrast_90 && contrast_90 <= 59.66767
////                && 49.38287 <= contrast_135 && contrast_135 <= 82.0876
//                && 0.0009 <= correlation_0 && correlation_0 <= 0.00171
////                && 0.00089 <= correlation_45 && correlation_45 <= 0.00168
////                && 0.0009 <= correlation_90 && correlation_90 <= 0.00172
////                && 0.00089 <= correlation_135 && correlation_135 <= 0.00168
//                && 0.58335 <= entropy_0 && entropy_0 <= 0.93565
////                && 0.60095 <= entropy_45 && entropy_45 <= 0.96404
////                && 0.57423 <= entropy_90 && entropy_90 <= 0.93401
////                && 0.59902 <= entropy_135 && entropy_135 <= 0.95854
//                && 0.93189 <= homogeneity_0 && homogeneity_0 <= 0.96722
////                && 0.9247 <= homogeneity_45 && homogeneity_45 <= 0.96209
////                && 0.9326 <= homogeneity_90 && homogeneity_90 <= 0.96825
////                && 0.92629 <= homogeneity_135 && homogeneity_135 <= 0.96259
//        ){
//            tvType.setText(R.string.kurma_sukari);
//
//        } else {
//            tvType.setText(R.string.non_kurma);
//        }

//        if (0.00500 < correlation_0 && correlation_0 < 0.02) {
////            tvType.setText(R.string.kurma_ajwa);
////        }  else if (0.00070 < correlation_135 && correlation_135 <0.0017) {
////            tvType.setText(R.string.kurma_sukari);
////        } else {
////            tvType.setText(R.string.kurma_deglet);
////        }
////
    }


    private void sliderResult(){
        DecimalFormat df = new DecimalFormat("#.#####");
        df.setRoundingMode(RoundingMode.HALF_UP);
        List<Result> results = new ArrayList<>();
        results.add(new Result("Distance = 1","Angle = Average", (df.format(entropy)),(df.format(energy)),(df.format(contrast)),(df.format(correlation)),(df.format(homogeneity))));
        results.add(new Result("Distance = 1", "Angle = 0°", (df.format(entropy_0)), (df.format(energy_0)), (df.format(contrast_0)), (df.format(correlation_0)), (df.format(homogeneity_0))));
        results.add(new Result("Distance = 1", "Angle = 45°", (df.format(entropy_45)), (df.format(energy_45)), (df.format(contrast_45)), (df.format(correlation_45)), (df.format(homogeneity_45))));
        results.add(new Result("Distance = 1", "Angle = 90°", (df.format(entropy_90)), (df.format(energy_90)), (df.format(contrast_90)), (df.format(correlation_90)), (df.format(homogeneity_90))));
        results.add(new Result("Distance = 1", "Angle = 135°", (df.format(entropy_135)), (df.format(energy_135)), (df.format(contrast_135)), (df.format(correlation_135)), (df.format(homogeneity_135))));


        SliderResultAdapter adapter = new SliderResultAdapter(this, results);
        vpResult.setAdapter(adapter);

    }


}
