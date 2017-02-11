package com.example.pawe.firstdb;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {
    private ListView listView;
    public final static SimpleDateFormat FORMATTER = new SimpleDateFormat("dd-MM-yyyy");
    public final static String ISEDIT_EXTRA = "com.example.pawe.firstdb.ifedit";
    public final static String EDIT_ID_EXTRA = "com.example.pawe.firstdb.id";
    public final static String OLD_PATH_EXTRA = "com.example.pawe.firstdb.oldpath";
    public final static String PATH_EXTRA = "com.example.pawe.firstdb.path";
    public final static String FIRST_NAME_EXTRA = "com.example.pawe.firstdb.firstname";
    public final static String LAST_NAME_EXTRA = "com.example.pawe.firstdb.lastname";
    public final static String DATE_EXTRA = "com.example.pawe.firstdb.date";
    private final String TAG = "MainActivity";
    private boolean isEdit;
    private long editId;
    private DBDbHelper mDbHelper = new DBDbHelper(this);
    private CustomAdapter customAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        FORMATTER.setLenient(false);
        FloatingActionButton floatingActionButton = (FloatingActionButton) findViewById(R.id.floatingActionButton_changeLang);
        Locale locale = Locale.getDefault();
        Locale enLocale = new Locale("EN");
        Locale plLocale = new Locale("pl");
        if (locale.getLanguage().equals(enLocale.getLanguage())) {
            Log.e(TAG,"onCreate: locale" + locale.getLanguage() + " equals en");
            floatingActionButton.setImageResource(R.drawable.poland);
        } else if (locale.getLanguage().equals(plLocale.getLanguage())){
            Log.e(TAG,"onCreate: locale " + locale.getLanguage() + " equals pl");
            floatingActionButton.setImageResource(R.drawable.unitedkingdom);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        listView = (ListView) findViewById(android.R.id.list);
        listView.setEmptyView(findViewById(android.R.id.empty));
        mDbHelper = new DBDbHelper(this);
        customAdapter = new CustomAdapter(this, getCursorForCustomAdapter());
        listView.setAdapter(customAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //editId = (int) id;
                Log.e(TAG, "onItemClick: " + String.valueOf(id));
            }
        });
        registerForContextMenu(listView);
    }

    @Override
    protected void onResume() {
        super.onResume();
        customAdapter.changeCursor(getCursorForCustomAdapter());

    }

    public void addPerson(View view) {
        Intent intent = new Intent(this, AddUserActivity.class);
        isEdit = false;
        intent.putExtra(ISEDIT_EXTRA, isEdit);
        startActivity(intent);
    }

    private boolean deleteRowById(long rowId) {
        Log.e(TAG, "deleteRowById: " + String.valueOf(rowId));
        SQLiteDatabase db = mDbHelper.getReadableDatabase();
        String[] projection = {
                DBContract.DB._ID,
                DBContract.DB.COLUMN_IMAGE_PATH
        };
        String selection = DBContract.DB._ID + " = ?";
        String[] selectionArgs = {Long.toString(rowId)};
        Cursor cursor = db.query(
                DBContract.DB.TABLE_PERSONS,
                projection,
                selection,
                selectionArgs,
                null,
                null,
                null
        );
        cursor.moveToFirst();
        String imagePath = cursor.getString(cursor.getColumnIndex(DBContract.DB.COLUMN_IMAGE_PATH));
        File file = new File(imagePath);
        file.delete();
        cursor.close();
        db = mDbHelper.getWritableDatabase();
        return db.delete(DBContract.DB.TABLE_PERSONS, selection, selectionArgs) > 0;
    }

    private Cursor getCursorForCustomAdapter() {
        SQLiteDatabase db = mDbHelper.getReadableDatabase();
        String[] projection = {
                DBContract.DB._ID,
                DBContract.DB.COLUMN_FIRSTNAME,
                DBContract.DB.COLUMN_LASTNAME,
                DBContract.DB.COLUMN_DATE,
                DBContract.DB.COLUMN_IMAGE_PATH
        };
        Cursor cursor = db.query(
                DBContract.DB.TABLE_PERSONS,
                projection,
                null,
                null,
                null,
                null,
                null
        );
        return cursor;
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        if (v.getId() == android.R.id.list) {
            AdapterView.AdapterContextMenuInfo adapterContextMenuInfo =
                    (AdapterView.AdapterContextMenuInfo) menuInfo;
            editId = customAdapter.getItemId(adapterContextMenuInfo.position);
            Log.e(TAG, "onCreateContextMenu: " + String.valueOf(editId));
            menu.add(Menu.NONE, 0, Menu.NONE, R.string.edit);
            menu.add(Menu.NONE, 1, Menu.NONE, R.string.delete);
        }
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        int menuItemIndex = item.getItemId();
        if (menuItemIndex == 0) {
            isEdit = true;
            Intent intent = new Intent(this, AddUserActivity.class);
            intent.putExtra(EDIT_ID_EXTRA, editId);
            intent.putExtra(ISEDIT_EXTRA, isEdit);
            startActivity(intent);
        } else if (menuItemIndex == 1) {
            if (deleteRowById(editId)) {
                Toast.makeText(this, R.string.dataDeleted, Toast.LENGTH_SHORT).show();
                customAdapter.changeCursor(getCursorForCustomAdapter());
            } else {
                Toast.makeText(this, "Błąd przy usuwaniu danych", Toast.LENGTH_SHORT).show();
            }
        }
        return super.onContextItemSelected(item);
    }

    public void setLang(String lang) {
        Locale myLocale = new Locale(lang);
        Resources res = getResources();
        DisplayMetrics dm = res.getDisplayMetrics();
        Configuration conf = res.getConfiguration();
        conf.setLocale(myLocale);
        Locale.setDefault(myLocale);
        res.updateConfiguration(conf,dm);
        Intent refresh = new Intent(this,MainActivity.class);
        startActivity(refresh);
        finish();
    }

    public void changeLanguageOnClick(View view) {
        Locale locale = Locale.getDefault();
        Locale enLocale = new Locale("en");
        if (locale.getLanguage().equals(enLocale.getLanguage())) {
            setLang("pl");
        } else {
            setLang("en");
        }
    }

    private class CustomAdapter extends CursorAdapter {
        private LayoutInflater mLayoutInflater;

        public CustomAdapter(Context context, Cursor cursor) {
            super(context, cursor, FLAG_REGISTER_CONTENT_OBSERVER);
            mLayoutInflater = LayoutInflater.from(context);
        }

        @Override
        public View newView(Context context, Cursor cursor, ViewGroup parent) {
            View v = mLayoutInflater.inflate(R.layout.listview_item, parent, false);
            return v;
        }

        @Override
        public void bindView(View v, Context context, Cursor cursor) {
            String firstName = cursor.getString(cursor.getColumnIndex(DBContract.DB.COLUMN_FIRSTNAME));
            String lastName = cursor.getString(cursor.getColumnIndex(DBContract.DB.COLUMN_LASTNAME));
            String date = cursor.getString(cursor.getColumnIndex(DBContract.DB.COLUMN_DATE));
            String imagePath = cursor.getString(cursor.getColumnIndex(DBContract.DB.COLUMN_IMAGE_PATH));

            TextView textViewFirstName = (TextView) v.findViewById(R.id.textView_firstName_listView);
            if (textViewFirstName != null) {
                textViewFirstName.setText(firstName);
            }
            TextView textViewLastName = (TextView) v.findViewById(R.id.textView_lastName_listView);
            if (textViewLastName != null) {
                textViewLastName.setText(lastName);
            }
            TextView textViewDate = (TextView) v.findViewById(R.id.textView_date_listView);
            if (textViewDate != null) {
                textViewDate.setText(date);
            }
            ImageView imageViewPhoto = (ImageView) v.findViewById(R.id.imageView_photo_listView);
            if (imageViewPhoto != null && !imagePath.equals("")) {
                Log.e(TAG, "CustomAdapter : bindView : " + imagePath);
                Glide.with(context).load(imagePath).into(imageViewPhoto);
            } else {
                try {
                    imageViewPhoto.setMinimumHeight(5);
                } catch (NullPointerException e) {
                    Log.e(TAG, "bindView: " + e.toString());
                }
            }
        }
    }
}
