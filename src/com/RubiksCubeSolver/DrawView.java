package com.RubiksCubeSolver;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.text.Layout.Alignment;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.LinearLayout;

@SuppressLint("ViewConstructor")
public class DrawView extends View {
	Canvas mCanvas;
	Paint p = new Paint();
	TextPaint tp = new TextPaint(TextPaint.ANTI_ALIAS_FLAG);
	int width,height;
    int flag;
    public DrawView(Context context,int n) {
        super(context);
        setLayoutParams(new LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT
                ,LinearLayout.LayoutParams.MATCH_PARENT));
        flag = n;
    }

	@Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        width  = canvas.getWidth();
        height = canvas.getHeight();
        mCanvas=canvas;
        if(flag == 0)
        	Camera(Solve.color[0]);
        else if (flag == 1)
        	Cube();
    }
	
    public void Camera(char[] color){
        p.setStrokeWidth(1);
        int ax = (width-height)/2 + height/6;
        int ay = height/6;
        for(int i=0;i<3;i++)
            for(int j=0;j<3;j++){
            	int x=ax+i*height/3;
            	int y=ay+j*height/3;
            	int index =3*i+j;
                if(color[index] == 'W')
                	p.setColor(Color.rgb(255,255,255));
                else if(color[index] == 'R')
                	p.setColor(Color.rgb(255,0,0));
                else if(color[index] == 'O')
                	p.setColor(Color.rgb(255,128,0));
                else if(color[index] == 'Y')
                	p.setColor(Color.rgb(255,255,0));
                else if(color[index] == 'G')
                	p.setColor(Color.rgb(0,255,0));
                else if(color[index] == 'B')
                	p.setColor(Color.rgb(0,0,255));
                else //if(color[index] == 'U')
                	p.setColor(Color.rgb(153,153,153));
            	mCanvas.drawRect(x-5, y-5, x+5, y+5, p);
            	p.setColor(Color.BLACK);
            	mCanvas.drawLine(x-5,y-5,x+5,y-5,p);
            	mCanvas.drawLine(x-5,y+5,x+5,y+5,p);
            	mCanvas.drawLine(x-5,y-5,x-5,y+5,p);
            	mCanvas.drawLine(x+5,y-5,x+5,y+5,p);
            	
            }
    }
    public void Cube(){
    	p.setAntiAlias(true);
    	p.setStrokeWidth(1);
        p.setStyle(Paint.Style.FILL);
        p.setTypeface(Typeface.MONOSPACE);
        p.setTextAlign(Paint.Align.CENTER);
        p.setTextSize(30);//设置字体大小
        char draw[][] = new char[6][9];
        draw[0]=Solve.color[5];
        draw[1]=Solve.color[3];
        draw[2]=Solve.color[0];
        draw[3]=Solve.color[1];
        draw[4]=Solve.color[2];
        draw[5]=Solve.color[4];
        
        float startx = 50;
        float starty = 50;
        float cube = (width - 2*startx)/4;
        float block = cube/3;
        float endx = width-startx;
        float endy = cube*3 + starty;

        //背景
        p.setColor(Color.BLACK);
        mCanvas.drawRect(0,0,width,height,p);
        
        for(int i=0;i<6;i++){
            float sx,sy;
            if(i==0){
                sx = startx + cube;
                sy = starty;
            }else if(i == 5){
                sx = startx + cube;
                sy = starty + 2*cube;
            }else{
                sx = startx + (i-1)*cube;
                sy = starty + cube;
            }
            for(int j=0;j<3;j++){
                for(int k=0;k<3;k++){
                    int index = 3*k+j;
                    if(draw[i][index] == 'W')
                        p.setColor(Color.rgb(255,255,255));
                    else if(draw[i][index] == 'R')
                        p.setColor(Color.rgb(255,0,0));
                    else if(draw[i][index] == 'O')
                        p.setColor(Color.rgb(255,128,0));
                    else if(draw[i][index] == 'Y')
                        p.setColor(Color.rgb(255,255,0));
                    else if(draw[i][index] == 'G')
                        p.setColor(Color.rgb(0,255,0));
                    else if(draw[i][index] == 'B')
                        p.setColor(Color.rgb(0,0,255));
                    else //if(draw[i][index] == 'U')
                        p.setColor(Color.rgb(153,153,153));

                    mCanvas.drawRect(sx + j*block,sy + k*block,sx + (j+1)*block,sy + (k+1)*block,p);
                    
                    if(j==1 && k==1){
                    	final char ReadQ[] = {'U','L','F','R','B','D'};
                        p.setColor(Color.BLACK);
                        int cx=(int)(sx + (j+0.5)*block);
                        int cy=(int)(sy + (k+0.85)*block);
                        mCanvas.drawText(String.valueOf(ReadQ[i]), cx,cy, p);
                    }
                }
            }
        }

        p.setColor(Color.BLACK);
        for(int i=0;i<10;i++)
            mCanvas.drawLine(startx,starty+i*block,endx,starty+i*block,p);
        for(int i=0;i<13;i++)
            mCanvas.drawLine(startx+i*block,starty,startx+i*block,endy,p);
        
        //蓝牙状态
        p.setTextSize(25);
        String str="蓝牙:";
        if(MainActivity.connected){
            p.setColor(Color.GREEN);
            str+="已连接";
        }else{
            p.setColor(Color.RED);
            str+="未连接";
        }
        mCanvas.drawText(str, startx+3*cube,(int)(starty+0.85*block), p);
        
        //魔方状态
        tp.setTypeface(Typeface.MONOSPACE);
        tp.setColor(Color.WHITE);
        tp.setTextSize(25);
        StaticLayout layout = new StaticLayout(MainActivity.status,tp,(int) (width-2*startx),Alignment.ALIGN_NORMAL,1.0F,0.0F,true);
        mCanvas.translate(startx,starty+10*block);
        layout.draw(mCanvas);
    }
}