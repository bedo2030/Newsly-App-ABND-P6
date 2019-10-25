package com.example.android.newsly;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Affandy on 11/07/2018.
 */

public final class Utils {
    /** Tag for the log messages */
    private static final String LOG_TAG = Utils.class.getSimpleName();
    private static final int READ_TIME_OUT = 10000;
    private static final int CONNECCT_TIME_OUT = 15000;

    /**
     * Create a private constructor because no one should ever create a {@link Utils} object.
     * This class is only meant to hold static variables and methods, which can be accessed
     * directly from the class name Utils (and an object instance of Utils is not needed).
     */
    private Utils() {
    }
    /* the main entry point for AsyncTaskLoader to get news from the guardian API.
     * return a list of NewsItem objects
     *  */
    public static List<NewsItem> fetchNewsFromSource(String url){
        // holds all news objects which extracted from the guardians API JSON respone
        List<NewsItem> newsItemsList = new ArrayList<>();
        //Create a URL object
        URL apiUrl = createUrlFromString(url);
        //if URL object is null return null earlier
        if (apiUrl == null){
            return null;
        }
        // create String variable to hold JSON Response from the server
        String jsonResponse = "";
        try {
            //try to make HTTP request and get a string Json response back
            jsonResponse = Utils.makeHttpRequest(apiUrl);
        }catch (IOException e){
            Log.e(LOG_TAG,"Error while closing InputStream: ", e);
        }
        //if for any reason jsonResponse variable equals null return earlier
        if(jsonResponse == null){
            return null;
        }
        newsItemsList = Utils.extractNewsFeaturesFromJsonResponse(jsonResponse);
        return newsItemsList;
    }
    /*extract required news features from a json response */
    private static List<NewsItem> extractNewsFeaturesFromJsonResponse(String response){
        List<NewsItem> newsItemList = new ArrayList<>();
        // try to extract news information from json response String
        try {
            JSONObject baseNewsObject = new JSONObject(response);
            JSONObject responseObject = baseNewsObject.getJSONObject("response");
            String status = responseObject.getString("status");
            //if result status provided in the json response isn't ok return earlier
            if(!status.equals("ok")) return null;

            JSONArray resultsArray = responseObject.getJSONArray("results");
            for(int i = 0; i < resultsArray.length(); i++){
                //Main json object
                JSONObject currentNewsItemObject = resultsArray.getJSONObject(i);
                //check if fields object which contains thumbnail is provided
                Bitmap thumbnail = null;
                if(currentNewsItemObject.has("fields")){
                    JSONObject fieldsObject = currentNewsItemObject.getJSONObject("fields");
                    String thumbnailStringUrl = fieldsObject.getString("thumbnail");
                    thumbnail = fetchNewsThumbnailFromUrl(thumbnailStringUrl);
                }
                //get news Date
                String date = currentNewsItemObject.getString("webPublicationDate");
                //get news headline title
                String headlineTitle = currentNewsItemObject.getString("webTitle");
                //get news web url
                String webUrl = currentNewsItemObject.getString("webUrl");
                //get news section name
                String sectionName = currentNewsItemObject.getString("sectionName");
                //Getting Author Name
                JSONArray tagsArray = currentNewsItemObject.getJSONArray("tags");
                JSONObject authorObject = null;
                String authorName = "Unknown";

                if(tagsArray.length() > 0){
                    authorObject = tagsArray.getJSONObject(0);
                    authorName = authorObject.getString("webTitle");
                }
                // Getting thumbnail

                newsItemList.add(new NewsItem(thumbnail,headlineTitle, authorName, date, webUrl, sectionName));
            }
        }catch (JSONException e){
            Log.e(LOG_TAG,"Error while parsing json features: ", e);
        }
        return newsItemList;
    }
    /* make Http request to a specified url to get news thumbnail */
    private static Bitmap fetchNewsThumbnailFromUrl(String url){
        Bitmap fetchedThumbnail = null;
        URL thumbnailUrl = createUrlFromString(url);
        //if URL object is null return null earlier
        if (thumbnailUrl == null){
            return null;
        }
        HttpURLConnection httpURLConnection = null;
        InputStream inputStream = null;

        try{
            httpURLConnection = (HttpURLConnection) thumbnailUrl.openConnection();
            httpURLConnection.setRequestMethod("GET");
            httpURLConnection.setReadTimeout(READ_TIME_OUT);
            httpURLConnection.setConnectTimeout(CONNECCT_TIME_OUT);
            httpURLConnection.connect();
            if (httpURLConnection.getResponseCode() == HttpURLConnection.HTTP_OK){
                inputStream = httpURLConnection.getInputStream();
                fetchedThumbnail = BitmapFactory.decodeStream(inputStream);
            }else{
                Log.e(LOG_TAG,"Error Response Code: " + httpURLConnection.getResponseCode());
                return null;
            }
        }catch (IOException e){
            Log.e(LOG_TAG, "ERROR Failed to Open Connection: ", e);
        }finally {
            if(httpURLConnection != null){
                httpURLConnection.disconnect();
            }
            if(inputStream != null){
                try{
                    inputStream.close();
                }catch (IOException e){
                    Log.e(LOG_TAG,"Error Failed to close input stream: ",e);
                }
            }
        }
        return fetchedThumbnail;
    }
    /*make http request to the Guardian API and return respective JSON Response */
    private static String makeHttpRequest(URL url) throws IOException{
        //String Variable to hold response from the server
        String response = "";

        HttpURLConnection httpURLConnection = null;
        InputStream inputStream = null;
        try{
            httpURLConnection = (HttpURLConnection) url.openConnection();
            httpURLConnection.setRequestMethod("GET");
            httpURLConnection.setReadTimeout(READ_TIME_OUT);
            httpURLConnection.setConnectTimeout(CONNECCT_TIME_OUT);
            httpURLConnection.connect();

            // If the request was successful (response code 200),
            // then read the input stream and parse the response.
            if(httpURLConnection.getResponseCode() == HttpURLConnection.HTTP_OK){
                inputStream = httpURLConnection.getInputStream();
                response = convertInputStreamToString(inputStream);
            }else{
                Log.e(LOG_TAG,"Error Failed to Connect to API Server, Response code: " + httpURLConnection.getResponseCode());
            }
        }catch (IOException e){
            Log.e(LOG_TAG,"Error in converting InputStream to String: ", e);
        }finally {
            if(httpURLConnection != null){
                httpURLConnection.disconnect();
            }
            if(inputStream != null){
                inputStream.close();
            }
        }
        return response;
    }

    /*converts received InputStream to a String response */
    private static String convertInputStreamToString(InputStream inputStream) throws IOException{
        StringBuilder output = new StringBuilder();
        if(inputStream == null) return null;
        InputStreamReader inputStreamReader = new InputStreamReader(inputStream, Charset.forName("UTF-8"));
        BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
        String line = bufferedReader.readLine();
        while (line != null){
            output.append(line);
            line = bufferedReader.readLine();
        }
        return output.toString();
    }
    /* reveives a string url then convert it to a URL object */
    private static URL createUrlFromString(String url){
        URL convertedUrl = null;
        try{
            convertedUrl = new URL(url);
        }catch (MalformedURLException e){
            Log.e(LOG_TAG,"Error converting String url to a URL object: ", e);
        }
        return convertedUrl;
    }
    /* Split a given date and time string */
    public static String splitDate(String date){
        String [] splittedDateAndTime = date.split("T", 2);
        String dateOnly = splittedDateAndTime[0];
        String [] dateOnlySplitted = dateOnly.split("-",3);
        return dateOnlySplitted[2]+"-"+dateOnlySplitted[1]+"-"+dateOnlySplitted[0];
    }
}
