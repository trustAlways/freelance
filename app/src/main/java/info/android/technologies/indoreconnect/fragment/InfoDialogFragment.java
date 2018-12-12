package info.android.technologies.indoreconnect.fragment;

import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.LinearLayout;
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

import info.android.technologies.indoreconnect.R;
import info.android.technologies.indoreconnect.activity.MapsActivity;
import info.android.technologies.indoreconnect.adapter.OfferImgAdapter;
import info.android.technologies.indoreconnect.model.Offer;
import info.android.technologies.indoreconnect.util.ConnectionDetector;
import info.android.technologies.indoreconnect.util.SessionManager;
import info.android.technologies.indoreconnect.util.WebAPI;

/**
 * Created by kamlesh on 12/7/2017.
 */
public class InfoDialogFragment extends Fragment {

    View dialog;
    Context ctx;
    String search_id;

    ConnectionDetector connectionDetector;
    SessionManager sessionManager;

    public InfoDialogFragment(Context ctx, String search_id) {
        this.ctx = ctx;
        this.search_id = search_id;
        connectionDetector = new ConnectionDetector();
        sessionManager = new SessionManager(ctx);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        dialog = inflater.inflate(R.layout.dialog_details, null);
        boolean internet = connectionDetector.isConnected();
        if (internet) {
            loadData();
        }
        return dialog;
    }

    private void loadData() {

        final ProgressDialog pd = new ProgressDialog(ctx);
        pd.setMessage("wait..");
        pd.setCancelable(false);
        pd.show();
        StringRequest stringRequest = new StringRequest(Request.Method.GET, WebAPI.URL_INFO_BUSINESS_DETAILS + search_id,
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
                            String lat = object.getString("lat");
                            String longi = object.getString("set_long");

                            setView(name, icon, type, contact, address, discription, lat, longi);
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

    public void setView(final String name, String icon, String type, final String contact, String address, String discription, String lat, String longi) {

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

        if (discription.equals("")) {
            webView.setVisibility(View.GONE);
        } else if (discription.length() < 5) {
            webView.setVisibility(View.GONE);
        } else {
            webView.getSettings().setJavaScriptEnabled(true);
            webView.loadDataWithBaseURL("", discription, "text/html", "UTF-8", "");
        }

        if (contact.equals("0")) {
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

        String lat_1 = lat;
        String long_1 = longi;

        double lat_buiss = 0;
        double long_buisn = 0;
        try {
            lat_buiss = Double.parseDouble(lat);
            long_buisn = Double.parseDouble(long_1);
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

                try {
                    Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + contact));
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

        if (!lat_1.equals("") || !long_1.equals("")) {

            double buis_lat = Double.parseDouble(lat);
            double buis_long = Double.parseDouble(longi);

            LatLng from = new LatLng(latitude, longitude);
            LatLng to = new LatLng(buis_lat, buis_long);

            Double distance = SphericalUtil.computeDistanceBetween(from, to);

            double distance_km = distance / 1000;

            distsnce_tv.setText(distance + " KM");
        }

        String url = icon;
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
    }

    private void toastView(String s) {
        Toast.makeText(ctx, s, Toast.LENGTH_LONG).show();
    }

    private void goDirection(double latitude, double longitude, double finalLat_buiss, double finalLong_buisn) {


        startActivity(new Intent(ctx, MapsActivity.class)
                .putExtra("fromlat", latitude)
                .putExtra("fromlong", longitude)
                .putExtra("tolat", finalLat_buiss)
                .putExtra("tolong", finalLong_buisn)
        );
    }
}
