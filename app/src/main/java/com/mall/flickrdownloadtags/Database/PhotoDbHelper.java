package com.mall.flickrdownloadtags.Database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;



public class PhotoDbHelper extends SQLiteOpenHelper {

    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "FlickrLocalData.db";

    private static final String SQL_CREATE_PHOTOS =
            "CREATE TABLE " + PhotoSchema.TABLE_NAME_PHOTOS + " (" +
                    PhotoSchema.PhotoEntry._ID + " INTEGER PRIMARY KEY," +
                    PhotoSchema.PhotoEntry.COLUMN_NAME_PHOTO_ID + " TEXT," +
                    PhotoSchema.PhotoEntry.COLUMN_NAME_FARM + " INTEGER," +
                    PhotoSchema.PhotoEntry.COLUMN_NAME_OWNER + " TEXT," +
                    PhotoSchema.PhotoEntry.COLUMN_NAME_SECRET + " TEXT," +
                    PhotoSchema.PhotoEntry.COLUMN_NAME_SERVER + " TEXT," +
                    PhotoSchema.PhotoEntry.COLUMN_NAME_TITLE + " TEXT)";

    private static final String SQL_DELETE_PHOTOS =
            "DROP TABLE IF EXISTS " + PhotoSchema.TABLE_NAME_PHOTOS;

    //Table PHOTOSTAGS have a foreign key (photo_id) which references to PHOTOS photo_id.
    private static final String SQL_CREATE_PHOTOSTAGS =
            "CREATE TABLE " + PhotoSchema.TABLE_NAME_PHOTOSTAGS + " (" +
                    PhotoSchema.PhotoTagEntry._ID + " INTEGER PRIMARY KEY," +
                    PhotoSchema.PhotoTagEntry.COLUMN_NAME_PHOTO_ID_PHOTOSTAGS + " TEXT," +
                    PhotoSchema.PhotoTagEntry.COLUMN_NAME_TAG_ID_PHOTOSTAGS + " TEXT," +
                    "FOREIGN KEY ("+PhotoSchema.PhotoTagEntry.COLUMN_NAME_PHOTO_ID_PHOTOSTAGS+
                    ") REFERENCES "+PhotoSchema.TABLE_NAME_PHOTOS+
                    "("+PhotoSchema.PhotoEntry.COLUMN_NAME_PHOTO_ID+")" +
                    ")";

    private static final String SQL_DELETE_PHOTOSTAGS =
            "DROP TABLE IF EXISTS " + PhotoSchema.TABLE_NAME_PHOTOSTAGS;



    public PhotoDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(SQL_CREATE_PHOTOS);
        sqLiteDatabase.execSQL(SQL_CREATE_PHOTOSTAGS);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL(SQL_DELETE_PHOTOS);
        sqLiteDatabase.execSQL(SQL_DELETE_PHOTOSTAGS);

        onCreate(sqLiteDatabase);

    }

    @Override
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }
}
