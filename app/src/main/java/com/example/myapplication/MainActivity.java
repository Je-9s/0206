package com.example.myapplication;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {
    private ListView lvPasswords;
    private TextView tvEmpty;
    private DatabaseHelper dbHelper;
    private String username;
    private int userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        lvPasswords = findViewById(R.id.lvPasswords);
        tvEmpty = findViewById(R.id.tvEmpty);
        dbHelper = new DatabaseHelper(this);

        username = getIntent().getStringExtra("USERNAME");
        userId = dbHelper.getUserId(username);

        loadPasswords();

        lvPasswords.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(MainActivity.this, PasswordDetailActivity.class);
                intent.putExtra("PASSWORD_ID", id);
                startActivity(intent);
            }
        });
    }

    private void loadPasswords() {
        Cursor cursor = dbHelper.getPasswords(userId);

        if (cursor.getCount() == 0) {
            tvEmpty.setVisibility(View.VISIBLE);
            lvPasswords.setVisibility(View.GONE);
        } else {
            tvEmpty.setVisibility(View.GONE);
            lvPasswords.setVisibility(View.VISIBLE);

            String[] from = new String[]{DatabaseHelper.COLUMN_SERVICE, DatabaseHelper.COLUMN_LOGIN};
            int[] to = new int[]{R.id.tvService, R.id.tvLogin};

            SimpleCursorAdapter adapter = new SimpleCursorAdapter(this,
                    R.layout.password_list_item,
                    cursor,
                    from,
                    to,
                    0);

            lvPasswords.setAdapter(adapter);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.menu_add) {
            Intent intent = new Intent(this, AddPasswordActivity.class);
            intent.putExtra("USER_ID", userId);
            startActivity(intent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadPasswords();
    }
}