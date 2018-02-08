package com.example.yixiangding.hw9;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.design.widget.TabLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.util.Log;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * This Activity is the second activity called by MainActivity to show quote result
 */
public class StockActivity extends AppCompatActivity implements Current.FavStatus {

    private SectionsPagerAdapter mSectionsPagerAdapter;
    private Gson gson;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    private ViewPager mViewPager;

    // Fragments
    private Current currentFrag;
    private Historical historicalFrag;
    private News newsFrag;
    private DetailsList detailsList;
    private Charts charts;
    private Charts historicalChart;
    private NewsList newsList;
    private boolean favStatus;
    private Bundle extras;

    // Current symbol
    private String symbol;

    // SharedPreference "fav"
    SharedPreferences pref;

    @Override
    public void onBackPressed() {
        if (favStatus) {
            if (detailsList.getFavData() == null) return;
            FavData favData = detailsList.getFavData();
            pref.edit().putString(symbol, gson.toJson(favData)).apply();
            Log.d("favChange:", "add");
        } else if (pref.contains(symbol)) {
            // remove
            pref.edit().remove(symbol).apply();
            Log.d("favChange", "remove");
        }
        super.onBackPressed();  // Must be called after setResult or it will set resultCode with 0 by default
        finish();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stock);
        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());
        // init fragments
        extras = getIntent().getExtras();
        if (extras != null) {
            symbol = extras.getString("symbol");  // retrieve symbol from MainActivity
        }
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(symbol);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        pref = this.getSharedPreferences("fav", Context.MODE_PRIVATE);
        currentFrag = Current.newInstance(symbol);
        historicalFrag = Historical.newInstance();
        newsFrag = News.newInstance();
        detailsList = DetailsList.newInstance();
        charts = Charts.newInstance();
        historicalChart = Charts.newInstance("file:///android_asset/historical.html");
        newsList = NewsList.newInstance();
        gson = new Gson();
