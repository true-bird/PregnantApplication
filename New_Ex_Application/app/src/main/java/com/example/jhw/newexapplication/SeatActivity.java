package com.example.jhw.newexapplication;

import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

import static com.example.jhw.newexapplication.MainActivity.IP_ADDRESS;

public class SeatActivity extends AppCompatActivity {
    private static String TAG = "test";
    private String lineId = "";
    private String lineName;
    private RecyclerView mRecyclerView;
    private ArrayList<TrainBlockInfo> mArrayList;
    private BlockAdapter mAdapter;
    private CustomProgressDialog customProgressDialog;
    private String mJsonString;

    static String first_traffic_num;
    static int drawableId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_seat);
        // 호출한 액티비티(TrafficSubwayInfoTypeB)로 부터 select_line과 first_traffic_num 키를 통해 선택된 호선과 첫 번째 차량 번호를 전달 받아 저장
        if (this.getIntent() != null && this.getIntent().getStringExtra("select_line") != null) {
            this.lineId = this.getIntent().getStringExtra("select_line");
        }
        if (this.getIntent() != null && this.getIntent().getStringExtra("first_traffic_num") != null) {
            this.first_traffic_num = this.getIntent().getStringExtra("first_traffic_num");
        }

        this.initView(); // 초기 세팅
        this.showCustomProgressDialog(); // 로딩 UI 보여주기
        (new SeatActivity.ProcessDatabaseSeatInfoThread()).execute("http://" + IP_ADDRESS + "/getseat.php", this.first_traffic_num,this.lineName);
        // ProcessDatabaseSeatInfoThread에 접속할 서버 php와 첫 번째 차량 번호, 호선 이름 인자로 실행
    }

    private void showCustomProgressDialog() { // 로딩 하기
        if (this.customProgressDialog == null) {
            this.customProgressDialog = new CustomProgressDialog(this);
            this.customProgressDialog.setCancelable(false); // 백버튼 막기
            this.customProgressDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT)); // 배경 투명
        }
        if (this.customProgressDialog != null) {
            this.customProgressDialog.show();
        }
    } // 로딩 UI 보여주기
    private void cancelCustomProgressDialog() { // 로딩 끄기
        if (this.customProgressDialog != null && this.customProgressDialog.isShowing()) {
            this.customProgressDialog.cancel();
        }
    } // 로딩 UI 끄기

    private void initView() { // 초기 세팅
        // 선택된 호선 id를 통해 호선 이름과 아이콘 이미지 구분
        switch (lineId) {
            case "1001":
                lineName = "line1";
                drawableId = R.drawable.full_1_2;
                break;
            case "1002":
                lineName = "line2";
                drawableId = R.drawable.full_2_2;
                break;
            case "1003":
                lineName = "line3";
                drawableId = R.drawable.full_3_2;
                break;
            case "1004":
                lineName = "line4";
                drawableId = R.drawable.full_4_2;
                break;
            case "1005":
                lineName = "line5";
                drawableId = R.drawable.full_5_2;
                break;
            case "1006":
                lineName = "line6";
                drawableId = R.drawable.full_6_2;
                break;
            case "1007":
                lineName = "line7";
                drawableId = R.drawable.full_7_2;
                break;
            case "1008":
                lineName = "line8";
                drawableId = R.drawable.full_8_2;
                break;
            case "1009":
                lineName = "line9";
                drawableId = R.drawable.full_9_2;
                break;
            case "1063":
                lineName = "kyeong";
                drawableId = R.drawable.full_kyeong_2;
                break;
            case "1065":
                lineName = "konghang";
                drawableId = R.drawable.full_konghang_2;
                break;
            case "1067":
                lineName = "kyeongchun";
                drawableId = R.drawable.full_kyeongchun_2;
                break;
            case "1069":
                lineName = "incheon";
                drawableId = R.drawable.full_incheon_2;
                break;
            case "1071":
                lineName = "sooin";
                drawableId = R.drawable.full_sooin_2;
                break;
            case "1075":
                lineName = "bundang";
                drawableId = R.drawable.full_bundang_2;
                break;
            case "1077":
                lineName = "sinbundang";
                drawableId = R.drawable.full_sinbundang_2;
        }

        // 뷰를 받아 오고 TrainBlockInfo를 BlockAdapter를 통해 연결하여 mRecyclerView에 출력
        mRecyclerView = (RecyclerView) findViewById(R.id.listView_main_list);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mArrayList = new ArrayList<>();
        mAdapter = new BlockAdapter(this, mArrayList,IP_ADDRESS);
        mRecyclerView.setAdapter(mAdapter);
        mAdapter.notifyDataSetChanged();
    }

    public class ProcessDatabaseSeatInfoThread extends AsyncTask<String, Void, String> { // 서버의 호선 별 열차 테이블을 조회

        String errorString = null;

        @Override
        protected String doInBackground(String... strings) {
            return this.executeClient(strings);
        }

        protected void onPostExecute(String result) {
            mJsonString = result;
            showResult(); // JSON TAG를 통해 값을 추출아여 TrainInfo에 저장 및 세팅
            SeatActivity.this.cancelCustomProgressDialog(); // 로딩 UI 끄기
        }

        public String executeClient(String[] strings) {
            // 호선 이름으로 테이블을 찾고 첫 번째 차량 번호로 열차를 찾아 조회
            String serverURL = strings[0];
            String postParameters = "first_traffic_num=" + strings[1]+ "&" + "line_num=" + strings[2];

            try {

                URL url = new URL(serverURL);
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();

                httpURLConnection.setReadTimeout(5000);
                httpURLConnection.setConnectTimeout(5000);
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setDoInput(true);
                httpURLConnection.connect();

                OutputStream outputStream = httpURLConnection.getOutputStream();
                outputStream.write(postParameters.getBytes("UTF-8"));
                outputStream.flush();
                outputStream.close();

                int responseStatusCode = httpURLConnection.getResponseCode();

                InputStream inputStream;
                if (responseStatusCode == HttpURLConnection.HTTP_OK) {
                    inputStream = httpURLConnection.getInputStream();
                } else {
                    inputStream = httpURLConnection.getErrorStream();
                }

                InputStreamReader inputStreamReader = new InputStreamReader(inputStream, "UTF-8");
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

                StringBuilder sb = new StringBuilder();
                String line;

                while ((line = bufferedReader.readLine()) != null) {
                    sb.append(line);
                }
                bufferedReader.close();
                return sb.toString().trim();
            } catch (Exception e) {
                errorString = e.toString();
                return null;
            }
        }

    }

    private void showResult() { // JSON TAG를 통해 값을 추출아여 TrainBlockInfo에 저장 및 세팅

        String TAG_JSON = "root";
        String TAG_TRAFFICNUM = "traffic_num";
        String TAG_EMPTYSEAT = "empty_seat";
        String TAG_TOTALSEAT = "total_seat";


        try {
            JSONObject jsonObject = new JSONObject(mJsonString);
            JSONArray jsonArray = jsonObject.getJSONArray(TAG_JSON);

            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject item = jsonArray.getJSONObject(i);
                String traffic_num = item.getString(TAG_TRAFFICNUM);
                String empty_seat = item.getString(TAG_EMPTYSEAT);
                String total_seat = item.getString(TAG_TOTALSEAT);

                TrainBlockInfo blockInfo = new TrainBlockInfo(traffic_num, empty_seat, total_seat);

                mArrayList.add(blockInfo);
                mAdapter.notifyDataSetChanged();
            }
        } catch (JSONException e) {
        }
    } // JSON TAG를 통해 값을 추출아여 TrainBlockInfo에 저장 및 세팅

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {// 다음 액티비티를 호출 했지만 서버에 제대로 접속 하지 못한 경우
        if(resultCode == RESULT_CANCELED) { // 액티비티 호출 후 결과 코드가 RESULT_CANCELED 일 경우
            Toast.makeText(getApplicationContext(),"서버접속이 불안정합니다.",Toast.LENGTH_LONG).show();
        }
    }
}
