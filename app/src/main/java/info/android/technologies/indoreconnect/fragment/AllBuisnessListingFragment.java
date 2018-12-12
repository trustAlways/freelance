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

import info.android.technologies.indoreconnect.R;
import info.android.technologies.indoreconnect.adapter.Adp_AllBuisnessListing;
import info.android.technologies.indoreconnect.adapter.Adp_Search;
import info.android.technologies.indoreconnect.model.AllBuissness;
import info.android.technologies.indoreconnect.model.Search;
import info.android.technologies.indoreconnect.util.AutocompleteCustomArrayAdapter;
import info.android.technologies.indoreconnect.util.ConnectionDetector;
import info.android.technologies.indoreconnect.util.CustomAutoCompleteView;
import info.android.technologies.indoreconnect.util.SessionManager;
import info.android.technologies.indoreconnect.util.WebAPI;

/**
 * Created by kamlesh on 12/1/2017.
 */
public class AllBuisnessListingFragment extends Fragment implements View.OnClickListener {

    Context ctx;
    View view;
    CustomAutoCompleteView actv_search;
    ImageView search_iv;
    Button nearme_bt, rating_bt, popularity_bt;
    RecyclerView recyclerView;

    double latitude, longitude;
    ArrayList<AllBuissness> buiss_list;
    ArrayList<String> buiss_id_list;

    SessionManager sessionManager;
    ConnectionDetector connectionDetector;

