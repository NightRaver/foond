package com.example.brian.foond;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.Toast;

/**
 * Created by Brian on 1/30/2016.
 */
public class Main extends AppCompatActivity{

    private GridView gridView;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.main);

        // Calls the grid view.
        gridView = (GridView) findViewById(R.id.grid_view);
        gridView.setAdapter(new ImageAdapter(this));

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(Main.this, Select.class);
                Log.i("TAG", "Hard code: " + R.drawable.sample_0);
                Log.i("TAG", "Position: " + position);
                Log.i("TAG", "ID: " + id);
                intent.putExtra("foodId", (int) id);
                startActivity(intent);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.upload:
                Intent upload = new Intent(this, Upload.class);
                startActivity(upload);
                break;
            case R.id.distance:
                Intent distance = new Intent(this, Distance.class);
                startActivity(distance);
                break;
            case R.id.account:
                Intent account = new Intent(this, Account.class);
                startActivity(account);
                break;
        }

        return super.onOptionsItemSelected(item);
    }
}
