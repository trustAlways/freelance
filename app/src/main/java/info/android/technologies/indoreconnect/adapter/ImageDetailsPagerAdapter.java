package info.android.technologies.indoreconnect.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import info.android.technologies.indoreconnect.model.Images;
import info.android.technologies.indoreconnect.util.RoundedCornersTransformation;
import info.android.technologies.indoreconnect.util.WebAPI;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by kamlesh on 11/1/2017.
 */
public class ImageDetailsPagerAdapter extends android.support.v4.view.PagerAdapter {
    Context ctx;
    LayoutInflater layoutInflater;

    ArrayList<Images> imaglist;

    public ImageDetailsPagerAdapter(Context ctx, ArrayList<Images> imaglist) {
        this.ctx = ctx;
        this.imaglist = imaglist;
        layoutInflater = (LayoutInflater) this.ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return imaglist.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == ((LinearLayout) object);
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        View itemView = layoutInflater.inflate(info.android.technologies.indoreconnect.R.layout.adp_pager_slider, container, false);

        ImageView imageView = (ImageView) itemView.findViewById(info.android.technologies.indoreconnect.R.id.imageView);

        String url = imaglist.get(position).getImage();
        String type = imaglist.get(position).getType();
        if (!url.equals("")) {

            if (type.equals("image")) {

                String banner_url = WebAPI.BASE_URL + "admin/img/business_images/images/" + url;
                String banner_url_1 = banner_url.replace(" ", "%20");
                Picasso.with(ctx)
                        .load(banner_url_1)
                        .resize(420, 230)
                        .transform(new RoundedCornersTransformation(10, 1))
                        .placeholder(info.android.technologies.indoreconnect.R.drawable.logo)
                        .error(info.android.technologies.indoreconnect.R.drawable.logo)
                        .into(imageView);
            } else if (type.equals("cover")) {

                String final_url = WebAPI.BASE_URL + "admin/img/business_images/cover/" + url;
                String final_url_2 = final_url.replace(" ", "%20");
                Picasso.with(ctx)
                        .load(final_url_2)
                        .resize(300, 150)
                        .transform(new RoundedCornersTransformation(10, 1))
                        .placeholder(info.android.technologies.indoreconnect.R.drawable.logo)
                        .error(info.android.technologies.indoreconnect.R.drawable.logo)
                        .into(imageView);
            }
        }
        try {
            container.addView(itemView);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return itemView;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((LinearLayout) object);
    }
}