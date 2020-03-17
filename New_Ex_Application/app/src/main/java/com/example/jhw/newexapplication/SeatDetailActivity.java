package com.example.jhw.newexapplication;

import android.content.DialogInterface;
import android.content.res.Resources;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;

import static com.example.jhw.newexapplication.MainActivity.IP_ADDRESS;
import static com.example.jhw.newexapplication.MainActivity.openAPIKey;

public class SeatDetailActivity extends AppCompatActivity {

    static String traffic_num;
    static int block_num;

    private CustomProgressDialog customProgressDialog;
    private TextView nextStation;
    private TextView stopStation;
    private ImageView lineIcon;
    private TextView arvlMsgHeading;
    private TextView arvlMsg;
    private String tagName = "";
    private String btrainNo;
    private String arvlmsg;
    private String bstatnNm;
    private String mJsonString;
    private ImageView btnRealTimeRefresh;
    private Indicator indicatorView;

    private ArrayList<RealtimeStationArrivalInfo> realtimeStationArrivalInfoArrayList = new ArrayList();
    private BlockPagerAdapter mSectionsPagerAdapter;
    private ViewPager mViewPager;
    private ArrayList<BlockSeatInfo> mArrayList = new ArrayList();
    private AsyncTask mtask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_seat_detail);
        // 호출한 액티비티(BlockAdapter)로 부터 trafficNum와 BlockNum 키를 통해 선택된 차량 번호와 칸 번호를 전달 받아 저장
        if (this.getIntent() != null && this.getIntent().getStringExtra("trafficNum") != null) {
            this.traffic_num = this.getIntent().getStringExtra("trafficNum");
        }
        if (this.getIntent() != null && this.getIntent().getIntExtra("BlockNum",-1) != -1) {
            this.block_num = this.getIntent().getIntExtra("BlockNum",-1);
        }

        this.initView(); // 초기 세팅
        this.showCustomProgressDialog(); // 로딩 UI 보여주기
        mtask = (new ProcessNetworkSubwayRealtimeStationArrivalInfoThread()).execute(new String[]{TrafficSubwayInfoTypeB.stationNM});
    }

    @Override
    protected void onDestroy() { // 액티비티 종료시
        super.onDestroy();
        mtask.cancel(true); // 실행 중인 백그라운드 작업 끄기
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
    } // 로딩 끄기

    private void initView() { // 초기 세팅
        this.btnRealTimeRefresh = (ImageView) this.findViewById(R.id.btn_real_time_refresh); // 새로고침
        this.btnRealTimeRefresh.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {  // 새로고침 클릭 시 리스트 초기화 및 실시간 도착정보 받아오기
                SeatDetailActivity.this.mArrayList.clear();
                SeatDetailActivity.this.realtimeStationArrivalInfoArrayList.clear();
                SeatDetailActivity.this.showCustomProgressDialog();
                mtask = (SeatDetailActivity.this.new ProcessNetworkSubwayRealtimeStationArrivalInfoThread()).execute(new String[]{TrafficSubwayInfoTypeB.stationNM});
            }
        });
        // 레이아웃 받아오기
        this.indicatorView = (Indicator)findViewById(R.id.indicator);
        indicatorView.init(10, R.drawable.empty_icon_s, R.drawable.full_icon_s, 15); // Indicator의 개수와 아이콘 설정
        this.arvlMsgHeading = (TextView) this.findViewById(R.id.arvl_msg_heading);
        this.arvlMsg = (TextView) this.findViewById(R.id.arvl_msg);
        this.nextStation = (TextView) this.findViewById(R.id.n_station);
        this.stopStation = (TextView) this.findViewById(R.id.s_station_nm);
        this.lineIcon = (ImageView) this.findViewById(R.id.line_icon2);
        nextStation.setText(TrafficSubwayInfoTypeB.nStation);
        stopStation.setText(TrafficSubwayInfoTypeB.stationNM);
        lineIcon.setImageResource(SeatActivity.drawableId);
    } // 초기 세팅

    // 역 이름을 인자로 받고 공공데이터의 실시간 열차 도착 정보를 통해 실시간 열차 도착 정보를 추출
    public class ProcessNetworkSubwayRealtimeStationArrivalInfoThread extends AsyncTask<String, Void, String> {
        // 역 이름을 인자로 받고 공공데이터의 실시간 열차 도착 정보를 통해 실시간 열차 도착 정보를 추출

        StringBuilder sb = new StringBuilder();

        @Override
        protected String doInBackground(String... strings) {
            this.executeClient(strings);
            return "";
        }

        protected void onPostExecute(String result) {
            SeatDetailActivity.this.setRealtimeArrival();
            (SeatDetailActivity.this.new ProcessDatabaseSeatInfoThread()).execute();
        }

        public void executeClient(String[] strings) {
            HttpURLConnection urlConn = null;

            URL apiURL = null;
            InputStream in = null;
            XmlPullParserFactory factory = null;
            XmlPullParser xpp = null;
            boolean var6 = true;
            try {
                String station = strings[0];
                if (station.equals("서울역")) {
                    station = "서울";
                }
                if (station.equals("증산")) { // 서울역 -> 서울로 파싱
                    station = "증산(명지대앞)";
                }
                if (station.equals("공릉")) { // 서울역 -> 서울로 파싱
                    station = "공릉(서울산업대입구)";
                }
                if (station.equals("신촌(경의중앙선)")) {
                    station = "신촌(경의.중앙선)";
                }
                if (station.equals("천호")) {
                    station = "천호(풍납토성)";
                }
                if (station.equals("굽은다리")) {
                    station = "굽은다리(강동구민회관앞)";
                }
                if (station.equals("몽촌토성")) {
                    station = "몽촌토성(평화의문)";
                }
                apiURL = new URL("http://swopenapi.seoul.go.kr/api/subway/" + openAPIKey + "/xml/realtimeStationArrival/1/999/" + URLEncoder.encode(station, "UTF-8"));
                in = apiURL.openStream();
                urlConn = (HttpURLConnection) apiURL.openConnection();

                long start = System.currentTimeMillis();
                while(urlConn.getResponseCode() != HttpURLConnection.HTTP_OK) {
                    urlConn = (HttpURLConnection) apiURL.openConnection();
                    if(System.currentTimeMillis()-start>5000) {
                        break;
                    }
                }

                factory = XmlPullParserFactory.newInstance();
                factory.setNamespaceAware(true);
                xpp = factory.newPullParser();
                xpp.setInput(in, "UTF-8");
                int eventType = xpp.getEventType();

                for (boolean isItemTag = false; eventType != 1; eventType = xpp.next()) {
                    if (eventType == 2) {
                        SeatDetailActivity.this.tagName = xpp.getName();
                        if (SeatDetailActivity.this.tagName.equals("row")) {
                            isItemTag = true;
                        }
                    } else if (eventType == 4) {
                        if (isItemTag && !SeatDetailActivity.this.tagName.equals("") && !xpp.getText().equals("")) {
                            if (SeatDetailActivity.this.tagName.equals("btrainNo")) { //
                                SeatDetailActivity.this.btrainNo = xpp.getText();
                            } else if (SeatDetailActivity.this.tagName.equals("bstatnNm")) { //
                                SeatDetailActivity.this.bstatnNm = xpp.getText();
                            } else if (SeatDetailActivity.this.tagName.equals("arvlMsg2")) { // 도착 메세지
                                SeatDetailActivity.this.arvlmsg = xpp.getText();
                            }
                        }
                    } else if (eventType == 3) {
                        SeatDetailActivity.this.tagName = xpp.getName();
                        if (SeatDetailActivity.this.tagName.equals("row")) {
                            isItemTag = false;
                            Log.d("test","realtest" + bstatnNm + ", " + btrainNo + ", " + arvlmsg);
                            RealtimeStationArrivalInfo info = new RealtimeStationArrivalInfo(bstatnNm,btrainNo,arvlmsg);
                            SeatDetailActivity.this.realtimeStationArrivalInfoArrayList.add(info);
                            info = null;
                        } else {
                            SeatDetailActivity.this.tagName = "";
                        }
                    }
                }
            } catch (MalformedURLException var22) {
                var22.printStackTrace();
            } catch (IOException var23) {
                var23.printStackTrace();
                setResult(RESULT_CANCELED);
                finish();
            } catch (XmlPullParserException var24) {
                var24.printStackTrace();
            } finally {
                if (in != null) {
                    try {
                        in.close();
                    } catch (IOException var21) {
                        var21.printStackTrace();
                    }
                }
            }
        }


    }

    private void setRealtimeArrival() { // 실시간 열차 도착 정보를 통해 추출한 정보를 뷰에 적용

        this.arvlMsgHeading.setText("도착정보가 없습니다.");
        this.arvlMsg.setText("-");

        for (int i = 0; i < this.realtimeStationArrivalInfoArrayList.size(); ++i) {
            if (((RealtimeStationArrivalInfo) this.realtimeStationArrivalInfoArrayList.get(i)).getBtrainNo().equals(TrafficSubwayInfoTypeB.nbtrainNo)) {
                String arrivalMsg;
                arrivalMsg = ((RealtimeStationArrivalInfo) this.realtimeStationArrivalInfoArrayList.get(i)).getArvlMsg2().replaceAll("\\[", "").replaceAll("\\]", "");
                arrivalMsg = arrivalMsg.split("\\(")[0]; // 7분 후
                if (!arrivalMsg.contains("도착") && !arrivalMsg.contains("출발") && !arrivalMsg.contains("진입")) {
                    arrivalMsg = arrivalMsg.concat("도착");
                }
                this.arvlMsgHeading.setText(((RealtimeStationArrivalInfo) this.realtimeStationArrivalInfoArrayList.get(i)).getBstatnNm() + "행");
                this.arvlMsg.setText(arrivalMsg);
            }
        }
    } // 실시간 열차 도착 정보를 통해 추출한 정보를 뷰에 적용

    public class ProcessDatabaseSeatInfoThread extends AsyncTask<String, Void, String> { // 서버의 열차 칸별 테이블을 조회
        String errorString = null;

        protected void onPostExecute(String result) {
            mJsonString = result;
            showResult();
            SeatDetailActivity.this.cancelCustomProgressDialog();
        }

        @Override
        protected String doInBackground(String... strings) {
            return executeClient();
        }
        public String executeClient() {
            // 첫번째 차량 번호로 테이블을 찾아 조회
            String serverURL = "http://" + IP_ADDRESS + "/getjreader.php";
            String postParameters = "first_traffic_num=" + SeatActivity.first_traffic_num;

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
    } // 서버의 열차 칸별 테이블을 조회

    private void showResult() { // JSON TAG를 통해 값을 추출아여 BlockSeatInfo에 저장 및 세팅
        String TAG_JSON = "root";
        String TAG_TRAFFICNUM = "traffic_num";
        String TAG_READERID = "reader_id";
        String TAG_STATE = "state";
        String TAG_LOCATED = "located";


        try {
            JSONObject jsonObject = new JSONObject(mJsonString);
            JSONArray jsonArray = jsonObject.getJSONArray(TAG_JSON);

            for (int i = 0; i < jsonArray.length(); i++) {

                JSONObject item = jsonArray.getJSONObject(i);
                String traffic_num = item.getString(TAG_TRAFFICNUM);
                String reader_id = item.getString(TAG_READERID);
                String state = item.getString(TAG_STATE);
                String located = item.getString(TAG_LOCATED);

                BlockSeatInfo blockInfo = new BlockSeatInfo(traffic_num, reader_id, state, located);
                mArrayList.add(blockInfo);

            }

        } catch (JSONException e) {
        }

        // blockInfo들을 mSectionsPagerAdapter(BlockFragment의 정보를 가짐)에 담아 mViewPager에 연결
        mSectionsPagerAdapter = new BlockPagerAdapter(getSupportFragmentManager(),mArrayList);
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() { // 페이지 변화 시 이벤트 설정
            @Override
            public void onPageScrolled(int i, float v, int i1) {
            }

            @Override
            public void onPageSelected(int i) { // 선택 된 페이지 인덱스를 block_num에 저장하고 indicatorView에 전달
                block_num = i;
                indicatorView.setSelection(i);
            }

            @Override
            public void onPageScrollStateChanged(int i) {
            }
        });
        mViewPager.setAdapter(mSectionsPagerAdapter);
        mViewPager.setCurrentItem(block_num); // 현재 페이지 인덱스를 뷰페이지에 연결
    }

}
