package info.android.technologies.indoreconnect.model;

/**
 * Created by kamlesh on 2/2/2018.
 */
public class AllBuissness {
    private String id;
    private String type;
    private String image;
    private String name;
    private String address;
    private String contact;
    private double lati;
    private double longi;
    private float rating;
    private double distance;
    private String parent_icon;

    public AllBuissness(String id, String type, String image, String name, String address, String contact, double lati, double longi, float rating, double distance, String parent_icon) {
        this.id = id;
        this.type = type;
        this.image = image;
        this.name = name;
        this.address = address;
        this.contact = contact;
        this.lati = lati;
        this.longi = longi;
        this.rating = rating;
        this.distance = distance;
        this.parent_icon = parent_icon;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getContact() {
        return contact;
    }

    public void setContact(String contact) {
        this.contact = contact;
    }

    public double getLati() {
        return lati;
    }

    public void setLati(double lati) {
        this.lati = lati;
    }

    public double getLongi() {
        return longi;
    }

    public void setLongi(double longi) {
        this.longi = longi;
    }

    public float getRating() {
        return rating;
    }

    public void setRating(float rating) {
        this.rating = rating;
    }

    public double getDistance() {
        return distance;
    }

    public void setDistance(double distance) {
        this.distance = distance;
    }

    public String getParent_icon() {
        return parent_icon;
    }

    public void setParent_icon(String parent_icon) {
        this.parent_icon = parent_icon;
    }
}

