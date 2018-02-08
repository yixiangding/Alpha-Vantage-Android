package com.example.yixiangding.hw9;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import org.json.JSONObject;

/**
 * It's the fragment at lower CurrentFragment consisting of a Webview to show charts
 */
public class Charts extends Fragment {
    private WebView chartsView;
    private String indName;
    private String url;
    WebViewInterface webViewInterface;

    private JSONObject priceJSON;
    private JSONObject SMAJSON;
    private JSONObject EMAJSON;
    private JSONObject STOCHJSON;
    private JSONObject RSIJSON;
    private JSONObject ADXJSON;
    private JSONObject CCIJSON;
    private JSONObject BBANDSJSON;
    private JSONObject MACDJSON;
    private JSONObject historicalJSON;

    public Charts() {
        // Required empty public constructor
    }

    public static Charts newInstance() {
        Charts fragment = new Charts();
        fragment.url = "file:///android_asset/charts.html";
        return fragment;
    }

    public static  Charts newInstance(String url) {
        Charts fragment = new Charts();
        fragment.url = url;
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_charts, container, false);

        chartsView = v.findViewById(R.id.charts_view);
        webViewInterface = new WebViewInterface(getContext());
        chartsView.addJavascriptInterface(webViewInterface, "Android");
        WebSettings webSettings = chartsView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setJavaScriptCanOpenWindowsAutomatically(true);
        webSettings.setAllowFileAccessFromFileURLs(true);
        webSettings.setAllowUniversalAccessFromFileURLs(true);
        chartsView.loadUrl(url);
        chartsView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                // draw charts
                drawChart();
            }

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                if (url.contains("http")) {
                    view.getContext().startActivity(
                            new Intent(Intent.ACTION_VIEW, Uri.parse(url)));
                    return true;
                }
                return false;
            }
        });

        return v;
    }

    public String getIndName() {
        return indName;
    }

    public WebViewInterface getWebViewInterface() {
        return webViewInterface;
    }

    /**
     * Set current showing indicator
     * @param name Current showing indicator
     */
    public void setInd(String name) {
        indName = name;
    }

    public void setIndicatorJSON(String name, JSONObject json) {
        if (name.equals("Price")) {
            priceJSON = json;
            historicalJSON = priceJSON;
        }
        else if (name.equals("SMA")) SMAJSON = json;
        else if (name.equals("EMA")) EMAJSON = json;
        else if (name.equals("STOCH")) STOCHJSON = json;
        else if (name.equals("RSI")) RSIJSON = json;
        else if (name.equals("ADX")) ADXJSON = json;
        else if (name.equals("CCI")) CCIJSON = json;
        else if (name.equals("BBANDS")) BBANDSJSON = json;
        else if (name.equals("MACD")) MACDJSON = json;
    }
    public boolean checkJSON(String name) {
        if (name.equals("Price")) return priceJSON != null;
        else if (name.equals("SMA")) return SMAJSON != null;
        else if (name.equals("EMA")) return EMAJSON != null;
        else if (name.equals("STOCH")) return STOCHJSON != null;
        else if (name.equals("RSI")) return RSIJSON != null;
        else if (name.equals("ADX")) return ADXJSON != null;
        else if (name.equals("CCI")) return CCIJSON != null;
        else if (name.equals("BBANDS")) return BBANDSJSON != null;
        else if (name.equals("MACD")) return MACDJSON != null;
        else return false;
    }

    // draw chart with given indicator name
    public void drawChart() {
        if (indName.equals("Price")) chartsView.loadUrl("javascript:addPriceData('" + priceJSON + "');");
        else if (indName.equals("SMA")) chartsView.loadUrl("javascript:makeSingle('" + SMAJSON + "', 'SMA');");
        else if (indName.equals("EMA")) chartsView.loadUrl("javascript:makeSingle('" + EMAJSON + "', 'EMA');");
        else if (indName.equals("RSI")) chartsView.loadUrl("javascript:makeSingle('" + RSIJSON + "', 'RSI');");
        else if (indName.equals("ADX")) chartsView.loadUrl("javascript:makeSingle('" + ADXJSON + "', 'ADX');");
        else if (indName.equals("STOCH")) chartsView.loadUrl("javascript:makeDouble('" + STOCHJSON + "', 'STOCH');");
        else if (indName.equals("BBANDS")) chartsView.loadUrl("javascript:makeTreble('" + BBANDSJSON + "', 'BBANDS');");
        else if (indName.equals("MACD")) chartsView.loadUrl("javascript:makeTreble('" + MACDJSON + "', 'MACD');");
        else if (indName.equals("Historical")) chartsView.loadUrl("javascript:makeHistorical('" + historicalJSON + "');");
        else Log.e("illegal ind", "illegal indName!!!");
    }

}
