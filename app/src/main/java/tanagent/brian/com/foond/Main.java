package tanagent.brian.com.foond;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;

import com.amazonaws.auth.CognitoCachingCredentialsProvider;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferListener;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferState;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferUtility;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.S3ObjectSummary;

import java.util.HashMap;
import java.util.List;

/**
 * Created by Brian on 1/30/2016.
 */
public class Main extends AppCompatActivity {

    private GridView gridView;
    private AmazonS3 s3;
    private List<S3ObjectSummary> s3ObjList;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.main);

        // Calls the grid view.
        gridView = (GridView) findViewById(R.id.grid_view);
        gridView.setAdapter(new ImageAdapter(this));

        new GetFileListTask(getApplicationContext()).execute();
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

            // Uncomment this to add features
            // Remember to uncomment the codes in menu.xml

            /*
            case R.id.distance:
                Intent distance = new Intent(this, Distance.class);
                startActivity(distance);
                break;
            case R.id.account:
                Intent account = new Intent(this, Account.class);
                startActivity(account);
                break;
             */
        }

        return super.onOptionsItemSelected(item);
    }
}
