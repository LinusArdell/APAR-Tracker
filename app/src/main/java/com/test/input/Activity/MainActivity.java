package com.test.input.Activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.DownloadManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Settings;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.getkeepsafe.taptargetview.TapTarget;
import com.getkeepsafe.taptargetview.TapTargetSequence;
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
import com.test.input.BuildConfig;
import com.test.input.Class.DataClass;
import com.test.input.Experimental.Updates;
import com.test.input.R;
import com.test.input.Class.UserClass;

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

    private DatabaseReference databaseReference;
    private ValueEventListener eventListener;
    private RecyclerView recyclerView;
    private List<DataClass> dataList;
    private EquipmentAdapter adapter;
    SearchView searchView;
    TextView jumlahAPAR;
    ImageButton btnQR;

    private DrawerLayout drawerLayout;
    FirebaseAuth mAuth;
    TextView userEmail, username, tvAppVersion;
    FloatingActionButton fabQr;
    private DatabaseReference mDatabase;

    private boolean isAscendingByName = true;
    private boolean isDescendingByName = false;
    private boolean isAscendingByDate = true;
    private boolean isDescendingByDate = false;
    private static final int PERMISSION_STORAGE_CODE = 1000;

    private SharedPreferences sharedPreferences;

    private ActivityResultLauncher<ScanOptions> qrCodeLauncher, qrCodeLaunchers;
    private ActivityResultLauncher<String> requestPermissionLauncher;
    Dialog dialog;
    Button btnCancel, btnLogout, btnBatal, btnYa;

    private static final String PREFS_NAME = "MyPrefsFile";
    private static final String KEY_ONBOARDING_COMPLETE = "onboarding_complete";

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

