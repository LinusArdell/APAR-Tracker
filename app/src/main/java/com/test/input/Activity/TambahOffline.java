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
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.material.switchmaterial.SwitchMaterial;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.journeyapps.barcodescanner.ScanContract;
import com.journeyapps.barcodescanner.ScanOptions;
import com.test.input.Class.UserClass;
import com.test.input.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class TambahOffline extends AppCompatActivity {

    private EditText etResult, etLokasi, etBerat, etketerangan;
    private SwitchMaterial oIsiTabung, oTekananTabung, oKesesuaianBerat, oKondisiTabung, oKondisiSelang, oKondisiPin, oKondisiNozzle, oPosisiTabung;
    private Spinner merkapar, jenisApar, satuanBerat;
    private Button btnSimpan, btnBack;
    private ImageView uploadGambar;
    private ImageButton btnImage, btnHelp, btnQR;
    private String imageURL;
    private Uri uri;
    ScrollView scrollView;
    Dialog dialog;
    private ActivityResultLauncher<String> requestPermissionLauncher;
    private ActivityResultLauncher<ScanOptions> qrCodeLauncher;
    private FirebaseAuth firebaseAuth;
    private static final int PICK_FILE_REQUEST_CODE = 123;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_tambah_offline);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }

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
        merkapar.setAdapter(mArrayAdapter);

        List<String> aList = Arrays.asList("Multi Purpose Dry Chemical",
                "Carbondioxide",
                "Halotron",
                "HF 11",
                "Dry chemical powder",
                "Abc Chemical multi purpose");
        ArrayAdapter<String> aArrayAdapter = new ArrayAdapter<String>(this, R.layout.spinner_list_jenis, aList);
        aArrayAdapter.setDropDownViewResource(R.layout.spinner_list_jenis);
        jenisApar.setAdapter(aArrayAdapter);

        List<String> bList = Arrays.asList("Kilogram", "Liter");
        ArrayAdapter<String> bArrayAdapter = new ArrayAdapter<>(this, R.layout.spinner_list_satuan, bList);
        bArrayAdapter.setDropDownViewResource(R.layout.spinner_list_satuan);
        satuanBerat.setAdapter(bArrayAdapter);

        firebaseAuth = FirebaseAuth.getInstance();
        findViewById(R.id.btn_upload_qr).setOnClickListener(view -> checkPermissionAndShowActivity(this));

        ActivityResultLauncher<Intent> takePictureLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        Bundle extras = result.getData().getExtras();
                        Bitmap imageBitmap = (Bitmap) extras.get("data");
                        uploadGambar.setImageBitmap(imageBitmap);
                        uri = getImageUri(this, imageBitmap);
                    } else {
                        Toast.makeText(TambahOffline.this, "No Image Selected", Toast.LENGTH_SHORT).show();
                    }
                });

        ActivityResultLauncher<Intent> activityResultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {
                        if (result.getResultCode() == Activity.RESULT_OK){
                            Intent data = result.getData();

                            uri = data.getData();
                            uploadGambar.setImageURI(uri);
                        } else {
                            Toast.makeText(TambahOffline.this, "No Image Selected", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

        initActivityResultLaunchers();

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        uploadGambar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent photoPicker = new Intent(Intent.ACTION_OPEN_DOCUMENT);
                photoPicker.addCategory(Intent.CATEGORY_OPENABLE);
                photoPicker.setType("image/*");
                photoPicker.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                photoPicker.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
                startActivityForResult(photoPicker, PICK_FILE_REQUEST_CODE);
                activityResultLauncher.launch(photoPicker);
            }
        });

        btnImage.setOnClickListener(View -> dispatchTakePictureIntent(takePictureLauncher));

        btnSimpan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferences sharedPreferences = getSharedPreferences("data_offline", MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();

                String kodeQR = etResult.getText().toString();
                String lokasiTabung = etLokasi.getText().toString();
                String merkAPAR = merkapar.getSelectedItem().toString();
                String beratTabung = etBerat.getText().toString();
                String jenisAPAR = jenisApar.getSelectedItem().toString();
                String SatuanBerat = satuanBerat.getSelectedItem().toString();

                Boolean isiTabung = oIsiTabung.isChecked();
                Boolean Tekanan = oTekananTabung.isChecked();
                Boolean kesesuaianBerat = oKesesuaianBerat.isChecked();
                Boolean kondisiTabung = oKondisiTabung.isChecked();
                Boolean kondisiSelang = oKondisiSelang.isChecked();
                Boolean kondisiPin = oKondisiPin.isChecked();
                Boolean nozzle = oKondisiNozzle.isChecked();
                Boolean posisi = oPosisiTabung.isChecked();

                String keterangan = etketerangan.getText().toString();

                SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yyyy HH.mm.ss", Locale.US);
                String currentDate = sdf.format(Calendar.getInstance().getTime());

                String username = getUsernameLocally();

                if (username != null && uri != null) {
                    // Lanjutkan menyimpan data ke SharedPreferences dengan nama pengguna yang diperoleh
                    String isiString = isiTabung ? "Baik" : "Beku";
                    String tekananString = Tekanan ? "Cukup" : "Kurang";
                    String kesesuaianString = kesesuaianBerat ? "Cukup" : "Kurang";
                    String kondisiString = kondisiTabung ? "Baik" : "Berkarat";
                    String selangString = kondisiSelang ? "Baik" : "Rusak";
                    String pinString = kondisiPin ? "Baik" : "Rusak";
                    String nozzleString = nozzle ? "Baik" : "Tersumbat";
                    String posisiString = posisi ? "Baik" : "Terhalang";

                    if (jenisAPAR.equals("Carbondioxide")){
                        kesesuaianString = kesesuaianBerat ? "Cukup" : "Kurang";
                    } else {
                        kesesuaianString = "N/A";
                    }

                    saveDataToSharedPreferences(kodeQR, lokasiTabung, merkAPAR, beratTabung, jenisAPAR, isiString, tekananString, kesesuaianString,
                            kondisiString, selangString, pinString, keterangan, uri, currentDate, username, nozzleString, posisiString, SatuanBerat);

                    Toast.makeText(TambahOffline.this, "Data tersimpan dalam penyimpanan lokal", Toast.LENGTH_SHORT).show();
                    displayAllDataFromSharedPreferences();

                    boolean isSaved = editor.commit(); // Simpan perubahan ke SharedPreferences
                    if (isSaved) {
                        Log.d("DataSave", "Data saved successfully");
                    } else {
                        Log.d("DataSave", "Failed to save data");
                    }

                    Intent intent = new Intent(TambahOffline.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                } else {
                    Toast.makeText(TambahOffline.this, "Gambar harus diisi", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @SuppressLint("WrongConstant")
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_FILE_REQUEST_CODE && resultCode == RESULT_OK) {
            Uri uri = data.getData();

            // Request persistent access to the URI
            int takeFlags = data.getFlags()
                    & (Intent.FLAG_GRANT_READ_URI_PERMISSION
                    | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
            // Check for the freshest data.
            getContentResolver().takePersistableUriPermission(uri, takeFlags);
        }
    }

    private void saveDataToSharedPreferences(String kodeQR, String lokasiTabung, String merkAPAR, String beratTabung, String jenisAPAR,
                                             String isiTabung, String Tekanan, String kesesuaianBerat, String kondisiTabung, String kondisiSelang,
                                             String kondisiPin, String keterangan, Uri dataImage, String dataDate, String user, String kondisiNozzle, String posisiTabung, String satuanBerat) {

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
        editor.putString(uniqueKey + "Satuan", satuanBerat);

        editor.apply();
    }

    private String getUsernameLocally() {
        SharedPreferences sharedPreferences = getSharedPreferences("user_data", MODE_PRIVATE);
        return sharedPreferences.getString("username", "");
    }

    private void displayAllDataFromSharedPreferences() {
        SharedPreferences sharedPreferences = getSharedPreferences("data_offline", MODE_PRIVATE);
        Map<String, ?> allEntries = sharedPreferences.getAll();
        for (Map.Entry<String, ?> entry : allEntries.entrySet()) {
            Log.d("SharedPreferencesData", entry.getKey() + ": " + entry.getValue().toString());
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

    private Uri getImageUri(Context context, Bitmap bitmap) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(context.getContentResolver(), bitmap, "Title", null);
        return Uri.parse(path);
    }

    private boolean containsIllegalCharacters(String kodeQR) {
        return kodeQR.contains(".") || kodeQR.contains("#") || kodeQR.contains("$") || kodeQR.contains("[") || kodeQR.contains("]");
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

    private void initializeUI() {
        etResult = findViewById(R.id.upload_qr);
        uploadGambar = findViewById(R.id.upload_image);
        btnSimpan = findViewById(R.id.btn_upload);
        btnImage = findViewById(R.id.btn_capture);
        oIsiTabung = findViewById(R.id.upload_isi);
        oTekananTabung = findViewById(R.id.upload_tekanan);
        oKesesuaianBerat = findViewById(R.id.upload_kesesuaian);
        oKondisiTabung = findViewById(R.id.upload_kondisi);
        oKondisiSelang = findViewById(R.id.upload_selang);
        oKondisiPin = findViewById(R.id.upload_pin);
        merkapar = findViewById(R.id.upload_merk);
        jenisApar = findViewById(R.id.upload_jenis);
        etketerangan = findViewById(R.id.upload_keterangan);
        etLokasi = findViewById(R.id.upload_lokasi);
        etBerat = findViewById(R.id.upload_berat);
        oKondisiNozzle = findViewById(R.id.upload_nozzle);
        oPosisiTabung = findViewById(R.id.upload_posisi);
        btnBack = findViewById(R.id.btn_back);
        btnHelp = findViewById(R.id.btn_help);
        btnQR = findViewById(R.id.btn_upload_qr);
        scrollView = findViewById(R.id.scrollView);
        satuanBerat = findViewById(R.id.upload_satuan);
    }
}