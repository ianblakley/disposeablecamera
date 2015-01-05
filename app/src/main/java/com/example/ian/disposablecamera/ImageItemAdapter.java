package com.example.ian.disposablecamera;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by Ian on 12/24/2014.
 * Image Adapter to display the images in the camera roll
 */
class ImageItemAdapter extends ArrayAdapter<ImageItem> {

    private final Context context;
    private final ArrayList<ImageItem> list;

    public ImageItemAdapter(Context context, ArrayList<ImageItem> list) {
        super(context, R.layout.image_item, list);
        this.context = context;
        this.list = list;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View rowView = convertView;
        ItemHolder holder;

        if (rowView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            rowView = inflater.inflate(R.layout.image_item, parent, false);
            holder = new ItemHolder();
            holder.picture = (ImageView) rowView.findViewById(R.id.image);
            holder.text = (TextView) rowView.findViewById(R.id.text);
            holder.developed = (TextView) rowView.findViewById(R.id.developed);

            rowView.setTag(holder);
        } else {
            holder = (ItemHolder) rowView.getTag();
        }

        ImageItem item = list.get(position);

        String displayString;
        SimpleDateFormat storageFormat = new SimpleDateFormat("yyyyMMdd_HHmmss");
        SimpleDateFormat displayFormat = new SimpleDateFormat("hh:mm a MM/dd/yyy");
        try {
            Date time = storageFormat.parse(item.timeStamp);
            displayString = displayFormat.format(time);
        } catch (ParseException e) {
            displayString = "Error Reading Time";
        }

        holder.text.setText(displayString);

        if (item.developed) {
            Bitmap bmp = BitmapFactory.decodeFile(item.path);
            holder.picture.setImageBitmap(bmp);
        } else {
            holder.picture.setImageResource(R.drawable.ic_launcher);
        }

        if (item.developed) {
            holder.developed.setText("Developed");
        } else {
            try {
                Date time = storageFormat.parse(item.timeStamp);
                Calendar cal = Calendar.getInstance();
                cal.setTime(time);
                cal.add(CameraActivity.TIME_UNIT, CameraActivity.DEVELOPMENT_TIME);
                displayFormat = new SimpleDateFormat("hh:mm a");
                String developTime = displayFormat.format(cal.getTime());
                holder.developed.setText("Developing at " + developTime);

            } catch (ParseException e) {
                holder.developed.setText("Not Developed");
            }

        }

        return rowView;
    }

    static class ItemHolder {
        ImageView picture;
        TextView text;
        TextView developed;
    }

}