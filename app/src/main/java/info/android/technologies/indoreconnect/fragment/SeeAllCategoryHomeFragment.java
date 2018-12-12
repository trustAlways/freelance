package info.android.technologies.indoreconnect.fragment;

import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import info.android.technologies.indoreconnect.R;
import info.android.technologies.indoreconnect.adapter.Adp_Search;
import info.android.technologies.indoreconnect.adapter.Adp_ShopListing;
import info.android.technologies.indoreconnect.adapter.SeeAllAdapter;
import info.android.technologies.indoreconnect.model.BuisnessList;
import info.android.technologies.indoreconnect.model.HomeCate;
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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by kamlesh on 12/1/2017.
 */
public class SeeAllCategoryHomeFragment extends Fragment implements AdapterView.OnItemClickListener, View.OnClickListener {

    Context ctx;
    View view;
    ImageView search_iv;
    GridView gridView;
    RecyclerView recyclerView;
    TextView maincate_tv;
    CustomAutoCompleteView autoCompleteTextView;

    ArrayList<HomeCate> subcate_list;
    ArrayList<String> serach_name_list, serach_id_list;
    ArrayList<BuisnessList> buisnessLists_list;
    String maincate;

    SessionManager sessionManager;
    ConnectionDetector connectionDetector;

    public SeeAllCategoryHomeFragment(Context ctx) {
        this.ctx = ctx;
        subcate_list = new ArrayList<>();
        buisnessLists_list = new ArrayList<>();
        serach_id_list = new ArrayList<>();
        serach_name_list = new ArrayList<>();
        sessionManager = new SessionManager(ctx);
        connectionDetector = new ConnectionDetector();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_seeallcategory, null);
        initxml();
        boolean internet = connectionDetector.isConnected();
        if (internet) {
            loadCategory();
        } else {
            toastView("no internet conncetion");
        }
        return view;
    }

    private void loadCategory() {

        final ProgressDialog pd = new ProgressDialog(ctx);
        pd.setMessage("wait..");
        pd.setCancelable(false);
        pd.show();
        StringRequest stringRequest = new StringRequest(Request.Method.GET, WebAPI.URL_CATEGORY,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        pd.dismiss();
                        try {
                            JSONArray dataarray = new JSONArray(response);
                            for (int i = 0; i < dataarray.length(); i++) {
                                JSONObject imgobj = dataarray.getJSONObject(i);
                                String parent_cate = imgobj.getString("parent_category");
                                String sub_cate = imgobj.getString("sub_category");
                                String category_icon = imgobj.getString("category_icon");
                                String category_id = imgobj.getString("category_id");

                                if (!parent_cate.equals("category_info")) {
                                    serach_name_list.add(sub_cate);
                                    serach_id_list.add(category_id);

                                    if (parent_cate.equals(maincate)) {
                                        subcate_list.add(new HomeCate(parent_cate, category_id, sub_cate, "", category_icon));
                                    }
                                }
                            }
                            setCategory();
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

//        ArrayAdapter adapter = new ArrayAdapter<String>(ctx, android.R.layout.simple_dropdown_item_1line, serach_name_list);
//        Adp_Search adapter = new Adp_Search(ctx, R.layout.adp_search, serach_name_list);
        AutocompleteCustomArrayAdapter adapter = new AutocompleteCustomArrayAdapter(ctx, R.layout.custom_actv, serach_name_list);
        autoCompleteTextView.setThreshold(1);
        autoCompleteTextView.setAdapter(adapter);
        autoCompleteTextView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                String selectd = autoCompleteTextView.getText().toString();
                String id1 = "";
                String cate = "";

                for (int i = 0; i < serach_name_list.size(); i++) {

                    if (selectd.equals(serach_name_list.get(i))) {
                        id1 = serach_id_list.get(i);
                        cate = serach_name_list.get(i);
                    }
                }
                boolean internet = connectionDetector.isConnected();
                if (internet) {
                    sessionManager.setData(SessionManager.KEY_CATEGORY_NAME, cate);
                    loadBuisnessList(id1);
                } else {
                    toastView("no internet connection");
                }
            }
        });
    }

    private void initxml() {
        search_iv = (ImageView) view.findViewById(R.id.iv_seeall_search);
        autoCompleteTextView = (CustomAutoCompleteView) view.findViewById(R.id.actv_seeall_searching);
        recyclerView = (RecyclerView) view.findViewById(R.id.rv_seeall_recycler_view);
        gridView = (GridView) view.findViewById(R.id.gv_seeall_gridview);
        maincate_tv = (TextView) view.findViewById(R.id.tv_seeallcate_maincate);

        maincate = sessionManager.getData(SessionManager.KEY_MAIN_CATEGORY_NAME);

        search_iv.setOnClickListener(this);
    }

    private void setCategory() {

        maincate_tv.setText(maincate);
        SeeAllAdapter adapter = new SeeAllAdapter(ctx, subcate_list, maincate);
        gridView.setAdapter(adapter);
        gridView.setOnItemClickListener(this);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

        boolean internet = connectionDetector.isConnected();
        if (internet) {
//            sessionManager.setData(SessionManager.KEY_CATEGORY_NAME, subcate_list.get(position).getSubcate_name());
//            sessionManager.setData(SessionManager.KEY_PAGE, "see_all_listing");
//            loadBuisnessList(subcate_list.get(position).getCategory_id());

            sessionManager.setData(SessionManager.KEY_PAGE, "allbuisness2");
            sessionManager.setData(SessionManager.KEY_ALL_BUISNESS_TYPE, "normal");
            sessionManager.setData(SessionManager.KEY_MAIN_BUISNESS_ID, subcate_list.get(position).getCategory_id());
            AllBuisnessListingFragment allBuisnessListingFragment = new AllBuisnessListingFragment(ctx);
            setFragment(allBuisnessListingFragment);
        } else {
            toastView("No internet connection");
        }
    }

    private void toastView(String s) {
        Toast.makeText(ctx, s, Toast.LENGTH_LONG).show();
    }

    private void loadBuisnessList(String category_id) {

        buisnessLists_list = new ArrayList<>();
        final ProgressDialog pd = new ProgressDialog(ctx);
        pd.setMessage("wait..");
        pd.setCancelable(false);
        pd.show();
        StringRequest stringRequest = new StringRequest(Request.Method.GET, WebAPI.URL_BUSINESS_LIST + category_id,
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

                                float distance = (float) 0.0;
                                String cate_icon = buisnness_obj.getString("category_icon");

                                buisnessLists_list.add(new BuisnessList(buisness_id, status, buisness_name, building, street, estb_year, area, city, pincode
                                        , state, contact_person_title, contact_person_name, designation, landline_no1, landline_no2, mobile_no1, mobile_no2
                                        , email_id, website, lat, set_long, image, rating, distance, cate_icon));
                            }
                            setBuisness();
                        } catch (JSONException e) {
                            e.printStackTrace();
                            toastView("No Data Available");
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

    private void setFragment(Fragment fragment) {

        final android.app.FragmentTransaction ft = getFragmentManager().beginTransaction();
        ft.replace(R.id.framelayout, fragment);
        ft.commit();
    }

    private void setBuisness() {

        double latitude = sessionManager.getDouble(SessionManager.KEY_LAT);
        double longitude = sessionManager.getDouble(SessionManager.KEY_LONG);

        gridView.setVisibility(View.GONE);
        recyclerView.setVisibility(View.VISIBLE);

        Adp_ShopListing adp_shopListing = new Adp_ShopListing(buisnessLists_list, ctx, latitude, longitude);

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(ctx.getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(adp_shopListing);

        recyclerView.addOnItemTouchListener(
                new SearchFragment.RecyclerItemClickListener(ctx, recyclerView, new SearchFragment.RecyclerItemClickListener.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {

                        sessionManager.setData(SessionManager.KEY_BUISNESS_ID, buisnessLists_list.get(position).getBuisness_id());
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

    @Override
    public void onClick(View v) {

        String text = autoCompleteTextView.getText().toString();
        if (!text.equals("")) {
            sessionManager.setData(SessionManager.KEY_PAGE, "see_all_listing");
            sessionManager.setData(SessionManager.KEY_SEARCH_TEXT, text);
            sessionManager.setData(SessionManager.KEY_ALL_BUISNESS_TYPE, "search");
            AllBuisnessListingFragment allBuisnessListingFragment = new AllBuisnessListingFragment(ctx);
            setFragment(allBuisnessListingFragment);
        } else {
            toastView("please enter text in search");
        }
    }
}
