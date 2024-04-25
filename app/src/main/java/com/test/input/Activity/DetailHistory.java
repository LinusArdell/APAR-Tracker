package com.test.input.Activity;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.material.button.MaterialButton;
import com.test.input.R;

public class DetailHistory extends AppCompatActivity {

    TextView detailKodeQR, detailTanggal, tvDeleteDialog, detailPemeriksa;
    EditText etFilename;
    ImageView detailImage;
    MaterialButton deleteButton, editButton;
    ImageButton btnQr, btnHelp;
    Button tbnBack;
    String key = "";
    String imageUrl = "";
    private TextView isiTabung, tekananTabung, kesesuaianBerat, kondisiTabung, kondisiSelang, kondisiPin;
    private TextView merkAPAR, jenisAPAR, kondisiNozzle, posisiTabung;
    private TextView etLokasi, etBerat, etketerangan, tvTitleDetail, tvDate;
    public boolean success = false;
    AlertDialog.Builder dialogScan;
    LayoutInflater inflaterScan;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_history);

        initializeComponents();
        fillDataFromIntent();

        tbnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
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
        tvTitleDetail = findViewById(R.id.tc_title_detail);
        tvDate = findViewById(R.id.tv_date);
        kondisiNozzle = findViewById(R.id.detail_nozzle);
        posisiTabung = findViewById(R.id.detail_posisi);
        tbnBack = findViewById(R.id.btn_back);
        detailPemeriksa = findViewById(R.id.detail_user);
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
            detailPemeriksa.setText(bundle.getString("User"));
            Glide.with(this).load(bundle.getString("Image")).into(detailImage);
        }
    }
}