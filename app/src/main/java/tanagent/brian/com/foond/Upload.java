package tanagent.brian.com.foond;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.amazonaws.auth.CognitoCachingCredentialsProvider;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferListener;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferObserver;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferState;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferUtility;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.S3ObjectSummary;
import com.firebase.client.Firebase;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import tanagent.brian.com.foond.Yelp.Yelp;

/**
 * Created by Brian on 1/31/2016.
 */
public class Upload extends Activity{

    private CognitoCachingCredentialsProvider credentialsProvider;
    private AmazonS3 s3;
    // The TransferUtility is the primary class for managing transfer to S3
    private TransferUtility transferUtility;
    private TransferObserver observer;

    private static final int RESULT_LOAD_IMAGE = 1;
    private static final int RESULT_YELP_DATA = 2;
    private static final int CAM_REQUEST = 3;
    private static final String TAG = "Upload";
    private ImageView foodImage;
    private Button submitButton, selectImageButton, selectRestaurant;
    private Button cameryButton;
    private ProgressBar progressBar;
    private TextView restaurantName, restaurantAddress;
    private File imageFile;

    private List<S3ObjectSummary> s3ObjList;

    private String restaurantNameString,
            restaurantAddressString,
            restaurantCityString,
            restaurantImgString,
            restaurantURLString,
            restaurantPhoneString,
            restaurantRatingString,
            fileName;

    private boolean restaurantAvailability;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.upload);

        transferUtility = Util.getTransferUtility(this);

        foodImage = (ImageView) findViewById(R.id.selected_image);

        selectRestaurant = (Button) findViewById(R.id.yelp_restaurants_btn);
        selectRestaurant.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent yelpIntent = new Intent(Upload.this, Yelp.class);
                startActivityForResult(yelpIntent, 2);
            }
        });

        selectImageButton = (Button) findViewById(R.id.gallery_button);
        selectImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent galleryIntent = new Intent();
                if (Build.VERSION.SDK_INT >= 19) {
                    // For Android versions of KitKat or later, we use a
                    // different intent to ensure
                    // we can get the file path from the returned intent URI
                    galleryIntent.setAction(Intent.ACTION_OPEN_DOCUMENT);
                    galleryIntent.addCategory(Intent.CATEGORY_OPENABLE);
                    galleryIntent.putExtra(Intent.EXTRA_LOCAL_ONLY, true);
                } else {
                    galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
                }

                galleryIntent.setType("image/*");
                startActivityForResult(galleryIntent, RESULT_LOAD_IMAGE);
            }
        });

        cameryButton = (Button) findViewById(R.id.camera_button);
        cameryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Intent camera_intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//                File file = getFile();
