package tanagent.brian.com.foond;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ContentUris;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
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
import com.amazonaws.services.s3.model.S3ObjectSummary;
import com.firebase.client.Firebase;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

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
    private static final String TAG = "Upload";
    private ImageView foodImage;
    private Button submitButton, selectImageButton, selectRestaurant;
    private Firebase firebase;
    private File imageFile;

    private List<S3ObjectSummary> s3ObjList;

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
                startActivity(yelpIntent);
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


        submitButton = (Button) findViewById(R.id.submit);
        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(imageFile == null) {
                    Toast.makeText(Upload.this, "Please upload a photo first", Toast.LENGTH_LONG).show();


                } else {
                    AsyncTask<Void, Void, Void> task = new AsyncTask<Void, Void, Void>() {

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

                            //Upload a file to amazon s3
                            Log.i("imageFile", imageFile.toString());

                            observer = transferUtility.upload(
                                    "foond",     /* The bucket to upload to */
                                    imageFile.getName(),    /* The key for the uploaded object */
                                    imageFile        /* The file where the data to upload exists */
                            );

                            Log.i("observer", observer.getAbsoluteFilePath());
                            Log.i("observer", observer.toString());
                            Log.i("observer", observer.getState().toString());
                            Log.i("observer", String.valueOf(observer.getBytesTransferred()));

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
                                    Log.i("id", String.valueOf(id));
                                    Log.i("bytesCurrent", String.valueOf(bytesCurrent));
                                    Log.i("bytesTotal", String.valueOf(bytesTotal));
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

            imageFile = new File(getFilesDir(), "asdf.jpg");
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

    private String getRealPathFromURI(Uri contentURI) {
        String result;
        Cursor cursor = getContentResolver().query(contentURI, null, null, null, null);
        if (cursor == null) { // Source is Dropbox or other similar local file path
            result = contentURI.getPath();
        } else {
            cursor.moveToFirst();
            int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
            result = cursor.getString(idx);
            cursor.close();
        }
        return result;
    }
}