//        currentFrag.setFaved(pref.contains(symbol));
        // Add available fragments to the adapter to use
        mSectionsPagerAdapter.addFrag(currentFrag);
        mSectionsPagerAdapter.addFrag(historicalFrag);
        mSectionsPagerAdapter.addFrag(newsFrag);

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.tabs_container);
        mViewPager.setAdapter(mSectionsPagerAdapter);
        mViewPager.setOffscreenPageLimit(3);


        TabLayout tabLayout = findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);

        makeQueries();

    }

    @Override
    public void setFavStatus(boolean status) {
        favStatus = status;
        Log.d("favStauts", favStatus+"");
    }

    private void makeQueries() {
        // init gloval vars
        charts.setInd("Price");
        historicalChart.setInd("Historical");
        RequestQueue queue = Volley.newRequestQueue(this);

        // URL for query price & concatenate other URLs
        String queryURL = "http://yixiangd.us-east-2.elasticbeanstalk.com/query?symbol=" + symbol;

        // query price
        JsonObjectRequest priceRequest = new JsonObjectRequest
                (Request.Method.GET, queryURL, null, new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        if (response.has("Error Message")) {
                            currentFrag.setFailed("Price");
                            historicalFrag.setFailed(true);
                        }

                        detailsList.construct(response);
                        currentFrag.showList(detailsList);
                        charts.setIndicatorJSON("Price", response);
                        if (charts.getIndName().equals("Price")) currentFrag.makeChart(charts);
                        historicalChart.setIndicatorJSON("Price", response);
                        historicalFrag.makeChart(historicalChart);
                        currentFrag.getFavBox().setEnabled(true);

                    }

                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("err", "Error->[Price]");
                        currentFrag.setFailed("Price");
                        currentFrag.showList(detailsList);
                        if (charts.getIndName().equals("Price")) currentFrag.makeChart(charts);
                        historicalFrag.setFailed(true);
                        historicalFrag.makeChart(historicalChart);
                    }
                });
        priceRequest.setRetryPolicy(new DefaultRetryPolicy(
                5000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
        ));
        queue.add(priceRequest);

        // query indicators
        // SMA
        JsonObjectRequest SMARequest = new JsonObjectRequest
                (Request.Method.GET, queryURL + "&func=SMA", null, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        if (response.has("Error Message")) {
                            currentFrag.setFailed("Price");
                        }
                        charts.setIndicatorJSON("SMA", response);
                        if (charts.getIndName().equals("SMA")) currentFrag.makeChart(charts);
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("err", "Error->[SMA]");
                        currentFrag.setFailed("SMA");
                        if (charts.getIndName().equals("SMA")) currentFrag.makeChart(charts);
                    }
                });
        SMARequest.setRetryPolicy(new DefaultRetryPolicy(
                5000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
        ));
        queue.add(SMARequest);

        // EMA
        JsonObjectRequest EMARequest = new JsonObjectRequest
                (Request.Method.GET, queryURL + "&func=EMA", null, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        if (response.has("Error Message")) {
                            currentFrag.setFailed("Price");
                        }
                        charts.setIndicatorJSON("EMA", response);
                        if (charts.getIndName().equals("EMA")) currentFrag.makeChart(charts);
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("err", "Error->[EMA]");
                        currentFrag.setFailed("EMA");
                        if (charts.getIndName().equals("EMA")) currentFrag.makeChart(charts);
                    }
                });
        EMARequest.setRetryPolicy(new DefaultRetryPolicy(
                5000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
        ));
        queue.add(EMARequest);

        // RSI
        JsonObjectRequest RSIRequest = new JsonObjectRequest
                (Request.Method.GET, queryURL + "&func=RSI", null, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        if (response.has("Error Message")) {
                            currentFrag.setFailed("Price");
                        }
                        charts.setIndicatorJSON("RSI", response);
                        if (charts.getIndName().equals("RSI")) currentFrag.makeChart(charts);
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("err", "Error->[RSI]");
                        currentFrag.setFailed("RSI");
                        if (charts.getIndName().equals("RSI")) currentFrag.makeChart(charts);
                    }
                });
        RSIRequest.setRetryPolicy(new DefaultRetryPolicy(
                5000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
        ));
        queue.add(RSIRequest);

        // ADX
        JsonObjectRequest ADXRequest = new JsonObjectRequest
                (Request.Method.GET, queryURL + "&func=ADX", null, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        if (response.has("Error Message")) {
                            currentFrag.setFailed("Price");
                        }
                        charts.setIndicatorJSON("ADX", response);
                        if (charts.getIndName().equals("ADX")) currentFrag.makeChart(charts);
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("err", "Error->[ADX]");
                        currentFrag.setFailed("ADX");
                        if (charts.getIndName().equals("ADX")) currentFrag.makeChart(charts);
                    }
                });
        ADXRequest.setRetryPolicy(new DefaultRetryPolicy(
                5000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
        ));
        queue.add(ADXRequest);

        // STOCH
        JsonObjectRequest STOCHRequest = new JsonObjectRequest
                (Request.Method.GET, queryURL + "&func=STOCH", null, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        if (response.has("Error Message")) {
                            currentFrag.setFailed("Price");
                        }
                        charts.setIndicatorJSON("STOCH", response);
                        if (charts.getIndName().equals("STOCH")) currentFrag.makeChart(charts);
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("err", "Error->[STOCH]");
                        currentFrag.setFailed("STOCH");
                        if (charts.getIndName().equals("STOCH")) currentFrag.makeChart(charts);
                    }
                });
        STOCHRequest.setRetryPolicy(new DefaultRetryPolicy(
                5000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
        ));
        queue.add(STOCHRequest);

        // BBANDS
        JsonObjectRequest BBANDSRequest = new JsonObjectRequest
                (Request.Method.GET, queryURL + "&func=BBANDS", null, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        if (response.has("Error Message")) {
                            currentFrag.setFailed("Price");
                        }
                        charts.setIndicatorJSON("BBANDS", response);
                        if (charts.getIndName().equals("BBANDS")) currentFrag.makeChart(charts);
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("err", "Error->[BBANDS]");
                        currentFrag.setFailed("BBANDS");
                        if (charts.getIndName().equals("BBANDS")) currentFrag.makeChart(charts);
                    }
                });
        BBANDSRequest.setRetryPolicy(new DefaultRetryPolicy(
                5000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
        ));
        queue.add(BBANDSRequest);

        // MACD
        JsonObjectRequest MACDRequest = new JsonObjectRequest
                (Request.Method.GET, queryURL + "&func=MACD", null, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        if (response.has("Error Message")) {
                            currentFrag.setFailed("Price");
                        }
                        charts.setIndicatorJSON("MACD", response);
                        if (charts.getIndName().equals("MACD")) currentFrag.makeChart(charts);
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("err", "Error->[MACD]");
                        currentFrag.setFailed("MACD");
                        if (charts.getIndName().equals("MACD")) currentFrag.makeChart(charts);
                    }
                });
        MACDRequest.setRetryPolicy(new DefaultRetryPolicy(
                5000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
        ));
        queue.add(MACDRequest);

        // News
        JsonArrayRequest newsRequest = new JsonArrayRequest
                (Request.Method.GET, queryURL + "&func=news", null, new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        newsList.construct(response);
                        newsFrag.showNews(newsList);
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("err", error.getMessage() + ": news err");
                        newsFrag.setFailed(true);
                        newsFrag.showNews(newsList);
                    }
                });
        newsRequest.setRetryPolicy(new DefaultRetryPolicy(
                5000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
        ));
        queue.add(newsRequest);
    }


    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {
        private List<Fragment> frags = new ArrayList<>();

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            return frags.get(position);
        }

        @Override
        public int getCount() {
            // Show 3 total pages.
            return 3;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return "CURRENT";
                case 1:
                    return "HISTORICAL";
                case 2:
                    return "NEWS";
                default:
                    return null;
            }
        }

        public void addFrag(Fragment frag) {
            frags.add(frag);
        }

    }

}
