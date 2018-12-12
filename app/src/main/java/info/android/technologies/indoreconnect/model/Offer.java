package info.android.technologies.indoreconnect.model;

/**
 * Created by kamlesh on 1/4/2018.
 */
public class Offer {
    private String category;
    private String image;
    private String link;

    public Offer(String category, String image, String link) {
        this.category = category;
        this.image = image;
        this.link = link;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }
}
