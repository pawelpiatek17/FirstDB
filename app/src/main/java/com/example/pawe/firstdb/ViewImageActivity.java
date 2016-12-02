package com.example.pawe.firstdb;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

public class ViewImageActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_image);
        Intent intent = getIntent();
        Bitmap bm = BitmapFactory.decodeFile(intent.getStringExtra(MainActivity.EXTRA_PATH));
        ImageView iv = (ImageView)findViewById(R.id.imageViewPhoto);
        iv.setImageBitmap(bm);
    }

    public void Back(View view) {
        finish();
    }
}
