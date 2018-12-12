package info.android.technologies.indoreconnect.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import info.android.technologies.indoreconnect.R;
import info.android.technologies.indoreconnect.model.Favourites;
import info.android.technologies.indoreconnect.util.WebAPI;

import java.util.ArrayList;

/**
 * Created by kamlesh on 12/15/2017.
 */
public class Adp_Fav_Listing extends BaseAdapter {

    View view;
    Context ctx;
    ArrayList<Favourites> fav_list;

    double latitude, longitude;

    public Adp_Fav_Listing(Context ctx, ArrayList<Favourites> fav_list, double latitude, double longitude) {
        this.ctx = ctx;
        this.fav_list = fav_list;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    @Override
    public int getCount() {
        return fav_list.size();
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
        view = inflater.inflate(R.layout.adp_shoplistview, null);

        TextView title_tv = (TextView) view.findViewById(info.android.technologies.indoreconnect.R.id.tv_adpshop_title);
        TextView address_tv = (TextView) view.findViewById(info.android.technologies.indoreconnect.R.id.tv_adpshop_address);
        TextView contact_tv = (TextView) view.findViewById(info.android.technologies.indoreconnect.R.id.tv_adpshop_contact);
        TextView distance_tv = (TextView) view.findViewById(info.android.technologies.indoreconnect.R.id.tv_adpshop_distance);
        TextView more_tv = (TextView) view.findViewById(info.android.technologies.indoreconnect.R.id.tv_adpshop_more);
        RatingBar ratingBar = (RatingBar) view.findViewById(info.android.technologies.indoreconnect.R.id.rb_adpshop_rating);
        ImageView imageView = (ImageView) view.findViewById(info.android.technologies.indoreconnect.R.id.iv_adpshop_image);

        title_tv.setText(fav_list.get(position).getTitle());
        address_tv.setText(fav_list.get(position).getAddress());
        contact_tv.setText(fav_list.get(position).getContact());

        double buiss_lat = Double.parseDouble(fav_list.get(position).getLati());
        double buiss_long = Double.parseDouble(fav_list.get(position).getLongi());

        double distance = distance(buiss_lat, buiss_long, latitude, longitude);
        distance_tv.setText((int) distance + " KM");

        String url = fav_list.get(position).getImage();
        if (!url.equals("")) {
            String icon_url = WebAPI.BASE_URL + "admin/img/business_images/logo/" + url;
            icon_url = icon_url.replace(" ", "%20");
            Picasso.with(ctx)
                    .load(icon_url)
                    .resize(100, 100)
                    .placeholder(info.android.technologies.indoreconnect.R.drawable.subcate_image)
                    .error(info.android.technologies.indoreconnect.R.drawable.subcate_image)
                    .into(imageView);
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
