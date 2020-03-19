package com.example.jhw.newexapplication;

import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
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

import static com.example.jhw.newexapplication.MainActivity.IP_ADDRESS;
import static com.example.jhw.newexapplication.MainActivity.user_id;

public class CertifyActivity extends AppCompatActivity {

    private EditText pregnum;
    private Button certify;
    private String mJsonString;
    private String d_pregnum;
    private String d_name;
    private String d_pregdate;
    private CustomProgressDialog customProgressDialog;
    private ContractDBHelper dbHelper = null ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_certify);

        this.pregnum = (EditText)this.findViewById(R.id.pregnum);
        this.certify = (Button) this.findViewById(R.id.certi);
        this.certify.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) { // 인증 버튼 클릭시 이벤트
                if(pregnum.getText().toString().equals("")) { // 입력하지 않고 클릭한 경우
                    Toast.makeText(view.getContext(),"임산부 번호를 입력해주세요.",Toast.LENGTH_SHORT).show(); // 메시지 띄우기
                } else { // 메인 쓰레드에서느 로딩 UI를 보여주고 백그라운드에서는 ProcessDatabasePregInfoThread 실행
                    showCustomProgressDialog(); // 로딩 UI 보여주기
                    (new CertifyActivity.ProcessDatabasePregInfoThread()).execute(pregnum.getText().toString()); // 임산부 번호를 인자로 받아 ProcessDatabasePregInfoThread 실행
                }
            }
        });
    }

    private void showCustomProgressDialog() { // 로딩 UI 보여주기
        if (this.customProgressDialog == null) {
            this.customProgressDialog = new CustomProgressDialog(this);
            this.customProgressDialog.setCancelable(false); // 백버튼 막기
            this.customProgressDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT)); // 배경 투명
        }
        if (this.customProgressDialog != null) {
            this.customProgressDialog.show();
        }
    }
    private void cancelCustomProgressDialog() { // 로딩 UI 끄기
        if (this.customProgressDialog != null && this.customProgressDialog.isShowing()) {
            this.customProgressDialog.cancel();
        }
    }

    private void save_uDB() { // 인증된 임산부의 임산부 번호, 이름, 임신 날짜를 사용자 단말기 DB에 저장
        // 사용자 단말기 내부 DB를 불러와 USER TABLE을 삭제 후 새로 생성
        dbHelper = new ContractDBHelper(this);
        SQLiteDatabase db = dbHelper.getWritableDatabase() ;
        db.execSQL(ContractDB.SQL_DROP_USER_TBL);
        db.execSQL(ContractDB.SQL_CREATE_USER_TBL);
        // 임산부 번호와 이름, 임신 날짜를 USER TABLE에 저장함
        String sqlInsert = ContractDB.SQL_INSERT_USER_TBL + " (" +
                "'" + d_pregnum + "'," +
                "'" + d_name + "'," +
                "'" + d_pregdate + "')" ;
        db.execSQL(sqlInsert) ;
    }


    public class ProcessDatabasePregInfoThread extends AsyncTask<String, Void, String> { // 보건부 서버(임시)의 임산부 등록 리스트를 조회 및 대조
        String errorString = null;

        protected void onPostExecute(String result) { // doInBackground의 결과를 인자로 유효한 임산부 번호인지 확인
            mJsonString = result;
            if(mJsonString==null || mJsonString.equals("")) { // 결과 값이 없을 경우
                Toast.makeText(CertifyActivity.this,"등록되지 않은 임산부 번호입니다.",Toast.LENGTH_SHORT).show();
                cancelCustomProgressDialog(); // 로딩 UI 끄기
            } else { // 결과 값이 있을 경우
                showResult(); // php로 받아온 json형식의 문자열을 키를 통해 값을 추출해냄
                save_uDB(); // 인증된 임산부의 임산부 번호, 이름, 임신 날짜를 사용자 단말기 DB에 저장
                (new CertifyActivity.InsertDatabaseUserListThread()).execute(); // 인증된 임산부의 정보를 서버의 유저 리스트에 저장
            }

        }
        @Override
        protected String doInBackground(String... strings) {
            return executeClient(strings);
        }

        public String executeClient(String... strings) { // 서버의 php에 접속하여 임산부 등록 리스트에서 임산부 번호를 조회하여 결과값을 반환

            String serverURL = "http://" + IP_ADDRESS + "/checkpreg.php";
            String postParameters = "pregnum=" + strings[0];
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

    private void showResult() { // php로 받아온 json형식의 문자열을 키를 통해 값을 추출해냄
        // 임산부 번호, 이름, 임신 날짜를 추출하여 각각 d_pregnum, d_name, d_pregdate에 저장
        String TAG_JSON = "root";
        String TAG_PREGNUM = "pregnum";
        String TAG_NAME = "name";
        String TAG_PREGDATE = "pregdate";
        try {
            JSONObject jsonObject = new JSONObject(mJsonString);
            JSONArray jsonArray = jsonObject.getJSONArray(TAG_JSON);
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject item = jsonArray.getJSONObject(i);
                d_pregnum = item.getString(TAG_PREGNUM);
                d_name = item.getString(TAG_NAME);
                d_pregdate = item.getString(TAG_PREGDATE);
            }
        } catch (JSONException e) {
        }
    }

    public class InsertDatabaseUserListThread extends AsyncTask<String, Void, String> { // 인증된 임산부의 정보를 서버의 유저 리스트에 저장
        String errorString = null;
        protected void onPostExecute(String result) { // 저장 완료 후 로딩 UI 종료, 완료 메시지, MainActivity 실행
            cancelCustomProgressDialog(); // 로딩 UI 끄기
            Toast.makeText(CertifyActivity.this,"인증이 완료 되었습니다.",Toast.LENGTH_LONG).show();
            Intent intent = new Intent(CertifyActivity.this, MainActivity.class); // MainActivity 실행
            startActivity(intent);
        }

        @Override
        protected String doInBackground(String... strings) {
            executeClient();
            return "";
        }

        public void executeClient() { // d_pregnum, d_name, d_pregdate 값을 저장

            String serverURL = "http://" + IP_ADDRESS + "/putuser.php";
            String postParameters = "pregnum=" + d_pregnum + "&" +
                    "name=" + d_name + "&" +
                    "pregdate=" + d_pregdate;


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
            } catch (Exception e) {
                Log.d("test", "GetData : Error ", e);
                errorString = e.toString();
            }
        }

    }
}
