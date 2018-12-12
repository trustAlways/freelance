package info.android.technologies.indoreconnect.fragment;

import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import info.android.technologies.indoreconnect.R;
import info.android.technologies.indoreconnect.activity.WebViewActivity;
import info.android.technologies.indoreconnect.adapter.OfferImgAdapter;
import info.android.technologies.indoreconnect.model.Offer;
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

import java.util.ArrayList;

/**
 * Created by kamlesh on 12/1/2017.
 */
public class OfferFragment extends Fragment implements AdapterView.OnItemSelectedListener, AdapterView.OnItemClickListener {
    Context ctx;
    View view;
    ListView listView;
    TextView comingsoon_tv;
    Spinner offer_sp;
    SessionManager sessionManager;

    ArrayList<Offer> offer_list, shorted_list;
    ArrayList<String> cate_list;
    ConnectionDetector connectionDetector;
    OfferImgAdapter offerImgAdapter;

    public OfferFragment(Context ctx) {
        this.ctx = ctx;
        connectionDetector = new ConnectionDetector();
        sessionManager = new SessionManager(ctx);
        offer_list = new ArrayList<>();
        shorted_list = new ArrayList<>();
        cate_list = new ArrayList<>();
        cate_list.add("All");
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_offer, null);
        initxml();
        offer_sp.setOnItemSelectedListener(this);
        boolean internet = connectionDetector.isConnected();
        if (internet) {
            loadImages();
        } else {
            toastView("No internet Connection");
        }
        listView.setOnItemClickListener(this);
        return view;
    }

    private void toastView(String s) {
        Toast.makeText(ctx, s, Toast.LENGTH_LONG).show();
    }

    private void initxml() {
        listView = (ListView) view.findViewById(R.id.lv_offer_imageoffer);
        offer_sp = (Spinner) view.findViewById(R.id.sp_offer_category);
        comingsoon_tv = (TextView) view.findViewById(R.id.tv_offer_comingsoon);
    }

    private void loadImages() {

        final ProgressDialog pd = new ProgressDialog(ctx);
        pd.setMessage("wait..");
        pd.setCancelable(false);
        pd.show();
        StringRequest stringRequest = new StringRequest(Request.Method.GET, WebAPI.URL_OFFER,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        pd.dismiss();
                        try {
                            JSONArray imageArray = new JSONArray(response);
                            for (int i = 0; i < imageArray.length(); i++) {
                                JSONObject imgobj = imageArray.getJSONObject(i);
                                String category = imgobj.getString("category_name");
                                String image = imgobj.getString("image");
                                String link = imgobj.getString("link");

                                offer_list.add(new Offer(category, image, link));
                                if (!cate_list.contains(category)) {
                                    cate_list.add(category);
                                }
                            }
                            if (offer_list.size() == 0) {
                                comingsoon_tv.setVisibility(View.VISIBLE);
                                listView.setVisibility(View.GONE);
                            } else {
                                offerImgAdapter = new OfferImgAdapter(ctx, offer_list);
                                listView.setAdapter(offerImgAdapter);

                                ArrayAdapter<String> adapter = new ArrayAdapter<String>(ctx, android.R.layout.simple_spinner_item, cate_list);
                                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                                offer_sp.setAdapter(adapter);
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
                });
        RequestQueue requestQueue = Volley.newRequestQueue(ctx);
        requestQueue.add(stringRequest);
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

        String selction = String.valueOf(offer_sp.getSelectedItem());

        if (selction.equals("All")) {
            offerImgAdapter = new OfferImgAdapter(ctx, offer_list);
            listView.setAdapter(offerImgAdapter);

        } else {
            shorted_list = new ArrayList<>();
            for (int i = 0; i < offer_list.size(); i++) {
                if (selction.equals(offer_list.get(i).getCategory())) {
                    shorted_list.add(offer_list.get(i));
                }
            }
            offerImgAdapter = new OfferImgAdapter(ctx, shorted_list);
            listView.setAdapter(offerImgAdapter);
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

        offerImgAdapter = new OfferImgAdapter(ctx, offer_list);
        listView.setAdapter(offerImgAdapter);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

        String selction = String.valueOf(offer_sp.getSelectedItem());

        if (selction.equals("All")) {
            String url = offer_list.get(position).getLink();
            if (url.contains("http")) {
                startActivity(new Intent(ctx, WebViewActivity.class).putExtra("link", url));
            } else {
                sessionManager.setData(SessionManager.KEY_BUISNESS_ID, offer_list.get(position).getLink());
                ShopDetailFragment shopDetailFragment = new ShopDetailFragment(ctx);
                setFragment(shopDetailFragment);
            }
        } else {
            String url = shorted_list.get(position).getLink();
            if (url.contains("http")) {
                startActivity(new Intent(ctx, WebViewActivity.class).putExtra("link", url));
            } else {
                toastView("buisness");
            }
        }
    }

    private void setFragment(Fragment fragment) {

        final android.app.FragmentTransaction ft = getFragmentManager().beginTransaction();
        ft.replace(R.id.framelayout, fragment);
        ft.commit();
    }
}
