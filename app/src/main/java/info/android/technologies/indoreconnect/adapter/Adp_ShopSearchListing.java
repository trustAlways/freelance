package info.android.technologies.indoreconnect.adapter;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.SphericalUtil;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import info.android.technologies.indoreconnect.R;
import info.android.technologies.indoreconnect.model.BuisnessList;
import info.android.technologies.indoreconnect.model.BuisnessListSearch;
import info.android.technologies.indoreconnect.util.SessionManager;
import info.android.technologies.indoreconnect.util.WebAPI;

/**
 * Created by kamlesh on 12/15/2017.
 */
public class Adp_ShopSearchListing extends RecyclerView.Adapter<Adp_ShopSearchListing.MyViewHolder> {

    private List<BuisnessListSearch> buisnessLists;
    Context ctx;
    ArrayList<String> imaglist;

    double latitude, longitude;

    SessionManager sessionManager;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView title_tv, address_tv, contact_tv, distance_tv, more_tv;
        RatingBar ratingBar;
        ImageView imageView;

        public MyViewHolder(View view, int i, Context ctx) {
            super(view);

            sessionManager = new SessionManager(ctx);
            if (i == 1) {
                title_tv = (TextView) view.findViewById(R.id.tv_adpshop_title);
                address_tv = (TextView) view.findViewById(R.id.tv_adpshop_address);
                contact_tv = (TextView) view.findViewById(R.id.tv_adpshop_contact);
                distance_tv = (TextView) view.findViewById(R.id.tv_adpshop_distance);
                more_tv = (TextView) view.findViewById(R.id.tv_adpshop_more);
                ratingBar = (RatingBar) view.findViewById(R.id.rb_adpshop_rating);
                imageView = (ImageView) view.findViewById(R.id.iv_adpshop_image);
            } else {
                ViewPager viewPager = (ViewPager) view.findViewById(R.id.vp_shoplist_viewpager);
                title_tv = (TextView) view.findViewById(R.id.tv_shoplist_title);
                address_tv = (TextView) view.findViewById(R.id.tv_shoplist_address);
                contact_tv = (TextView) view.findViewById(R.id.tv_shoplist_contact);
                distance_tv = (TextView) view.findViewById(R.id.tv_shoplist_distance);
                more_tv = (TextView) view.findViewById(R.id.tv_shoplist_more);
                ratingBar = (RatingBar) view.findViewById(R.id.rb_shoplist_rating);
                imageView = (ImageView) view.findViewById(R.id.iv_shoplist_image);

//                String response = "{\"result\":[{\"image\":\"http://encodletechnologies.com/kingfaisal/wokshop-dashboard/images/title.jpg\"},{\"image\":\"http://encodletechnologies.com/kingfaisal/wokshop-dashboard/images/images.jpg\"},{\"image\":\"http://encodletechnologies.com/kingfaisal/wokshop-dashboard/images/download.png\"},{\"image\":\"http://encodletechnologies.com/kingfaisal/wokshop-dashboard/images/logo.png\"}]}";
                String response = sessionManager.getData(SessionManager.KEY_BANNER);
                imaglist = new ArrayList<>();
                try {
                    JSONArray imageArray = new JSONArray(response);
                    for (int j = 0; j < imageArray.length(); j++) {
                        JSONObject imgobj = imageArray.getJSONObject(j);
                        String url = imgobj.getString("image");
                        imaglist.add(url);
                    }
                    ImagePagerAdapter pagerAdapter = new ImagePagerAdapter(ctx, imaglist);
                    viewPager.setAdapter(pagerAdapter);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public Adp_ShopSearchListing(List<BuisnessListSearch> moviesList, Context ctx, double latitude, double longitude) {
        this.buisnessLists = moviesList;
        this.ctx = ctx;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View itemView;
        if (viewType == 0) {

            itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.advertisment_shoplist_slider, parent, false);
            return new MyViewHolder(itemView, 0, parent.getContext());
        } else {
            itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.adp_shoplistview, parent, false);
            return new MyViewHolder(itemView, 1, parent.getContext());
        }
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {

        String address = buisnessLists.get(position).getBuilding() + "," + buisnessLists.get(position).getCity() + ",(M.P) " + buisnessLists.get(position).getPincode();
        BuisnessListSearch buisness = buisnessLists.get(position);
        holder.title_tv.setText(buisness.getBuisness_name());
        holder.address_tv.setText(address);
        holder.contact_tv.setText(buisness.getMobile_no1());

        int rating = buisnessLists.get(position).getRating();
        holder.ratingBar.setNumStars(rating);

        double buiss_lat = buisnessLists.get(position).getLat();
        double buiss_long = buisnessLists.get(position).getSet_long();

//        double distance = distance(buiss_lat, buiss_long, latitude, longitude);

        LatLng from = new LatLng(latitude, longitude);
        LatLng to = new LatLng(buiss_lat, buiss_long);

        Double distance = SphericalUtil.computeDistanceBetween(from, to);
        double distance_km = distance / 1000;

        String distn = String.valueOf(distance_km);
        String distan = distn.substring(0, 4);

        holder.distance_tv.setText(String.valueOf(distan) + " KM");

        String url = buisnessLists.get(position).getImage();
        String cate_url = buisnessLists.get(position).getCate_icon();
        if (!url.equals("")) {
            String icon_url = WebAPI.BASE_URL + "admin/img/business_images/logo/" + url;
            icon_url = icon_url.replace(" ", "%20");
            Picasso.with(ctx)
                    .load(icon_url)
                    .resize(100, 100)
                    .placeholder(R.drawable.ic_blue_logo_small)
                    .error(R.drawable.ic_blue_logo_small)
                    .into(holder.imageView);
        } else if (!cate_url.equals("")) {

            String icon_url = WebAPI.BASE_URL + "admin/img/category_icon/" + cate_url;
            icon_url = icon_url.replace(" ", "%20");
            Picasso.with(ctx)
                    .load(icon_url)
                    .resize(100, 100)
                    .placeholder(R.drawable.blue_ic_icon)
                    .error(R.drawable.blue_ic_icon)
                    .into(holder.imageView);
        }
    }

    @Override
    public int getItemCount() {
        return buisnessLists.size();
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0) {
            return 1;
        } else if (position % 5 == 0) {
            return 0;
        } else {
            return 1;
        }
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
