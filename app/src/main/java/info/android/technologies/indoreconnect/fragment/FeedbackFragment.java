package info.android.technologies.indoreconnect.fragment;

import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RatingBar;
import android.widget.Toast;

import info.android.technologies.indoreconnect.R;
import info.android.technologies.indoreconnect.util.ConnectionDetector;
import info.android.technologies.indoreconnect.util.WebAPI;
import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by kamlesh on 12/7/2017.
 */
public class FeedbackFragment extends Fragment {
    Context ctx;
    View view;

    ImageView sent_iv;
    RadioButton bug_rb, enhancement_rb, product_rb;
    EditText details_et;
    RatingBar ratingBar;

    ConnectionDetector connectionDetector;

    String about = "no", detail, rating = "0";
    String user_id;

    public FeedbackFragment(Context ctx, String user_id) {
        this.ctx = ctx;
        this.user_id = user_id;
        connectionDetector = new ConnectionDetector();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_feedback, null);
        initxml();
        return view;
    }

    private String validadtion() {

        if (detail.equals("")) {
            return "Please enter details about your feedback";
        } else if (rating.equals("0")) {
            return "Please select rating";
        } else {
            return "Done";
        }
    }

    private void getData() {

        detail = details_et.getText().toString();
        if (bug_rb.isChecked()) {
            about = "bug";
        } else if (enhancement_rb.isChecked()) {
            about = "enhancement";
        } else if (product_rb.isChecked()) {
            about = "product";
        } else {
            about = "nothing";
        }
        rating = String.valueOf(ratingBar.getRating());
    }

    private void toastView(String s) {
        Toast.makeText(ctx, s, Toast.LENGTH_LONG).show();
    }

    private void initxml() {
        bug_rb = (RadioButton) view.findViewById(R.id.rb_feedback_bug);
        enhancement_rb = (RadioButton) view.findViewById(R.id.rb_feedback_enhancement);
        product_rb = (RadioButton) view.findViewById(R.id.rb_feedback_product);

        sent_iv = (ImageView) view.findViewById(R.id.iv_feedback_sent);
        details_et = (EditText) view.findViewById(R.id.et_feedback_details);
        ratingBar = (RatingBar) view.findViewById(R.id.rb_feedback_rating);

        sent_iv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                boolean internet = connectionDetector.isConnected();
                getData();
                String valid = validadtion();
                if (internet) {
                    if (valid.equals("Done")) {
                        feedbackData();
                    } else {
                        toastView(valid);
                    }
                } else {
                    toastView("No internet Connection");
                }
            }
        });
    }

    private void setFragment(Fragment fragment) {

        final android.app.FragmentTransaction ft = getFragmentManager().beginTransaction();
        ft.replace(R.id.framelayout, fragment);
        ft.commit();
    }

    private void feedbackData() {

        final ProgressDialog pd = new ProgressDialog(ctx);
        pd.setMessage("loading");
        pd.setCancelable(false);
        pd.show();

        RequestQueue queue = Volley.newRequestQueue(ctx);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, WebAPI.URL_FEEDBACK,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        pd.dismiss();

                        toastView("Successfully submitted your feedback");
                        Home2Fragment fragment = new Home2Fragment(ctx);
                        setFragment(fragment);
                        /*try {
                            JSONObject data_obj = new JSONObject(response);
                            String succes = data_obj.getString("success");
                            String msg = data_obj.getString("msg");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }*/
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(ctx, error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("about", about);
                params.put("msg", detail);
                params.put("rate", rating);
                params.put("email", user_id);
                return params;
            }
        };
        queue.add(stringRequest);
    }

}
