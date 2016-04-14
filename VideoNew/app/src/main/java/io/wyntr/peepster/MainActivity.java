package io.wyntr.peepster;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.location.Location;
import android.location.LocationListener;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.lang.String;
import 	android.telephony.TelephonyManager;



import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v4.view.ViewPager;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import io.wyntr.peepster.file.FileUtils;
import io.wyntr.peepster.video.MediaController;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.parse.ParseAnalytics;
import com.parse.ParseUser;


public class MainActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener,GoogleApiClient.ConnectionCallbacks {

    private static final int RESULT_CODE_COMPRESS_VIDEO = 3;
    private static final String TAG = "MainActivity";
    private EditText editText;
//    private Button proceedbutton=(Button)findViewById(R.id.btnCompressVideo);
    private ProgressBar progressBar;
    private File tempFile;
    public Uri uri ;
    public static final int MEDIA_TYPE_IMAGE = 4;
    public static final int MEDIA_TYPE_VIDEO = 5;
    File mediaFile;


    private static final int TAKE_VIDEO_REQUEST = 1;
    public Uri senduri;
    Bitmap thumb;
    ViewPager mViewPager;
    private ActionBarDrawerToggle drawerToggle;
    private Toolbar toolbar;
    private TabLayout tabLayout;
    private DrawerLayout mDrawer;
    private NavigationView nvDrawer;
    SectionsPagerAdapter mSectionsPagerAdapter;
    private static int REQUEST_LOCATION = 1;
    public static Location mlastlocation;
    private final static int PLAY_SERVICES_RESOLUTION_REQUEST = 1000;
    private static GoogleApiClient GoogleApiClient;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mDrawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.pager);
        setupViewPager(mViewPager);
        tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);
        nvDrawer = (NavigationView) findViewById(R.id.nvView);
        View headerLayout = nvDrawer.inflateHeaderView(R.layout.drawer_header);
        ImageView ivHeaderPhoto = (ImageView) headerLayout.findViewById(R.id.circleView);
        ivHeaderPhoto.setImageResource(R.drawable.people);

        // Find our drawer view
        mDrawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawerToggle = setupDrawerToggle();

        // Tie DrawerLayout events to the ActionBarToggle
        mDrawer.setDrawerListener(drawerToggle);


        // Setup drawer view

        setupDrawerContent(nvDrawer);
       // proceedbutton.setVisibility(View.GONE);
       progressBar = (ProgressBar) findViewById(R.id.progressBar);
       // editText = (EditText) findViewById(R.id.editText);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            Toast.makeText(getApplicationContext(),"no permission",Toast.LENGTH_SHORT).show();
            return;
        }
        else Toast.makeText(getApplicationContext(),"permissions granted",Toast.LENGTH_SHORT).show();
        ParseAnalytics.trackAppOpened(getIntent());

        ParseUser currentUser = ParseUser.getCurrentUser();
        if (currentUser == null) {
            navigateToLogin();
        }

        else {
            Log.i(TAG, currentUser.getUsername());
        }

        {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // Contacts permissions have not been granted.
                Log.i(TAG, "Location permissions has NOT been granted. Requesting permissions.");
                ;


            }
            // Contact permissions have been granted. Show the contacts fragment.
            Log.i(TAG,
                    "Contact permissions have already been granted. Displaying contact details.");
        }

        if(checkPlayServices()){
            buildGoogleApiClient();
        }
        //
        // getusercountrycode();



    }

    private ActionBarDrawerToggle setupDrawerToggle() {

        return new ActionBarDrawerToggle(this, mDrawer, toolbar, R.string.drawer_open, R.string.drawer_close);

    }

    private void setupDrawerContent(NavigationView navigationView) {
        navigationView.setItemTextAppearance(R.style.Base_Animation_AppCompat_Dialog);

        navigationView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(MenuItem menuItem) {

                        selectDrawerItem(menuItem);
                        return true;
                    }
                });
    }

    public void selectDrawerItem(MenuItem menuItem) {
        switch (menuItem.getItemId()) {

            case R.id.nav_first_fragment:
                Intent intent = new Intent(MainActivity.this, EditFriendsActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                break;

            case R.id.nav_second_fragment:
                 intent = new Intent(MainActivity.this, MyPosts.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                break;
            case R.id.nav_third_fragment:
                intent = new Intent(MainActivity.this, MapFeedsFragment.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                break;
        }
        menuItem.setChecked(true);
        // Close the navigation drawer
        mDrawer.closeDrawers();

    }
    private void setupViewPager(ViewPager mViewPager) {
        mSectionsPagerAdapter = new SectionsPagerAdapter(this, getSupportFragmentManager());
        mSectionsPagerAdapter.addFragment(new FeedsFragment(), "Around You");
        mSectionsPagerAdapter.addFragment(new InboxFragment(), "Messages");
        //adapter.addFragment(new ThreeFragment(), "THREE");
        mViewPager.setAdapter(mSectionsPagerAdapter);
    }

    private Uri getOutputMediaFileUri(int mediaType) {
        // To be safe, you should check that the SDCard is mounted
        // using Environment.getExternalStorageState() before doing this.
        if (isExternalStorageAvailable()) {
            // get the URI

            // 1. Get the external storage directory
            String appName = MainActivity.this.getString(R.string.app_name);
            File mediaStorageDir = new File(
                    Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
                    appName);

            // 2. Create our subdirectory
            if (! mediaStorageDir.exists()) {
                if (! mediaStorageDir.mkdirs()) {
                    Log.e(TAG, "Failed to create directory.");
                    return null;
                }
            }

            // 3. Create a file name
            // 4. Create the file

            Date now = new Date();
            String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(now);

            String path = mediaStorageDir.getPath() + File.separator;
            if (mediaType == MEDIA_TYPE_IMAGE) {
                mediaFile = new File(path + "IMG_" + timestamp + ".jpg");
            }
            else if (mediaType == MEDIA_TYPE_VIDEO) {
                mediaFile = new File(path + "VID_" + timestamp + ".mp4");
            }
            else {
                return null;
            }

            Log.d(TAG, "File: " + Uri.fromFile(mediaFile));

            // 5. Return the file's URI
            return Uri.fromFile(mediaFile);

        }
        else {
            return null;
        }
    }
    private boolean isExternalStorageAvailable() {
        String state = Environment.getExternalStorageState();

        if (state.equals(Environment.MEDIA_MOUNTED)) {
            return true;
        }
        else {
            return false;
        }
    }


    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    protected void onActivityResult(int reqCode, int resCode, Intent data) {
        if(mediaFile!=null){
            compress();
        }

        if (resCode == Activity.RESULT_OK && data != null) {

            uri=data.getData();
            if(uri==null){Toast.makeText(getApplicationContext(),"uri blank",Toast.LENGTH_LONG).show();}
            else {Toast.makeText(getApplicationContext(),"uri full",Toast.LENGTH_LONG).show();}
            //proceedbutton.setVisibility(View.VISIBLE);




            if (resCode == RESULT_CODE_COMPRESS_VIDEO) {
                if (uri != null) {
                    Cursor cursor = getContentResolver().query(uri, null, null, null, null, null);

                    try {
                        if (cursor != null && cursor.moveToFirst()) {

                            String displayName = cursor.getString(
                                    cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
                            Log.i(TAG, "Display Name: " + displayName);

                            int sizeIndex = cursor.getColumnIndex(OpenableColumns.SIZE);
                            String size = null;
                            if (!cursor.isNull(sizeIndex)) {
                                size = cursor.getString(sizeIndex);
                            } else {
                                size = "Unknown";
                            }
                            Log.i(TAG, "Size: " + size);

                            tempFile = FileUtils.saveTempFile(displayName, this, uri);
                            editText.setText(mediaFile.getPath());


                        }
                    } finally {
                        if (cursor != null) {
                            cursor.close();
                        }
                    }
                }
            }

        }
    }

    private void deleteTempFile(){
        if(tempFile != null && tempFile.exists()){
            tempFile.delete();
            Toast.makeText(getApplicationContext(),"deleteTempFile",Toast.LENGTH_LONG).show();
        }
    }
    private void deleteCapturedFile(){
        if(mediaFile != null && mediaFile.exists()){
            mediaFile.delete();
            Toast.makeText(getApplicationContext(),"deleteTempFile",Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        deleteTempFile();
        Toast.makeText(getApplicationContext(),"onBackPressed",Toast.LENGTH_LONG).show();
    }

    private void navigateToLogin() {
        Intent intent = new Intent(this, LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        deleteTempFile();
        Toast.makeText(getApplicationContext(),"onDestroy",Toast.LENGTH_LONG).show();
    }

    public void compress() {
       MediaController.getInstance().scheduleVideoConvert(mediaFile.getPath());
        new VideoCompressor().execute();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();
        if(drawerToggle.onOptionsItemSelected(item)){
            return true;
        }

        switch(itemId) {

            case android.R.id.home:
            mDrawer.openDrawer(GravityCompat.START);

            case R.id.video_camera:
                Intent videoIntent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
                uri = getOutputMediaFileUri(MEDIA_TYPE_VIDEO);
                if (uri == null) {
                    // display an error
                    Toast.makeText(MainActivity.this, R.string.error_external_storage,
                            Toast.LENGTH_LONG).show();
                }
                else {
                    videoIntent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
                    videoIntent.putExtra(MediaStore.EXTRA_DURATION_LIMIT, 10);
                    videoIntent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 1);
                     // 0 = lowest res

                    startActivityForResult(videoIntent, RESULT_CODE_COMPRESS_VIDEO);

                }
                break;
        }

        return super.onOptionsItemSelected(item);
    }
    @Override
    protected void onPostCreate(Bundle savedInstanceState){
        super.onPostCreate(savedInstanceState);
        drawerToggle.syncState();
    }
    @Override
    public void onConfigurationChanged(Configuration newConfig){
        super.onConfigurationChanged(newConfig);
        drawerToggle.onConfigurationChanged(newConfig);
    }

    class VideoCompressor extends AsyncTask<Void, Void, Boolean>{

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressBar.setVisibility(View.VISIBLE);
            Log.d(TAG,"Start video compression");
        }

        @Override
        protected Boolean doInBackground(Void... voids) {
            return MediaController.getInstance().convertVideo(mediaFile.getPath());


        }

        @Override
        protected void onPostExecute(Boolean compressed) {
            super.onPostExecute(compressed);
            deleteCapturedFile();
            senduri=MediaController.finaluri;
            if(senduri!=null){Toast.makeText(getApplicationContext(),"fd",Toast.LENGTH_LONG).show();}
            else Toast.makeText(getApplicationContext(),"uri not found",Toast.LENGTH_LONG).show();
            thumb = ThumbnailUtils.createVideoThumbnail(senduri.getPath(),MediaStore.Video.Thumbnails.MINI_KIND);
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            thumb.compress(Bitmap.CompressFormat.PNG,60,stream);
            byte[] byteArray  = stream.toByteArray();
            progressBar.setVisibility(View.GONE);
            if(compressed){
                Log.d(TAG, "Compression successfully!");
            }
            Intent recipientsIntent = new Intent(MainActivity.this, RecipientsActivity.class);
            recipientsIntent.setData(senduri);
            recipientsIntent.putExtra("image",byteArray);
            startActivity(recipientsIntent);
        }
    }
    @Override
    public void onConnected(Bundle bundle) {
        getlocation();
        if(mlastlocation!=null){
            Toast.makeText(getApplicationContext(),"location found ",Toast.LENGTH_LONG).show();

        }

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    public void getlocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        mlastlocation = LocationServices.FusedLocationApi.getLastLocation(GoogleApiClient);
    }
    @Override
    public void onResume() {
        super.onResume();
        GoogleApiClient.connect();

    }
    @Override
    public void onPause() {
        super.onPause();

        GoogleApiClient.disconnect();
    }
    private boolean checkPlayServices() {
        int resultCode = GooglePlayServicesUtil
                .isGooglePlayServicesAvailable(this);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
                GooglePlayServicesUtil.getErrorDialog(resultCode, this,
                        PLAY_SERVICES_RESOLUTION_REQUEST).show();
            } else {
                Toast.makeText(getApplicationContext(),
                        "This device is not supported.", Toast.LENGTH_LONG)
                        .show();
                finish();
            }
            return false;
        }
        return true;
    }

    protected synchronized void buildGoogleApiClient() {
        GoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API).build();
    }
    private void getusercountrycode(){
        TelephonyManager tm = (TelephonyManager)getSystemService(Context.TELEPHONY_SERVICE);
        String countryCode = tm.getSimCountryIso();
        Toast.makeText(getApplicationContext(),"country code is "+ countryCode,Toast.LENGTH_LONG).show();
    }

}