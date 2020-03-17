package com.example.jhw.newexapplication;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

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
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.NetworkInterface;
import java.net.Socket;
import java.net.SocketException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Random;

import static com.example.jhw.newexapplication.MainActivity.IP_ADDRESS;
import static com.example.jhw.newexapplication.MainActivity.openAPIKey;

public class TrafficSubwayInfoTypeB extends AppCompatActivity {


    private String TAG = "test";

    private ImageView btnBackSubway;
    private TextView stationNm;
    private LinearLayout lineGroupLayout;
    private String selectedSubwayId = "";
    private ImageView btnRealTimeRefresh;
    private TextView prevStation;
    private TextView stopStation;
    private TextView nextStation;
    private TextView upLineArvl1Msg;
    private TextView upLineArvl2Msg;
    private TextView downLineArvl1Msg;
    private TextView downLineArvl2Msg;
    private TextView upLineArvl1MsgHeading;
    private TextView upLineArvl2MsgHeading;
    private TextView downLineArvl1MsgHeading;
    private TextView downLineArvl2MsgHeading;
    private TextView upLine1Seat;
    private TextView upLine2Seat;
    private TextView downLine1Seat;
    private TextView downLine2Seat;
    private String up1trainNo="";
    private String up2trainNo="";
    private String down1trainNo="";
    private String down2trainNo="";
    private String up1barvlDt;;
    private String up2barvlDt;;
    private String down1barvlDt;;
    private String down2barvlDt;
    ;

    private String tagName = "";
    private String statnFnm;
    private String statnTnm;
    private HashMap<String, StationInfo> stationInfoHashMap = new HashMap(); // Key와 value를 묶어 하나의 entry로 저장
    private String rowNum;
    private String trainLineNm;
    private String barvlDt;
    private String bstatnNm;
    private String btrainNo;
    private ArrayList<String> btrainNos;
    private String arvlMsg2;
    private ArrayList<RealtimeStationArrivalInfo> realtimeStationArrivalInfoArrayList;
    private String subwayId;
    private String subwayNm;
    private String updnLine;
    private ArrayList<String> lineArrayList = new ArrayList();
    private HashSet<String> lineHashSet = new HashSet();
    private CustomProgressDialog customProgressDialog=null;
    private String mJsonString;
    private ArrayList<TrainInfo> mArrayList;
    private RelativeLayout upLineTrain1;
    private RelativeLayout upLineTrain2;
    private RelativeLayout downLineTrain1;
    private RelativeLayout downLineTrain2;
    private AsyncTask mtask;




    static String nStation;
    static String stationNM = "";

