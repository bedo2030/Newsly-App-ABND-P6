package com.example.android.newsly;

import android.app.LoaderManager;
import android.content.Context;
import android.content.Intent;
import android.content.Loader;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<List<NewsItem>> {
    private final String THE_GUARDIAN_REQUEST_URL="https://content.guardianapis.com/technology";
    public static final String LOG_TAG = MainActivity.class.getSimpleName();
    public NewsAdapter mAdapter;
    public ListView mNewsListView;
    public TextView mEmptyStateTextView;
    private ProgressBar mLoadingIndicator;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mEmptyStateTextView = (TextView) findViewById(R.id.empty_state_textView);
        mLoadingIndicator = (ProgressBar) findViewById(R.id.loading_indicator);
        //finding news list view and setting ArrayAdapter and Empty State view to it
        mNewsListView = findViewById(R.id.list);
        mNewsListView.setEmptyView(mEmptyStateTextView);
        mAdapter = new NewsAdapter(this,new ArrayList<NewsItem>());
        mNewsListView.setAdapter(mAdapter);
        // getting current network connectivity status
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        // check for network connectivity
        if(networkInfo == null || !networkInfo.isConnected() ||
                (networkInfo.getType() != ConnectivityManager.TYPE_WIFI
                && networkInfo.getType() != ConnectivityManager.TYPE_MOBILE)){
            mLoadingIndicator.setVisibility(View.GONE);
            mEmptyStateTextView.setText(getString(R.string.no_internet));
        }else {
            getLoaderManager().initLoader(0,null,this).forceLoad();
        }
        // when an item from news list is clicked open its web url in a browser
        mNewsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                NewsItem currentNewsItem = mAdapter.getItem(position);
                Uri newsUri = Uri.parse(currentNewsItem.getWebUrl());
                Intent openNewsWebUrl = new Intent(Intent.ACTION_VIEW, newsUri);
                startActivity(openNewsWebUrl);
            }
        });
    }

    @NonNull
    @Override
    public Loader<List<NewsItem>> onCreateLoader(int id, @Nullable Bundle args) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        // getString retrieves a String value from the preferences.
        // The second parameter is the default value for this preference.
        String numberOfNews = sharedPreferences.getString(
                getString(R.string.settings_number_of_news_key),
                getString(R.string.settings_number_of_news_default));

        String orderBy = sharedPreferences.getString(
                getString(R.string.settings_order_by_key),
                getString(R.string.settings_order_by_default));
        // parse breaks apart the URI string that's passed into its parameter
        Uri baseUri = Uri.parse(THE_GUARDIAN_REQUEST_URL);
        // buildUpon prepares the baseUri that we just parsed so we can add query parameters to it
        Uri.Builder uriBuilder = baseUri.buildUpon();
        // Append query parameter and its value. For example, the `query=technology`
        uriBuilder.appendQueryParameter("q", "technolgy");
        uriBuilder.appendQueryParameter("section", "technology");
        uriBuilder.appendQueryParameter("page-size", numberOfNews);
        uriBuilder.appendQueryParameter("format", "json");
        uriBuilder.appendQueryParameter("show-tags", "contributor");
        uriBuilder.appendQueryParameter("show-fields", "thumbnail");
        uriBuilder.appendQueryParameter("order-by", orderBy);
        String apiKey = BuildConfig.apiKey;
        uriBuilder.appendQueryParameter("api-key", apiKey);

        return new NewsLoader(this, uriBuilder.toString());
    }

    @Override
    public void onLoadFinished(Loader<List<NewsItem>> loader, List<NewsItem> newsItemList) {
        mLoadingIndicator.setVisibility(View.GONE);
        // Set empty state text to display "No earthquakes found."
        mEmptyStateTextView.setText(R.string.no_news);
        // Clear the adapter of previous News data
        mAdapter.clear();
        if(!newsItemList.isEmpty())mAdapter.addAll(newsItemList);
    }

    @Override
    public void onLoaderReset(Loader<List<NewsItem>> loader) {
        mAdapter.clear();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();
        if(itemId == R.id.action_settings){
            Intent intent = new Intent(this, SettingsActivity.class);
            startActivity(intent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
