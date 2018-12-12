package info.android.technologies.indoreconnect.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import info.android.technologies.indoreconnect.R;
import info.android.technologies.indoreconnect.model.HomeCate;
import info.android.technologies.indoreconnect.util.RoundedCornersTransformation;
import info.android.technologies.indoreconnect.util.WebAPI;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by kamlesh on 12/15/2017.
 */
public class SeeAllAdapter extends BaseAdapter {
    Context ctx;
    ArrayList<HomeCate> subcate_list;
    String maincate;

    public SeeAllAdapter(Context ctx, ArrayList<HomeCate> subcate_list, String maincate) {
        this.ctx = ctx;
        this.subcate_list = subcate_list;
        this.maincate = maincate;
    }

    @Override
    public int getCount() {
        return subcate_list.size();
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
        View view = inflater.inflate(info.android.technologies.indoreconnect.R.layout.adp_home_subcate, null);

        ImageView imageView = (ImageView) view.findViewById(info.android.technologies.indoreconnect.R.id.iv_adphomegrid_subcate_image);
        TextView cate_name_tv = (TextView) view.findViewById(info.android.technologies.indoreconnect.R.id.tv_adphomegrid_subcate_name);

        cate_name_tv.setText(subcate_list.get(position).getSubcate_name());

        String url = subcate_list.get(position).getSubcate_image();
        if (!url.equals("")) {
            String icon_url = WebAPI.BASE_URL+"admin/img/category_icon/" + url;
            icon_url = icon_url.replace(" ","%20");
            Picasso.with(ctx)
                    .load(icon_url)
//                    .resize(70, 70)
                    .placeholder(R.drawable.blue_ic_icon)
                    .transform(new RoundedCornersTransformation(10, 1))
                    .error(R.drawable.blue_ic_icon)
                    .into(imageView);
        }
        return view;
    }
}
