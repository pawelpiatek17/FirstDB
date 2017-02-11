package com.example.pawe.firstdb;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;

import java.util.Calendar;
import java.util.Date;

public class AddUserActivity extends AppCompatActivity {
    private final String TAG = "AddUserActivity";
    Boolean flag;
    DBDbHelper mDbHelper = new DBDbHelper(this);
    EditText editTextFirstName;
    EditText editTextLastName;
    EditText editTextDate;
    DatePickerDialog datePickerDialog;
    int day;
    int month;
    int year;
    boolean isDateChanged = false;
    SQLiteDatabase db;
    Intent intent;
    Cursor cursor;
    Long id;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_user);
        intent = getIntent();
        flag = intent.getBooleanExtra(MainActivity.ISEDIT_EXTRA,false);
        if(flag){
            db = mDbHelper.getReadableDatabase();
            Button bt = (Button)findViewById(R.id.button_addUser);
            bt.setText("Zaktualizuj");
            editTextFirstName = (EditText)findViewById(R.id.editText_firstName);
            editTextLastName = (EditText)findViewById(R.id.editText_lastName);
            editTextDate = (EditText) findViewById(R.id.editText_date);
            id = intent.getLongExtra(MainActivity.EDIT_ID_EXTRA,-1);
            SQLiteDatabase db = mDbHelper.getReadableDatabase();
            String[] projection = {
                    DBContract.DB._ID,
                    DBContract.DB.COLUMN_FIRSTNAME,
                    DBContract.DB.COLUMN_LASTNAME,
                    DBContract.DB.COLUMN_DATE
            };
            String selection = DBContract.DB._ID + " = ?";
            String[] selectionArgs = { Long.toString(id) };
            String sortOrder = DBContract.DB._ID + " DESC";
            cursor = db.query(
                    DBContract.DB.TABLE_PERSONS,
                    projection,
                    selection,
                    selectionArgs,
                    null,
                    null,
                    sortOrder
            );
            cursor.moveToFirst();
            editTextFirstName.setText(cursor.getString(cursor.getColumnIndex(DBContract.DB.COLUMN_FIRSTNAME)));
            editTextLastName.setText(cursor.getString(cursor.getColumnIndex(DBContract.DB.COLUMN_LASTNAME)));
            editTextDate.setText((cursor.getString(cursor.getColumnIndex(DBContract.DB.COLUMN_DATE))));
            String [] date = editTextDate.getText().toString().split("-");
            day = Integer.parseInt(date[0]);
            month = Integer.parseInt(date[1])-1;
            year = Integer.parseInt(date[2]);
            datePickerDialog = new DatePickerDialog(this,onDateSetListener,year,month,day);
            isDateChanged = true;
        }
        else {
            datePickerDialog = new DatePickerDialog(this, onDateSetListener, 2016, 0, 1);
        }
        editTextDate = (EditText) findViewById(R.id.editText_date);
        editTextDate.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    datePickerDialog.show();
                }
            }
        });
    }
    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();

    }

    private DatePickerDialog.OnDateSetListener onDateSetListener = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker view, int _year, int _month, int dayOfMonth) {
            year = _year;
            month = _month;
            day = dayOfMonth;
            editTextDate.setText(String.valueOf(day) + "-" + String.valueOf(month+1) + "-" + String.valueOf(year));
            isDateChanged = true;
        }
    };

    public void continueClick(View view) {
        editTextFirstName = (EditText) findViewById(R.id.editText_firstName);
        editTextLastName = (EditText) findViewById(R.id.editText_lastName);
        String firstName = editTextFirstName.getText().toString();
        String lastName = editTextLastName.getText().toString();
        String date = editTextDate.getText().toString();
        if(TextUtils.isEmpty(firstName)){
            //TODO zmienic na string resources (inne jezyki)
            editTextFirstName.setError("To pole nie może być puste");
        }
        else if(TextUtils.isEmpty(lastName)){
            //TODO zmienic na string resources (inne jezyki)
            editTextLastName.setError("To pole nie może być puste");
        }
        else if (!isDateChanged) {
            editTextDate.setError("Ustaw datę urodzenia");
        }
        else{
            Intent _intent = new Intent(this,AddImageActivity.class);
            _intent.putExtra(MainActivity.FIRST_NAME_EXTRA,firstName);
            _intent.putExtra(MainActivity.LAST_NAME_EXTRA,lastName);
            _intent.putExtra(MainActivity.DATE_EXTRA,date);
            _intent.putExtra(MainActivity.ISEDIT_EXTRA,intent.getBooleanExtra(MainActivity.ISEDIT_EXTRA,false));
            _intent.putExtra(MainActivity.EDIT_ID_EXTRA,intent.getLongExtra(MainActivity.EDIT_ID_EXTRA,-1));
            startActivity(_intent);
        }
    }
}
