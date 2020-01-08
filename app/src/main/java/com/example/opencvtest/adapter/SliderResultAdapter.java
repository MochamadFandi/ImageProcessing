package com.example.opencvtest.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.opencvtest.R;
import com.example.opencvtest.data.Result;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.List;

public class SliderResultAdapter extends PagerAdapter {
    private Context context;
    private List<Result> mList;

    public SliderResultAdapter(Context context, List<Result> mList) {
        this.context = context;
        this.mList = mList;
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, final int position) {
        DecimalFormat df = new DecimalFormat("#.#####");
        df.setRoundingMode(RoundingMode.HALF_UP);

        View view = LayoutInflater.from(context).inflate(R.layout.result_item, container, false);
        TextView tvDistance = view.findViewById(R.id.tv_distance);
        TextView tvAngle = view.findViewById(R.id.tv_angle);
        TextView tvEnergy = view.findViewById(R.id.tv_energy);
        TextView tvEntropy = view.findViewById(R.id.tv_entropy);
        TextView tvContrast = view.findViewById(R.id.tv_contrast);
        TextView tvCorrelation = view.findViewById(R.id.tv_correlation);
        TextView tvHomogeneity = view.findViewById(R.id.tv_homogeneity);
        tvDistance.setText(mList.get(position).getDistance());
        tvAngle.setText(mList.get(position).getAngle());
        tvEnergy.setText(mList.get(position).getEnergy());
        tvEntropy.setText(mList.get(position).getEntropy());
        tvContrast.setText(mList.get(position).getContrast());
        tvCorrelation.setText(mList.get(position).getCorrelation());
        tvHomogeneity.setText(mList.get(position).getHomogeneity());


        container.addView(view);


        return view;
    }

    @Override
    public int getCount() {
        return mList.size();
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object o) {
        return view == o;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((View) object);
    }
}
