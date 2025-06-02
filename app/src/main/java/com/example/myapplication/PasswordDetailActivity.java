package com.example.myapplication;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class PasswordDetailActivity extends AppCompatActivity {
    private TextView tvService, tvLogin, tvPassword, tvNotes;
    private DatabaseHelper dbHelper;
    private long passwordId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_password_detail);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        tvService = findViewById(R.id.tvService);
        tvLogin = findViewById(R.id.tvLogin);
        tvPassword = findViewById(R.id.tvPassword);
        tvNotes = findViewById(R.id.tvNotes);

        dbHelper = new DatabaseHelper(this);
        passwordId = getIntent().getLongExtra("PASSWORD_ID", -1);

        loadPasswordDetails();
    }

    private void loadPasswordDetails() {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.query(
                DatabaseHelper.TABLE_PASSWORDS,
                new String[]{
                        DatabaseHelper.COLUMN_SERVICE,
                        DatabaseHelper.COLUMN_LOGIN,
                        DatabaseHelper.COLUMN_PASSWORD_VALUE,
                        DatabaseHelper.COLUMN_NOTES
                },
                DatabaseHelper.COLUMN_PASSWORD_ID + "=?",
                new String[]{String.valueOf(passwordId)},
                null, // groupBy
                null, // having
                null  // orderBy
        );
        if (cursor != null && cursor.moveToFirst()) {
            tvService.setText(cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_SERVICE)));
            tvLogin.setText(cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_LOGIN)));
            tvPassword.setText(cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_PASSWORD_VALUE)));
            tvNotes.setText(cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_NOTES)));
            cursor.close();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}