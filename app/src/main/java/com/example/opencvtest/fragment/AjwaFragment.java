package com.example.opencvtest.fragment;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.opencvtest.R;
import com.example.opencvtest.adapter.DateAdapter;
import com.example.opencvtest.data.Dates;

import java.util.ArrayList;
import java.util.List;


public class AjwaFragment extends Fragment {

    private List<Dates> dates = new ArrayList<>();


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_ajwa, container, false);
        RecyclerView rvDates = view.findViewById(R.id.rv_dates);
        DateAdapter dateAdapter = new DateAdapter(getContext(),dates);
        rvDates.setLayoutManager(new LinearLayoutManager(getActivity(),LinearLayoutManager.HORIZONTAL,false));
        rvDates.setAdapter(dateAdapter);

        return view;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        dates.add(new Dates(R.drawable.kurma_ajwa));
        dates.add(new Dates(R.drawable.kurma_ajwa1));
        dates.add(new Dates(R.drawable.kurma_ajwa2));
        dates.add(new Dates(R.drawable.kurma_ajwa3));
        dates.add(new Dates(R.drawable.kurma_ajwa4));


    }
}
