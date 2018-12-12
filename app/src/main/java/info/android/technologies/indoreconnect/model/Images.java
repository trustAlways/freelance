package info.android.technologies.indoreconnect.model;

/**
 * Created by kamlesh on 1/5/2018.
 */
public class Images
{
    private String type;
    private String image;

    public Images(String type, String image) {
        this.type = type;
        this.image = image;
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
}
