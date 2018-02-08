package com.example.yixiangding.hw9;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class NewsList extends Fragment {
    private static final int NEWS_ITEM_NUM = 5;
    private ArrayList<NewsData> newsList;
    private LayoutInflater inflater;

    public NewsList() {
        // Required empty public constructor
    }

    public static NewsList newInstance() {
        NewsList fragment = new NewsList();
        fragment.newsList = new ArrayList<>();
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
        View v = inflater.inflate(R.layout.fragment_news_list, container, false);
        ListView newsListView = v.findViewById(R.id.news_list);
        final NewsAdapter adapter = new NewsAdapter(getActivity(), newsList);
        newsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                NewsData dataWrapper = (NewsData) adapter.getItem(i);
                String url = dataWrapper.getLink();
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse(url));
                startActivity(intent);
            }
        });
        newsListView.setAdapter(adapter);
        return v;
    }

    public void construct(JSONArray json) {
        try {
            JSONArray items = json.getJSONObject(0).getJSONArray("item");
            int count = 0;
            for (int i = 0; i < items.length(); i++) {
                JSONObject item = items.getJSONObject(i);
                if (item.getJSONArray("link").getString(0).contains("/article/")) {
                    String title = item.getJSONArray("title").getString(0);
                    String author = item.getJSONArray("sa:author_name").getString(0);
                    String pubDate = item.getJSONArray("pubDate").getString(0);
                    String link = item.getJSONArray("link").getString(0);
                    pubDate = pubDate.substring(0, pubDate.indexOf("-")) + "EST";
                    newsList.add(new NewsData(title, author, pubDate, link));
                    count++;
                }
                if (count >= NEWS_ITEM_NUM) break;
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public class NewsAdapter extends BaseAdapter {
        private Context context;
        private ArrayList<NewsData> data;

        public NewsAdapter(Context context, ArrayList<NewsData> data) {
            this.context = context;
            this.data = data;
            inflater = (LayoutInflater) this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }
        @Override
        public int getCount() {
            return NEWS_ITEM_NUM;
        }

        @Override
        public Object getItem(int i) {
            return newsList.get(i);
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            View rowView = inflater.inflate(R.layout.news_list_row, viewGroup, false);
            NewsData newsData = (NewsData) getItem(i);
            TextView newsTitle = rowView.findViewById(R.id.news_title);
            TextView newsAuthor = rowView.findViewById(R.id.news_author);
            TextView newsDate = rowView.findViewById(R.id.news_date);
            newsTitle.setText(newsData.getTitle());
            newsAuthor.setText(newsData.getAuthor());
            newsDate.setText(newsData.getDate());
            return rowView;
        }
    }

}
