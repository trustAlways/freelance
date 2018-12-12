package info.android.technologies.indoreconnect.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Filterable;
import android.widget.TextView;

import java.util.ArrayList;

import info.android.technologies.indoreconnect.R;

/**
 * Created by kamlesh on 2/13/2018.
 */
public class Adp_Search extends ArrayAdapter<String> implements Filterable {

    Context mContext;
    int layoutResourceId;
    ArrayList<String> search_list;

    public Adp_Search(Context mContext, int layoutResourceId, ArrayList<String> search_list) {

        super(mContext, layoutResourceId, search_list);

        this.layoutResourceId = layoutResourceId;
        this.mContext = mContext;
        this.search_list = search_list;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        try {
            if (convertView == null) {
                // inflate the layout
                LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = inflater.inflate(layoutResourceId, parent, false);
            }

            TextView textViewItem = (TextView) convertView.findViewById(R.id.tv_adpsearch_name);
            textViewItem.setText(search_list.get(position));

        } catch (NullPointerException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return convertView;
    }
}
