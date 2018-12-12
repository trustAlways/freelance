package info.android.technologies.indoreconnect.model;

/**
 * Created by kamlesh on 1/15/2018.
 */
public class Favourites {
    private String id;
    private String title;
    private String address;
    private String contact;
    private String lati;
    private String longi;
    private String image;

    public Favourites(String id, String title, String address, String contact, String lati, String longi, String image) {
        this.id = id;
        this.title = title;
        this.address = address;
        this.contact = contact;
        this.lati = lati;
        this.longi = longi;
        this.image = image;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
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

    public String getLati() {
        return lati;
    }

    public void setLati(String lati) {
        this.lati = lati;
    }

    public String getLongi() {
        return longi;
    }

    public void setLongi(String longi) {
        this.longi = longi;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }
}
