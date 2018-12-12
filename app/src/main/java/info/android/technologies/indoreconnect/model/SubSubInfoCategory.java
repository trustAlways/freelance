package info.android.technologies.indoreconnect.model;

/**
 * Created by kamlesh on 1/5/2018.
 */
public class SubSubInfoCategory {
    private String title;
    private String icon;
    private String type;
    private String contact;
    private String address;
    private String discription;
    private String location;
    private double latitude;
    private double longitude;
    private double distance;
    private String alt_contact;
    private String category_icon;

    public SubSubInfoCategory(String title, String icon, String type, String contact, String address, String discription, String location, double latitude, double longitude, double distance, String alt_contact, String category_icon) {
        this.title = title;
        this.icon = icon;
        this.type = type;
        this.contact = contact;
        this.address = address;
        this.discription = discription;
        this.location = location;
        this.latitude = latitude;
        this.longitude = longitude;
        this.distance = distance;
        this.alt_contact = alt_contact;
        this.category_icon = category_icon;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getContact() {
        return contact;
    }

    public void setContact(String contact) {
        this.contact = contact;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getDiscription() {
        return discription;
    }

    public void setDiscription(String discription) {
        this.discription = discription;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public double getDistance() {
        return distance;
    }

    public void setDistance(double distance) {
        this.distance = distance;
    }

    public String getAlt_contact() {
        return alt_contact;
    }

    public void setAlt_contact(String alt_contact) {
        this.alt_contact = alt_contact;
    }

    public String getCategory_icon() {
        return category_icon;
    }

    public void setCategory_icon(String category_icon) {
        this.category_icon = category_icon;
    }
}
