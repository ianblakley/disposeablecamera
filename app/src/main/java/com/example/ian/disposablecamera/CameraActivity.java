package com.example.ian.disposablecamera;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;


public class CameraActivity extends Activity {

    public static final String PHOTO_DIRECTORY = "Developed Photos";
    public static final int MEDIA_TYPE_PICTURE = 1;
    private Camera.PictureCallback mPicture = new Camera.PictureCallback() {
        @Override
        public void onPictureTaken(byte[] bytes, Camera camera) {
            String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
            File pictureFile = getOutputMediaFile(MEDIA_TYPE_PICTURE, timeStamp);
            File internalFile = getInternalMediaFile(MEDIA_TYPE_PICTURE, timeStamp);
            try {
                FileOutputStream fos = new FileOutputStream(internalFile);
                fos.write(bytes);
                fos.close();
            } catch (IOException e) {
                Log.d("IO Error", "Unable to Write To File");
            }

            ImageItem picture = new ImageItem(timeStamp, pictureFile.getAbsolutePath());

            picturesTaken.add(0, picture);

            createDevelopmentTimer(internalFile, pictureFile);
            mCamera.startPreview();
        }
    };
    public static final int TIME_UNIT = Calendar.HOUR;
    public static final int DEVELOPMENT_TIME = 8;
    public Camera mCamera;
    public CameraPreview mPreview;
    public SharedPreferences preferences;
    public ArrayList<ImageItem> picturesTaken;
    public ImageButton flashButton;

    private static File getOutputMediaFile(int type, String timeStamp) {


        //Expand on this
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_UNMOUNTED)) {
            return null;
        }

        File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM), PHOTO_DIRECTORY);

        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                Log.d("Error", "Failed to create directory");
                return null;
            }
        }

        File mediaFile;
        if (type == MEDIA_TYPE_PICTURE) {
            mediaFile = new File(mediaStorageDir.getPath() + File.separator + "IMG_" + timeStamp + ".jpg");

        } else {
            return null;
        }
        return mediaFile;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);

        //Starts the camera
        detectCamera();

        //Retrieves settings
        preferences = PreferenceManager.getDefaultSharedPreferences(this);

        picturesTaken = ImageItem.getSavedList(this);

        setContentView(R.layout.activity_camera);

        mCamera = Camera.open();
        mPreview = new CameraPreview(this, mCamera);
        FrameLayout preview = (FrameLayout) findViewById(R.id.camera_preview);
        preview.addView(mPreview);

        //Sets Up UI
        flashButton = (ImageButton) findViewById(R.id.flash_button);
        setFlash();
        flashButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                toggleFlash();
            }
        });

        Button capture = (Button) findViewById(R.id.capture_button);
        capture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mCamera.takePicture(null, null, mPicture);
            }
        });

        ImageButton settings = (ImageButton) findViewById(R.id.roll_button);
        settings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                releaseCameraAndPreview();
                Intent goToRoll = new Intent(CameraActivity.this, CameraRollActivity.class);
                startActivity(goToRoll);
            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        releaseCameraAndPreview();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mCamera == null) {
            mCamera = Camera.open();
            mPreview = new CameraPreview(this, mCamera);
            FrameLayout preview = (FrameLayout) findViewById(R.id.camera_preview);
            preview.addView(mPreview);
        }
    }

    private void createDevelopmentTimer(File internal, File external) {
        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.add(TIME_UNIT, DEVELOPMENT_TIME);
        Intent developmentIntent = new Intent(this, DevelopmentReceiver.class);
        developmentIntent.putExtra(getString(R.string.internal_file), internal);
        developmentIntent.putExtra(getString(R.string.external_file), external);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 1, developmentIntent, 0);
        if (Build.VERSION.SDK_INT > 18) {
            alarmManager.setExact(AlarmManager.RTC, calendar.getTimeInMillis(), pendingIntent);
        } else {
            alarmManager.set(AlarmManager.RTC, calendar.getTimeInMillis(), pendingIntent);
        }
        ImageItem.putSavedList(this, picturesTaken);
    }

    private File getInternalMediaFile(int type, String timeStamp){
        File internalStorageDir = getFilesDir();

        File internalFile;
        if (type == MEDIA_TYPE_PICTURE){
            internalFile = new File(internalStorageDir.getPath() + File.separator + timeStamp + ".jpg");
        } else {return null;}
        return internalFile;
    }

    public void releaseCameraAndPreview(){
        if (mCamera != null){
            mCamera.release();
            mCamera = null;
            mPreview.getHolder().removeCallback(mPreview);
        }
    }

    private void detectCamera(){
        if (!getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA)) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle(R.string.no_camera_title).setMessage(R.string.no_camera_text);
            builder.setNeutralButton("Exit", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    System.exit(2);
                }
            });
        }
    }

    private void setFlash(){
        int flash_preference = preferences.getInt("preference_flash", 0);
        Camera.Parameters mCameraParameters = mCamera.getParameters();
        switch (flash_preference) {
            case (0):
                mCameraParameters.setFlashMode(Camera.Parameters.FLASH_MODE_AUTO);
                flashButton.setImageResource(R.drawable.ic_action_flash_automatic);
                break;
            case (1):
                mCameraParameters.setFlashMode(Camera.Parameters.FLASH_MODE_ON);
                flashButton.setImageResource(R.drawable.ic_action_flash_on);
                break;
            case (2):
                mCameraParameters.setFlashMode(Camera.Parameters.FLASH_MODE_OFF);
                flashButton.setImageResource(R.drawable.ic_action_flash_off);
                break;
        }
        mCamera.setParameters(mCameraParameters);
    }

    private void toggleFlash(){
        int flash = preferences.getInt("preference_flash", 0);
        flash = (flash+1)%3;
        preferences.edit().putInt("preference_flash", flash).apply();
        setFlash();
    }

}
