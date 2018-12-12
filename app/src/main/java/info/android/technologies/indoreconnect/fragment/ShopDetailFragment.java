package info.android.technologies.indoreconnect.fragment;

import android.app.Dialog;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import info.android.technologies.indoreconnect.R;
import info.android.technologies.indoreconnect.activity.MapsActivity;
import info.android.technologies.indoreconnect.activity.WebViewActivity;
import info.android.technologies.indoreconnect.adapter.ImageDetailsPagerAdapter;
import info.android.technologies.indoreconnect.model.BuisnessList;
import info.android.technologies.indoreconnect.model.Images;
import info.android.technologies.indoreconnect.util.ConnectionDetector;
import info.android.technologies.indoreconnect.util.HelperManager;
import info.android.technologies.indoreconnect.util.RoundedCornersTransformation;
import info.android.technologies.indoreconnect.util.SessionManager;
import info.android.technologies.indoreconnect.util.WebAPI;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.SphericalUtil;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by kamlesh on 12/7/2017.
 */
public class ShopDetailFragment extends Fragment implements View.OnClickListener {
    Context ctx;
    View view;
    Button readmore_bt;
    String buisness_id;

    de.hdodenhof.circleimageview.CircleImageView circular_image;
    LinearLayout direction_ll, calling_ll, website_icon_ll;
    TextView website_tv, shopname_tv, catename_tv, discription_tv, est_year_tv, contact_tv, contact_person_tv, address_tv, email_tv, distance_tv, status_tv;
    TextView all_rating_tv, also_listed_tv;

    TextView mon_op_tv, mon_cl_tv, tues_op_tv, tues_cl_tv, wednes_op_tv, wednes_cl_tv, thurs_op_tv, thurs_cl_tv, fri_op_tv, fri_cl_tv, satur_op_tv, satur_cl_tv, sun_op_tv, sun_cl_tv;
    TextView title_dec_tv;
    LinearLayout readmore_ll, website_text_ll, contact_persone_ll, contact_no_ll, email_ll;
    ViewPager viewpager;

    boolean check_fav = true;
    ImageView addfave_iv;
    TextView rate_us_tv;
    RatingBar ratingBar;

    double latitude, longitude, buis_lat, buis_long;
    ArrayList<Images> img_list;

    String user_id;

    ConnectionDetector connectionDetector;
    SessionManager sessionManager;


    String status_1, buisness_name_1, building_1, street_1, estb_year_1, area_1, city_1, pincode_1, state_1, contact_person_title_1, contact_person_name_1,
            designation_1, landline_no1_1, landline_no2_1, mobile_no1_1, mobile_no2_1, email_id_1, website_1, lat_1, set_long_1, image_1, rating_1, cate_icon_1;
    float distance_1;

    String contact_1, contact_2, contact_3, contact_4;

    public ShopDetailFragment(Context ctx) {
        this.ctx = ctx;

        img_list = new ArrayList<>();
        connectionDetector = new ConnectionDetector();
        sessionManager = new SessionManager(ctx);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(info.android.technologies.indoreconnect.R.layout.fragment_shopdetail, null);
        initxml();
        click();
        buisness_id = sessionManager.getData(SessionManager.KEY_BUISNESS_ID);
        checkFav();
        boolean internet = connectionDetector.isConnected();
        if (internet) {
            loadBuisnessData(buisness_id);
        } else {
            toastView("No internet Connection");
        }
        return view;
    }

    private void checkFav() {

        HelperManager helperManager = new HelperManager(ctx);
        ArrayList<String> id_list = helperManager.readOnlyID();

        if (id_list.contains(buisness_id)) {
            addfave_iv.setImageResource(R.drawable.heart);
            check_fav = false;
        } else {
            addfave_iv.setImageResource(R.drawable.heart_outline);
            check_fav = true;
        }
    }

    private void toastView(String s) {
        Toast.makeText(ctx, s, Toast.LENGTH_LONG).show();
    }

