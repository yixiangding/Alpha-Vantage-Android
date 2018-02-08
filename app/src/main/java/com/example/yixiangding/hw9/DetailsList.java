package com.example.yixiangding.hw9;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;


public class DetailsList extends Fragment {

    private ArrayList<ListData> currData;
    private FavData favData;

    public DetailsList() {
        // Required empty public constructor
    }

    public static DetailsList newInstance() {
        DetailsList fragment = new DetailsList();
        fragment.currData = new ArrayList<>();
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
        View v = inflater.inflate(R.layout.fragment_details_list, container, false);
        // After a empty list was created,
        // get the empty list
        ListView detailsList = v.findViewById(R.id.details_list);
        // Populate contents with customized adapter
        TableAdapter adapter = new TableAdapter(getActivity(), currData);
        detailsList.setAdapter(adapter);
        return v;
    }

    // Construct data ArrayList after data was retrieved (before this frag is created)
    public void construct(JSONObject json) {
        // test
//        currData.add(new ListData("testName", "testValue"));
        try {
            JSONObject meta = json.getJSONObject("Meta Data");
            JSONObject series = json.getJSONObject("Time Series (Daily)");
            JSONObject lastData = null, secondData = null;
            for (int i = 0; i < series.names().length(); i++) {
                if (i == 0) lastData = series.getJSONObject(series.names().getString(i));
                if (i == 1) {
                    secondData = series.getJSONObject(series.names().getString(i));
                    break;
                }
            }
            if (lastData == null || secondData == null) return;
            double changeVal = Formatter.formatter(
                    Double.valueOf(lastData.getString("4. close")) -
                            Double.valueOf(secondData.getString("4. close")),
                    2);
            double changePercent = Formatter.formatter(changeVal / Double.valueOf(lastData.getString("4. close")) * 100, 2);
            String timeStamp = meta.getString("3. Last Refreshed");
            timeStamp += timeStamp.length() > 10 ? " EDT" : " 16:00:00 EDT";
            String symbol = meta.getString("2. Symbol");
            String price = Formatter.formatter(Double.valueOf(lastData.getString("4. close")), 2) + "";

            currData.add(new ListData("Stock Symbol", symbol));
            currData.add(new ListData("Last Price", price));
            currData.add(new ListData("Change", String.valueOf(changeVal) + " (" + changePercent + "%)"));
            currData.add(new ListData("Timestamp", timeStamp));
            currData.add(new ListData("Open", Double.valueOf(lastData.getString("1. open")).toString()));
            currData.add(new ListData("Close", Double.valueOf(secondData.getString("4. close")).toString()));
            currData.add(new ListData("Day's Range", Double.valueOf(lastData.getString("3. low")) + " - " + Double.valueOf(lastData.getString("2. high"))));
            currData.add(new ListData("Volume", lastData.getString("5. volume")));
            currData.add(new ListData("", ""));

            // Init favData
            favData = new FavData(symbol, price, String.valueOf(changeVal), String.valueOf(changePercent), new Date().getTime());

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public FavData getFavData() {
        return favData;
    }


    /**
     Adapter Class to connect data (ArrayList) and Layout
     */
    public class TableAdapter extends BaseAdapter {
        private Context context;
        private ArrayList<ListData> data;
        private LayoutInflater inflater;

        public TableAdapter(Context context, ArrayList<ListData> data) {
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
            View rowView = inflater.inflate(R.layout.details_list_row, viewGroup, false);

            ListData listData = (ListData) getItem(i);
            TextView dataName = rowView.findViewById(R.id.list_data_name);
            TextView dataVal = rowView.findViewById(R.id.list_data_val);
            dataName.setText(data.get(i).getName());
            dataVal.setText(data.get(i).getValue());
            if (data.get(i).getName().equals("Change")) {
                ImageView arrow = rowView.findViewById(R.id.list_data_arrow);
                arrow.setImageResource(
                        Double.valueOf(
                                data.get(i).getValue().substring(0,2)) >= 0 ?
                                R.drawable.green_arrow_up : R.drawable.red_arrow_down);
            }

            return rowView;
        }
    }
}


