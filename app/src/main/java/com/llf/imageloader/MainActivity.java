package com.llf.imageloader;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;

import com.llf.imageloader.utils.CacheUtil;

public class MainActivity extends AppCompatActivity {
    private ImageView mImageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mImageView = (ImageView) findViewById(R.id.image);
        CacheUtil.getInstance(this).setImageToView("http://avatar01.jiaoliuqu.com/avatar/newavatarmale.jpg", mImageView);
    }
}
