package org.techconnect.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.webkit.WebView;

import org.techconnect.R;

import butterknife.Bind;
import butterknife.ButterKnife;

public class WebViewActivity extends AppCompatActivity {

    public static final String EXTRA_FILE = "file";
    public static final String EXTRA_URI = "uri";
    public static final String EXTRA_IS_FILE = "isFile";
    private static final String TAG = "WebViewActivity";
    @Bind(R.id.webview)
    WebView webView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web_view);
        ButterKnife.bind(this);

        if (getIntent() != null && getIntent().hasExtra(EXTRA_IS_FILE)) {
            if (getIntent().getBooleanExtra(EXTRA_IS_FILE, false)) {
                String file = getIntent().getStringExtra(EXTRA_FILE);
                Log.d(TAG, "Loading file " + file);
                webView.loadUrl("file://" + file);
            } else {
                String uri = getIntent().getStringExtra(EXTRA_URI);
                Log.d(TAG, "Loading URI " + uri);
                webView.loadUrl("uri");
            }
        }
    }
}
