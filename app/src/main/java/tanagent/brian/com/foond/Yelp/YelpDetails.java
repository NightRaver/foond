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

    public YelpDetails(String restaurantImage, String restaurantName,
                       String restaurantAddress, String restaurantCity) {
        this.restaurantImage = restaurantImage;
        this.restaurantName = restaurantName;
        this.restaurantAddress = restaurantAddress;
        this.restaurantCity = restaurantCity;
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
}
