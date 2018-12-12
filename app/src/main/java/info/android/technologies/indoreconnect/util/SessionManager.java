package info.android.technologies.indoreconnect.util;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import info.android.technologies.indoreconnect.activity.MainActivity;

import java.util.HashMap;

/**
 * Created by kamlesh on 11/25/2017.
 */
public class SessionManager {
    SharedPreferences pref;
    SharedPreferences.Editor editor;
    Context ctx;

    int PRIVATE_MODE = 0;
    private static final String PREF_NAME = "sessionmanager";
    private static final String IS_LOGIN = "IsLoggedIn";
    public static final String SUBSUBINFOCATE = "subsubinfocate";

    public static final String KEY_FNAME = "name";
    public static final String KEY_MOBILE = "mobile";
    public static final String KEY_EMAIL = "email";
    public static final String KEY_PKEY_CATEGORY_NAMEAGE = "page";
    public static final String KEY_BANNER = "banner";
    public static final String KEY_PAGE = "category";
    public static final String KEY_MAIN_CATEGORY_NAME = "main_category_name";

    public static final String KEY_LAT = "latitude";
    public static final String KEY_LONG = "longitude";
    public static final String KEY_OTP = "otp";
    public static final String KEY_CREATE_DATE = "date";
    public static final String KEY_SEARCH_DATA = "search_data";

    public static final String KEY_SEARCH_ID = "search_id";
    public static final String KEY_SEARCH_TYPE = "search_type";
    public static final String KEY_SEARCH_FROM = "search_from";
    public static final String KEY_NAV_VIEW_TYPE = "viewtype";

    // INFO SUB DATA
    public static final String KEY_SUB_ID = "subid";
    // info sub SUB data
    public static final String KEY_SUB_SUB_ID = "subsubid";
    public static final String KEY_SUB_SUB_ICON = "subsubicon";

    // buisness id
    public static final String KEY_BUISNESS_ID = "buisnessid";

    // all buissness listing
    public static final String KEY_MAIN_BUISNESS_ID = "mainbuisnessid";
    public static final String KEY_ALL_BUISNESS_TYPE = "allbiiss_type";

    // searching data
    public static final String KEY_SEARCH_TEXT = "searchtext";

    public SessionManager(Context context) {
        this.ctx = context;
        pref = ctx.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        editor = pref.edit();
    }

    public void createLoginSession(String name, String mobile, String email) {
        editor.putString(KEY_FNAME, name);
        editor.putString(KEY_MOBILE, mobile);
        editor.putString(KEY_EMAIL, email);
        editor.putBoolean(IS_LOGIN, true);
        editor.commit();
    }

    public void setboolean(String key, boolean value) {
        editor.putBoolean(key, value);
        editor.commit();
    }


    public void setData(String key, String value) {
        editor.putString(key, value);
        editor.commit();
    }

    public void setDouble(String key, double value) {

        editor.putLong(key, Double.doubleToRawLongBits(value));
        editor.commit();
    }

    public double getDouble(String key) {
        return Double.longBitsToDouble(pref.getLong(key, Double.doubleToLongBits(0)));
    }

    public void checkLogin() {
        if (!this.isLoggedIn()) {
            Intent i = new Intent(ctx, MainActivity.class);
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            ctx.startActivity(i);
        }
    }

    public HashMap<String, String> getUserDetails() {
        HashMap<String, String> user = new HashMap<String, String>();
        user.put(KEY_FNAME, pref.getString(KEY_FNAME, null));
        user.put(KEY_EMAIL, pref.getString(KEY_EMAIL, null));
        return user;
    }

    public void logoutUser() {
        editor.clear();
        editor.commit();
        Intent i = new Intent(ctx, MainActivity.class);
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        ctx.startActivity(i);
    }

    public boolean isLoggedIn() {
        return pref.getBoolean(IS_LOGIN, false);
    }

    public boolean getboolean(String key) {
        return pref.getBoolean(key, false);
    }

    public String getData(String key) {
        return pref.getString(key, null);
    }
}
