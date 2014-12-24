package com.example.ian.disposablecamera;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.FrameLayout;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;


public class CameraActivity extends Activity {

    public static final String PHOTO_DIRECTORY = "Developed Photos";
    public static final int MEDIA_TYPE_PICTURE = 1;
    public static final int DEVELOPMENT_TIME = 8;
    private CameraOpener opener;
    public Camera mCamera;
    public CameraPreview mPreview;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_camera);
        detectCamera();
        opener = new CameraOpener();
        opener.run();
        mCamera = opener.getCamera();
        mPreview = new CameraPreview(this, mCamera);
        FrameLayout preview = (FrameLayout)findViewById(R.id.camera_preview);
        preview.addView(mPreview);

        Button capture = (Button)findViewById(R.id.capture_button);
        capture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mCamera.takePicture(null,null,mPicture);
            }
        });

    }

    @Override
    protected void onPause(){
        super.onPause();
        releaseCameraAndPreview();
    }

    @Override
    protected void onResume(){
        super.onResume();
        if (mCamera == null){
            opener.run();
            mCamera = opener.getCamera();
            mPreview.setmCamera(mCamera);
        }
    }

    private Camera.PictureCallback mPicture = new Camera.PictureCallback() {
        @Override
        public void onPictureTaken(byte[] bytes, Camera camera) {
            String timeStamp = new SimpleDateFormat("MMdd_HHmmss").format(new Date());
            File pictureFile = getOutputMediaFile(MEDIA_TYPE_PICTURE, timeStamp);
            File internalFile = getInternalMediaFile(MEDIA_TYPE_PICTURE, timeStamp);
            try {
                FileOutputStream fos = new FileOutputStream(internalFile);
                fos.write(bytes);
                fos.close();
            } catch (IOException e) {
                Log.d("IO Error", "Unable to Write To File");
            }

            createDevelopmentTimer(internalFile, pictureFile);
            mCamera.startPreview();
        }
    };

    private void createDevelopmentTimer(File internal, File external){
        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.add(Calendar.SECOND, DEVELOPMENT_TIME);
        Intent developmentIntent = new Intent(this, DevelopmentReceiver.class);
        developmentIntent.putExtra(getString(R.string.internal_file), internal);
        developmentIntent.putExtra(getString(R.string.external_file), external);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 1, developmentIntent, 0);
        if (Build.VERSION.SDK_INT > 18) {
            alarmManager.setExact(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
        } else {
            alarmManager.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
        }
    }



    private static File getOutputMediaFile(int type, String timeStamp){


        //Expand on this
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_UNMOUNTED)){return null;}

        File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM), PHOTO_DIRECTORY);

        if (!mediaStorageDir.exists()){
            if (!mediaStorageDir.mkdirs()){
                Log.d("Error", "Failed to create directory");
                return null;
            }
        }

        File mediaFile;
        if (type == MEDIA_TYPE_PICTURE){
            mediaFile = new File(mediaStorageDir.getPath() + File.separator + "IMG_" + timeStamp + ".jpg");

        } else {return null;}
        return mediaFile;
    }

    private File getInternalMediaFile(int type, String timeStamp){
        File internalStorageDir = getFilesDir();

        File internalFile;
        if (type == MEDIA_TYPE_PICTURE){
            internalFile = new File(internalStorageDir.getPath() + File.separator + "IMG_" + timeStamp + ".jpg");
        } else {return null;}
        return internalFile;
    }

    public void releaseCameraAndPreview(){
        if (mCamera != null){
            mCamera.release();
            mCamera = null;
        }
    }

    private void detectCamera(){
        if (getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA)){
            return;
        } else {
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





    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_camera, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
