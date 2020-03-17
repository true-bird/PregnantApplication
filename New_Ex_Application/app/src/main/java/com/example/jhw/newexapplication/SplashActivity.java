package com.example.jhw.newexapplication;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import static com.example.jhw.newexapplication.MainActivity.IP_ADDRESS;

public class SplashActivity extends AppCompatActivity {

    private ContractDBHelper dbHelper = null ;
    private Intent intent;
    private Cursor cursor;

    @Override
    protected void onCreate(Bundle savedInstanceState) { // 액티비티 호출 시 맨 처음 실행
        super.onCreate(savedInstanceState);
        // 사용자 단말기 내부 DB를 불러와 USER TABLE을 cursor에 담음
        dbHelper = new ContractDBHelper(this);
        SQLiteDatabase db = dbHelper.getWritableDatabase() ;
        db.execSQL(ContractDB.SQL_CREATE_USER_TBL);
        cursor = db.rawQuery(ContractDB.SQL_SELECT_USER_TBL, null) ;

        if (cursor.moveToNext()) { // 레코드가 존재한다면
            (new SplashActivity.ProcessDatabaseUserInfoThread()).execute(cursor.getString(0)); // curosr의 0 인덱스 값(임산부 번호)을 넘겨주어 ProcessDatabaseUserInfoThread 를 실행
        } else { // 레코드가 존재하지 않는 다면 ( 유저 정보가 없다면 )
            intent = new Intent(this, CertifyActivity.class); // CertifyActivity를 실행
            startActivity(intent);
            finish();
        }
    }
    // 백그라운드로 php를 통해 DB에 접속
    public class ProcessDatabaseUserInfoThread extends AsyncTask<String, Void, String> { // 보건부 서버(임시)의 임산부 등록 리스트를 조회 및 대조
        String errorString = null;

        protected void onPostExecute(String result) { // doInBackground의 결과를 인자로 유효한 임산부 번호인지 확인
            if(!result.equals("")) { // 결과 값이 있을 경우 MainActivity를 실행
                intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);
                finish();
            } else { // 결과 값이 없을 경우 CertifyActivity를 실행
                intent = new Intent(getApplicationContext(), CertifyActivity.class);
                startActivity(intent);
                finish();
            }
        }
        @Override
        protected String doInBackground(String... strings) { // 클래스 실행시 받은 인자 값을 가지고 백그라운드에서 실행
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

}