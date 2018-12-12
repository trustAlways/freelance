package info.android.technologies.indoreconnect.model;

/**
 * Created by kamlesh on 1/14/2018.
 */
public class Notification {
    private String id;
    private String msg;
    private String date;

    public Notification(String id, String msg, String date) {
        this.id = id;
        this.msg = msg;
        this.date = date;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