    private void loadBuisnessData(String category_id) {

        final ProgressDialog pd = new ProgressDialog(ctx);
        pd.setMessage("wait..");
        pd.setCancelable(false);
        pd.show();
        StringRequest stringRequest = new StringRequest(Request.Method.GET, WebAPI.URL_BUSNESS_DETAILS + category_id,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        pd.dismiss();
                        try {
                            JSONObject object = new JSONObject(response);
                            JSONArray imag_array = object.getJSONArray("businessImages");
                            JSONArray working_array = object.getJSONArray("workingDetail");
                            JSONArray buisness_array = object.getJSONArray("businessDetail");
                            JSONArray buisness_rating = object.getJSONArray("businessRating");
                            JSONArray buisness_keyword = object.getJSONArray("businesskeyword");

                            JSONObject object1 = buisness_array.getJSONObject(0);
                            JSONObject rating_obj = buisness_rating.getJSONObject(0);
                            String rating = rating_obj.getString("rating");
                            String total_rating_user = rating_obj.getString("total_rating");

                            String status = object1.getString("status");
                            String building = object1.getString("building");
                            String area = object1.getString("area");
                            String city = object1.getString("city");
                            String pincode = object1.getString("pincode");
                            String address = building + " " + "\n" + city + " (M.P.) " + pincode;
                            String shopname = object1.getString("buisness_name");
                            String contact = object1.getString("mobile_no1");
                            String mobile2 = object1.getString("mobile_no2");

                            String contact_person_name_title = object1.getString("contact_person_title");
                            String contact_person_name = object1.getString("contact_person_name");

                            String email = object1.getString("email_id");
                            String discription = object1.getString("designation");
                            String lati = object1.getString("lat");
                            String longi = object1.getString("set_long");
                            String website = object1.getString("website");
                            String estb_year = object1.getString("year_esatablishing");
                            String landline1 = object1.getString("landline_no1");
                            String landline2 = object1.getString("landline_no2");

                            StringBuilder category = new StringBuilder();
                            for (int k = 0; k < buisness_keyword.length(); k++) {

                                JSONObject object2 = buisness_keyword.getJSONObject(k);
                                String category_name = object2.getString("category_name");

                                if (k == 0) {
                                    category.append(category_name);
                                } else {
                                    category.append(" ," + category_name);
                                }
                                String cate = category.toString();
                                also_listed_tv.setText(cate);
                            }

                            all_rating_tv.setText("" + total_rating_user);
                            status_1 = status;
                            buisness_name_1 = shopname;
                            building_1 = building;
                            street_1 = "";
                            estb_year_1 = estb_year;
                            area_1 = area;
                            city_1 = city;
                            pincode_1 = pincode;
                            state_1 = "Madhya Pradesh";
                            contact_person_title_1 = "";
                            contact_person_name_1 = contact_person_name_title + " " + contact_person_name;
                            designation_1 = discription;
                            landline_no1_1 = landline1;
                            landline_no2_1 = landline2;
                            mobile_no1_1 = contact;
                            mobile_no2_1 = mobile2;
                            email_id_1 = email;
                            website_1 = website;
                            lat_1 = lati;
                            set_long_1 = longi;
                            rating_1 = rating;
                            cate_icon_1 = "";
                            float distance_1 = 0;

                            contact_1 = contact;
                            contact_2 = mobile2;
                            contact_3 = landline1;
                            contact_4 = landline2;

                            img_list = new ArrayList<>();

                            for (int i = 0; i < imag_array.length(); i++) {

                                JSONObject image_obj = imag_array.getJSONObject(i);
                                String type = image_obj.getString("typee");
                                String image = image_obj.getString("image");

                                img_list.add(new Images(type, image));

                                if (type.equals("logo")) {
                                    image_1 = image;
                                }
                            }
                            setData(status, address, shopname, contact, contact_person_name_1, email, discription, lati, longi,
                                    website, estb_year, contact_2, contact_3, contact_4);
                            setWorking(working_array);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        setImages();
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

    private void setWorking(JSONArray working_array) {

        try {
            JSONObject week_obj = working_array.getJSONObject(0);

            String monday = week_obj.getString("monday");
            String tuesday = week_obj.getString("tuesday");
            String wednesday = week_obj.getString("wednesday");
            String thursday = week_obj.getString("thursday");
            String friday = week_obj.getString("friday");
            String saturday = week_obj.getString("saturday");
            String sunday = week_obj.getString("sunday");

            if (monday.contains("Closed ")) {
                monday = "closed";
            }
            if (tuesday.contains("Closed ")) {
                tuesday = "closed";
            }
            if (wednesday.contains("Closed ")) {
                wednesday = "closed";
            }
            if (thursday.contains("Closed ")) {
                thursday = "closed";
            }
            if (friday.contains("Closed ")) {
                friday = "closed";
            }
            if (saturday.contains("Closed ")) {
                saturday = "closed";
            }
            if (sunday.contains("Closed ")) {
                sunday = "closed";
            }
            mon_op_tv.setText(monday);
            tues_op_tv.setText(tuesday);
            wednes_op_tv.setText(wednesday);
            thurs_op_tv.setText(thursday);
            fri_op_tv.setText(friday);
            satur_op_tv.setText(saturday);
            sun_op_tv.setText(sunday);
            mon_cl_tv.setText("");
            tues_cl_tv.setText("");
            wednes_cl_tv.setText("");
            thurs_cl_tv.setText("");
            fri_cl_tv.setText("");
            satur_cl_tv.setText("");
            sun_cl_tv.setText("");

            if (monday.equals(" To ")) {
                mon_op_tv.setText("24 Hours Open");
            }
            if (tuesday.equals(" To ")) {
                tues_op_tv.setText("24 Hours Open");
            }
            if (wednesday.equals(" To ")) {
                wednes_op_tv.setText("24 Hours Open");
            }
            if (thursday.equals(" To ")) {
                thurs_op_tv.setText("24 Hours Open");
            }
            if (friday.equals(" To ")) {
                fri_op_tv.setText("24 Hours Open");
            }
            if (saturday.equals(" To ")) {
                satur_op_tv.setText("24 Hours Open");
            }
            if (sunday.equals(" To ")) {
                sun_op_tv.setText("24 Hours Open");
            }

            SimpleDateFormat sdf = new SimpleDateFormat("EEEE");
            Date d = new Date();
            String dayOfTheWeek = sdf.format(d);

            Calendar rightNow = Calendar.getInstance();
            int currentHour = rightNow.get(Calendar.HOUR_OF_DAY);

            if (dayOfTheWeek.equalsIgnoreCase("Monday")) {

                try {
                    if (monday.contains("Closed")) {
                        status_tv.setText("Closed");
                    } else if (monday.contains("Open")) {
                        status_tv.setText("Open");
                    } else if (monday.equals(" To ")) {
                        status_tv.setText("Open");
                    } else {

                        String start = monday.substring(0, 2);
                        String end = monday.substring(9, 11);
                        //                09:00 To 21:00;

                        int i_start = Integer.parseInt(start);
                        int i_end = Integer.parseInt(end);
                        if (i_start < currentHour && currentHour < i_end) {
                            status_tv.setText("Open");
                        } else {
                            status_tv.setText("Closed");
                        }

                    }
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                }

            } else if (dayOfTheWeek.equalsIgnoreCase("Tuesday")) {

                try {
                    if (tuesday.contains("Closed")) {
                        status_tv.setText("Closed");
                    } else if (tuesday.contains("Open")) {
                        status_tv.setText("Open");
                    } else if (tuesday.equals(" To ")) {
                        status_tv.setText("Open");
                    } else {

                        String start = tuesday.substring(0, 2);
                        String end = tuesday.substring(9, 11);
                        //                09:00 To 21:00;

                        int i_start = Integer.parseInt(start);
                        int i_end = Integer.parseInt(end);
                        if (i_start < currentHour && currentHour < i_end) {
                            status_tv.setText("Open");
                        } else {
                            status_tv.setText("Closed");
                        }

                    }
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                }


            } else if (dayOfTheWeek.equalsIgnoreCase("Wednesday")) {

                try {
                    if (wednesday.contains("Closed")) {
                        status_tv.setText("Closed");
                    } else if (wednesday.contains("Open")) {
                        status_tv.setText("Open");
                    } else if (wednesday.equals(" To ")) {
                        status_tv.setText("Open");
                    } else {

                        String start = wednesday.substring(0, 2);
                        String end = wednesday.substring(9, 11);
                        //                09:00 To 21:00;

                        int i_start = Integer.parseInt(start);
                        int i_end = Integer.parseInt(end);
                        if (i_start < currentHour && currentHour < i_end) {
                            status_tv.setText("Open");
                        } else {
                            status_tv.setText("Closed");
                        }

                    }
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                    status_tv.setText("Open");
                }
            } else if (dayOfTheWeek.equalsIgnoreCase("Thursday")) {

                try {
                    if (thursday.contains("Closed")) {
                        status_tv.setText("Closed");
                    } else if (thursday.contains("Open")) {
                        status_tv.setText("Open");
                    } else if (thursday.equals(" To ")) {
                        status_tv.setText("Open");
                    } else {

                        String start = thursday.substring(0, 2);
                        String end = thursday.substring(9, 11);
                        //                09:00 To 21:00;

                        int i_start = Integer.parseInt(start);
                        int i_end = Integer.parseInt(end);
                        if (i_start < currentHour && currentHour < i_end) {
                            status_tv.setText("Open");
                        } else {
                            status_tv.setText("Closed");
                        }

                    }
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                    status_tv.setText("Open");
                }

            } else if (dayOfTheWeek.equalsIgnoreCase("Friday")) {

                try {
                    if (friday.contains("Closed")) {
                        status_tv.setText("Closed");
                    } else if (friday.contains("Open")) {
                        status_tv.setText("Open");
                    } else if (friday.equals(" To ")) {
                        status_tv.setText("Open");
                    } else {

                        String start = friday.substring(0, 2);
                        String end = friday.substring(9, 11);
                        //                09:00 To 21:00;

                        int i_start = Integer.parseInt(start);
                        int i_end = Integer.parseInt(end);
                        if (i_start < currentHour && currentHour < i_end) {
                            status_tv.setText("Open");
                        } else {
                            status_tv.setText("Closed");
                        }

                    }
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                    status_tv.setText("Open");
                }


            } else if (dayOfTheWeek.equalsIgnoreCase("Saturday")) {

                try {
                    if (saturday.contains("Closed")) {
                        status_tv.setText("Closed");
                    } else if (saturday.contains("Open")) {
                        status_tv.setText("Open");
                    } else if (saturday.equals(" To ")) {
                        status_tv.setText("Open");
                    } else {

                        String start = saturday.substring(0, 2);
                        String end = saturday.substring(9, 11);
                        //                09:00 To 21:00;

                        int i_start = Integer.parseInt(start);
                        int i_end = Integer.parseInt(end);
                        if (i_start < currentHour && currentHour < i_end) {
                            status_tv.setText("Open");
                        } else {
                            status_tv.setText("Closed");
                        }

                    }
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                    status_tv.setText("Open");
                }


            } else if (dayOfTheWeek.equalsIgnoreCase("Sunday")) {

                try {
                    if (sunday.contains("Closed")) {
                        status_tv.setText("Closed");
                    } else if (sunday.contains("Open")) {
                        status_tv.setText("Open");
                    } else if (sunday.equals(" To ")) {
                        status_tv.setText("Open");
                    } else {

                        String start = sunday.substring(0, 2);
                        String end = sunday.substring(9, 11);
                        //                09:00 To 21:00;

                        int i_start = Integer.parseInt(start);
                        int i_end = Integer.parseInt(end);
                        if (i_start < currentHour && currentHour < i_end) {
                            status_tv.setText("Open");
                        } else {
                            status_tv.setText("Closed");
                        }

                    }
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                    status_tv.setText("Open");
                }
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    private void setImages() {

        String icon = "", cover = "";
        for (int j = 0; j < img_list.size(); j++) {

            if (img_list.get(j).getType().equals("logo")) {
                icon = img_list.get(j).getImage();
                img_list.remove(j);
            }
        }

        if (!icon.equals("")) {
            String final_url = WebAPI.BASE_URL + "admin/img/business_images/logo/" + icon;
            String final_url_1 = final_url.replace(" ", "%20");
            Picasso.with(ctx)
                    .load(final_url_1)
                    .placeholder(R.drawable.blue_ic_icon)
                    .error(R.drawable.blue_ic_icon)
                    .into(circular_image);
        }
        final ImageDetailsPagerAdapter pagerAdapter = new ImageDetailsPagerAdapter(ctx, img_list);
        viewpager.setAdapter(pagerAdapter);
    }

    private void setData(String status, String address, String shopname, String contact, String contact_person_name, String email,
                         String discription, String lati, String longi, String website, String estb_year, String contact2, String contact3, String contact4) {

        SessionManager sessionManager = new SessionManager(ctx);
        String category = sessionManager.getData(SessionManager.KEY_CATEGORY_NAME);
        user_id = sessionManager.getData(SessionManager.KEY_MOBILE);
        status_tv.setText("  " + status + "  ");

        shopname_tv.setText(shopname);
        address_tv.setText(address);

        ArrayList<String>cont_list = new ArrayList<>();
        if (!contact.equals("")){
            cont_list.add(contact);
        }
        if (!contact2.equals("")){
            cont_list.add(contact2);
        }
        if (!contact3.equals("")){
            cont_list.add(contact3);
        }
        if (!contact4.equals("")){
            cont_list.add(contact4);
        }

        StringBuilder con = new StringBuilder();

        if (cont_list.size()!=0){

            for (int i=0;i<cont_list.size();i++){
                if (i==0){
                    con.append(cont_list.get(i));
                }else {
                    con.append("\n"+cont_list.get(i));
                }
            }
            contact_tv.setText(con.toString());
        }else {
            contact_no_ll.setVisibility(View.GONE);
             calling_ll.setVisibility(View.GONE);
        }
/*
        if (!contact.equals("")) {
            con.append(contact);
        }
        if (!contact2.equals("")) {
            String con2 = " , " + contact2;
            con.append(con2);
        }
        if (!contact3.equals("")) {
            con.append("\n" + contact3);
        }
        if (!contact4.equals("")) {
            con.append("," + contact4);
        }
//        contact_tv.setText(contact + "," + contact2 + "\n" + contact3 + "," + contact4);
        contact_tv.setText(con.toString() + "");
*/

        if (contact_person_name.equals(" ")) {
            contact_persone_ll.setVisibility(View.GONE);
        } else {
            contact_person_tv.setText(contact_person_name);
        }

        if (email.equals("")) {
            email_ll.setVisibility(View.GONE);
        } else {
            email_tv.setText(email);
        }

        if (discription.equals("")) {
            title_dec_tv.setVisibility(View.GONE);
            discription_tv.setText(discription);
        } else {
            title_dec_tv.setVisibility(View.VISIBLE);
            discription_tv.setText(discription);
        }
        if (estb_year.equals("")) {
            est_year_tv.setVisibility(View.GONE);
        } else {
            est_year_tv.setText("Establish Year - " + estb_year);
        }
        catename_tv.setText(category);

        try {
            String rate = rating_1.substring(0, 1);
            int rate_1 = Integer.parseInt(rate);
            ratingBar.setRating(rate_1);
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }

        latitude = sessionManager.getDouble(SessionManager.KEY_LAT);
        longitude = sessionManager.getDouble(SessionManager.KEY_LONG);

        buis_lat = Double.parseDouble(lati);
        buis_long = Double.parseDouble(longi);

        DecimalFormat df2 = new DecimalFormat(".##");

        LatLng from = new LatLng(latitude, longitude);
        LatLng to = new LatLng(buis_lat, buis_long);

        Double distance = SphericalUtil.computeDistanceBetween(from, to);
        double distance_km = distance / 1000;

        String distn = String.valueOf(distance_km);
        String distan = distn.substring(0, 4);

        distance_tv.setText(String.valueOf(distan) + " KM");

//        double dis = distance(latitude, longitude, buis_lat, buis_long);
//        distance_tv.setText(df2.format(dis) + " KM");
//        direction_ll;

        if (website.equals("")) {
            website_icon_ll.setVisibility(View.GONE);
            website_text_ll.setVisibility(View.GONE);
        } else {
            website_tv.setText(website);
            website_tv.setVisibility(View.VISIBLE);
            website_text_ll.setVisibility(View.VISIBLE);
        }
    }

    private void click() {
        calling_ll.setOnClickListener(this);
        website_icon_ll.setOnClickListener(this);
        readmore_bt.setOnClickListener(this);
        addfave_iv.setOnClickListener(this);
        rate_us_tv.setOnClickListener(this);
        direction_ll.setOnClickListener(this);
    }

    private void initxml() {
        ratingBar = (RatingBar) view.findViewById(R.id.rb_details_rating);
        rate_us_tv = (TextView) view.findViewById(R.id.tv_details_rateus);
        title_dec_tv = (TextView) view.findViewById(R.id.tv_details_desc_tv);
        addfave_iv = (ImageView) view.findViewById(info.android.technologies.indoreconnect.R.id.iv_details_addfav);
        circular_image = (CircleImageView) view.findViewById(info.android.technologies.indoreconnect.R.id.iv_details_circular_image);
        viewpager = (ViewPager) view.findViewById(info.android.technologies.indoreconnect.R.id.vp_details_viewpager);
        readmore_bt = (Button) view.findViewById(info.android.technologies.indoreconnect.R.id.bt_shopdetail_readmore);
        readmore_ll = (LinearLayout) view.findViewById(info.android.technologies.indoreconnect.R.id.ll_shopdetails_readmore);
        website_text_ll = (LinearLayout) view.findViewById(info.android.technologies.indoreconnect.R.id.ll_shopdetails_website);
        contact_persone_ll = (LinearLayout) view.findViewById(R.id.ll_details_contact_person);
        contact_no_ll = (LinearLayout) view.findViewById(R.id.ll_details_contactno);
        email_ll = (LinearLayout) view.findViewById(R.id.ll_details_contact_email);

        direction_ll = (LinearLayout) view.findViewById(info.android.technologies.indoreconnect.R.id.ll_details_direction);
        calling_ll = (LinearLayout) view.findViewById(info.android.technologies.indoreconnect.R.id.ll_details_calling);
        website_icon_ll = (LinearLayout) view.findViewById(info.android.technologies.indoreconnect.R.id.ll_details_website);

        all_rating_tv = (TextView) view.findViewById(R.id.tv_details_allrating);
        also_listed_tv = (TextView) view.findViewById(R.id.tv_details_listedcate);
        shopname_tv = (TextView) view.findViewById(info.android.technologies.indoreconnect.R.id.tv_details_shopname);
        website_tv = (TextView) view.findViewById(info.android.technologies.indoreconnect.R.id.tv_details_website);
        catename_tv = (TextView) view.findViewById(info.android.technologies.indoreconnect.R.id.tv_details_catename);
        discription_tv = (TextView) view.findViewById(info.android.technologies.indoreconnect.R.id.tv_details_shopdetails);
        est_year_tv = (TextView) view.findViewById(info.android.technologies.indoreconnect.R.id.tv_details_estbyear);
        contact_tv = (TextView) view.findViewById(info.android.technologies.indoreconnect.R.id.tv_details_contactno);
        contact_person_tv = (TextView) view.findViewById(info.android.technologies.indoreconnect.R.id.tv_details_contactname);
        address_tv = (TextView) view.findViewById(info.android.technologies.indoreconnect.R.id.tv_details_shopaddress);
        email_tv = (TextView) view.findViewById(info.android.technologies.indoreconnect.R.id.tv_details_email);
        distance_tv = (TextView) view.findViewById(info.android.technologies.indoreconnect.R.id.tv_details_distance);
        status_tv = (TextView) view.findViewById(info.android.technologies.indoreconnect.R.id.tv_details_status);

        mon_op_tv = (TextView) view.findViewById(info.android.technologies.indoreconnect.R.id.tv_detail_op_mo);
        mon_cl_tv = (TextView) view.findViewById(info.android.technologies.indoreconnect.R.id.tv_detail_cl_mo);
        tues_op_tv = (TextView) view.findViewById(info.android.technologies.indoreconnect.R.id.tv_detail_op_tu);
        tues_cl_tv = (TextView) view.findViewById(info.android.technologies.indoreconnect.R.id.tv_detail_cl_tu);
        wednes_op_tv = (TextView) view.findViewById(info.android.technologies.indoreconnect.R.id.tv_detail_op_we);
        wednes_cl_tv = (TextView) view.findViewById(info.android.technologies.indoreconnect.R.id.tv_detail_cl_we);
        thurs_op_tv = (TextView) view.findViewById(info.android.technologies.indoreconnect.R.id.tv_detail_op_th);
        thurs_cl_tv = (TextView) view.findViewById(info.android.technologies.indoreconnect.R.id.tv_detail_cl_th);
        fri_op_tv = (TextView) view.findViewById(info.android.technologies.indoreconnect.R.id.tv_detail_op_fr);
        fri_cl_tv = (TextView) view.findViewById(info.android.technologies.indoreconnect.R.id.tv_detail_cl_fr);
        satur_op_tv = (TextView) view.findViewById(info.android.technologies.indoreconnect.R.id.tv_detail_op_sat);
        satur_cl_tv = (TextView) view.findViewById(info.android.technologies.indoreconnect.R.id.tv_detail_cl_sat);
        sun_op_tv = (TextView) view.findViewById(info.android.technologies.indoreconnect.R.id.tv_detail_op_sun);
        sun_cl_tv = (TextView) view.findViewById(info.android.technologies.indoreconnect.R.id.tv_detail_cl_sun);
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {

            case info.android.technologies.indoreconnect.R.id.bt_shopdetail_readmore:

                if (readmore_ll.getVisibility() == View.VISIBLE) {
                    readmore_ll.setVisibility(View.GONE);
                    readmore_bt.setText("read more");
                } else {
                    readmore_ll.setVisibility(View.VISIBLE);
                    readmore_bt.setText("read less");
                }
                break;
            case info.android.technologies.indoreconnect.R.id.ll_details_website:
                startActivity(new Intent(ctx, WebViewActivity.class).putExtra("link", website_1));
                break;

            case R.id.tv_details_rateus:
                rateUsAlert();
                break;

            case info.android.technologies.indoreconnect.R.id.ll_details_calling:

                ArrayList<String> contact_list = new ArrayList<>();
                if (!contact_1.equals("")) {
                    contact_list.add(contact_1);
                }
                if (!contact_2.equals("")) {
                    contact_list.add(contact_2);
                }
                if (!contact_3.equals("")) {
                    contact_list.add(contact_3);
                }
                if (!contact_4.equals("")) {
                    contact_list.add(contact_4);
                }
                if (contact_list.size() == 0) {
                    toastView("no contact available");
                } else {
                    contactDialog(contact_list);
                }
                break;

            case info.android.technologies.indoreconnect.R.id.iv_details_addfav:

                checkFav();
                if (check_fav) {
                    saveFav();
                } else {
                    removeFav();
                }
                break;

            case R.id.ll_details_direction:
                startActivity(new Intent(ctx, MapsActivity.class)
                        .putExtra("fromlat", latitude)
                        .putExtra("fromlong", longitude)
                        .putExtra("tolat", buis_lat)
                        .putExtra("tolong", buis_long)
                );
/*                Uri mapUri = Uri.parse("geo:0,0?q=" + buis_lat + "," + buis_long + "(" + buisness_name_1 + ")");
                Intent mapIntent = new Intent(Intent.ACTION_VIEW, mapUri);
                mapIntent.setPackage("com.google.android.apps.maps");
                startActivity(mapIntent);*/
                break;
        }
    }

    private void removeFav() {

        HelperManager helperManager = new HelperManager(ctx);
        BuisnessList buisness = new BuisnessList(buisness_id, status_1, buisness_name_1, building_1, street_1, estb_year_1, area_1, city_1, pincode_1, state_1, contact_person_title_1, contact_person_name_1
                , designation_1, landline_no1_1, landline_no2_1, mobile_no1_1, mobile_no2_1, email_id_1, website_1, lat_1, set_long_1, image_1, rating_1, distance_1, cate_icon_1);
//        Favourites favourites = new Favourites(id_1, title_1, address_1, contact_1, lat_1, long_1, image_1);

        helperManager.remove(Integer.parseInt(buisness_id));
        toastView("successfully remove from favourites");
        addfave_iv.setImageResource(R.drawable.heart_outline);
        check_fav = false;
    }

    private void contactDialog(final ArrayList<String> contact_list) {


        final Dialog dialog = new Dialog(ctx);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(true);
        dialog.setContentView(R.layout.dialog_contact);

        ListView listView = (ListView) dialog.findViewById(R.id.lv_dialogcontact_listview);

        ArrayAdapter<String> itemsAdapter = new ArrayAdapter<String>(ctx, android.R.layout.simple_list_item_1, contact_list);
        listView.setAdapter(itemsAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                try {
                    Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + contact_list.get(position)));
                    startActivity(intent);
                } catch (Exception e) {
                    Intent intent = new Intent();
                    intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                    Uri uri = Uri.fromParts("package", ctx.getPackageName(), null);
                    intent.setData(uri);
                    startActivity(intent);
                    toastView("please give permission for calling.");
                    e.printStackTrace();
                }
            }
        });
        dialog.show();
    }

    private void rateUsAlert() {

        final Dialog dialog = new Dialog(ctx);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(true);
        dialog.setContentView(R.layout.dialog_rateus);

        final RatingBar ratingBar = (RatingBar) dialog.findViewById(R.id.rb_rudialog_rateus);
        Button submit_bt = (Button) dialog.findViewById(R.id.bt_rudialog_submit);

        submit_bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Date curDate = new Date();
                SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
                String DateToStr = format.format(curDate);
                String date = DateToStr.substring(0, 10);
                String time = DateToStr.substring(11, 19);

                String rating = String.valueOf(ratingBar.getRating());
                if (rating.equals("")) {
                    toastView("please first rate us");
                } else {
                    sendRating(rating, date, time, dialog);
                }
            }
        });

