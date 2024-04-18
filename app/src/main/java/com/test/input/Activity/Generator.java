package com.test.input.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DownloadManager;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.navigation.NavigationView;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.journeyapps.barcodescanner.BarcodeEncoder;
import com.test.input.R;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class Generator extends AppCompatActivity {

    EditText tvGenerator, etFilename;
    ImageView qrCode;
    Button btnGenerate, btnClear, btnDownload;
    TextView userEmail;
    private static final int PERMISSION_STORAGE_CODE = 1000;
    ImageButton btnBack;
    public boolean success = false;
    AlertDialog.Builder dialogScan;
    Button btnOk;
    Dialog dialog;
    LayoutInflater inflaterScan;
    View dialogViewScan;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);

        setContentView(R.layout.activity_generator);
        btnBack = findViewById(R.id.btn_back);
        tvGenerator = findViewById(R.id.et_qr);
        qrCode = findViewById(R.id.iv_qr);
        btnGenerate = findViewById(R.id.btn_generate);
        btnClear = findViewById(R.id.btn_clear);
        btnDownload = findViewById(R.id.btn_download);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }

        btnClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Mengosongkan input teks di tvGenerator
                tvGenerator.setText("");

                // Menghapus gambar di qrCode
                qrCode.setImageDrawable(null);
            }
        });

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        btnGenerate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MultiFormatWriter multiFormatWriter = new MultiFormatWriter();

                try {
                    BitMatrix bitMatrix = multiFormatWriter.encode(tvGenerator.getText().toString(), BarcodeFormat.QR_CODE, 150,150);
                    BarcodeEncoder barcodeEncoder = new BarcodeEncoder();
                    Bitmap bitmap = barcodeEncoder.createBitmap(bitMatrix);

                    qrCode.setImageBitmap(bitmap);
                } catch (WriterException e) {
                    throw new RuntimeException(e);
                }
            }
        });

        btnDownload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                File folder = new File(Environment.getExternalStorageDirectory()
                        +"/Documents/"+ Generator.this.getString(R.string.folder_name));

                try {
                    success = true;
                    if (!folder.exists())
                        folder.mkdirs();

                    if (success){

                        dialog = new Dialog(Generator.this);
                        dialog.setContentView(R.layout.dialog_name_file);
                        dialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                        dialog.getWindow().setBackgroundDrawable(getDrawable(R.drawable.dialog_logout_bg));
                        dialog.setCancelable(false);

                        btnOk = dialog.findViewById(R.id.btn_ok);
                        etFilename = dialog.findViewById(R.id.file_name);

                        dialog.show();
                        btnOk.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                String filename = etFilename.getText().toString().trim();
                                if (filename.isEmpty()){
                                    etFilename.setError("Nama file harus diisi");
                                    etFilename.requestFocus();
                                    return;
                                }

                                try {
                                    MultiFormatWriter multiFormatWriter = new MultiFormatWriter();
                                    try {
                                        BitMatrix bitMatrix = multiFormatWriter.encode(tvGenerator.getText().toString(), BarcodeFormat.QR_CODE, 150,150);
                                        BarcodeEncoder barcodeEncoder = new BarcodeEncoder();
                                        Bitmap bitmap = barcodeEncoder.createBitmap(bitMatrix);

                                        saveImageToInternalStorage(bitmap, folder.getAbsolutePath(), etFilename.getText().toString());
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                    dialog.dismiss();
                                    Toast.makeText(Generator.this, "Gambar berhasil disimpan", Toast.LENGTH_SHORT).show();
                                    OpenFolder(folder.getAbsolutePath(), etFilename.getText().toString()+".jpg");
                                }catch (Exception e){
                                    e.printStackTrace();
                                }
                            }
                        });
                    }
                }catch (Exception e){
                    e.printStackTrace();
                    success = false;
                }
            }
        });
    }

    private void OpenFolder(String absolutePath, String filename) {
        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());

        File path = new File(absolutePath+"/"+filename);
        Uri uri = Uri.fromFile(path);

        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setDataAndType(uri, "image/jpg");
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    private String saveImageToInternalStorage(Bitmap bitmap, String folder, String filename){
        ContextWrapper cw = new ContextWrapper(getApplicationContext());
        File directory = cw.getDir("imageDir", Context.MODE_PRIVATE);

        File myPath = new File(folder, filename + ".jpg");

        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(myPath);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
        }catch (Exception e){
            e.printStackTrace();
        }
        finally {
            try {
                fos.close();
            }catch (IOException e){
                e.printStackTrace();
            }
        }
        return directory.getAbsolutePath();
    }
}