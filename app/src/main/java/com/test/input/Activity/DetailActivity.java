package com.test.input.Activity;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.github.clans.fab.FloatingActionButton;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.test.input.R;

public class DetailActivity extends AppCompatActivity {

    TextView detailKodeQR, detailTanggal;
    ImageView detailImage;
    FloatingActionButton deleteButton, editButton;
    String key = "";
    String imageUrl = "";

    private TextView isiTabung, tekananTabung, kesesuaianBerat, kondisiTabung, kondisiSelang, kondisiPin;
    private TextView merkAPAR, jenisAPAR, kondisiNozzle, posisiTabung;
    private TextView etLokasi, etBerat, etketerangan;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

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

        kondisiNozzle = findViewById(R.id.detail_nozzle);
        posisiTabung = findViewById(R.id.detail_posisi);

        Bundle bundle = getIntent().getExtras();
        if (bundle != null){
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

        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
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
        });

        editButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
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
        });
    }
}