    public AllBuisnessListingFragment(Context ctx) {
        this.ctx = ctx;
        sessionManager = new SessionManager(ctx);
        connectionDetector = new ConnectionDetector();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_all_bisness_list, null);
        initxml();
        setSearching();
        boolean internet = connectionDetector.isConnected();
        if (internet) {
            buiss_list = new ArrayList<>();
            buiss_id_list = new ArrayList<>();
            loadBuisnessList();
        } else {
            toastView("no internet connection");
        }
        return view;
    }

    private void initxml() {

        actv_search = (CustomAutoCompleteView) view.findViewById(R.id.actv_allbuissness_autocompletetextview);
        search_iv = (ImageView) view.findViewById(R.id.iv_allbuissness_search);
        nearme_bt = (Button) view.findViewById(R.id.bt_allbuissness_nearme);
        rating_bt = (Button) view.findViewById(R.id.bt_allbuissness_rating);
        popularity_bt = (Button) view.findViewById(R.id.bt_allbuissness_popularity);
        recyclerView = (RecyclerView) view.findViewById(R.id.rv_allbuisness_recycleview);

        latitude = sessionManager.getDouble(SessionManager.KEY_LAT);
        longitude = sessionManager.getDouble(SessionManager.KEY_LONG);

        search_iv.setOnClickListener(this);
        nearme_bt.setOnClickListener(this);
        rating_bt.setOnClickListener(this);
        popularity_bt.setOnClickListener(this);
    }

    public void loadBuisnessList() {

        String category_id = sessionManager.getData(SessionManager.KEY_MAIN_BUISNESS_ID);
        String search_text = "";
        String url_type = "";
        search_text = sessionManager.getData(SessionManager.KEY_SEARCH_TEXT);
        url_type = sessionManager.getData(SessionManager.KEY_ALL_BUISNESS_TYPE);
        String url;

        if (url_type.equals("search")) {
            url = WebAPI.URL_SEARCH_KEYWORD + search_text;
        } else {
            url = WebAPI.URL_BUSINESS_LIST + category_id;
        }
/* normal_search

        try {
            if (!search_text.equals(null)) {
                url = WebAPI.URL_SEARCH_KEYWORD + search_text;
            } else {
                url = WebAPI.URL_BUSINESS_LIST + category_id;
            }
        } catch (Exception e) {
            e.printStackTrace();
            url = WebAPI.URL_BUSINESS_LIST + category_id;
        }*/
        final ProgressDialog pd = new ProgressDialog(ctx);
        pd.setMessage("wait..");
        pd.setCancelable(false);
        pd.show();
        final String finalSearch_text = search_text;
        final String finalUrl_type = url_type;
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        pd.dismiss();
                        /*try {
                            if (!finalSearch_text.equals("")) {
                                setSearchingData(response);
                            } else {
                                setBuisnessData(response);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            setBuisnessData(response);
                        }*/
                        if (finalUrl_type.equals("search")) {
                            setSearchingData(response);
                        } else {
                            setBuisnessData(response);
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


    private void setSearchingData(String response) {

        sessionManager.setData(SessionManager.KEY_SEARCH_TEXT, "");
        try {
            JSONObject jsonObject = new JSONObject(response);
            try {
                JSONArray main_array = jsonObject.getJSONArray("blist");

                if (!main_array.equals(null)) {

                    if (main_array.length() != 0) {

                        for (int i = 0; i < main_array.length(); i++) {
                            JSONObject buisnness_obj = main_array.getJSONObject(i);
                            JSONObject ratimg_obj = buisnness_obj.getJSONObject("0");

                            float rat = 0;
                            try {
                                rat = Float.parseFloat(ratimg_obj.getString("rating"));
                            } catch (NumberFormatException e) {
                                e.printStackTrace();
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                            String buisness_id = buisnness_obj.getString("buisness_id");
                            String buisness_name = buisnness_obj.getString("buisness_name");
                            String building = buisnness_obj.getString("building");
                            String city = buisnness_obj.getString("city");
                            String pincode = buisnness_obj.getString("pincode");
                            String address = building + " " + city + ",(M.P) " + pincode;

                            String mobile_no1 = buisnness_obj.getString("mobile_no1");
                            double lat = buisnness_obj.getDouble("lat");
                            double set_long = buisnness_obj.getDouble("set_long");
                            String image = buisnness_obj.getString("image");
                            String cate_icon = "";

                            LatLng from = new LatLng(latitude, longitude);
                            LatLng to = new LatLng(lat, set_long);

                            Double distance = SphericalUtil.computeDistanceBetween(from, to);
                            double distance_km = distance / 1000;

                            if (!buiss_id_list.contains(buisness_id)) {
                                buiss_id_list.add(buisness_id);
                                buiss_list.add(new AllBuissness(buisness_id, "main", image, buisness_name, address, mobile_no1, lat, set_long, rat, distance_km, cate_icon));
                            }
                        }
                    }
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }

            JSONArray info_array = jsonObject.getJSONArray("infolist");
            if (info_array.length() != 0) {

                for (int i = 0; i < info_array.length(); i++) {
                    JSONObject buisnness_obj = info_array.getJSONObject(i);

                    String buisness_id = buisnness_obj.getString("category_info_id");
                    String buisness_name = buisnness_obj.getString("name");
                    String address = buisnness_obj.getString("address");

                    String mobile_no1 = buisnness_obj.getString("contact");
                    double lat = 0;
                    double set_long = 0;
                    try {
                        lat = buisnness_obj.getDouble("lat");
                        set_long = buisnness_obj.getDouble("set_long");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    String image = buisnness_obj.getString("icon");

                    LatLng from = new LatLng(latitude, longitude);
                    LatLng to = new LatLng(lat, set_long);

                    Double distance = SphericalUtil.computeDistanceBetween(from, to);
                    double distance_km = distance / 1000;

                    String lati = String.valueOf(lat);
                    String longi = String.valueOf(set_long);

                    if (!buiss_id_list.contains(buisness_id)) {

                        buiss_id_list.add(buisness_id);
                        if (lati.equals("") || longi.equals("")) {
                            buiss_list.add(new AllBuissness(buisness_id, "info", image, buisness_name, address, mobile_no1, 0, 0, 5, distance_km, ""));
                        } else {
                            buiss_list.add(new AllBuissness(buisness_id, "info", image, buisness_name, address, mobile_no1, lat, set_long, 5, distance_km, ""));
                        }
                    }
                }
            }
            setBuisness();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void setBuisnessData(String response) {

        try {
            JSONArray dataarray = new JSONArray(response);
            for (int i = 0; i < dataarray.length(); i++) {
                JSONObject buisnness_obj = dataarray.getJSONObject(i);
                JSONObject ratimg_obj = buisnness_obj.getJSONObject("0");

                float rat = 0;
                try {
                    rat = Float.parseFloat(ratimg_obj.getString("rating"));
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                String buisness_id = buisnness_obj.getString("buisness_id");
                String buisness_name = buisnness_obj.getString("buisness_name");
                String building = buisnness_obj.getString("building");
                String city = buisnness_obj.getString("city");
                String pincode = buisnness_obj.getString("pincode");
                String address = building + " " + city + ",(M.P) " + pincode;

                String mobile_no1 = buisnness_obj.getString("mobile_no1");
                double lat = buisnness_obj.getDouble("lat");
                double set_long = buisnness_obj.getDouble("set_long");
                String image = buisnness_obj.getString("image");
                String cate_icon = buisnness_obj.getString("category_icon");

                LatLng from = new LatLng(latitude, longitude);
                LatLng to = new LatLng(lat, set_long);

                Double distance = SphericalUtil.computeDistanceBetween(from, to);
                double distance_km = distance / 1000;

                buiss_list.add(new AllBuissness(buisness_id, "main", image, buisness_name, address, mobile_no1, lat, set_long, rat, distance_km, cate_icon));
            }
            setBuisness();
        } catch (JSONException e) {
            toastView("Data not available");
            e.printStackTrace();
        }
    }

    private void setBuisness() {

        final Adp_AllBuisnessListing adp_shopListing = new Adp_AllBuisnessListing(buiss_list, ctx);

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(ctx.getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setAdapter(adp_shopListing);

        recyclerView.addOnItemTouchListener(
                new SearchFragment.RecyclerItemClickListener(ctx, recyclerView, new SearchFragment.RecyclerItemClickListener.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {

                        if (buiss_list.get(position).getType().equals("main")) {

                            sessionManager.setData(SessionManager.KEY_PAGE, "allbuiss");
                            sessionManager.setData(SessionManager.KEY_BUISNESS_ID, buiss_list.get(position).getId());
                            ShopDetailFragment detailFragment = new ShopDetailFragment(ctx);
                            setFragment(detailFragment);
                        } else {

                            boolean internet = connectionDetector.isConnected();
                            if (internet) {
                                loadData(position);
                            } else {
                                toastView("no internet connection");
                            }
//                            InfoDialogFragment infoDialogFragment = new InfoDialogFragment(ctx, buiss_list.get(position).getId());
//                            setFragment(infoDialogFragment);
                        }
                    }

                    @Override
                    public void onLongItemClick(View view, int position) {
                        // do whatever
                    }
                })
        );
    }

    private void loadData(int position) {

        final ProgressDialog pd = new ProgressDialog(ctx);
        pd.setMessage("wait..");
        pd.setCancelable(false);
        pd.show();
        StringRequest stringRequest = new StringRequest(Request.Method.GET, WebAPI.URL_INFO_BUSINESS_DETAILS + buiss_list.get(position).getId(),
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        pd.dismiss();
                        try {
                            JSONArray array = new JSONArray(response);
                            JSONObject object = array.getJSONObject(0);

                            String name = object.getString("name");
                            String icon = object.getString("icon");
                            String type = object.getString("type");
                            String contact = object.getString("contact");
                            String address = object.getString("address");
                            String discription = object.getString("description");
                            double lat = object.getDouble("lat");
                            double longi = object.getDouble("set_long");
                            String altcno = object.getString("altcno");

                            showDialog(name, icon, type, contact, address, discription, lat, longi, altcno);
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


    public void showDialog(final String name, String icon, String type, final String contact, String address, String discription, double lat, double longi, final String altcno) {

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

        title_tv.setText(name);
        descrip_tv.setText(discription);

        webView.getSettings().setJavaScriptEnabled(true);
        webView.loadDataWithBaseURL("", discription, "text/html", "UTF-8", "");

        if (contact.equals("")) {
            contact_ll.setVisibility(View.GONE);
        } else if (!contact.equals("0") && !altcno.equals("")) {
            contact_tv.setText(contact + "," + altcno);
        } else if (!contact.equals("0") && altcno.equals("")) {
            contact_tv.setText(contact + "");
        } else if (contact.equals("0") && altcno.equals("")) {
            contact_ll.setVisibility(View.GONE);
        } else {
            contact_tv.setText(contact);
        }


        String location = address;
        if (location.equals("")) {
            address_ll.setVisibility(View.GONE);
        } else {
            address_tv.setText(address);
        }

        final double latitude = sessionManager.getDouble(SessionManager.KEY_LAT);
        final double longitude = sessionManager.getDouble(SessionManager.KEY_LONG);

        double lat_1 = lat;
        double long_1 = longi;

        double lat_buiss = 0;
        double long_buisn = 0;
        try {
            lat_buiss = lat_1;
            long_buisn = longi;
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }

        final double finalLat_buiss = lat_buiss;
        final double finalLong_buisn = long_buisn;
        address_ll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

//                goDirection(latitude, longitude, finalLat_buiss, finalLong_buisn);
                Uri mapUri = Uri.parse("geo:0,0?q=" + finalLat_buiss + "," + finalLong_buisn + "(" + name + ")");
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
                if (!altcno.equals("")) {
                    contact_list.add(altcno);
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

        double buis_lat = lat_1;
        double buis_long = longi;

        DecimalFormat df2 = new DecimalFormat(".##");
        double dis = distance(latitude, longitude, buis_lat, buis_long);
        distsnce_tv.setText(df2.format(dis) + " KM");

        String url = icon;
//        String parent_url = suninfo_cate_list.get(pos).getCategory_icon();
        if (!url.equals("")) {
            String icon_url = WebAPI.BASE_URL + "admin/img/category_icon/" + url;
            icon_url = icon_url.replace(" ", "%20");
            Picasso.with(ctx)
                    .load(icon_url)
                    .resize(300, 300)
                    .placeholder(R.drawable.ic_logo)
                    .error(R.drawable.ic_logo)
                    .into(imageView);
        }
        dialog.show();
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


    private void toastView(String s) {
        Toast.makeText(ctx, s, Toast.LENGTH_LONG).show();
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
        actv_search.setThreshold(1);
        actv_search.setAdapter(adapter);
        actv_search.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                String selectd = actv_search.getText().toString();
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
                sessionManager.setData(SessionManager.KEY_SEARCH_FROM, "home");
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

    @Override
    public void onClick(View v) {

        switch (v.getId()) {

            case R.id.iv_allbuissness_search:
                String text = actv_search.getText().toString();
                if (!text.equals("")) {
                    sessionManager.setData(SessionManager.KEY_PAGE, "subhome");
                    sessionManager.setData(SessionManager.KEY_SEARCH_TEXT, text);
                    sessionManager.setData(SessionManager.KEY_ALL_BUISNESS_TYPE, "search");
                    AllBuisnessListingFragment allBuisnessListingFragment = new AllBuisnessListingFragment(ctx);
                    setFragment(allBuisnessListingFragment);
                } else {
                    toastView("please enter text in search");
                }
                break;

            case R.id.bt_allbuissness_nearme:

                if (buiss_list.size() != 0) {

                    Collections.sort(buiss_list, new distanceComp());

                    final Adp_AllBuisnessListing adp_shopListing = new Adp_AllBuisnessListing(buiss_list, ctx);
                    RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(ctx.getApplicationContext());
                    recyclerView.setLayoutManager(mLayoutManager);
                    recyclerView.setAdapter(adp_shopListing);
                }
                break;

            case R.id.bt_allbuissness_rating:

                if (buiss_list.size() != 0) {

                    Collections.sort(buiss_list, new ratingComp());

                    final Adp_AllBuisnessListing adp_allBuisnessListing = new Adp_AllBuisnessListing(buiss_list, ctx);
                    RecyclerView.LayoutManager mLayoutManager1 = new LinearLayoutManager(ctx.getApplicationContext());
                    recyclerView.setLayoutManager(mLayoutManager1);
                    recyclerView.setAdapter(adp_allBuisnessListing);
                }
                break;

            case R.id.bt_allbuissness_popularity:
                if (buiss_list.size() != 0) {

                    Collections.sort(buiss_list, new ratingComp());

                    final Adp_AllBuisnessListing adp_allBuisnessListing1 = new Adp_AllBuisnessListing(buiss_list, ctx);
                    RecyclerView.LayoutManager mLayoutManager11 = new LinearLayoutManager(ctx.getApplicationContext());
                    recyclerView.setLayoutManager(mLayoutManager11);
                    recyclerView.setAdapter(adp_allBuisnessListing1);
                }

                break;
        }
    }

    class ratingComp implements Comparator<AllBuissness> {

        @Override
        public int compare(AllBuissness e1, AllBuissness e2) {
            if (e1.getRating() < e2.getRating()) {
                return 1;
            } else {
                return -1;
            }
        }
    }

    class distanceComp implements Comparator<AllBuissness> {

        @Override
        public int compare(AllBuissness e1, AllBuissness e2) {
            if (e1.getDistance() > e2.getDistance()) {
                return 1;
            } else {
                return -1;
            }
        }
    }
}
