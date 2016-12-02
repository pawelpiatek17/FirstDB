package com.example.pawe.firstdb;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class AddUserActivity extends AppCompatActivity {
    static final int REQUEST_IMAGE_CAPTURE = 1;
    private String photoPath;
    private String oldPhotoPath;
    Boolean flag;
    DBDbHelper mDbHelper = new DBDbHelper(this);
    SQLiteDatabase db;
    Cursor cursor;
    int id;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_user);
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();
        Intent intent = getIntent();
        flag = intent.getBooleanExtra(MainActivity.EXTRA_MESSAGE,false);
        if(flag){
            db = mDbHelper.getReadableDatabase();
            Button bt = (Button)findViewById(R.id.buttonDodaj);
            bt.setText("Zaktualizuj");
            EditText etImie = (EditText)findViewById(R.id.editTextImie);
            EditText etWiek = (EditText)findViewById(R.id.editTextWiek);
            ImageView ivZdj = (ImageView)findViewById(R.id.imageViewZdj);
            id = intent.getIntExtra(MainActivity.EDIT_ID,0);
            SQLiteDatabase db = mDbHelper.getReadableDatabase();
            String[] projection = {
                    DBContract.DB._ID,
                    DBContract.DB.COLUMN_IMIE,
                    DBContract.DB.COLUMN_WIEK,
                    DBContract.DB.COLUMN_IMAGE_PATH
            };
            String selection = DBContract.DB._ID + " = ?";
            String[] selectionArgs = { Integer.toString(id) };
            String sortOrder = DBContract.DB._ID + " DESC";
            cursor = db.query(
                    DBContract.DB.TABLE__OOSOBY,
                    projection,
                    selection,
                    selectionArgs,
                    null,
                    null,
                    sortOrder
            );
            cursor.moveToFirst();
            Log.d("cursor 1","sss");
            etImie.setText(cursor.getString(cursor.getColumnIndex(DBContract.DB.COLUMN_IMIE)));
            etWiek.setText(Integer.toString(cursor.getInt(cursor.getColumnIndex(DBContract.DB.COLUMN_WIEK))));
            oldPhotoPath = cursor.getString(cursor.getColumnIndex(DBContract.DB.COLUMN_IMAGE_PATH));
            photoPath = oldPhotoPath;
            Bitmap bm = BitmapFactory.decodeFile(cursor.getString(cursor.getColumnIndex(DBContract.DB.COLUMN_IMAGE_PATH)));
            ivZdj.setImageBitmap(bm);

        }
    }

    public void takePhoto(View view) {
        Intent takePhotoIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if(takePhotoIntent.resolveActivity(getPackageManager())!= null){
            File photoFile = null;
            try {
                photoFile = createImageFile();
            }
            catch (IOException ex) {

            }
            if(photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(this,
                        "com.example.android.fileprovider",
                        photoFile);
                takePhotoIntent.putExtra(MediaStore.EXTRA_OUTPUT,photoURI);
                startActivityForResult(takePhotoIntent, REQUEST_IMAGE_CAPTURE);
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK){
            Bitmap bm = BitmapFactory.decodeFile(photoPath);
            ImageView imgV = (ImageView) findViewById(R.id.imageViewZdj);
            imgV.setImageBitmap(bm);
        }
    }

    private File createImageFile() throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir((Environment.DIRECTORY_PICTURES));
        File image = File.createTempFile(
                imageFileName,
                ".jpg",
                storageDir
        );
        photoPath = image.getAbsolutePath();
        return image;
    }
    public void addToDatabase(View view) {
        EditText etImie = (EditText)findViewById(R.id.editTextImie);
        EditText etWiek = (EditText)findViewById(R.id.editTextWiek);
        String imie = etImie.getText().toString();
        String wiek = etWiek.getText().toString();
        Date currentDate = new Date();
        long currentDateInMiliseconds = currentDate.getTime();
        if(TextUtils.isEmpty(imie)){
            etImie.setError("To pole nie może być puste");
        }
        else if(TextUtils.isEmpty(wiek)){
            etWiek.setError("To pole nie może być puste");
        }
        else if (flag){
            db = mDbHelper.getReadableDatabase();
            ContentValues values = new ContentValues();
            values.put(DBContract.DB._ID,id);
            values.put(DBContract.DB.COLUMN_IMIE,imie);
            values.put(DBContract.DB.COLUMN_WIEK, Integer.parseInt(wiek));
            values.put(DBContract.DB.COLUMN_DATE,currentDateInMiliseconds);
            values.put(DBContract.DB.COLUMN_IMAGE_PATH,photoPath);
            String selection = DBContract.DB._ID +" LIKE ?";
            String[] selectionArgs = {Integer.toString(id)};
            db.update(DBContract.DB.TABLE__OOSOBY,values,selection,selectionArgs);
            if(oldPhotoPath != photoPath)
            {
                File f = new File(oldPhotoPath);
                f.delete();
            }
            finish();
        }
        else{
            db = mDbHelper.getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put(DBContract.DB.COLUMN_IMIE,imie);
            values.put(DBContract.DB.COLUMN_WIEK, Integer.parseInt(wiek));
            values.put(DBContract.DB.COLUMN_DATE,currentDateInMiliseconds);
            values.put(DBContract.DB.COLUMN_IMAGE_PATH,photoPath);
            db.insert(DBContract.DB.TABLE__OOSOBY,null,values);
            finish();
        }
    }
}
