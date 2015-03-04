package com.RubiksCubeSolver;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class BluetoothActivity extends Activity {
    private static final UUID SERIAL_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    public static BluetoothAdapter bluetoothAdapter;
    public static BluetoothSocket btSocket = null;
    public ArrayAdapter<String> arrayadapter;
    BroadcastReceiver mreceiver;
    List<String> name = new ArrayList<String>();
    List<String> address = new ArrayList<String>();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.bluetooth);

        //注册广播接收器
        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        mreceiver = new BroadcastReceiver(){
            @Override
            public void onReceive(Context context, Intent intent) {
                String action = intent.getAction();
                if(BluetoothDevice.ACTION_FOUND.equals(action)){
                    //可以从收到的Intent对象当中，将代表远程蓝牙适配器的对象取出
                    BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                    if (device != null) {
                        name.add(device.getName());
                        address.add(device.getAddress());
                        arrayadapter.notifyDataSetChanged();
                    }
                }
            }
        };
        registerReceiver(mreceiver,filter); // 不要忘了之后解除绑定

        
        if(bluetoothAdapter != null){
            if(!bluetoothAdapter.isEnabled()){
                Intent intent=new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(intent,300);
            }
            else{
            	mThread.start();
            }
        }
        /*
        //扫描
        new Thread(new Runnable(){
            @Override
            public void run(){

                if(bluetoothAdapter != null){
                    if(!bluetoothAdapter.isEnabled()){
                        Intent intent=new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                        startActivityForResult(intent,300);
                    }
                    //bluetoothAdapter.enable();
                    while(!bluetoothAdapter.isEnabled());

                    if(!bluetoothAdapter.isDiscovering()){
                        print("正在扫描...");

                        name.clear();
                        address.clear();
                        //arrayadapter.notifyDataSetChanged();
                        bluetoothAdapter.startDiscovery();
                    } else{
                        print("扫描中,请稍候...");
                    }
                }
            }
        }).start();
         */
        
        arrayadapter = new ArrayAdapter<String>(
                BluetoothActivity.this, android.R.layout.simple_expandable_list_item_1, name
        );
        ListView listview1 = (ListView) findViewById(R.id.ListView1);
        listview1.setAdapter(arrayadapter);
        listview1.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg1, View arg2, final int arg3, long arg4) {
                print("正在连接" + name.get(arg3) + "...");
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            bluetoothAdapter.cancelDiscovery();
                            BluetoothDevice device = bluetoothAdapter.getRemoteDevice(address.get(arg3));
                            //配对
                            if (device.getBondState() == BluetoothDevice.BOND_NONE){
                                Method createBondMethod = BluetoothDevice.class.getMethod("createBond");
                                createBondMethod.invoke(device);
                            }
                            //串口连接
                            btSocket = device.createRfcommSocketToServiceRecord(SERIAL_UUID);
                            btSocket.connect();

                            MainActivity.outStream = btSocket.getOutputStream();
                            MainActivity.inStream = btSocket.getInputStream();
                            setResult(100, null);
                            finish();

                        } catch (NoSuchMethodException e) {
                            e.printStackTrace();
                        } catch (IllegalAccessException e) {
                            e.printStackTrace();
                        } catch (InvocationTargetException e) {
                            e.printStackTrace();
                        } catch (IOException e) {
                            e.printStackTrace();
                            try {
                                if (btSocket != null) {
                                    btSocket.close();
                                }
                            } catch (IOException e2) {
                                e2.printStackTrace();
                            }
                        }
                    }
                }).start();
            }
        });

    }

    Thread mThread=new Thread(new Runnable(){
        @Override
        public void run(){
     	   		
                //while(!bluetoothAdapter.isEnabled());

                //if(bluetoothAdapter.isDiscovering())
             	//  bluetoothAdapter.cancelDiscovery();
                
                //if(!bluetoothAdapter.isDiscovering()){
                    print("正在扫描...");

                    name.clear();
                    address.clear();
                    //arrayadapter.notifyDataSetChanged();
                    bluetoothAdapter.startDiscovery();
                //} 
        }
    });
    public void onActivityResult(int requestCode, int resultCode, Intent data){  
        if(requestCode == 300){  
               if(resultCode == RESULT_OK){  
                    //蓝牙已经开启   
                   //扫描
                   mThread.start();
               } else{
            	   finish();
               }
        }  
    }  
    
    String text;
    @SuppressLint("HandlerLeak")
	Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {//此方法在ui线程运行
            switch (msg.what) {
                case 1:
                    Toast.makeText(BluetoothActivity.this, text ,Toast.LENGTH_LONG).show();
                    break;
                default:
                    break;
            }
        }
    };
    
    void print(String str){
        text = str;
        mHandler.obtainMessage(1).sendToTarget();
    }
    
    @Override  
    protected void onDestroy() {  
          this.unregisterReceiver(mreceiver);  
          super.onDestroy();  
    } 
}