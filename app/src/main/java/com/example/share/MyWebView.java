package com.example.share;

import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.util.Log;
import android.webkit.WebView;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

public class MyWebView extends WebView {
    public static final String TAG = "MyWebView";

    public MyWebView(Context context) {
        super(context);
    }

    public MyWebView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MyWebView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }


    public void loadJs(String js) {
        //fabric上出现崩溃， 建议api>21. 同时在UI线程中执行
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP) {
            evaluateJavascript(js, null);
        } else {
            try {
                js = URLEncoder.encode(js, "UTF-8");
            } catch (UnsupportedEncodingException e) {
                Log.e(TAG, "load js encode error");
            }
            loadUrl(js);
        }
    }
}
