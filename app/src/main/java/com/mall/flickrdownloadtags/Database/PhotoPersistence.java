package com.mall.flickrdownloadtags.Database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Environment;

import com.mall.flickrdownloadtags.Models.Photo;

import java.io.File;
import java.util.ArrayList;
import java.util.List;


public class PhotoPersistence {

    private SQLiteDatabase mDatabase;
    private Context mContext;

    private static PhotoPersistence mPersistence;

    public static PhotoPersistence sharedInstance(Context context){
        if(mPersistence == null){
            mPersistence = new PhotoPersistence(context);
        }
        return mPersistence;
    }

    private PhotoPersistence(Context context){
        mContext = context.getApplicationContext();
        mDatabase = new PhotoDbHelper(context).getWritableDatabase();
    }

    /**
     * Verifies if the photo is at the database
     * @param photo
     */
    public String verifyPhoto(String photo){
        String retorno = "";
        Cursor c = mDatabase.rawQuery("SELECT photo_id FROM "+ PhotoSchema.TABLE_NAME_PHOTOS
                + " WHERE photo_id = "+photo,null);
        if (c.moveToFirst()){
            do {
                // Passing values
                String column1 = c.getString(0);
                retorno = column1;
            } while(c.moveToNext());

        }
        return retorno;
    }

    /**
     * Verifies if the photo has the tag.
     * @param photo
     */
    public String verifyTag(String photo, String tag){
        String retorno = "";
        Cursor c = mDatabase.rawQuery("SELECT photo_id FROM "+ PhotoSchema.TABLE_NAME_PHOTOSTAGS
                + " WHERE photo_id == "+ photo + " AND tag_id = " + "'" + tag + "'",null);
        if (c.moveToFirst()){
            do {
                // Passing values
                String column1 = c.getString(0);
                retorno = column1;
            } while(c.moveToNext());

        }
        return retorno;
    }

    /**
     * Select all photos who contains the tag
     * @param tag
     */
    public ArrayList<Photo> selectPhotoWithTag(String tag){
        ArrayList<Photo> retorno = new ArrayList<Photo>();
        String movies = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MOVIES).getAbsolutePath();

        Cursor c = mDatabase.rawQuery("SELECT photos.photo_id, photos.owner, photos.secret, photos.server, photos.farm, photos.title FROM "
                + PhotoSchema.TABLE_NAME_PHOTOS +
                " INNER JOIN "+ PhotoSchema.TABLE_NAME_PHOTOSTAGS +
                " ON "+PhotoSchema.TABLE_NAME_PHOTOSTAGS+".photo_id = "
                +PhotoSchema.TABLE_NAME_PHOTOS+".photo_id "+
                "WHERE "+PhotoSchema.TABLE_NAME_PHOTOSTAGS+".tag_id = '"+tag+"'",null);

        if (c.moveToFirst()){
            do {
                // Passing values
                Photo photo = new Photo();
                String id = c.getString(0);
                String owner = c.getString(1);
                String secret = c.getString(2);
                String server = c.getString(3);
                int farm = c.getInt(4);
                String title = c.getString(5);

                photo.setId(id);
                photo.setOwner(owner);
                photo.setSecret(secret);
                photo.setServer(server);
                photo.setFarm(farm);
                photo.setTitle(title);
                //Set photo path
                String path = movies + File.separator + id + ".jpg";
                photo.setPath(path);
                retorno.add(photo);

            } while(c.moveToNext());

        }
        return retorno;
    }


    /**
     * Saves a photo locally
     * @param photo
     */
    public void savePhoto(Photo photo, String tag){
        /*ContentValues contentValuesTags = getContentValuesTags(photo, tag);
        mDatabase.insert(PhotoSchema.TABLE_NAME_PHOTOSTAGS, null, contentValuesTags);*/

        ContentValues contentValues = getContentValues(photo);
        mDatabase.insert(PhotoSchema.TABLE_NAME_PHOTOS, null, contentValues);

    }

    public void saveTag(Photo photo, String tag){
        ContentValues contentValuesTags = getContentValuesTags(photo, tag);
        mDatabase.insert(PhotoSchema.TABLE_NAME_PHOTOSTAGS, null, contentValuesTags);

    }


    private static ContentValues getContentValues(Photo photo){
        ContentValues contentValues = new ContentValues();
        contentValues.put(PhotoSchema.PhotoEntry.COLUMN_NAME_PHOTO_ID, photo.getId());
        contentValues.put(PhotoSchema.PhotoEntry.COLUMN_NAME_OWNER, photo.getOwner());
        contentValues.put(PhotoSchema.PhotoEntry.COLUMN_NAME_SECRET, photo.getSecret());
        contentValues.put(PhotoSchema.PhotoEntry.COLUMN_NAME_TITLE, photo.getTitle());
        contentValues.put(PhotoSchema.PhotoEntry.COLUMN_NAME_SERVER, photo.getServer());
        contentValues.put(PhotoSchema.PhotoEntry.COLUMN_NAME_FARM, photo.getFarm());
        return contentValues;
    }


    private static ContentValues getContentValuesTags(Photo photo, String tag){
        ContentValues contentValues = new ContentValues();
        contentValues.put(PhotoSchema.PhotoTagEntry.COLUMN_NAME_PHOTO_ID_PHOTOSTAGS, photo.getId());
        contentValues.put(PhotoSchema.PhotoTagEntry.COLUMN_NAME_TAG_ID_PHOTOSTAGS, tag);
        return contentValues;
    }
}
