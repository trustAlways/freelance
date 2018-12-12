package info.android.technologies.indoreconnect.model;

/**
 * Created by kamlesh on 12/7/2017.
 */
public class Navigation
{
    private int image;
    private String name;

    public Navigation(int image, String name) {
        this.image = image;
        this.name = name;
    }

    public int getImage() {
        return image;
    }

    public void setImage(int image) {
        this.image = image;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
