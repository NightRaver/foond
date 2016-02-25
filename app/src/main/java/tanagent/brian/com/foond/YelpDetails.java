package tanagent.brian.com.foond;

/**
 * Created by Brian on 2/24/2016.
 */
public class YelpDetails {

    private int photoId;
    private String restaurantName;
    private String address;

    public YelpDetails(int photoId, String restaurantName, String address) {
        this.photoId = photoId;
        this.restaurantName = restaurantName;
        this.address = address;
    }

    public int getPhotoId() {
        return photoId;
    }

    public void setPhotoId(int photoId) {
        this.photoId = photoId;
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
