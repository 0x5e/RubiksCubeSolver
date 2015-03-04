package com.RubiksCubeSolver;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import org.kociemba.twophase.Tools;

public class MainActivity extends Activity {
    TextView textview1;
    EditText edittext1;
    FrameLayout cube;
    boolean BTstate;
    volatile public static Boolean connected = false;
    public static OutputStream outStream;
    public static InputStream inStream;
    public static String cubeString ="";
    public static String result ="";
    public static String status ="";
    Thread mthread;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);// 保持屏幕亮
        setContentView(R.layout.main);
        
        textview1 = (TextView) findViewById(R.id.TextView1);
        edittext1 = (EditText) findViewById(R.id.EditText1);
        cube = (FrameLayout) findViewById(R.id.cube_preview);

        BluetoothActivity.bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        BTstate = BluetoothActivity.bluetoothAdapter.isEnabled();

        Solve.clean();
        cubeString = Solve.ReadColors();
        cube.addView(new DrawView(MainActivity.this,1));
        
        //串口发送
        findViewById(R.id.btn2).setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                try {                		             	
                    if(connected){
                        String tx = String.valueOf(edittext1.getText()) ;
                        outStream.write(tx.getBytes());
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

        //串口接收进程
        mthread = new Thread(new Runnable(){
            @Override
            public void run(){
                byte buffer[] = new byte[1024];
                connected = true;
                while (connected) {
                    //connected = BluetoothActivity.btSocket.isConnected();
                    try {
                        int len = inStream.read(buffer);
                        String rx = new String(buffer).substring(0, len);
                        receive.append(rx);
                        mHandler.obtainMessage(1).sendToTarget();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    StringBuilder receive = new StringBuilder("");
    Handler mHandler = new Handler() {
    	public void handleMessage(Message msg) {//此方法在ui线程运行
            switch (msg.what) {
                case 1:
                    textview1.setText("Connected!\r\nRead:\r\n" + receive);
                    break;
                case 2:
                	cube.addView(new DrawView(MainActivity.this,1));
                	break;
                default:
                    break;
            }
        }
    };

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        if(100 == resultCode){//蓝牙
            mthread.start();
            Toast.makeText(MainActivity.this, "已连接", Toast.LENGTH_LONG).show();
        }else if(200 == resultCode){//拍照
        	cubeString = Solve.ReadColors();
        	result ="Solving...";
        	status="状态: ";
            if(Tools.verify(Solve.ReadColors2())!=0)
             	status += "无";
            else{
            	status += cubeString;
            	status += "\n\n解法: " + result;
            
	        	new Thread(new Runnable(){
	        		@Override
	        		public void run(){
	        			result=Jaap.GetResult(cubeString);
	        			//result=Sirgedas.GetResult(cubeString);
	        			/*
	        			cubeString = Solve.SolveReadColors2();
	        			cubeString = Tools.randomCube();
	        			int maxDepth =24;
	        			int maxTime =5;
	        			result = Search.solution(cubeString, maxDepth, maxTime, false);
	        			while(result.equals("Solving..."))
	        				;
	        			*/
	        			status = "状态: " + cubeString +"\n\n解法: " + result;
	        			mHandler.obtainMessage(2).sendToTarget();
	        		}
	        	}).start();
            }
        }
        cube.addView(new DrawView(MainActivity.this,1));
        super.onActivityResult(requestCode, resultCode, data);
    }

    private long exitTime = 0;
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN){
            if((System.currentTimeMillis()-exitTime) > 2000){
                Toast.makeText(MainActivity.this, "再按一次退出程序", Toast.LENGTH_SHORT).show();
                exitTime = System.currentTimeMillis();
            } else {
                if(!BTstate)
                    BluetoothActivity.bluetoothAdapter.disable();
                finish();
                System.exit(0);
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.add(0,1,1, "蓝牙").setIcon(android.R.drawable.stat_sys_data_bluetooth);
        menu.add(0,2,2, "拍照").setIcon(android.R.drawable.ic_menu_camera);
        return super.onCreateOptionsMenu(menu);
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
	        case 1:
	        	//蓝牙
	        	startActivityForResult(new Intent(MainActivity.this,BluetoothActivity.class),100);
	            break;
	        case 2:
	        	//拍照
                startActivityForResult(new Intent(MainActivity.this,CameraUseActivity.class),200);
	        	break;
	        default:
	            break;
        }
        return super.onOptionsItemSelected(item);
    }
}
