package com.hubnx.app;

import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.content.SharedPreferences;
import android.content.Intent;
import android.os.Bundle;
import android.graphics.Color;
import android.net.Uri;
import android.text.InputType;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.Toast;
import androidx.webkit.WebViewAssetLoader;

public class MainActivity extends Activity {
    private static final String HOME = "https://appassets.androidplatform.net/assets/index.html";
    private static final int BG = Color.rgb(12, 17, 28);
    private static final int PANEL = Color.rgb(24, 36, 51);
    private static final int TEXT = Color.rgb(238, 247, 255);

    private UpdateChecker updater;
    private String orientationMode = "auto";
    private Button orientationToolbarButton;

    private final android.os.Handler checkHandler =
        new android.os.Handler(android.os.Looper.getMainLooper());
    private final Runnable periodicCheck = new Runnable() {
        @Override public void run() {
            if (updater != null) updater.check(false);
            checkHandler.postDelayed(this, 15L * 60 * 1000);
        }
    };
    private WebView library;
    private WebView browser;
    private WebViewAssetLoader assetLoader;
    private FrameLayout root;
    private LinearLayout browserShell;
    private EditText address;
    private View fullScreenView;
    private WebChromeClient.CustomViewCallback fullScreenCallback;

    @Override public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setStatusBarColor(BG);
        getWindow().setNavigationBarColor(BG);
        orientationMode = getSharedPreferences("hubnx_preferences", MODE_PRIVATE)
            .getString("screen_orientation", "auto");
        applyOrientation(orientationMode);

        assetLoader = new WebViewAssetLoader.Builder()
            .addPathHandler("/assets/", new WebViewAssetLoader.AssetsPathHandler(this))
            .build();

