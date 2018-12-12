package info.android.technologies.indoreconnect.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import info.android.technologies.indoreconnect.R;
import info.android.technologies.indoreconnect.model.Offer;
import info.android.technologies.indoreconnect.util.RoundedCornersTransformation;
import info.android.technologies.indoreconnect.util.WebAPI;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by kamlesh on 12/2/2017.
 */
public class OfferImgAdapter extends BaseAdapter {
    Context ctx;
    ArrayList<Offer> offer_imag_list;

    public OfferImgAdapter(Context ctx, ArrayList<Offer> offer_imag_list) {
        this.ctx = ctx;
        this.offer_imag_list = offer_imag_list;
    }

    @Override
    public int getCount() {
        return offer_imag_list.size();
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
        View view = inflater.inflate(R.layout.adp_offerimage, null);

        ImageView imageView = (ImageView) view.findViewById(R.id.iv_adpoffer_imageview);
        String url = offer_imag_list.get(position).getImage();
        if (!url.equals("")) {
            String final_url = WebAPI.BASE_URL+"admin/img/category_offer/" + url;
            final_url = final_url.replace(" ","%20");
            Picasso.with(ctx)
                    .load(final_url)
                    .resize(400, 230)
                    .transform(new RoundedCornersTransformation(10, 5))
                    .placeholder(R.drawable.logo)
                    .error(R.drawable.logo)
                    .into(imageView);
        }
        return view;
    }
}