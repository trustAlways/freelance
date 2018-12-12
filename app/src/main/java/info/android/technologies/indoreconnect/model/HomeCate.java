package info.android.technologies.indoreconnect.model;

/**
 * Created by kamlesh on 12/15/2017.
 */
public class HomeCate {
    private String category_name;
    private String category_id;
    private String subcate_name;
    private String subcate_id;
    private String subcate_image;

    public HomeCate(String category_name, String category_id, String subcate_name, String subcate_id, String subcate_image) {
        this.category_name = category_name;
        this.category_id = category_id;
        this.subcate_name = subcate_name;
        this.subcate_id = subcate_id;
        this.subcate_image = subcate_image;
    }

    public String getCategory_name() {
        return category_name;
    }

    public void setCategory_name(String category_name) {
        this.category_name = category_name;
    }

    public String getCategory_id() {
        return category_id;
    }

    public void setCategory_id(String category_id) {
        this.category_id = category_id;
    }

    public String getSubcate_name() {
        return subcate_name;
    }

    public void setSubcate_name(String subcate_name) {
        this.subcate_name = subcate_name;
    }

    public String getSubcate_id() {
        return subcate_id;
    }

    public void setSubcate_id(String subcate_id) {
        this.subcate_id = subcate_id;
    }

    public String getSubcate_image() {
        return subcate_image;
    }

    public void setSubcate_image(String subcate_image) {
        this.subcate_image = subcate_image;
    }
}
