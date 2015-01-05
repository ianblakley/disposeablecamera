package com.example.ian.disposablecamera;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import java.util.ArrayList;


public class CameraRollActivity extends Activity {

    ArrayList<ImageItem> pictureList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera_roll);
        setPictureList();
    }

    private void setPictureList() {
        pictureList = ImageItem.getSavedList(this);

        ListView pictureRoll = (ListView) findViewById(R.id.camera_roll_list);
        ImageItemAdapter adapter = new ImageItemAdapter(this, pictureList);
        pictureRoll.setAdapter(adapter);
        pictureRoll.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                ImageItem item = (ImageItem) adapterView.getItemAtPosition(i);
                Intent intent = new Intent(CameraRollActivity.this, ViewPictureActivity.class);
                intent.putExtra("image_timestamp", item.timeStamp);
                intent.putExtra("image_path", item.path);
                intent.putExtra("developed_boolean", item.developed);
                startActivity(intent);
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        pictureList = ImageItem.getSavedList(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_camera_roll, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        switch (id) {
            case R.id.action_settings:

                return true;

            case R.id.action_clear_roll:
                pictureList = new ArrayList<ImageItem>();
                ImageItem.putSavedList(this, pictureList);
                setPictureList();
                return true;

        }
        return false;
    }
}