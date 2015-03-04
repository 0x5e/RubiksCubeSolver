package com.RubiksCubeSolver;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.hardware.Camera;
import android.hardware.Camera.AutoFocusCallback;
import android.hardware.Camera.PictureCallback;
import android.media.AudioManager;
import android.os.Bundle;
import android.util.SparseArray;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import java.util.Arrays;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

@SuppressLint("UseSparseArrays")
public class CameraUseActivity extends Activity {
    /** ����ͷ��Ķ��� **/
    Camera mCamera;
    /** SurfaceView���� **/
    CameraPreview mPreview;
    DrawView mDrawView;
    FrameLayout preview;
    AudioManager mAudioManager;
    int ringerMode;
    static int step;
    static boolean auto=true;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Window window = getWindow();// �õ�����
        requestWindowFeature(Window.FEATURE_NO_TITLE);// û�б���
        window.setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
        );// ����ȫ��
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);// ������Ļ��
        setContentView(R.layout.camera);

        /*
        //����
        mAudioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        ringerMode = mAudioManager.getRingerMode();
        mAudioManager.setRingerMode(AudioManager.RINGER_MODE_SILENT);
         */
        step=0;
        Solve.clean();

        // ��ȡCamera�����ʵ��
        mCamera = Camera.open();
        // ��ʼ��SurfaceView
        mPreview = new CameraPreview(this, mCamera);
        preview = (FrameLayout) findViewById(R.id.camera_preview);
        // ��SurfaceView��ӵ�FrameLayout��
        preview.addView(mPreview);
        // �������������
        Camera.Parameters params = mCamera.getParameters();
        //��������ֱ���Ϊ���
        List<Camera.Size> sizes = params.getSupportedPictureSizes();
        int height[] = new int[sizes.size()];
        //Map<Integer, Integer> map = new HashMap<Integer, Integer>();
        SparseArray<Integer> map = new SparseArray<Integer>();
        for (int i = 0; i < sizes.size(); i++) {
            Camera.Size size = sizes.get(i);
            int sizeheight = size.height;
            int sizewidth = size.width;
            height[i] = sizeheight;
            map.put(sizeheight, sizewidth);
        }
        Arrays.sort(height);
        params.setPictureSize(map.get(height[0]) ,height[0]);
        params.setPreviewSize(map.get(height[0]) ,height[0]);

        params.setJpegQuality(100);
        params.setFlashMode(Camera.Parameters.FLASH_MODE_OFF);
        mCamera.setParameters(params);

        //��׼��
        mDrawView = new DrawView(this,0);
        preview.addView(mDrawView);

    }

    Bitmap bitmap;
    // PictureCallback�ص�����ʵ��
    private PictureCallback mPicture = new PictureCallback() {
        @Override
        public void onPictureTaken(byte[] data, Camera camera) {
        	 //��ȡ�Ÿ���ɫ
            Solve.color[0] = getcolors(data);
            
            switch(step){
	            case 0:
	            	Solve.RotateCubeB(false);
	            	break;
	            case 1:
	            	Solve.RotateCubeB(false);
	            	break;
	            case 2:
	            	Solve.RotateCubeB(false);
	            	break;
	            case 3:
	            	Solve.RotateCubeA(false);
	            	Solve.RotateCubeB(false);
	            	break;
	            case 4:
	            	Solve.RotateCubeB(false);
	            	Solve.RotateCubeB(false);
	            	break;
	            case 5:
	            	FixCenterColor();
	            	exit();
	            	break;
            }
            step++;

        }
    };
  
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        // ������Ļ�Զ��Խ�
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            // �������Զ��Խ�
            mCamera.autoFocus(new AutoFocusCallback() {
                @Override
                public void onAutoFocus(boolean success,Camera camera) {}
            });
			for(int i=0;i<6;i++){
				int delay=1750;
			     // 1.75�����������
			     new Timer().schedule(new TimerTask() {
			            @Override
			            public void run() {
			                // ���պ���
			                //mCamera.takePicture(mShutter, null, mPicture);
			                mCamera.takePicture(null, null, mPicture);
			                //�˳�preview
			                mCamera.startPreview();
			            }
			        }, delay*i);
			     if(!auto) 	 		break;
			} 	
        }
        return super.onTouchEvent(event);
    }

    @Override
    protected void onPause() {
        super.onPause();
        // �ͷ�Camera�������ʵ�֣�
		if(mCamera!=null){
            mCamera.release();
		}
    }

    public static char[] getcolors(byte[] data){
    	Bitmap img = BitmapFactory.decodeByteArray(data, 0, data.length, null);
    	char color[] = new char[9];
        int height = img.getHeight();
        int width  = img.getWidth();
        int x = height/6 + (width-height)/2;
        int y = height/6;
        int pix[]  = new int[width * height];
        img.getPixels(pix, 0, width, 0, 0, width, height);
       	img.recycle();
        for(int j=0;j<3;j++)
            for(int i=0;i<3;i++){
                int k = j*3+2-i;
                int index = (y + i*height/3) * width + (x + j*height/3);
                int grey = 100;
                	
                if(Color.red(pix[index]) > grey  && Color.green(pix[index]) > grey  && Color.blue(pix[index]) > grey)
                    color[k]='W';//WHITE
                else{                	
                    float R,O,Y,G,B,MIN;
                    float HSV[] = new float[3];
                    Color.colorToHSV(pix[index],HSV);
                    
                    R = Math.min(HSV[0],360-HSV[0]);
                    O = Math.abs(HSV[0]-15);
                    Y = Math.abs(HSV[0]-60);
                    G = Math.abs(HSV[0]-120);
                    B = Math.abs(HSV[0]-240);
                    MIN = Math.min(R,O);
                    MIN = Math.min(MIN,Y);
                    MIN = Math.min(MIN,G);
                    MIN = Math.min(MIN,B);

                    if(MIN == R)//RED
                        color[k]='R';
                    else if(MIN == O)//ORANGE
                        color[k]='O';
                    else if(MIN == Y)//YELLOW
                        color[k]='Y';
                    else if(MIN == G)//GREEN
                        color[k]='G';
                    else if(MIN == B)//BLUE
                        color[k]='B';
                }
            }
        return color;
    }

	//�޸���ɫ���Ŀ�
	static final int d[] ={2,3,0,1,5,4};
	static void FixCenterColor(){
	 	for(int i=0;i<6;i++)
	 		for(int j=(i+1)%6;j<6;j++)
	 			if(i!=j && Solve.color[i][4]==Solve.color[j][4])
	 				if(i!=d[j]){//�Ƕ���
	 					fix(i);
	 					fix(j);
	 				}
	 }
     static void fix(int i){
 		if(Solve.color[d[i]][4] == 'R')
 			Solve.color[i][4] = 'O';
 		else if(Solve.color[d[i]][4] == 'O')
 			Solve.color[i][4] = 'R';
 		else if(Solve.color[d[i]][4] == 'G')
 			Solve.color[i][4] = 'B';
 		else if(Solve.color[d[i]][4] == 'B')
 			Solve.color[i][4] = 'G';
 		else if(Solve.color[d[i]][4] == 'Y')
 			Solve.color[i][4] = 'W';
     }
     
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN){
            exit();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    void exit(){
        //ȡ������
        //mAudioManager.setRingerMode(ringerMode);
        setResult(200, null);
        finish();
    }
    
}
