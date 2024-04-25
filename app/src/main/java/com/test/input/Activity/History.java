package com.test.input.Activity;

import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.journeyapps.barcodescanner.ScanOptions;
import com.test.input.Adapter.DraftAdapter;
import com.test.input.Adapter.EquipmentAdapter;
import com.test.input.Adapter.HistoryAdapter;
import com.test.input.Class.DataClass;
import com.test.input.Class.DraftClass;
import com.test.input.Class.HistoryClass;
import com.test.input.R;

import java.util.ArrayList;
import java.util.List;

public class History extends AppCompatActivity {

    DatabaseReference databaseReferences;
    ValueEventListener eventListeners;
    RecyclerView recyclerViews;
    List<HistoryClass> dataLists;
    HistoryAdapter adapter;
    TextView tvTitle;
    Button btnBack;

    @SuppressLint("WrongViewCast")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);

        recyclerViews = findViewById(R.id.recyHistorya);
        tvTitle = findViewById(R.id.t_history_title);
        btnBack = findViewById(R.id.btn_backs);

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        fillDataFromIntent();

        // Inisialisasi dataLists
        dataLists = new ArrayList<>();

        // Inisialisasi adapter
        adapter = new HistoryAdapter(History.this, dataLists);

        // Atur layout manager untuk RecyclerView
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerViews.setLayoutManager(layoutManager);

        // Atur adapter ke RecyclerView
        recyclerViews.setAdapter(adapter);

        String childKey = tvTitle.getText().toString();

        // Referensi ke database Firebase
        databaseReferences = FirebaseDatabase.getInstance().getReference("History").child(childKey);

        // Tambahkan ValueEventListener untuk membaca data dari Firebase
        eventListeners = databaseReferences.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                // Bersihkan dataLists sebelum menambahkan data baru
                dataLists.clear();

                // Iterasi melalui setiap child dari "Test" di Firebase
                for (DataSnapshot itemSnapshot: snapshot.getChildren()){
                    // Ambil data dan tambahkan ke dataLists
                    HistoryClass dataClass = itemSnapshot.getValue(HistoryClass.class);
                    dataClass.setKey(itemSnapshot.getKey());
                    dataLists.add(dataClass);
                }

                // Pemberitahuan ke adapter bahwa data telah berubah
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Penanganan kesalahan jika baca data gagal
                Toast.makeText(History.this, "Failed to read data from Firebase", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void fillDataFromIntent(){
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            tvTitle.setText(bundle.getString("KodeQR"));
        }
    }
}
