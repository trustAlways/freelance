package info.android.technologies.indoreconnect.fragment;

import android.app.Dialog;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.webkit.WebView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import info.android.technologies.indoreconnect.R;
import info.android.technologies.indoreconnect.activity.MapsActivity;
import info.android.technologies.indoreconnect.adapter.Adp_AllBuisnessListing;
import info.android.technologies.indoreconnect.adapter.Adp_Search;
import info.android.technologies.indoreconnect.adapter.SubInfoCateAdapter;
import info.android.technologies.indoreconnect.model.AllBuissness;
import info.android.technologies.indoreconnect.model.Search;
import info.android.technologies.indoreconnect.model.SubSubInfoCategory;
import info.android.technologies.indoreconnect.util.AutocompleteCustomArrayAdapter;
import info.android.technologies.indoreconnect.util.ConnectionDetector;
import info.android.technologies.indoreconnect.util.CustomAutoCompleteView;
import info.android.technologies.indoreconnect.util.SessionManager;
import info.android.technologies.indoreconnect.util.WebAPI;

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
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

/**
 * Created by kamlesh on 1/4/2018.
 */
public class SubSubInfoCategoryFragment extends Fragment implements AdapterView.OnItemClickListener, View.OnClickListener {
    Context ctx;
    String id, icon;
    View view;
    ListView listView;
    ImageView search_iv;
    Button nearme_bt;

    CustomAutoCompleteView actv_info;

    ArrayList<SubSubInfoCategory> suninfo_cate_list;

    ConnectionDetector connectionDetector;
    SessionManager sessionManager;

    public SubSubInfoCategoryFragment(Context ctx) {
        this.ctx = ctx;
        connectionDetector = new ConnectionDetector();
        sessionManager = new SessionManager(ctx);
        suninfo_cate_list = new ArrayList<>();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.subinfocategory, null);
        initxml();
        setSearching();
        id = sessionManager.getData(SessionManager.KEY_SUB_SUB_ID);
        icon = sessionManager.getData(SessionManager.KEY_SUB_SUB_ICON);

