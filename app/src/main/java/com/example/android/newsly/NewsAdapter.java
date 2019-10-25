package com.example.android.newsly;

import android.app.Activity;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

/**
 * Created by Affandy on 11/07/2018.
 */

public class NewsAdapter extends ArrayAdapter<NewsItem> {
    private Activity mContext;
    public NewsAdapter(@NonNull Activity context, @NonNull List<NewsItem> newsItemList) {
        super(context, 0, newsItemList);
        mContext = context;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View listItemView = convertView;
        if(listItemView == null){
            listItemView = LayoutInflater.from(mContext).inflate(R.layout.list_item,parent,false);
        }
        NewsItem currentNewsItem = getItem(position);
        // setting news headline textView text
        TextView title = (TextView) listItemView.findViewById(R.id.news_headline_textView);
        title.setText(currentNewsItem.getHeadlineTitle());
        // setting news author name textView text
        TextView author = (TextView) listItemView.findViewById(R.id.news_author_name_textView);
        if(!currentNewsItem.getAuthorName().equals("Unknown")) {
            author.setText("by "+currentNewsItem.getAuthorName());
        }else{
            author.setText(currentNewsItem.getAuthorName());
        }
        // setting news section textView text
        TextView section = (TextView) listItemView.findViewById(R.id.news_section_textView);
        section.setText(currentNewsItem.getSectionName());
        // setting news date textView text
        TextView date = (TextView) listItemView.findViewById(R.id.news_date_textView);
        String dateOnlyWithoutTime = Utils.splitDate(currentNewsItem.getDate());
        date.setText(dateOnlyWithoutTime);
        // setting news thumbnail textView text
        ImageView thumbnail = (ImageView) listItemView.findViewById(R.id.news_thumbnail_imageView);
        if(currentNewsItem.getThumbnail() == null){
            thumbnail.setImageResource(R.drawable.no_thumbnail);
        }else {
            thumbnail.setImageBitmap(currentNewsItem.getThumbnail());
        }

        // return final listView's item view
        return listItemView;
    }
}
