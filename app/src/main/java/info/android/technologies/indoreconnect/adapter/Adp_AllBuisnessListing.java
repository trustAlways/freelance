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
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import info.android.technologies.indoreconnect.R;
import info.android.technologies.indoreconnect.model.AllBuissness;
import info.android.technologies.indoreconnect.model.BuisnessList;
import info.android.technologies.indoreconnect.util.SessionManager;
import info.android.technologies.indoreconnect.util.WebAPI;

/**
 * Created by kamlesh on 12/15/2017.
 */
public class Adp_AllBuisnessListing extends RecyclerView.Adapter<Adp_AllBuisnessListing.MyViewHolder> {

    private List<AllBuissness> buisnessLists;
    Context ctx;
    ArrayList<String> imaglist;

    double latitude, longitude;

    SessionManager sessionManager;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView title_tv, address_tv, contact_tv, distance_tv, more_tv, underline_tv;
        RatingBar ratingBar;
        ImageView imageView;

        public MyViewHolder(View view, int i, Context ctx) {
            super(view);

            sessionManager = new SessionManager(ctx);
            if (i == 1) {
                title_tv = (TextView) view.findViewById(R.id.tv_adpshop_title);
                underline_tv = (TextView) view.findViewById(R.id.tv_adpshop_underline);
                address_tv = (TextView) view.findViewById(R.id.tv_adpshop_address);
                contact_tv = (TextView) view.findViewById(R.id.tv_adpshop_contact);
                distance_tv = (TextView) view.findViewById(R.id.tv_adpshop_distance);
                more_tv = (TextView) view.findViewById(R.id.tv_adpshop_more);
                ratingBar = (RatingBar) view.findViewById(R.id.rb_adpshop_rating);
                imageView = (ImageView) view.findViewById(R.id.iv_adpshop_image);
            } else {
                ViewPager viewPager = (ViewPager) view.findViewById(R.id.vp_shoplist_viewpager);
                title_tv = (TextView) view.findViewById(R.id.tv_shoplist_title);
                underline_tv = (TextView) view.findViewById(R.id.tv_shoplist_underline);
                address_tv = (TextView) view.findViewById(R.id.tv_shoplist_address);
                contact_tv = (TextView) view.findViewById(R.id.tv_shoplist_contact);
                distance_tv = (TextView) view.findViewById(R.id.tv_shoplist_distance);
                more_tv = (TextView) view.findViewById(R.id.tv_shoplist_more);
                ratingBar = (RatingBar) view.findViewById(R.id.rb_shoplist_rating);
                imageView = (ImageView) view.findViewById(R.id.iv_shoplist_image);

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

    public Adp_AllBuisnessListing(List<AllBuissness> moviesList, Context ctx) {
        this.buisnessLists = moviesList;
        this.ctx = ctx;
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

        AllBuissness buisness = buisnessLists.get(position);
        holder.title_tv.setText(buisness.getName());
        holder.address_tv.setText(buisness.getAddress());
        holder.contact_tv.setText(buisness.getContact());

        holder.underline_tv.setText(buisness.getName());


        String rating = String.valueOf(buisnessLists.get(position).getRating());
        String rating_1 = rating.substring(0, 1);
        int rate = Integer.parseInt(rating_1);
        holder.ratingBar.setRating(rate);

        String distance = String.valueOf(buisness.getDistance());
        String dis = "";
        try {
            dis = distance.substring(0, 3);
        } catch (Exception e) {
            e.printStackTrace();
        }

        holder.distance_tv.setText(dis + " KM");

        String url = buisnessLists.get(position).getImage();
        String cate_url = buisnessLists.get(position).getParent_icon();
        String type = buisnessLists.get(position).getType();
        if (type.equals("main")) {

            if (!url.equals("")) {
                String icon_url = WebAPI.BASE_URL + "admin/img/business_images/logo/" + url;
                String final_url = icon_url.replace(" ", "%20");
                Picasso.with(ctx)
                        .load(final_url)
                        .placeholder(R.drawable.blue_ic_icon)
                        .error(R.drawable.blue_ic_icon)
                        .into(holder.imageView);
            } else if (!cate_url.equals("")) {

                String icon_url = WebAPI.BASE_URL + "admin/img/category_icon/" + cate_url;
                String final_url = icon_url.replace(" ", "%20");
                Picasso.with(ctx)
                        .load(final_url)
                        .placeholder(R.drawable.blue_ic_icon)
                        .error(R.drawable.blue_ic_icon)
                        .into(holder.imageView);
            }
        } else {

            if (!url.equals("")) {
                String icon_url = WebAPI.BASE_URL + "admin/img/category_icon/" + url;
                String final_url = icon_url.replace(" ", "%20");
                Picasso.with(ctx)
                        .load(final_url)
                        .placeholder(R.drawable.blue_ic_icon)
                        .error(R.drawable.blue_ic_icon)
                        .into(holder.imageView);
            }
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
