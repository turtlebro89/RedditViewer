package com.llavender.redditviewer;


import java.util.ArrayList;
import java.util.List;
import android.os.Bundle;
import android.os.Handler;
import android.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

/**
 * While this looks like a lot of code, all this class
 * actually does is load the posts in to the listview.
 *
 * @author Hathy
 */
public class PostsFragment extends Fragment{

    ListView postsList;
    ArrayAdapter<RedditObject> adapter;
    Handler handler;

    String subreddit;
    List<RedditObject> posts;
    PostsHolder postsHolder;

    public PostsFragment(){
        handler=new Handler();
        posts=new ArrayList<>();
    }

    public static Fragment newInstance(String subreddit){
        PostsFragment pf=new PostsFragment();
        pf.subreddit=subreddit;
        pf.postsHolder=new PostsHolder(pf.subreddit);
        return pf;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v=inflater.inflate(R.layout.fragment_posts, container, false);
        postsList=(ListView)v.findViewById(R.id.posts_list);

        postsList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String url = posts.get(position).url;
                Bundle bundle = new Bundle();
                bundle.putString("WEBURL", url);
                WebViewFragment webFrag = new WebViewFragment();
                webFrag.setArguments(bundle);
                getFragmentManager().beginTransaction().replace(R.id.fragments_holder, webFrag)
                        .addToBackStack(null).commit();


            }
        });
        return v;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initialize();
    }

    private void initialize(){
        // This should run only once for the fragment as the
        // setRetainInstance(true) method has been called on
        // this fragment

        if(posts.size() == 0){

            // Must execute network tasks outside the UI
            // thread. So create a new thread.

            new Thread(){
                public void run(){
                    posts.addAll(postsHolder.fetchPosts());

                    // UI elements should be accessed only in
                    // the primary thread, so we must use the
                    // handler here.

                    handler.post(new Runnable(){
                        public void run(){
                            createAdapter();
                        }
                    });
                }
            }.start();
        }else{
            createAdapter();
        }
    }

    /**
     * This method creates the adapter from the list of posts
     * , and assigns it to the list.
     */
    private void createAdapter(){

        // Make sure this fragment is still a part of the activity.
        if(getActivity()==null) return;

        adapter=new ArrayAdapter<RedditObject>(getActivity()
                ,R.layout.post_item
                , posts){
            @Override
            public View getView(int position,
                                View convertView,
                                ViewGroup parent) {

                if(convertView==null){
                    convertView=getActivity()
                            .getLayoutInflater()
                            .inflate(R.layout.post_item, null);
                }
                ImageView thumbnail;
                thumbnail = (ImageView)convertView.findViewById(R.id.thumbnail);

                TextView postTitle;
                postTitle=(TextView)convertView.findViewById(R.id.post_title);

                TextView postDetails;
                postDetails=(TextView)convertView.findViewById(R.id.post_details);

                TextView postScore;
                postScore=(TextView)convertView.findViewById(R.id.post_score);

                Log.d("thumbnail", posts.get(position).thumbnail);
                if(posts.get(position).thumbnail.equals("default") || posts.get(position).thumbnail.isEmpty()){
                    Picasso.with(getActivity()).load(R.drawable.reddit_alien).fit().centerInside().into(thumbnail);
                } else {
                    Picasso.with(getActivity()).load(posts.get(position).thumbnail).fit().centerInside().into(thumbnail);
                }
                postTitle.setText(posts.get(position).title);
                postDetails.setText(posts.get(position).getDetails());
                postScore.setText(posts.get(position).getScore());
                return convertView;
            }
        };
        postsList.setAdapter(adapter);

        postsList.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {

            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                int lastItemInView = firstVisibleItem + visibleItemCount;
                if (lastItemInView == totalItemCount) {
                    new Thread() {
                        public void run() {
                            posts.addAll(postsHolder.fetchMorePosts());

                            // UI elements should be accessed only in
                            // the primary thread, so we must use the
                            // handler here.

                            handler.post(new Runnable() {
                                public void run() {
                                    adapter.notifyDataSetChanged();
                                }
                            });
                        }
                    }.start();
                }
            }
        });
    }
}

