package com.example.opencvtest.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.example.opencvtest.R;
import com.example.opencvtest.data.Dates;

import java.util.List;

public class DateAdapter extends RecyclerView.Adapter<DateAdapter.ViewHolder> {

    private final Context context;
    private final List<Dates> datesList;

    public DateAdapter(Context context, List<Dates> datesList) {
        this.context = context;
        this.datesList = datesList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(context).inflate(R.layout.dates_item, viewGroup, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        Glide.with(context).load(datesList.get(i).getImage()).into(viewHolder.ivDates);
    }

    @Override
    public int getItemCount() {
        return datesList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        ImageView ivDates;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            ivDates = itemView.findViewById(R.id.iv_dates);
        }
    }
}