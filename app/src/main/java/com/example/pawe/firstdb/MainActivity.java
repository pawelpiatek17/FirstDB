package com.example.pawe.firstdb;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
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
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class MainActivity extends AppCompatActivity {
    private List<Integer> viewList = new ArrayList<>();
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
    DBDbHelper mDbHelper = new DBDbHelper(this);
    CustomAdapter customAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        FORMATTER.setLenient(false);
    }

    @Override
    protected void onStart() {
        super.onStart();
        listView = (ListView)findViewById(android.R.id.list);
        listView.setEmptyView(findViewById(android.R.id.empty));
        mDbHelper = new DBDbHelper(this);
        customAdapter = new CustomAdapter(this, getCursorForCustomAdapter());
        listView.setAdapter(customAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //editId = (int) id;
                Log.e(TAG,"onItemClick: " + String.valueOf(id));
            }
        });
        registerForContextMenu(listView);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    public void addPerson(View view) {
        Intent intent = new Intent(this, AddUserActivity.class);
        isEdit = false;
        intent.putExtra(ISEDIT_EXTRA,isEdit);
        startActivity(intent);
    }

    public boolean deleteRowById(long rowId) {
        Log.e(TAG,"deleteRowById: " + String.valueOf(rowId));
        SQLiteDatabase db = mDbHelper.getReadableDatabase();
        String[] projection = {
                DBContract.DB._ID,
                DBContract.DB.COLUMN_IMAGE_PATH
        };
        String selection = DBContract.DB._ID + " = ?";
        String[] selectionArgs = { Long.toString(rowId) };
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
        db = mDbHelper.getWritableDatabase();
        return db.delete(DBContract.DB.TABLE_PERSONS, selection,selectionArgs) > 0;
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
        if(v.getId() == android.R.id.list) {
            AdapterView.AdapterContextMenuInfo adapterContextMenuInfo =
                    (AdapterView.AdapterContextMenuInfo) menuInfo;
            editId = customAdapter.getItemId(adapterContextMenuInfo.position);
            Log.e(TAG,"onCreateContextMenu: " + String.valueOf(editId));
            //TODO tlumaczenie edytuj i usun
            menu.add(Menu.NONE, 0, Menu.NONE, "Edytuj");
            menu.add(Menu.NONE, 1, Menu.NONE, "Usuń");
        }
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info =
                (AdapterView.AdapterContextMenuInfo)item.getMenuInfo();
        int menuItemIndex = item.getItemId();
        if (menuItemIndex == 0) {
            isEdit = true;
            Intent intent = new Intent(this,AddUserActivity.class);
            intent.putExtra(EDIT_ID_EXTRA,editId);
            intent.putExtra(ISEDIT_EXTRA,isEdit);
            startActivity(intent);
        }
        else if (menuItemIndex == 1) {
            //TODO tlumaczenie
            if (deleteRowById(editId)) {
                Toast.makeText(this,"Dane usunięte" , Toast.LENGTH_SHORT).show();
                customAdapter.changeCursor(getCursorForCustomAdapter());
            }
            else {
                Toast.makeText(this, "Błąd przy usuwaniu danych", Toast.LENGTH_SHORT).show();
            }
        }
        return super.onContextItemSelected(item);
    }

    private class CustomAdapter extends CursorAdapter {
        private LayoutInflater mLayoutInflater;
        public CustomAdapter(Context context, Cursor cursor) {
            super(context, cursor,FLAG_REGISTER_CONTENT_OBSERVER);
            mLayoutInflater = LayoutInflater.from(context);
        }

        @Override
        public View newView(Context context, Cursor cursor, ViewGroup parent) {
            View v = mLayoutInflater.inflate(R.layout.listview_item, parent, false);
            return v;
        }

        /**
         * @author will
         *
         * @param   v
         *          The view in which the elements we set up here will be displayed.
         *
         * @param   context
         *          The running context where this ListView adapter will be active.
         *
         * @param   cursor
         *          The Cursor containing the query results we will display.
         */

        @Override
        public void bindView(View v, Context context, Cursor cursor) {
            String firstName = cursor.getString(cursor.getColumnIndex(DBContract.DB.COLUMN_FIRSTNAME));
            String lastName = cursor.getString(cursor.getColumnIndex(DBContract.DB.COLUMN_LASTNAME));
            String date = cursor.getString(cursor.getColumnIndex(DBContract.DB.COLUMN_DATE));
            String imagePath = cursor.getString(cursor.getColumnIndex(DBContract.DB.COLUMN_IMAGE_PATH));

            /**
             * Next set the title of the entry.
             */

            TextView textViewFirstName = (TextView) v.findViewById(R.id.textView_firstName);
            if (textViewFirstName != null) {
                textViewFirstName.setText(firstName);
            }
            TextView textViewLastName = (TextView) v.findViewById(R.id.textView_lastName);
            if (textViewLastName != null) {
                textViewLastName.setText(lastName);
            }
            TextView textViewDate = (TextView) v.findViewById(R.id.textView_date);
            if (textViewDate != null) {
                textViewDate.setText(date);
            }
            ImageView imageViewPhoto = (ImageView) v.findViewById(R.id.imageView_photo_listView);
            if (imageViewPhoto != null && !Objects.equals(imagePath, "")) {
                Log.e(TAG,"CustomAdapter : bindView : "+imagePath);
                Glide.with(context).load(imagePath).into(imageViewPhoto);
            } else {
                try {
                    imageViewPhoto.setMinimumHeight(5);
                } catch (NullPointerException e) {
                    Log.e(TAG,"bindView: " + e.toString());
                }
            }
        }
    }
     /*public void refreshView(){
        SQLiteDatabase db = mDbHelper.getReadableDatabase();
        String[] projection = {
                DBContract.DB._ID,
                DBContract.DB.COLUMN_FIRSTNAME,
                DBContract.DB.COLUMN_DATE,
                DBContract.DB.COLUMN_LASTNAME,
                DBContract.DB.COLUMN_IMAGE_PATH
        };
        String sortOrder = DBContract.DB._ID + " DESC";
        final Cursor cursor = db.query(
                DBContract.DB.TABLE_PERSONS,
                projection,
                null,
                null,
                null,
                null,
                sortOrder
        );
        Log.d("resume","przed try");
        try {
            int tvI = 0;
            int btI = 1000;
            int dtI = 1500;
            int ibI = 1600;
            LinearLayout linearLayout = (LinearLayout)findViewById(R.id.activity_main);
            for (Integer i : viewList) {
                linearLayout.removeView(findViewById(i));
            }
            while (cursor.moveToNext()){
                Log.d("resume","while 1");
                TextView tvImie = new TextView(this);
                TextView tvWiek = new TextView(this);
                TextView tvData = new TextView(this);
                Button btEdytuj = new Button(this);
                Button btUsun   = new Button(this);
                Button ibZdj    = new Button(this);
                Log.d("resume","set text");
                tvImie.setText(cursor.getString(cursor.getColumnIndex(DBContract.DB.COLUMN_FIRSTNAME)));
                tvWiek.setText(Integer.toString(cursor.getInt(cursor.getColumnIndex(DBContract.DB.COLUMN_LASTNAME))));
                tvData.setText(FORMATTER.format(new Date(cursor.getLong(cursor.getColumnIndex(DBContract.DB.COLUMN_DATE)))));
                btUsun.setText("U");
                btUsun.setTag(cursor.getInt(cursor.getColumnIndex(DBContract.DB._ID)));
                btUsun.setMinHeight(40);
                btUsun.setMinWidth(40);
                btUsun.setMinimumHeight(40);
                btUsun.setMinimumWidth(40);
                Log.d("resume ID",Integer.toString(cursor.getInt(cursor.getColumnIndex(DBContract.DB._ID))));
                btUsun.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View viev) {
                        SQLiteDatabase db = mDbHelper.getWritableDatabase();
                        Log.d("delete",viev.getTag().toString());
                        int id = (Integer) viev.getTag();
                        String selection = DBContract.DB._ID + " LIKE ?";
                        String[] selectionArgs = {Integer.toString(id)};
                        String[] projection = {
                                DBContract.DB.COLUMN_IMAGE_PATH
                        };
                        Cursor c = db.query(DBContract.DB.TABLE_PERSONS,
                                projection,
                                selection,
                                selectionArgs,
                                null,
                                null,
                                null
                        );
                        c.moveToFirst();
                        String path = c.getString(c.getColumnIndex(DBContract.DB.COLUMN_IMAGE_PATH));
                        db.delete(DBContract.DB.TABLE_PERSONS,selection,selectionArgs);
                        File f = new File(path);
                        boolean check = f.delete();
                        if (check)
                        {
                            Toast.makeText(MainActivity.this, "ok", Toast.LENGTH_SHORT).show();
                        }
                        refreshView();
                    }
                });
                btEdytuj.setText("E");
                btEdytuj.setTag(cursor.getInt(cursor.getColumnIndex(DBContract.DB._ID)));
                btEdytuj.setMinHeight(40);
                btEdytuj.setMinWidth(40);
                btEdytuj.setMinimumHeight(40);
                btEdytuj.setMinimumWidth(40);
                btEdytuj.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Log.d("edit","ed");
                        Intent intent = new Intent(MainActivity.this, AddUserActivity.class);
                        ifEdit = true;
                        intent.putExtra(ISEDIT_EXTRA,ifEdit);
                        int id = (Integer) view.getTag();
                        intent.putExtra(EDIT_ID_EXTRA,id);
                        startActivity(intent);
                        refreshView();
                    }
                });
               // ibZdj.setBackground(getDrawable(R.drawable.ic_photo_black_24dp));
                ibZdj.setText("Z");
                ibZdj.setMinHeight(40);
                ibZdj.setMinWidth(40);
                ibZdj.setMinimumHeight(40);
                ibZdj.setMinimumWidth(40);
                String path = cursor.getString(cursor.getColumnIndex(DBContract.DB.COLUMN_IMAGE_PATH));
                ibZdj.setTag(path);
                ibZdj.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(MainActivity.this,AddImageActivity.class);
                        intent.putExtra(PATH_EXTRA,v.getTag().toString());
                        startActivity(intent);
                    }
                });
                tvI += 1;
                tvImie.setId(tvI);
                tvI += 1;
                tvWiek.setId(tvI);
                dtI += 1;
                tvData.setId(dtI);
                btI += 1;
                btUsun.setId(btI);
                btI += 1;
                btEdytuj.setId(btI);
                ibI +=1;
                ibZdj.setId(ibI);
                Log.d("resume","Params");
                RelativeLayout.LayoutParams paramsImie = new RelativeLayout.LayoutParams(
                        ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT);
                RelativeLayout.LayoutParams paramsWiek = new RelativeLayout.LayoutParams(
                        ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT);
                RelativeLayout.LayoutParams paramsEdytuj = new RelativeLayout.LayoutParams(
                        ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT);
                RelativeLayout.LayoutParams paramsUsun = new RelativeLayout.LayoutParams(
                        ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT);
                RelativeLayout.LayoutParams paramsData = new RelativeLayout.LayoutParams(
                        ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT);
                RelativeLayout.LayoutParams paramsZdj = new RelativeLayout.LayoutParams(
                        ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT);
                //paramsZdj.width = 100;
                //paramsZdj.height = 100;
                Log.d("resume","przed if tvI" + tvI);
                Log.d("resume","przed if" + btI);
                if(tvI>2){
                    Log.d("resume","if 1");
                    paramsImie.addRule(RelativeLayout.BELOW,tvI-3);
                    paramsImie.topMargin = 60;
                    paramsWiek.addRule(RelativeLayout.BELOW,tvI-2);
                    paramsWiek.addRule(RelativeLayout.ALIGN_START,tvI-2);
                    paramsWiek.topMargin = 60;
                    paramsData.addRule(RelativeLayout.BELOW,dtI-1);
                    paramsData.addRule(RelativeLayout.ALIGN_START,dtI-1);
                    paramsData.topMargin = 60;
                    paramsUsun.addRule(RelativeLayout.BELOW,btI-3);
                    paramsUsun.addRule(RelativeLayout.ALIGN_START,btI-3);
                    paramsUsun.addRule(RelativeLayout.ALIGN_BASELINE,tvI-1);
                    paramsEdytuj.addRule(RelativeLayout.BELOW,btI-2);
                    paramsEdytuj.addRule(RelativeLayout.START_OF,btI-1);
                    paramsEdytuj.addRule(RelativeLayout.ALIGN_BASELINE,tvI-1 );
                    paramsZdj.addRule(RelativeLayout.BELOW,ibI-1);
                    paramsZdj.addRule(RelativeLayout.ALIGN_START,ibI-1);
                    paramsZdj.addRule(RelativeLayout.ALIGN_BASELINE,tvI-1);
                }
                else{
                    Log.d("resume","else");
                    paramsImie.addRule(RelativeLayout.BELOW,R.id.textViewImie);
                    paramsImie.topMargin = 40;
                    paramsWiek.addRule(RelativeLayout.BELOW,R.id.textViewWiek);
                    paramsWiek.addRule(RelativeLayout.ALIGN_START,R.id.textViewWiek);
                    paramsWiek.topMargin = 40;
                    paramsData.addRule(RelativeLayout.BELOW,R.id.textViewData);
                    paramsData.addRule(RelativeLayout.ALIGN_START,R.id.textViewData);
                    paramsData.topMargin = 40;
                    paramsUsun.addRule(RelativeLayout.ALIGN_PARENT_RIGHT,RelativeLayout.TRUE);
                    paramsUsun.addRule(RelativeLayout.ALIGN_BASELINE,1);
                    paramsEdytuj.addRule(RelativeLayout.START_OF,1001);
                    paramsEdytuj.addRule(RelativeLayout.ALIGN_BASELINE,1001);
                    paramsZdj.addRule(RelativeLayout.START_OF,1002);
                    paramsZdj.addRule(RelativeLayout.ALIGN_BASELINE,1001);
                    //paramsZdj.addRule(RelativeLayout.ALIGN_TOP,1001);
                    Log.d("aa",R.id.textViewImie + " " + R.id.textViewWiek);
                }
                //layout.requestLayout();
                viewList.add(tvImie.getId());
                viewList.add(tvWiek.getId());
                viewList.add(tvData.getId());
                viewList.add(btUsun.getId());
                viewList.add(btEdytuj.getId());
                viewList.add(ibZdj.getId());
                linearLayout.addView(tvImie,paramsImie);
                linearLayout.addView(tvWiek,paramsWiek);
                linearLayout.addView(tvData,paramsData);
                linearLayout.addView(btUsun,paramsUsun);
                linearLayout.addView(btEdytuj,paramsEdytuj);
                linearLayout.addView(ibZdj,paramsZdj);



            }
        }
        finally {
            Log.d("resume","close");
            cursor.close();
        }

    }
    */
}

