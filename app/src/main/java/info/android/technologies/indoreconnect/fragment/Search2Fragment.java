package info.android.technologies.indoreconnect.fragment;

import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.SphericalUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import info.android.technologies.indoreconnect.R;
import info.android.technologies.indoreconnect.adapter.Adp_Search;
import info.android.technologies.indoreconnect.adapter.Adp_ShopListing;
import info.android.technologies.indoreconnect.adapter.Adp_ShopSearchListing;
import info.android.technologies.indoreconnect.model.BuisnessList;
import info.android.technologies.indoreconnect.model.BuisnessListSearch;
import info.android.technologies.indoreconnect.model.Search;
import info.android.technologies.indoreconnect.util.AutocompleteCustomArrayAdapter;
import info.android.technologies.indoreconnect.util.ConnectionDetector;
import info.android.technologies.indoreconnect.util.CustomAutoCompleteView;
import info.android.technologies.indoreconnect.util.SessionManager;
import info.android.technologies.indoreconnect.util.WebAPI;

/**
 * Created by kamlesh on 12/1/2017.
 */
public class Search2Fragment extends Fragment implements View.OnClickListener {

    Context ctx;
    View view;
    ImageView search_iv;
    // components
    RecyclerView recyclerView;
    GridView gridView;
    ListView listView;
    Button near_me_bt, rating_bt, popularity_bt;
    CustomAutoCompleteView autoCompleteTextView;

    ConnectionDetector connectionDetector;
    SessionManager sessionManager;

    ArrayList<String> namelist;
    ArrayList<Search> search_list;

    ArrayList<BuisnessList> buisnessLists;
    ArrayList<BuisnessListSearch> searchList;

