package com.test.input.Activity;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.test.input.Adapter.DraftAdapter;
import com.test.input.Class.DraftClass;
import com.test.input.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Preview extends AppCompatActivity {

    TextView textViewKodeQR, textViewLokasi, textViewMerk, textViewBerat, textViewJenis, tvTekanan, tvKondisiPin,
            tvKondisiSelang, tvIsiTabung, tKeterangan, tNozzle, tTanggal, tPosisi, tKesesuaian, tKondisiTabung, tvUser;

    ImageView tvImage;

    private RecyclerView recyclerView;
    private List<DraftClass> dataList;
    private DraftAdapter adapter;
    ImageButton btnBack;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preview);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }

        btnBack = findViewById(R.id.btn_backs);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        recyclerView = findViewById(R.id.recyDraft);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        dataList = new ArrayList<>();

//        dataList = getAllImageDataFromSharedPreferences();
        dataList.clear();
        dataList.addAll(getAllImageDataFromSharedPreferences());
        adapter = new DraftAdapter(this, dataList);
        recyclerView.setAdapter(adapter);
        adapter.notifyDataSetChanged();
    }

    private List<DraftClass> getAllImageDataFromSharedPreferences(){
        List<DraftClass> dataLists =new ArrayList<>();
        SharedPreferences sharedPreferences = getSharedPreferences("data_offline", MODE_PRIVATE);

        Map<String, ?> allEntries = sharedPreferences.getAll();
        for (Map.Entry<String, ?> entry : allEntries.entrySet()) {
            String key = entry.getKey();
            if (key.startsWith("imageUri_")) {

                String uriString = (String) entry.getValue();
                Uri uri = Uri.parse(uriString);

                String kodeQRkey = key + "KodeQR";
                String lokasiKey = key + "Lokasi";
                String merkKey = key + "Merk";
                String beratKey = key + "Berat";
                String jenisKey = key + "jenis";
                String tekananKey = key + "Tekanan";
                String kondisiPinKey = key + "KondisiPin";
                String userKey = key + "User";
                String kondisiSelangKey = key + "KondisiSelang";
                String isiTabungKey = key + "IsiTabung";
                String keteranganKey = key + "Keterangan";
                String nozzleKey = key + "Nozzle";
                String tanggalKey = key + "Tanggal";
                String posisiKey = key + "Posisi";
                String kesesuaianKey = key + "Kesesuaian";
                String kondisiTabungKey = key + "KondisiTabung";

                String kodeQR = sharedPreferences.getString(kodeQRkey, "");
                String lokasi = sharedPreferences.getString(lokasiKey, "");
                String merk = sharedPreferences.getString(merkKey, "");
                String berat = sharedPreferences.getString(beratKey, "");
                String jenis = sharedPreferences.getString(jenisKey, "");

                String Tekanan = sharedPreferences.getString(tekananKey, "");
                String KondisiPin = sharedPreferences.getString(kondisiPinKey, "");
                String User = sharedPreferences.getString(userKey, "");
                String KondisiSelang = sharedPreferences.getString(kondisiSelangKey, "");
                String IsiTabung = sharedPreferences.getString(isiTabungKey, "");
                String Keterangan = sharedPreferences.getString(keteranganKey, "");
                String Nozzle = sharedPreferences.getString(nozzleKey, "");
                String Tanggal = sharedPreferences.getString(tanggalKey, "");
                String Posisi = sharedPreferences.getString(posisiKey, "");
                String Kesesuaian = sharedPreferences.getString(kesesuaianKey, "");
                String KondisiTabung = sharedPreferences.getString(kondisiTabungKey, "");
                if (!TextUtils.isEmpty(kodeQR)) {
                    dataLists.add(new DraftClass(kodeQR,  lokasi,  merk,  berat,  jenis,
                            IsiTabung,  Tekanan,  Kesesuaian,  KondisiTabung,  KondisiSelang,
                            KondisiPin,  Keterangan,  uriString,  Tanggal,  User,  Nozzle,  Posisi));
                }
            }
        }
        return dataLists;
    }
}

