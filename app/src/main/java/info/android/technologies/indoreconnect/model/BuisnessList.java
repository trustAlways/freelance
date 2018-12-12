package info.android.technologies.indoreconnect.model;

/**
 * Created by kamlesh on 1/5/2018.
 */
public class BuisnessList {
    private String buisness_id;
    private String status;
    private String buisness_name;
    private String building;
    private String street;
    private String estb_year;
    private String area;
    private String city;
    private String pincode;
    private String state;
    private String contact_person_title;
    private String contact_person_name;
    private String designation;
    private String landline_no1;
    private String landline_no2;
    private String mobile_no1;
    private String mobile_no2;
    private String email_id;
    private String website;
    private String lat;
    private String set_long;
    private String image;
    private String rating;
    private float distance;
    private String cate_icon;

    public BuisnessList(String buisness_id, String status, String buisness_name, String building, String street, String estb_year,
                        String area, String city, String pincode, String state, String contact_person_title, String contact_person_name,
                        String designation, String landline_no1, String landline_no2, String mobile_no1, String mobile_no2, String email_id,
                        String website, String lat, String set_long, String image, String rating, float distance, String cate_icon) {
        this.buisness_id = buisness_id;
        this.status = status;
        this.buisness_name = buisness_name;
        this.building = building;
        this.street = street;
        this.estb_year = estb_year;
        this.area = area;
        this.city = city;
        this.pincode = pincode;
        this.state = state;
        this.contact_person_title = contact_person_title;
        this.contact_person_name = contact_person_name;
        this.designation = designation;
        this.landline_no1 = landline_no1;
        this.landline_no2 = landline_no2;
        this.mobile_no1 = mobile_no1;
        this.mobile_no2 = mobile_no2;
        this.email_id = email_id;
        this.website = website;
        this.lat = lat;
        this.set_long = set_long;
        this.image = image;
        this.rating = rating;
        this.distance = distance;
        this.cate_icon = cate_icon;
    }

    public String getCate_icon() {
        return cate_icon;
    }

    public void setCate_icon(String cate_icon) {
        this.cate_icon = cate_icon;
    }

    public float getDistance() {
        return distance;
    }

    public void setDistance(float distance) {
        this.distance = distance;
    }

    public String getBuisness_id() {
        return buisness_id;
    }

    public void setBuisness_id(String buisness_id) {
        this.buisness_id = buisness_id;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getBuisness_name() {
        return buisness_name;
    }

    public void setBuisness_name(String buisness_name) {
        this.buisness_name = buisness_name;
    }

    public String getBuilding() {
        return building;
    }

    public void setBuilding(String building) {
        this.building = building;
    }

    public String getStreet() {
        return street;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    public String getEstb_year() {
        return estb_year;
    }

    public void setEstb_year(String estb_year) {
        this.estb_year = estb_year;
    }

    public String getArea() {
        return area;
    }

    public void setArea(String area) {
        this.area = area;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getPincode() {
        return pincode;
    }

    public void setPincode(String pincode) {
        this.pincode = pincode;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getContact_person_title() {
        return contact_person_title;
    }

    public void setContact_person_title(String contact_person_title) {
        this.contact_person_title = contact_person_title;
    }

    public String getContact_person_name() {
        return contact_person_name;
    }

    public void setContact_person_name(String contact_person_name) {
        this.contact_person_name = contact_person_name;
    }

    public String getDesignation() {
        return designation;
    }

    public void setDesignation(String designation) {
        this.designation = designation;
    }

    public String getLandline_no1() {
        return landline_no1;
    }

    public void setLandline_no1(String landline_no1) {
        this.landline_no1 = landline_no1;
    }

    public String getLandline_no2() {
        return landline_no2;
    }

    public void setLandline_no2(String landline_no2) {
        this.landline_no2 = landline_no2;
    }

    public String getMobile_no1() {
        return mobile_no1;
    }

    public void setMobile_no1(String mobile_no1) {
        this.mobile_no1 = mobile_no1;
    }

    public String getMobile_no2() {
        return mobile_no2;
    }

    public void setMobile_no2(String mobile_no2) {
        this.mobile_no2 = mobile_no2;
    }

    public String getEmail_id() {
        return email_id;
    }

    public void setEmail_id(String email_id) {
        this.email_id = email_id;
    }

    public String getWebsite() {
        return website;
    }

    public void setWebsite(String website) {
        this.website = website;
    }

    public String getLat() {
        return lat;
    }

    public void setLat(String lat) {
        this.lat = lat;
    }

    public String getSet_long() {
        return set_long;
    }

    public void setSet_long(String set_long) {
        this.set_long = set_long;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getRating() {
        return rating;
    }

    public void setRating(String rating) {
        this.rating = rating;
    }
}
