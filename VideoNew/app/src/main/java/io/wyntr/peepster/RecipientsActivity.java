package io.wyntr.peepster;

/**
 * Created by Siddharth on 3/26/16.
 */
import android.app.AlertDialog;
import android.app.ListActivity;
import android.graphics.Bitmap;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.CheckedTextView;
import android.widget.ListView;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseGeoPoint;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseRelation;
import com.parse.ParseUser;
import com.parse.SaveCallback;


import java.util.ArrayList;
import java.util.List;

public class RecipientsActivity extends ListActivity {

    public static final String TAG = RecipientsActivity.class.getSimpleName();

    byte[] fileBytes;

    protected ParseRelation<ParseUser> mFriendsRelation;
    protected ParseUser mCurrentUser;
    protected List<ParseUser> mFriends;
    private Bitmap Thumb;
    protected Uri mMediaUri;
    private FloatingActionButton fab;
    private CheckedTextView around;
    private boolean story = false;
    byte[] byteArray;
    private Location mylocation;
    ParseGeoPoint mypoint;
    private double mylat;
    private double mylon;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        setContentView(R.layout.activity_recipients);

        getListView().setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
        mylocation=MainActivity.mlastlocation;
        mylat=mylocation.getLatitude();
        mylon=mylocation.getLongitude();
        mypoint = new ParseGeoPoint(mylat,mylon);


