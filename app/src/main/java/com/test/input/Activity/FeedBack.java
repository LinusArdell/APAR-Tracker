package com.test.input.Activity;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.material.button.MaterialButton;
import com.test.input.R;

public class FeedBack extends AppCompatActivity {

    EditText etPesan;
    MaterialButton btnSend;
    Button btnBack;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feed_back);

        etPesan = findViewById(R.id.et_text_email);
        btnSend = findViewById(R.id.btnSend);
        btnBack = findViewById(R.id.btn_backs);

        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String iEmailTo, iSubjek, iKonten;

                iSubjek = "Masukan dan Saran";
                iKonten = etPesan.getText().toString();
                iEmailTo = "linus2016chaesa@mhs.mdp.ac.id";

                if (iKonten.isEmpty()){
                    Toast.makeText(FeedBack.this, "Tidak ada pesan yang dapat dikirim", Toast.LENGTH_SHORT).show();
                } else {
                    sendEmail(iSubjek, iKonten, iEmailTo);
                }

                finish();
            }
        });

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
    }

    public void sendEmail(String subjek, String konten, String email){
        Intent i = new Intent(Intent.ACTION_SEND);
        i.putExtra(Intent.EXTRA_EMAIL, new String[]{email});
        i.putExtra(Intent.EXTRA_SUBJECT, subjek);
        i.putExtra(Intent.EXTRA_TEXT, konten);

        i.setType("message/rfc822");
        startActivity(Intent.createChooser(i, "Buka dengan Gmail :"));
    }
}