package info.android.technologies.indoreconnect.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import info.android.technologies.indoreconnect.R;
import info.android.technologies.indoreconnect.model.Notification;

import java.util.ArrayList;

/**
 * Created by kamlesh on 12/8/2017.
 */
public class NotificationAdapter extends BaseAdapter {
    Context ctx;
    ArrayList<Notification> noti_list;

    public NotificationAdapter(Context ctx, ArrayList<Notification> noti_list) {
        this.ctx = ctx;
        this.noti_list = noti_list;
    }

    @Override
    public int getCount() {
        return noti_list.size();
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
        View view = inflater.inflate(R.layout.adp_notification, null);

        TextView msg_tv = (TextView) view.findViewById(R.id.tv_notiadp_msg);
        TextView date_tv = (TextView) view.findViewById(R.id.tv_notiadp_date);
        msg_tv.setText(noti_list.get(position).getMsg());
        date_tv.setText(noti_list.get(position).getDate());
        return view;
    }
}
