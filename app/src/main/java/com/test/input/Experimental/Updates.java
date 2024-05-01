package com.test.input.Experimental;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.test.input.Adapter.VersionAdapter;
import com.test.input.BuildConfig;
import com.test.input.Class.VersionClass;
import com.test.input.R;

import java.util.ArrayList;
import java.util.List;

public class Updates extends AppCompatActivity {

    RecyclerView recyclerView;
    DatabaseReference databaseReference;
    VersionAdapter adapter;
    List<VersionClass> dataList;
    Button btnBack;
    TextView tvCurrentVer;
    TextView tvIsEmpty;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_updates);

        btnBack = findViewById(R.id.btn_backs);
        tvCurrentVer = findViewById(R.id.tv_currentver);
        recyclerView = findViewById(R.id.recVersion);
//        tvIsEmpty = findViewById(R.id.emptyState);

        databaseReference = FirebaseDatabase.getInstance().getReference("files");
        dataList =new ArrayList<>();

        adapter =new VersionAdapter(Updates.this, dataList);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        String versionApp = BuildConfig.VERSION_NAME;

        tvCurrentVer.setText("Versi aplikasi anda saat ini : " + versionApp);

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        fetchDataFromFirebase();

//        if (dataList.isEmpty()) {
//            recyclerView.setVisibility(View.GONE);
//            tvIsEmpty.setVisibility(View.VISIBLE);
//        } else {
//            recyclerView.setVisibility(View.VISIBLE);
//            tvIsEmpty.setVisibility(View.GONE);
//        }
    }

    private void fetchDataFromFirebase() {
        // Tambahkan listener untuk mengambil data dari Firebase
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                // Bersihkan list sebelum menambahkan data baru
                dataList.clear();

                // Iterasi melalui setiap item pada dataSnapshot
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    // Ambil nilai dari snapshot dan tambahkan ke list
                    VersionClass file = snapshot.getValue(VersionClass.class);
                    dataList.add(file);
                }

                // Perbarui tampilan RecyclerView setelah mendapatkan data baru
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Tangani pembatalan jika ada
                Log.e("AppsVer", "Error: " + databaseError.getMessage());
            }
        });
    }
}