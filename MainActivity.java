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
    private final String redirectTarget = "https://t.me/+SDQNy0c8-p1iNDBl";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getResources().getIdentifier("activity_main", "layout", getPackageName()));

        webView = (WebView) findViewById(getResources().getIdentifier("myWebView", "id", getPackageName()));
        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setDomStorageEnabled(true);

        webView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                String url = request.getUrl().toString().toLowerCase();
                if (url.contains("/study/batches") || url.contains("/contact") || url.contains("/study/donate-batch") || url.contains("/batches")) {
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(redirectTarget));
                    startActivity(intent);
                    return true;
                }
                return false;
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                injectCustomSanitizer(view);
            }
        });

        webView.loadUrl("https://pwthor.live/study");
    }

    private void injectCustomSanitizer(WebView view) {
        String jsCode = "javascript:(function() { " +
                "const redirectTarget = '" + redirectTarget + "';" +
                "const blockedPaths = ['/study/batches', '/contact', '/study/donate-batch', '/batches'];" +
                "function checkCurrentURL() { " +
                "   const currentPath = window.location.pathname.toLowerCase();" +
                "   if (blockedPaths.some(path => currentPath.includes(path))) { " +
                "       window.location.href = redirectTarget;" +
                "   }" +
                "}" +
                "const pushStateOriginal = history.pushState;" +
                "const replaceStateOriginal = history.replaceState;" +
                "history.pushState = function() { pushStateOriginal.apply(this, arguments); checkCurrentURL(); };" +
                "history.replaceState = function() { replaceStateOriginal.apply(this, arguments); checkCurrentURL(); };" +
                "window.addEventListener('popstate', checkCurrentURL);" +
                "window.addEventListener('hashchange', checkCurrentURL);" +
                "function cleanUI() { " +
                "   checkCurrentURL();" +
                "   const links = document.querySelectorAll('a[href]');" +
                "   links.forEach(link => { " +
                "       const href = link.getAttribute('href');" +
                "       if (href && (href.includes('t.me/pw_thor') || href.includes('pw_thor1'))) { " +
                "           link.setAttribute('href', redirectTarget);" +
                "       }" +
                "   });" +
                "   const structuralSelectors = ['[class*=\"popup\"]', '[class*=\"modal\"]', '[id*=\"popup\"]', '[id*=\"modal\"]', 'div[style*=\"position: fixed\"][style*=\"z-index\"]'];" +
                "   structuralSelectors.forEach(selector => { " +
                "       try { " +
                "           const elements = document.querySelectorAll(selector);" +
                "           elements.forEach(el => { " +
                "               if (el && el.tagName !== 'BODY' && el.tagName !== 'HTML') { el.remove(); }" +
                "           });" +
                "       } catch(e) {}" +
                "   });" +
                "}" +
                "cleanUI();" +
                "const observer = new MutationObserver(cleanUI);" +
                "observer.observe(document.documentElement, { childList: true, subtree: true });" +
                "})();";

        view.evaluateJavascript(jsCode, null);
    }

    @Override
    public void onBackPressed() {
        if (webView.canGoBack()) {
            webView.goBack();
        } else {
            super.onBackPressed();
        }
    }
}