        dialog.show();
    }

    private void sendRating(final String rating, final String date, final String time, final Dialog dialog) {

        final ProgressDialog pd = new ProgressDialog(ctx);
        pd.setMessage("loading");
        pd.setCancelable(false);
        pd.show();
        RequestQueue queue = Volley.newRequestQueue(ctx);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, WebAPI.URL_RATING,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        pd.dismiss();
                        try {
                            JSONObject data_obj = new JSONObject(response);
                            String success = data_obj.getString("success");
                            String msg = data_obj.getString("msg");

                            if (success.equals("1") && msg.equals("Rating submited Successfully")) {
                                toastView("thanks for Given Rating to us");
                                ShopDetailFragment detailFragment = new ShopDetailFragment(ctx);
                                setFragment(detailFragment);
                                dialog.dismiss();
                            } else if (success.equals("0") && msg.equals("Feedback Already Submitted")) {
                                toastView("Feedback Already Submitted");
                                dialog.dismiss();
                                dialog.dismiss();
                            } else {
                                toastView("Something went wrong please try again");
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(ctx, error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("business_id", buisness_id);
                params.put("user_id", user_id);
                params.put("rating", rating);
                params.put("review", "");
                params.put("rating_date", date);
                params.put("rating_time", time);
                return params;
            }
        };

        queue.add(stringRequest);
    }

    private void setFragment(Fragment fragment) {

        final android.app.FragmentTransaction ft = getFragmentManager().beginTransaction();
        ft.replace(R.id.framelayout, fragment);
        ft.commit();
    }

    private void saveFav() {

        HelperManager helperManager = new HelperManager(ctx);

        BuisnessList buisness = new BuisnessList(buisness_id, status_1, buisness_name_1, building_1, street_1, estb_year_1, area_1, city_1, pincode_1, state_1, contact_person_title_1, contact_person_name_1
                , designation_1, landline_no1_1, landline_no2_1, mobile_no1_1, mobile_no2_1, email_id_1, website_1, lat_1, set_long_1, image_1, rating_1, distance_1, cate_icon_1);
//        Favourites favourites = new Favourites(id_1, title_1, address_1, contact_1, lat_1, long_1, image_1);

        boolean check = helperManager.insert(buisness);
        if (check) {
            toastView("successfully added in favourites");
            addfave_iv.setImageResource(R.drawable.heart);
            check_fav = false;
        }
    }

    private double distance(double lat1, double lon1, double lat2, double lon2) {
        double theta = lon1 - lon2;
        double dist = Math.sin(deg2rad(lat1))
                * Math.sin(deg2rad(lat2))
                + Math.cos(deg2rad(lat1))
                * Math.cos(deg2rad(lat2))
                * Math.cos(deg2rad(theta));
        dist = Math.acos(dist);
        dist = rad2deg(dist);
        dist = dist * 60 * 1.1515;
        return (dist);
    }

    private double deg2rad(double deg) {
        return (deg * Math.PI / 180.0);
    }

    private double rad2deg(double rad) {
        return (rad * 180.0 / Math.PI);
    }
}
