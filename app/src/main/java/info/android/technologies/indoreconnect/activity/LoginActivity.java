package info.android.technologies.indoreconnect.activity;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import info.android.technologies.indoreconnect.util.ConnectionDetector;
import info.android.technologies.indoreconnect.util.SessionManager;
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

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    Context ctx;
    EditText usernam_et, password_et;
    Button login_bt, register_bt;
    CheckBox remember_cb;
    TextView forgot_tv;

    String email, password;
    ConnectionDetector connectionDetector;
    SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(info.android.technologies.indoreconnect.R.layout.activity_login);
        initxml();
        click();
        getPermission();
        checkSession();
    }

    private void checkSession() {

        boolean login = sessionManager.isLoggedIn();
        if (login) {
            startActivity(new Intent(ctx, MainActivity.class));
            finish();
        }
    }

    private void click() {
        login_bt.setOnClickListener(this);
        register_bt.setOnClickListener(this);
        forgot_tv.setOnClickListener(this);
    }

    private void initxml() {
        ctx = this;
        usernam_et = (EditText) findViewById(info.android.technologies.indoreconnect.R.id.et_login_username);
        password_et = (EditText) findViewById(info.android.technologies.indoreconnect.R.id.et_login_password);
        login_bt = (Button) findViewById(info.android.technologies.indoreconnect.R.id.bt_login_login);
        register_bt = (Button) findViewById(info.android.technologies.indoreconnect.R.id.bt_login_register);
        remember_cb = (CheckBox) findViewById(info.android.technologies.indoreconnect.R.id.cb_login_remember);
        forgot_tv = (TextView) findViewById(info.android.technologies.indoreconnect.R.id.tv_login_forgot);

        connectionDetector = new ConnectionDetector();
        sessionManager = new SessionManager(ctx);
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {

            case info.android.technologies.indoreconnect.R.id.bt_login_login:
                boolean internet = connectionDetector.isConnected();
                if (internet) {
                    getData();
                    if (!email.equals("") || !password.equals("")) {
                        boolean remember = remember_cb.isChecked();
                        if (remember) {
                            loginData("yes");
                        } else {
                            loginData("not");
                        }
                    } else {
                        toastView("Please enter username and password");
                    }
                } else {
                    toastView("No internet Connection");
                }
                break;

            case info.android.technologies.indoreconnect.R.id.bt_login_register:
                startActivity(new Intent(ctx, RegisterActivity.class));
                break;

            case info.android.technologies.indoreconnect.R.id.tv_login_forgot:
                startActivity(new Intent(ctx, ForgotActivity.class));
                break;
        }
    }

    private void loginData(final String checked) {

        final ProgressDialog pd = new ProgressDialog(ctx);
        pd.setMessage("loading");
        pd.setCancelable(false);
        pd.show();

        RequestQueue queue = Volley.newRequestQueue(ctx);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, WebAPI.URL_LOGIN,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        pd.dismiss();

                        try {
                            JSONObject data_obj = new JSONObject(response);
                            String succes = data_obj.getString("success");
                            String msg = data_obj.getString("msg");

                            if (succes.equals("1") || msg.equals("Login Successfully")) {
                                toastView("Login Successfully");
                                JSONObject data = data_obj.getJSONObject("data");
                                String name = data.getString("name");
                                String email = data.getString("email");
                                String phone = data.getString("phone_no");
                                String user_id = data.getString("user_id");
                                sessionManager.createLoginSession(name, phone, email);
                                startActivity(new Intent(ctx, MainActivity.class));
                            } else if (succes.equals("0")) {
                                toastView("Something went wrong please try again");
                            } else {
                                toastView("Something went wrong please try again");
                            }
                            usernam_et.setText("");
                            password_et.setText("");
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
                params.put("password", password);
                return params;
            }
        };

        queue.add(stringRequest);
    }

    private void toastView(String s) {
        Toast.makeText(ctx, s, Toast.LENGTH_LONG).show();
    }

    private void getData() {
        email = usernam_et.getText().toString();
        password = password_et.getText().toString();
    }

    // runtime permission

    private static final int PERMISSION_CALLBACK_CONSTANT = 100;
    private static final int REQUEST_PERMISSION_SETTING = 101;
    String[] permissionsRequired = new String[]{Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.CALL_PHONE,
            Manifest.permission.ACCESS_NETWORK_STATE, Manifest.permission.RECEIVE_SMS};
    private SharedPreferences permissionStatus;
    private boolean sentToSettings = false;

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_CALLBACK_CONSTANT) {
            //check if all permissions are granted
            boolean allgranted = false;
            for (int i = 0; i < grantResults.length; i++) {
                if (grantResults[i] == PackageManager.PERMISSION_GRANTED) {
                    allgranted = true;
                } else {
                    allgranted = false;
                    break;
                }
            }
            if (allgranted) {
            } else if (ActivityCompat.shouldShowRequestPermissionRationale(LoginActivity.this, permissionsRequired[0])
                    || ActivityCompat.shouldShowRequestPermissionRationale(LoginActivity.this, permissionsRequired[1])
                    || ActivityCompat.shouldShowRequestPermissionRationale(LoginActivity.this, permissionsRequired[2])
                    || ActivityCompat.shouldShowRequestPermissionRationale(LoginActivity.this, permissionsRequired[3])) {
                android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(LoginActivity.this);
                builder.setTitle("Need Multiple Permissions");
                builder.setMessage("This app needs Find Location permissions.");
                builder.setPositiveButton("Grant", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                        ActivityCompat.requestPermissions(LoginActivity.this, permissionsRequired, PERMISSION_CALLBACK_CONSTANT);
                    }
                });
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
                builder.show();
            } else {
                Toast.makeText(getBaseContext(), "Unable to get Permission", Toast.LENGTH_LONG).show();
            }
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_PERMISSION_SETTING) {
            if (ActivityCompat.checkSelfPermission(LoginActivity.this, permissionsRequired[0]) == PackageManager.PERMISSION_GRANTED) {
                //Got Permission
            }
        }
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
        if (sentToSettings) {
            if (ActivityCompat.checkSelfPermission(LoginActivity.this, permissionsRequired[0]) == PackageManager.PERMISSION_GRANTED) {
                //Got Permission
            }
        }
    }

    private void getPermission() {

        permissionStatus = getSharedPreferences("permissionStatus", MODE_PRIVATE);

        if (ActivityCompat.checkSelfPermission(LoginActivity.this, permissionsRequired[0]) != PackageManager.PERMISSION_GRANTED
                || ActivityCompat.checkSelfPermission(LoginActivity.this, permissionsRequired[1]) != PackageManager.PERMISSION_GRANTED
                || ActivityCompat.checkSelfPermission(LoginActivity.this, permissionsRequired[2]) != PackageManager.PERMISSION_GRANTED
                || ActivityCompat.checkSelfPermission(LoginActivity.this, permissionsRequired[3]) != PackageManager.PERMISSION_GRANTED) {

            if (ActivityCompat.shouldShowRequestPermissionRationale(LoginActivity.this, permissionsRequired[0])
                    || ActivityCompat.shouldShowRequestPermissionRationale(LoginActivity.this, permissionsRequired[1])
                    || ActivityCompat.shouldShowRequestPermissionRationale(LoginActivity.this, permissionsRequired[2])
                    || ActivityCompat.shouldShowRequestPermissionRationale(LoginActivity.this, permissionsRequired[3])) {
                //Show Information about why you need the permission
                android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(LoginActivity.this);
                builder.setTitle("Need Multiple Permissions");
                builder.setMessage("This app needs Access fine location permissions.");
                builder.setPositiveButton("Grant", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                        ActivityCompat.requestPermissions(LoginActivity.this, permissionsRequired, PERMISSION_CALLBACK_CONSTANT);
                    }
                });
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
                builder.show();
            } else if (permissionStatus.getBoolean(permissionsRequired[0], false)) {
                //Previously Permission Request was cancelled with 'Dont Ask Again',
                // Redirect to Settings after showing Information about why you need the permission
                android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(LoginActivity.this);
                builder.setTitle("Need Multiple Permissions");
                builder.setMessage("This app needs Read and Wrte Storage permissions.");
                builder.setPositiveButton("Grant", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                        sentToSettings = true;
                        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                        Uri uri = Uri.fromParts("package", getPackageName(), null);
                        intent.setData(uri);
                        startActivityForResult(intent, REQUEST_PERMISSION_SETTING);
                        Toast.makeText(getBaseContext(), "Go to Permissions to Grant storage", Toast.LENGTH_LONG).show();
                    }
                });
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
                builder.show();
            } else {
                //just request the permission
                ActivityCompat.requestPermissions(LoginActivity.this, permissionsRequired, PERMISSION_CALLBACK_CONSTANT);
            }
            SharedPreferences.Editor editor = permissionStatus.edit();
            editor.putBoolean(permissionsRequired[0], true);
            editor.commit();
        } else {
            //You already have the permission, just go ahead.
        }
    }
}
