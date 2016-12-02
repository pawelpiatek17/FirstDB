package com.example.pawe.firstdb;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private List<Integer> viewList = new ArrayList<>();
    public final static SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy/HH:mm");
    public final static String EXTRA_MESSAGE = "com.example.pawe.firstdb.MESSAGE";
    public final static String EDIT_ID = "com.example.pawe.firstdb.ID";
    public final static String EXTRA_PATH = "com.example.pawe.firstdb.PATH";
    public boolean ifEdit;
    DBDbHelper mDbHelper = new DBDbHelper(this);
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        formatter.setLenient(false);
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();
        refreshViev();
    }

    public void addPerson(View view) {
        Intent intent = new Intent(this, AddUserActivity.class);
        ifEdit = false;
        intent.putExtra(EXTRA_MESSAGE,ifEdit);
        startActivity(intent);
        refreshViev();
    }
    public void refreshViev(){
        SQLiteDatabase db = mDbHelper.getReadableDatabase();
        String[] projection = {
                DBContract.DB._ID,
                DBContract.DB.COLUMN_IMIE,
                DBContract.DB.COLUMN_DATE,
                DBContract.DB.COLUMN_WIEK,
                DBContract.DB.COLUMN_IMAGE_PATH
        };
        String sortOrder = DBContract.DB._ID + " DESC";
        final Cursor cursor = db.query(
                DBContract.DB.TABLE__OOSOBY,
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
            RelativeLayout layout = (RelativeLayout)findViewById(R.id.activity_main);
            for (Integer i : viewList) {
                layout.removeView(findViewById(i));
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
                tvImie.setText(cursor.getString(cursor.getColumnIndex(DBContract.DB.COLUMN_IMIE)));
                tvWiek.setText(Integer.toString(cursor.getInt(cursor.getColumnIndex(DBContract.DB.COLUMN_WIEK))));
                tvData.setText(formatter.format(new Date(cursor.getLong(cursor.getColumnIndex(DBContract.DB.COLUMN_DATE)))));
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
                        Cursor c = db.query(DBContract.DB.TABLE__OOSOBY,
                                projection,
                                selection,
                                selectionArgs,
                                null,
                                null,
                                null
                        );
                        c.moveToFirst();
                        String path = c.getString(c.getColumnIndex(DBContract.DB.COLUMN_IMAGE_PATH));
                        db.delete(DBContract.DB.TABLE__OOSOBY,selection,selectionArgs);
                        File f = new File(path);
                        boolean check = f.delete();
                        if (check)
                        {
                            Toast.makeText(MainActivity.this, "ok", Toast.LENGTH_SHORT).show();
                        }
                        refreshViev();
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
                        intent.putExtra(EXTRA_MESSAGE,ifEdit);
                        int id = (Integer) view.getTag();
                        intent.putExtra(EDIT_ID,id);
                        startActivity(intent);
                        refreshViev();
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
                        Intent intent = new Intent(MainActivity.this,ViewImageActivity.class);
                        intent.putExtra(EXTRA_PATH,v.getTag().toString());
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
                layout.addView(tvImie,paramsImie);
                layout.addView(tvWiek,paramsWiek);
                layout.addView(tvData,paramsData);
                layout.addView(btUsun,paramsUsun);
                layout.addView(btEdytuj,paramsEdytuj);
                layout.addView(ibZdj,paramsZdj);



            }
        }
        finally {
            Log.d("resume","close");
            cursor.close();
        }

    }
}
