package com.example.jhw.newexapplication;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import static com.example.jhw.newexapplication.MainActivity.IP_ADDRESS;
import static com.example.jhw.newexapplication.MainActivity.openAPIKey;

public class SubActivity extends AppCompatActivity {

    private ImageView btnBackSubway;
    private WebView lineMapWebview;
    private WebViewInterfaceTypeB mWebViewInterface;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sub);
        this.initView();
    }

    private void initView() { // 초기 세팅
        this.btnBackSubway = (ImageView)this.findViewById(R.id.btn_back_home); // 백버튼
        this.btnBackSubway.setOnClickListener(new View.OnClickListener() { // 백버튼 클릭시 이벤트
            public void onClick(View view) {
                SubActivity.this.finish(); // 현재 액티비티 종료
            }
        });
        this.lineMapWebview = (WebView)this.findViewById(R.id.line_map_webview);
        this.lineMapWebview.setWebViewClient(new WebViewClient()); // 내부 웹뷰 클라이언트 (디폴트 : 외부 웹브라우져 )
        this.lineMapWebview.getSettings().setJavaScriptEnabled(true);
        this.lineMapWebview.getSettings().setBuiltInZoomControls(true);
        this.lineMapWebview.getSettings().setSupportZoom(true);
        this.lineMapWebview.getSettings().setDisplayZoomControls(false);
        this.lineMapWebview.getSettings().setDefaultTextEncodingName("UTF-8");
        this.mWebViewInterface = new WebViewInterfaceTypeB(this, this.lineMapWebview);
        this.lineMapWebview.addJavascriptInterface(this.mWebViewInterface, "Android"); // 자바스크립트에서 Android라는 이름으로 mWebViewInterface에 접근 가능
        this.lineMapWebview.loadUrl("file:///android_asset/mSeoul_Subway.html"); // 지도맵 html로딩(자바스크립트 포함)
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) { // 다음 액티비티를 호출 했지만 서버에 제대로 접속 하지 못한 경우
        if(resultCode == RESULT_CANCELED) { // 액티비티 호출 후 결과 코드가 RESULT_CANCELED 일 경우
            Toast.makeText(getApplicationContext(),"서버접속이 불안정합니다.",Toast.LENGTH_LONG).show();
        }
    }
}
