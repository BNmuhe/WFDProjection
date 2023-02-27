package com.tcwg.wfdprojection.activity;

import android.app.Activity;
import android.app.Service;
import android.content.Intent;
import android.graphics.Point;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.tcwg.wfdprojection.constant.MyDeviceConstants;

public class BaseActivity extends AppCompatActivity {


    public static final String TAG = BaseActivity.class.getSimpleName();
    protected void setTitle(String title) {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle(title);
        }
    }

    protected void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    protected <T extends Activity> void startActivity(Class<T> tClass) {
        startActivity(new Intent(this, tClass));
    }

    protected <T extends Service> void startService(Class<T> tClass) {
        startService(new Intent(this, tClass));
    }
    public void initConstant(){
        Point point = new Point();
        getWindowManager().getDefaultDisplay().getRealSize(point);
        MyDeviceConstants.setVideoHeight(point.y);
        MyDeviceConstants.setVideoWidth(point.x);
        Log.e(TAG,"set Constant width "+ MyDeviceConstants.getVideoWidth()+" height "+ MyDeviceConstants.getVideoHeight()+" fps "+ MyDeviceConstants.getScreenFrameRate());
    }
}
