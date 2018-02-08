package com.example.yixiangding.hw9;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;

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
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity {

    // global vars
    private RequestQueue queue;
    private AutoCompleteTextView inputView;
    // SharedPreference "fav"
    private SharedPreferences pref;
    private Gson gson;
    private ArrayList<FavData> favData;  // Storing symbols
    private ListComparators comparators;

    private ListView favListView;
    private Spinner sortBySpinner;
    private Spinner orderSpinner;
    private Switch autoRefreshSwitch;
    private android.widget.ProgressBar refreshProgressBar;
    private Button refreshBtn;
    private Timer timer;
    private int countDown;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        try {
            getSupportActionBar().hide();
        } catch (NullPointerException e) {
            e.printStackTrace();
        }

        // init global vars
        pref = this.getSharedPreferences("fav", Context.MODE_PRIVATE);
        queue = Volley.newRequestQueue(this);
        gson = new Gson();
        favListView = findViewById(R.id.fav_list);
        refreshBtn = findViewById(R.id.refresh_btn);
        refreshProgressBar = findViewById(R.id.refresh_progress_bar);
        refreshProgressBar.setVisibility(View.GONE);
        if (pref.getAll().keySet().size() != 0) refresh(favListView);

        // initialize favorite list click listener
        favListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, final int position, long l) {
                PopupMenu popupMenu = new PopupMenu(getApplicationContext(), view);
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem menuItem) {
                        int id = menuItem.getItemId();
                        switch (id) {
                            case R.id.option_yes:
                                Toast.makeText(getApplicationContext(), "Selected Yes", Toast.LENGTH_SHORT).show();
                                pref.edit().remove(favData.get(position).getSymbol()).apply();
                                constructFavList();
                                break;
                            case R.id.option_no:
                                Toast.makeText(getApplicationContext(), "Selected No", Toast.LENGTH_SHORT).show();
                                break;
                        }
                        return true;
                    }
                });
                popupMenu.inflate(R.menu.menu_fav_list);
                popupMenu.show();
                return true;
            }
        });

        // spinners setting-ups
        sortBySpinner = findViewById(R.id.sort_by);
        orderSpinner = findViewById(R.id.order);
        String[] sortByItems = new String[] {
                "Sort by", "Default", "Symbol", "Price", "Change", "Change Percent"
        };
        String[] orderItems = new String[] {"Order", "Ascending", "Descending"};
        sortBySpinner.setAdapter(new ArrayAdapter<String>(this, R.layout.support_simple_spinner_dropdown_item, new ArrayList<>(Arrays.asList(sortByItems))) {
            @Override
            public boolean isEnabled(int position) {
                if (position == 0) return false;
                return true;
            }

            @Override
            public View getDropDownView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
                View v = super.getDropDownView(position, convertView, parent);
                if (position == 0) {
                    ((TextView) v).setTextColor(Color.GRAY);
                }
                return v;
            }
        });
        orderSpinner.setAdapter(new ArrayAdapter<String>(this, R.layout.support_simple_spinner_dropdown_item, new ArrayList<>(Arrays.asList(orderItems))) {
            @Override
            public boolean isEnabled(int position) {
                if (position == 0) return false;
                return true;
            }

            @Override
            public View getDropDownView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
                View v = super.getDropDownView(position, convertView, parent);
                if (position == 0) {
                    ((TextView) v).setTextColor(Color.GRAY);
                }
                return v;
            }
        });

        // Auto-Refresh Switch setting-up
        autoRefreshSwitch = findViewById(R.id.autorefresh_switch);
        autoRefreshSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    timer = new Timer();
                    timer.scheduleAtFixedRate(new TimerTask() {
                        @Override
                        public void run() {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    refresh(refreshBtn);
                                }
                            });
                        }
                    }, 0, 5000);
                } else {
                    timer.cancel();
                }
            }
        });

        // construct initial favList comparators
        comparators = new ListComparators();
        constructFavList();

        // auto-completion setting-ups
        inputView = (AutoCompleteTextView)
                findViewById(R.id.input);
        final TextWatcher watcher = new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                String inputSymbol = inputView.getText().toString();
                if (inputSymbol.contains("-")) return;
