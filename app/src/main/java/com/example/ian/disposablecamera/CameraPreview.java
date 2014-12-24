package com.example.ian.disposablecamera;

import android.content.Context;
import android.hardware.Camera;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.io.IOException;

/**
 * Created by Ian on 12/22/2014.
 */
public class CameraPreview extends SurfaceView implements SurfaceHolder.Callback{

    SurfaceHolder mSurfaceHolder;
    Camera mCamera;

    CameraPreview(Context context, Camera camera){
        super(context);

        mCamera = camera;

        mSurfaceHolder = getHolder();
        mSurfaceHolder.addCallback(this);
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder){
        try{
            Camera.Parameters params = mCamera.getParameters();
            params.setRotation(90);
            mCamera.setParameters(params);
            mCamera.setDisplayOrientation(90);

            mCamera.setPreviewDisplay(holder);
            mCamera.startPreview();
        } catch (IOException e){
            e.printStackTrace();
        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int w, int h){
        if (mSurfaceHolder.getSurface() == null){return;}
        try { mCamera.stopPreview();}
        catch (Exception e) { e.printStackTrace(); }

        //make any preview changes here

        try {
            mCamera.setPreviewDisplay(mSurfaceHolder);
            mCamera.startPreview();
        } catch (IOException e) {e.printStackTrace(); }
    }

    public void surfaceDestroyed(SurfaceHolder holder){}

    public void setmCamera(Camera camera){ mCamera = camera; }
}
