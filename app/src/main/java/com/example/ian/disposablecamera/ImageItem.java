package com.example.ian.disposablecamera;

import android.content.Context;
import android.util.Log;

import com.google.gson.Gson;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by Ian on 12/24/2014.
 * Image Item that is stored in a list of pictures taken and displayed in the camera roll
 */
public class ImageItem {

    public String timeStamp;
    public String path;
    public boolean developed;

    public ImageItem(String time, String imageLocation) {
        timeStamp = time;
        path = imageLocation;
        developed = false;
    }

    public ImageItem(String imageString) {

        int start = imageString.indexOf("=");
        int end = imageString.indexOf(",");
        timeStamp = imageString.substring(start + 1, end);
        start = imageString.indexOf("=", end);
        end = imageString.indexOf(",", start);
        path = imageString.substring(start + 1, end);
        File output = new File(path);
        developed = output.exists();
    }

    public static ArrayList<ImageItem> getSavedList(Context context) {
        File internalFile = new File(context.getFilesDir() + File.separator + context.getString(R.string.camera_roll_list_filename));

        ArrayList<ImageItem> list = new ArrayList<ImageItem>();
        Gson gson = new Gson();

        if (internalFile.exists()) {
            try {
                FileInputStream fis = new FileInputStream(internalFile);
                int available = fis.available();
                byte[] byteArray = new byte[available];
                int len = fis.read(byteArray);

                if (len != available) {
                    Log.e("File Read Error", "File not read correctly");
                }

                String listJson = new String(byteArray);


                int start;
                int end = 0;

                if (listJson.length() == 0) {
                    return new ArrayList<ImageItem>();
                }

                while (end != listJson.length() - 1) {
                    start = listJson.indexOf("{", end);
                    end = listJson.indexOf("}", start);
                    ImageItem item = gson.fromJson(listJson.substring(start, end + 1), ImageItem.class);

                    boolean fileGone = false;
                    try {
                        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_HHmmss");
                        Date date = sdf.parse(item.timeStamp);
                        Calendar cal = Calendar.getInstance();
                        cal.add(CameraActivity.TIME_UNIT, -CameraActivity.DEVELOPMENT_TIME);
                        fileGone = date.before(cal.getTime());
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }

                    if (((item.developed || fileGone) && !new File(item.path).exists())) {
                        Log.v("File Deleted", "A file has been deleted and removed from the camera roll");
                    } else {
                        item.developed = new File(item.path).exists();
                        list.add(item);
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
        putSavedList(context, list);

        return list;


    }

    public static void putSavedList(Context context, ArrayList<ImageItem> list) {
        File internalFile = new File(context.getFilesDir() + File.separator + context.getString(R.string.camera_roll_list_filename));
        try {
            Gson gson = new Gson();
            FileOutputStream fos = new FileOutputStream(internalFile);
            for (ImageItem i : list) {
                String item = gson.toJson(i);
                fos.write(item.getBytes());
            }
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String toString() {
        return "ImageItem [timeStamp=" + timeStamp + ", imageLocation=" + path + ", developed=" + developed + "]";
    }


}
