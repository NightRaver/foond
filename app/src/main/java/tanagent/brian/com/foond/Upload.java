package tanagent.brian.com.foond;

import android.app.Activity;
import android.content.Intent;
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

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

import java.io.ByteArrayOutputStream;

/**
 * Created by Brian on 1/31/2016.
 */
public class Upload extends Activity{

    private static final int RESULT_LOAD_IMAGE = 1;
    private ImageView foodImage;
    private Button submitButton, selectImageButton, selectRestaurant;
    private Firebase firebase;

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
                firebase.child("Pictures/").setValue("hey");
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
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == RESULT_LOAD_IMAGE && resultCode == RESULT_OK && data != null) {
            Uri selectedImage = data.getData();
            foodImage.setImageURI(selectedImage);
        }
    }

    // Convert image to Base64 string and push to firebase
    public static String encodeTobase64(Bitmap image) {
        Bitmap immagex=image;
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        immagex.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] b = baos.toByteArray();
        String imageEncoded = Base64.encodeToString(b, Base64.DEFAULT);

        Log.e("LOOK", imageEncoded);
        return imageEncoded;
    }

    // to decode the image
    public static Bitmap decodeBase64(String input) {
        byte[] decodedByte = Base64.decode(input, 0);
        return BitmapFactory.decodeByteArray(decodedByte, 0, decodedByte.length);
    }

    // converts imageview to bitmap
    public static Bitmap convertToBitmap(ImageView imageView) {
        imageView.buildDrawingCache();
        Bitmap bmap = imageView.getDrawingCache();
        return bmap;
    }
}
