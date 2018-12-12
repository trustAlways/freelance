package info.android.technologies.indoreconnect.activity;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;

import info.android.technologies.indoreconnect.R;

public class WebViewActivity extends AppCompatActivity implements View.OnClickListener {

    WebView webView;
    ImageView back_iv;
    Context ctx;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web_view);
        initxml();
        setData();
    }

    private void setData() {

        String url = getIntent().getStringExtra("link");

//        webView.getSettings().setJavaScriptEnabled(true);
//        webView.loadUrl(url);

        WebSettings webSettings = webView.getSettings();
        webSettings.setBuiltInZoomControls(true);

        webView.setWebViewClient(new Callback());  //HERE IS THE MAIN CHANGE
        webView.loadUrl(url);

        back_iv.setOnClickListener(this);
    }

    private void initxml() {
        ctx = this;
        webView = (WebView) findViewById(R.id.wv_web_activity);
        back_iv = (ImageView) findViewById(R.id.iv_web_back);
    }

    @Override
    public void onClick(View v) {

        finish();
    }

    private class Callback extends WebViewClient {  //HERE IS THE MAIN CHANGE.

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            return (false);
        }

    }
}
