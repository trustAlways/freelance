package info.android.technologies.indoreconnect.fragment;

import android.annotation.SuppressLint;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.Toast;

import info.android.technologies.indoreconnect.R;
import info.android.technologies.indoreconnect.adapter.Adp_ShopListing;
import info.android.technologies.indoreconnect.model.BuisnessList;
import info.android.technologies.indoreconnect.util.ConnectionDetector;
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

import java.text.DecimalFormat;
import java.util.ArrayList;

/**
 * Created by kamlesh on 12/1/2017.
 */
public class SearchFragment extends Fragment implements AdapterView.OnItemClickListener, View.OnClickListener {
    Context ctx;
    View view;
    RecyclerView recyclerView;
    Button near_me_bt;

    ConnectionDetector connectionDetector;
    SessionManager sessionManager;

    ArrayList<String> serach_name_list;
    ArrayList<String> serach_id_list;
    ArrayList<BuisnessList> buisnessLists_list;
    AutoCompleteTextView autoCompleteTextView;

    double latitude, longitude;
    String id, type;

    @SuppressLint("ValidFragment")
    public SearchFragment(Context ctx) {
        this.ctx = ctx;
        connectionDetector = new ConnectionDetector();
        sessionManager = new SessionManager(ctx);
        serach_name_list = new ArrayList<>();
        serach_id_list = new ArrayList<>();
        buisnessLists_list = new ArrayList<>();

    }

    public SearchFragment(Context ctx, String id, String type) {
        this.ctx = ctx;
        this.type = type;
        this.id = id;
        connectionDetector = new ConnectionDetector();
        sessionManager = new SessionManager(ctx);
        serach_name_list = new ArrayList<>();
        serach_id_list = new ArrayList<>();
        buisnessLists_list = new ArrayList<>();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_search, null);
        initxml();

        latitude = sessionManager.getDouble(SessionManager.KEY_LAT);
        longitude = sessionManager.getDouble(SessionManager.KEY_LONG);

        Adp_ShopListing adp_shopListing = new Adp_ShopListing(buisnessLists_list, ctx, latitude, longitude);

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(ctx.getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(adp_shopListing);

        recyclerView.addOnItemTouchListener(
                new RecyclerItemClickListener(ctx, recyclerView, new RecyclerItemClickListener.OnItemClickListener() {
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

        return view;
    }

    private void toastView(String s) {
        Toast.makeText(ctx, s, Toast.LENGTH_LONG).show();
    }

    private void initxml() {

        autoCompleteTextView = (AutoCompleteTextView) view.findViewById(R.id.actv_search_searching);
        recyclerView = (RecyclerView) view.findViewById(R.id.rv_search_recycler_view);
        near_me_bt = (Button) view.findViewById(R.id.bt_search_nearme);

        autoCompleteTextView.requestFocus();
        InputMethodManager imm = (InputMethodManager) ctx.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);

        near_me_bt.setOnClickListener(this);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        /*ShopDetailFragment detailFragment = new ShopDetailFragment(ctx);
        setFragment(detailFragment);*/
    }

    private void setFragment(Fragment fragment) {

        final android.app.FragmentTransaction ft = getFragmentManager().beginTransaction();
        ft.replace(R.id.framelayout, fragment);
        ft.commit();
    }

    private void loadBuisnessList(String category_id) {

        final ProgressDialog pd = new ProgressDialog(ctx);
        pd.setMessage("wait..");
        pd.setCancelable(false);
        pd.show();
        StringRequest stringRequest = new StringRequest(Request.Method.GET, WebAPI.URL_BUSINESS_LIST + category_id,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        pd.dismiss();
                        buisnessLists_list = new ArrayList<>();
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

                                double dis = distance(latitude, longitude, Double.parseDouble(lat), Double.parseDouble(set_long));
                                DecimalFormat df2 = new DecimalFormat(".##");
                                float final_distance = Float.parseFloat(df2.format(dis));

                                buisnessLists_list.add(new BuisnessList(buisness_id, status, buisness_name, building, street, estb_year, area, city, pincode
                                        , state, contact_person_title, contact_person_name, designation, landline_no1, landline_no2, mobile_no1, mobile_no2
                                        , email_id, website, lat, set_long, image, rating, final_distance, cate_icon));
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

        switch (v.getId()) {

            case R.id.bt_search_nearme:

/*                Adp_ShopListing adp_shopListing = new Adp_ShopListing(buisnessLists_list, ctx, latitude, longitude);
                RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(ctx.getApplicationContext());
                recyclerView.setLayoutManager(mLayoutManager);
                recyclerView.setItemAnimator(new DefaultItemAnimator());
                recyclerView.setAdapter(adp_shopListing);*/
                break;
        }
    }

    public static class RecyclerItemClickListener implements RecyclerView.OnItemTouchListener {
        private OnItemClickListener mListener;

        public interface OnItemClickListener {
            public void onItemClick(View view, int position);

            public void onLongItemClick(View view, int position);
        }

        GestureDetector mGestureDetector;

        public RecyclerItemClickListener(Context context, final RecyclerView recyclerView, OnItemClickListener listener) {
            mListener = listener;
            mGestureDetector = new GestureDetector(context, new GestureDetector.SimpleOnGestureListener() {
                @Override
                public boolean onSingleTapUp(MotionEvent e) {
                    return true;
                }

                @Override
                public void onLongPress(MotionEvent e) {
                    View child = recyclerView.findChildViewUnder(e.getX(), e.getY());
                    if (child != null && mListener != null) {
                        mListener.onLongItemClick(child, recyclerView.getChildAdapterPosition(child));
                    }
                }
            });
        }

        @Override
        public boolean onInterceptTouchEvent(RecyclerView view, MotionEvent e) {
            View childView = view.findChildViewUnder(e.getX(), e.getY());
            if (childView != null && mListener != null && mGestureDetector.onTouchEvent(e)) {
                mListener.onItemClick(childView, view.getChildAdapterPosition(childView));
                return true;
            }
            return false;
        }

        @Override
        public void onTouchEvent(RecyclerView view, MotionEvent motionEvent) {
        }

        @Override
        public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {
        }
    }

    private void setAdapter() {

        ArrayAdapter adapter = new ArrayAdapter<String>(ctx, android.R.layout.simple_dropdown_item_1line, serach_name_list);

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
                sessionManager.setData(SessionManager.KEY_CATEGORY_NAME, cate);
                loadBuisnessList(id1);
            }
        });
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
