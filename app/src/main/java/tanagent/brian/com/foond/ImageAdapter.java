package tanagent.brian.com.foond;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.Toast;

import com.amazonaws.auth.CognitoCachingCredentialsProvider;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferListener;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferState;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferUtility;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.S3ObjectSummary;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Brian on 2/3/2016.
 */
public class ImageAdapter extends BaseAdapter {

    private Context context;

    private List<Food> foods = new ArrayList<Food>();
    private LayoutInflater inflater;

    private List<S3ObjectSummary> s3ObjList;
    private CognitoCachingCredentialsProvider credentialsProvider;
    private AmazonS3 s3;


    public ImageAdapter(Context context, Context awsContext) {
        inflater = LayoutInflater.from(context);


        Log.i("arrayList ImageAdapter", String.valueOf(foods).toString());

//        foodImages(awsContext);
        new FileListTask(awsContext).execute();
    }

    public void foodImages(final Context awsContext) {
//        foods.add(new Food("https://s3.amazonaws.com/foond/example/food0.jpg"));
//        foods.add(new Food("https://s3.amazonaws.com/foond/example/food1.jpg"));
//        foods.add(new Food("https://s3.amazonaws.com/foond/example/food2.jpg"));
//        foods.add(new Food("https://s3.amazonaws.com/foond/example/food3.jpg"));
//        foods.add(new Food("https://s3.amazonaws.com/foond/example/food4.jpg"));
//        foods.add(new Food("https://s3.amazonaws.com/foond/example/food5.jpg"));
//        foods.add(new Food("https://s3.amazonaws.com/foond/example/food6.jpg"));
//        foods.add(new Food("https://s3.amazonaws.com/foond/example/food7.jpg"));
//        foods.add(new Food("https://s3.amazonaws.com/foond/example/food8.jpg"));
//
//        Log.i("arrayList2", String.valueOf(foods).toString());
    }

    @Override
    public int getCount() {
        return foods.size();
    }

    @Override
    public Object getItem(int position) {
        return foods.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        View view = convertView;
        ImageView imageView;

        if(view == null) {
            view = inflater.inflate(R.layout.gridview_item, parent, false);
            view.setTag(R.id.picture, view.findViewById(R.id.picture));
        }

        imageView = (ImageView) view.getTag(R.id.picture);

        Food food = (Food) getItem(position);

        final String imageUrl = food.foodId;

        Picasso.with(view.getContext()).load(imageUrl).into(imageView);

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), Select.class);
                intent.putExtra("url", imageUrl);
                v.getContext().startActivity(intent);
                Log.i("url", imageUrl.toString());
            }
        });

        return view;
    }

    private class FileListTask extends AsyncTask<Void, String, Void> {

        private Context context;

        public FileListTask(Context context) {
            this.context = context;
        }

        @Override
        protected synchronized void onPreExecute() {
            foods.add(new Food("https://s3.amazonaws.com/foond/example/food0.jpg"));
            foods.add(new Food("https://s3.amazonaws.com/foond/example/food1.jpg"));
            foods.add(new Food("https://s3.amazonaws.com/foond/example/food2.jpg"));
            foods.add(new Food("https://s3.amazonaws.com/foond/example/food3.jpg"));
            foods.add(new Food("https://s3.amazonaws.com/foond/example/food4.jpg"));
            foods.add(new Food("https://s3.amazonaws.com/foond/example/food5.jpg"));
            foods.add(new Food("https://s3.amazonaws.com/foond/example/food6.jpg"));
            foods.add(new Food("https://s3.amazonaws.com/foond/example/food7.jpg"));
            foods.add(new Food("https://s3.amazonaws.com/foond/example/food8.jpg"));

//            super.onPreExecute();
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
                Log.i("URL", "https://s3.amazonaws.com/" + summary.getBucketName() + "/" + summary.getKey());

                foods.add(new Food("https://s3.amazonaws.com/" + summary.getBucketName() + "/" + summary.getKey()));

                publishProgress("https://s3.amazonaws.com/" + summary.getBucketName() + "/" + summary.getKey());
            }

            Log.i("arrayList3", String.valueOf(foods).toString());

            return null;
        }

        @Override
        protected void onProgressUpdate(String... values) {
            super.onProgressUpdate(values);
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
        }
    }
}
