package io.wyntr.peepster;

import android.location.Location;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.Toast;
import java.math.BigDecimal;

import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseGeoPoint;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.util.ArrayList;
import java.util.List;
import java.text.DecimalFormat;



import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.Toast;
import android.os.SystemClock;

import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseGeoPoint;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;

public class FeedsFragment extends Fragment {
    GridView gridview;
    List<ParseObject> ob;
    FeedsGridAdapter adapter;
    private List<ParseFeeds> phonearraylist = null;
    View rootView;
    Location mylocation;
    private Handler mHandler = new Handler();
    private double latitude;
    private double longitude;



    public static final String TAG = FeedsFragment.class.getSimpleName();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.feeds_layout,
                container, false);

        mylocation=MainActivity.mlastlocation;
        mHandler.postDelayed(new Runnable() {
            public void run() {
                onResume();
            }
        }, 3000);


        if (mylocation!=null){
            Toast.makeText(getActivity(), "Party", Toast.LENGTH_SHORT).show();

            new RemoteDataTask().execute();
            return rootView;
        }
        if (mylocation==null) {
            Toast.makeText(getActivity(),"Could load location,check settings",Toast.LENGTH_SHORT).show();

        }
        return rootView;

    }


    public class RemoteDataTask extends AsyncTask<Void,Void,Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mylocation=MainActivity.mlastlocation;
            if (mylocation==null){
                Toast.makeText(getActivity(),"null",Toast.LENGTH_SHORT).show();}
            if (mylocation!=null) Toast.makeText(getActivity(),"not null",Toast.LENGTH_SHORT).show();

        }

        @Override
        protected Void doInBackground(Void... params) {
            // Create the array
            phonearraylist = new ArrayList<ParseFeeds>();
            try {
                // Locate the class table named "SamsungPhones" in Parse.com
                ParseQuery<ParseObject> query = new ParseQuery<ParseObject>(
                        "AroundMe");
                query.setCachePolicy(ParseQuery.CachePolicy.NETWORK_ELSE_CACHE);
                query.setMaxCacheAge(43200000);

                // Locate the column named "position" in Parse.com and order list
                // by ascending
                latitude=mylocation.getLatitude();
                longitude=mylocation.getLongitude();
                ParseGeoPoint mypoint= new ParseGeoPoint(latitude,longitude);

                /*if(mypoint!=null){
                    query.whereWithinKilometers("GeoArea",mypoint,1);
                }*/




                query.orderByDescending("createdAt");
                //query.whereNotEqualTo("senderId", ParseUser.getCurrentUser().getObjectId());
                ob = query.find();
                for (ParseObject country : ob) {
                    ParseFile image = (ParseFile) country.get(ParseConstants.KEY_THUMB);
                    ParseFile video = (ParseFile)country.get("file");
                    String user = country.getString(ParseConstants.KEY_SENDER_NAME);
                    String userId=country.getString(ParseConstants.KEY_SENDER_ID);
                    ParseGeoPoint postlocation=country.getParseGeoPoint(ParseConstants.KEY_GEOPOINT);



                    String objectId=country.getObjectId();
                    double distance=mypoint.distanceInKilometersTo(postlocation);
                    DecimalFormat dec = new DecimalFormat("@@##");

                    double finaldistance = Double.valueOf(dec.format(distance));



                    int views=country.getInt("views");
                    //Uri fileUri = Uri.parse(video.getUrl());
                    ParseFeeds map = new ParseFeeds();
                    map.setPhone(image.getUrl());
                    map.setVideo(video.getUrl());
                    map.setUser(user);
                    map.setUserId(userId);
                    map.setViews(views);
                    map.setObjectId(objectId);
                    map.setdistance(finaldistance);

                    phonearraylist.add(map);
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
            gridview = (GridView) rootView.findViewById(R.id.gridview);
            // Pass the results into ListViewAdapter.java
            adapter = new FeedsGridAdapter(FeedsFragment.this.getContext(),
                    phonearraylist);
            adapter.notifyDataSetChanged();
            // Binds the Adapter to the ListView
            gridview.setAdapter(adapter);

        }
    }
    @Override
    public void onResume() {
        super.onResume();
        mylocation=MainActivity.mlastlocation;
        if (mylocation != null) {
            new RemoteDataTask().execute();

        }


    }

}
