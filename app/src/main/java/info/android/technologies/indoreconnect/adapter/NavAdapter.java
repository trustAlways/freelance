package info.android.technologies.indoreconnect.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import info.android.technologies.indoreconnect.R;
import info.android.technologies.indoreconnect.model.Navigation;
import info.android.technologies.indoreconnect.util.SessionManager;

import java.util.ArrayList;

/**
 * Created by kamlesh on 12/7/2017.
 */
public class NavAdapter extends BaseAdapter {
    Context ctx;
    ArrayList<Navigation> nav_list;
    String type = "";
    SessionManager sessionManager;

    public NavAdapter(Context ctx, ArrayList<Navigation> nav_list) {
        this.ctx = ctx;
        this.nav_list = nav_list;
        sessionManager = new SessionManager(ctx);


        type = sessionManager.getData(SessionManager.KEY_NAV_VIEW_TYPE);
    }

    @Override
    public int getCount() {
        return nav_list.size();
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
        View view = inflater.inflate(R.layout.nav_adapter, null);

        ImageView imageView = (ImageView) view.findViewById(R.id.iv_navadp_image);
        ImageView new_text_iv = (ImageView) view.findViewById(R.id.iv_navadp_new);
        TextView name_tv = (TextView) view.findViewById(R.id.tv_navadp_name);

        imageView.setImageResource(nav_list.get(position).getImage());
        name_tv.setText(nav_list.get(position).getName() + "");

        if (position == 1) {
            new_text_iv.setVisibility(View.VISIBLE);
            try {
                if (type.equals("view")) {
                    new_text_iv.setVisibility(View.VISIBLE);
                } else {
                    new_text_iv.setVisibility(View.GONE);
                }
            } catch (Exception e) {
                e.printStackTrace();
                new_text_iv.setVisibility(View.GONE);
            }
        }
        return view;
    }
}
