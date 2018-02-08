package com.example.yixiangding.hw9;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.Spinner;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.facebook.share.model.ShareLinkContent;
import com.facebook.share.widget.ShareDialog;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

/**
 * It's the whole Current Tab Fragment
 */
public class Current extends Fragment {
    private Button changer;
    private Button facebookBtn;
    private Spinner spinner;
    private Charts charts;
    private CheckBox favBox;
    private FavStatus favStatus;
    private HashSet<String> failedInds;
    private boolean faved;
    private String symbol;
    private String selectedInd;

    public Current() {
        // Required empty public constructor
    }

    public static Current newInstance(String symbol) {
        Current fragment = new Current();
        fragment.failedInds = new HashSet<String>();
        fragment.symbol = symbol;
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // if we're being restored from a previous state,
        // we don't need to do anything and should return
        if (savedInstanceState != null) return;
    }

    public void setFaved(boolean faved) {
        this.faved = faved;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_current, container, false);
        changer = v.findViewById(R.id.indicator_changer);
        spinner = v.findViewById(R.id.indicator_spinner);
        favBox = v.findViewById(R.id.fav);
        SharedPreferences pref = this.getActivity().getSharedPreferences("fav", Context.MODE_PRIVATE);

        getActivity().getSupportFragmentManager().beginTransaction().add(R.id.details_list_container, ProgressBar.newInstance()).commit();
        getActivity().getSupportFragmentManager().beginTransaction().add(R.id.indicators_container, ProgressBar.newInstance()).commit();
        favStatus.setFavStatus(pref.contains(symbol));

        favBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                favStatus.setFavStatus(favBox.isChecked());
            }
        });
        selectedInd = "Price";
        Log.d("fav_symbol", symbol);
        favBox.setChecked(pref.contains(symbol));
        favBox.setEnabled(false);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (selectedInd == null) return;
                if (selectedInd.equals(spinner.getSelectedItem().toString())) {
                    changer.setEnabled(false);
                    changer.setTextColor(Color.GRAY);
                } else {
                    changer.setEnabled(true);
                    changer.setTextColor(Color.BLACK);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        changer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectedInd = spinner.getSelectedItem().toString();

                if (charts == null) return;
                charts.setInd(selectedInd);

                if (failedInds.contains(selectedInd)) {
                    // Failed to retrieve JSON
                    FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
                    transaction.replace(R.id.indicators_container, ErrorMsg.newInstance());
                    transaction.commit();
                } else if (!charts.checkJSON(selectedInd)) {
                    // JSON has not been retrieved
                    FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
                    transaction.replace(R.id.indicators_container, ProgressBar.newInstance());
                    transaction.commit();
                } else {
                    // The needed JSON has been retrieved
                    Log.d("makeChart: ", selectedInd);
                    makeChart(charts);  // if progress bar is showing, replace
                    charts.drawChart();
                }

                if (selectedInd.equals(spinner.getSelectedItem().toString())) {
                    changer.setEnabled(false);
                    changer.setTextColor(Color.GRAY);
                }
            }
        });

        facebookBtn = v.findViewById(R.id.facebook_share);
        facebookBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final ShareDialog dialog = new ShareDialog(getActivity());
                if (charts == null) return;
                final String option = charts.getWebViewInterface().getOption();
                Log.d("Option:", option);
                final String queryUrl = "https://export.highcharts.com/";
                StringRequest stringRequest = new StringRequest(Request.Method.POST, queryUrl,
                        new Response.Listener<String>() {

                            @Override
                            public void onResponse(String response) {
                                Log.d("response:", response);
                                ShareLinkContent content = new ShareLinkContent.Builder()
                                        .setContentUrl(Uri.parse(queryUrl + response))
                                        .build();
                                dialog.show(content);
                            }
                        }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d("Request Err", error.toString());
                    }
                }){
                    @Override
                    protected Map<String, String> getParams() throws AuthFailureError {
                        Map<String,String> paras = new HashMap<>();
                        paras.put("async","true");
                        paras.put("type","image/png");
                        paras.put("options",option);

                        return paras;
                    }
                };
                RequestQueue queue = Volley.newRequestQueue(getContext());
                queue.add(stringRequest);
            }
        });

        return v;
    }

    public CheckBox getFavBox() {
        return favBox;
    }

    // Show stock list after successful query
    public void showList(DetailsList list) {
        try {
            FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
            if (failedInds.contains("Price")) { // error happens
                transaction.replace(R.id.details_list_container, new ErrorMsg());
            } else {
                transaction.replace(R.id.details_list_container, list);
            }
//        transaction.addToBackStack(null);
            transaction.commit();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // show charts
    public void makeChart(Charts charts) {
        try {
            FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();

            if (failedInds.contains(charts.getIndName())) { // error happens
                transaction.replace(R.id.indicators_container, new ErrorMsg());
            } else {
                // store the charts constructed in Activity
                this.charts = charts;
                // replace with charts container
                transaction.replace(R.id.indicators_container, charts);
            }
            transaction.commit();
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
    }

    // Informing failed indicators
    public void setFailed(String name) {
        failedInds.add(name);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        try {
            favStatus = (FavStatus) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString()
                    + " must implement TextClicked");
        }
    }

    public interface FavStatus {
        public void setFavStatus(boolean status);
    }

}
