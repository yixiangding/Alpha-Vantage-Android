package com.example.yixiangding.hw9;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class ErrorMsg extends Fragment {

    private String msg;

    public ErrorMsg() {
        // Required empty public constructor
    }

    public static ErrorMsg newInstance() {
        ErrorMsg fragment = new ErrorMsg();
        return fragment;
    }

    public void setErrorMsg(String msg) {
        this.msg = msg;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_error_msg, container, false);
        if (msg != null) {
            TextView ErrMsg = v.findViewById(R.id.error_msg);
            ErrMsg.setText(msg);
        }
        return v;
    }

}
