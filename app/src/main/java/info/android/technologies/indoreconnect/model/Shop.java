package info.android.technologies.indoreconnect.model;

/**
 * Created by kamlesh on 12/15/2017.
 */
public class Shop {
    private String shop_image;
    private String shop_name;
    private String shop_address;
    private String shop_contact;
    private String shop_distance;
    private String shop_rating;

    public Shop(String shop_image, String shop_name, String shop_address, String shop_contact, String shop_distance, String shop_rating) {
        this.shop_image = shop_image;
        this.shop_name = shop_name;
        this.shop_address = shop_address;
        this.shop_contact = shop_contact;
        this.shop_distance = shop_distance;
        this.shop_rating = shop_rating;
    }

    public String getShop_image() {
        return shop_image;
    }

    public void setShop_image(String shop_image) {
        this.shop_image = shop_image;
    }

    public String getShop_name() {
        return shop_name;
    }

    public void setShop_name(String shop_name) {
        this.shop_name = shop_name;
    }

    public String getShop_address() {
        return shop_address;
    }

    public void setShop_address(String shop_address) {
        this.shop_address = shop_address;
    }

    public String getShop_contact() {
        return shop_contact;
    }

    public void setShop_contact(String shop_contact) {
        this.shop_contact = shop_contact;
    }

    public String getShop_distance() {
        return shop_distance;
    }

    public void setShop_distance(String shop_distance) {
        this.shop_distance = shop_distance;
    }

    public String getShop_rating() {
        return shop_rating;
    }

    public void setShop_rating(String shop_rating) {
        this.shop_rating = shop_rating;
    }
}
