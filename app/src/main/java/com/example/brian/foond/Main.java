package com.example.brian.foond;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

/**
 * Created by Brian on 1/30/2016.
 */
public class Main extends AppCompatActivity{
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.main);

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
//                Toast.makeText(getBaseContext(), "Upload", Toast.LENGTH_LONG).show();
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
