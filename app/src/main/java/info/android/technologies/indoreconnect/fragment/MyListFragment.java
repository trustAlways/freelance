package info.android.technologies.indoreconnect.fragment;

import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
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
public class MyListFragment extends Fragment {
    Context ctx;
    View view;

    EditText name_et, email_et, mobile_et, category_et;
    Button submit_bt;

    String user_id;
    String name, mobile;
    ConnectionDetector connectionDetector;

    public MyListFragment(Context ctx, String user_id) {
        this.ctx = ctx;
        this.user_id = user_id;
        connectionDetector = new ConnectionDetector();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_mylist, null);
        initxml();
        return view;
    }

    private String validation() {

        name = name_et.getText().toString();
        mobile = mobile_et.getText().toString();

        if (name.equals("") || mobile.equals("")) {
            return "Please enter all field";
        } else if (mobile.length() != 10) {
            return "Please enter 10 digit mobile number";
        } else {
            return "done";
        }
    }

    private void initxml() {

        name_et = (EditText) view.findViewById(R.id.et_list_name);
        email_et = (EditText) view.findViewById(R.id.et_list_email);
        mobile_et = (EditText) view.findViewById(R.id.et_list_mobile);
        category_et = (EditText) view.findViewById(R.id.et_list_category);
        submit_bt = (Button) view.findViewById(R.id.bt_list_submit);

        submit_bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String valid = validation();
                if (valid.equals("done")) {
                    myListing();
                } else {
                    toastView(valid);
                }
            }
        });
    }

    private void setFragment(Fragment fragment) {

        final android.app.FragmentTransaction ft = getFragmentManager().beginTransaction();
        ft.replace(R.id.framelayout, fragment);
        ft.commit();
    }

    private void myListing() {

        final ProgressDialog pd = new ProgressDialog(ctx);
        pd.setMessage("loading");
        pd.setCancelable(false);
        pd.show();

        RequestQueue queue = Volley.newRequestQueue(ctx);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, WebAPI.URL_LIST_YOURSELF,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        pd.dismiss();

                        toastView("Successfully submitted your Info");
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
                params.put("name", name);
                params.put("email", "");
                params.put("mob", mobile);
                params.put("cate", "");
                return params;
            }
        };
        queue.add(stringRequest);
    }

    private void toastView(String s) {
        Toast.makeText(ctx, s, Toast.LENGTH_LONG).show();
    }
}