//        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
//                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }

        recyclerView = findViewById(R.id.recyclerView);
        searchView = findViewById(R.id.search);
        searchView.clearFocus();
        btnQR = findViewById(R.id.searchQr);

        dataList = new ArrayList<>();

        adapter = new EquipmentAdapter(MainActivity.this, dataList);
        recyclerView.setAdapter(adapter);

        fabQr = findViewById(R.id.qrScan);

        dialog = new Dialog(MainActivity.this);
        dialog.setContentView(R.layout.custom_dialog_logout);
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setBackgroundDrawable(getDrawable(R.drawable.dialog_logout_bg));
        dialog.setCancelable(false);

        btnCancel = dialog.findViewById(R.id.customCancel);
        btnLogout = dialog.findViewById(R.id.customLogout);

        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                logout();
            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        Toolbar toolbar = findViewById(R.id.tool_bar);
        Menu item = toolbar.getMenu();
        MenuItem sortByAz = item.findItem(R.id.sort_name_ascending);
        MenuItem sortByZa = item.findItem(R.id.sort_name_descending);
        MenuItem sortByRecent = item.findItem(R.id.sort_date_ascending);
        MenuItem sortByDate = item.findItem(R.id.sort_date_descending);

        sortByAz.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(@NonNull MenuItem menuItem) {
                isAscendingByName = true;
                isDescendingByName = false;
                sortDataByName();
                return true;
            }
        });

        sortByZa.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(@NonNull MenuItem menuItem) {
                isAscendingByName = false;
                isDescendingByName = true;
                sortDataByName();
                return true;
            }
        });

        sortByRecent.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(@NonNull MenuItem menuItem) {
                isAscendingByDate = true;
                isDescendingByDate = false;
                sortDataByDate();
                return true;
            }
        });

        sortByDate.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(@NonNull MenuItem menuItem) {
                isAscendingByDate = false;
                isDescendingByDate = true;
                sortDataByDate();
                return true;
            }
        });

        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();

        drawerLayout = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        View headerView = navigationView.getHeaderView(0);
        userEmail = headerView.findViewById(R.id.user_email);
        username = headerView.findViewById(R.id.username);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.open_nav, R.string.close_nav);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, PERMISSION_STORAGE_CODE);
        sharedPreferences = getSharedPreferences("sorting_prefs", Context.MODE_PRIVATE);

        Menu menu = navigationView.getMenu();
        MenuItem logoutItem = menu.findItem(R.id.nav_logout);
        MenuItem addNew = menu.findItem(R.id.nav_add);
        MenuItem downloadItem = menu.findItem(R.id.nav_download);
        MenuItem generateQR = menu.findItem(R.id.nav_generate);
        MenuItem onboard = menu.findItem(R.id.nav_guide);
        MenuItem register = menu.findItem(R.id.nav_register);
        MenuItem draft = menu.findItem(R.id.nav_draft);
        MenuItem version = menu.findItem(R.id.nav_version);
        MenuItem feedback = menu.findItem(R.id.nav_feedback);
        MenuItem report = menu.findItem(R.id.nav_report);
        MenuItem update = menu.findItem(R.id.nav_update);

        String versionApp = BuildConfig.VERSION_NAME;

        version.setTitle("Versi aplikasi : " + versionApp);

        update.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(@NonNull MenuItem menuItem) {
                Intent i = new Intent(MainActivity.this, Updates.class);
                startActivity(i);
                return false;
            }
        });

        report.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(@NonNull MenuItem menuItem) {
                Intent i = new Intent(MainActivity.this, Report.class);
                startActivity(i);
                return false;
            }
        });

        feedback.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(@NonNull MenuItem menuItem) {
                Intent i = new Intent(MainActivity.this, FeedBack.class);
                startActivity(i);
                return false;
            }
        });

        draft.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(@NonNull MenuItem menuItem) {
                Intent i = new Intent(MainActivity.this, Preview.class);
                i.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                startActivity(i);
                return false;
            }
        });

        register.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(@NonNull MenuItem menuItem) {
                Intent i = new Intent(MainActivity.this, Register.class);
                startActivity(i);
                return false;
            }
        });

        onboard.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(@NonNull MenuItem menuItem) {
                showOnboarding();
                drawerLayout.closeDrawer(GravityCompat.START);
                return true;
            }
        });

        generateQR.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(@NonNull MenuItem menuItem) {
                Intent intent = new Intent(MainActivity.this, Generator.class);
                startActivity(intent);
                return false;
            }
        });

        downloadItem.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(@NonNull MenuItem menuItem) {
                if (isStoragePermissionGranted()) {
                    downloadFile();
                } else {
                    requestStoragePermission();
                }
                return false;
            }
        });

        logoutItem.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                dialog.show();
                return false;
            }
        });

        addNew.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(@NonNull MenuItem menuItem) {
                if (isNetworkStatusAvialable(getApplicationContext())){
                    Intent intent = new Intent(MainActivity.this, EquipmentTambahActivity.class);
                    startActivity(intent);
                }
                else {
                    dialog = new Dialog(MainActivity.this);
                    dialog.setContentView(R.layout.dialog_no_internet_upload);
                    dialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                    dialog.getWindow().setBackgroundDrawable(getDrawable(R.drawable.dialog_logout_bg));
                    dialog.setCancelable(false);

                    btnBatal = dialog.findViewById(R.id.btnBatal);
                    btnYa = dialog.findViewById(R.id.btnYa);

                    dialog.show();

                    btnBatal.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            dialog.dismiss();
                        }
                    });

                    btnYa.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Intent i = new Intent(MainActivity.this, TambahOffline.class);
                            startActivity(i);
                            dialog.dismiss();
                        }
                    });
                }
                return false;
            }
        });

        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            String userId = currentUser.getUid();

            String email = currentUser.getEmail();
            userEmail.setText(email);
            mDatabase.child("users").child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        UserClass userData = snapshot.getValue(UserClass.class);

                        String username1 = userData.getUsername();
                        username.setText(username1);

                        saveUserLocally(username1);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    // Handle error
                }
            });
        }

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

        databaseReference = FirebaseDatabase.getInstance().getReference("Test");
