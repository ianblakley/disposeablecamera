package com.example.ian.disposablecamera;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;


public class ViewPictureActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_picture);


        ImageView image = (ImageView) findViewById(R.id.image_view);
        TextView text = (TextView) findViewById(R.id.text_view);

        Intent intent = getIntent();

        String timeStamp = intent.getStringExtra("image_timestamp");
        String path = intent.getStringExtra("image_path");
        boolean developed = intent.getBooleanExtra("developed_boolean", false);

        String displayString;
        SimpleDateFormat storageFormat = new SimpleDateFormat("yyyyMMdd_HHmmss");
        SimpleDateFormat displayFormat = new SimpleDateFormat("hh:mm a MM/dd/yyy");
        try {
            Date time = storageFormat.parse(timeStamp);
            displayString = displayFormat.format(time);
        } catch (ParseException e) {
            displayString = "Error Reading Time";
        }

        text.setText(displayString);

        if (developed) {
            Bitmap bmp = BitmapFactory.decodeFile(path);
            image.setImageBitmap(bmp);
        } else {
            image.setImageResource(R.drawable.ic_launcher);
        }
    }
}
