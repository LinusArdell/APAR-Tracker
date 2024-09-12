package com.test.input.Activity;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
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
import com.test.input.Class.DataClass;
import com.test.input.R;
import com.test.input.Class.UserClass;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class UpdateActivity extends AppCompatActivity {

    ImageView updateImage;
    Button updateButton, btnCancel, btnSave;
    ImageButton btnCapture;
    String kodeQr;
    String imageUrl, historyImageUrl;
    String key, oldImageURL;
    Uri uri;
    DatabaseReference databaseReference, historyDatabaseReference;
    StorageReference storageReference, historyStorageReference;
    private FirebaseAuth firebaseAuth;
    private ActivityResultLauncher<String> requestPermissionLauncher;
    private SwitchMaterial isiTabung, tekananTabung, kesesuaianBerat, kondisiTabung, kondisiSelang, kondisiPin, kondisiNozzle, posisiTabung;
    private Spinner merkAPAR, jenisAPAR, satuanBerat;
    private EditText etLokasi, etBerat, etketerangan;
    private TextView tvQR, tvID;

    private static final int REQUEST_TAKE_PHOTO = 1;
    private String currentPhotoPath;
    private FloatingActionButton fab1, fab2, fab3, fab4, fab5, fab6, fab7, fab8;
    private TextView tv1, tv2, tv3, tv4, tv5, tv6, tv7, tv8;

    private Uri getImageUri(Context context, Bitmap bitmap) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 70, bytes);
        String path = MediaStore.Images.Media.insertImage(context.getContentResolver(), bitmap, "Title", null);
        return Uri.parse(path);
    }

    @SuppressLint({"WrongViewCast", "MissingInflatedId"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }

        initializeComponents();
        fabOnClick();

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

        jenisAPAR.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                String selectedItem = parentView.getItemAtPosition(position).toString();

                // Jika pengguna memilih "Halotron", switch isiTabung dinonaktifkan
                if (selectedItem.equals("Halotron") || (selectedItem.equals("Carbondioxide"))) {
                    isiTabung.setEnabled(false);
                    isiTabung.setVisibility(View.GONE);
                    tv7.setVisibility(View.VISIBLE);
                    fab7.setEnabled(false);
                    fab7.setImageResource(com.github.clans.fab.R.drawable.fab_add);

                } else {
                    // Jika item lain dipilih, kembalikan switch isiTabung ke kondisi semula
                    isiTabung.setEnabled(true);
                    isiTabung.setVisibility(View.VISIBLE);
                    tv7.setVisibility(View.GONE);
                    fab7.setEnabled(true);
                    fab7.setImageResource(R.drawable.baseline_remove_24);
                }

                if (selectedItem.equals("Carbondioxide")) {
                    kesesuaianBerat.setEnabled(true);
                    kesesuaianBerat.setVisibility(View.VISIBLE);
                    tv8.setVisibility(View.GONE);
                    fab8.setImageResource(R.drawable.baseline_remove_24);
                    fab8.setEnabled(true);
                } else {
                    kesesuaianBerat.setEnabled(false);
                    kesesuaianBerat.setVisibility(View.GONE);
                    tv8.setVisibility(View.VISIBLE);
                    fab8.setEnabled(false);
                    fab8.setImageResource(com.github.clans.fab.R.drawable.fab_add);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // Tidak melakukan apapun jika tidak ada yang dipilih
            }
        });

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

                if (isNetworkStatusAvialable(getApplicationContext())){
                    saveData();
                } else {
                    saveAsDraft();
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
                            Toast.makeText(UpdateActivity.this, "No Image Selected", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

        ActivityResultLauncher<Intent> takePictureLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(), result -> {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        if (result.getData() != null && result.getData().getExtras() != null) {
                            Bundle extras = result.getData().getExtras();
                            Bitmap imageBitmap = (Bitmap) extras.get("data");
                            updateImage.setImageBitmap(imageBitmap);
                            uri = getImageUri(this, imageBitmap);
                        } else {
                            Bitmap imageBitmap = BitmapFactory.decodeFile(currentPhotoPath);
                            updateImage.setImageBitmap(imageBitmap);
                            uri = Uri.fromFile(new File(currentPhotoPath));
                        }
                    } else {
                        Toast.makeText(UpdateActivity.this, "No Image Selected", Toast.LENGTH_SHORT).show();
                    }
                });

        Bundle bundle = getIntent().getExtras();
        if (bundle != null){
            tvQR.setText(bundle.getString("KodeQR"));

            Glide.with(UpdateActivity.this).load(bundle.getString("Image")).into(updateImage);

            etLokasi.setText(bundle.getString("Lokasi"));
            etBerat.setText(bundle.getString("Berat"));
            etketerangan.setText(bundle.getString("Keterangan"));

            String merkAparValue = bundle.getString("Merk");
            String jenisAparValue = bundle.getString("Jenis");
            String satuanBeratValue = bundle.getString("Satuan");

            if (merkAparValue != null) {
                int merkAparPosition = getIndexSpinner(merkAPAR, merkAparValue);
                if (merkAparPosition != -1) {
                    merkAPAR.setSelection(merkAparPosition);
                }
            }

            if (jenisAparValue != null) {
                int jenisAparPosition = getIndexSpinner(jenisAPAR, jenisAparValue);
                if (jenisAparPosition != -1) {
                    jenisAPAR.setSelection(jenisAparPosition);
                }
            }

            if (satuanBeratValue != null){
                int satuanBeratPosition = getIndexSpinner(satuanBerat, satuanBeratValue);
                if (satuanBeratPosition != -1){
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

            key = bundle.getString("KodeQR");

            oldImageURL = bundle.getString("Image");

            tvID.setText("Update " + bundle.getString("KodeQR"));

            if ("N/A".equals(kondisiString)) {
                kondisiTabung.setEnabled(false);
                kondisiTabung.setVisibility(View.GONE);
                tv1.setVisibility(View.VISIBLE);
                fab1.setImageResource(com.github.clans.fab.R.drawable.fab_add);
            } else {
                boolean kondisiBoolean = kondisiString.equals("Baik");
                kondisiTabung.setChecked(kondisiBoolean);
                kondisiTabung.setEnabled(true);
                kondisiTabung.setVisibility(View.VISIBLE);
                tv1.setVisibility(View.GONE);
                fab1.setImageResource(R.drawable.baseline_remove_24);
            }

            if ("N/A".equals(posisiString)) {
                posisiTabung.setEnabled(false);
                posisiTabung.setVisibility(View.GONE);
                tv2.setVisibility(View.VISIBLE);
                fab2.setImageResource(com.github.clans.fab.R.drawable.fab_add);
            } else {
                boolean posisiBoolean = posisiString.equals("Baik");
                posisiTabung.setChecked(posisiBoolean);
                posisiTabung.setEnabled(true);
                posisiTabung.setVisibility(View.VISIBLE);
                tv2.setVisibility(View.GONE);
                fab2.setImageResource(R.drawable.baseline_remove_24);
            }

            if ("N/A".equals(selangString)) {
                kondisiSelang.setEnabled(false);
                kondisiSelang.setVisibility(View.GONE);
                tv3.setVisibility(View.VISIBLE);
                fab3.setImageResource(com.github.clans.fab.R.drawable.fab_add);
            } else {
                boolean selangBoolean = selangString.equals("Baik");
                kondisiSelang.setChecked(selangBoolean);
                kondisiSelang.setEnabled(true);
                kondisiSelang.setVisibility(View.VISIBLE);
                tv3.setVisibility(View.GONE);
                fab3.setImageResource(R.drawable.baseline_remove_24);
            }

            if ("N/A".equals(pinString)) {
                kondisiPin.setEnabled(false);
                kondisiPin.setVisibility(View.GONE);
                tv4.setVisibility(View.VISIBLE);
                fab4.setImageResource(com.github.clans.fab.R.drawable.fab_add);
            } else {
                boolean pinBoolean = pinString.equals("Baik");
                kondisiPin.setChecked(pinBoolean);
                kondisiPin.setEnabled(true);
                kondisiPin.setVisibility(View.VISIBLE);
                tv4.setVisibility(View.GONE);
                fab4.setImageResource(R.drawable.baseline_remove_24);
            }

            if ("N/A".equals(nozzleString)) {
                kondisiNozzle.setEnabled(false);
                kondisiNozzle.setVisibility(View.GONE);
                tv5.setVisibility(View.VISIBLE);
                fab5.setImageResource(com.github.clans.fab.R.drawable.fab_add);
            } else {
                boolean nozzleBoolean = nozzleString.equals("Baik");
                kondisiNozzle.setChecked(nozzleBoolean);
                kondisiNozzle.setEnabled(true);
                kondisiNozzle.setVisibility(View.VISIBLE);
                tv5.setVisibility(View.GONE);
                fab5.setImageResource(R.drawable.baseline_remove_24);
            }

            if ("N/A".equals(tekananString)) {
                tekananTabung.setEnabled(false);
                tekananTabung.setVisibility(View.GONE);
                tv6.setVisibility(View.VISIBLE);
                fab6.setImageResource(com.github.clans.fab.R.drawable.fab_add);
            } else {
                boolean tekananBoolean = tekananString.equals("Cukup");
                tekananTabung.setChecked(tekananBoolean);
                tekananTabung.setEnabled(true);
                tekananTabung.setVisibility(View.VISIBLE);
                tv6.setVisibility(View.GONE);
                fab6.setImageResource(R.drawable.baseline_remove_24);
            }

            if ("N/A".equals(isiTabungString)) {
                isiTabung.setEnabled(false);
                isiTabung.setVisibility(View.GONE);
                tv7.setVisibility(View.VISIBLE);
                fab7.setImageResource(com.github.clans.fab.R.drawable.fab_add);
            } else {
                boolean isiTabungBoolean = "Baik".equals(isiTabungString);
                isiTabung.setChecked(isiTabungBoolean);
                isiTabung.setEnabled(true);
                isiTabung.setVisibility(View.VISIBLE);
                tv7.setVisibility(View.GONE);
                fab7.setImageResource(R.drawable.baseline_remove_24);
            }

            if ("N/A".equals(kesesuaianString)) {
                kesesuaianBerat.setEnabled(false);
                kesesuaianBerat.setVisibility(View.GONE);
                tv8.setVisibility(View.VISIBLE);
                fab8.setImageResource(com.github.clans.fab.R.drawable.fab_add);
            } else {
                boolean kesesuaianBoolean = kesesuaianString.equals("Cukup");
                kesesuaianBerat.setChecked(kesesuaianBoolean);
                kesesuaianBerat.setEnabled(true);
                kesesuaianBerat.setVisibility(View.VISIBLE);
                tv8.setVisibility(View.GONE);
                fab8.setImageResource(R.drawable.baseline_remove_24);
            }
        }

        databaseReference = FirebaseDatabase.getInstance().getReference("Test").child(key);

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
                if (isNetworkStatusAvialable(getApplicationContext())){
                    saveData();
                } else {
                    saveAsDraft();
                }
            }
        });

        btnCapture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dispatchTakePictureIntent(takePictureLauncher);
            }
        });

        requestPermissionLauncher = registerForActivityResult(
                new ActivityResultContracts.RequestPermission(), isGranted -> {
                    if (isGranted) {
                        Toast.makeText(UpdateActivity.this, "Access Granted", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(UpdateActivity.this, "Camera permission required", Toast.LENGTH_SHORT).show();
                    }
                }
        );
    }

    private void saveAsDraft() {
        SharedPreferences sharedPreferences = getSharedPreferences("data_offline", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        String kodeQR = tvQR.getText().toString();
        String lokasiTabung = etLokasi.getText().toString();
        String MerkAPAR = merkAPAR.getSelectedItem().toString();
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

            if (tv1.getVisibility() == View.VISIBLE){
                kondisiString = "N/A";
            } else {
                kondisiString = skondisiTabung ? "Baik" : "Berkarat";
            }

            if (tv2.getVisibility() == View.VISIBLE){
                posisiString = "N/A";
            } else {
                posisiString = posisi ? "Baik" : "Terhalang";
            }

            if (tv3.getVisibility() == View.VISIBLE){
                selangString = "N/A";
            } else {
                selangString = skondisiSelang ? "Baik" : "Rusak";
            }

            if (tv4.getVisibility() == View.VISIBLE){
                pinString = "N/A";
            } else {
                pinString = kondisiPins ? "Baik" : "Rusak";
            }

            if (tv5.getVisibility() == View.VISIBLE){
                nozzleString = "N/A";
            } else {
                nozzleString = nozzles ? "Baik" : "Tersumbat";
            }

            if (tv6.getVisibility() == View.VISIBLE){
                tekananString = "N/A";
            } else {
                tekananString = Tekanan ? "Cukup" : "Kurang";
            }

            if (tv7.getVisibility() == View.VISIBLE){
                isiString = "N/A";
            } else {
                isiString = isiTabungs ? "Baik" : "Beku";
            }

            if (tv8.getVisibility() == View.VISIBLE){
                kesesuaianString = "N/A";
            } else {
                kesesuaianString = skesesuaianBerat ? "Cukup" : "Kurang";
            }

            saveDataToSharedPreferences(kodeQR, lokasiTabung, MerkAPAR, beratTabung, JenisAPArs, isiString, tekananString, kesesuaianString,
                    kondisiString, selangString, pinString, keterangan, uri, currentDate, username, nozzleString, posisiString, SatuanBerat);

            Toast.makeText(UpdateActivity.this, "Data tersimpan dalam penyimpanan lokal", Toast.LENGTH_SHORT).show();

            boolean isSaved = editor.commit();
            if (isSaved) {

                Log.d("DataSave", "Data saved successfully");
            } else {
                Log.d("DataSave", "Failed to save data");
            }

            Intent intent = new Intent(UpdateActivity.this, MainActivity.class);
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            startActivity(intent);
            finish();
        } else {
            Toast.makeText(UpdateActivity.this, "Gambar harus diperbarui", Toast.LENGTH_SHORT).show();
        }
    }

    private int getIndexSpinner(Spinner spinner, String value) {
        for (int i = 0; i < spinner.getCount(); i++) {
            if (spinner.getItemAtPosition(i).toString().equalsIgnoreCase(value)) {
                return i;
            }
        }
        return -1;
    }

    public void saveData(){
        Log.d("UpdateActivity", "URI: " + uri);

        if (uri == null) {
            Toast.makeText(UpdateActivity.this, "Gambar wajib diperbarui", Toast.LENGTH_SHORT).show();
            return;
        }

        String kodeQRi = tvQR.getText().toString();

        SimpleDateFormat dateFormat = new SimpleDateFormat("dd MM yy", Locale.US);
        Calendar calendar = Calendar.getInstance();
        String currentDates = dateFormat.format(calendar.getTime());

        String childKey = currentDates + kodeQRi;

        String fileName = "image_" + childKey;

        historyStorageReference = FirebaseStorage.getInstance().getReference("History Images").child(fileName);

        AlertDialog.Builder builder = new AlertDialog.Builder(UpdateActivity.this);
        builder.setCancelable(false);
        builder.setView(R.layout.progress_layout);
        AlertDialog dialog = builder.create();
        dialog.show();

        historyStorageReference.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Task<Uri> uriTask = taskSnapshot.getStorage().getDownloadUrl();
                while (!uriTask.isComplete());
                Uri urlImage = uriTask.getResult();
                historyImageUrl = urlImage.toString();
                updateData();
                dialog.dismiss();
            }
        });
    }

    public void updateData(){
        kodeQr = tvQR.getText().toString();

        String lokasi = etLokasi.getText().toString();
        String berat = etBerat.getText().toString();
        String keterangan = etketerangan.getText().toString();

        String MerkAPAR = merkAPAR.getSelectedItem().toString();
        String JenisAPAR = jenisAPAR.getSelectedItem().toString();
        String SatuanBerat = satuanBerat.getSelectedItem().toString();

        Boolean isitabung = isiTabung.isChecked();
        Boolean tekanan = tekananTabung.isChecked();
        Boolean kesesuaian = kesesuaianBerat.isChecked();
        Boolean kondisi = kondisiTabung.isChecked();
        Boolean selang = kondisiSelang.isChecked();
        Boolean pin = kondisiPin.isChecked();

        Boolean nozzle = kondisiNozzle.isChecked();
        Boolean posisi = posisiTabung.isChecked();

        final FirebaseUser users = firebaseAuth.getCurrentUser();
        final String[] finalUser = {""};

        if (users != null) {
            String userId = users.getUid();
            DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("users").child(userId);
            userRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        UserClass userData = snapshot.getValue(UserClass.class);
                        if (userData != null) {
                            finalUser[0] = userData.getUsername();

                            SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yyyy HH.mm.ss", Locale.US);
                            String currentDate = sdf.format(Calendar.getInstance().getTime());

                            if (kodeQr.isEmpty()) {
                                Toast.makeText(UpdateActivity.this, "Kode QR tidak boleh kosong", Toast.LENGTH_SHORT).show();
                                return;
                            }

                            if (uri == null) {
                                Toast.makeText(UpdateActivity.this, "Gambar harus di pilih", Toast.LENGTH_SHORT).show();
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

                            if (tv1.getVisibility() == View.VISIBLE){
                                kondisiString = "N/A";
                            } else {
                                kondisiString = kondisi ? "Baik" : "Berkarat";
                            }

                            if (tv2.getVisibility() == View.VISIBLE){
                                posisiString = "N/A";
                            } else {
                                posisiString = posisi ? "Baik" : "Terhalang";
                            }

                            if (tv3.getVisibility() == View.VISIBLE){
                                selangString = "N/A";
                            } else {
                                selangString = selang ? "Baik" : "Rusak";
                            }

                            if (tv4.getVisibility() == View.VISIBLE){
                                pinString = "N/A";
                            } else {
                                pinString = pin ? "Baik" : "Rusak";
                            }

                            if (tv5.getVisibility() == View.VISIBLE){
                                nozzleString = "N/A";
                            } else {
                                nozzleString = nozzle ? "Baik" : "Tersumbat";
                            }

                            if (tv6.getVisibility() == View.VISIBLE){
                                tekananString = "N/A";
                            } else {
                                tekananString = tekanan ? "Cukup" : "Kurang";
                            }

                            if (tv7.getVisibility() == View.VISIBLE){
                                isiString = "N/A";
                            } else {
                                isiString = isitabung ? "Baik" : "Beku";
                            }

                            if (tv8.getVisibility() == View.VISIBLE){
                                kesesuaianString = "N/A";
                            } else {
                                kesesuaianString = kesesuaian ? "Cukup" : "Kurang";
                            }

                            DataClass dataClass = new DataClass(kodeQr, lokasi, MerkAPAR, berat, JenisAPAR, isiString, tekananString, kesesuaianString,
                                    kondisiString, selangString, pinString, keterangan, historyImageUrl, currentDate, finalUser[0], nozzleString, posisiString, SatuanBerat);

                            DataClass historyData = new DataClass(kodeQr, lokasi, MerkAPAR, berat, JenisAPAR, isiString, tekananString, kesesuaianString,
                                    kondisiString,selangString, pinString, keterangan, historyImageUrl, currentDate, finalUser[0], nozzleString, posisiString, SatuanBerat);

                            databaseReference.setValue(dataClass).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        SimpleDateFormat dateFormat = new SimpleDateFormat("MM dd yy", Locale.US);
                                        Calendar calendar = Calendar.getInstance();
                                        String currentDate = dateFormat.format(calendar.getTime());

                                        String childKey = currentDate + kodeQr;
                                        FirebaseDatabase.getInstance().getReference("History").child(kodeQr).child(childKey).setValue(historyData);
                                        Toast.makeText(UpdateActivity.this, "Updated", Toast.LENGTH_SHORT).show();

                                        Intent intent = new Intent(UpdateActivity.this, MainActivity.class);
                                        startActivity(intent);
                                        finish();
                                    }
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(UpdateActivity.this, e.getMessage().toString(), Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    // Penanganan kesalahan (jika diperlukan)
                }
            });
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

    public static boolean isNetworkStatusAvialable (Context context) {
        ConnectivityManager cm =
                (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        return activeNetwork != null &&
                activeNetwork.isConnectedOrConnecting();

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

    private void fabOnClick(){
        fab1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (kondisiTabung.isEnabled()){
                    kondisiTabung.setEnabled(false);
                    kondisiTabung.setVisibility(View.GONE);
                    tv1.setVisibility(View.VISIBLE);
                    fab1.setImageResource(com.github.clans.fab.R.drawable.fab_add);
                } else {
                    kondisiTabung.setEnabled(true);
                    kondisiTabung.setVisibility(View.VISIBLE);
                    tv1.setVisibility(View.GONE);
                    fab1.setImageResource(R.drawable.baseline_remove_24);
                }
            }
        });

        fab2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (posisiTabung.isEnabled()){
                    posisiTabung.setEnabled(false);
                    posisiTabung.setVisibility(View.GONE);
                    tv2.setVisibility(View.VISIBLE);
                    fab2.setImageResource(com.github.clans.fab.R.drawable.fab_add);
                } else {
                    posisiTabung.setEnabled(true);
                    posisiTabung.setVisibility(View.VISIBLE);
                    tv2.setVisibility(View.GONE);
                    fab2.setImageResource(R.drawable.baseline_remove_24);
                }
            }
        });

        fab3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (kondisiSelang.isEnabled()){
                    kondisiSelang.setEnabled(false);
                    kondisiSelang.setVisibility(View.GONE);
                    tv3.setVisibility(View.VISIBLE);
                    fab3.setImageResource(com.github.clans.fab.R.drawable.fab_add);
                } else {
                    kondisiSelang.setEnabled(true);
                    kondisiSelang.setVisibility(View.VISIBLE);
                    tv3.setVisibility(View.GONE);
                    fab3.setImageResource(R.drawable.baseline_remove_24);
                }
            }
        });

        fab4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (kondisiPin.isEnabled()){
                    kondisiPin.setEnabled(false);
                    kondisiPin.setVisibility(View.GONE);
                    tv4.setVisibility(View.VISIBLE);
                    fab4.setImageResource(com.github.clans.fab.R.drawable.fab_add);
                } else {
                    kondisiPin.setEnabled(true);
                    kondisiPin.setVisibility(View.VISIBLE);
                    tv4.setVisibility(View.GONE);
                    fab4.setImageResource(R.drawable.baseline_remove_24);
                }
            }
        });

        fab5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (kondisiNozzle.isEnabled()){
                    kondisiNozzle.setEnabled(false);
                    kondisiNozzle.setVisibility(View.GONE);
                    tv5.setVisibility(View.VISIBLE);
                    fab5.setImageResource(com.github.clans.fab.R.drawable.fab_add);
                } else {
                    kondisiNozzle.setEnabled(true);
                    kondisiNozzle.setVisibility(View.VISIBLE);
                    tv5.setVisibility(View.GONE);
                    fab5.setImageResource(R.drawable.baseline_remove_24);
                }
            }
        });

        fab6.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (tekananTabung.isEnabled()){
                    tekananTabung.setEnabled(false);
                    tekananTabung.setVisibility(View.GONE);
                    tv6.setVisibility(View.VISIBLE);
                    fab6.setImageResource(com.github.clans.fab.R.drawable.fab_add);
                } else {
                    tekananTabung.setEnabled(true);
                    tekananTabung.setVisibility(View.VISIBLE);
                    tv6.setVisibility(View.GONE);
                    fab6.setImageResource(R.drawable.baseline_remove_24);
                }
            }
        });

        fab7.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isiTabung.isEnabled()){
                    isiTabung.setEnabled(false);
                    isiTabung.setVisibility(View.GONE);
                    tv7.setVisibility(View.VISIBLE);
                    fab7.setImageResource(com.github.clans.fab.R.drawable.fab_add);
                } else {
                    isiTabung.setEnabled(true);
                    isiTabung.setVisibility(View.VISIBLE);
                    tv7.setVisibility(View.GONE);
                    fab7.setImageResource(R.drawable.baseline_remove_24);
                }
            }
        });

        fab8.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (kesesuaianBerat.isEnabled()){
                    kesesuaianBerat.setEnabled(false);
                    kesesuaianBerat.setVisibility(View.GONE);
                    tv8.setVisibility(View.VISIBLE);
                    fab8.setImageResource(com.github.clans.fab.R.drawable.fab_add);
                } else {
                    kesesuaianBerat.setEnabled(true);
                    kesesuaianBerat.setVisibility(View.VISIBLE);
                    tv8.setVisibility(View.GONE);
                    fab8.setImageResource(R.drawable.baseline_remove_24);
                }
            }
        });
    }

    private void initializeComponents(){
        fab1 = findViewById(R.id.fab_1);
        fab2 = findViewById(R.id.fab_2);
        fab3 = findViewById(R.id.fab_3);
        fab4 = findViewById(R.id.fab_4);
        fab5 = findViewById(R.id.fab_5);
        fab6 = findViewById(R.id.fab_6);
        fab7 = findViewById(R.id.fab_7);
        fab8 = findViewById(R.id.fab_8);

        tv1 = findViewById(R.id.tv_na_1);
        tv2 = findViewById(R.id.tv_na_2);
        tv3 = findViewById(R.id.tv_na_3);
        tv4 = findViewById(R.id.tv_na_4);
        tv5 = findViewById(R.id.tv_na_5);
        tv6 = findViewById(R.id.tv_na_6);
        tv7 = findViewById(R.id.tv_na_7);
        tv8 = findViewById(R.id.tv_na_8);

        merkAPAR = findViewById(R.id.update_merk);
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
}