    static String nArvlMsgHeading;
    static String nArvlMsg;
    static String nbtrainNo;
    static String nbarvlDt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_traffic_subway_info_type_b);

        // 호출한 액티비티(WebViewInterfaceTypeB)로 부터 StationNM키를 통해 역이름을 전달 받아 저장
        if (this.getIntent() != null && this.getIntent().getStringExtra("StationNM") != null) {
            this.stationNM = this.getIntent().getStringExtra("StationNM");
        }

        this.initView(); // 초기 세팅
        this.showCustomProgressDialog(); // 로딩 UI 보여주기
        mtask = (new TrafficSubwayInfoTypeB.ProcessNetworkSubwayStationInfoThread()).execute(new String[]{this.stationNM});
    }

    @Override
    protected void onDestroy() { // 액티비티 종료시
        customProgressDialog.dismiss(); // 로딩 UI 끄기
        mtask.cancel(true); // 실행 중인 백그라운드 작업 끄기
        super.onDestroy();
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
        View.OnClickListener onClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) { // 열차를 클릭시 이벤트
                Intent intent = new Intent(TrafficSubwayInfoTypeB.this,SeatActivity.class);
                intent.putExtra("select_line", selectedSubwayId);
                Boolean flag = false; // 열차 존재성 판단
                switch (v.getId()) { // 선택한 열차의 정보를 받아와 저장
                    case R.id.up_line_arvl_train :
                        if(mArrayList.size()>0) {
                            intent.putExtra("first_traffic_num", mArrayList.get(0).getFirst_traffic_num());
                            nStation = (String) prevStation.getText();
                            nArvlMsgHeading = (String) upLineArvl1MsgHeading.getText();
                            nArvlMsg = (String) upLineArvl1Msg.getText();
                            nbtrainNo = up1trainNo;
                            nbarvlDt = up1barvlDt;
                            flag = true;
                        }
                        break ;

                    case R.id.up_line_arvl_2_train :
                        if(mArrayList.size()>2) {
                            intent.putExtra("first_traffic_num", mArrayList.get(2).getFirst_traffic_num());
                            nStation = (String) prevStation.getText();
                            nArvlMsgHeading = (String) upLineArvl2MsgHeading.getText();
                            nArvlMsg = (String) upLineArvl2Msg.getText();
                            nbtrainNo = up2trainNo;
                            nbarvlDt = up2barvlDt;
                            flag = true;
                        }
                        break ;
                    case R.id.down_line_arvl_train :
                        if(mArrayList.size()>1) {
                            intent.putExtra("first_traffic_num", mArrayList.get(1).getFirst_traffic_num());
                            nStation = (String) nextStation.getText();
                            nArvlMsgHeading = (String) downLineArvl1MsgHeading.getText();
                            nArvlMsg = (String) downLineArvl1Msg.getText();
                            nbtrainNo = down1trainNo;
                            nbarvlDt = down1barvlDt;
                            flag = true;
                        }
                        break ;
                    case R.id.down_line_arvl_2_train :
                        if(mArrayList.size()>3) {
                            intent.putExtra("first_traffic_num", mArrayList.get(3).getFirst_traffic_num());
                            nStation = (String) nextStation.getText();
                            nArvlMsgHeading = (String) downLineArvl2MsgHeading.getText();
                            nArvlMsg = (String) downLineArvl2Msg.getText();
                            nbtrainNo = down2trainNo;
                            nbarvlDt = down2barvlDt;
                            flag = true;
                        }
                        break ;
                }
                if(flag) { // 열차가 존재 한다면
                    TrafficSubwayInfoTypeB.this.startActivity(intent);
                }
            }
        };
        this.btnBackSubway = (ImageView) this.findViewById(R.id.btn_back_subway); // 백버튼
        this.btnBackSubway.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) { // 백버튼 클릭 시
                customProgressDialog.dismiss(); // 로딩 UI 끄기
                finish(); // 액티비티 종료
            }
        });
        // 레이아웃 받아오기
        this.stationNm = (TextView) this.findViewById(R.id.station_nm); // 역 명
        this.stationNm.setText(this.stationNM);
        this.lineGroupLayout = (LinearLayout) this.findViewById(R.id.line_group_layout); // 호선 그룹
        this.upLineTrain1 = (RelativeLayout) this.findViewById(R.id.up_line_arvl_train); // 상행선
        upLineTrain1.setOnClickListener(onClickListener);
        this.upLineTrain2 = (RelativeLayout) this.findViewById(R.id.up_line_arvl_2_train);
        upLineTrain2.setOnClickListener(onClickListener);
        this.downLineTrain1 = (RelativeLayout) this.findViewById(R.id.down_line_arvl_train); // 하행선
        downLineTrain1.setOnClickListener(onClickListener);
        this.downLineTrain2 = (RelativeLayout) this.findViewById(R.id.down_line_arvl_2_train);
        downLineTrain2.setOnClickListener(onClickListener);
        this.btnRealTimeRefresh = (ImageView) this.findViewById(R.id.btn_real_time_refresh); // 새로고침
        this.btnRealTimeRefresh.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) { // 새로고침 클릭 시 리스트 초기화 및 실시간 도착정보 받아오기
                TrafficSubwayInfoTypeB.this.mArrayList.clear();
                TrafficSubwayInfoTypeB.this.btrainNos.clear();
                TrafficSubwayInfoTypeB.this.realtimeStationArrivalInfoArrayList.clear();
                TrafficSubwayInfoTypeB.this.showCustomProgressDialog();
                (TrafficSubwayInfoTypeB.this.new ProcessNetworkSubwayRealtimeStationArrivalInfoThread()).execute(new String[]{stationNM});
            }
        });
        this.prevStation = (TextView) this.findViewById(R.id.prev_station); // 이전 역
        this.stopStation = (TextView) this.findViewById(R.id.stop_station_nm); // 정차 역
        this.nextStation = (TextView) this.findViewById(R.id.next_station); // 다음 역
        this.stopStation.setText(this.stationNM);
        this.upLineArvl1Msg = (TextView) this.findViewById(R.id.up_line_arvl_1_msg); // 몇분 후 도착 or ~ 진입
        this.upLineArvl2Msg = (TextView) this.findViewById(R.id.up_line_arvl_2_msg); //
        this.downLineArvl1Msg = (TextView) this.findViewById(R.id.down_line_arvl_1_msg);
        this.downLineArvl2Msg = (TextView) this.findViewById(R.id.down_line_arvl_2_msg);
        this.upLineArvl1MsgHeading = (TextView) this.findViewById(R.id.up_line_arvl_1_msg_heading); // ~행
        this.upLineArvl2MsgHeading = (TextView) this.findViewById(R.id.up_line_arvl_2_msg_heading);
        this.downLineArvl1MsgHeading = (TextView) this.findViewById(R.id.down_line_arvl_1_msg_heading);
        this.downLineArvl2MsgHeading = (TextView) this.findViewById(R.id.down_line_arvl_2_msg_heading);
        this.upLine1Seat = (TextView) this.findViewById(R.id.up_line_seat); // 좌석 수
        this.upLine2Seat = (TextView) this.findViewById(R.id.up_line_2_seat);
        this.downLine1Seat = (TextView) this.findViewById(R.id.down_line_seat);
        this.downLine2Seat = (TextView) this.findViewById(R.id.down_line_2_seat);

        this.mArrayList = new ArrayList<>(); // 열차 정보 리스트 ( TrainInfo )
        this.btrainNos = new ArrayList<>(); // 열차 번호 리스트
        this.realtimeStationArrivalInfoArrayList = new ArrayList<>(); // 실시간 열차 도착 정보 리스트 ( RealtimeStationArrivalInfo )
    }

    // Info -> Arrival -> setLine -> setRealtime -> Database -> showResult
    public class ProcessNetworkSubwayStationInfoThread extends AsyncTask<String, Void, String> { // 공공데이터의 역정보를 통해 지하철 id, 이전 역, 다음 역 추출

        @Override
        protected String doInBackground(String... strings) {
            this.executeClient(strings);
            return "";
        }

        protected void onPostExecute(String result) { // 끝나고 ProcessNetworkSubwayRealtimeStationArrivalInfoThread에 역 이름을 인자로 실행
            mtask = (TrafficSubwayInfoTypeB.this.new ProcessNetworkSubwayRealtimeStationArrivalInfoThread()).execute(new String[]{stationNM});
        }

        public void executeClient(String[] strings) { // 오픈 api를 통해 얻은 공공데이터 xml 결과를 파싱하여 추출
            HttpURLConnection urlConn = null;
            URL apiURL = null;
            InputStream in = null;
            XmlPullParserFactory factory = null; // KXmlParser 생성기
            XmlPullParser xpp = null; // parser 인터페이스
            boolean var6 = true;

            try {
                String station = strings[0];

                if (station.equals("서울역")) { // 서울역 -> 서울로 파싱
                    station = "서울";
                }

                apiURL = new URL("http://swopenapi.seoul.go.kr/api/subway/" + openAPIKey + "/xml/stationInfo/1/999/" + URLEncoder.encode(station, "UTF-8"));
                urlConn = (HttpURLConnection) apiURL.openConnection();

                // 연결에 실패할 시 일정 시간 동안 연결 재시도
                long start = System.currentTimeMillis();
                while(urlConn.getResponseCode() != HttpURLConnection.HTTP_OK) {
                    urlConn = (HttpURLConnection) apiURL.openConnection();
                    if(System.currentTimeMillis()-start>5000) {
                        break;
                    }
                }

                in = apiURL.openStream();
                factory = XmlPullParserFactory.newInstance();
                factory.setNamespaceAware(true);
                xpp = factory.newPullParser();
                xpp.setInput(in, "UTF-8"); // 외부에서 오픈된 입력 스트림 처리
                int eventType = xpp.getEventType(); // 0 : START_DOUCUMENT

                for (boolean isItemTag = false; eventType != 1; eventType = xpp.next()) { // 1 : END_DOUCUMENT
                    if (eventType == 2) { // START_TAG
                        TrafficSubwayInfoTypeB.this.tagName = xpp.getName();
                        if (TrafficSubwayInfoTypeB.this.tagName.equals("row")) {
                            isItemTag = true;
                        }
                    } else if (eventType == 4) { // TEXT
                        if (isItemTag && !TrafficSubwayInfoTypeB.this.tagName.equals("") && !xpp.getText().equals("")) {
                            if (TrafficSubwayInfoTypeB.this.tagName.equals("subwayId")) {
                                TrafficSubwayInfoTypeB.this.subwayId = xpp.getText();
                            } else if (TrafficSubwayInfoTypeB.this.tagName.equals("statnFnm")) {
                                TrafficSubwayInfoTypeB.this.statnFnm = xpp.getText();
                            } else if (TrafficSubwayInfoTypeB.this.tagName.equals("statnTnm")) {
                                TrafficSubwayInfoTypeB.this.statnTnm = xpp.getText();
                            }
                        }
                    } else if (eventType == 3) {  // END_TAG
                        TrafficSubwayInfoTypeB.this.tagName = xpp.getName();
                        if (!TrafficSubwayInfoTypeB.this.tagName.equals("row")) { // 작은 태그 종료
                            TrafficSubwayInfoTypeB.this.tagName = "";
                        } else { // row 태그 종료
                            isItemTag = false;
                            StationInfo info;
                            if (!TrafficSubwayInfoTypeB.this.subwayId.equals("1065")) { // 2호선은 반대로 설정
                                info = new StationInfo(TrafficSubwayInfoTypeB.this.subwayId, TrafficSubwayInfoTypeB.this.statnFnm, TrafficSubwayInfoTypeB.this.statnTnm);
                            } else {
                                info = new StationInfo(TrafficSubwayInfoTypeB.this.subwayId, TrafficSubwayInfoTypeB.this.statnTnm, TrafficSubwayInfoTypeB.this.statnFnm);
                            }
                            TrafficSubwayInfoTypeB.this.stationInfoHashMap.put(info.getSubwayId(), info); // 지하철 id 별로 지하철 정보 저장
                            TrafficSubwayInfoTypeB.this.lineArrayList.add(TrafficSubwayInfoTypeB.this.subwayId); // 지하철 id 저장 ( 호선별 )
                            info = null;
                        }
                    }
                }
                setResult(RESULT_OK); // RESULT_OK를 결과 코드로 호출한 액티비티에 전달
            } catch (MalformedURLException var22) {
                var22.printStackTrace();
            } catch (IOException var23) {
                var23.printStackTrace();
                var23.printStackTrace();
                setResult(RESULT_CANCELED); // RESULT_CANCELED를 결과 코드로 호출한 액티비티에 전달
                customProgressDialog.dismiss();
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
    } // 공공데이터의 역정보를 통해 지하철 id, 이전 역, 다음 역 추출
    public class ProcessNetworkSubwayRealtimeStationArrivalInfoThread extends AsyncTask<String, Void, String> {
        // 역 이름을 인자로 받고 공공데이터의 실시간 열차 도착 정보를 통해 실시간 열차 도착 정보를 추출
        StringBuilder sb = new StringBuilder();

        @Override
        protected String doInBackground(String... strings) {
            this.executeClient(strings);
            return "";
        }

        protected void onPostExecute(String result) {
            TrafficSubwayInfoTypeB.this.lineHashSet.addAll(TrafficSubwayInfoTypeB.this.lineArrayList);
            TrafficSubwayInfoTypeB.this.lineArrayList.clear();
            TrafficSubwayInfoTypeB.this.lineArrayList.addAll(TrafficSubwayInfoTypeB.this.lineHashSet); // 중복 제거
            TrafficSubwayInfoTypeB.this.setLineIcon(); // setLineIcon을 통해 선택 역에 해당 하는 호선들의 아이콘 설정
            if(up1trainNo=="" && up2trainNo=="" && down1trainNo=="" && down2trainNo=="") { // 도착 열차가 없을 시
                TrafficSubwayInfoTypeB.this.cancelCustomProgressDialog();
            } else { // 열차 번호들을 인자로 ProcessDatabaseTrainInfoThread 실행
                mtask = (TrafficSubwayInfoTypeB.this.new ProcessDatabaseTrainInfoThread()).execute(sb.toString().trim().split(" "));
            }
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
                urlConn = (HttpURLConnection) apiURL.openConnection();

                long start = System.currentTimeMillis();
                while(urlConn.getResponseCode() != HttpURLConnection.HTTP_OK) {
                    urlConn = (HttpURLConnection) apiURL.openConnection();
                    if(System.currentTimeMillis()-start>5000) {
                        break;
                    }
                }

                in = apiURL.openStream();
                factory = XmlPullParserFactory.newInstance();
                factory.setNamespaceAware(true);
                xpp = factory.newPullParser();
                xpp.setInput(in, "UTF-8");
                int eventType = xpp.getEventType();
                // 태그를 구분하여 xml 파싱
                for (boolean isItemTag = false; eventType != 1; eventType = xpp.next()) {
                    if (eventType == 2) {
                        TrafficSubwayInfoTypeB.this.tagName = xpp.getName();
                        if (TrafficSubwayInfoTypeB.this.tagName.equals("row")) {
                            isItemTag = true;
                        }
                    } else if (eventType == 4) {
                        if (isItemTag && !TrafficSubwayInfoTypeB.this.tagName.equals("") && !xpp.getText().equals("")) {
                            if (TrafficSubwayInfoTypeB.this.tagName.equals("rowNum")) {
                                TrafficSubwayInfoTypeB.this.rowNum = xpp.getText();
                            } else if (TrafficSubwayInfoTypeB.this.tagName.equals("subwayId")) {
                                TrafficSubwayInfoTypeB.this.subwayId = xpp.getText();
                            } else if (TrafficSubwayInfoTypeB.this.tagName.equals("updnLine")) { // 상하행선 구분
                                TrafficSubwayInfoTypeB.this.updnLine = xpp.getText();
                            } else if (TrafficSubwayInfoTypeB.this.tagName.equals("trainLineNm")) { // 도착지 방면
                                TrafficSubwayInfoTypeB.this.trainLineNm = xpp.getText();
                            } else if (TrafficSubwayInfoTypeB.this.tagName.equals("barvlDt")) { // 열차 도착 예정 시간 (초)
                                TrafficSubwayInfoTypeB.this.barvlDt = xpp.getText();
                            } else if (TrafficSubwayInfoTypeB.this.tagName.equals("bstatnNm")) { //
                                TrafficSubwayInfoTypeB.this.bstatnNm = xpp.getText();
                            } else if (TrafficSubwayInfoTypeB.this.tagName.equals("btrainNo")) { // 열차 번호
                                TrafficSubwayInfoTypeB.this.btrainNo = xpp.getText();
                                sb.append(TrafficSubwayInfoTypeB.this.btrainNo.trim() + " "); // 열차 번호 누적 저장
                            } else if (TrafficSubwayInfoTypeB.this.tagName.equals("arvlMsg2")) { // 도착 메세지
                                TrafficSubwayInfoTypeB.this.arvlMsg2 = xpp.getText();
                            }
                        }
                    } else if (eventType == 3) {
                        TrafficSubwayInfoTypeB.this.tagName = xpp.getName();
                        if (TrafficSubwayInfoTypeB.this.tagName.equals("row")) {
                            isItemTag = false;
                            RealtimeStationArrivalInfo info = new RealtimeStationArrivalInfo(TrafficSubwayInfoTypeB.this.rowNum, TrafficSubwayInfoTypeB.this.subwayId, TrafficSubwayInfoTypeB.this.updnLine, TrafficSubwayInfoTypeB.this.trainLineNm, TrafficSubwayInfoTypeB.this.barvlDt, TrafficSubwayInfoTypeB.this.bstatnNm,  TrafficSubwayInfoTypeB.this.btrainNo,TrafficSubwayInfoTypeB.this.arvlMsg2);
                            TrafficSubwayInfoTypeB.this.realtimeStationArrivalInfoArrayList.add(info);
                            if (TrafficSubwayInfoTypeB.this.lineArrayList.size() == 0) {
                                TrafficSubwayInfoTypeB.this.lineArrayList.add(TrafficSubwayInfoTypeB.this.subwayId);
                            }
                            info = null;
                        } else {
                            TrafficSubwayInfoTypeB.this.tagName = "";
                        }
                    }
                }
                setResult(RESULT_OK);
            } catch (MalformedURLException var22) {
                var22.printStackTrace();
            } catch (IOException var23) {
                var23.printStackTrace();
                setResult(RESULT_CANCELED);
                customProgressDialog.dismiss();
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
    // 역 이름을 인자로 받고 공공데이터의 실시간 열차 도착 정보를 통해 실시간 열차 도착 정보를 추출
    private void setLineIcon() { // 선택 역에 해당 하는 호선들의 아이콘 설정
        if (this.lineGroupLayout.getChildCount() == 0) { // 처음 호출시
            LayoutInflater layoutInflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE); // inflate : 객체화
            int drawableId = 0;
            if (this.lineArrayList.size() > 0) { // 정렬
                Collections.sort(this.lineArrayList, new Comparator<String>() {
                    public int compare(String s, String t1) {
                        return s.compareToIgnoreCase(t1);
                    }
                });
            }
            for (int i = 0; i < this.lineArrayList.size(); ++i) { // 라인그룹에 요소 추가
                LinearLayout lineIconLayout = (LinearLayout) layoutInflater.inflate(R.layout.traffic_subway_info_type_b_line_icon, (ViewGroup) null);
                switch (Integer.parseInt((String) this.lineArrayList.get(i))) { // 지하철 id로 호선 아이콘 이미지 구분
                    case 1001:
                        drawableId = R.drawable.selector_trafficsub_radio_1;
                        break;
                    case 1002:
                        drawableId = R.drawable.selector_trafficsub_radio_2;
                        break;
                    case 1003:
                        drawableId = R.drawable.selector_trafficsub_radio_3;
                        break;
                    case 1004:
                        drawableId = R.drawable.selector_trafficsub_radio_4;
                        break;
                    case 1005:
                        drawableId = R.drawable.selector_trafficsub_radio_5;
                        break;
                    case 1006:
                        drawableId = R.drawable.selector_trafficsub_radio_6;
                        break;
                    case 1007:
                        drawableId = R.drawable.selector_trafficsub_radio_7;
                        break;
                    case 1008:
                        drawableId = R.drawable.selector_trafficsub_radio_8;
                        break;
                    case 1009:
                        drawableId = R.drawable.selector_trafficsub_radio_9;
                        break;
                    case 1063:
                        drawableId = R.drawable.selector_trafficsub_radio_kyeong;
                        break;
                    case 1065:
                        drawableId = R.drawable.selector_trafficsub_radio_konghang;
                        break;
                    case 1067:
                        drawableId = R.drawable.selector_trafficsub_radio_kyeongchun;
                        break;
                    case 1069:
                        drawableId = R.drawable.selector_trafficsub_radio_incheon;
                        break;
                    case 1071:
                        drawableId = R.drawable.selector_trafficsub_radio_sooin;
                        break;
                    case 1075:
                        drawableId = R.drawable.selector_trafficsub_radio_bundang;
                        break;
                    case 1077:
                        drawableId = R.drawable.selector_trafficsub_radio_sinbundang;
                }

                lineIconLayout.setId(Integer.parseInt((String) this.lineArrayList.get(i)));
                if (Build.VERSION.SDK_INT >= 16) { // 버전에 따른 이미지 적용
                    ((ImageView) lineIconLayout.findViewById(R.id.line_icon)).setBackground(this.getResources().getDrawable(drawableId, (Resources.Theme) null));
                } else {
                    ((ImageView) lineIconLayout.findViewById(R.id.line_icon)).setBackgroundResource(drawableId);
                }

                lineIconLayout.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View view) { // 아이콘 선택시
                        if (TrafficSubwayInfoTypeB.this.lineGroupLayout.getChildCount() > 0) {
                            for (int i = 0; i < TrafficSubwayInfoTypeB.this.lineGroupLayout.getChildCount(); ++i) { // 초기화
                                TrafficSubwayInfoTypeB.this.lineGroupLayout.getChildAt(i).setSelected(false);
                                TrafficSubwayInfoTypeB.this.lineGroupLayout.getChildAt(0).findViewById(R.id.line_icon).setSelected(false);
                            }
                            // 선택된 호선에 대해 setRealtimeArrival 호출
                            view.setSelected(true);
                            view.findViewById(R.id.line_icon).setSelected(true);
                            TrafficSubwayInfoTypeB.this.selectedSubwayId = String.valueOf(view.getId());
                            TrafficSubwayInfoTypeB.this.setRealtimeArrival();
                        }
                    }
                });
                this.lineGroupLayout.addView(lineIconLayout);
            }

            if (this.lineGroupLayout.getChildCount() > 0) { // 초기 화면
                this.lineGroupLayout.getChildAt(0).setSelected(true);
                this.lineGroupLayout.getChildAt(0).findViewById(R.id.line_icon).setSelected(true);
                this.selectedSubwayId = String.valueOf(this.lineGroupLayout.getChildAt(0).getId());
                this.setRealtimeArrival();
            }
        } else { // 다시 호출 시
            this.setRealtimeArrival();
        }
    } // 선택 역에 해당 하는 호선들의 아이콘 설정
    private void setRealtimeArrival() { // 실시간 열차 도착 정보를 통해 추출한 정보를 뷰에 적용
        if (this.stationInfoHashMap.size() > 0) { // 호선에 따른 역 정보 세팅
            this.prevStation.setText(((StationInfo) this.stationInfoHashMap.get(this.selectedSubwayId)).getStatnFnm());
            this.nextStation.setText(((StationInfo) this.stationInfoHashMap.get(this.selectedSubwayId)).getStatnTnm());
        }
        // 초기 값
        this.upLineArvl1MsgHeading.setText("도착정보가 없습니다.");
        this.upLineArvl1Msg.setText("-");
        this.upLineArvl2MsgHeading.setText("도착정보가 없습니다.");
        this.upLineArvl2Msg.setText("-");
        this.downLineArvl1MsgHeading.setText("도착정보가 없습니다.");
        this.downLineArvl1Msg.setText("-");
        this.downLineArvl2MsgHeading.setText("도착정보가 없습니다.");
        this.downLineArvl2Msg.setText("-");
        int upLineNo = 0;
        int downLineNo = 0;

        if (this.realtimeStationArrivalInfoArrayList.size() > 0) { // 정렬
            Collections.sort(this.realtimeStationArrivalInfoArrayList, new Comparator<RealtimeStationArrivalInfo>() {
                public int compare(RealtimeStationArrivalInfo s, RealtimeStationArrivalInfo t1) {
                    return Integer.parseInt(s.getBarvlDt()) < Integer.parseInt(t1.getBarvlDt()) ? -1 : (Integer.parseInt(s.getBarvlDt()) > Integer.parseInt(t1.getBarvlDt()) ? 1 : 0);
                }
            });
        }

        for (int i = 0; i < this.realtimeStationArrivalInfoArrayList.size(); ++i) { // 정렬된 순서에 따라 상행, 하행에 따라 열차 도착 정보 세팅
            // 선택된 지하철 id와 일치하는 정보만
            if (((RealtimeStationArrivalInfo) this.realtimeStationArrivalInfoArrayList.get(i)).getSubwayId().equals(this.selectedSubwayId)) {
                String arrivalMsg;
                if (!((RealtimeStationArrivalInfo) this.realtimeStationArrivalInfoArrayList.get(i)).getUpdnLine().equals("상행") && !((RealtimeStationArrivalInfo) this.realtimeStationArrivalInfoArrayList.get(i)).getUpdnLine().equals("외선")) {
                    if (downLineNo < 2) { // 2개 까지
                        arrivalMsg = ((RealtimeStationArrivalInfo) this.realtimeStationArrivalInfoArrayList.get(i)).getArvlMsg2().replaceAll("\\[", "").replaceAll("\\]", "");
                        arrivalMsg = arrivalMsg.split("\\(")[0];
                        if (!arrivalMsg.contains("도착") && !arrivalMsg.contains("출발") && !arrivalMsg.contains("진입")) {
                            arrivalMsg = arrivalMsg.concat("도착");
                        }
                        if (downLineNo == 0) {
                            this.downLineArvl1MsgHeading.setText(((RealtimeStationArrivalInfo) this.realtimeStationArrivalInfoArrayList.get(i)).getBstatnNm() + "행");
                            this.downLineArvl1Msg.setText(arrivalMsg);
                            this.down1trainNo =  this.realtimeStationArrivalInfoArrayList.get(i).getBtrainNo();
                            this.down1barvlDt = this.realtimeStationArrivalInfoArrayList.get(i).getBarvlDt();
                            ++downLineNo;
                        } else {
                            this.downLineArvl2MsgHeading.setText(((RealtimeStationArrivalInfo) this.realtimeStationArrivalInfoArrayList.get(i)).getBstatnNm() + "행");
                            this.downLineArvl2Msg.setText(arrivalMsg);
                            this.down2trainNo =  this.realtimeStationArrivalInfoArrayList.get(i).getBtrainNo();
                            this.down2barvlDt = this.realtimeStationArrivalInfoArrayList.get(i).getBarvlDt();
                            ++downLineNo;
                        }
                    }
                } else if (upLineNo < 2) {
                    arrivalMsg = ((RealtimeStationArrivalInfo) this.realtimeStationArrivalInfoArrayList.get(i)).getArvlMsg2().replaceAll("\\[", "").replaceAll("\\]", "");
                    arrivalMsg = arrivalMsg.split("\\(")[0];
                    if (!arrivalMsg.contains("도착") && !arrivalMsg.contains("출발") && !arrivalMsg.contains("진입")) {
                        arrivalMsg = arrivalMsg.concat(" 도착");
                    }
                    if (upLineNo == 0) {
                        this.upLineArvl1MsgHeading.setText(((RealtimeStationArrivalInfo) this.realtimeStationArrivalInfoArrayList.get(i)).getBstatnNm() + "행");
                        this.upLineArvl1Msg.setText(arrivalMsg);
                        this.up1trainNo =  this.realtimeStationArrivalInfoArrayList.get(i).getBtrainNo();
                        this.up1barvlDt = this.realtimeStationArrivalInfoArrayList.get(i).getBarvlDt();
                        ++upLineNo;
                    } else {
                        this.upLineArvl2MsgHeading.setText(((RealtimeStationArrivalInfo) this.realtimeStationArrivalInfoArrayList.get(i)).getBstatnNm() + "행");
                        this.upLineArvl2Msg.setText(arrivalMsg);
                        this.up2trainNo =  this.realtimeStationArrivalInfoArrayList.get(i).getBtrainNo();
                        this.up2barvlDt = this.realtimeStationArrivalInfoArrayList.get(i).getBarvlDt();
                        ++upLineNo;
                    }
                }
            }
        }
    } // 실시간 열차 도착 정보를 통해 추출한 정보를 뷰에 적용
    public class ProcessDatabaseTrainInfoThread extends AsyncTask<String, Void, String> {
        // 서버의 전차 열차 테이블을 조회
        String errorString = null;
        @Override
        protected String doInBackground(String... strings) {
            if(strings[0]=="") { // 열차 번호가 없을 시
                return "";
            } else {
                return this.executeClient(strings);
            }

        }

        protected void onPostExecute(String result) { // doInBackground의 결과를 showResult를 통해Json 파싱
            mJsonString = result;
            Log.i("json",mJsonString);
            showResult();
            TrafficSubwayInfoTypeB.this.cancelCustomProgressDialog(); // 로딩 UI 끄기
        }

        public String executeClient(String[] strings) { // 서버의 php에 접속하여 전체 열차 테이블에서 열차 번호를 조회하여 결과값을 반환
            // 임시 열차 번호
            ArrayList<String> trainlist = new ArrayList();
            trainlist.add("2331"); // 2002
            trainlist.add("2019"); // 2004
            trainlist.add("2314"); // 2007
            trainlist.add("2318"); // 2010

            String[] searchKeywords = {"","","",""};
            for(int i=0;i<strings.length;i++) { // 열차 수 만큼
                if(i>=4) break;
                searchKeywords[i] = trainlist.get(i);
            }

            String serverURL = "http://" + IP_ADDRESS + "/gettrains.php";
            String postParameters = "train_num=" + searchKeywords[0] + "&" + "train_num2=" + searchKeywords[1] +
                    "&" + "train_num3=" + searchKeywords[2] + "&" + "train_num4="+ searchKeywords[3];

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

    } // 서버의 전차 열차 테이블을 조회
    private void showResult() { // JSON TAG를 통해 값을 추출아여 TrainInfo에 저장 및 세팅

        String TAG_JSON = "root";
        String TAG_FIRSTTRAFFICNUM = "first_traffic_num";
        String TAG_TRAINNUM = "train_num";
        String TAG_EMPTYSEAT = "empty_seat";
        String TAG_TOTALSEAT = "total_seat";


        try {
            JSONObject jsonObject = new JSONObject(mJsonString);
            JSONArray jsonArray = jsonObject.getJSONArray(TAG_JSON);
            mArrayList.clear();
            // JSON 파싱
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject item = jsonArray.getJSONObject(i);
                String first_traffic_num = item.getString(TAG_FIRSTTRAFFICNUM);
                String train_num = item.getString(TAG_TRAINNUM);
                String empty_seat = item.getString(TAG_EMPTYSEAT);
                String total_seat = item.getString(TAG_TOTALSEAT);
                TrainInfo trainInfo = new TrainInfo(train_num, first_traffic_num, empty_seat, total_seat);
                mArrayList.add(trainInfo);
            }

            if(mArrayList.size()>0) { // 좌석 수 세팅
                this.upLine1Seat.setText(mArrayList.get(0).getEmpty_seat() + "/" + mArrayList.get(0).getTotal_seat());
                if (mArrayList.size() > 1) {
                    this.downLine1Seat.setText(mArrayList.get(1).getEmpty_seat() + "/" + mArrayList.get(1).getTotal_seat());
                    if (mArrayList.size() > 2) {
                        this.upLine2Seat.setText(mArrayList.get(2).getEmpty_seat() + "/" + mArrayList.get(2).getTotal_seat());
                        if (mArrayList.size() > 3) {
                            this.downLine2Seat.setText(mArrayList.get(3).getEmpty_seat() + "/" + mArrayList.get(3).getTotal_seat());
                        }
                    }
                }
            }

            /*
            for(int i=0;i<mArrayList.size();i++) {
                if(mArrayList.get(i).getTrain_num()==up1trainNo) {
                    this.upLine1Seat.setText(mArrayList.get(i).getEmpty_seat() + "/" + mArrayList.get(i).getTotal_seat());
                } else if(mArrayList.get(0).getTrain_num()==up2trainNo) {
                    this.upLine2Seat.setText(mArrayList.get(i).getEmpty_seat() + "/" + mArrayList.get(i).getTotal_seat());
                } else if(mArrayList.get(0).getTrain_num()==down1trainNo) {
                    this.downLine1Seat.setText(mArrayList.get(i).getEmpty_seat() + "/" + mArrayList.get(i).getTotal_seat());
                } else if(mArrayList.get(0).getTrain_num()==down2trainNo) {
                    this.downLine2Seat.setText(mArrayList.get(i).getEmpty_seat() + "/" + mArrayList.get(i).getTotal_seat());
                }
            }
            */
        } catch (JSONException e) {
        }
    } // JSON TAG를 통해 값을 추출아여 TrainInfo에 저장 및 세팅





}