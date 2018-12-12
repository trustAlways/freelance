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
import android.widget.EditText;
import android.widget.Toast;

import info.android.technologies.indoreconnect.R;
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

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class RegisterActivity extends AppCompatActivity {

    Context ctx;
    EditText name_et, email_et, mobile_et, password_et;
    Button register_bt;

    String name, email, mobile;
    ConnectionDetector connectionDetector;
    SessionManager sessionManager;

    String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        initxml();
        getPermission();
        boolean login = sessionManager.isLoggedIn();
        if (login) {
            startActivity(new Intent(ctx, MainActivity.class));
            finish();
        }
        click();
    }

    private void getData() {
        name = name_et.getText().toString();
        email = email_et.getText().toString();
        mobile = mobile_et.getText().toString();
    }

    private void registerData() {

        final ProgressDialog pd = new ProgressDialog(ctx);
        pd.setMessage("loading");
        pd.setCancelable(false);
        pd.show();

        RequestQueue queue = Volley.newRequestQueue(ctx);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, WebAPI.URL_REGISTER,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        pd.dismiss();

                        try {
                            JSONObject data_obj = new JSONObject(response);
                            String msg = data_obj.getString("success");

                            if (msg.equals("1")) {
                                setDate();
                                toastView("Account Successfully Created.");
//                                sessionManager.createLoginSession(name, mobile, email);
//                                startActivity(new Intent(ctx, MainActivity.class));
                                startActivity(new Intent(ctx, OTPActivity.class)
                                        .putExtra("name", name)
                                        .putExtra("mobile", mobile)
                                        .putExtra("email", email)
                                );
                                finish();
                            } else if (msg.equals("2")) {
//                                sessionManager.createLoginSession(name, mobile, email);
//                                startActivity(new Intent(ctx, MainActivity.class));
                                setDate();
                                toastView("Already exist fom this number please verify your account.");
                                startActivity(new Intent(ctx, OTPActivity.class)
                                        .putExtra("name", name)
                                        .putExtra("mobile", mobile)
                                        .putExtra("email", email)
                                );
                                finish();
                            } else {
                                toastView("Something went wrong please try again");
                                email_et.setText("");
                                mobile_et.setText("");
                                email_et.setText("");
                                name_et.setText("");
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
                params.put("name", name);
                params.put("email", email);
                params.put("mobile_no", mobile);
                params.put("password", "123456");
                return params;
            }
        };

        queue.add(stringRequest);
    }

    private void setDate() {
        Date curDate = new Date();
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        String DateToStr = format.format(curDate);
        DateToStr = DateToStr.replace(" ", "%20");
        sessionManager.setData(SessionManager.KEY_CREATE_DATE, DateToStr);
    }

    private String checkValidation() {

        if (name.equals("") || email.equals("") || mobile.equals("")) {
            return "Please enter all field";
        } else if (mobile.length() != 10) {
            return "Please enter 10 digit mobile number";
        } else {
            return "done";
        }
    }

    private void toastView(String s) {
        Toast.makeText(ctx, s, Toast.LENGTH_LONG).show();
    }

    private void click() {

        register_bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean internet = connectionDetector.isConnected();
                if (internet) {
                    getData();
                    String check = checkValidation();
                    if (check.equals("done")) {
                        if (email.matches(emailPattern)) {
                            registerData();
                        } else {
                            toastView("Invalid E-mail ");
                        }
                    } else {
                        toastView(check);
                    }
                } else {
                    toastView("");
                }
            }
        });
    }

    private void initxml() {
        ctx = this;
        name_et = (EditText) findViewById(R.id.et_reg_name);
        email_et = (EditText) findViewById(R.id.et_reg_email);
        mobile_et = (EditText) findViewById(R.id.et_reg_mobile);
        password_et = (EditText) findViewById(R.id.et_reg_password);
        register_bt = (Button) findViewById(R.id.bt_reg_register);

        connectionDetector = new ConnectionDetector();
        sessionManager = new SessionManager(ctx);
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
            } else if (ActivityCompat.shouldShowRequestPermissionRationale(RegisterActivity.this, permissionsRequired[0])
                    || ActivityCompat.shouldShowRequestPermissionRationale(RegisterActivity.this, permissionsRequired[1])
                    || ActivityCompat.shouldShowRequestPermissionRationale(RegisterActivity.this, permissionsRequired[2])
                    || ActivityCompat.shouldShowRequestPermissionRationale(RegisterActivity.this, permissionsRequired[3])) {
                android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(RegisterActivity.this);
                builder.setTitle("Need Multiple Permissions");
                builder.setMessage("This app needs Find Location permissions.");
                builder.setPositiveButton("Grant", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                        ActivityCompat.requestPermissions(RegisterActivity.this, permissionsRequired, PERMISSION_CALLBACK_CONSTANT);
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
            if (ActivityCompat.checkSelfPermission(RegisterActivity.this, permissionsRequired[0]) == PackageManager.PERMISSION_GRANTED) {
                //Got Permission
            }
        }
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
        if (sentToSettings) {
            if (ActivityCompat.checkSelfPermission(RegisterActivity.this, permissionsRequired[0]) == PackageManager.PERMISSION_GRANTED) {
                //Got Permission
            }
        }
    }

    private void getPermission() {

        permissionStatus = getSharedPreferences("permissionStatus", MODE_PRIVATE);

        if (ActivityCompat.checkSelfPermission(RegisterActivity.this, permissionsRequired[0]) != PackageManager.PERMISSION_GRANTED
                || ActivityCompat.checkSelfPermission(RegisterActivity.this, permissionsRequired[1]) != PackageManager.PERMISSION_GRANTED
                || ActivityCompat.checkSelfPermission(RegisterActivity.this, permissionsRequired[2]) != PackageManager.PERMISSION_GRANTED
                || ActivityCompat.checkSelfPermission(RegisterActivity.this, permissionsRequired[3]) != PackageManager.PERMISSION_GRANTED) {

            if (ActivityCompat.shouldShowRequestPermissionRationale(RegisterActivity.this, permissionsRequired[0])
                    || ActivityCompat.shouldShowRequestPermissionRationale(RegisterActivity.this, permissionsRequired[1])
                    || ActivityCompat.shouldShowRequestPermissionRationale(RegisterActivity.this, permissionsRequired[2])
                    || ActivityCompat.shouldShowRequestPermissionRationale(RegisterActivity.this, permissionsRequired[3])) {
                //Show Information about why you need the permission
                android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(RegisterActivity.this);
                builder.setTitle("Need Multiple Permissions");
                builder.setMessage("This app needs Access fine location permissions.");
                builder.setPositiveButton("Grant", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                        ActivityCompat.requestPermissions(RegisterActivity.this, permissionsRequired, PERMISSION_CALLBACK_CONSTANT);
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
                android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(RegisterActivity.this);
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
                ActivityCompat.requestPermissions(RegisterActivity.this, permissionsRequired, PERMISSION_CALLBACK_CONSTANT);
            }
            SharedPreferences.Editor editor = permissionStatus.edit();
            editor.putBoolean(permissionsRequired[0], true);
            editor.commit();
        } else {
            //You already have the permission, just go ahead.
        }
    }
}
