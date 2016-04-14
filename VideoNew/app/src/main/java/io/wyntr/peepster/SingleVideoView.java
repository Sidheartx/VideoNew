package io.wyntr.peepster;

/**
 * Created by Siddharth on 4/1/16.
 */

import android.app.Activity;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.widget.SlidingPaneLayout;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.MediaController;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;



import android.app.Activity;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.widget.MediaController;
import android.widget.VideoView;

import com.parse.CountCallback;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by sagar_000 on 3/9/2016.
 */
public class SingleVideoView extends Activity {

    private static final String TAG = "VideoActivity";
    VideoView mVideoView = null;
    String phone;
    EditText CommentBox;
    Button Send;
    String senderId;
    String videoID;
    private SlidingUpPanelLayout mLayout;
    ListView listView;
    List<ParseObject> ob;
    CommentsAdapter adapter;
    String view;
    int countobjects;
    private ProgressBar loadprogressBar;
    private boolean looping=false;
    private boolean mute=false;

    private List<ParseComments> parseCommentsList = null;
    @Override
    protected void onCreate (Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.video_layout);
        mVideoView = (VideoView)findViewById(R.id.videoView);
        loadprogressBar = (ProgressBar) findViewById(R.id.loadprogressBar);

        // Get the intent from ListViewAdapter
        Intent i = getIntent();
        // Get the intent from ListViewAdapter
        phone = i.getStringExtra("video");
        senderId = i.getStringExtra("userId");
        videoID = i.getStringExtra("objectId");
        view = i.getStringExtra("views");
        new RemoteDataTask().execute();
        CommentBox = (EditText)findViewById(R.id.comment_text);
        TextView views = (TextView)findViewById(R.id.eye_video);
        views.setText(view);
        ParseQuery<ParseObject> query = ParseQuery.getQuery("Comments");
        query.whereEqualTo("videoID", videoID);
        query.countInBackground(new CountCallback() {
            public void done(int count, ParseException e) {
                if (e == null) {
                    countobjects=count;
                    TextView commentscount = (TextView) findViewById(R.id.comments_count);
                    commentscount.setText(String.valueOf(countobjects));
                } else {
                    Toast.makeText(SingleVideoView.this, "Comments", Toast.LENGTH_SHORT);
                }
            }
        });
        Send = (Button)findViewById(R.id.send_comment);
        Send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String comment = CommentBox.getText().toString();
                comment = comment.trim();
                if (comment.isEmpty()) {
                } else {
                    CommentBox.clearComposingText();
                    // create the new user!
                    ParseObject ParseComment = new ParseObject("Comments");
                    ParseComment.put("Comments", comment);
                    ParseComment.put(ParseConstants.KEY_SENDER_ID, ParseUser.getCurrentUser().getObjectId());
                    ParseComment.put(ParseConstants.KEY_RECIPIENT_IDS, senderId);
                    ParseComment.put(ParseConstants.KEY_VIDEO_ID, videoID);
                    ParseComment.increment("CommentsCount", 1);
                    ParseComment.saveInBackground(new SaveCallback() {
                        @Override
                        public void done(ParseException e) {
                            if (e == null) {
                                // success!
                                Toast.makeText(SingleVideoView.this, R.string.success_message, Toast.LENGTH_LONG).show();
                            } else {
                                AlertDialog.Builder builder = new AlertDialog.Builder(SingleVideoView.this);
                                builder.setMessage(R.string.error_sending_message)
                                        .setTitle(R.string.error_selecting_file_title)
                                        .setPositiveButton(android.R.string.ok, null);
                                AlertDialog dialog = builder.create();
                                dialog.show();
                            }
                        }
                    });
                }
            }
        });
        MediaController mediaController = new MediaController(this);


        mediaController.setAnchorView(mVideoView);
        mVideoView.setVideoPath(phone);
        mVideoView.start();
        loadprogressBar.setVisibility(View.VISIBLE);
        mVideoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                mp.start();

                if(looping){
                    mp.setLooping(true);
                    mp.setVolume(0,0);
                }
                mp.setOnVideoSizeChangedListener(new MediaPlayer.OnVideoSizeChangedListener() {
                    @Override
                    public void onVideoSizeChanged(MediaPlayer mp, int width, int height) {
                        loadprogressBar.setVisibility(View.GONE);

                    }
                });
                Toast.makeText(getApplicationContext(), "onPrepared", Toast.LENGTH_SHORT);

                if (mVideoView.isPlaying()) {


                    //////increasing the number of views/////
//                    ParseObject views = new ParseObject("AroundMe");
                   // views.increment("views", 1);
                   // views.setObjectId(videoID);
                   // views.saveInBackground();
                    //////increasing the number of total impressions by the posting user////////
                    ParseObject impressions = new ParseObject("UserData");
                    impressions.put("user",senderId);
                    impressions.increment("impressions", 1);

                    impressions.saveInBackground();




                }
                if (mLayout.getPanelState() == SlidingUpPanelLayout.PanelState.HIDDEN) {
                    mVideoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                        @Override
                        public void onCompletion(MediaPlayer mp) {
                            finish();


                        }
                    });
                } else {
                    mVideoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                        @Override
                        public void onCompletion(MediaPlayer mp) {
                            mp.setLooping(true);
                            mp.setVolume(0,0);
                        }
                    });
                   /* Log.i(TAG, "somechange ");
                    mp.setLooping(true);
                    mp.setVolume(0, 0);*/
                }
            }
        });
        mLayout = (SlidingUpPanelLayout) findViewById(R.id.sliding_layout);
        mLayout.addPanelSlideListener(new SlidingUpPanelLayout.PanelSlideListener() {
            @Override
            public void onPanelSlide(View panel, float slideOffset) {
                Log.i(TAG, "onPanelSlide, offset " + slideOffset);



            }
            @Override
            public void onPanelStateChanged(View panel, SlidingUpPanelLayout.PanelState previousState, SlidingUpPanelLayout.PanelState newState) {
                Log.i(TAG, "onPanelStateChanged " + newState);


                if (mLayout.getPanelState() == SlidingUpPanelLayout.PanelState.EXPANDED) {
                    mVideoView.pause();
                    looping=true;
                    mute=true;
                    mVideoView.resume();
                }
                if (mLayout.getPanelState()== SlidingUpPanelLayout.PanelState.COLLAPSED){
                    mVideoView.pause();
                    looping=false;
                    mute=false;
                    mVideoView.resume();
                }



            }
        });
        mLayout.setFadeOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mLayout.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);
            }
        });
    }


    private class RemoteDataTask extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... params) {
            parseCommentsList = new ArrayList<>();
            try {
                ParseQuery<ParseObject> query = new ParseQuery<>(
                        "Comments");
                query.whereEqualTo(ParseConstants.KEY_VIDEO_ID, videoID);
                query.setCachePolicy(ParseQuery.CachePolicy.NETWORK_ELSE_CACHE);
                query.orderByDescending("createdAt");
                ob = query.find();
                for (ParseObject country : ob) {
                    ParseComments map = new ParseComments();
                    map.setComments((String) country.get("Comments"));
                    parseCommentsList.add(map);
                }
            } catch (ParseException e) {
                Log.e("Error", e.getMessage());
                e.printStackTrace();
            }
            return null;
        }
        @Override
        protected void onPostExecute(Void result) {
            listView = (ListView) findViewById(R.id.comments_list);
            adapter = new CommentsAdapter(SingleVideoView.this,
                    parseCommentsList);
            listView.setAdapter(adapter);
        }
    }
    @Override
    public void onBackPressed() {
        if (mLayout != null &&
                (mLayout.getPanelState() == SlidingUpPanelLayout.PanelState.EXPANDED || mLayout.getPanelState() == SlidingUpPanelLayout.PanelState.ANCHORED)) {
            mLayout.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);
        } else {
            super.onBackPressed();
        }
    }
}
