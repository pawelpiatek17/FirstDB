package com.example.pawe.firstdb;

import android.provider.BaseColumns;

/**
 * Created by Paweł on 2016-11-27.
 */

public final class DBContract {
    private DBContract(){}
    public static class DB implements BaseColumns {
        public static final String TABLE__OOSOBY = "osoby";
        public static final String COLUMN_IMIE= "imie";
        public static final String COLUMN_WIEK = "wiek";
        public static final String COLUMN_DATE = "data";
        public static final String COLUMN_IMAGE_PATH = "sciezkaDoZdjecia";


    }
}
