package info.android.technologies.indoreconnect.activity;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import info.android.technologies.indoreconnect.R;
import info.android.technologies.indoreconnect.adapter.NavAdapter;
import info.android.technologies.indoreconnect.adapter.NotificationAdapter;
import info.android.technologies.indoreconnect.fragment.AboutUsFragment;
import info.android.technologies.indoreconnect.fragment.AllBuisnessListingFragment;
import info.android.technologies.indoreconnect.fragment.ContactUsFragment;
import info.android.technologies.indoreconnect.fragment.FavouritsFragment;
import info.android.technologies.indoreconnect.fragment.FeedbackFragment;
import info.android.technologies.indoreconnect.fragment.Home2Fragment;
import info.android.technologies.indoreconnect.fragment.InfoFragment;
import info.android.technologies.indoreconnect.fragment.InfoSubCateFragment;
import info.android.technologies.indoreconnect.fragment.MyListFragment;
import info.android.technologies.indoreconnect.fragment.NotificationFragment;
import info.android.technologies.indoreconnect.fragment.OfferFragment;
import info.android.technologies.indoreconnect.fragment.Search2Fragment;
import info.android.technologies.indoreconnect.fragment.SearchFragment;
import info.android.technologies.indoreconnect.fragment.SeeAllCategoryHomeFragment;
import info.android.technologies.indoreconnect.fragment.SubSubInfoCategoryFragment;
import info.android.technologies.indoreconnect.model.BuisnessList;
import info.android.technologies.indoreconnect.model.Navigation;
import info.android.technologies.indoreconnect.model.Notification;
import info.android.technologies.indoreconnect.util.ConnectionDetector;
import info.android.technologies.indoreconnect.util.GPSTracker;
import info.android.technologies.indoreconnect.util.HelperManager;
import info.android.technologies.indoreconnect.util.SessionManager;
import info.android.technologies.indoreconnect.util.WebAPI;

