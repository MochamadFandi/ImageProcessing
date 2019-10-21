package com.example.opencvtest.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.opencvtest.R;
import com.example.opencvtest.activity.InformationActivity;
import com.example.opencvtest.data.Slide;

import java.util.List;

public class SliderPagerAdapter extends PagerAdapter {

    private Context context;
    private List<Slide> mList;

    public SliderPagerAdapter(Context context, List<Slide> mList) {
        this.context = context;
        this.mList = mList;
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, final int position) {
        View slideLayout = LayoutInflater.from(context).inflate(R.layout.slider_item, container, false);
        ImageView slideImage = slideLayout.findViewById(R.id.iv_date);
        TextView slideText = slideLayout.findViewById(R.id.tv_date);
        Glide.with(context).load(mList.get(position).getImage()).into(slideImage);
        slideText.setText(mList.get(position).getTitle());

        slideImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent move = new Intent(context, InformationActivity.class);
                move.putExtra("TAB ID", mList.get(position).getTab());
                context.startActivity(move);
            }
        });

        container.addView(slideLayout);
        return slideLayout;

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
        container.removeView((View)object);
    }
}
