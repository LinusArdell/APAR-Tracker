package com.test.input.Activity;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.test.input.R;

public class Report extends AppCompatActivity {

    Button btnBack;
    EditText etKonten;
    MaterialButton btnSend;
    FloatingActionButton fab;
    ImageView ivReport;
    Uri selectedImageUri;
    private static final int EMAIL_REQUEST_CODE = 456;
    private static final int GALLERY_REQUEST_CODE = 123;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report);

        btnBack = findViewById(R.id.btn_backs);
        etKonten = findViewById(R.id.et_text_email);
        btnSend = findViewById(R.id.btnSend);
        fab = findViewById(R.id.upload_picture);
        ivReport = findViewById(R.id.imageView);

        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String iEmailTo, iSubjek, iKonten;

                iSubjek = "Laporan";
                iKonten = etKonten.getText().toString();
                iEmailTo = "linus2016chaesa@mhs.mdp.ac.id";

                if (iKonten.isEmpty()){
                    Toast.makeText(Report.this, "Tidak ada pesan yang dapat dikirim", Toast.LENGTH_SHORT).show();
                } else {
                    sendEmail(iSubjek, iKonten, iEmailTo, selectedImageUri);
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

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openGallery();
            }
        });
    }

    public void sendEmail(String subjek, String konten, String email, Uri attachmentUri){
        Intent i = new Intent(Intent.ACTION_SEND);
        i.putExtra(Intent.EXTRA_EMAIL, new String[]{email});
        i.putExtra(Intent.EXTRA_SUBJECT, subjek);
        i.putExtra(Intent.EXTRA_TEXT, konten);

        i.setType("image/*");

        if (attachmentUri != null) {
            i.putExtra(Intent.EXTRA_STREAM, attachmentUri);
        }

        startActivity(Intent.createChooser(i, "Buka dengan Gmail :"));
    }

    public void openGallery() {
        Intent galleryIntent = new Intent(Intent.ACTION_OPEN_DOCUMENT, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        galleryIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION| Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
        startActivityForResult(galleryIntent, GALLERY_REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == GALLERY_REQUEST_CODE && resultCode == RESULT_OK) {
            selectedImageUri = data.getData();
            if (selectedImageUri != null) {
                ivReport.setImageURI(selectedImageUri);
            }
        } else if (requestCode == EMAIL_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                Toast.makeText(this, "Email berhasil dikirim", Toast.LENGTH_SHORT).show();
            } else if (requestCode == EMAIL_REQUEST_CODE && resultCode == RESULT_OK) {
                // Email sent successfully, you can handle any action here like returning to previous activity
                Toast.makeText(this, "Email berhasil dikirim", Toast.LENGTH_SHORT).show();
                // Kembali ke halaman sebelumnya
                finish();
            }
        }
    }
}