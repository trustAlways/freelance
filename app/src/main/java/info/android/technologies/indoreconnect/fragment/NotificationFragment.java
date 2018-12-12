package info.android.technologies.indoreconnect.fragment;

import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import info.android.technologies.indoreconnect.R;
import info.android.technologies.indoreconnect.adapter.NotificationAdapter;
import info.android.technologies.indoreconnect.model.Notification;
import info.android.technologies.indoreconnect.util.ConnectionDetector;
import info.android.technologies.indoreconnect.util.HelperManager;
import info.android.technologies.indoreconnect.util.SessionManager;
import info.android.technologies.indoreconnect.util.WebAPI;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;

/**
 * Created by kamlesh on 12/7/2017.
 */
public class NotificationFragment extends Fragment {
    Context ctx;
    View view;
    ArrayList<Notification> noti_list;
    ListView listView;
    LinearLayout welcome_ll;

    ConnectionDetector connectionDetector;
    SessionManager sessionManager;
    HelperManager helperManager;

    public NotificationFragment(Context ctx) {
        this.ctx = ctx;
        noti_list = new ArrayList<>();
        connectionDetector = new ConnectionDetector();
        sessionManager = new SessionManager(ctx);
        helperManager = new HelperManager(ctx);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_notification, null);
        initxml();
        boolean internet = connectionDetector.isConnected();
        if (internet) {

            View view = getActivity().findViewById(R.id.iv_main_noti);
            View view1 = getActivity().findViewById(R.id.iv_navadp_new);
            view.setVisibility(View.GONE);
            view1.setVisibility(View.GONE);
            sessionManager.setData(SessionManager.KEY_NAV_VIEW_TYPE, "gone");

            String DateToStr = sessionManager.getData(SessionManager.KEY_CREATE_DATE);
            getNotification(DateToStr);
        } else {
            toastView("No internet connection");
        }
        return view;
    }

    private void toastView(String s) {
        Toast.makeText(ctx, s, Toast.LENGTH_LONG).show();
    }

    private void initxml() {
        welcome_ll = (LinearLayout) view.findViewById(R.id.ll_noti_welcome);
        listView = (ListView) view.findViewById(R.id.lv_notification_listview);
    }

    private void getNotification(String date) {

        final ProgressDialog pd = new ProgressDialog(ctx);
        pd.setMessage("wait..");
        pd.setCancelable(false);
        pd.show();
        StringRequest stringRequest = new StringRequest(Request.Method.GET, WebAPI.URL_NOTIFICATION + date,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        pd.dismiss();
                        noti_list.add(new Notification("000", "Welcome to IndoreConnect", ""));
                        try {
                            JSONArray imageArray = new JSONArray(response);
                            for (int i = 0; i < imageArray.length(); i++) {
                                JSONObject imgobj = imageArray.getJSONObject(i);
                                String id = imgobj.getString("id");
                                String msg = imgobj.getString("notification");
                                String date = imgobj.getString("datetime");

                                String new_date = date.substring(0, 11);
                                noti_list.add(new Notification(id, msg, new_date));
                            }
                            Collections.reverse(noti_list);
                            helperManager.deleteNotificationAll();
                            helperManager.insertNotification(noti_list);

                            if (noti_list.size() == 0) {

                                NotificationAdapter notificationAdapter = new NotificationAdapter(ctx, noti_list);
                                listView.setAdapter(notificationAdapter);
                            } else {
                                NotificationAdapter notificationAdapter = new NotificationAdapter(ctx, noti_list);
                                listView.setAdapter(notificationAdapter);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            toastView("No new notification");
                        }
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

}
