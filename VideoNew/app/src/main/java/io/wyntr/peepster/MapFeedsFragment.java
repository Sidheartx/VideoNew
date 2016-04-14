package io.wyntr.peepster;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.FragmentActivity;
import android.widget.Toast;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseGeoPoint;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseQueryAdapter;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by sagar_000 on 4/7/2016.
 */
public class MapFeedsFragment extends FragmentActivity {
    // Fields for helping process map and location changes
    private final Map<String, Marker> mapMarkers = new HashMap<String, Marker>();
    private int mostRecentMapUpdate;
    Location mylocation;
    double latitude;
    double longitude;
    private Handler mHandler = new Handler();
    private ParseQueryAdapter<ParseMap> postsQueryAdapter;

    private SupportMapFragment mapFragment;
    private String userId;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.map_fragment);
        mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);

        mHandler.postDelayed(new Runnable() {
            public void run() {
                onResume();
            }
        }, 3000);
        mylocation = MainActivity.mlastlocation;
        // Gets to GoogleMap from the MapView and does initialization stuff
        mapFragment.getMap().setMyLocationEnabled(true);
        // Enable the current location "blue dot"
        mapFragment.getMap().setOnCameraChangeListener(new GoogleMap.OnCameraChangeListener() {
            public void onCameraChange(CameraPosition position) {

                if (mylocation == null) {
                    Toast.makeText(MapFeedsFragment.this, "null", Toast.LENGTH_SHORT).show();
                }
                if (mylocation != null)
                    Toast.makeText(MapFeedsFragment.this, "not null", Toast.LENGTH_SHORT).show();
                ParseMap aroundposts = new ParseMap();
                doMapQuery();
            }
        });
    }

    private ParseGeoPoint geoPointFromLocation(Location location) {
        latitude = location.getLatitude();
        longitude = location.getLongitude();
        ParseGeoPoint myloc = new ParseGeoPoint(latitude, longitude);
        return myloc;
    }



    private void doMapQuery() {
        final int myUpdateNumber = ++mostRecentMapUpdate;
        //Location myLoc = (currentLocation == null) ? lastLocation : currentLocation;
        // If location info isn't available, clean up any existing markers
        if (mylocation == null) {
            cleanUpMarkers(new HashSet<String>());
            return;
        }
        final ParseGeoPoint myPoint = geoPointFromLocation(mylocation);
        // Create the map Parse query
        ParseQuery<ParseMap> mapQuery = ParseMap.getQuery();
        // Set up additional query filters
        //mapQuery.whereWithinKilometers("location", myPoint, MAX_POST_SEARCH_DISTANCE);
        mapQuery.include("user");
        mapQuery.orderByDescending("createdAt");
        //mapQuery.setLimit(MAX_POST_SEARCH_RESULTS);
        // Kick off the query in the background
        mapQuery.findInBackground(new FindCallback<ParseMap>() {
            @Override
            public void done(List<ParseMap> objects, ParseException e) {
                if (e != null) {
                    //if (Application.APPDEBUG) {
                    //  Log.d(Application.APPTAG, "An error occurred while querying for map posts.", e);
                    //}
                    return;
                }
        /*
         * Make sure we're processing results from
         * the most recent update, in case there
         * may be more than one in progress.
         */
                if (myUpdateNumber != mostRecentMapUpdate) {
                    return;
                }
                // Posts to show on the map
                Set<String> toKeep = new HashSet<String>();
                // Loop through the results of the search
                for (final ParseMap post : objects) {
                    // Add this post to the list of map pins to keep
                    toKeep.add(post.getObjectId());
                    // Check for an existing marker for this post
                    Marker oldMarker = mapMarkers.get(post.getObjectId());
                    // Set up the map marker's location
                    String uri = post.getVideoThumb().getUrl();
                    // final MarkerOptions finalMarkerOpts = markerOpts;
                    Picasso.with(MapFeedsFragment.this)
                            .load(uri)
                            .resize(100,100)
                            .centerCrop()
                            .into(new Target() {
                                @Override
                                public void onBitmapLoaded(final Bitmap bitmap, Picasso.LoadedFrom from) {
            /* Save the bitmap or do something with it here */

                                    final MarkerOptions markerOpts =
                                            new MarkerOptions().position(new LatLng(post.getLocation().getLatitude(), post
                                                    .getLocation().getLongitude())).icon(BitmapDescriptorFactory.fromBitmap(bitmap)).snippet(post.getObjectId()).title(post.getUser());
                                    // Add a new marker
                                    Marker marker = mapFragment.getMap().addMarker(markerOpts);
                                    mapMarkers.put(post.getObjectId(), marker);
                                    userId = post.getUserId();

                                }

                                @Override
                                public void onBitmapFailed(Drawable errorDrawable) {

                                }

                                @Override
                                public void onPrepareLoad(Drawable placeHolderDrawable) {

                                }
                            });



                    mapFragment.getMap().setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {

                        @Override
                        public boolean onMarkerClick(Marker marker) {
                            final String objectId = marker.getSnippet();
                            ParseQuery<ParseObject> query = ParseQuery.getQuery("AroundMe");
                            query.getInBackground(objectId, new GetCallback<ParseObject>() {
                                @Override
                                public void done(ParseObject parseObject, com.parse.ParseException e) {
                                    if (e == null) {
                                        String video = parseObject.getParseFile("file").getUrl();
                                        String userId = parseObject.getString("senderId");
                                        int views = parseObject.getInt("views");
                                        Intent intent = new Intent (MapFeedsFragment.this,SingleVideoView.class);
                                        intent.putExtra("objectId",objectId);
                                        intent.putExtra("video", video);
                                        intent.putExtra("userId",userId);
                                        //intent.putExtra("objectId",post.getObjectId());
                                        intent.putExtra("views", views);
                                        startActivity(intent);

                                    }
                                    else {
Toast.makeText(getApplicationContext(),"couldnt get values ",Toast.LENGTH_LONG);
                                    }

                                }

                            });

                           // Toast.makeText(getApplicationContext(),String.valueOf()userId,Toast.LENGTH_LONG);
                            return true;


                        }
                    });
                }
            }
        });
    }

    private void cleanUpMarkers(Set<String> markersToKeep) {
        for (String objId : new HashSet<String>(mapMarkers.keySet())) {
            if (!markersToKeep.contains(objId)) {
                Marker marker = mapMarkers.get(objId);
                marker.remove();
                mapMarkers.get(objId).remove();
                mapMarkers.remove(objId);
            }
        }
    }
}