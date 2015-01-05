package com.example.ian.disposablecamera;

import android.app.Application;
import android.test.ApplicationTestCase;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * <a href="http://d.android.com/tools/testing/testing_android.html">Testing Fundamentals</a>
 */
public class ApplicationTest extends ApplicationTestCase<Application> {

    ArrayList<ImageItem> list;
    String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
    String testString = "ImageItem [timeStamp=" + timeStamp + ", imageLocation=, developed=true]";

    public ApplicationTest() {
        super(Application.class);
    }

    @Override
    public void setUp() throws Exception {
        super.setUp();
        list = new ArrayList<ImageItem>();
        list.add(new ImageItem(testString));
        list.add(new ImageItem(testString));
        ImageItem.putSavedList(getContext(), list);
    }

    public void testWrite() {


    }

    public void testRead() {

        ArrayList<ImageItem> readList = ImageItem.getSavedList(getContext());
        assertEquals(readList.toString(), list.toString());
        ImageItem.putSavedList(getContext(), new ArrayList<ImageItem>());
    }
}