import java.util.ArrayList;
import java.util.Collections;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, AdapterView.OnItemClickListener {

    Context ctx;
    FrameLayout frameLayout;
    ImageView home_iv, info_iv, search_iv, offer_iv, menu_iv, noti_iv, new_text_iv;
    DrawerLayout drawerLayout;
    ListView listView;
    ArrayList<Navigation> nav_list;
    ArrayList<String> noti_list;
    LinearLayout home_ll, info_ll, search_ll, offer_ll;

    ConnectionDetector connectionDetector;
    SessionManager sessionManager;

    TextView name_tv, mobile_tv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initxml();
        click();
        getLocation();
        sessionManager.setData(SessionManager.KEY_PAGE, "home");
        setData();

        boolean internet = connectionDetector.isConnected();
        if (internet) {
            getNotification();
            loadSearchingData();
        } else {
            Toast.makeText(ctx, "No ineternet connection", Toast.LENGTH_LONG).show();
        }
    }

    private void checkNotification() {

        String type = "";
        HelperManager helperManager = new HelperManager(ctx);
        ArrayList<String> database_noti_list;

        database_noti_list = helperManager.readNotification();
        for (int i = 0; i < noti_list.size(); i++) {

            if (database_noti_list.contains(noti_list.get(i))) {
                sessionManager.setData(SessionManager.KEY_NAV_VIEW_TYPE, "gone");
            } else {
                noti_iv.setVisibility(View.VISIBLE);
                sessionManager.setData(SessionManager.KEY_NAV_VIEW_TYPE, "view");
            }
        }
        setNavigation();
    }


    private void getNotification() {

        String DateToStr = sessionManager.getData(SessionManager.KEY_CREATE_DATE);
        final ProgressDialog pd = new ProgressDialog(ctx);
        pd.setMessage("wait..");
        pd.setCancelable(false);
        pd.show();
        StringRequest stringRequest = new StringRequest(Request.Method.GET, WebAPI.URL_NOTIFICATION + DateToStr,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        pd.dismiss();
                        try {
                            JSONArray imageArray = new JSONArray(response);
                            for (int i = 0; i < imageArray.length(); i++) {
                                JSONObject imgobj = imageArray.getJSONObject(i);
                                String id = imgobj.getString("id");
                                noti_list.add(id);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        checkNotification();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(ctx, error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
        RequestQueue requestQueue = Volley.newRequestQueue(ctx);
        requestQueue.add(stringRequest);
    }

    private void loadSearchingData() {

        final ProgressDialog pd = new ProgressDialog(ctx);
        pd.setMessage("wait..");
        pd.setCancelable(false);
        pd.show();
        StringRequest stringRequest = new StringRequest(Request.Method.GET, WebAPI.URL_SEARCH,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        pd.dismiss();
                        sessionManager.setData(SessionManager.KEY_SEARCH_DATA, response);
                        Home2Fragment homeFragment = new Home2Fragment(ctx);
                        setFragment(homeFragment);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(ctx, error.getMessage(), Toast.LENGTH_SHORT).show();
                        Toast.makeText(ctx, "some eror", Toast.LENGTH_SHORT).show();
                    }
                });
        RequestQueue requestQueue = Volley.newRequestQueue(ctx);
        requestQueue.add(stringRequest);
    }


    private void getLocation() {

        GPSTracker gps = new GPSTracker(MainActivity.this);
        if (gps.canGetLocation()) {
            double latitude = gps.getLatitude();
            double longitude = gps.getLongitude();

            sessionManager.setDouble(SessionManager.KEY_LAT, latitude);
            sessionManager.setDouble(SessionManager.KEY_LONG, longitude);
//            Toast.makeText(getApplicationContext(), "Your Location is - \nLat: " + latitude + "\nLong: " + longitude, Toast.LENGTH_LONG).show();
        } else {
            gps.showSettingsAlert();
        }
    }

    private void setData() {

        SessionManager sessionManager = new SessionManager(ctx);
        String name = sessionManager.getData(SessionManager.KEY_FNAME);
        String mobile = sessionManager.getData(SessionManager.KEY_MOBILE);

        name_tv.setText(name + "");
        mobile_tv.setText(mobile + "");

    }

    private void setNavigation() {

        nav_list = new ArrayList<>();
        nav_list.add(new Navigation(R.drawable.favourite, "Favourite"));
        nav_list.add(new Navigation(R.drawable.notification, "Notification"));
        nav_list.add(new Navigation(R.drawable.list_urself, "List Yourself"));
        nav_list.add(new Navigation(R.drawable.share, "Share these App"));
        nav_list.add(new Navigation(R.drawable.rate_us, "Rate Us"));
        nav_list.add(new Navigation(R.drawable.feedback, "Feedback"));
        nav_list.add(new Navigation(R.drawable.about_us, "About Us"));
        nav_list.add(new Navigation(R.drawable.contact, "Contact Us"));

        NavAdapter navAdapter = new NavAdapter(ctx, nav_list);
        listView.setAdapter(navAdapter);
    }

    private void click() {
        home_iv.setOnClickListener(this);
        info_iv.setOnClickListener(this);
        search_iv.setOnClickListener(this);
        offer_iv.setOnClickListener(this);
        menu_iv.setOnClickListener(this);

        listView.setOnItemClickListener(this);
    }

    private void initxml() {
        ctx = this;
        frameLayout = (FrameLayout) findViewById(R.id.framelayout);
        home_ll = (LinearLayout) findViewById(R.id.ll_main_home);
        info_ll = (LinearLayout) findViewById(R.id.ll_main_info);
        search_ll = (LinearLayout) findViewById(R.id.ll_main_search);
        offer_ll = (LinearLayout) findViewById(R.id.ll_main_offer);
        home_iv = (ImageView) findViewById(R.id.iv_main_home);
        info_iv = (ImageView) findViewById(R.id.iv_main_info);
        search_iv = (ImageView) findViewById(R.id.iv_main_search);
        offer_iv = (ImageView) findViewById(R.id.iv_main_offer);
        menu_iv = (ImageView) findViewById(R.id.iv_main_menu);
        noti_iv = (ImageView) findViewById(R.id.iv_main_noti);
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        listView = (ListView) findViewById(R.id.lv_nav_listview);

        name_tv = (TextView) findViewById(R.id.tv_nav_name);
        mobile_tv = (TextView) findViewById(R.id.tv_nav_mobile);

        sessionManager = new SessionManager(ctx);
        connectionDetector = new ConnectionDetector();

        noti_list = new ArrayList<>();
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {

            case R.id.iv_main_home:
                sessionManager.setData(SessionManager.KEY_PAGE, "home");
                setIndication(true, false, false, false);
                Home2Fragment homeFragment = new Home2Fragment(ctx);
                setFragment(homeFragment);
                break;
            case R.id.iv_main_info:
                sessionManager.setData(SessionManager.KEY_PAGE, "info");
                setIndication(false, true, false, false);
                InfoFragment infoFragment = new InfoFragment(ctx);
                setFragment(infoFragment);
                break;
            case R.id.iv_main_search:
                sessionManager.setData(SessionManager.KEY_SEARCH_FROM, "search");
                sessionManager.setData(SessionManager.KEY_PAGE, "search");
                setIndication(false, false, true, false);
                Search2Fragment searchFragment = new Search2Fragment(ctx);
                setFragment(searchFragment);
                break;
            case R.id.iv_main_offer:
                sessionManager.setData(SessionManager.KEY_PAGE, "offer");
                setIndication(false, false, false, true);
                OfferFragment offerFragment = new OfferFragment(ctx);
                setFragment(offerFragment);
                break;
            case R.id.iv_main_menu:
                sessionManager.setData(SessionManager.KEY_PAGE, "menu");
                setNavigation();
                drawerLayout.openDrawer(Gravity.RIGHT);
                break;
        }
    }

    private void setIndication(boolean home, boolean info, boolean search, boolean offer) {

        if (home) {
            home_ll.setBackgroundResource(R.color.selction);
        } else {
            home_ll.setBackgroundResource(R.color.blue);
        }
        if (info) {
            info_ll.setBackgroundResource(R.color.selction);
        } else {
            info_ll.setBackgroundResource(R.color.blue);
        }
        if (search) {
            search_ll.setBackgroundResource(R.color.selction);
        } else {
            search_ll.setBackgroundResource(R.color.blue);
        }
        if (offer) {
            offer_ll.setBackgroundResource(R.color.selction);
        } else {
            offer_ll.setBackgroundResource(R.color.blue);
        }
    }

    private void setFragment(Fragment fragment) {

        final android.app.FragmentTransaction ft = getFragmentManager().beginTransaction();
        ft.replace(R.id.framelayout, fragment);
        ft.commit();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

        Fragment fragment = null;
        if (position == 0) {
            fragment = new FavouritsFragment(ctx);
            setFragment(fragment);
            sessionManager.setData(SessionManager.KEY_PAGE, "fav");
        } else if (position == 1) {

            fragment = new NotificationFragment(ctx);
            setFragment(fragment);
            sessionManager.setData(SessionManager.KEY_PAGE, "notification");
        } else if (position == 2) {

            boolean check = sessionManager.isLoggedIn();
            if (check) {
                String user_id = sessionManager.getData(SessionManager.KEY_EMAIL);
                fragment = new MyListFragment(ctx, user_id);
                setFragment(fragment);
                sessionManager.setData(SessionManager.KEY_PAGE, "mylist");
            } else {
                Toast.makeText(ctx, "Please login first then list yourself", Toast.LENGTH_LONG).show();
            }
        } else if (position == 3) {
            shareApp();
        } else if (position == 4) {
            rateUs();
        } else if (position == 5) {
            boolean check = sessionManager.isLoggedIn();
            if (check) {
                String user_id = sessionManager.getData(SessionManager.KEY_EMAIL);
                fragment = new FeedbackFragment(ctx, user_id);
                setFragment(fragment);
                sessionManager.setData(SessionManager.KEY_PAGE, "feedback");
            } else {
                Toast.makeText(ctx, "Please login first for submit your feedback", Toast.LENGTH_LONG).show();
            }
        } else if (position == 6) {
            fragment = new AboutUsFragment();
            setFragment(fragment);
            sessionManager.setData(SessionManager.KEY_PAGE, "about");
        } else if (position == 7) {
            fragment = new ContactUsFragment();
            setFragment(fragment);
            sessionManager.setData(SessionManager.KEY_PAGE, "contact");
        } else if (position == 8) {
            startActivity(new Intent(ctx, LoginActivity.class));
        } else {
            Toast.makeText(ctx, "Something wrong", Toast.LENGTH_LONG).show();
        }
        drawerLayout.closeDrawer(Gravity.RIGHT);
    }

    private void rateUs() {
        try {
            Intent rateIntent = rateIntentForUrl("market://details");
            startActivity(rateIntent);
        } catch (ActivityNotFoundException e) {
            Intent rateIntent = rateIntentForUrl("https://play.google.com/store/apps/details");
            startActivity(rateIntent);
        }
    }

    private Intent rateIntentForUrl(String url) {
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(String.format("%s?id=%s", url, getPackageName())));
        int flags = Intent.FLAG_ACTIVITY_NO_HISTORY | Intent.FLAG_ACTIVITY_MULTIPLE_TASK;
        if (Build.VERSION.SDK_INT >= 21) {
            flags |= Intent.FLAG_ACTIVITY_NEW_DOCUMENT;
        } else {
            flags |= Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET;
        }
        intent.addFlags(flags);
        return intent;
    }

    private void shareApp() {
        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_TEXT,
                "Hey check out my app at: https://play.google.com/store/apps/details?id=info.android.technologies.indoreconnect&hl=en");
        sendIntent.setType("text/plain");
        startActivity(sendIntent);
    }

    @Override
    public void onBackPressed() {

        String page = sessionManager.getData(SessionManager.KEY_PAGE);

        if (drawerLayout.isDrawerOpen(GravityCompat.END)) {
            //drawer is open
//            Toast.makeText(ctx,"drawer open",Toast.LENGTH_LONG).show();
            sessionManager.setData(SessionManager.KEY_PAGE, "subhome");
            drawerLayout.closeDrawers();
        } else {

            if (page.equals("menu")) {
                sessionManager.setData(SessionManager.KEY_PAGE, "home");
                drawerLayout.closeDrawers();
            } else {

                if (page.equals("home")) {

                    alertDialog();
                } else if (page.equals("info")) {

                    InfoFragment infoFragment = new InfoFragment(ctx);
                    setFragment(infoFragment);
                    sessionManager.setData(SessionManager.KEY_PAGE, "infocate");
                } else if (page.equals("subhome")) {

                    Home2Fragment fragment = new Home2Fragment(ctx);
                    setFragment(fragment);
                    sessionManager.setData(SessionManager.KEY_PAGE, "home");
                    setIndication(true, false, false, false);

                } else if (page.equals("subhomelisting")) {

                    Search2Fragment search2Fragment = new Search2Fragment(ctx);
                    setFragment(search2Fragment);

                } else if (page.equals("see_all_listing")) {

                    sessionManager.setData(SessionManager.KEY_PAGE, "subhome");
                    String main_cate = sessionManager.getData(SessionManager.KEY_MAIN_CATEGORY_NAME);
                    SeeAllCategoryHomeFragment categoryHomeFragment = new SeeAllCategoryHomeFragment(ctx);
                    setFragment(categoryHomeFragment);

                } else if (page.equals("search")) {

                    sessionManager.setData(SessionManager.KEY_PAGE, "subhome");
                    Search2Fragment searchFragment = new Search2Fragment(ctx);
                    setFragment(searchFragment);
                } else if (page.equals("infosub")) {

                    sessionManager.setData(SessionManager.KEY_PAGE, "info");
                    InfoSubCateFragment infoSubCateFragment = new InfoSubCateFragment(ctx);
                    setFragment(infoSubCateFragment);
                } else if (page.equals("fav")) {

                    sessionManager.setData(SessionManager.KEY_PAGE, "notification");
                    FavouritsFragment favouritsFragment = new FavouritsFragment(ctx);
                    setFragment(favouritsFragment);

                } else if (page.equals("allbuiss")) {

                    sessionManager.setData(SessionManager.KEY_PAGE, "notification");
                    AllBuisnessListingFragment allBuisnessListingFragment = new AllBuisnessListingFragment(ctx);
                    setFragment(allBuisnessListingFragment);

                } else if (page.equals("allbuiss2")) {

                    sessionManager.setData(SessionManager.KEY_PAGE, "notification");
                    SeeAllCategoryHomeFragment seeAllCategoryHomeFragment = new SeeAllCategoryHomeFragment(ctx);
                    setFragment(seeAllCategoryHomeFragment);

                } else if (page.equals("subhome")) {

                    sessionManager.setData(SessionManager.KEY_PAGE, "notification");
                    SeeAllCategoryHomeFragment categoryHomeFragment = new SeeAllCategoryHomeFragment(ctx);
                    setFragment(categoryHomeFragment);

                } else {
                    Home2Fragment fragment = new Home2Fragment(ctx);
                    setFragment(fragment);
                    sessionManager.setData(SessionManager.KEY_PAGE, "home");
                    setIndication(true, false, false, false);
                }
            }

        }
    }

    private void alertDialog() {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Are you sure you want to exit?")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        finish();
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
        AlertDialog alert = builder.create();
        alert.show();
    }

}