//                String url = "http://dev.markitondemand.com/MODApis/Api/v2/Lookup/json?input=";
                String url = "http://yixiangd.us-east-2.elasticbeanstalk.com/query?func=ac&symbol=";
                JsonArrayRequest request = new JsonArrayRequest
                        (Request.Method.GET, url + inputSymbol, null, new Response.Listener<JSONArray>() {

                            @Override
                            public void onResponse(JSONArray response) {
                                try {
                                    String[] results = new String[Math.min(5, response.length())];
                                    for (int i = 0; i < results.length; i++) {
                                        JSONObject json = response.getJSONObject(i);
                                        results[i] = json.getString("Symbol") + " - " +
                                                json.getString("Name") + " (" + json.getString("Exchange") + ")";
                                    }
                                    ArrayAdapter<String> adapter = new ArrayAdapter<>(MainActivity.this,
                                            android.R.layout.simple_dropdown_item_1line, results);
                                    inputView.setAdapter(adapter);
                                    inputView.showDropDown();
                                    Log.w("DEBUG", response.toString());
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        }, new Response.ErrorListener() {

                            @Override
                            public void onErrorResponse(VolleyError error) {
                                Log.e("err", "REQUEST ERROR!");

                            }
                        });
                queue.add(request);
            }

            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        };
        inputView.addTextChangedListener(watcher);

        AdapterView.OnItemSelectedListener spinnerListener = new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                constructFavList();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        };
        orderSpinner.setOnItemSelectedListener(spinnerListener);
        sortBySpinner.setOnItemSelectedListener(spinnerListener);
    }


    private void constructFavList() {
        favData = new ArrayList<>();
        for (String key : pref.getAll().keySet()) {
            FavData dataItem = gson.fromJson(pref.getString(key, ""), FavData.class);
            if (dataItem != null) favData.add(dataItem);
            Log.d("fav:", key+": "+pref.getString(key, ""));
            Log.d("favSize:", pref.getAll().keySet().size() + "");
        }
        String sortBy = sortBySpinner.getSelectedItem().toString();
        String order = orderSpinner.getSelectedItem().toString();
        Comparator<FavData> theComparator = comparators.getDefaultAscending();
        if (order.equals("Ascending")) {
            if (sortBy.equals("Symbol")) {
                theComparator = comparators.getSymbolAscending();
            } else if (sortBy.equals("Price")) {
                theComparator = comparators.getPriceAscending();
            } else if (sortBy.equals("Change")) {
                theComparator = comparators.getChangeAscending();
            } else if (sortBy.equals("Change Percent")) {
                theComparator = comparators.getPercentAscending();
            } else if (sortBy.equals("Default")) {
                theComparator = comparators.getDefaultAscending();
            }
        } else if (order.equals("Descending")) {
            if (sortBy.equals("Symbol")) {
                theComparator = comparators.getSymbolDescending();
            } else if (sortBy.equals("Price")) {
                theComparator = comparators.getPriceDescending();
            } else if (sortBy.equals("Change")) {
                theComparator = comparators.getChangeDescending();
            } else if (sortBy.equals("Change Percent")) {
                theComparator = comparators.getPercenDescending();
            } else if (sortBy.equals("Default")) {
                theComparator = comparators.getDefaultDescending();
            }
        }
        Collections.sort(favData, theComparator);
        FavListAdapter adapter = new FavListAdapter(this, favData);
        favListView.setAdapter(adapter);
    }

    // Triggered when try to get quote
    public void getQuote(View view) {
        String fullInput = inputView.getText().toString();
        if (fullInput.trim().equals("")) {
            Toast.makeText(this, "Please enter a stock name or symbol", Toast.LENGTH_SHORT).show();
            return;
        }
        String symbol = fullInput;
        if (symbol.contains(" - ")) {
            symbol = fullInput.substring(0, fullInput.indexOf(" - "));
        }
        Intent getQuoteIntent = new Intent(MainActivity.this, StockActivity.class);
        getQuoteIntent.putExtra("symbol", symbol);  // passing symbol variable to next activity
//        MainActivity.this.startActivity(getQuoteIntent);
        MainActivity.this.startActivityForResult(getQuoteIntent, 1);
    }

    // Triggered by clear button
    public void clear(View view) {
        inputView.setText("");
    }
    // Triggered when refresh btn is clicked
    public void refresh(View view) {
        refreshProgressBar.setVisibility(View.VISIBLE);
        countDown = pref.getAll().keySet().size();
        for (String key : pref.getAll().keySet()) {
            final FavData favItem = gson.fromJson(pref.getString(key, ""), FavData.class);
            if (favItem == null) continue;
            String queryURL = "http://yixiangd.us-east-2.elasticbeanstalk.com/query?symbol=" + favItem.getSymbol();
            JsonObjectRequest priceRequest = new JsonObjectRequest
                    (Request.Method.GET, queryURL, null, new Response.Listener<JSONObject>() {

                        @Override
                        public void onResponse(JSONObject response) {
                            countDown--;
                            if (countDown == 0) refreshProgressBar.setVisibility(View.GONE);
                            if (response == null) return;

                            try {

                                JSONObject series = response.getJSONObject("Time Series (Daily)");
                                JSONObject lastData = series.getJSONObject(series.names().getString(0));
                                JSONObject secondData = series.getJSONObject(series.names().getString(1));
                                String newPrice = String.valueOf(
                                        Formatter.formatter(Double.valueOf(lastData.getString("4. close")), 2));
                                String newChange = String.valueOf(
                                        Formatter.formatter(
                                                Double.valueOf(lastData.getString("4. close")) -
                                                        Double.valueOf(secondData.getString("4. close")),
                                                2));
                                String newChangePercent = String.valueOf(Formatter.formatter(
                                        (Double.valueOf(lastData.getString("4. close")) -
                                                Double.valueOf(secondData.getString("4. close"))) /
                                                Double.valueOf(secondData.getString("4. close")) * 100,
                                        2));
                                favItem.setPrice(newPrice);
                                favItem.setChange(newChange);
                                favItem.setChangePercentage(newChangePercent);
                                // update data in SharedPref
                                pref.edit().putString(favItem.getSymbol(), gson.toJson(favItem)).apply();

                                constructFavList();

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                        }

                    }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Log.e("err", "Error->[refresh]->" + favItem.getSymbol());
                            Toast.makeText(getApplicationContext(), "Failed to refresh " + favItem.getSymbol() + " data!", Toast.LENGTH_SHORT).show();
                            countDown--;
                            if (countDown == 0) refreshProgressBar.setVisibility(View.GONE);
                        }
                    });
            priceRequest.setRetryPolicy(new DefaultRetryPolicy(
                    5000,
                    DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
            ));
            queue.add(priceRequest);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d("requestCode", requestCode+"");
        if (requestCode == 1) {
            Log.d("resultCode", resultCode +"");
//            if (resultCode == RESULT_OK) {
//                Log.d("testPassing", data.getStringExtra("MyData"));
//            }
            constructFavList();
            if (pref.getAll().keySet().size() != 0) refresh(favListView);
        }
    }


    /**
     Adapter Class to connect data (ArrayList) and Layout
     */
    public class FavListAdapter extends BaseAdapter {
        private Context context;
        private ArrayList<FavData> data;
        private LayoutInflater inflater;

        public FavListAdapter(Context context, ArrayList<FavData> data) {
            this.context = context;
            this.data = data;
            inflater = (LayoutInflater) this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        @Override
        public int getCount() {
            return data.size();
        }

        @Override
        public Object getItem(int i) {
            return data.get(i);
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            View rowView = inflater.inflate(R.layout.fav_list_row, viewGroup, false);

            TextView dataSymbol = rowView.findViewById(R.id.fav_list_symbol);
            TextView dataPrice = rowView.findViewById(R.id.fav_list_price);
            TextView dataChange = rowView.findViewById(R.id.fav_list_change);
            dataSymbol.setText(data.get(i).getSymbol());
            dataPrice.setText(data.get(i).getPrice());
            String changeSlotText = data.get(i).getChange() + " (" + data.get(i).getChangePercentage() + "%)";
            dataChange.setText(changeSlotText);
            int color = Double.valueOf(data.get(i).getChange()) < 0 ? Color.RED : Color.GREEN;
            dataChange.setTextColor(color);

            return rowView;
        }
    }
}
