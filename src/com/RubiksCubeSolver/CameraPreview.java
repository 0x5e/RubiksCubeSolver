package com.RubiksCubeSolver;

import android.annotation.SuppressLint;
import android.content.Context;
import android.hardware.Camera;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import java.io.IOException;

@SuppressLint("ViewConstructor")
public class CameraPreview extends SurfaceView implements
        SurfaceHolder.Callback {
    private SurfaceHolder mHolder;
    private Camera mCamera;
    public static char color[]=new char[6];
    
    @SuppressWarnings("deprecation")
	public CameraPreview(Context context, Camera camera) {
        super(context);
        mCamera = camera;
        mHolder = getHolder();
        mHolder.addCallback(this);
        mHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        
        /*
        new Camera.PreviewCallback(){
	        @Override
	        public void onPreviewFrame(byte[] data, Camera camera) {
	        	//获取九格颜色
	            color = CameraUseActivity.getcolors(data);

	        }
        };*/
    }

    
    @Override
    public void surfaceCreated(SurfaceHolder holder) {
    	
    	if (holder.getSurface() == null)
            return;
        try {
           mCamera.setPreviewDisplay(holder);
        } catch (IOException exception) {
            mCamera.release();
            mCamera = null;
        }
        mCamera.startPreview();
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int w, int h) {
        if (mHolder.getSurface() == null)
            return;
        mCamera.stopPreview();
        try {
            mCamera.setPreviewDisplay(mHolder);
        } catch (IOException e) {
            e.printStackTrace();
        }
    	mCamera.startPreview();
    }

    /**
     * 停止预览
     */
        @Override
        public void surfaceDestroyed(SurfaceHolder holder) {
                //mCamera.stopPreview();
        		if(mCamera!=null){
        			//mCamera.stopPreview();
	                mCamera.release();//加上这句，就OK！
	                mCamera=null;
        		}
        }
}