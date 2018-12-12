package info.android.technologies.indoreconnect.util;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import info.android.technologies.indoreconnect.model.BuisnessList;
import info.android.technologies.indoreconnect.model.Notification;

import java.util.ArrayList;

/**
 * Created by Win-7 on 5/14/2016.
 */
public class HelperManager {

    Context context;

    SQLiteDatabase db;
    Helper helper;

    public HelperManager(Context context) {
        this.context = context;
        helper = new Helper(context, Helper.DBName, null, Helper.DBVERSION);
    }

    /*Methods for Favourites database*/
    public boolean insert(BuisnessList fav) {

        db = helper.getWritableDatabase();
        ContentValues v = new ContentValues();
        v.put("id", fav.getBuisness_id());
        v.put("status", fav.getStatus());
        v.put("bname", fav.getBuisness_name());
        v.put("building", fav.getBuilding());
        v.put("streer", fav.getStreet());
        v.put("estb", fav.getEstb_year());
        v.put("area", fav.getArea());
        v.put("city", fav.getCity());
        v.put("pincode", fav.getPincode());
        v.put("state", fav.getState());
        v.put("contact_person_title", fav.getContact_person_title());
        v.put("contact_person_name", fav.getContact_person_name());
        v.put("designation", fav.getDesignation());
        v.put("landline1", fav.getLandline_no1());
        v.put("landline2", fav.getLandline_no2());
        v.put("mobile1", fav.getMobile_no1());
        v.put("mobile2", fav.getMobile_no2());
        v.put("email", fav.getEmail_id());
        v.put("website", fav.getWebsite());
        v.put("lat", fav.getLat());
        v.put("longi", fav.getSet_long());
        v.put("image", fav.getImage());
        v.put("rating", fav.getRating());
        v.put("distance", fav.getDistance());
        long isCheck = db.insert("Cart", null, v);
        if (isCheck > 0) {
            return true;
        } else {
            return false;
        }
    }

    public void deleteNotificationAll() {

        db = helper.getWritableDatabase();
        db.execSQL("delete from " + "Notification");

    }

    /*Methods for Favourites database*/
    public void insertNotification(ArrayList<Notification> noti_list) {


        db = helper.getWritableDatabase();
        for (int i = 0; i < noti_list.size(); i++) {

            ContentValues v = new ContentValues();
            v.put("id", noti_list.get(i).getId());
            v.put("msg", noti_list.get(i).getMsg());
            v.put("date", noti_list.get(i).getDate());
            long isCheck = db.insert("Notification", null, v);
        }
    }

    public ArrayList<String> readNotification() {
        db = helper.getReadableDatabase();
        ArrayList<String> noti_list = new ArrayList<>();
        Cursor cursor = db.rawQuery("select * from Notification", null);

        while (cursor.moveToNext()) {
            String id = cursor.getString(0);
            noti_list.add(id);
        }
        return noti_list;
    }

    public ArrayList<String> readOnlyID() {
        db = helper.getReadableDatabase();
        ArrayList<String> cart_list = new ArrayList<>();
        Cursor cursor = db.rawQuery("select * from Cart", null);

        while (cursor.moveToNext()) {
            boolean stock;
            String buisness_id = cursor.getString(0);
//            cart_list.add(new Menu_item(00,id,name,pr,url,"avillable"));
            cart_list.add(buisness_id);
        }
        return cart_list;
    }

    public ArrayList<BuisnessList> read() {
        db = helper.getReadableDatabase();
        ArrayList<BuisnessList> cart_list = new ArrayList<>();
        Cursor cursor = db.rawQuery("select * from Cart", null);

        while (cursor.moveToNext()) {

            String buisness_id = cursor.getString(0);
            String status = cursor.getString(1);
            String buisness_name = cursor.getString(2);
            String building = cursor.getString(3);
            String street = cursor.getString(4);
            String estb_year = cursor.getString(5);
            String area = cursor.getString(6);
            String city = cursor.getString(7);
            String pincode = cursor.getString(8);
            String state = cursor.getString(9);
            String contact_person_title = cursor.getString(10);
            String contact_person_name = cursor.getString(11);
            String designation = cursor.getString(12);
            String landline_no1 = cursor.getString(13);
            String landline_no2 = cursor.getString(14);
            String mobile_no1 = cursor.getString(15);
            String mobile_no2 = cursor.getString(16);
            String email_id = cursor.getString(17);
            String website = cursor.getString(18);
            String lat = cursor.getString(19);
            String set_long = cursor.getString(20);
            String image = cursor.getString(21);
            String rating = cursor.getString(22);
            float distance = cursor.getFloat(23);

            cart_list.add(new BuisnessList(buisness_id, status, buisness_name, building, street, estb_year, area, city, pincode,
                    state, contact_person_title, contact_person_name, designation, landline_no1, landline_no2, mobile_no1, mobile_no2, email_id,
                    website, lat, set_long, image, rating, distance, ""));
        }
        return cart_list;
    }

    public boolean remove(int id) {
        db = helper.getWritableDatabase();
        int isDelete = db.delete("Cart", "id=?", new String[]{id + ""});
        db.close();
        return false;
    }

/*    public void update(int id, String name, float price, String src, int quantity) {

        db = helper.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("id", id);
        cv.put("name", name);
        cv.put("price", price);
        cv.put("image", src);
        cv.put("quantity", quantity);
        db.update("Cart", cv, "id=?", new String[]{id + ""});
    }*/

/*    public int read_by_column(int id) {
        int quantity = 0;
        int id_1;
        Cursor cs;
        db = helper.getReadableDatabase();
        cs = db.rawQuery("select * from cart", new String[]{id + ""});
        while (cs.moveToFirst()) {
            quantity = cs.getColumnIndex("quantity");
//            id_1 = cs.getColumnIndex("id");
        }
        return quantity;
    }*/
}
