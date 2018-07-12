package com.mall.flickrdownloadtags.Database;

import android.provider.BaseColumns;


public class PhotoSchema {
    public static final String TABLE_NAME_PHOTOS = "photos";
    public static class PhotoEntry implements BaseColumns {
        public static final String COLUMN_NAME_PHOTO_ID = "photo_id";
        public static final String COLUMN_NAME_OWNER = "owner";
        public static final String COLUMN_NAME_SECRET = "secret";
        public static final String COLUMN_NAME_SERVER = "server";
        public static final String COLUMN_NAME_FARM = "farm";
        public static final String COLUMN_NAME_TITLE = "title";
    }

    public static final String TABLE_NAME_PHOTOSTAGS = "photostags";
    public static class PhotoTagEntry implements BaseColumns {
        public static final String COLUMN_NAME_PHOTO_ID_PHOTOSTAGS = "photo_id";
        public static final String COLUMN_NAME_TAG_ID_PHOTOSTAGS = "tag_id";
    }


}
