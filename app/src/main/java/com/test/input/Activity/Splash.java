package com.test.input.Activity;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Toast;

import com.test.input.R;

public class Splash extends AppCompatActivity {

    Button btnOk, btnNo;
    Dialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);

        setContentView(R.layout.activity_splash);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
        WindowManager.LayoutParams.FLAG_FULLSCREEN);

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

                            dialog = new Dialog(Splash.this);
                            dialog.setContentView(R.layout.dialog_no_internet);
                            dialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                            dialog.getWindow().setBackgroundDrawable(getDrawable(R.drawable.dialog_logout_bg));
                            dialog.setCancelable(false);

                            btnOk = dialog.findViewById(R.id.noInternetOk);

                            dialog.show();

                            btnOk.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    dialog.dismiss();
                                    finish();
                                }
                            });

                            Toast.makeText(Splash.this, "Tidak ada akses internet", Toast.LENGTH_LONG).show();
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