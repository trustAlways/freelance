package info.android.technologies.indoreconnect.model;

/**
 * Created by kamlesh on 1/26/2018.
 */
public class InfoSubCate {

    private String id;
    private String name;
    private String icon;
    private String parent_icon;

    public InfoSubCate(String id, String name, String icon, String parent_icon) {
        this.id = id;
        this.name = name;
        this.icon = icon;
        this.parent_icon = parent_icon;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public String getParent_icon() {
        return parent_icon;
    }

    public void setParent_icon(String parent_icon) {
        this.parent_icon = parent_icon;
    }
}