        boolean internet = connectionDetector.isConnected();
        if (internet) {
            loadSubInfoCategory();
        } else {
            toastView("No internet Connection");
        }
        listView.setOnItemClickListener(this);
        return view;
    }

    private void setSearching() {

        String response = sessionManager.getData(SessionManager.KEY_SEARCH_DATA);
        final ArrayList<Search> search_list = new ArrayList<>();
        ArrayList<String> search_name_list = new ArrayList<>();

        try {
            JSONArray array = new JSONArray(response);
            for (int i = 0; i < array.length(); i++) {
                JSONObject object = array.getJSONObject(i);
                String id = object.getString("id");
                String name = object.getString("name");
                String type = object.getString("type");
                search_list.add(new Search(name, id, type));
                search_name_list.add(name);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

//        ArrayAdapter adapter = new ArrayAdapter<String>(ctx, android.R.layout.simple_dropdown_item_1line, search_name_list);
//        Adp_Search adapter = new Adp_Search(ctx, R.layout.adp_search, search_name_list);
        AutocompleteCustomArrayAdapter adapter = new AutocompleteCustomArrayAdapter(ctx, R.layout.custom_actv, search_name_list);
        actv_info.setThreshold(1);
        actv_info.setAdapter(adapter);
        actv_info.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                String selectd = actv_info.getText().toString();
                String id1 = "";
                String type = "";
                for (int i = 0; i < search_list.size(); i++) {

                    if (selectd.equals(search_list.get(i).getName())) {
                        id1 = search_list.get(i).getId();
                        type = search_list.get(i).getType();
                    }
                }
                sessionManager.setData(SessionManager.KEY_SEARCH_ID, id1);
                sessionManager.setData(SessionManager.KEY_SEARCH_TYPE, type);
                sessionManager.setData(SessionManager.KEY_SEARCH_FROM, "info");
                Search2Fragment searchFragment = new Search2Fragment(ctx);
                setFragment(searchFragment);
            }
        });
    }

    private void setFragment(Fragment fragment) {

        final android.app.FragmentTransaction ft = getFragmentManager().beginTransaction();
        ft.replace(R.id.framelayout, fragment);
        ft.commit();
    }

    private void toastView(String s) {
        Toast.makeText(ctx, s, Toast.LENGTH_LONG).show();
    }

    private void initxml() {
        nearme_bt = (Button) view.findViewById(R.id.bt_subsubinfo_nearme);
        search_iv = (ImageView) view.findViewById(R.id.iv_subsubinfo_search);
        listView = (ListView) view.findViewById(R.id.lv_subinfocate_listview);
        actv_info = (CustomAutoCompleteView) view.findViewById(R.id.actv_infosubsubcate_searching);

        search_iv.setOnClickListener(this);
        nearme_bt.setOnClickListener(this);
    }

    private void loadSubInfoCategory() {

        final double from_latitude, from_longitude;

        from_latitude = sessionManager.getDouble(SessionManager.KEY_LAT);
        from_longitude = sessionManager.getDouble(SessionManager.KEY_LONG);

        String url = "";
        boolean check = sessionManager.getboolean(SessionManager.SUBSUBINFOCATE);
        if (check) {
            String search_id = sessionManager.getData(SessionManager.KEY_SEARCH_ID);
            url = WebAPI.URL_INFO_SUB_SUB_CATEGORY + search_id;
            sessionManager.setboolean(SessionManager.SUBSUBINFOCATE, false);
        } else {
            url = WebAPI.URL_INFO_SUB_SUB_CATEGORY + id;
        }

        final ProgressDialog pd = new ProgressDialog(ctx);
        pd.setMessage("wait..");
        pd.setCancelable(false);
        pd.show();
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        pd.dismiss();
                        try {
                            JSONArray dataarray = new JSONArray(response);
                            for (int i = 0; i < dataarray.length(); i++) {
                                JSONObject object = dataarray.getJSONObject(i);
                                String title = object.getString("name");
                                String icon = object.getString("icon");
                                String type = object.getString("type");
                                String contact = object.getString("contact");
                                String alt_contact = object.getString("altcno");
                                String address = object.getString("address");
                                String descrption = object.getString("description");
                                String location = object.getString("location");
                                String category_icon = object.getString("category_icon");

                                Double latitude = 0.0;
                                Double longitude = 0.0;
                                try {
                                    latitude = object.getDouble("lat");
                                    longitude = object.getDouble("set_long");
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }

                                LatLng from = new LatLng(from_latitude, from_longitude);
                                LatLng to = new LatLng(latitude, longitude);

                                Double distance = SphericalUtil.computeDistanceBetween(from, to);
                                double distance_km = distance / 1000;

                                suninfo_cate_list.add(new SubSubInfoCategory(title, icon, type, contact, address, descrption, location, latitude, longitude, distance, alt_contact, category_icon));
                            }
                            setAdapter();
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
                });
        RequestQueue requestQueue = Volley.newRequestQueue(ctx);
        requestQueue.add(stringRequest);
    }

    private void setAdapter() {

        SubInfoCateAdapter adapter = new SubInfoCateAdapter(ctx, suninfo_cate_list);
        listView.setAdapter(adapter);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

        String type = suninfo_cate_list.get(position).getType();
        if (type.equals("contact")) {
            if (suninfo_cate_list.get(position).getContact().equals("0")) {
                toastView("Contact number not available");
            } else {
                String contact = suninfo_cate_list.get(position).getContact();
                String alt_contact = suninfo_cate_list.get(position).getAlt_contact();

                ArrayList<String> contact_list = new ArrayList<>();
                if (!contact.equals("null")) {
                    contact_list.add(contact);
                }
                if (!alt_contact.equals("")) {
                    contact_list.add(alt_contact);
                }
                if (contact_list.size() == 0) {
                    toastView("no contact available");
                } else {
                    contactDialog(contact_list);
                }
            }
//            showDialog(position);
        } else {
            showDialog(position);
        }
    }

    public void showDialog(final int pos) {

        final Dialog dialog = new Dialog(ctx);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(true);
        dialog.setContentView(R.layout.dialog_details);

        TextView title_tv = (TextView) dialog.findViewById(R.id.tv_dialog_sic_title);
        TextView descrip_tv = (TextView) dialog.findViewById(R.id.tv_dialog_sic_description);
        WebView webView = (WebView) dialog.findViewById(R.id.wv_dialog_description);
        TextView contact_tv = (TextView) dialog.findViewById(R.id.tv_dialog_sic_contact);
        TextView address_tv = (TextView) dialog.findViewById(R.id.tv_dialog_sic_address);
        TextView distsnce_tv = (TextView) dialog.findViewById(R.id.tv_dialog_distance);

        LinearLayout contact_ll = (LinearLayout) dialog.findViewById(R.id.ll_infodetails_calling);
        LinearLayout address_ll = (LinearLayout) dialog.findViewById(R.id.ll_infodetails_address);

        ImageView imageView = (ImageView) dialog.findViewById(R.id.iv_dialog_sic_image);

        title_tv.setText(suninfo_cate_list.get(pos).getTitle());
        descrip_tv.setText(suninfo_cate_list.get(pos).getDiscription());
        final String contact = suninfo_cate_list.get(pos).getContact();
        final String alt_contact = suninfo_cate_list.get(pos).getAlt_contact();

        webView.getSettings().setJavaScriptEnabled(true);
        webView.loadDataWithBaseURL("", suninfo_cate_list.get(pos).getDiscription(), "text/html", "UTF-8", "");

        if (contact.equals("")) {
            contact_ll.setVisibility(View.GONE);
        } else if (!contact.equals("0") && !alt_contact.equals("")) {
            contact_tv.setText(suninfo_cate_list.get(pos).getContact() + "," + alt_contact);
        } else if (!contact.equals("0") && alt_contact.equals("")) {
            contact_tv.setText(suninfo_cate_list.get(pos).getContact() + "");
        } else if (contact.equals("0") && alt_contact.equals("")) {
            contact_ll.setVisibility(View.GONE);
        } else {
            contact_tv.setText(suninfo_cate_list.get(pos).getContact());
        }


        String location = suninfo_cate_list.get(pos).getAddress();
        if (location.equals("")) {
            address_ll.setVisibility(View.GONE);
        } else {
            address_tv.setText(suninfo_cate_list.get(pos).getAddress());
        }

        final double latitude = sessionManager.getDouble(SessionManager.KEY_LAT);
        final double longitude = sessionManager.getDouble(SessionManager.KEY_LONG);

        double lat_1 = suninfo_cate_list.get(pos).getLatitude();
        double long_1 = suninfo_cate_list.get(pos).getLongitude();

        double lat_buiss = 0;
        double long_buisn = 0;
        try {
            lat_buiss = suninfo_cate_list.get(pos).getLatitude();
            long_buisn = suninfo_cate_list.get(pos).getLongitude();
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }

        final double finalLat_buiss = lat_buiss;
        final double finalLong_buisn = long_buisn;
        address_ll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

//                goDirection(latitude, longitude, finalLat_buiss, finalLong_buisn);
                Uri mapUri = Uri.parse("geo:0,0?q=" + finalLat_buiss + "," + finalLong_buisn + "(" + suninfo_cate_list.get(pos).getTitle() + ")");
                Intent mapIntent = new Intent(Intent.ACTION_VIEW, mapUri);
                mapIntent.setPackage("com.google.android.apps.maps");
                startActivity(mapIntent);
            }
        });

        contact_ll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                ArrayList<String> contact_list = new ArrayList<>();
                if (!contact.equals("")) {
                    contact_list.add(contact);
                }
                if (!alt_contact.equals("")) {
                    contact_list.add(alt_contact);
                }
                if (contact_list.size() == 0) {
                    toastView("no contact available");
                } else {
                    contactDialog(contact_list);
                }
