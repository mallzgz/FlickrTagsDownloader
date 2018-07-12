package com.mall.flickrdownloadtags.Activities;

import android.app.Activity;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.BottomNavigationView.OnNavigationItemSelectedListener;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.widget.Toast;
import java.io.*;
import java.lang.reflect.Array;
import java.net.URL;
import butterknife.ButterKnife;
import android.os.Environment;
import android.os.StrictMode;
import android.Manifest;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.content.Context;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;



import com.android.volley.Request.Method;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.mall.flickrdownloadtags.Database.PhotoPersistence;
import com.mall.flickrdownloadtags.ViewFragments.GalleryViewFragment;
import com.mall.flickrdownloadtags.Interfaces.OnPhotoSearchRequestListener;
import com.mall.flickrdownloadtags.Models.Photo;
import com.mall.flickrdownloadtags.R;
import com.mall.flickrdownloadtags.Singletons.VolleyRequestQueueService;
import com.mall.flickrdownloadtags.ViewFragments.LoadingFragment;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class MainActivity extends AppCompatActivity implements OnPhotoSearchRequestListener {

    private static final String CURRENT_PAGE = "CURRENT_PAGE";
    private static final String CURRENT_QUERY = "CURRENT_QUERY";
    private static final String CURRENT_PHOTOS = "CURRENT_PHOTOS";

//    private ArrayList<Photo> currentPhotos;


    private String searchQuery = "zaragoza";
    private String tag = "";
    private String urlString;

    private FragmentManager mManager;

    private GalleryViewFragment mGalleryViewFragment;

    private BottomNavigationView navigation;

    private boolean esLocal = false;
    private LoadingFragment loadingFragment;
    // Storage Permissions
    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);


        mManager = getSupportFragmentManager();

        navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        //save the page the user was viewing, and their most recent search query
        if(savedInstanceState != null){
            if(savedInstanceState.containsKey(CURRENT_PAGE)){
                navigation.setSelectedItemId(savedInstanceState.getInt(CURRENT_PAGE));
            }
            if(savedInstanceState.containsKey(CURRENT_QUERY)){
                searchQuery = savedInstanceState.getString(CURRENT_QUERY);
            }
        }


        //always show discover page.

            mGalleryViewFragment = (GalleryViewFragment) mManager.findFragmentByTag(GalleryViewFragment.TAG);

            if (mGalleryViewFragment == null) {
                mGalleryViewFragment = new GalleryViewFragment();

                mManager.beginTransaction()
                        .replace(R.id.container, mGalleryViewFragment, GalleryViewFragment.TAG)
                        .commit();

            }

            loadingFragment = new LoadingFragment(this, "Loading images");

    }

    @Override
    protected void onStart() {
        super.onStart();

        //when we load the view, show the photos.
        tag = searchQuery;
        urlString = getString(R.string.flickr_url, searchQuery);
        onPhotoSearchRequest(urlString);

    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(CURRENT_PAGE, navigation.getSelectedItemId());
        outState.putString(CURRENT_QUERY, searchQuery);
    }

    private OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {

                //navigate user to discover page
                case R.id.navigation_home:
                    esLocal = false;
                    mGalleryViewFragment = (GalleryViewFragment) mManager.findFragmentByTag(GalleryViewFragment.TAG);

                    if (mGalleryViewFragment == null) {
                        mGalleryViewFragment = new GalleryViewFragment();

                        mManager.beginTransaction()
                                .replace(R.id.container, mGalleryViewFragment, GalleryViewFragment.TAG)
                                .commit();

                        onPhotoSearchRequest(searchQuery);
                    }

                    return true;


                case R.id.navigation_home_local:
                    esLocal = true;
                    mGalleryViewFragment = (GalleryViewFragment) mManager.findFragmentByTag(GalleryViewFragment.TAG);

                    if (mGalleryViewFragment == null) {
                        mGalleryViewFragment = new GalleryViewFragment();

                        mManager.beginTransaction()
                                .replace(R.id.container, mGalleryViewFragment, GalleryViewFragment.TAG)
                                .commit();

                        onPhotoSearchRequest(searchQuery);
                    }
                    return true;
            }
            return false;
        }

    };

    /**
     * OnPhotoSearchRequestListener Methods
     */

    @Override
    public void onPhotoSearchRequest(final String searchQuery) {
        if (esLocal == false){
            if(searchQuery != null) {
                if (searchQuery.startsWith("http")) {
                    int startIndex = searchQuery.lastIndexOf("&tags=");
                    int endIndex = searchQuery.lastIndexOf("&format=");
                    tag = searchQuery.substring(startIndex, endIndex).replace("&tags=", "");

                } else {
                    tag = searchQuery;
                }
            }
            else{
                if (this.searchQuery.startsWith("http")) {
                    int startIndex = this.searchQuery.lastIndexOf("&tags=");
                    int endIndex = this.searchQuery.lastIndexOf("&format=");
                    tag = this.searchQuery.substring(startIndex, endIndex).replace("&tags=", "");

                } else {
                    tag = this.searchQuery;
                }
            }

            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
            //searches flickr for photos matching query user selected
            // final String tag = searchQuery;

            loadingFragment.show();

            if (searchQuery != null && !searchQuery.equals("")) {
                this.searchQuery = searchQuery;
            }

            String urlString = getString(R.string.flickr_url, this.searchQuery);

            JsonObjectRequest request = new JsonObjectRequest(Method.GET, urlString, null,
                    new Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            try {
                                JSONObject photosJSON = response.getJSONObject("photos");
                                JSONArray photosJSONArray = photosJSON.getJSONArray("photo");

                                ArrayList<Photo> photos = new ArrayList<>();


                                photos = new ArrayList<>();
                                ArrayList<Photo> photoIds = new ArrayList<Photo>();
                                ArrayList<Photo> photoIdsRemaining = new ArrayList<Photo>();

                                for (int i = 0; i < photosJSONArray.length(); i++) {
                                    photos.add(new Photo((photosJSONArray.getJSONObject(i))));
                                    photoIds.add(new Photo((photosJSONArray.getJSONObject(i))));
                                }

                                //At this point, we have 25 photos with its data.
                                //First, we need to go to the database in order to see if these images are in our database.
                                String movies = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MOVIES).getAbsolutePath();
                                for (int i = 0; i < photoIds.size(); i++) {
                                    String photo = photoIds.get(i).getId();
                                    String idExiste = PhotoPersistence.sharedInstance(getApplicationContext()).verifyPhoto(photo);
                                    if (idExiste.equals("")) {
                                        //it's not at the database. We have to go to the image to API again.
                                        photoIdsRemaining.add(photoIds.get(i));
                                    }

                                    //If the photo is at the database, we are going to insert the tag at the table "tags" in order to save it.
                                    String tagExiste = PhotoPersistence.sharedInstance(getApplicationContext()).verifyTag(photo, tag);
                                    if (tagExiste.equals("")) {
                                        //The tag is not saved, we save it.
                                        PhotoPersistence.sharedInstance(getApplicationContext()).saveTag(photoIds.get(i), tag);

                                    }
                                }

                                //Now we have all the photos which are not at the database. We must go to the API and get all the data.
                                for (int i = 0; i < photoIdsRemaining.size(); i++) {
                                    final Photo photo = photoIdsRemaining.get(i);

                                    String urlString = "https://farm" + photo.getFarm() + ".staticflickr.com/" +
                                            photo.getServer() + "/" + photo.getId() + "_" +
                                            photo.getSecret() + ".jpg";
                                    String destinationFile = photo.getId();
                                    //@Override
                                    //public void run() {
                                    try {
                                        saveImage( urlString, destinationFile);

                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }

                                    //In this point, we have saved the remaining images.
                                    //Now, we have to save that images at the database.


                                }
                                for (int i = 0; i < photoIdsRemaining.size(); i++) {
                                    final Photo photo = photoIdsRemaining.get(i);
                                    PhotoPersistence.sharedInstance(MainActivity.this).savePhoto(photo, tag);
                                    //Now we have saved the photo, we can save the tag linked to it.
                                    String tagExiste = PhotoPersistence.sharedInstance(getApplicationContext()).verifyTag(photo.getId(), tag);
                                    if (tagExiste.equals("")) {
                                        //The tag is not saved, we save it.
                                        PhotoPersistence.sharedInstance(getApplicationContext()).saveTag(photo, tag);

                                    }
                                }
                                //If we have the path of the image, we send it to the gallery in order to show the local image,
                                //not the online one.
                                //pass loaded photos to gallery fragment for display+
                                // If we are saving now the images, we try to show the online ones, because of the time problems of saving
                                // and showing at the same time.
                                if (mGalleryViewFragment != null && mGalleryViewFragment.isVisible()) {

                                    for (int i = 0; i < photos.size(); i++) {
                                        String path = movies + File.separator + photos.get(i).getId() + ".jpg";
                                        File f = new File(path);
                                        if (f.exists() && !f.isDirectory()) {
                                            //We show the local image
                                            photos.get(i).setPath(path);
                                        }
                                    }

                                    mGalleryViewFragment.setPhotos(photos);
                                }
                                loadingFragment.dismissWithDelay(500);

                            } catch (JSONException e) {
                                e.printStackTrace();
                                loadingFragment.dismissWithDelay(500);
                            }
                        }
                    }, new ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    //display error to user if no photos found that match their search query
                    //also clear any photos displayed in gallery from last query
                    loadingFragment.dismiss();
                    Toast.makeText(MainActivity.this, "unable to find photos", Toast.LENGTH_SHORT).show();
                    if (mGalleryViewFragment != null && mGalleryViewFragment.isVisible()) {
                        mGalleryViewFragment.setPhotos(null);
                    }
                }
            });

            VolleyRequestQueueService.getInstance(this).addToRequestQueue(request);
        }

        else{
            //We only go to local database
            try{
                if(searchQuery != null) {
                    if (searchQuery.startsWith("http")) {
                        int startIndex = searchQuery.lastIndexOf("&tags=");
                        int endIndex = searchQuery.lastIndexOf("&format=");
                        tag = searchQuery.substring(startIndex, endIndex).replace("&tags=", "");

                    } else {
                        tag = searchQuery;
                    }
                }
                else{
                    if (this.searchQuery.startsWith("http")) {
                        int startIndex = this.searchQuery.lastIndexOf("&tags=");
                        int endIndex = this.searchQuery.lastIndexOf("&format=");
                        tag = this.searchQuery.substring(startIndex, endIndex).replace("&tags=", "");

                    } else {
                        tag = this.searchQuery;
                    }
                }
                loadingFragment.show();
                ArrayList<Photo> photos = new ArrayList<>();

                //Select images from database and path from local storage
                ArrayList<Photo> photoLocalList = PhotoPersistence.sharedInstance(getApplicationContext()).selectPhotoWithTag(tag);

                //pass local loaded photos to gallery fragment for display
                if (mGalleryViewFragment != null && mGalleryViewFragment.isVisible()) {

                    mGalleryViewFragment.setPhotos(photoLocalList);
                }
                loadingFragment.dismissWithDelay(500);
            }
            catch(Exception e){
                //display error to user if we dont find photos
                //also, we clear the gallery
                loadingFragment.dismiss();
                Toast.makeText(MainActivity.this, "unable to find local photos", Toast.LENGTH_SHORT).show();
                if (mGalleryViewFragment != null && mGalleryViewFragment.isVisible()) {
                    mGalleryViewFragment.setPhotos(null);
                }
            }


        }


    }


    public static void saveImage(String imageUrl, String destinationFile) throws IOException {
        URL url = new URL(imageUrl);
        InputStream input = url.openStream();
        try {
            File storagePath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MOVIES);
            if(!storagePath.exists()){
                storagePath.mkdirs();
            }
            OutputStream output = new FileOutputStream (storagePath +"/" + destinationFile +".jpg");
            try {
                byte[] buffer = new byte[256];
                int bytesRead = 0;
                while ((bytesRead = input.read(buffer, 0, buffer.length)) >= 0) {
                    output.write(buffer, 0, bytesRead);
                }
            }
            finally {
                output.close();
            }
        }

        finally {
            input.close();
        }
    }



    public static void verifyStoragePermissions(Activity activity) {
        // Check if we have write permission
        int permission = ActivityCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE);

        if (permission != PackageManager.PERMISSION_GRANTED) {
            // We don't have permission so prompt the user
            ActivityCompat.requestPermissions(
                    activity,
                    PERMISSIONS_STORAGE,
                    REQUEST_EXTERNAL_STORAGE
            );
        }
    }
}
