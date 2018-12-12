package info.android.technologies.indoreconnect.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import info.android.technologies.indoreconnect.R;
import info.android.technologies.indoreconnect.model.InfoCate;
import info.android.technologies.indoreconnect.util.WebAPI;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by kamlesh on 12/2/2017.
 */
public class CateGVAdp extends BaseAdapter {
    Context ctx;
    ArrayList<InfoCate> infocate_list;

    public CateGVAdp(Context ctx, ArrayList<InfoCate> infocate_list) {
        this.ctx = ctx;
        this.infocate_list = infocate_list;
    }

    @Override
    public int getCount() {
        return infocate_list.size();
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
        View view = inflater.inflate(info.android.technologies.indoreconnect.R.layout.adp_info_cate, null);

        ImageView imageView = (ImageView) view.findViewById(info.android.technologies.indoreconnect.R.id.iv_adpinfo_image);
        TextView name_tv = (TextView) view.findViewById(info.android.technologies.indoreconnect.R.id.tv_adpinfo_name);

        String url = infocate_list.get(position).getCate_icon();
        if (!url.equals("")) {

            String icon_url = WebAPI.BASE_URL+"admin/img/category_icon/" + url;
            icon_url = icon_url.replace(" ","%20");
            Picasso.with(ctx)
                    .load(icon_url)
//                    .resize(70, 70)
                    .placeholder(R.drawable.blue_ic_icon)
                    .error(R.drawable.blue_ic_icon)
                    .into(imageView);
        }
        name_tv.setText(infocate_list.get(position).getSub_cate());
        return view;
    }
}