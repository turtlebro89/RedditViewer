package com.llavender.redditviewer;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import android.util.Log;

import com.google.gson.Gson;

/**
 * This is the class that creates Post objects out of the Reddit
 * API, and maintains a list of these posts for other classes
 * to use.
 *
 * @author Hathy
 */
public class PostsHolder {

    /**
     * We will be fetching JSON data from the API.
     */
    private final String URL_TEMPLATE=
            "http://www.reddit.com/SUBREDDIT_NAME/"
                    +".json"
                    +"?after=AFTER";

    String subreddit;
    String url;
    String after;

    PostsHolder(String sr){
        subreddit=sr.substring(1, sr.length()-1);
        after="";
        generateURL();
    }

    /**
     * Generates the actual URL from the template based on the
     * subreddit name and the 'after' property.
     */
    private void generateURL(){
        url=URL_TEMPLATE.replace("SUBREDDIT_NAME", subreddit);
        url=url.replace("AFTER", after);
    }

    /**
     * Returns a list of Post objects after fetching data from
     * Reddit using the JSON API.
     *
     * @return
     */
    List<RedditObject> fetchPosts(){
        String raw = RemoteData.readContents(url);
        List<RedditObject> list=new ArrayList<>();
        try{
            JSONObject data=new JSONObject(raw)
                    .getJSONObject("data");
            JSONArray children=data.getJSONArray("children");

            //Using this property we can fetch the next set of
            //posts from the same subreddit
            after=data.getString("after");

            for(int i=0;i<children.length();i++){
                Gson gson = new Gson();
                RedditObject obj = gson.fromJson(children.getJSONObject(i).getJSONObject("data").toString(), RedditObject.class);

                if(obj.title!=null && !obj.over18)
                    list.add(obj);
            }
        }catch(Exception e){
            Log.e("fetchPosts()",e.toString());
        }
        return list;
    }

    /**
     * This is to fetch the next set of posts
     * using the 'after' property
     * @return
     */
    List<RedditObject> fetchMorePosts(){
        generateURL();
        return fetchPosts();
    }
}
