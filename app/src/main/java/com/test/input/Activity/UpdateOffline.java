package com.test.input.Activity;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
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
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.material.switchmaterial.SwitchMaterial;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.storage.StorageReference;
import com.test.input.R;

import java.io.ByteArrayOutputStream;
import java.text.DateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.Map;

public class UpdateOffline extends AppCompatActivity {

    ImageView updateImage;
    Button updateButton, btnCancel, btnSave;
    ImageButton btnCapture;
    String kodeQr;
    String imageUrl;
    String key, oldImageURL;
    Uri uri;
    private FirebaseAuth firebaseAuth;
    private ActivityResultLauncher<String> requestPermissionLauncher;

    private SwitchMaterial isiTabung, tekananTabung, kesesuaianBerat, kondisiTabung, kondisiSelang, kondisiPin, kondisiNozzle, posisiTabung;
    private Spinner merkAPARs, jenisAPAR, satuanBerat;
    private EditText etLokasi, etBerat, etketerangan;
    private TextView tvQR, tvID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_offline);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }

        iniSialisasiUi();

        merkAPARs = findViewById(R.id.update_merk);
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
        merkAPARs.setAdapter(mArrayAdapter);

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

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferences sharedPreferences = getSharedPreferences("data_offline", MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();

                String kodeQR = tvQR.getText().toString();
                String lokasiTabung = etLokasi.getText().toString();
                String merkAPAR = merkAPARs.getSelectedItem().toString();
                String beratTabung = etBerat.getText().toString();
                String jenisAPAr = jenisAPAR.getSelectedItem().toString();
                String SatuanBerat = satuanBerat.getSelectedItem().toString();

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

                    saveDataToSharedPreferences(kodeQR, lokasiTabung, merkAPAR, beratTabung, jenisAPAr, isiString, tekananString, kesesuaianString,
                            kondisiString, selangString, pinString, keterangan, uri, currentDate, username, nozzleString, posisiString, SatuanBerat);

                    Toast.makeText(UpdateOffline.this, "Data tersimpan", Toast.LENGTH_SHORT).show();

                    boolean isSaved = editor.commit(); // Simpan perubahan ke SharedPreferences
                    if (isSaved) {

                        Log.d("DataSave", "Data saved successfully");
                    } else {
                        Log.d("DataSave", "Failed to save data");
                    }

                    Intent intent = new Intent(UpdateOffline.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                    Toast.makeText(UpdateOffline.this, "Data tersimpan dalam draft", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(UpdateOffline.this, "Gambar harus diisi", Toast.LENGTH_SHORT).show();
                }
            }
        });

        ActivityResultLauncher<Intent> activityResultLauncher= registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult o) {
                        if (o.getResultCode() == Activity.RESULT_OK) {
                            Intent data = o.getData();
                            uri= data.getData();
                            updateImage.setImageURI(uri);
                        } else {
                            Toast.makeText(UpdateOffline.this, "No Image Selected", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

        ActivityResultLauncher<Intent> takePictureLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(), result -> {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        Bundle extras = result.getData().getExtras();
                        Bitmap imageBitmap = (Bitmap) extras.get("data");
                        updateImage.setImageBitmap(imageBitmap);
                        uri = getImageUri(this, imageBitmap);
                    } else {
                        Toast.makeText(UpdateOffline.this, "No Image Selected", Toast.LENGTH_SHORT).show();
                    }
                });

        Bundle bundle = getIntent().getExtras();
        if (bundle != null){
            tvQR.setText(bundle.getString("KodeQR"));

            Glide.with(UpdateOffline.this).load(bundle.getString("Image")).into(updateImage);

            etLokasi.setText(bundle.getString("Lokasi"));
            etBerat.setText(bundle.getString("Berat"));
            etketerangan.setText(bundle.getString("Keterangan"));

            String merkAparValue = bundle.getString("Merk");
            String jenisAparValue = bundle.getString("Jenis");
            String satuanBeratValue = bundle.getString("Satuan");

            if (merkAparValue != null) {
                int merkAparPosition = getIndexSpinner(merkAPARs, merkAparValue);
                if (merkAparPosition != -1) {
                    merkAPARs.setSelection(merkAparPosition);
                }
            }

            if (jenisAparValue != null) {
                int jenisAparPosition = getIndexSpinner(jenisAPAR, jenisAparValue);
                if (jenisAparPosition != -1) {
                    jenisAPAR.setSelection(jenisAparPosition);
                }
            }

            if (satuanBeratValue != null) {
                int satuanBeratPosition = getIndexSpinner(satuanBerat, satuanBeratValue);
                if (satuanBeratPosition != -1) {
                    satuanBerat.setSelection(satuanBeratPosition);
                }
            }

            String isiTabungString = bundle.getString("IsiTabung");
            String tekananString = bundle.getString("Tekanan");
            String kesesuaianString = bundle.getString("Kesesuaian");
            String kondisiString = bundle.getString("KondisiTabung");
            String selangString = bundle.getString("KondisiSelang");
            String pinString = bundle.getString("KondisiPin");

            String nozzleString = bundle.getString("Nozzle");
            String posisiString = bundle.getString("Posisi");

            boolean isiTabungBoolean = isiTabungString.equals("Baik");
            boolean tekananBoolean = tekananString.equals("Cukup");
            boolean kesesuaianBoolean = kesesuaianString.equals("Cukup");
            boolean kondisiBoolean = kondisiString.equals("Baik");
            boolean selangBoolean = selangString.equals("Baik");
            boolean pinBoolean = pinString.equals("Baik");

            boolean nozzleBoolean = nozzleString.equals("Baik");
            boolean posisiBoolean = posisiString.equals("Baik");

            isiTabung.setChecked(isiTabungBoolean);
            tekananTabung.setChecked(tekananBoolean);
            kesesuaianBerat.setChecked(kesesuaianBoolean);
            kondisiTabung.setChecked(kondisiBoolean);
            kondisiSelang.setChecked(selangBoolean);
            kondisiPin.setChecked(pinBoolean);

            kondisiNozzle.setChecked(nozzleBoolean);
            posisiTabung.setChecked(posisiBoolean);

            key = bundle.getString("Key");
            oldImageURL = bundle.getString("Image");

            tvID.setText("Update " + bundle.getString("KodeQR"));
        }

        updateImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent photoPicker = new Intent(Intent.ACTION_OPEN_DOCUMENT);
                photoPicker.setType("image/*");
                activityResultLauncher.launch(photoPicker);
            }
        });

        updateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferences sharedPreferences = getSharedPreferences("data_offline", MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();

                String kodeQR = tvQR.getText().toString();
                String lokasiTabung = etLokasi.getText().toString();
                String merkAPAR = merkAPARs.getSelectedItem().toString();
                String beratTabung = etBerat.getText().toString();
                String JenisAPArs = jenisAPAR.getSelectedItem().toString();
                String SatuanBerat = satuanBerat.getSelectedItem().toString();

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

                    if (JenisAPArs.equals("Carbondioxide")){
                        kesesuaianString = skesesuaianBerat ? "Cukup" : "Kurang";
                    } else {
                        kesesuaianString = "N/A";
                    }

                    saveDataToSharedPreferences(kodeQR, lokasiTabung, merkAPAR, beratTabung, JenisAPArs, isiString, tekananString, kesesuaianString,
                            kondisiString, selangString, pinString, keterangan, uri, currentDate, username, nozzleString, posisiString, SatuanBerat);

                    Toast.makeText(UpdateOffline.this, "Data tersimpan dalam draft", Toast.LENGTH_SHORT).show();
                    displayAllDataFromSharedPreferences();

                    boolean isSaved = editor.commit();
                    if (isSaved) {

                        Log.d("DataSave", "Data saved successfully");
                    } else {
                        Log.d("DataSave", "Failed to save data");
                    }

                    Intent intent = new Intent(UpdateOffline.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                } else {
                    Toast.makeText(UpdateOffline.this, "Gambar harus diisi", Toast.LENGTH_SHORT).show();
                }
            }
        });

        btnCapture.setOnClickListener(View -> dispatchTakePictureIntent(takePictureLauncher));

        requestPermissionLauncher = registerForActivityResult(
                new ActivityResultContracts.RequestPermission(), isGranted -> {
                    if (isGranted) {
                        Toast.makeText(UpdateOffline.this, "Access Granted", Toast.LENGTH_SHORT).show();
//                        dispatchTakePictureIntent(takePictureLauncher);
                    } else {
                        Toast.makeText(UpdateOffline.this, "Camera permission required", Toast.LENGTH_SHORT).show();
                    }
                }
        );
    }

    private void iniSialisasiUi() {
        merkAPARs = findViewById(R.id.update_merk);
        jenisAPAR = findViewById(R.id.update_jenis);
        etLokasi = findViewById(R.id.update_lokasi);
        etBerat = findViewById(R.id.update_berat);
        etketerangan = findViewById(R.id.update_keterangan);
        isiTabung = findViewById(R.id.update_isi);
        tekananTabung = findViewById(R.id.update_tekanan);
        kesesuaianBerat = findViewById(R.id.update_kesesuaian);
        kondisiTabung = findViewById(R.id.update_tabung);
        kondisiSelang = findViewById(R.id.update_selang);
        kondisiPin = findViewById(R.id.update_pin);
        btnCapture = findViewById(R.id.btn_capture);
        firebaseAuth = FirebaseAuth.getInstance();
        updateButton = findViewById(R.id.btn_update);
        updateImage = findViewById(R.id.update_image);
        btnCancel = findViewById(R.id.btn_cancel);
        btnSave = findViewById(R.id.btn_save);
        satuanBerat = findViewById(R.id.upload_satuan);
        kondisiNozzle = findViewById(R.id.update_nozzle);
        posisiTabung = findViewById(R.id.update_posisi);

        tvQR = findViewById(R.id.tv_qr_update);
        tvID = findViewById(R.id.tv_title_id);
    }

    private Uri getImageUri(Context context, Bitmap bitmap) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(context.getContentResolver(), bitmap, "Title", null);
        return Uri.parse(path);
    }

    private int getIndexSpinner(Spinner spinner, String value) {
        for (int i = 0; i < spinner.getCount(); i++) {
            if (spinner.getItemAtPosition(i).toString().equalsIgnoreCase(value)) {
                return i;
            }
        }
        return -1;
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

    private void dispatchTakePictureIntent(ActivityResultLauncher<Intent> takePictureLauncher) {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            takePictureLauncher.launch(takePictureIntent);
        } else {
            Toast.makeText(this, "No camera app available", Toast.LENGTH_SHORT).show();
        }
    }

    private void displayAllDataFromSharedPreferences() {
        SharedPreferences sharedPreferences = getSharedPreferences("data_offline", MODE_PRIVATE);
        Map<String, ?> allEntries = sharedPreferences.getAll();
        for (Map.Entry<String, ?> entry : allEntries.entrySet()) {
            Log.d("SharedPreferencesData", entry.getKey() + ": " + entry.getValue().toString());
        }
    }
}