package info.android.technologies.indoreconnect.fragment;

import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ImageView;
import android.widget.ListView;
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

import java.util.ArrayList;

import info.android.technologies.indoreconnect.R;
import info.android.technologies.indoreconnect.adapter.Adp_Search;
import info.android.technologies.indoreconnect.adapter.InfoSubListAdp;
import info.android.technologies.indoreconnect.model.InfoSubCate;
import info.android.technologies.indoreconnect.model.Search;
import info.android.technologies.indoreconnect.util.AutocompleteCustomArrayAdapter;
import info.android.technologies.indoreconnect.util.ConnectionDetector;
import info.android.technologies.indoreconnect.util.CustomAutoCompleteView;
import info.android.technologies.indoreconnect.util.SessionManager;
import info.android.technologies.indoreconnect.util.WebAPI;

/**
 * Created by kamlesh on 12/1/2017.
 */
public class InfoSubCateFragment extends Fragment implements AdapterView.OnItemClickListener, View.OnClickListener {

    Context ctx;
    View view;
    ListView listView;
    ImageView search_iv;

    String main_cate_id;
    CustomAutoCompleteView actv_subinfo;

    ConnectionDetector connectionDetector;
    SessionManager sessionManager;

    ArrayList<InfoSubCate> infosublist;

    public InfoSubCateFragment(Context ctx) {
        this.ctx = ctx;
        connectionDetector = new ConnectionDetector();
        sessionManager = new SessionManager(ctx);
        infosublist = new ArrayList<>();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_infosubcate, null);
        initxml();
        setSearching();
        main_cate_id = sessionManager.getData(SessionManager.KEY_SUB_ID);
        boolean internet = connectionDetector.isConnected();
        if (internet) {
            loadSubSubInfoCategory();
        } else {
            toastView("No internet connection");
        }
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
        actv_subinfo.setThreshold(1);
        actv_subinfo.setAdapter(adapter);
        actv_subinfo.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                String selectd = actv_subinfo.getText().toString();
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

    private void toastView(String s) {
        Toast.makeText(ctx, s, Toast.LENGTH_LONG).show();
    }

    private void initxml() {
        search_iv = (ImageView) view.findViewById(R.id.iv_infosub_search);
        listView = (ListView) view.findViewById(R.id.lv_sub_cate_listview);
        actv_subinfo = (CustomAutoCompleteView) view.findViewById(R.id.actv_infosubcate_searching);

        listView.setOnItemClickListener(this);
        search_iv.setOnClickListener(this);
    }

    private void loadSubSubInfoCategory() {

        final ProgressDialog pd = new ProgressDialog(ctx);
        pd.setMessage("wait..");
        pd.setCancelable(false);
        pd.show();
        StringRequest stringRequest = new StringRequest(Request.Method.GET, WebAPI.URL_INFO_SUB_CATEORY + main_cate_id,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        pd.dismiss();
                        try {
                            JSONArray dataarray = new JSONArray(response);
                            for (int i = 0; i < dataarray.length(); i++) {
                                JSONObject object = dataarray.getJSONObject(i);
                                String category_name = object.getString("sub_category");
                                String category_id = object.getString("category_id");
                                String category_icon = object.getString("category_icon");
                                String parent_icon = object.getString("parent_icon");

                                infosublist.add(new InfoSubCate(category_id, category_name, category_icon, parent_icon));
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
        InfoSubListAdp infoSubListAdp = new InfoSubListAdp(ctx, infosublist);
        listView.setAdapter(infoSubListAdp);
    }

    private void setFragment(Fragment fragment) {
        final android.app.FragmentTransaction ft = getFragmentManager().beginTransaction();
        ft.replace(R.id.framelayout, fragment);
        ft.commit();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

        sessionManager.setData(SessionManager.KEY_PAGE, "infosub");
        sessionManager.setData(SessionManager.KEY_SUB_SUB_ID, infosublist.get(position).getId());
        String icon = infosublist.get(position).getIcon();
        String parent_icon = infosublist.get(position).getParent_icon();
        if (!icon.equals("")) {
            sessionManager.setData(SessionManager.KEY_SUB_SUB_ICON, infosublist.get(position).getIcon());
        } else {
            sessionManager.setData(SessionManager.KEY_SUB_SUB_ICON, parent_icon);
        }
        SubSubInfoCategoryFragment fragment = new SubSubInfoCategoryFragment(ctx);
        setFragment(fragment);
    }

    @Override
    public void onClick(View v) {
        String text = actv_subinfo.getText().toString();
        if (!text.equals("")) {
            sessionManager.setData(SessionManager.KEY_PAGE, "infosub");
            sessionManager.setData(SessionManager.KEY_SEARCH_TEXT, text);
            sessionManager.setData(SessionManager.KEY_ALL_BUISNESS_TYPE, "search");
            AllBuisnessListingFragment allBuisnessListingFragment = new AllBuisnessListingFragment(ctx);
            setFragment(allBuisnessListingFragment);
        } else {
            toastView("please enter text in search");
        }
    }
}
