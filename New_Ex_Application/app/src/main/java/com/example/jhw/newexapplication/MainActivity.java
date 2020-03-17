package com.example.jhw.newexapplication;

import android.animation.ObjectAnimator;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;

import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Method;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Enumeration;
import java.util.Set;
import java.util.UUID;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    static String openAPIKey = "696e5247766a68773736546c797442"; // 서울 교통 공사 공공 데이터 오픈 api 인증키
    static String IP_ADDRESS = "13.125.152.123"; // 서버 ip 주소
    static String user_id = "";
    static ContractDBHelper dbHelper = null;
    static Boolean bluestate= false;
    static BluetoothDevice selecteddevice = null;

    private BluetoothAdapter mBtAdapter;
    private Set<BluetoothDevice> pairedDevices;

    private AlertDialog.Builder alertDialogBuilder;
    private TextView pregname;
    private TextView pregdate;
    private Button sub_button;
    private ImageView card_image;

    DrawerLayout drawer;

    private BluetoothService bluetoothService_obj = null;

    private final Handler mHandler = new Handler() { // 블루투스 서비스 핸들러
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        dbHelper = new ContractDBHelper(this); // 사용자 단말기 내부 DB 연결
        mBtAdapter = BluetoothAdapter.getDefaultAdapter(); // 블루투스 어댑터 연결

        // 툴바 및 드로워 세팅
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.setDrawerIndicatorEnabled(false);
        toolbar.setNavigationIcon(R.drawable.menu);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                drawer.openDrawer(Gravity.LEFT);
            }
        });
        toggle.syncState();

        // 네비게이션 뷰 세팅
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        View nav_header_view = navigationView.getHeaderView(0);
        pregname = (TextView) nav_header_view.findViewById(R.id.user_name);
        pregdate = (TextView) nav_header_view.findViewById(R.id.preg_info);

        load_uDB(); // 사용자 단말기 내부 DB 생성 및 학인

        // 예약 버튼 세팅
        this.sub_button = (Button)this.findViewById(R.id.sub);
        this.sub_button.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, SubActivity.class);
                startActivity(intent);
            }
        });


        this.bluetoothService_obj = new BluetoothService(this, mHandler); // 블루투스 서비스 객체 생성
        this.card_image = (ImageView)this.findViewById(R.id.card_image);
        this.card_image.setOnClickListener(new ImageView.OnClickListener() { // 카드 이미지 선택시
            @Override
            public void onClick(View v) {
                if(bluestate==false) { // 블루투스 연결이 되어 있지 않을 때
                    if(bluetoothService_obj.getDeviceState()) { // 블루투스 기기의 지원여부가 true 일때
                        bluetoothService_obj.enableBluetooth(); // 블루투스 활성화 시작.
                    } else {
                        Toast.makeText(getApplicationContext(), "블루투스를 지원하지 않는 단말기입니다.", Toast.LENGTH_SHORT).show();
                    }
                } else { // 블루투스 연결이 되어 있을 때
                    alertDialogBuilder = new AlertDialog.Builder(MainActivity.this);
                    alertDialogBuilder.setTitle("임산부석 반납");
                    alertDialogBuilder.setMessage("임산부석 반납 하시겠습니까?");
                    alertDialogBuilder.setNegativeButton("아니요", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.cancel();
                        }
                    });
                    alertDialogBuilder.setPositiveButton("예", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) { // 페이렁 취소
                            bluestate = false;
                            pairedDevices = mBtAdapter.getBondedDevices();
                            for (BluetoothDevice bt : pairedDevices) {
                                if (bt.getName().contains("pro")) {
                                    try {
                                        Method m = bt.getClass().getMethod("removeBond", (Class[]) null);
                                        m.invoke(bt, (Object[]) null);
                                    } catch (Exception e) {}
                                }
                            }
                            dialog.cancel();
                        }
                    });
                    alertDialogBuilder.show();
                }
            }
        });
    }

    private void load_uDB() { // 사용자 단말기 내부 DB 생성 및 학인

        SQLiteDatabase db = dbHelper.getReadableDatabase() ;
        db.execSQL(ContractDB.SQL_CREATE_USER_TBL);
        Cursor cursor = db.rawQuery(ContractDB.SQL_SELECT_USER_TBL, null) ;

        if (cursor.moveToNext()) { // 데이터가 존재한다면,
            user_id =  cursor.getString(0);
            long diff=0;
            this.pregname.setText(cursor.getString(1)+"님");
            try { // 현재 부터 임신 날짜까자 차이 계산
                diff = (new Date().getTime()-(new SimpleDateFormat("yyyy-MM-dd").parse(cursor.getString(2)).getTime()))/(24*60*60*1000);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            this.pregdate.setText("임신 후 "+Long.toString(diff)+"일");
        }
    }


    @Override
    public void onBackPressed() { // 네비게이션 드로워를 열었을 때 뒤로가기 버튼 구분

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            ActivityCompat.finishAffinity(this);
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) { // 네이게이션 메뉴

        int id = item.getItemId();
        if (id == R.id.check_reserve) { // 예약 확인 및 취소를 누를 경우
            Intent intent = new Intent(MainActivity.this, CheckReserve.class);
            startActivity(intent);
        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    protected void onResume() { // MainActivity로 돌아 왔을 때 메시지 구분
        super.onResume();
        Intent intent = getIntent();
        if(intent.getBooleanExtra("reserved",false)) {
            intent.putExtra("reserved",false);
            Toast.makeText(this,"예약이 되었습니다.",Toast.LENGTH_LONG).show();
        } else if(intent.getBooleanExtra("disconnect",false)) {
            intent.putExtra("disconnect",false);
            Toast.makeText(this,"블루투스 연결이 끊어졌습니다.",Toast.LENGTH_LONG).show();
        }
    }
}
