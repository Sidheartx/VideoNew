package io.wyntr.peepster;

/**
 * Created by Siddharth on 3/23/16.
 */

import android.app.Activity;
import android.content.Context;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.view.MotionEvent;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.VideoView;
import android.widget.ViewFlipper;

import com.parse.ParseObject;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;


//import com.squareup.picasso.Picasso;


public class ViewImageActivity extends Activity {

    ViewFlipper viewFlipper;
    VideoView mVideoView;
    private ArrayList<ParseObject> Media;
    ParseObject media;
    private float lastX;
    Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_image);
        // Get the intent from ListViewAdapter
        Uri imagefile = getIntent().getData();
        Uri videoUri = getIntent().getData();
        viewFlipper = (ViewFlipper)findViewById(R.id.flipper);


        if (media.getString("FileType").equals("ImageType")) {
            // view the image
            ImageView imageView = (ImageView)findViewById(R.id.imageView);
           // Picasso.with(this).load(imagefile.toString()).into(imageView);
        }
        else{

            mVideoView = (VideoView)findViewById(R.id.videoView);
            MediaController mediaController = new MediaController(this);
            mediaController.setAnchorView(mVideoView);
            // mVideoView.setMediaController(mediaController);
            mVideoView.setVideoURI(videoUri);

            mVideoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mp) {
                    mVideoView.start();
                }
            });
        }
    }

    @Override
    public boolean onTouchEvent (MotionEvent touchevent) {
        switch (touchevent.getAction()) {

            case MotionEvent.ACTION_DOWN:
                lastX = touchevent.getX();
                break;
            case MotionEvent.ACTION_UP:

                float currentX = touchevent.getX();
                // Handling left to right screen swap.
                if (lastX < currentX) {
                    // If there aren't any other children, just break.
                    if (viewFlipper.getDisplayedChild() == 0)
                        break;
                    // Next screen comes in from left.
                    //viewFlipper.setInAnimation(context, R.anim.slide_in_from_left);
                    // Current screen goes out from right.
                    //viewFlipper.setOutAnimation(context, R.anim.slide_out_to_right);
                    // Display next screen.
                    Timer timer = new Timer();
                    timer.schedule(new TimerTask() {
                        @Override
                        public void run() {
                            viewFlipper.showNext();
                        }
                    }, 3 * 1000);
                    viewFlipper.showNext();
                }
                break;
        }
        return false;
    }

}