//        databaseReference = FirebaseDatabase.getInstance().getReference("Draft");
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
                sortDataByDate();
                restoreSortingStatus();
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
                return false;
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

    private void showOnboarding() {
        new TapTargetSequence(this)
                .targets(
                        TapTarget.forView(fabQr,"QR Scanner", "Klik untuk memulai semuanya")
                                .outerCircleColor(R.color.main_color)
                                .outerCircleAlpha(0.96f)
                                .targetCircleColor(R.color.white)
                                .titleTextSize(26)
                                .titleTextColor(R.color.white)
                                .descriptionTextSize(12)
                                .descriptionTextColor(R.color.white)
                                .textColor(R.color.white)
                                .textTypeface(Typeface.SANS_SERIF)
                                .dimColor(R.color.white)
                                .drawShadow(true)
                                .cancelable(false)
                                .tintTarget(true)
                                .transparentTarget(true)
                                .targetRadius(60),
                        TapTarget.forView(searchView,"Search", "Filter data berdasarkan kata kunci")
                                .outerCircleColor(R.color.main_color)
                                .outerCircleAlpha(0.96f)
                                .targetCircleColor(R.color.white)
                                .titleTextSize(26)
                                .titleTextColor(R.color.white)
                                .descriptionTextSize(12)
                                .descriptionTextColor(R.color.white)
                                .textColor(R.color.white)
                                .textTypeface(Typeface.SANS_SERIF)
                                .dimColor(R.color.white)
                                .drawShadow(true)
                                .cancelable(false)
                                .tintTarget(true)
                                .transparentTarget(true)
                                .targetRadius(60)).listener(new TapTargetSequence.Listener() {
                    @Override
                    public void onSequenceFinish() {
                        setOnboardingCompleted();
                    }

                    @Override
                    public void onSequenceStep(TapTarget lastTarget, boolean targetClicked) {

                    }

                    @Override
                    public void onSequenceCanceled(TapTarget lastTarget) {

                    }
                }).start();
    }

    private boolean isOnboardingCompleted() {
        SharedPreferences sharedPreferences = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        return sharedPreferences.getBoolean(KEY_ONBOARDING_COMPLETE, false);
    }

    private void setOnboardingCompleted() {
        SharedPreferences sharedPreferences = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(KEY_ONBOARDING_COMPLETE, true);
        editor.apply();
    }

    private void logout() {
        mAuth.signOut();
        Intent intent = new Intent(MainActivity.this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    private void restoreSortingStatus() {
        isAscendingByDate = sharedPreferences.getBoolean("ascending_by_date", true);
        isDescendingByDate = sharedPreferences.getBoolean("descending_by_date", false);
        isAscendingByName = sharedPreferences.getBoolean("ascending_by_name", true);
        isDescendingByName = sharedPreferences.getBoolean("descending_by_name", false);

        if (isAscendingByName || isDescendingByName) {
            sortDataByName();
        } else if (isAscendingByDate || isDescendingByDate) {
            sortDataByDate();
        }
    }

    private void saveSortingStatus() {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean("ascending_by_date", isAscendingByDate);
        editor.putBoolean("descending_by_date", isDescendingByDate);
        editor.putBoolean("ascending_by_name", isAscendingByName);
        editor.putBoolean("descending_by_name", isDescendingByName);
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

    public void searchList(String text) {
        ArrayList<DataClass> searchList = new ArrayList<>();
        for (DataClass dataClass : dataList) {
            if (dataClass.getKodeQR().toLowerCase().contains(text.toLowerCase())
                    || dataClass.getLokasiTabung().toLowerCase().contains(text.toLowerCase())
                    || dataClass.getJenisAPAR().toLowerCase().contains(text.toLowerCase())
                    || dataClass.getDataDate().toLowerCase().contains(text.toLowerCase())) {
                searchList.add(dataClass);
            }
        }
        adapter.searchDataList(searchList);
        restoreSortingStatus();
    }

    private void initActivityResultLaunchers() {
        requestPermissionLauncher = registerForActivityResult(new ActivityResultContracts.RequestPermission(), new ActivityResultCallback<Boolean>() {
            @Override
            public void onActivityResult(Boolean isGranted) {
                if (isGranted) {
                    showCamera();
                } else {
                    Toast.makeText(MainActivity.this, "Beri akses kamera untuk aplikasi", Toast.LENGTH_SHORT).show();
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
                        intent.putExtra("User", data.getUser().toString());
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
                Toast.makeText(MainActivity.this, "Database Error!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showCamera() {
        ScanOptions options = new ScanOptions();
        options.setDesiredBarcodeFormats(ScanOptions.QR_CODE);
        options.setDesiredBarcodeFormats(ScanOptions.ALL_CODE_TYPES);
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
        options.setDesiredBarcodeFormats(ScanOptions.ALL_CODE_TYPES);
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

    private void requestStoragePermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            try {
                Intent intent = new Intent(Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION);
                startActivityForResult(intent, PERMISSION_STORAGE_CODE);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, PERMISSION_STORAGE_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_STORAGE_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            } else {
            }
        }
    }

    public static boolean isNetworkStatusAvialable (Context context) {
        ConnectivityManager cm =
                (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        return activeNetwork != null &&
                activeNetwork.isConnectedOrConnecting();
    }

    private void saveUserLocally(String username) {
        SharedPreferences sharedPreferences = getSharedPreferences("user_data", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("username", username);
        editor.apply();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PERMISSION_STORAGE_CODE) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R && Environment.isExternalStorageManager()) {
                downloadFile();
            } else {
                Toast.makeText(this, "Izin penyimpanan dibutuhkan untuk mengunduh file.", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
