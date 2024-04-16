package com.test.input.Activity;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.common.BitMatrix;
import com.journeyapps.barcodescanner.BarcodeEncoder;
import com.test.input.R;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class DetailActivity extends AppCompatActivity {

    TextView detailKodeQR, detailTanggal, tvDeleteDialog;
    ImageView detailImage;
    MaterialButton deleteButton, editButton;
    ImageButton btnQr;
    Button tbnBack;
    String key = "";
    String imageUrl = "";

    private TextView isiTabung, tekananTabung, kesesuaianBerat, kondisiTabung, kondisiSelang, kondisiPin;
    private TextView merkAPAR, jenisAPAR, kondisiNozzle, posisiTabung;
    private TextView etLokasi, etBerat, etketerangan, tvTitleDetail, tvDate;
    public boolean success = false;
    AlertDialog.Builder dialogScan;
    LayoutInflater inflaterScan;
    View dialogViewScan;
    Dialog dialog;
    Button btnCancel, btnDelete;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        initializeComponents();
        fillDataFromIntent();
        setupDeleteButtonListener();
        setupEditButtonListener();

        dialog = new Dialog(DetailActivity.this);
        dialog.setContentView(R.layout.custom_dialog_delete);
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setBackgroundDrawable(getDrawable(R.drawable.dialog_logout_bg));
        dialog.setCancelable(false);

        btnCancel = dialog.findViewById(R.id.customCancelDelete);
        btnDelete = dialog.findViewById(R.id.customDelete);
        tvDeleteDialog = dialog.findViewById(R.id.deleteID);

        tvDeleteDialog.setText("Anda yakin ingin menghapus " + detailKodeQR.getText().toString() + "?");

        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                performDeleteAction();
            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        tbnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        btnQr.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                File folder = new File(Environment.getExternalStorageDirectory()
                        +"/Documents/"+ DetailActivity.this.getString(R.string.folder_name));

                try {
                    success = true;
                    if (!folder.exists())
                        folder.mkdirs();

                    if (success){
                        dialogScan  = new AlertDialog.Builder(DetailActivity.this,
                                androidx.appcompat.R.style.Base_Theme_AppCompat_Light_Dialog);
                        inflaterScan = getLayoutInflater();
                        dialogViewScan = inflaterScan.inflate(R.layout.dialog_name_file,null);
                        Button okButton = dialogViewScan.findViewById(R.id.btn_ok);
                        TextInputEditText filename = dialogViewScan.findViewById(R.id.file_name);
                        dialogScan.setView(dialogViewScan);
                        dialogScan.setCancelable(true);

                        final AlertDialog show = dialogScan.show();
                        okButton.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                try {
                                    MultiFormatWriter multiFormatWriter = new MultiFormatWriter();
                                    try {
                                        BitMatrix bitMatrix = multiFormatWriter.encode(detailKodeQR.getText().toString(), BarcodeFormat.QR_CODE, 150,150);
                                        BarcodeEncoder barcodeEncoder = new BarcodeEncoder();
                                        Bitmap bitmap = barcodeEncoder.createBitmap(bitMatrix);

                                        saveImageToInternalStorage(bitmap, folder.getAbsolutePath(), filename.getText().toString());
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                    show.dismiss();
                                    Toast.makeText(DetailActivity.this, "Gambar berhasil disimpan", Toast.LENGTH_SHORT).show();
                                    OpenFolder(folder.getAbsolutePath(), filename.getText().toString()+".jpg");
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

    private void initializeComponents() {
        isiTabung = findViewById(R.id.detail_isi);
        tekananTabung = findViewById(R.id.detail_tekanan);
        kesesuaianBerat = findViewById(R.id.detail_kesesuaian);
        kondisiTabung = findViewById(R.id.detail_kondisi);
        kondisiSelang = findViewById(R.id.detail_selang);
        kondisiPin = findViewById(R.id.detail_pin);
        merkAPAR = findViewById(R.id.detail_merk);
        jenisAPAR = findViewById(R.id.detail_jenis);
        etLokasi = findViewById(R.id.detail_lokasi);
        etBerat = findViewById(R.id.detail_berat);
        etketerangan = findViewById(R.id.detail_keterangan);
        detailKodeQR = findViewById(R.id.detail_qr);
        detailImage = findViewById(R.id.detail_image);
        detailTanggal = findViewById(R.id.detail_tanggal);
        deleteButton = findViewById(R.id.deleteButton);
        editButton = findViewById(R.id.editButton);
        tvTitleDetail = findViewById(R.id.tc_title_detail);
        tvDate = findViewById(R.id.tv_date);
        kondisiNozzle = findViewById(R.id.detail_nozzle);
        posisiTabung = findViewById(R.id.detail_posisi);
        tbnBack = findViewById(R.id.btn_back);
        btnQr = findViewById(R.id.btn_qr);
    }

    private void fillDataFromIntent() {
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            tvTitleDetail.setText("Preview Pemeriksaan " + bundle.getString("KodeQR"));
            tvDate.setText("Pertanggal " + bundle.getString("Tanggal"));

            detailKodeQR.setText(bundle.getString("KodeQR"));
            detailTanggal.setText(bundle.getString("Tanggal"));
            isiTabung.setText(bundle.getString("IsiTabung"));
            tekananTabung.setText(bundle.getString("Tekanan"));
            kesesuaianBerat.setText(bundle.getString("Kesesuaian"));
            kondisiTabung.setText(bundle.getString("KondisiTabung"));
            kondisiSelang.setText(bundle.getString("KondisiSelang"));
            kondisiPin.setText(bundle.getString("KondisiPin"));
            merkAPAR.setText(bundle.getString("Merk"));
            jenisAPAR.setText(bundle.getString("Jenis"));
            etLokasi.setText(bundle.getString("Lokasi"));
            etBerat.setText(bundle.getString("Berat"));
            etketerangan.setText(bundle.getString("Keterangan"));
            kondisiNozzle.setText(bundle.getString("Nozzle"));
            posisiTabung.setText(bundle.getString("Posisi"));
            key = bundle.getString("Key");
            imageUrl = bundle.getString("Image");
            Glide.with(this).load(bundle.getString("Image")).into(detailImage);
        }
    }

    private void setupDeleteButtonListener() {
        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.show();
            }
        });
    }

    private void performDeleteAction() {
        final DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Test");
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageReference = storage.getReferenceFromUrl(imageUrl);
        storageReference.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                reference.child(key).removeValue();
                Toast.makeText(DetailActivity.this, "Deleted", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(getApplicationContext(), MainActivity.class));
                finish();
            }
        });
    }

    private void setupEditButtonListener() {
        editButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startUpdateActivity();
            }
        });
    }

    private void startUpdateActivity() {
        Intent intent = new Intent(DetailActivity.this, UpdateActivity.class)
                .putExtra("KodeQR", detailKodeQR.getText().toString())
                .putExtra("Tanggal", detailTanggal.getText().toString())
                .putExtra("Lokasi", etLokasi.getText().toString())
                .putExtra("Merk", merkAPAR.getText().toString())
                .putExtra("Berat", etBerat.getText().toString())
                .putExtra("Jenis", jenisAPAR.getText().toString())
                .putExtra("IsiTabung", isiTabung.getText().toString())
                .putExtra("Tekanan", tekananTabung.getText().toString())
                .putExtra("Kesesuaian", kesesuaianBerat.getText().toString())
                .putExtra("KondisiTabung", kondisiTabung.getText().toString())
                .putExtra("KondisiSelang", kondisiSelang.getText().toString())
                .putExtra("KondisiPin", kondisiPin.getText().toString())
                .putExtra("Keterangan", etketerangan.getText().toString())
                .putExtra("Nozzle", kondisiNozzle.getText().toString())
                .putExtra("Posisi", posisiTabung.getText().toString())
                .putExtra("Image", imageUrl)
                .putExtra("Key", key);
        startActivity(intent);
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