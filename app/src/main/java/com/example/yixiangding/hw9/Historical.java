package com.example.yixiangding.hw9;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class Historical extends Fragment {
    private boolean failed;
    private Charts historicalChart;

    public Historical() {
        // Required empty public constructor
    }

    public static Historical newInstance() {
        Historical fragment = new Historical();
        fragment.failed = false;
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_historical, container, false);

        getActivity().getSupportFragmentManager().beginTransaction().add(R.id.historical_container, ProgressBar.newInstance()).commit();

        return v;
    }

    // When selected, triggered with true param
//    @Override
//    public void setUserVisibleHint(boolean isVisibleToUser) {
//        super.setUserVisibleHint(isVisibleToUser);
//
//        if (isVisibleToUser && !failed) {
//            if (historicalChart != null) historicalChart.drawChart();
//        }
//    }

    public void makeChart(Charts historicalChart) {
        try {
            FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();

            if (failed) { // error happens
                transaction.replace(R.id.historical_container, new ErrorMsg());
            } else {
                // store the historicalChart constructed in Activity
                this.historicalChart = historicalChart;
                // replace with historicalChart container
                transaction.replace(R.id.historical_container, historicalChart);
            }
            transaction.commit();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setFailed(boolean status) {
        failed = status;
    }
}
