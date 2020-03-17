package com.example.jhw.newexapplication;

import android.app.Activity;
import android.content.Intent;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;

public class WebViewInterfaceTypeB { // 웹뷰 관리 클래스

    private WebView mAppView;
    private Activity mContext;
    private static final int msg = 1;

    public WebViewInterfaceTypeB(Activity activity, WebView view) { // 웹뷰의 뷰와 액티비티의 context를 받아옴
        this.mAppView = view;
        this.mContext = activity;
    }

    @JavascriptInterface
    public void showSubwayInfo(String station) { // 자바스크립트에서 역 클릭시 역 이름을 인자로 실행
        Intent intent = new Intent(this.mContext, TrafficSubwayInfoTypeB.class);
        intent.putExtra("StationNM", station); // 역 이름 값을 StationNM을 키로 인텐트를 통해 다음 클래스에 전달
        this.mContext.startActivityForResult(intent,msg);
    }
}
