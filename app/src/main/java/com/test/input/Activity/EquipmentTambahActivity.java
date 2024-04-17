package com.test.input.Activity;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
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
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
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
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.journeyapps.barcodescanner.ScanContract;
import com.journeyapps.barcodescanner.ScanOptions;
import com.test.input.DataClass;
import com.test.input.R;

import java.io.ByteArrayOutputStream;
import java.text.DateFormat;
import java.util.Calendar;

public class EquipmentTambahActivity extends AppCompatActivity {

    private EditText etResult, etLokasi, etBerat, etketerangan;
    private SwitchMaterial isiTabung, tekananTabung, kesesuaianBerat, kondisiTabung, kondisiSelang, kondisiPin, kondisiNozzle, posisiTabung;
    private Spinner merkAPAR, jenisAPAR;
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

    private Uri getImageUri(Context context, Bitmap bitmap) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(context.getContentResolver(), bitmap, "Title", null);
        return Uri.parse(path);
    }

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_equipment_tambah);
        firebaseAuth = FirebaseAuth.getInstance();

        initializeUI();

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
                Intent photoPicker = new Intent(Intent.ACTION_PICK);
                photoPicker.setType("image/*");
                activityResultLauncher.launch(photoPicker);
            }
        });

        btnImage.setOnClickListener(View -> dispatchTakePictureIntent(takePictureLauncher));

        initActivityResultLaunchers();

        btnUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveData();
            }
        });

        btnHelp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                scrollView.smoothScrollTo(0, 0);

                // Tampilkan onboarding setelah ScrollView selesai digulirkan ke atas
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
            Toast.makeText(EquipmentTambahActivity.this, "Pilih gambar terlebih dahulu", Toast.LENGTH_SHORT).show();
            return;
        }

        StorageReference storageReference = FirebaseStorage.getInstance().getReference().child("Android Images")
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
        String finalUser = users.getEmail();

        String currentDate = DateFormat.getDateTimeInstance().format(Calendar.getInstance().getTime());

        if (kodeQR.isEmpty()) {
            Toast.makeText(EquipmentTambahActivity.this, "Kode QR tidak boleh kosong", Toast.LENGTH_SHORT).show();
            return;
        }
        if (uri == null) {
            Toast.makeText(EquipmentTambahActivity.this, "Gambar harus di pilih", Toast.LENGTH_SHORT).show();
            return;
        }

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
                kondisiString,selangString, pinString, keterangan, imageURL, currentDate, finalUser, nozzleString, posisiString);

        FirebaseDatabase.getInstance().getReference("Test").child(kodeQR).setValue(dataClass).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()){
                    Toast.makeText(EquipmentTambahActivity.this, "Saved", Toast.LENGTH_SHORT).show();
                    finish();
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(EquipmentTambahActivity.this, e.getMessage().toString(), Toast.LENGTH_SHORT).show();
            }
        });
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
        // Dapatkan posisi vertikal elemen terakhir yang ditargetkan
        int lastTargetYPosition = btnUpload.getTop(); // Ganti dengan elemen terakhir yang ditargetkan

        // Gulirkan tampilan ke posisi elemen terakhir yang ditargetkan
        scrollView.smoothScrollTo(0, lastTargetYPosition);
    }
}