package com.example.android.newsly;

import android.graphics.Bitmap;

/**
 * Created by Affandy on 11/07/2018.
 */

public class NewsItem {
    private Bitmap mThumbnail;
    private String mHeadlineTitle;
    private String mAuthorName;
    private String mDate;
    private String mWebUrl;
    private String mSectionName;


    public NewsItem(Bitmap thumbnail, String headlineTitle, String authorName, String date, String webUrl, String sectionName){
        mThumbnail = thumbnail;
        mHeadlineTitle = headlineTitle;
        mAuthorName = authorName;
        mDate = date;
        mWebUrl = webUrl;
        mSectionName = sectionName;
    }

    public Bitmap getThumbnail(){
        return mThumbnail;
    }
    public String getHeadlineTitle(){
        return mHeadlineTitle;
    }
    public String getAuthorName(){
        return mAuthorName;
    }
    public String getDate(){
        return mDate;
    }
    public String getWebUrl(){
        return mWebUrl;
    }
    public String getSectionName(){
        return mSectionName;
    }
}
