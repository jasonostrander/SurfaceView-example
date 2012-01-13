package com.example;

import android.content.Context;
import android.graphics.Canvas;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

public class ExampleSurfaceView extends SurfaceView implements SurfaceHolder.Callback {
    SurfaceHolder mSurfaceHolder;
    DrawingThread mThread;
    int mRed;
    int mGreen;
    int mBlue = 127;
    
    public ExampleSurfaceView(Context context) {
        super(context);
        mSurfaceHolder = getHolder();
        mSurfaceHolder.addCallback(this);
        mThread = new DrawingThread();
    }
    
    @Override
    protected void onDraw(Canvas canvas) {
        canvas.drawRGB(mRed, mGreen, mBlue);
    }
    
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
        case MotionEvent.ACTION_DOWN:
        case MotionEvent.ACTION_MOVE:
            synchronized(mSurfaceHolder) {
                mRed = (int) (255*event.getX()/getWidth());
                mGreen = (int) (255*event.getY()/getHeight());
            }
            return true;
        }
        return super.onTouchEvent(event);
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width,
            int height) {
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        mThread.keepRunning = true;
        mThread.start();
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        mThread.keepRunning = false;
        boolean retry = true;
        while (retry) {
            try {
                mThread.join();
                retry = false;
            } catch (InterruptedException e) {}
        }
    }
    
    private class DrawingThread extends Thread {
        boolean keepRunning = true;
        
        @Override
        public void run() {
            Canvas c;
            while (keepRunning) {
                c = null;
                
                try {
                    c = mSurfaceHolder.lockCanvas();
                    synchronized (mSurfaceHolder) {
                        onDraw(c);
                    }
                } finally {
                    if (c != null)
                        mSurfaceHolder.unlockCanvasAndPost(c);
                }
            }
        }
    }
}
