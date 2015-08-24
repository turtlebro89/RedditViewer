package com.llavender.redditviewer;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Spinner;

import java.util.ArrayList;

public class MainActivity extends Activity {

    Spinner subreddits;

    ArrayList<RedditObject> subredditObjects;
    ArrayList<String> subredditStrings;

    SharedPreferences preferences;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initializeVariables();
        addFragment("/reddits/", false);

        subreddits.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }



    private void initializeVariables(){
        preferences = getSharedPreferences("REDDITACTIVITY", MODE_PRIVATE);
        subredditObjects = new ArrayList<>();
        subredditStrings = new ArrayList<>();
        subreddits = (Spinner) findViewById(R.id.subreddit_spinner);
    }

    private void addFragment(String reddit, boolean doReplace){
        if(doReplace){
            getFragmentManager().beginTransaction()
                    .replace(R.id.fragments_holder, PostsFragment.newInstance(reddit))
                    .addToBackStack(null)
                    .commit();
        } else {
            getFragmentManager()
                    .beginTransaction()
                    .add(R.id.fragments_holder
                            , SubRedditsList.newInstance(reddit))
                    .commit();
        }
    }
}

