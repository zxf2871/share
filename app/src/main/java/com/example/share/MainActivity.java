package com.example.share;

import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.net.http.SslError;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.webkit.JavascriptInterface;
import android.webkit.JsPromptResult;
import android.webkit.JsResult;
import android.webkit.SslErrorHandler;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    private MyWebView mWebView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mWebView = findViewById(R.id.webview);
        initWebView();
        initSetting();
        initChromeClient();
        initWebViewClient();
        start();
    }

    private void initWebView() {
        mWebView.addJavascriptInterface(new JS(), "nameSpace");

    }


    private void initSetting() {
        //声明WebSettings子类
        WebSettings webSettings = mWebView.getSettings();
        //如果访问的页面中要与Javascript交互，则webview必须设置支持Javascript
        webSettings.setJavaScriptEnabled(true);
        //设置自适应屏幕，两者合用
        webSettings.setUseWideViewPort(true); //将图片调整到适合webview的大小
        webSettings.setLoadWithOverviewMode(true); // 缩放至屏幕的大小
        //缩放操作
        webSettings.setSupportZoom(true); //支持缩放，默认为true。是下面那个的前提。
        webSettings.setBuiltInZoomControls(true); //设置内置的缩放控件。若为false，则该WebView不可缩放
        webSettings.setDisplayZoomControls(false); //隐藏原生的缩放控件
        //其他细节操作
        webSettings.setAllowFileAccess(true); //设置可以访问文件
        webSettings.setJavaScriptCanOpenWindowsAutomatically(true); //支持通过JS打开新窗口
        webSettings.setLoadsImagesAutomatically(true); //支持自动加载图片
        webSettings.setDefaultTextEncodingName("utf-8");//设置编码格式



        //缓存模式如下：
        //LOAD_CACHE_ONLY: 不使用网络，只读取本地缓存数据
        //LOAD_DEFAULT: （默认）根据cache-control决定是否从网络上取数据。
        //LOAD_NO_CACHE: 不使用缓存，只从网络获取数据.
        //LOAD_CACHE_ELSE_NETWORK，只要本地有，无论是否过期，或者no-cache，都使用缓存中的数据。
        //优先使用缓存:
        webSettings.setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);
        //不使用缓存:
//        WebView.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);
        String cacheDirPath = this.getFilesDir().getAbsolutePath()+"cache/";
        // 1. 设置缓存路径
        webSettings.setAppCachePath(cacheDirPath);
        // 2. 设置缓存大小
        webSettings.setAppCacheMaxSize(20*1024*1024);
        // 3. 开启Application Cache存储机制
        webSettings.setAppCacheEnabled(true);
        //特别注意
        // 每个 Application 只调用一次 WebSettings.setAppCachePath() 和


        // 通过设置 `WebView`的`Settings`类实现
        // 开启DOM storage
        webSettings.setDomStorageEnabled(true);

    }

    private void initChromeClient() {
        mWebView.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                super.onProgressChanged(view, newProgress);
            }

            //获取网页中的title
            @Override
            public void onReceivedTitle(WebView view, String title) {
                //注意 * android 6.0以下通过判断title中是否为404，500等错误码判断错误信息
            }

            //监听alert框
            @Override
            public boolean onJsAlert(WebView view, String url, String message, final JsResult result) {
                AlertDialog.Builder b = new AlertDialog.Builder(MainActivity.this);
                b.setTitle("Alert");
                b.setMessage(message);
                b.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        result.confirm();
                    }
                });
                b.setCancelable(false);
                b.create().show();
                return true;
            }

            //监听confirm框
            @Override
            public boolean onJsConfirm(WebView view, String url, String message, JsResult result) {
                return super.onJsConfirm(view, url, message, result);
            }

            //监听Prompt
            @Override
            public boolean onJsPrompt(WebView view, String url, String message, String defaultValue, JsPromptResult result) {
                return super.onJsPrompt(view, url, message, defaultValue, result);
            }
        });
    }

    private void initWebViewClient() {
        mWebView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                return super.shouldOverrideUrlLoading(view, request);
            }

            //在页面加载时调用，页面加载前可以设定一个native的loading
            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                //设定加载开始的操作
            }

            //在页面加载结束时调用。我们可以关闭loading，切换程序动作
            @Override
            public void onPageFinished(WebView view, String url) {
                //设定加载结束的操作
            }

            //加载页面的服务器出现错误时（如404, 500, 等）调用
            @Override
            public void onReceivedError(WebView view, int errorCode
                    , String description, String failingUrl) {
                //处理错误
                //注意 android 6.0的错误需要在WebChromeClient类的onReceivedTitle方法通过判断title获取
            }

            //处理https请求证书错误处理
            @Override
            public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
                handler.proceed();    //表示等待证书响应
                // handler.cancel();      //表示挂起连接，为默认方式
                // handler.handleMessage(null);    //可做其他处理
            }


            // API 21 以下用shouldInterceptRequest(WebView view, String url)
            @Override
            public WebResourceResponse shouldInterceptRequest(WebView view, String url) {
                //1 判断url文件类型：html, css, js, png
                //2 根据url找到本地对应文件
                //3 将本地文件转成 InputStream 输入流
                //4 生成 WebResourceResponse 替换方法中的

                //几种常见的content-type:
                //js: "application/x-javascript";
                //css: "text/css";
                //html: "text/html";
                //png: "image/png";

//                if(使用自建缓存){
//                    WebResourceResponse response = new WebResourceResponse("",
//                            "utf-8", is);
//                    return response;
//                }
                return super.shouldInterceptRequest(view, url);
            }

            //API 21以上使用
            @Override
            public WebResourceResponse shouldInterceptRequest(WebView view, WebResourceRequest request) {
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
                    String url = request.getUrl().toString();
                }
//                ...
                return super.shouldInterceptRequest(view, request);
            }

        });

    }


    private void start() {
        mWebView.loadUrl("file:///android_asset/index.html");
    }

    public class JS {

        @JavascriptInterface
        public void callAndroidMethod(String message) {
            Toast.makeText(MainActivity.this, message, Toast.LENGTH_SHORT).show();
        }
    }
}