//                camera_intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(file));
//                startActivityForResult(camera_intent, CAM_REQUEST);

                Intent intent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(intent, CAM_REQUEST);
            }
        });

        submitButton = (Button) findViewById(R.id.submit);
        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (imageFile == null) {
                    Toast.makeText(Upload.this, "Please upload a photo", Toast.LENGTH_LONG).show();
                } else if (restaurantNameString == null || restaurantAddressString == null) {
                    Toast.makeText(Upload.this, "Please select a restaurant", Toast.LENGTH_LONG).show();
                } else {
                    AsyncTask<Void, Integer, Void> task = new AsyncTask<Void, Integer, Void>() {

                        @Override
                        protected void onPreExecute() {
                            Toast.makeText(Upload.this, "Uploading...", Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        protected Void doInBackground(Void... params) {
                            credentialsProvider = new CognitoCachingCredentialsProvider(
                                    getApplicationContext(),
                                    Constants.COGNITO_POOL_ID, // Identity Pool ID
                                    Regions.US_EAST_1 // Region
                            );

                            // Instantiate an S3 Client
                            // Create an S3 client
                            s3 = new AmazonS3Client(credentialsProvider);

                            // Set the region of your s3 bucket
                            s3.setRegion(Region.getRegion(Regions.US_EAST_1));

                            // Instantiate TransferUtility
                            transferUtility = new TransferUtility(s3, getApplicationContext());

                            ObjectMetadata myObjectMetadata = new ObjectMetadata();

                            //create a map to store user metadata
                            Map<String, String> userMetadata = new HashMap<String, String>();
//                            userMetadata.put("MyKey","MyVal");
                            userMetadata.put("name", restaurantNameString);
                            userMetadata.put("address", restaurantAddressString);
                            userMetadata.put("city", restaurantCityString);
                            userMetadata.put("img", restaurantImgString);
                            userMetadata.put("url", restaurantURLString);
                            userMetadata.put("phone", restaurantPhoneString);
                            userMetadata.put("rating", restaurantRatingString);
                            userMetadata.put("avail", String.valueOf(restaurantAvailability));

                            //call setUserMetadata on our ObjectMetadata object, passing it our map
                            myObjectMetadata.setUserMetadata(userMetadata);

                            observer = transferUtility.upload(
                                    Constants.BUCKET_NAME,     /* The bucket to upload to */
//                                    imageFile.getName(),       /* The key for the uploaded object */
                                    fileName,
                                    imageFile,                  /* The file where the data to upload exists */
                                    myObjectMetadata
                            );

                            progressBar = (ProgressBar) findViewById(R.id.progressBar1);

                            observer.setTransferListener(new TransferListener() {
                                @Override
                                public void onStateChanged(int id, TransferState state) {
                                    Log.i("id", String.valueOf(id));
                                    Log.i("TransferState", String.valueOf(state));
                                    if (String.valueOf(state).equals("COMPLETED"))
                                        Toast.makeText(Upload.this, "The photo has been uploaded", Toast.LENGTH_LONG).show();
                                }

                                @Override
                                public void onProgressChanged(int id, long bytesCurrent, long bytesTotal) {
                                    progressBar.setMax((int) bytesTotal);
                                    Integer current = new Integer((int) bytesCurrent);
                                    publishProgress(current);
                                }

                                @Override
                                public void onError(int id, Exception ex) {
                                    Log.e("Error1", String.valueOf(ex));
                                }
                            });

                            Log.i("observer", observer.getState().toString());
                            Log.i("observer", String.valueOf(observer.getBytesTransferred()));

                            return null;
                        }

                        @Override
                        protected void onProgressUpdate(Integer... values) {
                            int progress = values[0];
                            progressBar.setProgress(progress);
                        }
                    };
                    task.execute();
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == RESULT_LOAD_IMAGE && resultCode == RESULT_OK && data != null) {
            Uri selectedImage = data.getData();
            foodImage.setImageURI(selectedImage);

            Drawable mDrawable = foodImage.getDrawable();
            Bitmap mBitmap = drawableToBitmap(mDrawable);

            if (fileName == null || imageFile == null) {
                fileName = UUID.randomUUID().toString() + ".jpg";
                imageFile = new File(getFilesDir(), fileName);
            }

            if(imageFile.exists())
                imageFile.delete();

            try {
                imageFile.createNewFile();
                FileOutputStream out = new FileOutputStream(imageFile);
                mBitmap.compress(Bitmap.CompressFormat.JPEG,100,out);
                out.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        if(requestCode == RESULT_YELP_DATA) {
            if(data != null) {
                // fetch the String
                restaurantNameString = data.getStringExtra("restaurantName");
                restaurantAddressString = data.getStringExtra("restaurantAddress");
                restaurantCityString = data.getStringExtra("restaurantCity");
                restaurantImgString = data.getStringExtra("restaurantImg");
                restaurantURLString = data.getStringExtra("restaurantURL");
                restaurantPhoneString = data.getStringExtra("restaurantPhone");
                restaurantRatingString = data.getStringExtra("restaurantRating");
                restaurantAvailability = data.getBooleanExtra("restaurantAvailability", false);

                restaurantName = (TextView) findViewById(R.id.restaurant_name);
                restaurantAddress = (TextView) findViewById(R.id.restaurant_address);

                if(restaurantNameString != null && restaurantAddressString != null) {
                    restaurantName.setText(restaurantNameString);
                    restaurantAddress.setText(restaurantAddressString);
                }
            }
        }

        if(requestCode == CAM_REQUEST) {

            Bitmap thumbnail = (Bitmap) data.getExtras().get("data");
            foodImage.setImageBitmap(thumbnail);
            ByteArrayOutputStream bytes = new ByteArrayOutputStream();
            thumbnail.compress(Bitmap.CompressFormat.JPEG, 100, bytes);

//            imageFile = new File(Environment.getExternalStorageDirectory()+File.separator + "image.jpg");

            if (fileName == null || imageFile == null) {
                fileName = UUID.randomUUID().toString() + ".jpg";
                imageFile = new File(getFilesDir(), fileName);
            }

            if(imageFile.exists())
                imageFile.delete();

            try {
                imageFile.createNewFile();
                FileOutputStream fo = new FileOutputStream(imageFile);
                fo.write(bytes.toByteArray());
                fo.close();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }

    public static Bitmap drawableToBitmap (Drawable drawable) {
        Bitmap bitmap = null;

        if (drawable instanceof BitmapDrawable) {
            BitmapDrawable bitmapDrawable = (BitmapDrawable) drawable;
            if(bitmapDrawable.getBitmap() != null) {
                return bitmapDrawable.getBitmap();
            }
        }

        if(drawable.getIntrinsicWidth() <= 0 || drawable.getIntrinsicHeight() <= 0) {
            bitmap = Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888); // Single color bitmap will be created of 1x1 pixel
        } else {
            bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        }

        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);
        return bitmap;
    }
}