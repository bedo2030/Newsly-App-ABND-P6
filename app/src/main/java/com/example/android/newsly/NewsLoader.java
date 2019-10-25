package com.example.android.newsly;

import android.content.AsyncTaskLoader;
import android.content.Context;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Affandy on 11/07/2018.
 */

public class NewsLoader extends AsyncTaskLoader<List<NewsItem>> {
    /** Tag for log messages */
    private static final String LOG_TAG = NewsLoader.class.getName();
    /** Query URL */
    private String mUrl;

    public NewsLoader(Context context, String url){
        super(context);
        mUrl = url;
    }

    @Override
    protected void onStartLoading() {
        forceLoad();
    }

    @Override
    public List<NewsItem> loadInBackground() {
        List<NewsItem> newsItemList = new ArrayList<>();
        // get news from specified url
        newsItemList = Utils.fetchNewsFromSource(mUrl);
        return newsItemList;
    }
}
