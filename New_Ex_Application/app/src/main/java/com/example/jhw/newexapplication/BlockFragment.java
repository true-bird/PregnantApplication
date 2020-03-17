package com.example.jhw.newexapplication;

import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.support.annotation.IdRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;

import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import static com.example.jhw.newexapplication.MainActivity.IP_ADDRESS;
import static com.example.jhw.newexapplication.MainActivity.dbHelper;
import static com.example.jhw.newexapplication.MainActivity.user_id;


public class BlockFragment extends Fragment {

    private AlertDialog.Builder alertDialogBuilder;
    private ImageView leftseat;
    private ImageView rightseat;
    private AlertDialog ad;
    private RadioGroup radioGroup;
    private RadioButton rleftseat;
    private RadioButton rrightseat;
    private TextView res_button;
    private TextView number;
    private String mJsonString;
    static String selectedSeat;

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) { // 사용자 눈에 보이는 프래그먼트 예약 버튼 및 라디오 버튼 초기화
        if(res_button!=null) {
            res_button.setEnabled(false);
            res_button.setBackgroundResource(R.color.darker_gray);
        }
        if(radioGroup!=null && rrightseat!=null && rleftseat!=null) {
            if (rleftseat.getVisibility() != View.INVISIBLE || rrightseat.getVisibility() != View.INVISIBLE) {
                radioGroup.clearCheck();
            }
        }
        super.setUserVisibleHint(isVisibleToUser);
    }

    @Override
    public void onPause() { // 액티비티 일시정지 시 프래그먼트 예약 버튼 및 라디오 버튼 초기화
        if(res_button!=null) {
            res_button.setEnabled(false);
            res_button.setBackgroundResource(R.color.darker_gray);
        }
        if(radioGroup!=null && rrightseat!=null && rleftseat!=null) {
            if (rleftseat.getVisibility() != View.INVISIBLE || rrightseat.getVisibility() != View.INVISIBLE) {
                radioGroup.clearCheck();
            }
        }
        super.onPause();
    }

    public BlockFragment() {}

    public static BlockFragment newInstance(int sectionNumber) { // 생성자 대신 하나의 인스턴스를 생성하여 객체 공유 ( 싱글톤 패턴 )

        String leftState;
        String rightState;
        String trafficNum;
        // BlockSeatInfo의 차량 번호 순으로 정렬 된 데이터에서 located 값을 통해 위치 구분
        if(BlockPagerAdapter.mArrayList.get(sectionNumber*2).getLocated().equals("0")) { // located 값이 0일 경우 왼쪽 좌석 정보가 먼저
            leftState = BlockPagerAdapter.mArrayList.get(sectionNumber*2).getState();
            rightState = BlockPagerAdapter.mArrayList.get(sectionNumber*2+1).getState();
        }
        else { // located 값이 0이 아닐 경우 오른쪽 좌석 정보가 먼저
            rightState = BlockPagerAdapter.mArrayList.get(sectionNumber*2).getState();
            leftState = BlockPagerAdapter.mArrayList.get(sectionNumber*2+1).getState();
        }
        trafficNum = BlockPagerAdapter.mArrayList.get(sectionNumber*2).getTraffic_num(); // 차량 번호 저장
        BlockFragment fragment = new BlockFragment(); // 생성
        Bundle args = new Bundle();

        args.putInt("num", sectionNumber+1); // 키 값 쌍
        args.putString("traffic_num", trafficNum); // 키 값 쌍
        args.putString("leftstate", leftState); // 키 값 쌍
        args.putString("rightstate", rightState); // 키 값 쌍

        fragment.setArguments(args); // 객체에 칸 번호, 차량 번호, 좌석 상태를 인자 값으로 설정

        return fragment; // 객체 반환

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable final Bundle savedInstanceState) {

        // 예약 버튼 초기 설정
        res_button = (TextView)getActivity().findViewById(R.id.res_button);
        res_button.setBackgroundResource(R.color.darker_gray);
        res_button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) { // 예약 버튼 클릭시 ProcessDatabaseReserveInfoThread 실행
                (new ProcessDatabaseReserveInfoThread()).execute(); // 서버의 예약 리스트 테이블을 조회하여 기존 예약되니 고객인지 판별
             }
        });
        res_button.setEnabled(false); // 디폴트 비활성화

        // 각 좌석의 상태가 0이 아닌 경우 라디오 버튼 숨기기
        View rootView = inflater.inflate(R.layout.activity_fragment, container, false);
        number = (TextView)rootView.findViewById(R.id.number);
        number.setText(Integer.toString(getArguments().getInt("num"))+"호차");

        // 각 좌석의 상태가 0이 아닌 경우 라디오 버튼 숨기기
        rleftseat = (RadioButton)rootView.findViewById(R.id.rleft_seat);
        if(!getArguments().getString("leftstate").equals("0")) {
            rleftseat.setVisibility(View.INVISIBLE);
        }
        rrightseat = (RadioButton)rootView.findViewById(R.id.rright_seat);
        if(!getArguments().getString("rightstate").equals("0")) {
            rrightseat.setVisibility(View.INVISIBLE);
        }

        // 라디오 버튼이 체크 되었을 때 설정
        radioGroup = (RadioGroup)rootView.findViewById(R.id.radiogroup);
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            public void onCheckedChanged(RadioGroup radioGroup, @IdRes int i) {
                if(i == R.id.rleft_seat) {
                    selectedSeat="0"; // 선택 좌석 위치 저장
                    res_button.setEnabled(true); // 예약 버튼 활성화
                    res_button.setBackgroundResource(R.color.colorPrimary);
                }
                else if(i == R.id.rright_seat) {
                    selectedSeat="1";
                    res_button.setEnabled(true);
                    res_button.setBackgroundResource(R.color.colorPrimary);
                }
            }
        });

        // 예약이나 자리가 있는 좌석일 때 라디오 버튼 대신 아이콘 표시
        this.leftseat = (ImageView) rootView.findViewById(R.id.left_seat);
        if(getArguments().getString("leftstate").equals("0")) {
            this.leftseat.setVisibility(View.INVISIBLE);
        } else if(getArguments().getString("leftstate").equals("1")) {
            this.leftseat.setImageResource(R.drawable.reserved_icon);
        } else {
            this.leftseat.setImageResource(R.drawable.full_icon);
        }
        this.rightseat = (ImageView) rootView.findViewById(R.id.right_seat);
        if(getArguments().getString("rightstate").equals("0")) {
            this.rightseat.setVisibility(View.INVISIBLE);
        } else if(getArguments().getString("rightstate").equals("1")) {
            this.rightseat.setImageResource(R.drawable.reserved_icon);
        } else {
            this.rightseat.setImageResource(R.drawable.full_icon);
        }

        // 팝업 창 설정
        alertDialogBuilder = new AlertDialog.Builder(getContext());
        alertDialogBuilder.setTitle("예약 확인");
        alertDialogBuilder.setCancelable(false);// 뒤로 가기 버튼 막기
        alertDialogBuilder.setNegativeButton("취소", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.cancel(); // 팝업 창 종료
            }
        });
        alertDialogBuilder.setPositiveButton("예약", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {

                (new InsertDatabaseSeatInfoThread()).execute(); // 서버의 예약 리스트 테이블에 예약 정보 입력
                save_Sdb() ; // 사용자 내부 DB에 예약 테이블 생성 및 정보 입력

                // 예약됐음을 정보로 주고 액티비티 종료
                Intent intent = new Intent(getContext(),MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.putExtra("reserved", true);
                getActivity().startActivity(intent);
            }
        });
        return rootView; // 프레그먼트 뷰 반환
    }
    public class ProcessDatabaseReserveInfoThread extends AsyncTask<String, Void, String> { // 서버의 예약 리스트 테이블을 조회하여 기존 예약되니 고객인지 판별
        String errorString = null;

        protected void onPostExecute(String result) {
            mJsonString = result;
            if(mJsonString.isEmpty()) { // 사용자 id를 통한 조회 결과가 없을 경우
                alertDialogBuilder.setMessage("예약 하시겠습니까?");
            }
            else { // 사용자 id를 통한 조회 결과가 있을 경우
                alertDialogBuilder.setMessage("기존 예약이 있습니다. 새로 예약 하시겠습니까?");
            }
            ad = alertDialogBuilder.create();
            ad.show(); // 팝업 창 보여주기
        }

        @Override
        protected String doInBackground(String... strings) {
            return executeClient();
        }

        public String executeClient() {
            // 예약 리스트 테이블에서 사용자 id를 조건으로 찾아 조회
            String serverURL = "http://" + IP_ADDRESS + "/checkres.php";
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

    } // 서버의 예약 리스트 테이블을 조회하여 기존 예약되니 고객인지 판별

    public class InsertDatabaseSeatInfoThread extends AsyncTask<String, Void, String> { // 서버의 예약 리스트 테이블에 예약 정보 입력
        String errorString = null;

        protected void onPostExecute(String result) {
            Log.d("tTTt", "onPostExecute: "+selectedSeat);
            (new UpdateDatabaseSeatInfoThread()).execute(BlockPagerAdapter.mArrayList.get(SeatDetailActivity.block_num*2).getTraffic_num(),selectedSeat);
            // 서버의 열차 칸별 테이블에 예약 정보 갱신
        }

        @Override
        protected String doInBackground(String... strings) {
            executeClient(strings);
            return "";
        }

        public void executeClient(String[] strings) {

            // 도착 예정 시간이 너무 짧을 경우 60초로 설정
            if( Integer.parseInt(TrafficSubwayInfoTypeB.nbarvlDt)<=30) {
                TrafficSubwayInfoTypeB.nbarvlDt="60";
            }

            // 열차 번호, 아두이노(리더기) id, 차량 번호, 좌석 위치, 역 이름, 다음 역, 호선 id, 사용자 id, 첫번째 차량번호, 남은 도착 예정 시간을 예약 리스트 테이블에 저장
            String serverURL = "http://" + IP_ADDRESS + "/putres.php";
            String postParameters = "train_num=" + TrafficSubwayInfoTypeB.nbtrainNo + "&" +
                    "reader_id=" + BlockPagerAdapter.mArrayList.get(SeatDetailActivity.block_num*2).getReader_id() + "&" +
                    "traffic_num=" + BlockPagerAdapter.mArrayList.get(SeatDetailActivity.block_num*2).getTraffic_num() + "&" +
                    "located=" +  selectedSeat + "&" +
                    "station_name=" + TrafficSubwayInfoTypeB.stationNM + "&" +
                    "next_station=" + TrafficSubwayInfoTypeB.nStation + "&" +
                    "drawable_id=" + SeatActivity.drawableId + "&" +
                    "user_id=" + user_id + "&" +
                    "first_traffic_num=" + SeatActivity.first_traffic_num + "&" +
                    "arrival_time=" + TrafficSubwayInfoTypeB.nbarvlDt;

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
            } catch (Exception e) {
                errorString = e.toString();
            }
        }

    }

    private void save_Sdb() { // 사용자 내부 DB에 예약 테이블 생성 및 정보 입력

        SQLiteDatabase db = dbHelper.getWritableDatabase() ;
        db.execSQL(ContractDB.SQL_DROP_RES_TBL);
        db.execSQL(ContractDB.SQL_CREATE_RES_TBL);

        String time = new SimpleDateFormat( "yyyy-MM-dd HH:mm:ss").format(new Date()); // 현재 시간 추출
        // 역 이름, 다음 역, 호선 id, 예약 시간, 예약 좌석 위치, 칸 번호, 사용자 id 정보를 예약 테이블에 입력
        String sqlInsert = ContractDB.SQL_INSERT_RES_TBL + " (" +
                "'" + TrafficSubwayInfoTypeB.stationNM + "'," +
                "'" + TrafficSubwayInfoTypeB.nStation + "'," +
                SeatActivity.drawableId + "," +
                "'" + time + "'," +
                "'" + selectedSeat + "'," +
                "'" + Integer.toString(SeatDetailActivity.block_num+1) + "'," +
                "'" + BlockPagerAdapter.mArrayList.get(SeatDetailActivity.block_num*2).getReader_id() + "'," +
                "'" + user_id + "')" ;

        db.execSQL(sqlInsert) ;
    } // 사용자 내부 DB에 예약 테이블 생성 및 정보 입력

    public class UpdateDatabaseSeatInfoThread extends AsyncTask<String, Void, String> { // 서버의 열차 칸별 테이블에 예약 정보 갱신
        String errorString = null;
        protected void onPostExecute(String result) {
            ad.dismiss();
        }

        @Override
        protected String doInBackground(String... strings) {
            executeClient(strings);
            return "";
        }

        public void executeClient(String[] strings) {

            // 첫번재 차량 번호로 찾은 열차 칸별 테이블에서 차량 번호와 좌석 위치로 예약 좌석을 찾아 좌석 상태와 예약 사용자 id를 갱신
            String serverURL = "http://" + IP_ADDRESS + "/setres.php";
            String postParameters = "traffic_num=" + strings[0] + "&" + "first_traffic_num=" + SeatActivity.first_traffic_num + "&" + "located=" + strings[1] + "&" + "user_id=" + user_id;
            Log.d("tTTt", "executeClient: "+strings[0]+"first_traffic_num=" + SeatActivity.first_traffic_num + "&" + "located=" + strings[1] + "&" + "user_id=" + user_id);
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
            } catch (Exception e) {
                errorString = e.toString();
            }
        }

    } // 서버의 열차 칸별 테이블에 예약 정보 갱신

}
