package info.android.technologies.indoreconnect.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import info.android.technologies.indoreconnect.R;
import info.android.technologies.indoreconnect.model.InfoSubCate;
import info.android.technologies.indoreconnect.util.WebAPI;

/**
 * Created by kamlesh on 1/26/2018.
 */
public class InfoSubListAdp extends BaseAdapter {

    Context ctx;
    ArrayList<InfoSubCate> sub_cate_list;

    public InfoSubListAdp(Context ctx, ArrayList<InfoSubCate> sub_cate_list) {
        this.ctx = ctx;
        this.sub_cate_list = sub_cate_list;
    }

    @Override
    public int getCount() {
        return sub_cate_list.size();
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
        View view = inflater.inflate(R.layout.adp_info_sub_cate, null);

        TextView name_tv = (TextView) view.findViewById(R.id.tv_infosubcate_name);
        ImageView image = (ImageView) view.findViewById(R.id.iv_subinfoadp_image);

        name_tv.setText(sub_cate_list.get(position).getName());

        String url = sub_cate_list.get(position).getIcon();
        String parent_url = sub_cate_list.get(position).getParent_icon();
        if (!url.equals("")) {

            String icon_url = WebAPI.BASE_URL + "admin/img/category_icon/" + url;
            icon_url = icon_url.replace(" ", "%20");
            Picasso.with(ctx)
                    .load(icon_url)
//                    .resize(70, 70)
                    .placeholder(R.drawable.blue_ic_icon)
                    .error(R.drawable.blue_ic_icon)
                    .into(image);
        } else {
            if (!parent_url.equals("")) {

                String icon_url = WebAPI.BASE_URL + "admin/img/category_icon/" + parent_url;
                icon_url = icon_url.replace(" ", "%20");
                Picasso.with(ctx)
                        .load(icon_url)
//                    .resize(70, 70)
                        .placeholder(R.drawable.blue_ic_icon)
                        .error(R.drawable.blue_ic_icon)
                        .into(image);
            }
        }
        return view;
    }
}
