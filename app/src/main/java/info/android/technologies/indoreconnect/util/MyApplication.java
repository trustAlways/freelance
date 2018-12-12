package info.android.technologies.indoreconnect.util;

import android.app.Application;
import android.support.multidex.MultiDex;
import android.support.multidex.MultiDexApplication;

/**
 * Created by kamlesh on 11/9/2017.
 */
public class MyApplication extends MultiDexApplication {
    private static MyApplication mInstance;

    @Override
    public void onCreate() {
        super.onCreate();
        mInstance = this;
        MultiDex.install(this);
    }

    public static synchronized MyApplication getInstance() {
        return mInstance;
    }

    public void setConnectivityListener(ConnectionDetector.ConnectivityReceiverListener listener) {
        ConnectionDetector.connectivityReceiverListener = listener;
    }
}
