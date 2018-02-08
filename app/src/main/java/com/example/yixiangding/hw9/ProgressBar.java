package com.example.yixiangding.hw9;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class ProgressBar extends Fragment {

    public ProgressBar() {
        // Required empty public constructor
    }


    public static ProgressBar newInstance() {
        ProgressBar fragment = new ProgressBar();
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
        return inflater.inflate(R.layout.fragment_progress_bar, container, false);
    }



    @Override
    public void onDetach() {
        super.onDetach();
    }
}
