package info.android.technologies.indoreconnect.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import info.android.technologies.indoreconnect.R;
import info.android.technologies.indoreconnect.model.SubSubInfoCategory;
import info.android.technologies.indoreconnect.util.SessionManager;
import info.android.technologies.indoreconnect.util.WebAPI;

import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.SphericalUtil;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by kamlesh on 12/7/2017.
 */
public class SubInfoCateAdapter extends BaseAdapter {
    Context ctx;
    ArrayList<SubSubInfoCategory> subinfocate_list;
    SessionManager sessionManager;

    String parent_icon;

    public SubInfoCateAdapter(Context ctx, ArrayList<SubSubInfoCategory> subinfocate_list) {
        this.ctx = ctx;
        this.subinfocate_list = subinfocate_list;
        sessionManager = new SessionManager(ctx);
    }

    @Override
    public int getCount() {
        return subinfocate_list.size();
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        LayoutInflater inflater = (LayoutInflater) ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.adp_contactsubinfo, null);

        ImageView imageView = (ImageView) view.findViewById(R.id.iv_adp_subcontact_image);
        ImageView calling_iv = (ImageView) view.findViewById(R.id.iv_adp_subcontact_calling);
        TextView name_tv = (TextView) view.findViewById(R.id.tv_adp_subcontact_title);
        TextView distace_tv = (TextView) view.findViewById(R.id.tv_adp_subcontact_distance);

        name_tv.setText(subinfocate_list.get(position).getTitle() + "");

        double lat = subinfocate_list.get(position).getLatitude();
        double longi = subinfocate_list.get(position).getLongitude();

        try {
            double latitude = sessionManager.getDouble(SessionManager.KEY_LAT);
            double longitude = sessionManager.getDouble(SessionManager.KEY_LONG);

            double buiss_lat = subinfocate_list.get(position).getLatitude();
            double buiss_long = subinfocate_list.get(position).getLongitude();

            LatLng from = new LatLng(latitude, longitude);
            LatLng to = new LatLng(buiss_lat, buiss_long);

            Double distance = SphericalUtil.computeDistanceBetween(from, to);
            double distance_km = distance / 1000;

            String distn = String.valueOf(distance_km);
            String distan = distn.substring(0, 4);

            distace_tv.setText(String.valueOf(distan) + " KM");
        } catch (Exception e) {
            e.printStackTrace();
        }

        String url = subinfocate_list.get(position).getIcon();
        parent_icon = subinfocate_list.get(position).getCategory_icon();

        if (!url.equals("")) {
            String icon_url = WebAPI.BASE_URL + "admin/img/category_icon/" + url;
            icon_url = icon_url.replace(" ", "%20");
            Picasso.with(ctx)
                    .load(icon_url)
                    .placeholder(R.drawable.blue_ic_icon)
                    .error(R.drawable.blue_ic_icon)
                    .into(imageView);
        } else if (!parent_icon.equals("")) {

            String icon_url2 = WebAPI.BASE_URL + "admin/img/category_icon/" + parent_icon;
            icon_url2 = icon_url2.replace(" ", "%20");
            Picasso.with(ctx)
                    .load(icon_url2)
                    .placeholder(R.drawable.blue_ic_icon)
                    .error(R.drawable.blue_ic_icon)
                    .into(imageView);
        }
        String type = subinfocate_list.get(0).getType();
        if (!type.equals("contact")) {
            calling_iv.setVisibility(View.GONE);
        }
        if (type.equals("contact")) {
            distace_tv.setVisibility(View.GONE);
        }
        return view;
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
