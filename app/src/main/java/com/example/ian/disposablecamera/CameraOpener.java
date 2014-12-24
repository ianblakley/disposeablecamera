package com.example.ian.disposablecamera;

import android.hardware.Camera;
import android.util.Log;

/**
 * Created by Ian on 12/22/2014.
 */
public class CameraOpener implements Runnable {

    public Camera camera;

    public CameraOpener(){
        camera = Camera.open();
    }
    @Override
    public void run(){
        try {
            camera.release();
            camera = null;
            camera = Camera.open();

        } catch (Exception e){
            Log.d("Error: ", "Failed to open Camera");
            e.printStackTrace();
        }

    }
    public Camera getCamera(){return camera;}

}
