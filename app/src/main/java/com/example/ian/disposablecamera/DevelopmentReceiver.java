package com.example.ian.disposablecamera;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;

/**
 * Created by Ian on 12/23/2014.
 */
public class DevelopmentReceiver extends BroadcastReceiver {
    Context context;

    public DevelopmentReceiver(){

    }

    @Override
    public void onReceive(Context context, Intent intent){
        this.context = context;
        Bundle fileNames = intent.getExtras();
        File internal = (File)fileNames.get(context.getString(R.string.internal_file));
        File external = (File)fileNames.get(context.getString(R.string.external_file));

        try{
            FileInputStream fil = new FileInputStream(internal);
            FileOutputStream fos = new FileOutputStream(external);
            FileChannel inChannel = fil.getChannel();
            FileChannel outChannel = fos.getChannel();
            inChannel.transferTo(0, inChannel.size(), outChannel);
            fil.close();
            fos.close();
            internal.delete();
            addPicToGallery(external);
        } catch (IOException e){
            Log.d(context.getString(R.string.app_name), "Unable to copy data");
        }
        Toast toast = Toast.makeText(context, "New Photo Developed", Toast.LENGTH_SHORT);
        toast.show();
    }

    private void addPicToGallery(File picture){
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        Uri uri = Uri.fromFile(picture);
        mediaScanIntent.setData(uri);
        context.sendBroadcast(mediaScanIntent);
    }
}
