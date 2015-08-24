package com.llavender.redditviewer;

import android.os.AsyncTask;
import android.util.Log;

import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

/**
 * Created by lsl017 on 6/5/2015.
 */
public class LoadJSON extends AsyncTask<Void, Void, String> {

    public AsyncResponse mCallback;

    public LoadJSON(AsyncResponse asyncResponse){
        mCallback = asyncResponse;
    }

    public interface AsyncResponse {
        void processFinish(String JSONString);
    }

    public HttpsURLConnection load() {

        HttpsURLConnection urlConnection = null;

        try {
            URL url = new URL("https://www.reddit.com/r/nottheonion/.json");
            urlConnection = (HttpsURLConnection) url.openConnection();
        } catch (MalformedURLException badURLException) {
            badURLException.printStackTrace();
        } catch (IOException ioException) {
            ioException.printStackTrace();
        }
        return urlConnection;
    }

    public RedditObject loadJSONData(HttpsURLConnection urlConnection) {

        RedditObject redObj = null;
        Gson gson = new Gson();

        if (urlConnection != null) {
            BufferedReader br = null;

            try{
                InputStream is = urlConnection.getInputStream();
                br = new BufferedReader(new InputStreamReader(is));
                while(br.ready()){
                    Log.d("newline", br.readLine());
                }
                redObj = gson.fromJson(br, RedditObject.class);
            } catch (IOException ioException) {
                ioException.printStackTrace();
            } finally {
                if (br != null) {
                    try {
                        br.close();
                    } catch (IOException ioException) {
                        ioException.printStackTrace();
                    }
                }
            }
        }
        return redObj;
    }

    @Override
    protected String doInBackground(Void... params) {
        return loadJSONData(load()).toString();
    }

    @Override
    protected void onPostExecute(String result){
        mCallback.processFinish(result);
    }
}

