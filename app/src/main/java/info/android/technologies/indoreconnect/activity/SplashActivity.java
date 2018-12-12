package info.android.technologies.indoreconnect.activity;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class SplashActivity extends AppCompatActivity {

    Context ctx;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(info.android.technologies.indoreconnect.R.layout.activity_splash);
        ctx = this;
        Thread thread = new Thread() {

            @Override
            public void run() {
                super.run();
                try {
                    sleep(3000);



                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                startActivity(new Intent(ctx, RegisterActivity.class));
                finish();
            }
        };
        thread.start();
    }
}
