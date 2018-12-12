package info.android.technologies.indoreconnect.fragment;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import info.android.technologies.indoreconnect.R;

/**
 * Created by kamlesh on 12/7/2017.
 */
public class AboutUsFragment extends Fragment {

    View view;

    TextView about_us_tv1, about_us_tv2, about_us_tv3;

    public AboutUsFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_aboutus, null);
        about_us_tv1 = (TextView) view.findViewById(R.id.tv_about_us1);
        about_us_tv2 = (TextView) view.findViewById(R.id.tv_about_us2);
        about_us_tv3 = (TextView) view.findViewById(R.id.tv_about_us3);
//        String sourceString = "<b>" + id + "</b> " + name;
        String sourceString1 = "<b>" + "“IC – indore connect”" + "</b>" + "is a product of" + "<b>" + "“SAFAR Innovation”" + "</b>" + " which situated in <b>INDORE </b>which properly known as <b>Food Capital</b> of <b>INDIA</b>.";
        String sourceString2 = "As we are indorian, so we are to glad to introduce our indore to you as our APP which we called <b>IC – indore connect </b>connecting you to indore";
        about_us_tv1.setText(Html.fromHtml(sourceString1));
        about_us_tv2.setText(Html.fromHtml(sourceString2));

        return view;
    }
}