        around = (CheckedTextView)findViewById(R.id.CheckText);
        around.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (around.isChecked()) {
                    around.setChecked(false);
                    CoordinatorLayout.LayoutParams p = new CoordinatorLayout.LayoutParams(CoordinatorLayout.LayoutParams.WRAP_CONTENT, CoordinatorLayout.LayoutParams.WRAP_CONTENT);
                    p.anchorGravity = Gravity.BOTTOM | Gravity.CENTER;
                    p.setAnchorId(R.id.colayout);
                    fab.setLayoutParams(p);
                    fab.setVisibility(View.INVISIBLE);
                }
                else {
                    around.setChecked(true);
                    CoordinatorLayout.LayoutParams p = new CoordinatorLayout.LayoutParams(CoordinatorLayout.LayoutParams.WRAP_CONTENT, CoordinatorLayout.LayoutParams.WRAP_CONTENT);
                    p.anchorGravity = Gravity.BOTTOM | Gravity.CENTER;
                    p.setAnchorId(R.id.colayout);
                    fab.setLayoutParams(p);
                    fab.setVisibility(View.VISIBLE);
                }
                story=true;

            }
        });

        mMediaUri = getIntent().getData();
        fileBytes = FileHelper.getByteArrayFromFile(this, mMediaUri);
        byteArray = getIntent().getByteArrayExtra("image");

        fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Click action
                ParseObject message = createMessage();


                if (message == null) {
                    // error
                    AlertDialog.Builder builder = new AlertDialog.Builder(RecipientsActivity.this);
                    builder.setMessage(R.string.error_selecting_file)
                            .setTitle(R.string.error_selecting_file_title)
                            .setPositiveButton(android.R.string.ok, null);
                    AlertDialog dialog = builder.create();
                    dialog.show();
                }
                else {
                    send(message);
                    finish();
                }
                if (story) {
                    ParseFile file = new ParseFile("peeps.mp4", fileBytes);
                  ParseFile thumbs = new ParseFile("thumbs.png",byteArray);
                    ParseObject around = new ParseObject(ParseConstants.CLASS_AROUNDME);

                    around.put(ParseConstants.KEY_SENDER_ID, ParseUser.getCurrentUser().getObjectId());
                    around.put(ParseConstants.KEY_FILE, file);
                    around.put(ParseConstants.KEY_GEOPOINT,mypoint);
                  around.put(ParseConstants.KEY_THUMB, thumbs);
                    around.put(ParseConstants.KEY_SENDER_NAME,ParseUser.getCurrentUser().getString("name"));
                    around.saveInBackground();

                }

            }

        });
    }

    @Override
    public void onResume() {
        super.onResume();

        mCurrentUser = ParseUser.getCurrentUser();
        mFriendsRelation = mCurrentUser.getRelation(ParseConstants.KEY_FRIENDS_RELATION);

        //setProgressBarIndeterminateVisibility(true);

        ParseQuery<ParseUser> query = mFriendsRelation.getQuery();
        query.addAscendingOrder(ParseConstants.KEY_USERNAME);
        query.findInBackground(new FindCallback<ParseUser>() {
            @Override
            public void done(List<ParseUser> friends, ParseException e) {
                //setProgressBarIndeterminateVisibility(false);

                if (e == null) {
                    mFriends = friends;

                    String[] usernames = new String[mFriends.size()];
                    int i = 0;
                    for (ParseUser user : mFriends) {
                        usernames[i] = user.getString("name");
                        i++;
                    }
                    ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                            getListView().getContext(),
                            android.R.layout.simple_list_item_checked,
                            usernames);
                    setListAdapter(adapter);
                } else {
                    Log.e(TAG, e.getMessage());
                    AlertDialog.Builder builder = new AlertDialog.Builder(RecipientsActivity.this);
                    builder.setMessage(e.getMessage())
                            .setTitle(R.string.error_title)
                            .setPositiveButton(android.R.string.ok, null);
                    AlertDialog dialog = builder.create();
                    dialog.show();
                }
            }
        });
    }


    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);

        if (l.getCheckedItemCount() > 0) {
            CoordinatorLayout.LayoutParams p = new CoordinatorLayout.LayoutParams(CoordinatorLayout.LayoutParams.WRAP_CONTENT, CoordinatorLayout.LayoutParams.WRAP_CONTENT);
            p.anchorGravity = Gravity.BOTTOM | Gravity.CENTER;
            p.setAnchorId(R.id.colayout);
            fab.setLayoutParams(p);
            fab.setVisibility(View.VISIBLE);
        }
        else if(story){
            CoordinatorLayout.LayoutParams p = new CoordinatorLayout.LayoutParams(CoordinatorLayout.LayoutParams.WRAP_CONTENT, CoordinatorLayout.LayoutParams.WRAP_CONTENT);
            p.anchorGravity = Gravity.BOTTOM | Gravity.CENTER;
            p.setAnchorId(R.id.colayout);
            fab.setLayoutParams(p);
            fab.setVisibility(View.VISIBLE);
        }

        else{

            CoordinatorLayout.LayoutParams p = new CoordinatorLayout.LayoutParams(CoordinatorLayout.LayoutParams.WRAP_CONTENT, CoordinatorLayout.LayoutParams.WRAP_CONTENT);
            p.anchorGravity = Gravity.BOTTOM | Gravity.CENTER;
            p.setAnchorId(R.id.colayout);
            fab.setLayoutParams(p);
            fab.setVisibility(View.INVISIBLE);
        }

    }



    protected ParseObject createMessage() {
        ParseObject message = new ParseObject(ParseConstants.CLASS_MESSAGES);
        message.put(ParseConstants.KEY_SENDER_ID, ParseUser.getCurrentUser().getObjectId());
        message.put(ParseConstants.KEY_SENDER_NAME, ParseUser.getCurrentUser().getUsername());
        message.put(ParseConstants.KEY_RECIPIENT_IDS, getRecipientIds());

        if (fileBytes == null) {
            return null;
        }
        else {
            ParseFile file = new ParseFile("peeps.mp4", fileBytes);
            message.put(ParseConstants.KEY_FILE, file);
            return message;
        }
    }

    protected ArrayList<String> getRecipientIds() {
        ArrayList<String> recipientIds = new ArrayList<String>();
        for (int i = 0; i < getListView().getCount(); i++) {
            if (getListView().isItemChecked(i)) {
                recipientIds.add(mFriends.get(i).getObjectId());
            }
        }
        return recipientIds;
    }

    protected void send(ParseObject message) {

        message.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e == null) {
                    // success!
                    Toast.makeText(RecipientsActivity.this, R.string.success_message, Toast.LENGTH_LONG).show();
                } else {
                    AlertDialog.Builder builder = new AlertDialog.Builder(RecipientsActivity.this);
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

