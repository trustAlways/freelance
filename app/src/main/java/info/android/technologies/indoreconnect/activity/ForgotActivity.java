package info.android.technologies.indoreconnect.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import info.android.technologies.indoreconnect.util.ConnectionDetector;
import info.android.technologies.indoreconnect.util.WebAPI;
import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class ForgotActivity extends AppCompatActivity implements View.OnClickListener {

    Context ctx;
    EditText email_et;
    Button submit_bt;

    String email;
    ConnectionDetector connectionDetector;
    String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(info.android.technologies.indoreconnect.R.layout.activity_forgot);
        initxml();
    }

    private void initxml() {
        ctx = this;
        email_et = (EditText) findViewById(info.android.technologies.indoreconnect.R.id.et_forgot_email);
        submit_bt = (Button) findViewById(info.android.technologies.indoreconnect.R.id.bt_forgot_submit);

        connectionDetector = new ConnectionDetector();
        submit_bt.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
/*
        boolean internet = connectionDetector.isConnected();
        if (internet) {
            email = email_et.getText().toString();
            if (email.matches(emailPattern)) {
                recoverPassword();
            } else {
                toastView("Please enter valid email");
            }
        } else {
            toastView("No internet Connection");
        }*/
        finish();
    }

    private void recoverPassword() {

        final ProgressDialog pd = new ProgressDialog(ctx);
        pd.setMessage("loading");
        pd.setCancelable(false);
        pd.show();

        RequestQueue queue = Volley.newRequestQueue(ctx);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, WebAPI.URL_FORGOT,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        pd.dismiss();

                        try {
                            JSONObject data_obj = new JSONObject(response);
                            String succes = data_obj.getString("success");
                            String msg = data_obj.getString("msg");

                            if (succes.equals("1") || msg.equals("User Created Successfully")) {
                                toastView("Account Successfully Created.");
                                finish();
                            } else if (succes.equals("0") || msg.equals("User Already Exists")) {
                                toastView("User Already Exists, Please use another id");
                                finish();
                            } else {
                                toastView("Something went wrong please try again");
                                finish();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
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
                params.put("email", email);
                return params;
            }
        };

        queue.add(stringRequest);
    }

    private void toastView(String s) {
        Toast.makeText(ctx, s, Toast.LENGTH_LONG).show();
    }
}
