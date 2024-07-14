package com.test.input.Activity;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.Toast;

import com.getkeepsafe.taptargetview.TapTarget;
import com.getkeepsafe.taptargetview.TapTargetSequence;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.switchmaterial.SwitchMaterial;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.journeyapps.barcodescanner.ScanContract;
import com.journeyapps.barcodescanner.ScanOptions;
import com.test.input.Class.DataClass;
import com.test.input.R;
import com.test.input.Class.UserClass;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class TambahData extends AppCompatActivity {

    private EditText etResult, etLokasi, etBerat, etketerangan;
    private SwitchMaterial tekananTabung, kesesuaianBerat, isiTabung, kondisiSelang, kondisiPin, kondisiNozzle, posisiTabung;
    private Spinner merkAPAR, jenisAPAR, satuanBerat;
    private SwitchMaterial kondisiTabung;
    private Button btnUpload, btnBack;
    private ImageView uploadGambar;
    private ImageButton btnImage, btnHelp, btnQR;
    private String imageURL, historyImageUrl;
    private Uri uri;
    ScrollView scrollView;
    private ActivityResultLauncher<String> requestPermissionLauncher;
    private ActivityResultLauncher<ScanOptions> qrCodeLauncher;
    private FirebaseAuth firebaseAuth;

    private static final String PREFS_NAME = "MyPrefsFile";
    private static final String KEY_ONBOARDING_COMPLETE = "onboarding_complete";

    private static final int REQUEST_TAKE_PHOTO = 1;
    private String currentPhotoPath;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_equipment_tambah);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }

        firebaseAuth = FirebaseAuth.getInstance();

        initializeUI();

        List<String> mList = Arrays.asList("Appron",
                "Yamato",
                "Garra Fire",
                "321 Stop",
                "Gunnebo",
                "Hercules",
                "Chubb",
                "Pyrosafe",
                "Pina",
                "Altek",
                "Holly Fire",
                "Chubb Fire",
                "Tonanta",
                "Starvvo");

        ArrayAdapter<String> mArrayAdapter = new ArrayAdapter<String>(this, R.layout.spinner_list_merk, mList);
        mArrayAdapter.setDropDownViewResource(R.layout.spinner_list_merk);
        merkAPAR.setAdapter(mArrayAdapter);

        List<String> aList = Arrays.asList("Multi Purpose Dry Chemical",
                "Carbondioxide",
                "Halotron",
                "HF 11",
                "Dry chemical powder",
                "Abc Chemical multi purpose");
        ArrayAdapter<String> aArrayAdapter = new ArrayAdapter<String>(this, R.layout.spinner_list_jenis, aList);
        aArrayAdapter.setDropDownViewResource(R.layout.spinner_list_jenis);
        jenisAPAR.setAdapter(aArrayAdapter);

        List<String> bList = Arrays.asList("Kilogram", "Liter");
        ArrayAdapter<String> bArrayAdapter = new ArrayAdapter<>(this, R.layout.spinner_list_satuan, bList);
        bArrayAdapter.setDropDownViewResource(R.layout.spinner_list_satuan);
        satuanBerat.setAdapter(bArrayAdapter);

        findViewById(R.id.btn_upload_qr).setOnClickListener(view -> checkPermissionAndShowActivity(this));

        String qrResult = getIntent().getStringExtra("KodeQR");
        if (qrResult != null){
            etResult.setText(qrResult);
        }

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        ActivityResultLauncher<Intent> takePictureLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(), result -> {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        if (result.getData() != null && result.getData().getExtras() != null) {
                            Bundle extras = result.getData().getExtras();
                            Bitmap imageBitmap = (Bitmap) extras.get("data");
                            uploadGambar.setImageBitmap(imageBitmap);
                            uri = getImageUri(this, imageBitmap);
                        } else {
                            Bitmap imageBitmap = BitmapFactory.decodeFile(currentPhotoPath);
                            uploadGambar.setImageBitmap(imageBitmap);
                            uri = Uri.fromFile(new File(currentPhotoPath));
                        }
                    } else {
                        Toast.makeText(TambahData.this, "No Image Selected", Toast.LENGTH_SHORT).show();
                    }
                });

        ActivityResultLauncher<Intent> activityResultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {
                        if (result.getResultCode() == Activity.RESULT_OK){
                            Intent data = result.getData();
                            uri =data.getData();
                            uploadGambar.setImageURI(uri);
                        } else {
                            Toast.makeText(TambahData.this, "No Image Selected", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

        uploadGambar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent photoPicker = new Intent(Intent.ACTION_OPEN_DOCUMENT);
                photoPicker.setType("image/*");
                activityResultLauncher.launch(photoPicker);
            }
        });

        btnImage.setOnClickListener(View -> dispatchTakePictureIntent(takePictureLauncher));

        initActivityResultLaunchers();

        btnUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (isNetworkStatusAvialable(getApplicationContext())){
                    saveData();
                } else {
                    saveAsDraft();
                }

            }
        });

        btnHelp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                scrollView.smoothScrollTo(0, 0);
                scrollView.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        showOnboarding();
                    }
                }, 500);
            }
        });

        if (!isOnboardingCompleted()) {
            showOnboarding();
        }
    }

    private void initializeUI() {
        etResult = findViewById(R.id.upload_qr);
        uploadGambar = findViewById(R.id.upload_image);
        btnUpload = findViewById(R.id.btn_upload);
        btnImage = findViewById(R.id.btn_capture);
        isiTabung = findViewById(R.id.upload_isi);
        tekananTabung = findViewById(R.id.upload_tekanan);
        kesesuaianBerat = findViewById(R.id.upload_kesesuaian);
        kondisiTabung = findViewById(R.id.upload_kondisi);
        kondisiSelang = findViewById(R.id.upload_selang);
        kondisiPin = findViewById(R.id.upload_pin);
        merkAPAR = findViewById(R.id.upload_merk);
        jenisAPAR = findViewById(R.id.upload_jenis);
        etketerangan = findViewById(R.id.upload_keterangan);
        etLokasi = findViewById(R.id.upload_lokasi);
        etBerat = findViewById(R.id.upload_berat);
        kondisiNozzle = findViewById(R.id.upload_nozzle);
        posisiTabung = findViewById(R.id.upload_posisi);
        btnBack = findViewById(R.id.btn_back);
        btnHelp = findViewById(R.id.btn_help);
        btnQR = findViewById(R.id.btn_upload_qr);
        scrollView = findViewById(R.id.scrollView);
        satuanBerat = findViewById(R.id.upload_satuan);
    }

    public void saveData() {
        if (uri == null) {
            Toast.makeText(TambahData.this, "Gambar wajib dipilih", Toast.LENGTH_SHORT).show();
            return;
        }

        StorageReference historyStorage = FirebaseStorage.getInstance().getReference().child("History Images").child(uri.getLastPathSegment());

        AlertDialog.Builder builder = new AlertDialog.Builder(TambahData.this);
        builder.setCancelable(false);
        builder.setView(R.layout.progress_layout);
        AlertDialog dialog = builder.create();
        dialog.show();

        historyStorage.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Task<Uri> uriTask = taskSnapshot.getStorage().getDownloadUrl();
                while (!uriTask.isComplete());
                Uri urlImages = uriTask.getResult();
                historyImageUrl = urlImages.toString();
                uploadData();
                dialog.dismiss();
            }
        });
    }

    private void uploadData() {
        String kodeQR = etResult.getText().toString();
        String lokasi = etLokasi.getText().toString();
        int berat;
        try {
            berat = Integer.parseInt(etBerat.getText().toString());
        } catch (NumberFormatException e) {
            etBerat.setError("Berat harus berupa angka");
            Toast.makeText(TambahData.this, "Berat harus berupa angka", Toast.LENGTH_SHORT).show();
            return;
        }
        String keterangan = etketerangan.getText().toString();

        String SatuanBerat = satuanBerat.getSelectedItem().toString();
        String MerkAPAR = merkAPAR.getSelectedItem().toString();
        String JenisAPAR = jenisAPAR.getSelectedItem().toString();

        Boolean isitabung = isiTabung.isChecked();
        Boolean tekanan = tekananTabung.isChecked();
        Boolean kesesuaian;
        if (JenisAPAR.equals("Carbondioxide")) {
            kesesuaian = kesesuaianBerat.isChecked();
        } else {
            kesesuaian = false;
        }
        Boolean kondisi = kondisiTabung.isChecked();
        Boolean selang = kondisiSelang.isChecked();
        Boolean pin = kondisiPin.isChecked();
        Boolean nozzle = kondisiNozzle.isChecked();
        Boolean posisi = posisiTabung.isChecked();

        final FirebaseUser users = firebaseAuth.getCurrentUser();
        final String[] finalUser = {""}; // Inisialisasi finalUser

        if (users != null) {
            String userId = users.getUid();
            DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("Pengguna").child(userId);

            if (kodeQR.isEmpty()) {
                etResult.setError("Kode QR tidak boleh kosong");
                Toast.makeText(TambahData.this, "Kode QR tidak boleh kosong", Toast.LENGTH_SHORT).show();
                return;
            }

            if (containsIllegalCharacters(kodeQR)) {
                etResult.setError("Kode QR tidak boleh mengandung karakter '.', '#', '$', '[', atau ']'");
                Toast.makeText(TambahData.this, "Kode QR tidak boleh mengandung karakter '.', '#', '$', '[', atau ']'", Toast.LENGTH_SHORT).show();
                return;
            }

            if (uri == null) {
                Toast.makeText(TambahData.this, "Gambar harus di pilih", Toast.LENGTH_SHORT).show();
                return;
            }
            userRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        UserClass userData = snapshot.getValue(UserClass.class);
                        if (userData != null) {
                            finalUser[0] = userData.getUsername();

                            SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yyyy HH.mm.ss", Locale.US);

                            SimpleDateFormat sdfs = new SimpleDateFormat("dd MMM yyyy", Locale.US);
                            String currentDates = sdfs.format(Calendar.getInstance().getTime());

                            SimpleDateFormat months = new SimpleDateFormat("MMM", Locale.US);
                            String currentMonth = months.format(Calendar.getInstance().getTime());

                            String currentDate = sdf.format(Calendar.getInstance().getTime());

                            DataClass dataClass = new DataClass(kodeQR, lokasi, MerkAPAR, berat, JenisAPAR, isitabung, tekanan, kesesuaian,
                                    kondisi,selang, pin, keterangan, historyImageUrl, currentDate, finalUser[0], nozzle, posisi, SatuanBerat, currentDates, currentMonth);

                            DataClass historyData = new DataClass(kodeQR, lokasi, MerkAPAR, berat, JenisAPAR, isitabung, tekanan, kesesuaian,
                                    kondisi,selang, pin, keterangan, historyImageUrl, currentDate, finalUser[0], nozzle, posisi, SatuanBerat, currentDates, currentMonth);

                            FirebaseDatabase.getInstance().getReference("APAR").child(kodeQR).setValue(dataClass).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()){
                                        SimpleDateFormat dateFormat = new SimpleDateFormat("MM dd yy", Locale.US);
                                        Calendar calendar = Calendar.getInstance();
                                        String currentDate = dateFormat.format(calendar.getTime());

                                        String childKey = currentDate + kodeQR;
                                        FirebaseDatabase.getInstance().getReference("Riwayat_Pemeriksaan_APAR").child(kodeQR).child(childKey).setValue(historyData);

                                        Toast.makeText(TambahData.this, "Saved", Toast.LENGTH_SHORT).show();
                                        Log.d(String.valueOf(uri), "Uri");
                                        finish();
                                    }
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(TambahData.this, "Database Error", Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Toast.makeText(TambahData.this, "Cannot connect to database", Toast.LENGTH_SHORT).show();
                }
            });
        }

    }

    private void saveAsDraft() {
        SharedPreferences sharedPreferences = getSharedPreferences("data_offline", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        String kodeQR = etResult.getText().toString();
        String lokasiTabung = etLokasi.getText().toString();
        String merkAPAr = merkAPAR.getSelectedItem().toString();
        String beratTabung = etBerat.getText().toString();
        String jenisAPAr = jenisAPAR.getSelectedItem().toString();
        String satuanberat = satuanBerat.getSelectedItem().toString();

        Boolean isiTabungs = isiTabung.isChecked();
        Boolean Tekanan = tekananTabung.isChecked();
        Boolean skesesuaianBerat = kesesuaianBerat.isChecked();
        Boolean skondisiTabung = kondisiTabung.isChecked();
        Boolean skondisiSelang = kondisiSelang.isChecked();
        Boolean kondisiPins = kondisiPin.isChecked();
        Boolean nozzles = kondisiNozzle.isChecked();
        Boolean posisi = posisiTabung.isChecked();

        String keterangan = etketerangan.getText().toString();

        SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yyyy HH.mm.ss", Locale.US);
        String currentDate = sdf.format(Calendar.getInstance().getTime());

        String username = getUsernameLocally();

        if (username != null && uri != null) {
            String isiString = isiTabungs ? "Baik" : "Beku";
            String tekananString = Tekanan ? "Cukup" : "Kurang";
            String kesesuaianString = skesesuaianBerat ? "Cukup" : "Kurang";
            String kondisiString = skondisiTabung ? "Baik" : "Berkarat";
            String selangString = skondisiSelang ? "Baik" : "Rusak";
            String pinString = kondisiPins ? "Baik" : "Rusak";
            String nozzleString = nozzles ? "Baik" : "Tersumbat";
            String posisiString = posisi ? "Baik" : "Terhalang";

            if (jenisAPAr.equals("Carbondioxide")) {
                kesesuaianString = skesesuaianBerat ? "Cukup" : "Kurang";
            } else {
                kesesuaianString = "N/A";
            }

            saveDataToSharedPreferences(kodeQR, lokasiTabung, merkAPAr, beratTabung, jenisAPAr, isiString, tekananString, kesesuaianString,
                    kondisiString, selangString, pinString, keterangan, uri, currentDate, username, nozzleString, posisiString, satuanberat);

            Toast.makeText(TambahData.this, "Data tersimpan dalam penyimpanan lokal", Toast.LENGTH_SHORT).show();

            boolean isSaved = editor.commit(); // Simpan perubahan ke SharedPreferences
            if (isSaved) {
                Log.d("DataSave", "Data saved successfully");
            } else {
                Log.d("DataSave", "Failed to save data");
            }

            finish();
            Toast.makeText(TambahData.this, "Data tersimpan dalam penyimpanan lokal", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(TambahData.this, "Gambar harus diisi", Toast.LENGTH_SHORT).show();
        }
    }

    public static boolean isNetworkStatusAvialable (Context context) {
        ConnectivityManager cm =
                (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        return activeNetwork != null &&
                activeNetwork.isConnectedOrConnecting();

    }

    private void saveDataToSharedPreferences(String kodeQR, String lokasiTabung, String merkAPAR, String beratTabung, String jenisAPAR,
                                             String isiTabung, String Tekanan, String kesesuaianBerat, String kondisiTabung, String kondisiSelang,
                                             String kondisiPin, String keterangan, Uri dataImage, String dataDate, String user, String kondisiNozzle, String posisiTabung, String SatuanBerat) {

        SharedPreferences sharedPreferences = getSharedPreferences("data_offline", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        String uniqueKey = "imageUri_" + kodeQR + "_";
        editor.putString(uniqueKey, dataImage.toString());

        editor.putString(uniqueKey + "KodeQR", kodeQR);
        editor.putString(uniqueKey + "Lokasi", lokasiTabung);
        editor.putString(uniqueKey + "Merk", merkAPAR);
        editor.putString(uniqueKey + "Berat",beratTabung);
        editor.putString(uniqueKey + "Jenis",jenisAPAR);
        editor.putString(uniqueKey + "IsiTabung",isiTabung);
        editor.putString(uniqueKey + "Tekanan",Tekanan);
        editor.putString(uniqueKey + "Kesesuaian",kesesuaianBerat);
        editor.putString(uniqueKey + "KondisiTabung",kondisiTabung);
        editor.putString(uniqueKey + "KondisiSelang",kondisiSelang);
        editor.putString(uniqueKey + "KondisiPin",kondisiPin);
        editor.putString(uniqueKey + "Keterangan",keterangan);
        editor.putString(uniqueKey + "Tanggal",dataDate);
        editor.putString(uniqueKey + "User",user);
        editor.putString(uniqueKey + "Nozzle",kondisiNozzle);
        editor.putString(uniqueKey + "Posisi",posisiTabung);
        editor.putString(uniqueKey + "Satuan", SatuanBerat);

        editor.apply();
    }

    private String getUsernameLocally() {
        SharedPreferences sharedPreferences = getSharedPreferences("user_data", MODE_PRIVATE);
        return sharedPreferences.getString("username", "");
    }

    private boolean containsIllegalCharacters(String kodeQR) {
        return kodeQR.contains(".") || kodeQR.contains("#") || kodeQR.contains("$") || kodeQR.contains("[") || kodeQR.contains("]");
    }

    private void initActivityResultLaunchers() {
        requestPermissionLauncher = registerForActivityResult(new ActivityResultContracts.RequestPermission(), new ActivityResultCallback<Boolean>() {
            @Override
            public void onActivityResult(Boolean isGranted) {
                if (isGranted) {
                    showCamera();
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

    private void setResult(String contents) {
        etResult.setText(contents);
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

    private void dispatchTakePictureIntent(ActivityResultLauncher<Intent> takePictureLauncher) {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
            }
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(this,
                        "com.test.input.Fileprovider",
                        photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                takePictureLauncher.launch(takePictureIntent);
            }
        } else {
            Toast.makeText(this, "No camera app available", Toast.LENGTH_SHORT).show();
        }
    }

    private void showOnboarding() {

        new TapTargetSequence(this)
                .targets(
                        TapTarget.forView(uploadGambar,"Pilih", "Pilih gambar dari galeri")
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
                        TapTarget.forView(btnImage,"Capture", "Ambil gambar dengan kamera")
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
                        TapTarget.forView(btnQR,"Scan QR", "Isi kode QR secara otomatis")
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
                        TapTarget.forView(btnUpload,"Simpan", "Klik untuk menyimpan data")
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
                        scrollToShowOnboarding();
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

    private void scrollToShowOnboarding() {
        int lastTargetYPosition = btnUpload.getTop();
        scrollView.smoothScrollTo(0, lastTargetYPosition);
    }

    private Uri getImageUri(Context context, Bitmap bitmap) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(context.getContentResolver(), bitmap, "Title", null);
        return Uri.parse(path);
    }

    private File createImageFile() throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        currentPhotoPath = image.getAbsolutePath();
        return image;
    }
}