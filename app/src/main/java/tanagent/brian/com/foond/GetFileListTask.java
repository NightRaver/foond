package tanagent.brian.com.foond;

/**
 * Created by Brian on 3/14/2016.
 */

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.amazonaws.auth.CognitoCachingCredentialsProvider;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.S3ObjectSummary;

import java.util.HashMap;
import java.util.List;

/**
 * This async task queries S3 for all files in the given bucket so that they
 * can be displayed on the screen
 */
public class GetFileListTask extends AsyncTask<Void, Void, Void> {

    // The list of objects we find in the S3 bucket
    private List<S3ObjectSummary> s3ObjList;
    private CognitoCachingCredentialsProvider credentialsProvider;
    private AmazonS3 s3;
    private Context context;

    public GetFileListTask(Context context) {
        this.context = context;
    }

    @Override
    protected Void doInBackground(Void... params) {

        credentialsProvider = new CognitoCachingCredentialsProvider(
                context,
                "us-east-1:ac620376-ff60-4580-b750-17bd70ef228d", // Identity Pool ID
                Regions.US_EAST_1 // Region
        );

        // Instantiate an S3 Client
        // Create an S3 client
        s3 = new AmazonS3Client(credentialsProvider);

        // Set the region of your s3 bucket
        s3.setRegion(Region.getRegion(Regions.US_EAST_1));

        s3ObjList = s3.listObjects(Constants.BUCKET_NAME).getObjectSummaries();

        for (S3ObjectSummary summary : s3ObjList) {
            Log.i("key", summary.getKey().toString());
            Log.i("bucketname", summary.getBucketName());
            Log.i("etag", summary.getETag());
        }

        return null;
    }
}
