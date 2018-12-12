package info.android.technologies.indoreconnect.fragment;

import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.Toast;

import info.android.technologies.indoreconnect.R;
import info.android.technologies.indoreconnect.adapter.Adp_Search;
import info.android.technologies.indoreconnect.adapter.CateGVAdp;
import info.android.technologies.indoreconnect.adapter.ImagePagerAdapter;
import info.android.technologies.indoreconnect.model.InfoCate;
import info.android.technologies.indoreconnect.model.Search;
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
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by kamlesh on 12/1/2017.
 */
public class InfoFragment extends Fragment implements AdapterView.OnItemClickListener, View.OnClickListener {

    Context ctx;
    View view;
    GridView gridView;
    ImageView search_iv;
    ArrayList<InfoCate> cate_list;
    ArrayList<String> imaglist;
    ViewPager viewPager;
    ImagePagerAdapter pagerAdapter;
    CustomAutoCompleteView actv_info;

    ConnectionDetector connectionDetector;
    SessionManager sessionManager;

    public InfoFragment(Context ctx) {
        this.ctx = ctx;
        connectionDetector = new ConnectionDetector();
        sessionManager = new SessionManager(ctx);
        imaglist = new ArrayList<>();
        cate_list = new ArrayList<>();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_info, null);
        initxml();
        setSearching();
        boolean internet = connectionDetector.isConnected();
        if (internet) {
            loadImages();
            loadInfoCategory();
        } else {
            toastView("No internet connection");
        }
        return view;
    }

    private void toastView(String s) {

        Toast.makeText(ctx, s, Toast.LENGTH_LONG).show();
    }

    private void setAdapter() {

        CateGVAdp cateGVAdp = new CateGVAdp(ctx, cate_list);
        gridView.setAdapter(cateGVAdp);
        gridView.setOnItemClickListener(this);
    }

    private void initxml() {
        viewPager = (ViewPager) view.findViewById(R.id.vp_info_viewpager);
        gridView = (GridView) view.findViewById(R.id.gv_info_categridview);
        actv_info = (CustomAutoCompleteView) view.findViewById(R.id.actv_info_searching);
        search_iv = (ImageView) view.findViewById(R.id.iv_info_search);
        search_iv.setOnClickListener(this);

        TimerTask timerTask = new TimerTask() {
            @Override
            public void run() {
                viewPager.post(new Runnable(){

                    @Override
                    public void run() {
                        viewPager.setCurrentItem((viewPager.getCurrentItem()+1)%imaglist.size());
                    }
                });
            }

        };
        Timer timer = new Timer();
        timer.schedule(timerTask, 3000, 3000);


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

    private void loadInfoCategory() {

        final ProgressDialog pd = new ProgressDialog(ctx);
        pd.setMessage("wait..");
        pd.setCancelable(false);
        pd.show();
        StringRequest stringRequest = new StringRequest(Request.Method.GET, WebAPI.URL_INFO_CATEGORY,
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

                                cate_list.add(new InfoCate(parent_cate, sub_cate, category_icon, category_id));
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

    private void loadImages() {

        final ProgressDialog pd = new ProgressDialog(ctx);
        pd.setMessage("wait..");
        pd.setCancelable(false);
        pd.show();
        StringRequest stringRequest = new StringRequest(Request.Method.GET, WebAPI.URL_BANNER,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        pd.dismiss();
                        try {
                            JSONArray imageArray = new JSONArray(response);
                            for (int i = 0; i < imageArray.length(); i++) {
                                JSONObject imgobj = imageArray.getJSONObject(i);
                                String url = imgobj.getString("image");
                                imaglist.add(url);
                            }
                            pagerAdapter = new ImagePagerAdapter(ctx, imaglist);
                            viewPager.setAdapter(pagerAdapter);

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

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

        SessionManager sessionManager = new SessionManager(ctx);
        sessionManager.setData(SessionManager.KEY_PAGE, "info");
        sessionManager.setData(SessionManager.KEY_SUB_ID, cate_list.get(position).getCate_id());
        InfoSubCateFragment infoSubCateFragment = new InfoSubCateFragment(ctx);
        setFragment(infoSubCateFragment);
    }

    private void setFragment(Fragment fragment) {

        final android.app.FragmentTransaction ft = getFragmentManager().beginTransaction();
        ft.replace(R.id.framelayout, fragment);
        ft.commit();
    }

    @Override
    public void onClick(View v) {

        String text = actv_info.getText().toString();
        if (!text.equals("")) {
            sessionManager.setData(SessionManager.KEY_PAGE, "info");
            sessionManager.setData(SessionManager.KEY_SEARCH_TEXT, text);
            sessionManager.setData(SessionManager.KEY_ALL_BUISNESS_TYPE, "search");
            AllBuisnessListingFragment allBuisnessListingFragment = new AllBuisnessListingFragment(ctx);
            setFragment(allBuisnessListingFragment);
        } else {
            toastView("please enter text in search");
        }
    }
}