    public Search2Fragment(Context ctx) {
        this.ctx = ctx;
        connectionDetector = new ConnectionDetector();
        sessionManager = new SessionManager(ctx);
        namelist = new ArrayList<>();
        search_list = new ArrayList<>();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_search, null);
        initxml();
        setTvAdapter();
        otherPageView();
        return view;
    }

    private void otherPageView() {

        String search_type = sessionManager.getData(SessionManager.KEY_SEARCH_FROM);
        if (search_type.equals("home")) {

            String search_id = sessionManager.getData(SessionManager.KEY_SEARCH_ID);
            String search_view = sessionManager.getData(SessionManager.KEY_SEARCH_TYPE);

            if (search_view.equals("Businesslist")) {
                sessionManager.setData(SessionManager.KEY_PAGE, "subhome");

                sessionManager.setData(SessionManager.KEY_BUISNESS_ID, search_id);
                ShopDetailFragment shopDetailFragment = new ShopDetailFragment(ctx);
                setFragment(shopDetailFragment);
            } else if (search_view.equals("InfoBusinesslist")) {

                sessionManager.setData(SessionManager.KEY_PAGE, "subhome");
                InfoDialogFragment infoDialogFragment = new InfoDialogFragment(ctx, search_id);
                setFragment(infoDialogFragment);
            } else if (search_view.equals("InfoSubCategory")) {
                sessionManager.setData(SessionManager.KEY_PAGE, "subhome");
                setSubInfoCate();
            } else if (search_view.equals("MainSubCategory")) {
                sessionManager.setData(SessionManager.KEY_PAGE, "subhome");
                sessionManager.setData(SessionManager.KEY_MAIN_BUISNESS_ID, search_id);
                sessionManager.setData(SessionManager.KEY_ALL_BUISNESS_TYPE, "other");
                AllBuisnessListingFragment allBuisnessListingFragment = new AllBuisnessListingFragment(ctx);
                setFragment(allBuisnessListingFragment);
//                setMainSubCate(search_id);
            }
        } else if (search_type.equals("info")) {

            String search_id = sessionManager.getData(SessionManager.KEY_SEARCH_ID);
            String search_view = sessionManager.getData(SessionManager.KEY_SEARCH_TYPE);

            if (search_view.equals("Businesslist")) {
                sessionManager.setData(SessionManager.KEY_PAGE, "info");
                sessionManager.setData(SessionManager.KEY_BUISNESS_ID, search_id);
                ShopDetailFragment shopDetailFragment = new ShopDetailFragment(ctx);
                setFragment(shopDetailFragment);
            } else if (search_view.equals("InfoBusinesslist")) {

                sessionManager.setData(SessionManager.KEY_PAGE, "info");
                InfoDialogFragment infoDialogFragment = new InfoDialogFragment(ctx, search_id);
                setFragment(infoDialogFragment);
            } else if (search_view.equals("InfoSubCategory")) {
                sessionManager.setData(SessionManager.KEY_PAGE, "info");
                setSubInfoCate();
            } else if (search_view.equals("MainSubCategory")) {
                sessionManager.setData(SessionManager.KEY_PAGE, "info");
                sessionManager.setData(SessionManager.KEY_MAIN_BUISNESS_ID, search_id);
                sessionManager.setData(SessionManager.KEY_ALL_BUISNESS_TYPE, "other");
                AllBuisnessListingFragment allBuisnessListingFragment = new AllBuisnessListingFragment(ctx);
                setFragment(allBuisnessListingFragment);
//                setMainSubCate(search_id);
            }
        } else {
            sessionManager.setData(SessionManager.KEY_PAGE, "subhome");
        }
    }

    private void setSubInfoCate() {

        sessionManager.setboolean(SessionManager.SUBSUBINFOCATE, true);
        SubSubInfoCategoryFragment subSubInfoCategoryFragment = new SubSubInfoCategoryFragment(ctx);
        setFragment(subSubInfoCategoryFragment);
    }

    private void setTvAdapter() {
        String response = sessionManager.getData(SessionManager.KEY_SEARCH_DATA);
        try {
            JSONArray array = new JSONArray(response);
            for (int i = 0; i < array.length(); i++) {
                JSONObject object = array.getJSONObject(i);
                String id = object.getString("id");
                String type = object.getString("type");
                String name = object.getString("name");
                namelist.add(name);
                search_list.add(new Search(name, id, type));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
//        ArrayAdapter adapter = new ArrayAdapter<String>(ctx, android.R.layout.simple_dropdown_item_1line, namelist);
//        Adp_Search adapter = new Adp_Search(ctx, R.layout.adp_search, namelist);
        AutocompleteCustomArrayAdapter adapter = new AutocompleteCustomArrayAdapter(ctx, R.layout.custom_actv, namelist);
        autoCompleteTextView.setThreshold(1);
        autoCompleteTextView.setAdapter(adapter);
        autoCompleteTextView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String selectd = autoCompleteTextView.getText().toString();
                String id1 = "";
                String view_type = "";

                for (int i = 0; i < search_list.size(); i++) {

                    if (selectd.equals(search_list.get(i).getName())) {
                        id1 = search_list.get(i).getId();
                        view_type = search_list.get(i).getType();
                    }
                }
                if (view_type.equals("Businesslist")) {
                    sessionManager.setData(SessionManager.KEY_PAGE, "search");
                    sessionManager.setData(SessionManager.KEY_BUISNESS_ID, id1);
                    ShopDetailFragment shopDetailFragment = new ShopDetailFragment(ctx);
                    setFragment(shopDetailFragment);
                } else if (view_type.equals("InfoBusinesslist")) {
                    sessionManager.setData(SessionManager.KEY_PAGE, "search");
                    InfoDialogFragment infoDialogFragment = new InfoDialogFragment(ctx, id1);
                    setFragment(infoDialogFragment);
                } else if (view_type.equals("InfoSubCategory")) {
                    setSubInfoCate();
                } else if (view_type.equals("MainSubCategory")) {
//                    setMainSubCate(id1);
                    sessionManager.setData(SessionManager.KEY_MAIN_BUISNESS_ID, id1);
                    sessionManager.setData(SessionManager.KEY_ALL_BUISNESS_TYPE, "other");
                    AllBuisnessListingFragment allBuisnessListingFragment = new AllBuisnessListingFragment(ctx);
                    setFragment(allBuisnessListingFragment);
                }
            }
        });
    }

    private void setMainSubCate(String id1) {

        listView.setVisibility(View.GONE);
        gridView.setVisibility(View.GONE);
        recyclerView.setVisibility(View.VISIBLE);

        buisnessLists = new ArrayList<>();

        final ProgressDialog pd = new ProgressDialog(ctx);
        pd.setMessage("wait..");
        pd.setCancelable(false);
        pd.show();
        StringRequest stringRequest = new StringRequest(Request.Method.GET, WebAPI.URL_BUSINESS_LIST + id1,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        pd.dismiss();
                        try {
                            JSONArray dataarray = new JSONArray(response);
                            for (int i = 0; i < dataarray.length(); i++) {
                                JSONObject buisnness_obj = dataarray.getJSONObject(i);

                                String buisness_id = buisnness_obj.getString("buisness_id");
                                String status = buisnness_obj.getString("status");
                                String buisness_name = buisnness_obj.getString("buisness_name");
                                String building = buisnness_obj.getString("building");
                                String street = buisnness_obj.getString("street");
                                String estb_year = buisnness_obj.getString("year_esatablishing");
                                String area = buisnness_obj.getString("area");
                                String city = buisnness_obj.getString("city");
                                String pincode = buisnness_obj.getString("pincode");
                                String state = buisnness_obj.getString("state");
                                String contact_person_title = buisnness_obj.getString("contact_person_title");
                                String contact_person_name = buisnness_obj.getString("contact_person_name");
                                String designation = buisnness_obj.getString("designation");
                                String landline_no1 = buisnness_obj.getString("landline_no1");
                                String landline_no2 = buisnness_obj.getString("landline_no2");
                                String mobile_no1 = buisnness_obj.getString("mobile_no1");
                                String mobile_no2 = buisnness_obj.getString("mobile_no2");
                                String email_id = buisnness_obj.getString("email_id");
                                String website = buisnness_obj.getString("website");
                                String lat = buisnness_obj.getString("lat");
                                String set_long = buisnness_obj.getString("set_long");
                                String image = buisnness_obj.getString("image");

                                JSONObject ratimg_obj = buisnness_obj.getJSONObject("0");
                                String rat = ratimg_obj.getString("rating");
                                String rating = rat.substring(0, 1);

                                String cate_icon = buisnness_obj.getString("category_icon");
                                float distance = (float) 0.0;
                                buisnessLists.add(new BuisnessList(buisness_id, status, buisness_name, building, street, estb_year, area, city, pincode
                                        , state, contact_person_title, contact_person_name, designation, landline_no1, landline_no2, mobile_no1, mobile_no2
                                        , email_id, website, lat, set_long, image, rating, distance, cate_icon));
                            }
                            setBuisness();
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

    private void setBuisness() {

        double latitude = sessionManager.getDouble(SessionManager.KEY_LAT);
        double longitude = sessionManager.getDouble(SessionManager.KEY_LONG);

        final SessionManager sessionManager = new SessionManager(ctx);

        final Adp_ShopListing adp_shopListing = new Adp_ShopListing(buisnessLists, ctx, latitude, longitude);

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(ctx.getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setAdapter(adp_shopListing);

        recyclerView.addOnItemTouchListener(
                new SearchFragment.RecyclerItemClickListener(ctx, recyclerView, new SearchFragment.RecyclerItemClickListener.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {

                        sessionManager.setData(SessionManager.KEY_PAGE, "subhomelisting");
                        sessionManager.setData(SessionManager.KEY_BUISNESS_ID, buisnessLists.get(position).getBuisness_id());
                        ShopDetailFragment detailFragment = new ShopDetailFragment(ctx);
                        setFragment(detailFragment);
                    }

                    @Override
                    public void onLongItemClick(View view, int position) {
                        // do whatever
                    }
                })
        );
    }

    private void setFragment(Fragment fragment) {

        final android.app.FragmentTransaction ft = getFragmentManager().beginTransaction();
        ft.replace(R.id.framelayout, fragment);
        ft.commit();
    }


    private void initxml() {

        search_iv = (ImageView) view.findViewById(R.id.iv_search_search);
        autoCompleteTextView = (CustomAutoCompleteView) view.findViewById(R.id.actv_search_searching);
        recyclerView = (RecyclerView) view.findViewById(R.id.rv_search_recycler_view);
        gridView = (GridView) view.findViewById(R.id.gv_search_gridview);
        listView = (ListView) view.findViewById(R.id.lv_search_listview);
        near_me_bt = (Button) view.findViewById(R.id.bt_search_nearme);
        rating_bt = (Button) view.findViewById(R.id.bt_search_rating);
        popularity_bt = (Button) view.findViewById(R.id.bt_search_popularity);

        autoCompleteTextView.requestFocus();
        InputMethodManager imm = (InputMethodManager) ctx.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);

        near_me_bt.setOnClickListener(this);
        popularity_bt.setOnClickListener(this);
        rating_bt.setOnClickListener(this);
        search_iv.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {

        double latitude = sessionManager.getDouble(SessionManager.KEY_LAT);
        double longitude = sessionManager.getDouble(SessionManager.KEY_LONG);

        switch (v.getId()) {

            case R.id.bt_search_nearme:
                try {
                    if (search_list.size() != 0) {
                        makeList();
                        Collections.sort(searchList, new distanceComp());

                        final Adp_ShopSearchListing adp_shopListing = new Adp_ShopSearchListing(searchList, ctx, latitude, longitude);
                        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(ctx.getApplicationContext());
                        recyclerView.setLayoutManager(mLayoutManager);
                        recyclerView.setAdapter(adp_shopListing);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

                break;

            case R.id.bt_search_rating:

                try {
                    if (search_list.size() != 0) {

                        makeList();
                        Collections.sort(searchList, new ratingComp());

                        final Adp_ShopSearchListing adp_shopListing1 = new Adp_ShopSearchListing(searchList, ctx, latitude, longitude);
                        RecyclerView.LayoutManager mLayoutManager1 = new LinearLayoutManager(ctx.getApplicationContext());
                        recyclerView.setLayoutManager(mLayoutManager1);
                        recyclerView.setAdapter(adp_shopListing1);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;

            case R.id.bt_search_popularity:

                try {
                    if (search_list.size() != 0) {

                        makeList();
                        Collections.sort(searchList, new ratingComp());

                        final Adp_ShopSearchListing adp_shopListing2 = new Adp_ShopSearchListing(searchList, ctx, latitude, longitude);
                        RecyclerView.LayoutManager mLayoutManager2 = new LinearLayoutManager(ctx.getApplicationContext());
                        recyclerView.setLayoutManager(mLayoutManager2);
                        recyclerView.setAdapter(adp_shopListing2);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;

            case R.id.iv_search_search:

                String text = autoCompleteTextView.getText().toString();
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
        }
    }

    public void makeList() {

        double latitude = sessionManager.getDouble(SessionManager.KEY_LAT);
        double longitude = sessionManager.getDouble(SessionManager.KEY_LONG);

        searchList = new ArrayList<>();

        for (int i = 0; i < buisnessLists.size(); i++) {

            double lat = Double.parseDouble(buisnessLists.get(i).getLat());
            double longi = Double.parseDouble(buisnessLists.get(i).getSet_long());

            LatLng from = new LatLng(latitude, longitude);
            LatLng to = new LatLng(lat, longi);

            Double distance = SphericalUtil.computeDistanceBetween(from, to);

            double distance_km = distance / 1000;
            String rating = buisnessLists.get(i).getRating();
            String rate = rating.substring(0, 1);

            int final_rating = Integer.parseInt(rate);

            searchList.add(new BuisnessListSearch(buisnessLists.get(i).getBuisness_id(),
                    buisnessLists.get(i).getStatus(),
                    buisnessLists.get(i).getBuisness_name(),
                    buisnessLists.get(i).getBuilding(),
                    buisnessLists.get(i).getStreet(),
                    buisnessLists.get(i).getEstb_year(),
                    buisnessLists.get(i).getArea(),
                    buisnessLists.get(i).getCity(),
                    buisnessLists.get(i).getPincode(),
                    buisnessLists.get(i).getState(),
                    buisnessLists.get(i).getContact_person_title(),
                    buisnessLists.get(i).getContact_person_name(),
                    buisnessLists.get(i).getDesignation(),
                    buisnessLists.get(i).getLandline_no1(),
                    buisnessLists.get(i).getLandline_no2(),
                    buisnessLists.get(i).getMobile_no1(),
                    buisnessLists.get(i).getMobile_no2(),
                    buisnessLists.get(i).getEmail_id(),
                    buisnessLists.get(i).getWebsite(),
                    lat, longi,
                    buisnessLists.get(i).getImage(), final_rating, distance_km, buisnessLists.get(i).getCate_icon()));
        }

    }

    private void toastView(String s) {
        Toast.makeText(ctx, s, Toast.LENGTH_LONG).show();
    }


    class distanceComp implements Comparator<BuisnessListSearch> {

        @Override
        public int compare(BuisnessListSearch e1, BuisnessListSearch e2) {
            if (e1.getDistance() < e2.getDistance()) {
                return 1;
            } else {
                return -1;
            }
        }
    }

    class ratingComp implements Comparator<BuisnessListSearch> {

        @Override
        public int compare(BuisnessListSearch e1, BuisnessListSearch e2) {
            if (e1.getRating() < e2.getRating()) {
                return 1;
            } else {
                return -1;
            }
        }
    }
}
