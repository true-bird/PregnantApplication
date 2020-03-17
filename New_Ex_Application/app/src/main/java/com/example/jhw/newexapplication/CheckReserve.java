package com.example.jhw.newexapplication;

import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Layout;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
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

import static com.example.jhw.newexapplication.MainActivity.IP_ADDRESS;
import static com.example.jhw.newexapplication.MainActivity.dbHelper;
import static com.example.jhw.newexapplication.MainActivity.user_id;

public class CheckReserve extends AppCompatActivity {

    private CustomProgressDialog customProgressDialog;
    private String tagName = "";
    private String btrainNo;
    private String trainNum;

    private String arvlmsg;
    private String bstatnNm;
    private String traffic_num;
    private boolean entireflag;

    private String mJsonString;
    private String located;
    private TextView arvlMsgHeading;
    private TextView arvlMsg;
    private TextView nextStation;
    private TextView stopStation;
    private ImageView lineIcon;
    private ImageView leftseat;
    private ImageView rightseat;
    private TextView number;
    private TextView resCancel;

    private RelativeLayout layout;

    private String res_time="";
    private String nStation="";
    private String stationNM="";
    private int drawableId;


    private AlertDialog.Builder alertDialogBuilder;
    private ArrayList<RealtimeStationArrivalInfo> realtimeStationArrivalInfoArrayList = new ArrayList();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check_reserve);
        this.initView(); // 초기 세팅
        this.showCustomProgressDialog(); // 로딩 UI 보여주기
        (new ProcessDatabaseReserveInfoThread()).execute();
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
    } // 로딩
    private void cancelCustomProgressDialog() { // 로딩 끄기
        if (this.customProgressDialog != null && this.customProgressDialog.isShowing()) {
            this.customProgressDialog.cancel();
        }
    } // 로딩 끄기

    private void initView() { // 초기 세팅
        this.layout = (RelativeLayout)this.findViewById(R.id.constraintLayout);
        this.resCancel = (TextView)this.findViewById(R.id.res_cancel_button);
        this.resCancel.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                alertDialogBuilder = new AlertDialog.Builder(CheckReserve.this);
                alertDialogBuilder.setTitle("예약 취소");
                alertDialogBuilder.setMessage("예약을 취소하시겠습니까?");
                alertDialogBuilder.setCancelable(false);
                alertDialogBuilder.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        (new DeleteDatabaseReserveInfoThread()).execute();
                    }
                });
                alertDialogBuilder.setNegativeButton("취소", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // 다이얼로그를 취소한다
                        dialog.cancel();
                    }
                });
                alertDialogBuilder.show();
            }
        });
        this.number = (TextView)this.findViewById(R.id.check_number);
        this.leftseat = (ImageView) this.findViewById(R.id.check_left_seat);
        this.rightseat = (ImageView) this.findViewById(R.id.check_right_seat);
        this.leftseat.setImageResource(R.drawable.full_icon);
        this.rightseat.setImageResource(R.drawable.full_icon);
        this.leftseat.setVisibility(View.INVISIBLE);
        this.rightseat.setVisibility(View.INVISIBLE);
        this.arvlMsgHeading = (TextView) this.findViewById(R.id.check_arvl_msg_heading);
        this.arvlMsg = (TextView) this.findViewById(R.id.check_arvl_msg);


        // 사용자 db, 서버 db 동시 저장
        this.nextStation = (TextView) this.findViewById(R.id.check_n_station);
        this.stopStation = (TextView) this.findViewById(R.id.check_station_nm);
        this.lineIcon = (ImageView) this.findViewById(R.id.check_line_icon);

    }
    private void setRealtimeArrival() { // 실시간 열차 도착 정보를 통해 추출한 정보를 뷰에 적용
        this.arvlMsgHeading.setText("도착정보가 없습니다.");
        this.arvlMsg.setText("-");
        for (int i = 0; i < this.realtimeStationArrivalInfoArrayList.size(); ++i) {
            if (((RealtimeStationArrivalInfo) this.realtimeStationArrivalInfoArrayList.get(i)).getBtrainNo().equals(this.trainNum)) {
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


    private void load_Sdb() { // 사용자 내부 DB에 예약 테이블 조회

        SQLiteDatabase db = dbHelper.getReadableDatabase() ;
        db.execSQL(ContractDB.SQL_CREATE_RES_TBL);
        Cursor cursor = db.rawQuery(ContractDB.SQL_SELECT_RES_TBL, null) ;

        if (cursor.moveToNext()) { // 레코드가 존재한다면,
            // 예약한 역 이름, 다음 역 이름, 호선 id, 예약 시간, 좌석 위치, 칸 번호를 추출
            this.stationNM = cursor.getString(0) ;
            this.stopStation = (TextView) this.findViewById(R.id.check_station_nm);
            this.stopStation.setText(this.stationNM);

            this.nStation = cursor.getString(1) ;
            this.nextStation = (TextView) this.findViewById(R.id.check_n_station);
            this.nextStation.setText(this.nStation);

            // 호선 id에 통해 아이콘 설정
            this.drawableId = cursor.getInt(2) ;
            this.lineIcon = (ImageView) this.findViewById(R.id.check_line_icon);
            this.lineIcon.setImageResource(this.drawableId);

            this.arvlMsg.setText(cursor.getString(3) );

            // 좌석 위치에 따른 아이콘 표시 구분
            if(cursor.getString(4).equals("0") ) {
                this.leftseat.setVisibility(View.VISIBLE);
            } else {
                this.rightseat.setVisibility(View.VISIBLE);
            }

            this.number.setText((cursor.getString(5))+"호차");
        }

    }

    public class ProcessDatabaseReserveInfoThread extends AsyncTask<String, Void, String> {
        String errorString = null;

        protected void onPostExecute(String result) {

            mJsonString = result;
            Log.d("tTTt", "onPostExecute: "+ result);
            showResult();
            if(entireflag) {
                (new ProcessNetworkSubwayRealtimeStationArrivalInfoThread()).execute(new String[]{stationNM}); // 사용자 선택 역
            }
            else {

                alertDialogBuilder = new AlertDialog.Builder(CheckReserve.this);
                alertDialogBuilder.setTitle("예약 확인");
                alertDialogBuilder.setMessage("예약 정보가 없습니다");
                alertDialogBuilder.setCancelable(false);
                alertDialogBuilder.setNegativeButton("확인", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // 다이얼로그를 취소한다
                        dialog.cancel();
                        cancelCustomProgressDialog();
                    }
                });
                alertDialogBuilder.show();

                arvlMsgHeading.setText("최근 예약정보");
                load_Sdb();
                nextStation.setText(nStation);
                stopStation.setText(stationNM);
                lineIcon.setImageResource(drawableId);
                LinearLayout.LayoutParams msgParams = new LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.WRAP_CONTENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT);
                arvlMsgHeading.setLayoutParams(msgParams);
                arvlMsg.setLayoutParams(msgParams);
                msgParams.setMargins(10,150,35,50);
                resCancel.setVisibility(View.INVISIBLE);
                //layout.setVisibility(View.INVISIBLE);
            }

        }

        @Override
        protected String doInBackground(String... strings) {
            return executeClient();
        }
        public String executeClient() {

            String serverURL = "http://" + IP_ADDRESS + "/checkres.php";
            String postParameters = "user_id=" + user_id;
            Log.d("tTTt", "executeClient: "+user_id);
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
                Log.d("test", "response code - " + responseStatusCode);

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

                Log.d("test", "GetData : Error ", e);
                errorString = e.toString();

                return null;
            }
        }

    } // 사용자 차량 번호, 칸 번호
    public class DeleteDatabaseReserveInfoThread extends AsyncTask<String, Void, String> {
        String errorString = null;

        protected void onPostExecute(String result) {
            mJsonString = result;
            Log.d("test", "response-- " + result);
            finish();
        }

        @Override
        protected String doInBackground(String... strings) {
            return executeClient();
        }
        public String executeClient() {

            String serverURL = "http://" + IP_ADDRESS + "/delres.php";
            String postParameters = "user_id=" + user_id;

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
                Log.d("test", "response code - " + responseStatusCode);

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

                Log.d("test", "GetData : Error ", e);
                errorString = e.toString();

                return null;
            }
        }
    }

    private void showResult() {

        String TAG_JSON = "root";
        String TAG_TRAINNUM = "train_num";
        String TAG_TRAFFICNUM = "traffic_num";
        String TAG_STATIONNAME = "station_name";
        String TAG_LOCATED = "located";
        String TAG_NEXTSTATION = "next_station";
        String TAG_DRAWABLEID = "drawable_id";



        try {
            JSONObject jsonObject = new JSONObject(mJsonString);
            JSONArray jsonArray = jsonObject.getJSONArray(TAG_JSON);

            if(jsonArray.length()==0) entireflag=false;
            else entireflag=true;

            for (int i = 0; i < jsonArray.length(); i++) {

                JSONObject item = jsonArray.getJSONObject(i);

                traffic_num = item.getString(TAG_TRAFFICNUM); // 호차
                trainNum = item.getString(TAG_TRAINNUM).trim(); // 실시간 조회
                located = item.getString(TAG_LOCATED); // 위치

                stationNM = item.getString(TAG_STATIONNAME);
                nStation = item.getString(TAG_NEXTSTATION);
                drawableId = item.getInt(TAG_DRAWABLEID);

            }

            if(located.equals("0")) {
                this.leftseat.setVisibility(View.VISIBLE);
            } else {
                this.rightseat.setVisibility(View.VISIBLE);
            }
            nextStation.setText(this.nStation);
            stopStation.setText(this.stationNM);
            lineIcon.setImageResource(this.drawableId);
            number.setText((traffic_num.charAt(1)-47)+"호차");

        } catch (JSONException e) {
            Log.d("test", "showResult : ", e);
        }


    }
    public class ProcessNetworkSubwayRealtimeStationArrivalInfoThread extends AsyncTask<String, Void, String> {
        // 역 이름을 인자로 받고 공공데이터의 실시간 열차 도착 정보를 통해 실시간 열차 도착 정보를 추출
        StringBuilder sb = new StringBuilder();


        @Override
        protected String doInBackground(String... strings) {
            this.executeClient(strings);
            return "";
        }

        protected void onPostExecute(String result) {
            CheckReserve.this.setRealtimeArrival();
            CheckReserve.this.cancelCustomProgressDialog();

        }

        public void executeClient(String[] strings) {
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
                apiURL = new URL("http://swopenapi.seoul.go.kr/api/subway/696e5247766a68773736546c797442/xml/realtimeStationArrival/1/999/" + URLEncoder.encode(strings[0], "UTF-8"));
                in = apiURL.openStream();


                factory = XmlPullParserFactory.newInstance();
                factory.setNamespaceAware(true);
                xpp = factory.newPullParser();
                xpp.setInput(in, "UTF-8");
                int eventType = xpp.getEventType();

                for (boolean isItemTag = false; eventType != 1; eventType = xpp.next()) {

                    if (eventType == 2) {
                        CheckReserve.this.tagName = xpp.getName();
                        if (CheckReserve.this.tagName.equals("row")) {
                            isItemTag = true;
                        }
                    } else if (eventType == 4) {
                        if (isItemTag && !CheckReserve.this.tagName.equals("") && !xpp.getText().equals("")) {
                            if (CheckReserve.this.tagName.equals("btrainNo")) { //
                                CheckReserve.this.btrainNo = xpp.getText().trim();
                            } else if (CheckReserve.this.tagName.equals("bstatnNm")) { //
                                CheckReserve.this.bstatnNm = xpp.getText();
                            } else if (CheckReserve.this.tagName.equals("arvlMsg2")) { // 도착 메세지
                                CheckReserve.this.arvlmsg = xpp.getText();
                            }
                        }
                    } else if (eventType == 3) {

                        CheckReserve.this.tagName = xpp.getName();
                        Log.d("Testet","eventtype    "+tagName+"d왓엉");
                        if (CheckReserve.this.tagName.equals("row")) {
                            Log.d("Testet","eventtype"+eventType+"d왓엉");
                            isItemTag = false;
                            Log.d("test","realtest" + bstatnNm + ", " + btrainNo + ", " + arvlmsg);
                            RealtimeStationArrivalInfo info = new RealtimeStationArrivalInfo(bstatnNm,btrainNo,arvlmsg);
                            CheckReserve.this.realtimeStationArrivalInfoArrayList.add(info);

                            info = null;
                            Log.d("Testet","realtest2 " +  CheckReserve.this.realtimeStationArrivalInfoArrayList.get(0).getBtrainNo());

                        } else {
                            CheckReserve.this.tagName = "";
                        }
                    }
                }
            } catch (MalformedURLException var22) {
                var22.printStackTrace();
            } catch (IOException var23) {
                var23.printStackTrace();
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

}
