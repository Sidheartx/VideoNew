package io.wyntr.peepster;

import android.app.Activity;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.GridView;

import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseGeoPoint;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Siddharth on 4/4/16.
 */


public class MyPosts extends Activity {
    GridView gridview;
    List<ParseObject> ob;
    MyPostsAdapter adapter;
    private Location mylocation;
    private double latitude;
    private double longitude;
    private ParseGeoPoint mypoint;

    private List<ParsePosts> postsarraylist = null;
    public static final String TAG = FeedsFragment.class.getSimpleName();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_posts);
        new RemoteDataTask().execute();
        mylocation=MainActivity.mlastlocation;
        latitude=mylocation.getLatitude();
        longitude=mylocation.getLongitude();
        mypoint=new ParseGeoPoint(latitude,longitude);
    }
    private class RemoteDataTask extends AsyncTask<Void,Void,Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }
        @Override
        protected Void doInBackground(Void... params) {
            // Create the array
            postsarraylist = new ArrayList<>();
            try {
                ParseQuery<ParseObject> query = new ParseQuery<ParseObject>(
                        "AroundMe");
                query.setCachePolicy(ParseQuery.CachePolicy.NETWORK_ELSE_CACHE);
                query.setMaxCacheAge(43200000);
                query.orderByDescending("createdAt");
                //query.whereWithinKilometers("GeoArea",mypoint,1);
                query.whereEqualTo("senderId", ParseUser.getCurrentUser().getObjectId());
                ob = query.find();
                for (ParseObject country : ob) {
                    ParseFile image = (ParseFile) country.get("Thumbs");
                    ParseFile video = (ParseFile)country.get("file");
                    String user = country.getString(ParseConstants.KEY_SENDER_NAME);
                    String userId = country.getString(ParseConstants.KEY_SENDER_ID);
                    int views = country.getInt("views");
                    String objectId = country.getObjectId();
                    //Uri fileUri = Uri.parse(video.getUrl());
                    ParsePosts map = new ParsePosts();
                    map.setPhone(image.getUrl());
                    map.setVideo(video.getUrl());
                    map.setUser(user);
                    map.setUserId(userId);
                    map.setObjectId(objectId);
                    map.setViews(views);
                    postsarraylist.add(map);
                }
            } catch (ParseException e) {
                Log.e("Error", e.getMessage());
                e.printStackTrace();
            }
            return null;
        }
        @Override
        protected void onPostExecute(Void result) {
            // Locate the gridview in gridview_main.xml
            gridview = (GridView)findViewById(R.id.posts_grid_view);
            // Pass the results into ListViewAdapter.java
            adapter = new MyPostsAdapter(MyPosts.this,
                    postsarraylist);
            // Binds the Adapter to the ListView
            gridview.setAdapter(adapter);
        }
    }
}
