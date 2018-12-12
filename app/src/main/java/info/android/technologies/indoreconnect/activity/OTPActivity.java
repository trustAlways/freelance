package info.android.technologies.indoreconnect.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import info.android.technologies.indoreconnect.R;
import info.android.technologies.indoreconnect.util.ConnectionDetector;
import info.android.technologies.indoreconnect.util.SessionManager;
import info.android.technologies.indoreconnect.util.SmsListener;
import info.android.technologies.indoreconnect.util.SmsReceiver;
import info.android.technologies.indoreconnect.util.WebAPI;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

public class OTPActivity extends AppCompatActivity implements View.OnClickListener {

    Context ctx;
    EditText otp_et;
    Button verify_bt;

    String name, email, mobile, otp;

    ConnectionDetector connectionDetector;
    SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_otp);
        initxml();
        getData();
        boolean internet = connectionDetector.isConnected();
        if (internet) {
            sendOTP();
        } else {
            toastView("No internet Connection.");
        }

        SmsReceiver.bindListener(new SmsListener() {
            @Override
            public void messageReceived(String messageText) {
                String otp = messageText.substring(72, 78);

                if (messageText.contains("Thanks to connect with IC")) {
                    otp_et.setText(otp);
                }
            }
        });
    }

    private void sendOTP() {
        otp = generateOTP(6);
        sessionManager.setData(SessionManager.KEY_OTP, otp);
        String msg = "Hi," +
                "Thanks to connect with IC. Plz verify yourself, verification code is " + otp + "." +
                "Thanks.";
        String url = WebAPI.URL_MESSAGE_GATEWAY + "mobiles=" + mobile + "&message=" + msg + "&sender=ICCONN&route=4&country=91";

        final ProgressDialog pd = new ProgressDialog(ctx);
        pd.setMessage("wait..");
        pd.setCancelable(false);
        pd.show();
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        pd.dismiss();
                        toastView("Successfully sent verification code on your mobile number.Please Enter Verification code");
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(ctx, error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
        RequestQueue requestQueue = Volley.newRequestQueue(ctx);
        requestQueue.add(stringRequest);
    }

    private void getData() {

        Intent in = getIntent();
        name = in.getStringExtra("name");
        email = in.getStringExtra("email");
        mobile = in.getStringExtra("mobile");
    }

    private void initxml() {
        ctx = this;
        otp_et = (EditText) findViewById(R.id.et_otp_otpbox);
        verify_bt = (Button) findViewById(R.id.bt_otp_verify);

        connectionDetector = new ConnectionDetector();
        sessionManager = new SessionManager(ctx);
        verify_bt.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {

            case R.id.bt_otp_verify:
                boolean internet = connectionDetector.isConnected();
                if (internet) {
                    String enter_otp = otp_et.getText().toString();
                    if (!enter_otp.equals("")) {
                        String sent_otp = sessionManager.getData(SessionManager.KEY_OTP);

                        if (enter_otp.equals(sent_otp)) {
                            sessionManager.createLoginSession(name, mobile, email);
                            startActivity(new Intent(ctx, MainActivity.class));
                            finish();
                            toastView("Verified Successfully.");
                        } else {
                            otp_et.setText("");
                            toastView("You enter wrong OTP,please try again");
                        }
                    } else {
                        toastView("Please enter OTP");
                    }
                } else {
                    toastView("No internet Connection");
                }
                break;
        }
    }

    private void toastView(String s) {
        Toast.makeText(ctx, s, Toast.LENGTH_LONG).show();
    }

    public static String generateOTP(int size) {

        StringBuilder generatedToken = new StringBuilder();
        try {
            SecureRandom number = SecureRandom.getInstance("SHA1PRNG");
            for (int i = 0; i < size; i++) {
                generatedToken.append(number.nextInt(9));
            }
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return generatedToken.toString();
    }

}
