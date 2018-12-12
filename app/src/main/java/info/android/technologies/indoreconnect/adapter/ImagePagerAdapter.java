package info.android.technologies.indoreconnect.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import info.android.technologies.indoreconnect.R;
import info.android.technologies.indoreconnect.util.RoundedCornersTransformation;
import info.android.technologies.indoreconnect.util.WebAPI;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by kamlesh on 11/1/2017.
 */
public class ImagePagerAdapter extends android.support.v4.view.PagerAdapter {
    Context ctx;
    LayoutInflater layoutInflater;

    ArrayList<String> imaglist;

    public ImagePagerAdapter(Context ctx, ArrayList<String> imaglist) {
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
        View itemView = layoutInflater.inflate(R.layout.adp_pager_slider, container, false);

        ImageView imageView = (ImageView) itemView.findViewById(R.id.imageView);

        String url = imaglist.get(position);
        if (!url.equals("")) {
            String banner_url = WebAPI.BASE_URL+"admin/img/banner/" + url;
            String final_url = banner_url.replace(" ", "%20");
            Picasso.with(ctx)
                    .load(final_url)
                    .resize(420, 230)
                    .transform(new RoundedCornersTransformation(10, 5))
                    .placeholder(R.drawable.blue_ic_icon)
                    .error(R.drawable.blue_ic_icon)
                    .into(imageView);
        }
        container.addView(itemView);
        return itemView;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((LinearLayout) object);
    }
}