package com.apps.yecotec.fridgetracker.data;

import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by kenruizinoue on 9/10/17.
 */

public class SectionContract {

    public static final String AUTHORITY = "com.apps.yecotec.fridgetracker";
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + AUTHORITY);
    public static final String PATH_SECTION = "section";

    public static final class SectionEntry implements BaseColumns {

        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_SECTION).build();

        public static final String TABLE_NAME = "sectionlist";
        public static final String COLUMN_SECTION_NAME = "sectionName";
        public static final String COLUMN_SECTION_COLOR = "sectionColor";

    }

}
