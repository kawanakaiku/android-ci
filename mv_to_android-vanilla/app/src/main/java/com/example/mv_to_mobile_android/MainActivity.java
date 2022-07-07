package com.example.mv_to_mobile_android;

import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.view.View;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import androidx.appcompat.app.AppCompatActivity;
import androidx.webkit.WebViewAssetLoader;

import java.io.File;

public class MainActivity extends AppCompatActivity {
    private WebView webView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        int permission = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE);
        if (permission != PackageManager.PERMISSION_GRANTED) {
            // We don't have permission so prompt the user
            static final int REQUEST_CODE = 1;
            ActivityCompat.requestPermissions(this, new String[]{
                    READ_EXTERNAL_STORAGE,
            }, REQUEST_CODE);
        }

        hideNavigationBar();

        webView = (WebView) findViewById(R.id.webView);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setDomStorageEnabled(true);
        webView.getSettings().setAllowUniversalAccessFromFileURLs(true);

        ScriptHandler scriptHandler = new ScriptHandler();
        Handler handler = new Handler();
        scriptHandler.setActivity(MainActivity.this);
        scriptHandler.setHandler(handler);
        scriptHandler.setWebView(webView);
        webView.addJavascriptInterface(scriptHandler, "MVZxAndroidHandlers");
        
        File publicDir = new File(Environment.getExternalStorageDirectory(), "public");

        final WebViewAssetLoader assetLoader = new WebViewAssetLoader.Builder()
                .addPathHandler("/htmlSource/", new WebViewAssetLoader.AssetsPathHandler(this))
                .addPathHandler("/assets/", new WebViewAssetLoader.AssetsPathHandler(this))
                .addPathHandler("/res/", new WebViewAssetLoader.ResourcesPathHandler(this))
                .addPathHandler("/public/", new WebViewAssetLoader.InternalStoragePathHandler(this, publicDir))
                .build();
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public WebResourceResponse shouldInterceptRequest(WebView view, WebResourceRequest request) {
                return assetLoader.shouldInterceptRequest(request.getUrl());
            }
        });

        String path = "https://appassets.androidplatform.net/htmlSource/index.html";
        this.webView.loadUrl(path);
    }

    @Override
    protected void onResume() {
        super.onResume();
        hideNavigationBar();
    }

    private void hideNavigationBar() {
        // Hide both the navigation bar and the status bar.
        View decoView = getWindow().getDecorView();
        int uiOptions = View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY | View.SYSTEM_UI_FLAG_IMMERSIVE | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_FULLSCREEN;
        decoView.setSystemUiVisibility(uiOptions);
    }
}