        updater = new UpdateChecker(this);
        root = new FrameLayout(this);
        root.setBackgroundColor(BG);
        library = new WebView(this);
        configureWebView(library);
        library.getSettings().setUserAgentString(
            library.getSettings().getUserAgentString() + " HubNXNative/0.2.6");
        library.setWebViewClient(new WebViewClient() {
            @Override public WebResourceResponse shouldInterceptRequest(WebView view, WebResourceRequest request) {
                return assetLoader.shouldInterceptRequest(request.getUrl());
            }
            @Override public void onPageFinished(WebView view, String url) {
                if (isLocalAsset(Uri.parse(url))) updateOrientationControls();
            }

            @Override public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                Uri uri = request.getUrl();
                if ("hubnx".equalsIgnoreCase(uri.getScheme()) &&
                    "orientation".equalsIgnoreCase(uri.getHost())) {
                    applyOrientation(uri.getQueryParameter("mode"));
                    return true;
                }
                if ("hubnx".equalsIgnoreCase(uri.getScheme()) &&
                    "updates".equalsIgnoreCase(uri.getHost())) {
                    updater.check(true);
                    return true;
                }
                if (isLocalAsset(uri)) return false;
                if (isWeb(uri)) openSite(uri.toString());
                else toast("Endereço não suportado no HubNX");
                return true;
            }
        });

        browserShell = new LinearLayout(this);
        browserShell.setOrientation(LinearLayout.VERTICAL);
        browserShell.setBackgroundColor(BG);

        LinearLayout toolbar = new LinearLayout(this);
        toolbar.setOrientation(LinearLayout.HORIZONTAL);
        toolbar.setGravity(Gravity.CENTER_VERTICAL);
        toolbar.setPadding(dp(4), dp(4), dp(4), dp(4));
        toolbar.setBackgroundColor(PANEL);

        Button home = button("⌂", toolbar);
        home.setContentDescription("Voltar à biblioteca");
        home.setOnClickListener(v -> returnToLibrary());

        Button back = button("‹", toolbar);
        back.setContentDescription("Voltar uma página");
        back.setOnClickListener(v -> {
            if (browser.canGoBack()) browser.goBack();
            else returnToLibrary();
        });

        Button forward = button("›", toolbar);
        forward.setContentDescription("Avançar uma página");
        forward.setOnClickListener(v -> { if (browser.canGoForward()) browser.goForward(); });

        Button reload = button("⟳", toolbar);
        reload.setContentDescription("Recarregar site");
        reload.setOnClickListener(v -> browser.reload());

        orientationToolbarButton = button("⤾", toolbar);
        orientationToolbarButton.setContentDescription("Alternar orientação da tela");
        orientationToolbarButton.setOnClickListener(v -> {
            String next = "auto".equals(orientationMode) ? "portrait" :
                ("portrait".equals(orientationMode) ? "landscape" : "auto");
            applyOrientation(next);
            toast("Orientação: " + orientationLabel());
        });

        address = new EditText(this);
        address.setSingleLine(true);
        address.setTextColor(TEXT);
        address.setTextSize(12);
        address.setSelectAllOnFocus(true);
        address.setHint("https://...");
        address.setHintTextColor(Color.LTGRAY);
        address.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_URI);
        toolbar.addView(address, new LinearLayout.LayoutParams(0, dp(46), 1f));
        address.setOnEditorActionListener((v, actionId, event) -> {
            navigateFromAddress();
            return true;
        });
        Button go = button("Ir", toolbar);
        go.setContentDescription("Abrir endereço");
        go.setOnClickListener(v -> navigateFromAddress());

        browser = new WebView(this);
        configureWebView(browser);
        // A lista local e os sites remotos ficam em WebViews SEPARADAS,
        // sem ponte JavaScript, acesso a arquivos ou credenciais compartilhadas.
        browser.getSettings().setMediaPlaybackRequiresUserGesture(false);
        browser.getSettings().setSupportMultipleWindows(false);
        browser.getSettings().setJavaScriptCanOpenWindowsAutomatically(false);
        browser.setWebViewClient(new WebViewClient() {
            @Override public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                if (isWeb(request.getUrl())) return false;
                toast("Link externo não suportado neste navegador");
                return true;
            }

            @Override public void onPageStarted(WebView view, String url, android.graphics.Bitmap favicon) {
                address.setText(url);
            }

            @Override public void onPageFinished(WebView view, String url) {
                address.setText(url);
            }
        });
        browser.setWebChromeClient(new WebChromeClient() {
            @Override public void onShowCustomView(View view, CustomViewCallback callback) {
                if (fullScreenView != null) {
                    callback.onCustomViewHidden();
                    return;
                }
                fullScreenView = view;
                fullScreenCallback = callback;
                browserShell.setVisibility(View.GONE);
                FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
                root.addView(fullScreenView, lp);
                getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
            }

            @Override public void onHideCustomView() {
                exitFullScreen();
            }
        });

        browserShell.addView(toolbar,
            new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, dp(56)));
        browserShell.addView(browser,
            new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 0, 1f));

        root.addView(library,
            new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        root.addView(browserShell,
            new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        browserShell.setVisibility(View.GONE);
        setContentView(root);
        library.loadUrl(HOME);
        UpdateWorker.schedule(this);
        if (android.os.Build.VERSION.SDK_INT >= 33 &&
            checkSelfPermission(android.Manifest.permission.POST_NOTIFICATIONS)
                != android.content.pm.PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{android.Manifest.permission.POST_NOTIFICATIONS}, 55);
        }
    }

    private String orientationLabel() {
        return "portrait".equals(orientationMode) ? "Retrato" :
            ("landscape".equals(orientationMode) ? "Paisagem" : "Automático");
    }

    private void updateOrientationControls() {
        if (orientationToolbarButton != null) {
            orientationToolbarButton.setContentDescription(
                "Orientação: " + orientationLabel() + ". Alternar");
        }
        if (library != null) {
            String js = "if(window.hubnxSetOrientation){window.hubnxSetOrientation(" +
                org.json.JSONObject.quote(orientationMode) + ");}";
            library.evaluateJavascript(js, null);
        }
    }

    private void applyOrientation(String mode) {
        if (!"portrait".equals(mode) && !"landscape".equals(mode)) mode = "auto";
        orientationMode = mode;
        getSharedPreferences("hubnx_preferences", MODE_PRIVATE).edit()
            .putString("screen_orientation", mode).apply();
        int choice = "portrait".equals(mode) ? ActivityInfo.SCREEN_ORIENTATION_PORTRAIT :
            ("landscape".equals(mode) ? ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE :
                ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);
        setRequestedOrientation(choice);
        updateOrientationControls();
    }

    private void configureWebView(WebView view) {
        view.setBackgroundColor(BG);
        WebSettings settings = view.getSettings();
        settings.setJavaScriptEnabled(true);
        settings.setDomStorageEnabled(true);
        settings.setAllowFileAccess(false);
        settings.setAllowContentAccess(false);
        settings.setMixedContentMode(WebSettings.MIXED_CONTENT_NEVER_ALLOW);
        settings.setSupportZoom(false);
    }

    private boolean isWeb(Uri uri) {
        String scheme = uri.getScheme();
        return ("https".equalsIgnoreCase(scheme) || "http".equalsIgnoreCase(scheme))
            && uri.getHost() != null && !uri.getHost().isEmpty();
    }

    private boolean isLocalAsset(Uri uri) {
        return "https".equalsIgnoreCase(uri.getScheme())
            && "appassets.androidplatform.net".equalsIgnoreCase(uri.getHost())
            && uri.getPath() != null && uri.getPath().startsWith("/assets/");
    }

    /** Opens the public update page inside HubNX, not the external browser. */
    public void openUpdatePage(String url) {
        if(url!=null && url.matches("https://github\\.com/Wilianjcr/HubNX/actions/runs/[0-9]+")) {
            openSite(url);
        }
    }

    private void openSite(String url) {
        Uri uri = Uri.parse(url);
        if (!isWeb(uri)) { toast("Endereço inválido"); return; }
        if ("http".equalsIgnoreCase(uri.getScheme())) {
            toast("Este site usa HTTP sem criptografia. Prefira HTTPS.");
        }
        browserShell.setVisibility(View.VISIBLE);
        library.setVisibility(View.GONE);
        address.setText(url);
        browser.loadUrl(url);
    }

    private void navigateFromAddress() {
        String value = address.getText().toString().trim();
        if (!value.contains("://")) value = "https://" + value;
        Uri uri = Uri.parse(value);
        if (!isWeb(uri) || uri.getUserInfo() != null) {
            toast("Informe um endereço HTTP ou HTTPS válido");
            return;
        }
        openSite(value);
        address.clearFocus();
    }

    private void returnToLibrary() {
        if (fullScreenView != null) exitFullScreen();
        browserShell.setVisibility(View.GONE);
        library.setVisibility(View.VISIBLE);
        browser.loadUrl("about:blank");
    }

    private void exitFullScreen() {
        if (fullScreenView == null) return;
        root.removeView(fullScreenView);
        fullScreenView = null;
        browserShell.setVisibility(View.VISIBLE);
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        WebChromeClient.CustomViewCallback callback = fullScreenCallback;
        fullScreenCallback = null;
        if (callback != null) callback.onCustomViewHidden();
    }

    private Button button(String label, LinearLayout parent) {
        Button b = new Button(this);
        b.setAllCaps(false);
        b.setText(label);
        b.setTextColor(TEXT);
        b.setTextSize(18);
        b.setBackgroundColor(Color.TRANSPARENT);
        b.setMinWidth(0);
        b.setMinimumWidth(0);
        b.setPadding(dp(2), 0, dp(2), 0);
        parent.addView(b, new LinearLayout.LayoutParams(dp(label.equals("Ir") ? 40 : 36), dp(45)));
        return b;
    }

    private int dp(int value) {
        return Math.round(value * getResources().getDisplayMetrics().density);
    }

    private void toast(String s) {
        Toast.makeText(this, s, Toast.LENGTH_SHORT).show();
    }

    @Override public void onBackPressed() {
        if (fullScreenView != null) exitFullScreen();
        else if (browserShell.getVisibility() == View.VISIBLE) {
            if (browser.canGoBack()) browser.goBack();
            else returnToLibrary();
        } else if (library.canGoBack()) library.goBack();
        else super.onBackPressed();
    }

    @Override protected void onResume() {
        super.onResume();
        if (updater != null) updater.check(false);
        checkHandler.removeCallbacks(periodicCheck);
        checkHandler.postDelayed(periodicCheck, 15L * 60 * 1000);
    }

    @Override protected void onPause() {
        checkHandler.removeCallbacks(periodicCheck);
        super.onPause();
    }

    @Override protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        if (intent != null && intent.getBooleanExtra("hubnx_check_update", false)
            && updater != null) updater.check(true);
    }

    @Override protected void onDestroy() {
        checkHandler.removeCallbacks(periodicCheck);
        if (fullScreenView != null) exitFullScreen();
        if (updater != null) updater.release();
        if (library != null) { library.destroy(); library = null; }
        if (browser != null) { browser.destroy(); browser = null; }
        super.onDestroy();
    }
}
