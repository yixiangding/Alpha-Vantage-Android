package com.example.yixiangding.hw9;

import android.content.Context;
import android.webkit.JavascriptInterface;

public class WebViewInterface {
    Context mContext;
    String option;

    /** Instantiate the interface and set the context */
    WebViewInterface(Context c) {
        mContext = c;
        option = "empty..";
    }

    /** Show a toast from the web page */
    @JavascriptInterface
    public void setOption(String option) {
        this.option = option;
    }

    public String getOption() {
        return option;
    }
}