//                Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + contact));
//                startActivity(intent);
            }
        });

        double buis_lat = suninfo_cate_list.get(pos).getLatitude();
        double buis_long = suninfo_cate_list.get(pos).getLongitude();

        DecimalFormat df2 = new DecimalFormat(".##");
        double dis = distance(latitude, longitude, buis_lat, buis_long);
        distsnce_tv.setText(df2.format(dis) + " KM");

        String url = suninfo_cate_list.get(pos).getIcon();
        String parent_url = suninfo_cate_list.get(pos).getCategory_icon();
        if (!url.equals("")) {
            String icon_url = WebAPI.BASE_URL + "admin/img/category_icon/" + url;
            icon_url = icon_url.replace(" ", "%20");
            Picasso.with(ctx)
                    .load(icon_url)
                    .resize(300, 300)
                    .placeholder(R.drawable.ic_logo)
                    .error(R.drawable.ic_logo)
                    .into(imageView);
        } else if (!parent_url.equals("")) {
            String icon_url2 = WebAPI.BASE_URL + "admin/img/category_icon/" + parent_url;
            icon_url2 = icon_url2.replace(" ", "%20");
            Picasso.with(ctx)
                    .load(icon_url2)
                    .placeholder(R.drawable.blue_ic_icon)
                    .error(R.drawable.blue_ic_icon)
                    .into(imageView);
        }
        dialog.show();
    }

    private void goDirection(double latitude, double longitude, double buis_lat, double buis_long) {


        startActivity(new Intent(ctx, MapsActivity.class)
                .putExtra("fromlat", latitude)
                .putExtra("fromlong", longitude)
                .putExtra("tolat", buis_lat)
                .putExtra("tolong", buis_long)
        );
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

    @Override
    public void onClick(View v) {

        switch (v.getId()) {

            case R.id.iv_subsubinfo_search:
                String text = actv_info.getText().toString();
                if (!text.equals("")) {
                    sessionManager.setData(SessionManager.KEY_PAGE, "infosub");
                    sessionManager.setData(SessionManager.KEY_SEARCH_TEXT, text);
                    sessionManager.setData(SessionManager.KEY_ALL_BUISNESS_TYPE, "search");
                    AllBuisnessListingFragment allBuisnessListingFragment = new AllBuisnessListingFragment(ctx);
                    setFragment(allBuisnessListingFragment);
                } else {
                    toastView("please enter text in search");
                }
                break;

            case R.id.bt_subsubinfo_nearme:

                if (suninfo_cate_list.size() != 0) {
                    Collections.sort(suninfo_cate_list, new distanceComp());

                    SubInfoCateAdapter adapter = new SubInfoCateAdapter(ctx, suninfo_cate_list);
                    listView.setAdapter(adapter);
                }
                break;
        }
    }

    class distanceComp implements Comparator<SubSubInfoCategory> {

        @Override
        public int compare(SubSubInfoCategory e1, SubSubInfoCategory e2) {
            if (e1.getDistance() > e2.getDistance()) {
                return 1;
            } else {
                return -1;
            }
        }
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


}
