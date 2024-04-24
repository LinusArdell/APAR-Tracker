package com.test.input.Activity;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
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
import java.text.DateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

public class EquipmentTambahActivity extends AppCompatActivity {

    private EditText etResult, etLokasi, etBerat, etketerangan;
    private SwitchMaterial tekananTabung, kesesuaianBerat, isiTabung, kondisiSelang, kondisiPin, kondisiNozzle, posisiTabung;
    private Spinner merkAPAR, jenisAPAR;
    private SwitchMaterial kondisiTabung;
    private Button btnUpload;
    private ImageView uploadGambar;
    private ImageButton btnImage, btnBack, btnHelp, btnQR;
    private String imageURL;
    private Uri uri;
    ScrollView scrollView;
    private ActivityResultLauncher<String> requestPermissionLauncher;
    private ActivityResultLauncher<ScanOptions> qrCodeLauncher;
    private FirebaseAuth firebaseAuth;

    private static final String PREFS_NAME = "MyPrefsFile";
    private static final String KEY_ONBOARDING_COMPLETE = "onboarding_complete";

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

//        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);

        setContentView(R.layout.activity_equipment_tambah);

//        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
//                WindowManager.LayoutParams.FLAG_FULLSCREEN);

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
                "Tonanta");

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
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        Bundle extras = result.getData().getExtras();
                        Bitmap imageBitmap = (Bitmap) extras.get("data");
                        uploadGambar.setImageBitmap(imageBitmap);
                        uri = getImageUri(this, imageBitmap);
                    } else {
                        Toast.makeText(EquipmentTambahActivity.this, "No Image Selected", Toast.LENGTH_SHORT).show();
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
                            Toast.makeText(EquipmentTambahActivity.this, "No Image Selected", Toast.LENGTH_SHORT).show();
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
    }

    public void saveData() {
        if (uri == null) {
            Toast.makeText(EquipmentTambahActivity.this, "Gambar wajib dipilih", Toast.LENGTH_SHORT).show();
            return;
        }

        StorageReference storageReference = FirebaseStorage.getInstance().getReference().child("Android Images")
//        StorageReference storageReference = FirebaseStorage.getInstance().getReference().child("Image Test")
                .child(uri.getLastPathSegment());

        AlertDialog.Builder builder = new AlertDialog.Builder(EquipmentTambahActivity.this);
        builder.setCancelable(false);
        builder.setView(R.layout.progress_layout);
        AlertDialog dialog = builder.create();
        dialog.show();

        storageReference.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Task<Uri> uriTask = taskSnapshot.getStorage().getDownloadUrl();
                while (!uriTask.isComplete());
                Uri urlImage = uriTask.getResult();
                imageURL = urlImage.toString();
                uploadData();
                dialog.dismiss();
                Log.d("URI_Log", "URI: " + uri.toString());
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                dialog.dismiss();
            }
        });
    }

    private void uploadData() {
        String kodeQR = etResult.getText().toString().trim();
        String lokasi = etLokasi.getText().toString();
        String berat = etBerat.getText().toString();
        String keterangan = etketerangan.getText().toString();

        String MerkAPAR = merkAPAR.getSelectedItem().toString();
        String JenisAPAR = jenisAPAR.getSelectedItem().toString();

        Boolean isitabung = isiTabung.isChecked();
        Boolean tekanan = tekananTabung.isChecked();
        Boolean kesesuaian = kesesuaianBerat.isChecked();
        Boolean kondisi = kondisiTabung.isChecked();
        Boolean selang = kondisiSelang.isChecked();
        Boolean pin = kondisiPin.isChecked();
        Boolean nozzle = kondisiNozzle.isChecked();
        Boolean posisi = posisiTabung.isChecked();

        final FirebaseUser users = firebaseAuth.getCurrentUser();
        final String[] finalUser = {""}; // Inisialisasi finalUser

        if (users != null) {
            String userId = users.getUid();
            DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("users").child(userId);

            if (kodeQR.isEmpty()) {
                etResult.setError("Kode QR tidak boleh kosong");
                Toast.makeText(EquipmentTambahActivity.this, "Kode QR tidak boleh kosong", Toast.LENGTH_SHORT).show();
                return;
            }

            if (containsIllegalCharacters(kodeQR)) {
                etResult.setError("Kode QR tidak boleh mengandung karakter '.', '#', '$', '[', atau ']'");
                Toast.makeText(EquipmentTambahActivity.this, "Kode QR tidak boleh mengandung karakter '.', '#', '$', '[', atau ']'", Toast.LENGTH_SHORT).show();
                return;
            }

            if (uri == null) {
                Toast.makeText(EquipmentTambahActivity.this, "Gambar harus di pilih", Toast.LENGTH_SHORT).show();
                return;
            }
            userRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        UserClass userData = snapshot.getValue(UserClass.class);
                        if (userData != null) {
                            finalUser[0] = userData.getUsername();

                            String currentDate = DateFormat.getDateTimeInstance().format(Calendar.getInstance().getTime());

                            String isiString = isitabung ? "Baik" : "Beku";
                            String tekananString = tekanan ? "Cukup" : "Kurang";
                            String kesesuaianString = kesesuaian ? "Cukup" : "Kurang";
                            String kondisiString = kondisi ? "Baik" : "Berkarat";
                            String selangString = selang ? "Baik" : "Rusak";
                            String pinString = pin ? "Baik" : "Rusak";
                            String nozzleString = nozzle ? "Baik" : "Tersumbat";
                            String posisiString = posisi ? "Baik" : "Terhalang";

                            if (jenisAPAR.equals("Carbondioxide")){
                                kesesuaianString = kesesuaian ? "Cukup" : "Kurang";
                            } else {
                                kesesuaianString = "N/A";
                            }

                            DataClass dataClass = new DataClass(kodeQR, lokasi, MerkAPAR, berat, JenisAPAR, isiString, tekananString, kesesuaianString,
                                    kondisiString,selangString, pinString, keterangan, imageURL, currentDate, finalUser[0], nozzleString, posisiString);//signatureUrl

                            FirebaseDatabase.getInstance().getReference("Test").child(kodeQR).setValue(dataClass).addOnCompleteListener(new OnCompleteListener<Void>() {
//                            FirebaseDatabase.getInstance().getReference("Draft").child(kodeQR).setValue(dataClass).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()){
                                        Toast.makeText(EquipmentTambahActivity.this, "Saved", Toast.LENGTH_SHORT).show();
                                        Log.d(String.valueOf(uri), "Uri");
                                        finish();
                                    }
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(EquipmentTambahActivity.this, "Database Error", Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Toast.makeText(EquipmentTambahActivity.this, "Cannot connect to database", Toast.LENGTH_SHORT).show();
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

        Boolean isiTabungs = isiTabung.isChecked();
        Boolean Tekanan = tekananTabung.isChecked();
        Boolean skesesuaianBerat = kesesuaianBerat.isChecked();
        Boolean skondisiTabung = kondisiTabung.isChecked();
        Boolean skondisiSelang = kondisiSelang.isChecked();
        Boolean kondisiPins = kondisiPin.isChecked();
        Boolean nozzles = kondisiNozzle.isChecked();
        Boolean posisi = posisiTabung.isChecked();

        String keterangan = etketerangan.getText().toString();

        String currentDate = DateFormat.getDateTimeInstance().format(Calendar.getInstance().getTime());

        String username = getUsernameLocally();

        if (username != null && uri != null) {
            // Lanjutkan menyimpan data ke SharedPreferences dengan nama pengguna yang diperoleh
            String isiString = isiTabungs ? "Baik" : "Beku";
            String tekananString = Tekanan ? "Cukup" : "Kurang";
            String kesesuaianString = skesesuaianBerat ? "Cukup" : "Kurang";
            String kondisiString = skondisiTabung ? "Baik" : "Berkarat";
            String selangString = skondisiSelang ? "Baik" : "Rusak";
            String pinString = kondisiPins ? "Baik" : "Rusak";
            String nozzleString = nozzles ? "Baik" : "Tersumbat";
            String posisiString = posisi ? "Baik" : "Terhalang";

            saveDataToSharedPreferences(kodeQR, lokasiTabung, merkAPAr, beratTabung, jenisAPAr, isiString, tekananString, kesesuaianString,
                    kondisiString, selangString, pinString, keterangan, uri, currentDate, username, nozzleString, posisiString);

            Toast.makeText(EquipmentTambahActivity.this, "Data tersimpan kedalam draft", Toast.LENGTH_SHORT).show();

            boolean isSaved = editor.commit(); // Simpan perubahan ke SharedPreferences
            if (isSaved) {
                Log.d("DataSave", "Data saved successfully");
            } else {
                Log.d("DataSave", "Failed to save data");
            }

            Intent intent = new Intent(EquipmentTambahActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
            Toast.makeText(EquipmentTambahActivity.this, "Data tersimpan dalam draft", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(EquipmentTambahActivity.this, "Gambar harus diisi", Toast.LENGTH_SHORT).show();
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
                                             String kondisiPin, String keterangan, Uri dataImage, String dataDate, String user, String kondisiNozzle, String posisiTabung) {

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
            takePictureLauncher.launch(takePictureIntent);
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
}