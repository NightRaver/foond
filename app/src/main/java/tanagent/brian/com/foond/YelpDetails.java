package tanagent.brian.com.foond;

import java.io.File;
import java.net.URL;

/**
 * Created by Brian on 2/24/2016.
 */
public class YelpDetails {

//    private int photoId;
    private String restaurantImage;
    private String restaurantName;
    private String address;

    public YelpDetails(String restaurantImage, String restaurantName, String address) {
        this.restaurantImage = restaurantImage;
        this.restaurantName = restaurantName;
        this.address = address;
    }

//    public int getPhotoId() {
//        return photoId;
//    }
//
//    public void setPhotoId(int photoId) {
//        this.photoId = photoId;
//    }

    public String getRestaurantImage() {
        return restaurantImage;
    }

    public void setRestaurantImage(String restaurantImage) {
        this.restaurantImage = restaurantImage;
    }

    public String getRestaurantName() {
        return restaurantName;
    }

    public void setRestaurantName(String restaurantName) {
        this.restaurantName = restaurantName;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }
}
