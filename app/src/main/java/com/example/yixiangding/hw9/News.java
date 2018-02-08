package com.example.yixiangding.hw9;

import android.app.Activity;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class News extends Fragment {

    private boolean failed;
    private FragmentActivity stockActivity;
    private NewsList newsList;

    public News() {
        // Required empty public constructor
    }


    public static News newInstance() {
        News fragment = new News();
        return fragment;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        stockActivity = (FragmentActivity) context;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_news, container, false);
        stockActivity.getSupportFragmentManager().beginTransaction().add(R.id.news_container, ProgressBar.newInstance()).commit();
        return v;
    }


    public void showNews(NewsList newsList) {
        // when Ajax call back, show news
//        Log.e("FragAc: ", stockActivity.toString());
        this.newsList = newsList;
        if (!isAdded()) return;
        FragmentTransaction transaction = stockActivity.getSupportFragmentManager().beginTransaction();
        if (failed) { // error happens
            ErrorMsg error = new ErrorMsg();
            error.setErrorMsg("Failed to load news data.");
            transaction.replace(R.id.news_container, error);
        } else {
            // replace with charts container
            transaction.replace(R.id.news_container, newsList);
        }
        transaction.commit();
    }

    public void setFailed(boolean status) {
        failed = status;
    }
}
