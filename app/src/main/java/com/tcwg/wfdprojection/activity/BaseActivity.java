package com.tcwg.wfdprojection.activity;

import android.app.Activity;
import android.app.Service;
import android.content.Intent;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

public class BaseActivity extends AppCompatActivity {

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

}
