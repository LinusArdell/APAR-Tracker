package com.test.input.Activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.DownloadManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
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
import com.test.input.DataClass;
import com.test.input.ProfileActivity;
import com.test.input.R;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    DatabaseReference databaseReference;
    ValueEventListener eventListener;
    RecyclerView recyclerView;
    List<DataClass> dataList;
    EquipmentAdapter adapter;
    SearchView searchView;
    TextView jumlahAPAR;
    ImageButton btnQR;

    private DrawerLayout drawerLayout;
    FirebaseAuth mAuth;
    TextView userEmail;

    private boolean isAscendingByName = true;
    private boolean isDescendingByName = false;
    private boolean isAscendingByDate = true;
    private boolean isDescendingByDate = false;
    private static final int PERMISSION_STORAGE_CODE = 1000;

    private SharedPreferences sharedPreferences;

    private ActivityResultLauncher<ScanOptions> qrCodeLauncher, qrCodeLaunchers;
    private ActivityResultLauncher<String> requestPermissionLauncher;
    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.tool_bar);
        setSupportActionBar(toolbar);

        mAuth = FirebaseAuth.getInstance();

        drawerLayout = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        View headerView = navigationView.getHeaderView(0);
        userEmail = headerView.findViewById(R.id.user_email);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.open_nav, R.string.close_nav);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, PERMISSION_STORAGE_CODE);
        sharedPreferences = getSharedPreferences("sorting_prefs", Context.MODE_PRIVATE);

        Menu menu = navigationView.getMenu();
        MenuItem logoutItem = menu.findItem(R.id.nav_logout);
        MenuItem addNew = menu.findItem(R.id.nav_add);
        MenuItem downloadItem = menu.findItem(R.id.nav_download);

        downloadItem.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(@NonNull MenuItem menuItem) {
                if (isStoragePermissionGranted()) {
                    downloadFile();
                } else {
                    Toast.makeText(MainActivity.this, "Izin penyimpanan diperlukan untuk mengunduh file.", Toast.LENGTH_SHORT).show();
                }
                return false;
            }
        });

        logoutItem.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                logout();
                return true;
            }
        });

        addNew.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(@NonNull MenuItem menuItem) {
                Intent intent = new Intent(MainActivity.this, EquipmentTambahActivity.class);
                startActivity(intent);
                return false;
            }
        });

        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            String email = currentUser.getEmail();
            userEmail.setText(email);
        }

        recyclerView = findViewById(R.id.recyclerView);
        searchView = findViewById(R.id.search);
        searchView.clearFocus();
        jumlahAPAR = findViewById(R.id.tv_jumlahAPAR);
        btnQR = findViewById(R.id.searchQr);

        initActivityResultLaunchers();

        findViewById(R.id.qrScan).setOnClickListener(view -> checkPermissionAndShowActivity(this));
        findViewById(R.id.searchQr).setOnClickListener(view -> checkPermissionAndShowActivitys(this));

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
                restoreSortingStatus();
                dialog.dismiss();

                int totalData = dataList.size();
                String totalDataString = "Jumlah data: " + totalData;
                jumlahAPAR.setText(totalDataString);
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

    private void logout() {
        mAuth.signOut();
        Intent intent = new Intent(MainActivity.this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    private void restoreSortingStatus() {
        isAscendingByName = sharedPreferences.getBoolean("ascending_by_name", true);
        isDescendingByName = sharedPreferences.getBoolean("descending_by_name", false);
        isAscendingByDate = sharedPreferences.getBoolean("ascending_by_date", true);
        isDescendingByDate = sharedPreferences.getBoolean("descending_by_date", false);

        if (isAscendingByName || isDescendingByName) {
            sortDataByName();
        } else if (isAscendingByDate || isDescendingByDate) {
            sortDataByDate();
        }
    }

    private void saveSortingStatus() {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean("ascending_by_name", isAscendingByName);
        editor.putBoolean("descending_by_name", isDescendingByName);
        editor.putBoolean("ascending_by_date", isAscendingByDate);
        editor.putBoolean("descending_by_date", isDescendingByDate);
        editor.apply();
    }

    private void sortDataByName() {
        Collections.sort(dataList, new Comparator<DataClass>() {
            @Override
            public int compare(DataClass data1, DataClass data2) {
                if (isAscendingByName) {
                    return data1.getKodeQR().compareTo(data2.getKodeQR());
                } else if (isDescendingByName) {
                    return data2.getKodeQR().compareTo(data1.getKodeQR());
                }
                return 0;
            }
        });
        adapter.notifyDataSetChanged();
        saveSortingStatus();
    }

    private void sortDataByDate() {
        Collections.sort(dataList, new Comparator<DataClass>() {
            DateFormat dateFormat = new SimpleDateFormat("dd MMM yyyy HH.mm.ss", Locale.getDefault());
            @Override
            public int compare(DataClass data1, DataClass data2) {
                try {
                    Date date1 = dateFormat.parse(data1.getDataDate());
                    Date date2 = dateFormat.parse(data2.getDataDate());
                    if (isAscendingByDate) {
                        return date1.compareTo(date2);
                    } else if (isDescendingByDate) {
                        return date2.compareTo(date1);
                    }
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                return 0;
            }
        });
        adapter.notifyDataSetChanged();
        saveSortingStatus();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.sort_name_ascending:
                isAscendingByName = true;
                isDescendingByName = false;
                sortDataByName();
                return true;
            case R.id.sort_name_descending:
                isAscendingByName = false;
                isDescendingByName = true;
                sortDataByName();
                return true;
            case R.id.sort_date_ascending:
                isAscendingByDate = true;
                isDescendingByDate = false;
                sortDataByDate();
                return true;
            case R.id.sort_date_descending:
                isAscendingByDate = false;
                isDescendingByDate = true;
                sortDataByDate();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void searchList(String text){
        ArrayList<DataClass> searchList = new ArrayList<>();
        for (DataClass dataClass: dataList){
            if (dataClass.getKodeQR().toLowerCase().contains(text.toLowerCase())
                    || dataClass.getLokasiTabung().toLowerCase().contains(text.toLowerCase())
                    || dataClass.getUser().toLowerCase().contains(text.toLowerCase())
                    || dataClass.getDataDate().toLowerCase().contains(text.toLowerCase())) {
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

        qrCodeLaunchers = registerForActivityResult(new ScanContract(), result -> {
            if (result.getContents() == null) {
                Toast.makeText(this, "Cancelled", Toast.LENGTH_SHORT).show();
            } else {
                setResults(result.getContents());
            }
        });
    }

    private void setResults(String contents) {
        searchView.setQuery(contents, true);
    }

    private void setResult(String contents) {
        // Find data in the database based on the QR scan result
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

    private void showCameras() {
        ScanOptions options = new ScanOptions();
        options.setDesiredBarcodeFormats(ScanOptions.QR_CODE);
        options.setPrompt("Scan QR Code");
        options.setCameraId(0);
        options.setBeepEnabled(false);
        options.setBarcodeImageEnabled(true);
        options.setOrientationLocked(false);

        qrCodeLaunchers.launch(options);
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

    private void checkPermissionAndShowActivitys(Context context) {
        if (ContextCompat.checkSelfPermission(
                context,
                android.Manifest.permission.CAMERA
        ) == PackageManager.PERMISSION_GRANTED) {
            showCameras();
        } else if (shouldShowRequestPermissionRationale(android.Manifest.permission.CAMERA)) {
            Toast.makeText(context, "Camera permission required", Toast.LENGTH_SHORT).show();
        } else {
            requestPermissionLauncher.launch(Manifest.permission.CAMERA);
        }
    }

    private boolean isStoragePermissionGranted() {
        return ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;
    }

    private void downloadFile() {
        String url = "https://docs.google.com/spreadsheets/d/e/2PACX-1vSlBkOtlUi_8ROqZxxeID4-pniUSl4s9plF5WVtonEEep3EJRBC1VOFvVhOnSngu8pCOaNQJaepMBdk/pub?output=xlsx";

        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(url));
        request.setTitle("File Spreadsheet");
        request.setDescription("Mengunduh file spreadsheet...");
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
        request.setDestinationInExternalFilesDir(this, Environment.DIRECTORY_DOWNLOADS, "spreadsheet.xlsx");

        DownloadManager downloadManager = (DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);
        downloadManager.enqueue(request);

        Toast.makeText(this, "Mengunduh file...", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_STORAGE_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            } else {
                Toast.makeText(this, "Izin penyimpanan dibutuhkan untuk mengunduh file.", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
