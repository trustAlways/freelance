package info.android.technologies.indoreconnect.model;

/**
 * Created by kamlesh on 12/2/2017.
 */
public class InfoCate {
    private String parent_cate;
    private String sub_cate;
    private String cate_icon;
    private String cate_id;

    public InfoCate(String parent_cate, String sub_cate, String cate_icon, String cate_id) {
        this.parent_cate = parent_cate;
        this.sub_cate = sub_cate;
        this.cate_icon = cate_icon;
        this.cate_id = cate_id;
    }

    public String getParent_cate() {
        return parent_cate;
    }

    public void setParent_cate(String parent_cate) {
        this.parent_cate = parent_cate;
    }

    public String getSub_cate() {
        return sub_cate;
    }

    public void setSub_cate(String sub_cate) {
        this.sub_cate = sub_cate;
    }

    public String getCate_icon() {
        return cate_icon;
    }

    public void setCate_icon(String cate_icon) {
        this.cate_icon = cate_icon;
    }

    public String getCate_id() {
        return cate_id;
    }

    public void setCate_id(String cate_id) {
        this.cate_id = cate_id;
    }
}
