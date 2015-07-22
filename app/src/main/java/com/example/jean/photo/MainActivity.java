package com.example.jean.photo;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.widget.GridView;


public class MainActivity extends ActionBarActivity {

    private GridView mGridView;
    private ImageAdapter mImageAdapter;
    private String[] mImageThumbUrls = Images.imageThumbUrls;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mGridView = (GridView)findViewById(R.id.gridView);
        mImageAdapter = new ImageAdapter(this,mGridView,mImageThumbUrls);
        mGridView.setAdapter(mImageAdapter);
    }

}
