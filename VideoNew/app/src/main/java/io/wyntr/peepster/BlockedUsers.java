package io.wyntr.peepster;

/**
 * Created by Siddharth on 3/23/16.
 */

import android.app.AlertDialog;
import android.app.ListActivity;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseRelation;
import com.parse.ParseUser;
import com.parse.SaveCallback;
//import com.parse.anyphone.R;

import java.util.List;


//import com.parse.anyphone.R;


public class BlockedUsers extends ListActivity {

    protected ParseRelation<ParseUser> mFriendsRelation;
    protected ParseUser mCurrentUser;
    protected ParseRelation<ParseUser> mBlockedRelation;

    public static final String TAG = EditFriendsActivity.class.getSimpleName();

    protected List<ParseUser> mUsers;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        setContentView(R.layout.activity_blockedusers);
        mCurrentUser = ParseUser.getCurrentUser();
        mBlockedRelation = mCurrentUser.getRelation(ParseConstants.KEY_BLOCKED_RELATION);
        // Show the Up button in the action bar.


        getListView().setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
        mBlockedRelation=mCurrentUser.getRelation("blockedusers");
        getListView().setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {

            //////unblocking users////////
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                mBlockedRelation.remove(mUsers.get(position));
                //mFriendsRelation.remove(mUsers.get(position));
                mCurrentUser.saveInBackground(new SaveCallback() {
                    @Override
                    public void done(ParseException e) {
                        if (e != null) {
                            Log.e(TAG, e.getMessage());
                        }
                    }
                });



                Toast.makeText(BlockedUsers.this, "User UnBlocked" + position, Toast.LENGTH_SHORT).show();
                return false;
            }
        });



    }




    @Override
    protected void onResume() {
        super.onResume();

        /////code to load contacts///////

        mCurrentUser = ParseUser.getCurrentUser();
        mBlockedRelation = mCurrentUser.getRelation(ParseConstants.KEY_BLOCKED_RELATION);


        setProgressBarIndeterminateVisibility(true);

        ParseQuery<ParseUser> query = mBlockedRelation.getQuery();

        query.orderByAscending("name");
        query.setCachePolicy(ParseQuery.CachePolicy.CACHE_THEN_NETWORK);
        query.findInBackground(new FindCallback<ParseUser>() {
            @Override
            public void done(List<ParseUser> users, ParseException e) {
                setProgressBarIndeterminateVisibility(false);

                if (e == null) {
                    // Success
                    mUsers = users;
                    String[] usernames = new String[mUsers.size()];
                    int i = 0;
                    for(ParseUser user : mUsers) {
                        usernames[i] = user.getString("name");



                        i++;
                    }
                    ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                            BlockedUsers.this,
                            android.R.layout.simple_list_item_checked,
                            usernames);
                    setListAdapter(adapter);

                    UnblockCheckmarks();
                }
                else {
                    Log.e(TAG, e.getMessage());
                    AlertDialog.Builder builder = new AlertDialog.Builder(BlockedUsers.this);
                    builder.setMessage(e.getMessage())
                            .setTitle(R.string.error_title)
                            .setPositiveButton(android.R.string.ok, null);
                    AlertDialog dialog = builder.create();
                    dialog.show();
                }
            }
        });
    }

    /**
     * Set up the {@link android.app.ActionBar}.
     */


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                // This ID represents the Home or Up button. In the case of this
                // activity, the Up button is shown. Use NavUtils to allow users
                // to navigate up one level in the application structure. For
                // more details, see the Navigation pattern on Android Design:
                //
                // http://developer.android.com/design/patterns/navigation.html#up-vs-back
                //
                NavUtils.navigateUpFromSameTask(this);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);

        if (getListView().isItemChecked(position)) {
            // add the friend
            mBlockedRelation.add(mUsers.get(position));
        }
        else {
            // remove the friend
            mBlockedRelation.remove(mUsers.get(position));
        }

        mCurrentUser.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e != null) {
                    Log.e(TAG, e.getMessage());
                }
            }
        });
    }



    private void UnblockCheckmarks() {
        mBlockedRelation.getQuery().findInBackground(new FindCallback<ParseUser>() {
            @Override
            public void done(List<ParseUser> friends, ParseException e) {
                if (e == null) {
                    // list returned - look for a match
                    for (int i = 0; i < mUsers.size(); i++) {
                        ParseUser user = mUsers.get(i);

                        for (ParseUser friend : friends) {
                            if (friend.getObjectId().equals(user.getObjectId())) {
                                getListView().setItemChecked(i, true);
                            }
                        }
                    }
                }
                else {
                    Log.e(TAG, e.getMessage());
                }
            }
        });
    }
}










