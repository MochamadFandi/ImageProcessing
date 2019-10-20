package com.example.opencvtest.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.opencvtest.R;
import com.example.opencvtest.data.DetailItem;

import java.util.List;

public class DetailItemAdapter extends RecyclerView.Adapter<DetailItemAdapter.ViewHolder> {

    private final Context context;
    private final List<DetailItem> detailItemList;

    public DetailItemAdapter(Context context, List<DetailItem> detailItemList) {
        this.context = context;
        this.detailItemList = detailItemList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(context).inflate(R.layout.detail_item, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {

        viewHolder.tvDetails.setText(detailItemList.get(i).getText());
        Glide.with(context).asBitmap().load(detailItemList.get(i).getPhoto()).into(viewHolder.ivDetails);

    }

    @Override
    public int getItemCount() {
        return detailItemList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        ImageView ivDetails;
        TextView tvDetails;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            ivDetails = itemView.findViewById(R.id.iv_details);
            tvDetails = itemView.findViewById(R.id.tv_details);
        }
    }
}
