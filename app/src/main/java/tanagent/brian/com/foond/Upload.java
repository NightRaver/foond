package tanagent.brian.com.foond;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.amazonaws.auth.CognitoCachingCredentialsProvider;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferObserver;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferUtility;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

import java.io.ByteArrayOutputStream;
import java.io.File;

/**
 * Created by Brian on 1/31/2016.
 */
public class Upload extends Activity{

    private static final int RESULT_LOAD_IMAGE = 1;
    private ImageView foodImage;
    private Button submitButton, selectImageButton, selectRestaurant;
    private Firebase firebase;
    private File imageFile;

    private Button exampleButton; // for firebase

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.upload);

        // initialize Firebase
        firebase.setAndroidContext(this);
        firebase = new Firebase("https://amber-heat-9533.firebaseio.com/");
        firebase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
//                String example = (String) dataSnapshot.getValue();
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });


//        // Converting to Bitmap
//        Bitmap bitFood = convertToBitmap(foodImage);
//
//        // Converting it to Base64
//        String baseFood = encodeTobase64(bitFood);

        exampleButton = (Button) findViewById(R.id.example);
        exampleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                firebase.child("Pictures/").setValue("bye");
            }
        });

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
                Intent galleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(galleryIntent, RESULT_LOAD_IMAGE);
            }
        });


        submitButton = (Button) findViewById(R.id.submit);
        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(Upload.this, "The photo has been uploaded", Toast.LENGTH_LONG).show();

                new Thread() {
                    @Override
                    public void run() {
//                        super.run();

                        // Initialize the Amazon Cognito credentials provider
                        CognitoCachingCredentialsProvider credentialsProvider = new CognitoCachingCredentialsProvider(
                                getApplicationContext(),
                                "us-east-1:c32aa5d6-c50f-4195-8d9a-dc047a86707d", // Identity Pool ID
                                Regions.US_EAST_1 // Region
                        );


                        // Instantiate an S3 Client
                        // Create an S3 client
                        AmazonS3 s3 = new AmazonS3Client(credentialsProvider);

                        // Set the region of your s3 bucket
                        s3.setRegion(Region.getRegion(Regions.DEFAULT_REGION));

                        // Instantiate TransferUtility
                        TransferUtility transferUtility = new TransferUtility(s3, getApplicationContext());

                        //Upload a file to amazon s3

                        TransferObserver observer = transferUtility.upload(
                                "/foond",     /* The bucket to upload to */
                                "key",    /* The key for the uploaded object */
                                imageFile        /* The file where the data to upload exists */
                        );
                    }
                }.start();
            }

        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == RESULT_LOAD_IMAGE && resultCode == RESULT_OK && data != null) {
            Uri selectedImage = data.getData();
            foodImage.setImageURI(selectedImage);
            imageFile = new File(getRealPathFromURI(selectedImage));
        }
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
