package com.example.ian.disposablecamera;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;

/**
 * Created by Ian on 12/23/2014.
 * This class receives the development alarm and moves the picture from internal to external memory, adds it to the gallery and adds it to the camera roll
 */
public class DevelopmentReceiver extends BroadcastReceiver {
    Context context;

    @Override
    public void onReceive(Context context, Intent intent) {
        this.context = context;
        Bundle fileNames = intent.getExtras();
        File internal = (File) fileNames.get(context.getString(R.string.internal_file));
        File external = (File) fileNames.get(context.getString(R.string.external_file));

        try {
            FileInputStream fil = new FileInputStream(internal);
            FileOutputStream fos = new FileOutputStream(external);
            FileChannel inChannel = fil.getChannel();
            FileChannel outChannel = fos.getChannel();
            inChannel.transferTo(0, inChannel.size(), outChannel);
            fil.close();
            fos.close();
            boolean deleted = internal.delete();
            if (!deleted) {
                Log.d("File Error", "File Not Deleted");
            }
            addPicToGallery(external);
        } catch (IOException e) {
            Log.d(context.getString(R.string.app_name), "Unable to copy data");
        }
    }

    private void addPicToGallery(File picture) {
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        Uri uri = Uri.fromFile(picture);
        mediaScanIntent.setData(uri);
        context.sendBroadcast(mediaScanIntent);
    }
}
