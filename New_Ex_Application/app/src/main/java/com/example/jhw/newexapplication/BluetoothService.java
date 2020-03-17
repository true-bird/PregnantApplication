package com.example.jhw.newexapplication;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.widget.Toast;

import java.util.UUID;
import java.util.function.ToLongBiFunction;


public class BluetoothService {

    private static final int REQUEST_CONNECT_DEVICE = 1;
    private static final int REQUEST_ENABLE_BT= 2;

    private static final String TAG = "BluetoothService";
    private static final UUID MY_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

    private BluetoothAdapter btAdapter;
    private Activity mActivity;
    private Handler mHandler;

    public BluetoothService(Activity activity,Handler handler) {
        mActivity = activity;
        mHandler = handler;
        btAdapter = BluetoothAdapter.getDefaultAdapter();
    }

    public boolean getDeviceState()
    {
        if(btAdapter==null)
        {
            return false;
        }
        else
        {
            return true;
        }
    }

    public void enableBluetooth()
    {
        Intent serverIntent = new Intent(mActivity, DeviceListActivity.class);
        if(btAdapter.getState() != BluetoothAdapter.STATE_TURNING_ON || btAdapter.getState() != btAdapter.STATE_ON){
            btAdapter.enable();
            serverIntent.putExtra("needloading", true);
        }
        mActivity.startActivityForResult(serverIntent,REQUEST_CONNECT_DEVICE);

        //scanDevice();
    }



    public void scanDevice() {
        Intent serverIntent = new Intent(mActivity, DeviceListActivity.class);
        mActivity.startActivityForResult(serverIntent,REQUEST_CONNECT_DEVICE);
    }





/*
    IntentFilter stateFilter = new IntentFilter();
    stateFilter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED); //BluetoothAdapter.ACTION_STATE_CHANGED : 블루투스 상태변화 액션
    registerReceiver(mBluetoothStateReceiver, stateFilter);
    //리시버2
    IntentFilter searchFilter = new IntentFilter();
        searchFilter.addAction(BluetoothAdapter.ACTION_DISCOVERY_STARTED); //BluetoothAdapter.ACTION_DISCOVERY_STARTED : 블루투스 검색 시작
        searchFilter.addAction(BluetoothDevice.ACTION_FOUND); //BluetoothDevice.ACTION_FOUND : 블루투스 디바이스 찾음
        searchFilter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED); //BluetoothAdapter.ACTION_DISCOVERY_FINISHED : 블루투스 검색 종료
    registerReceiver(mBluetoothSearchReceiver, searchFilter);



    BroadcastReceiver mBluetoothStateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            //BluetoothAdapter.EXTRA_STATE : 블루투스의 현재상태 변화
            int state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, -1);

            //블루투스 활성화
            if(state == BluetoothAdapter.STATE_ON){
                Log.d(TAG, "onReceive: 블루투스 활성화");
            }
            //블루투스 활성화 중
            else if(state == BluetoothAdapter.STATE_TURNING_ON){
                Log.d(TAG, "onReceive: 블루투스 활성화 중..");
            }
            //블루투스 비활성화
            else if(state == BluetoothAdapter.STATE_OFF){
                Log.d(TAG, "onReceive: 블루투스 비활성화");
            }
            //블루투스 비활성화 중
            else if(state == BluetoothAdapter.STATE_TURNING_OFF){
                Log.d(TAG, "onReceive: 블루투스 비활성화 중..");
            }
        }
    };
*/


}
