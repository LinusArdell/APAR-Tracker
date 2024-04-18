package com.test.input.Activity;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.view.WindowManager;
import android.widget.Toast;

import com.test.input.R;

public class Splash extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }

        Handler HDL = new Handler();
        HDL.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (isNetworkStatusAvialable(getApplicationContext())){
                    startActivity(new Intent(Splash.this, LoginActivity.class));
                    finish();
                } else {
                    HDL.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(Splash.this, "Tidak ada akses internet", Toast.LENGTH_LONG).show();
                            finish();
                        }

                    },1000);
                }
            }
        }, 2000);
    }

    public static boolean isNetworkStatusAvialable (Context context) {
        ConnectivityManager cm =
                (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        return activeNetwork != null &&
                activeNetwork.isConnectedOrConnecting();

    }
}