package com.example.jhw.newexapplication;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothClass;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Method;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public class DeviceListActivity extends AppCompatActivity {
    // Debugging
    private static final String TAG = "DeviceListActivity";
    private static final boolean D = true;

    public static String EXTRA_DEVICE_ADDRESS = "device_address";


    private BluetoothAdapter mBtAdapter;
    private ArrayAdapter<String> mPairedDevicesArrayAdapter;
    private ArrayAdapter<String> mNewDevicesArrayAdapter;
    private IntentFilter filter;
    private List<BluetoothDevice> bluetoothDevices;
    public static BluetoothDevice mRemoteDevice;


    private BluetoothDevice leftDevice;
    private BluetoothDevice rightDevice;
    private int devicecount = 0;
    private ListView newDevicesListView;
    private String readerid;



    private int selectDevice;

    private EditText mEditReceive;



    BluetoothSocket mSocket = null;
    OutputStream mOutputStream = null;
    InputStream mInputStream = null;
    String mStrDelimiter = "\n";
    char mCharDelimiter =  '\n';


    Thread mWorkerThread = null;
    byte[] readBuffer;
    int readBufferPosition;


    private ImageView leftseat;
    private ImageView rightseat;
    private TextView scanning;
    private TextView cancel;
    private TextView explain;
    private CustomProgressDialog customProgressDialog=null;
    private BluetoothDevice pairingdevice;



    BluetoothAdapter mBluetoothAdapter;

    private Set<BluetoothDevice> mDevices;
    private Set<BluetoothDevice> pairedDevices;




    private void load_Sdb() { // 사용자 내부 DB에 예약 테이블 조회
        SQLiteDatabase db = MainActivity.dbHelper.getReadableDatabase() ;
        db.execSQL(ContractDB.SQL_CREATE_RES_TBL);
        Cursor cursor = db.rawQuery(ContractDB.SQL_SELECT_RES_TBL, null) ;

        if (cursor.moveToNext()) { // 레코드가 존재한다면,
            if(cursor.getString(6).equals("a0001")) {
                this.readerid = "98:D3:32:30:BBD:8E";
            }
        }

    }

    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
                    String action = intent.getAction();


            // When discovery finds a device
                    if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                        BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);

                        Log.d(TAG, "onReceive!!: " + device.getAddress()+"/"+device.getName());
                        if (device.getAddress().equals("98:D3:32:30:BD:8E")) { // 아두이노(리더기) id
                            Log.d(TAG, "onReceive!!@@: " + device.getName());
                            if (device.getName().contains("rpro1")) {
                                if (device.getBondState() != BluetoothDevice.BOND_BONDED) {
                                    mNewDevicesArrayAdapter.add(device.getName() + "\n" + device.getAddress());
                                    bluetoothDevices.add(device);
                                    leftDevice = device;
                                    leftseat.setVisibility(View.VISIBLE);
                                    devicecount++;
                                }
                            } else if (device.getName().contains("rpro2")) {
                                if (device.getBondState() != BluetoothDevice.BOND_BONDED) {
                                    mNewDevicesArrayAdapter.add(device.getName() + "\n" + device.getAddress());
                                    bluetoothDevices.add(device);
                                    rightDevice = device;
                                    rightseat.setVisibility(View.VISIBLE);
                                    devicecount++;
                                }
                            }
                        } else if (device.getName() != null) {
                            if (device.getName().contains("dpro1")) {
                                if (device.getBondState() != BluetoothDevice.BOND_BONDED) {
                                    mNewDevicesArrayAdapter.add(device.getName() + "\n" + device.getAddress());
                                    bluetoothDevices.add(device);
                                    leftDevice = device;
                                    leftseat.setVisibility(View.VISIBLE);
                                    devicecount++;
                                }
                            } else if (device.getName().contains("dpro2")) {
                                if (device.getBondState() != BluetoothDevice.BOND_BONDED) {
                                    mNewDevicesArrayAdapter.add(device.getName() + "\n" + device.getAddress());
                                    bluetoothDevices.add(device);
                                    rightDevice = device;
                                    rightseat.setVisibility(View.VISIBLE);
                                    devicecount++;
                                }
                            }
                            Log.d(TAG, "onReceive: " + devicecount);
                            if (devicecount > 1) {
                                mBtAdapter.cancelDiscovery();
                            }
                        }
                    } else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {
                        //setProgressBarIndeterminateVisibility(false);
                        //setTitle(R.string.select_device);

                        cancel.setVisibility(View.INVISIBLE);
                        if(devicecount<1) {
                            scanning.setText("임산부석을 찾지 못했습니다.");
                        } else {
                            scanning.setVisibility(View.INVISIBLE);
                        }
                    } else if (BluetoothDevice.ACTION_PAIRING_REQUEST.equals(action)) {
                        pairingdevice = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                        if (pairingdevice.getName() != null) {
                            if (pairingdevice.getName().contains("pro")) {
                                explain.setVisibility(View.VISIBLE);
                                Log.d(TAG, "ACTION_PAIRING_REQUEST : " + pairingdevice.getName());
                                try {
                                    byte[] pinBytes = {0x38, 0x38, 0x37, 0x37};
                                    pairingdevice.setPin(pinBytes);
                                } catch (Exception e) {
                                    Log.e(TAG, "Error occurs when trying to auto pair");
                                    e.printStackTrace();
                                }
                            }
                        }
                    } else if (BluetoothDevice.ACTION_BOND_STATE_CHANGED.equals(action)) {
                        BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                        if (device.getName() != null) {
                            Log.d(TAG, "dododo");
                                final int bond_state        = intent.getIntExtra(BluetoothDevice.EXTRA_BOND_STATE, BluetoothDevice.ERROR);
                                final int bond_prevState    = intent.getIntExtra(BluetoothDevice.EXTRA_PREVIOUS_BOND_STATE, BluetoothDevice.ERROR);
                            Log.d(TAG, "ACTION_BOND_STATE_CHANGED(bond_state) : " + bond_state);
                            Log.d(TAG, "ACTION_BOND_STATE_CHANGED(bond_prevState) : " + bond_prevState);
                                if (bond_state == BluetoothDevice.BOND_BONDED && bond_state != bond_prevState) {
                                    Log.d(TAG, "Paired");

                                    MainActivity.selecteddevice = device;
                                    Toast.makeText(getApplicationContext(),"연결이 정상적으로 되었습니다.",Toast.LENGTH_LONG).show();
                                    connectToSelectedDevices(device.getName());
                                    sendData("pp\n");
                                    Intent gintent = new Intent(getApplicationContext(), MainActivity.class);
                                    gintent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                    MainActivity.bluestate = true;
                                    startActivity(gintent);

                                    //Intent newintent = new Intent(DeviceListActivity.this, BtcontrolActivity.class);
                                    //startActivity(newintent);
                                    // BondDeviceConnect();
                                } else if (bond_state == BluetoothDevice.BOND_NONE && bond_state != bond_prevState) {
                                    Log.d(TAG, "Unpaired");
                                    // m_BondState = STATE_NONE;
                                }
                        }
                    } else if (BluetoothDevice.ACTION_ACL_DISCONNECTED.equals(action)) {
                        BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                        if (device.getName() != null) {
                            if (device.getName().contains("pro")) {
                                Intent gintent = new Intent(getApplicationContext(), MainActivity.class);
                                gintent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                gintent.putExtra("disconnect", true);
                                MainActivity.bluestate = false;
                                startActivity(gintent);
                            }
                        }
                    }

            /*
            String action = intent.getAction();
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                Log.d("ghcn", "ghcnf3");
                //bluetooth device found
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                Log.d("device was searched", device.getName());
                Toast.makeText(DeviceListActivity.this,"Found device " + device.getName(), Toast.LENGTH_SHORT).show();
            }
            */
        }
    };







    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_device_list);


        mPairedDevicesArrayAdapter = new ArrayAdapter<String>(this, R.layout.device_name);
        mNewDevicesArrayAdapter = new ArrayAdapter<String>(this, R.layout.device_name);
        bluetoothDevices = new ArrayList<>();

        this.newDevicesListView = (ListView) findViewById(R.id.new_devices);
        newDevicesListView.setAdapter(mNewDevicesArrayAdapter);
        newDevicesListView.setOnItemClickListener(mDeviceClickListener);

        this.explain = (TextView) this.findViewById(R.id.explain);
        this.explain.setVisibility(View.INVISIBLE);

        this.cancel = (TextView) this.findViewById(R.id.scan_cancel_button);
        this.cancel.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                mBtAdapter.cancelDiscovery();
                finish();
            }
        });
        this.scanning = (TextView) this.findViewById(R.id.title_paired_devices);
        this.leftseat = (ImageView) this.findViewById(R.id.check_left_seat);
        this.rightseat = (ImageView) this.findViewById(R.id.check_right_seat);
        this.leftseat.setImageResource(R.drawable.full_icon);
        this.rightseat.setImageResource(R.drawable.full_icon);
        this.leftseat.setVisibility(View.INVISIBLE);
        this.rightseat.setVisibility(View.INVISIBLE);
        this.leftseat.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                try {
                    Method method = leftDevice.getClass().getMethod("createBond", (Class[]) null);
                    method.invoke(leftDevice, (Object[]) null);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                mBtAdapter.cancelDiscovery();
            }
        });
        this.rightseat.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                try {
                    Method method = rightDevice.getClass().getMethod("createBond", (Class[]) null);
                    method.invoke(rightDevice, (Object[]) null);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                mBtAdapter.cancelDiscovery();
            }
        });

        setResult(Activity.RESULT_CANCELED);


        ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION}, 1);
        filter = new IntentFilter();
        filter.addAction(BluetoothDevice.ACTION_FOUND);
        filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        filter.addAction(BluetoothDevice.ACTION_PAIRING_REQUEST);
        filter.addAction(BluetoothDevice.ACTION_BOND_STATE_CHANGED);
        filter.addAction(BluetoothDevice.ACTION_ACL_DISCONNECTED);
        this.registerReceiver(mReceiver, filter);

        mBtAdapter = BluetoothAdapter.getDefaultAdapter();
        Set<BluetoothDevice> pairedDevices = mBtAdapter.getBondedDevices();

        // If there are paired devices, add each one to the ArrayAdapter
        if (pairedDevices.size() > 0) {
            for (BluetoothDevice device : pairedDevices) {
                mPairedDevicesArrayAdapter.add(device.getName() + "\n" + device.getAddress());
            }
        } else {
            String noDevices = getResources().getText(R.string.none_paired).toString();
            mPairedDevicesArrayAdapter.add(noDevices);
        }

        if (this.getIntent() != null && this.getIntent().getBooleanExtra("needloading",false) != false) {
            final Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    doDiscovery();
                }
            }, 700 );
        } else {
            doDiscovery();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d("ghcn", "ghcnf");
        try {
        //    this.unregisterReceiver(mReceiver);
        } catch (IllegalArgumentException e) {
        } catch (Exception e) {
        } finally {
        }
        //finish();
    }

    protected void onDestroy() {

        if (mBtAdapter != null) {
            mBtAdapter.cancelDiscovery();
        }
        try {
            mWorkerThread.interrupt(); // 데이터 수신 쓰레드 종료
            mInputStream.close();
            mSocket.close();
        } catch (Exception e) {}
        super.onDestroy();
    }

    private void doDiscovery() {
        if (D) Log.d(TAG, "doDiscovery()");

//        setProgressBarIndeterminateVisibility(true);
//        setTitle(R.string.scanning);


        // If we're already discovering, stop it
        if (mBtAdapter.isDiscovering()) {
            //mBtAdapter.cancelDiscovery();
        }
        pairedDevices = mBtAdapter.getBondedDevices();
        for (BluetoothDevice bt : pairedDevices) {
            if (bt.getName().contains("pro")) {
                try {
                    Method m = bt.getClass().getMethod("removeBond", (Class[]) null);
                    m.invoke(bt, (Object[]) null);
                } catch (Exception e) { Log.e(TAG, e.getMessage()); }
            }
        }
        mBtAdapter.startDiscovery();
    }

    BluetoothDevice getDeviceFromBondedList(String name) {
        BluetoothDevice selectedDevice = null;
        mDevices = mBtAdapter.getBondedDevices();
        for(BluetoothDevice device : mDevices) {
            if(name.equals(device.getName())) {
                selectedDevice = device;
                break;
            }
        }
        return selectedDevice;
    }
    void sendData(String msg) {
        msg += mStrDelimiter;  // 문자열 종료표시 (\n)
        try{
            // getBytes() : String을 byte로 변환
            // OutputStream.write : 데이터를 쓸때는 write(byte[]) 메소드를 사용함.
            // byte[] 안에 있는 데이터를 한번에 기록해 준다.
            Log.d(TAG, "sendData: "+msg);
            mOutputStream.write(msg.getBytes());  // 문자열 전송.
            Intent Intent = new Intent(DeviceListActivity.this, BtcontrolActivity.class);
            //startActivity(Intent);
        }catch(Exception e) {  // 문자열 전송 도중 오류가 발생한 경우
            Toast.makeText(getApplicationContext(), "연결중 오류 발생",
                    Toast.LENGTH_LONG).show();
            finish();  // App 종료
        }
    }

    private void connectToSelectedDevices(String selectedDeviceName) {
        mRemoteDevice = getDeviceFromBondedList(selectedDeviceName);
        UUID uuid = java.util.UUID.fromString("00001101-0000-1000-8000-00805f9b34fb");

        try {
            // 소켓 생성
            mSocket = mRemoteDevice.createRfcommSocketToServiceRecord(uuid);
            // RFCOMM 채널을 통한 연결
            mSocket.connect();

            // 데이터 송수신을 위한 스트림 열기
            mOutputStream = mSocket.getOutputStream();
            mInputStream = mSocket.getInputStream();
            // 데이터 수신 준비

        }catch(Exception e) {
            Log.d(TAG, "connectToSelectedDevices: error");
            finish();   // 어플 종료
        }
    }

    private AdapterView.OnItemClickListener mDeviceClickListener = new AdapterView.OnItemClickListener() {
        public void onItemClick(AdapterView<?> av, View v, int arg2, long arg3) {
            // Cancel discovery because it's costly and we're about to connect

            BluetoothDevice device = bluetoothDevices.get(arg2); // position

            try {
                //선택한 디바이스 페어링 요청
                Method method = device.getClass().getMethod("createBond", (Class[]) null);
                method.invoke(device, (Object[]) null);
            } catch (Exception e) {
                e.printStackTrace();
            }
            mBtAdapter.cancelDiscovery();
            /*
            // Get the device MAC address, which is the last 17 chars in the View
            String info = ((TextView) v).getText().toString();
            String address = info.substring(info.length() - 17);

            Intent intent = new Intent();
            intent.putExtra(EXTRA_DEVICE_ADDRESS, address);
            setResult(Activity.RESULT_OK, intent);
            finish();
            */
        }
    };

}

