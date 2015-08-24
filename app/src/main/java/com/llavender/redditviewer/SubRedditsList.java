package com.llavender.redditviewer;

import android.os.Bundle;
import android.app.Fragment;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class SubRedditsList extends Fragment {

    ListView redditList;
    ArrayAdapter<RedditObject> adapter;
    Handler handler;

    String subreddit;
    List<RedditObject> subreddits;
    PostsHolder postsHolder;

    public SubRedditsList() {
        handler = new Handler();
        subreddits = new ArrayList<>();
    }

    public static Fragment newInstance(String subreddit){

        SubRedditsList subRedditList = new SubRedditsList();
        subRedditList.subreddit = subreddit;
        subRedditList.postsHolder = new PostsHolder(subRedditList.subreddit);

        return subRedditList;
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        View view = inflater.inflate(R.layout.fragment_sub_reddits_list, container, false);
        redditList = (ListView) view.findViewById(R.id.subreddit_list_view);

        redditList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                getFragmentManager().beginTransaction()
                        .replace(R.id.fragments_holder,
                                PostsFragment.newInstance(subreddits.get(position).url))
                        .addToBackStack(null)
                        .commit();
            }
        });

        return view;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState){
        super.onActivityCreated(savedInstanceState);
        initialize();
    }

    private void initialize(){
        if(subreddits.size() == 0){
            new Thread(){
                public void run(){
                    subreddits.addAll(postsHolder.fetchPosts());

                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            createAdapter();
                        }
                    });
                }
            }.start();
        } else {
            createAdapter();
        }
    }

    private void createAdapter(){
        if(getActivity() == null) return;

        adapter = new ArrayAdapter<RedditObject>(getActivity(), R.layout.post_item, subreddits){
            @Override
            public View getView(int position, View convertView, ViewGroup parent){
                if(convertView == null){
                    convertView = getActivity().getLayoutInflater().inflate(R.layout.post_item, null);
                }
                ImageView thumbnail;
                thumbnail = (ImageView)convertView.findViewById(R.id.thumbnail);

                TextView postTitle;
                postTitle = (TextView)convertView.findViewById(R.id.post_title);

                TextView postDetails;
                postDetails = (TextView)convertView.findViewById(R.id.post_details);

                TextView postScore;
                postScore = (TextView)convertView.findViewById(R.id.post_score);

                thumbnail.setVisibility(View.GONE);
                postTitle.setText(subreddits.get(position).title);
                postDetails.setVisibility(View.GONE);
                postScore.setVisibility(View.GONE);

                return convertView;
            }
        };

        redditList.setAdapter(adapter);
        redditList.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState){}

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                int lastItemInView = firstVisibleItem + visibleItemCount;
                if (lastItemInView == totalItemCount) {
                    new Thread() {
                        public void run() {
                            subreddits.addAll(postsHolder.fetchMorePosts());

                            handler.post(new Runnable() {
                                @Override
                                public void run() {
                                    adapter.notifyDataSetChanged();
                                }
                            });
                        }
                    };
                }
            }
        });
    }
}
