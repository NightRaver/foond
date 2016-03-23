package tanagent.brian.com.foond.Yelp;

import java.io.File;
import java.net.URL;

/**
 * Created by Brian on 2/24/2016.
 */
public class YelpDetails {

    private String restaurantImage;
    private String restaurantName;
    private String restaurantAddress;
    private String restaurantCity;
    private String restaurantURL;
    private String restaurantPhone;
    private String restaurantRating;
    private boolean restaurantAvailability;

    public YelpDetails(String restaurantImage, String restaurantName,
                       String restaurantAddress, String restaurantCity,
                       String restaurantURL, String restaurantPhone,
                       String restaurantRating, boolean restaurantAvailability) {

        this.restaurantImage = restaurantImage;
        this.restaurantName = restaurantName;
        this.restaurantAddress = restaurantAddress;
        this.restaurantCity = restaurantCity;
        this.restaurantURL = restaurantURL;
        this.restaurantPhone = restaurantPhone;
        this.restaurantRating = restaurantRating;
        this.restaurantAvailability = restaurantAvailability;
    }

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

    public String getRestaurantAddress() {
        return restaurantAddress;
    }

    public void setRestaurantAddress(String address) {
        this.restaurantAddress = restaurantAddress;
    }

    public String getRestaurantCity() {
        return restaurantCity;
    }

    public void setRestaurantCity(String restaurantCity) {
        this.restaurantCity = restaurantCity;
    }

    public String getRestaurantPhone() {
        return restaurantPhone;
    }

    public void setRestaurantPhone(String restaurantPhone) {
        this.restaurantPhone = restaurantPhone;
    }

    public String getRestaurantRating() {
        return restaurantRating;
    }

    public void setRestaurantRating(String restaurantRating) {
        this.restaurantRating = restaurantRating;
    }

    public boolean isRestaurantAvailability() {
        return restaurantAvailability;
    }

    public void setRestaurantAvailability(boolean restaurantAvailability) {
        this.restaurantAvailability = restaurantAvailability;
    }

    public String getRestaurantURL() {

        return restaurantURL;
    }

    public void setRestaurantURL(String restaurantURL) {
        this.restaurantURL = restaurantURL;
    }
}
