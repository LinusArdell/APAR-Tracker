package com.test.input;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.journeyapps.barcodescanner.ScanContract;
import com.journeyapps.barcodescanner.ScanOptions;
import com.test.input.Adapter.EquipmentAdapter;
import com.google.firebase.FirebaseApp;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    FloatingActionButton fab;
    DatabaseReference databaseReference;
    ValueEventListener eventListener;
    RecyclerView recyclerView;
    List<DataClass> dataList;
    EquipmentAdapter adapter;
    SearchView searchView;

    private FirebaseAuth auth;

    private ActivityResultLauncher<ScanOptions> qrCodeLauncher;
    private ActivityResultLauncher<String> requestPermissionLauncher;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        recyclerView = findViewById(R.id.recyclerView);
        fab = findViewById(R.id.fab);

        searchView = findViewById(R.id.search);
        searchView.clearFocus();

        initActivityResultLaunchers();

        findViewById(R.id.qrScan).setOnClickListener(view -> checkPermissionAndShowActivity(this));


        GridLayoutManager gridLayoutManager = new GridLayoutManager(MainActivity.this, 1);
        recyclerView.setLayoutManager(gridLayoutManager);

        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setCancelable(false);
        builder.setView(R.layout.progress_layout);
        AlertDialog dialog = builder.create();
        dialog.show();

        dataList = new ArrayList<>();

        adapter = new EquipmentAdapter(MainActivity.this, dataList);
        recyclerView.setAdapter(adapter);

        databaseReference = FirebaseDatabase.getInstance().getReference("Test");
        dialog.show();
        eventListener = databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                dataList.clear();
                for (DataSnapshot itemSnapshot: snapshot.getChildren()){
                    DataClass dataClass = itemSnapshot.getValue(DataClass.class);
                    dataClass.setKey(itemSnapshot.getKey());
                    dataList.add(dataClass);
                }
                adapter.notifyDataSetChanged();
                dialog.dismiss();
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                dialog.dismiss();
            }
        });

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }
            @Override
            public boolean onQueryTextChange(String newText) {
                searchList(newText);
                return true;
            }
        });

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, EquipmentTambahActivity.class);
                startActivity(intent);
            }
        });

        // Inisialisasi ActivityResultLauncher untuk izin kamera
        requestPermissionLauncher = registerForActivityResult(
                new ActivityResultContracts.RequestPermission(), isGranted -> {
                    if (isGranted) {
                        Toast.makeText(MainActivity.this, "Access Granted", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(MainActivity.this, "Camera permission required", Toast.LENGTH_SHORT).show();
                    }
                }
        );
    }

    public void searchList(String text){
        ArrayList<DataClass> searchList = new ArrayList<>();
        for (DataClass dataClass: dataList){
            if (dataClass.getKodeQR().toLowerCase().contains(text.toLowerCase())){
                searchList.add(dataClass);
            }
        }
        adapter.searchDataList(searchList);
    }

    private void initActivityResultLaunchers() {
        requestPermissionLauncher = registerForActivityResult(new ActivityResultContracts.RequestPermission(), new ActivityResultCallback<Boolean>() {
            @Override
            public void onActivityResult(Boolean isGranted) {
                if (isGranted) {
                    showCamera();
                } else {
                    // Handle permission not granted
                }
            }
        });

        qrCodeLauncher = registerForActivityResult(new ScanContract(), result -> {
            if (result.getContents() == null) {
                Toast.makeText(this, "Cancelled", Toast.LENGTH_SHORT).show();
            } else {
                setResult(result.getContents());
            }
        });
    }

//    private void setResult(String contents) {
//        searchView.setQuery(contents, true);
//    }

    private void setResult(String contents) {
        // Cari data dalam database berdasarkan hasil pemindaian QR
        Query query = databaseReference.orderByChild("kodeQR").equalTo(contents);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    // Jika data ditemukan, buka aktivitas detail
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        DataClass data = snapshot.getValue(DataClass.class);
                        Intent intent = new Intent(MainActivity.this, DetailActivity.class);

                        intent.putExtra("Key", data.getKodeQR().toString());

                        intent.putExtra("Image", data.getDataImage());
                        intent.putExtra("KodeQR", data.getKodeQR().toString());
                        intent.putExtra("Tanggal", data.getDataDate().toString());
                        intent.putExtra("Lokasi", data.getLokasiTabung().toString());
                        intent.putExtra("Merk", data.getMerkAPAR().toString());
                        intent.putExtra("Berat", data.getBeratTabung().toString());
                        intent.putExtra("Jenis", data.getJenisAPAR().toString());
                        intent.putExtra("IsiTabung", data.getIsiTabung().toString());
                        intent.putExtra("Tekanan", data.getTekananTabung().toString());
                        intent.putExtra("Kesesuaian", data.getKesesuaianBerat().toString());
                        intent.putExtra("KondisiTabung", data.getKondisiTabung().toString());
                        intent.putExtra("KondisiSelang", data.getKondisiSelang().toString());
                        intent.putExtra("KondisiPin", data.getKondisiPin().toString());
                        intent.putExtra("Keterangan", data.getKeterangan().toString());
                        intent.putExtra("User", data.getUser().toString());

                        intent.putExtra("Nozzle", data.getKondisiNozzle().toString());
                        intent.putExtra("Posisi", data.getPosisiTabung().toString());

                        startActivity(intent);
                    }
                } else {
                    // Jika data tidak ditemukan, buka aktivitas tambah
                    Intent intent = new Intent(MainActivity.this, EquipmentTambahActivity.class);
                    intent.putExtra("KodeQR", contents);
                    startActivity(intent);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Tangani pembatalan permintaan
            }
        });
    }

    private void showCamera() {
        ScanOptions options = new ScanOptions();
        options.setDesiredBarcodeFormats(ScanOptions.QR_CODE);
        options.setPrompt("Scan QR Code");
        options.setCameraId(0);
        options.setBeepEnabled(false);
        options.setBarcodeImageEnabled(true);
        options.setOrientationLocked(false);

        qrCodeLauncher.launch(options);
    }

    private void checkPermissionAndShowActivity(Context context) {
        if (ContextCompat.checkSelfPermission(
                context,
                android.Manifest.permission.CAMERA
        ) == PackageManager.PERMISSION_GRANTED) {
            showCamera();
        } else if (shouldShowRequestPermissionRationale(android.Manifest.permission.CAMERA)) {
            Toast.makeText(context, "Camera permission required", Toast.LENGTH_SHORT).show();
        } else {
            requestPermissionLauncher.launch(Manifest.permission.CAMERA);
        }
    }
}
