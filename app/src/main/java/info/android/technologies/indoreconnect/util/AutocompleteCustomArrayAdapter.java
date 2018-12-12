package info.android.technologies.indoreconnect.util;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import info.android.technologies.indoreconnect.R;
import info.android.technologies.indoreconnect.activity.MainActivity;

/**
 * Created by kamlesh on 2/21/2018.
 */
public class AutocompleteCustomArrayAdapter extends ArrayAdapter<String> {

    final String TAG = "AutocompleteCustomArrayAdapter.java";

    Context mContext;
    int layoutResourceId;
    ArrayList<String> list;

    public AutocompleteCustomArrayAdapter(Context mContext, int layoutResourceId, ArrayList<String> list) {

        super(mContext, layoutResourceId, list);

        this.layoutResourceId = layoutResourceId;
        this.mContext = mContext;
        this.list = list;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        try {

            /*
             * The convertView argument is essentially a "ScrapView" as described is Lucas post
             * http://lucasr.org/2012/04/05/performance-tips-for-androids-listview/
             * It will have a non-null value when ListView is asking you recycle the row layout.
             * So, when convertView is not null, you should simply update its contents instead of inflating a new row layout.
             */
            if (convertView == null) {
                // inflate the layout
                LayoutInflater inflater = ((MainActivity) mContext).getLayoutInflater();
                convertView = inflater.inflate(layoutResourceId, parent, false);
            }

            // object item based on the position

            // get the TextView and then set the text (item name) and tag (item ID) values
            TextView textViewItem = (TextView) convertView.findViewById(R.id.tv_customactv);
            textViewItem.setText(list.get(position));

            // in case you want to add some style, you can do something like:
//            textViewItem.setBackgroundColor(Color.CYAN);

        } catch (NullPointerException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return convertView;

    }
}
