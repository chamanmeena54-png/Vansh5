package com.example.webwrapper;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class MainActivity extends Activity {

    private WebView webView;
    private final String targetTelegram = "https://t.me/+SDQNy0c8-p1iNDBl";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        webView = new WebView(this);
        setContentView(webView);

        WebSettings settings = webView.getSettings();
        settings.setJavaScriptEnabled(true);
        settings.setDomStorageEnabled(true);
        settings.setDatabaseEnabled(true);
        settings.setCacheMode(WebSettings.LOAD_DEFAULT);
        settings.setUserAgentString("Mozilla/5.0 (Linux; Android 10; K) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Mobile Safari/537.36");

        webView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                String url = request.getUrl().toString();
                String urlLower = url.toLowerCase();
                
                // 1. FORCE EXTERNAL OPEN: download.pwthor.live and custom Telegram invite link
                if (urlLower.contains("download.pwthor.live") || url.equals(targetTelegram)) {
                    try {
                        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                        return true;
                    } catch (Exception e) {
                        return false; 
                    }
                }

                // 2. STRICT URL BLOCKING: Redirects if specific paths are targeted
                if (urlLower.contains("/study/batches") || 
                    urlLower.contains("/contact") || 
                    urlLower.contains("/study/donate") || 
                    urlLower.contains("t.me/pw_thor") || 
                    urlLower.contains("t.me/pw_thor1")) {
                    
                    try {
                        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(targetTelegram));
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                        return true;
                    } catch (Exception e) {
                        return false;
                    }
                }
                return false;
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                if (!url.equals(targetTelegram)) {
                    executeInjectedSanitizer(view);
                }
            }
        });

        webView.loadUrl("https://pwthor.live/study");
    }

    private void executeInjectedSanitizer(WebView view) {
        String js = "javascript:(function() { " +
                "const targetTg = '" + targetTelegram + "';" +
                "const matches = ['/study/batches', '/contact', '/study/donate'];" +
                
                "function interceptRouter() { " +
                "   const path = window.location.pathname.toLowerCase();" +
                "   if (path !== '/' && path !== '/study' && matches.some(p => path === p || path === p + '/')) { " +
                "       window.location.href = targetTg;" +
                "   }" +
                "}" +
                
                "const push = history.pushState; const replace = history.replaceState;" +
                "history.pushState = function() { push.apply(this, arguments); interceptRouter(); };" +
                "history.replaceState = function() { replace.apply(this, arguments); interceptRouter(); };" +
                "window.addEventListener('popstate', interceptRouter);" +
                "window.addEventListener('hashchange', interceptRouter);" +

                "function sweepUI() { " +
                "   interceptRouter();" +
                "   document.querySelectorAll('a[href]').forEach(link => { " +
                "       const href = link.getAttribute('href');" +
                "       if (href && (href.includes('t.me/pw_thor') || href.includes('pw_thor1'))) { " +
                "           if (!href.includes('+SDQNy0c8')) { link.setAttribute('href', targetTg); }" +
                "       }" +
                "   });" +
                "}" +
                
                "sweepUI();" +
                "const obs = new MutationObserver(sweepUI);" +
                "obs.observe(document.documentElement, { childList: true, subtree: true });" +
                "})();";

        view.evaluateJavascript(js, null);
    }

    @Override
    public void onBackPressed() {
        if (webView != null && webView.canGoBack()) {
            webView.goBack();
        } else {
            super.onBackPressed();
        }
    }
                }
