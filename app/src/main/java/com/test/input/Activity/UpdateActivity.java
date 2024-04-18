package com.test.input.Activity;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
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
import com.test.input.DataClass;
import com.test.input.R;
import com.test.input.UserClass;

import java.io.ByteArrayOutputStream;
import java.text.DateFormat;
import java.util.Calendar;

public class UpdateActivity extends AppCompatActivity {

    ImageView updateImage;
    Button updateButton, btnCancel, btnSave;
    ImageButton btnCapture;
    String kodeQr;
    String imageUrl;
    String key, oldImageURL;
    Uri uri;
    DatabaseReference databaseReference;
    StorageReference storageReference;
    private FirebaseAuth firebaseAuth;
    private ActivityResultLauncher<String> requestPermissionLauncher;

    private SwitchMaterial isiTabung, tekananTabung, kesesuaianBerat, kondisiTabung, kondisiSelang, kondisiPin, kondisiNozzle, posisiTabung;
    private Spinner merkAPAR, jenisAPAR;
    private EditText etLokasi, etBerat, etketerangan;
    private TextView tvQR, tvID;

    private Uri getImageUri(Context context, Bitmap bitmap) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(context.getContentResolver(), bitmap, "Title", null);
        return Uri.parse(path);
    }

    @SuppressLint({"WrongViewCast", "MissingInflatedId"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update);

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

        kondisiNozzle = findViewById(R.id.update_nozzle);
        posisiTabung = findViewById(R.id.update_posisi);

        tvQR = findViewById(R.id.tv_qr_update);
        tvID = findViewById(R.id.tv_title_id);
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveData();
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
                        Bundle extras = result.getData().getExtras();
                        Bitmap imageBitmap = (Bitmap) extras.get("data");
                        // Menampilkan gambar di ImageView
                        updateImage.setImageBitmap(imageBitmap);
                        // Mengonversi Bitmap ke Uri
                        uri = getImageUri(this, imageBitmap);
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


        databaseReference = FirebaseDatabase.getInstance().getReference("Server").child(key);

        updateImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent photoPicker = new Intent(Intent.ACTION_PICK);
                photoPicker.setType("image/*");
                activityResultLauncher.launch(photoPicker);
            }
        });

        updateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveData();
            }
        });

        btnCapture.setOnClickListener(View -> dispatchTakePictureIntent(takePictureLauncher));

        requestPermissionLauncher = registerForActivityResult(
                new ActivityResultContracts.RequestPermission(), isGranted -> {
                    if (isGranted) {
                        Toast.makeText(UpdateActivity.this, "Access Granted", Toast.LENGTH_SHORT).show();
//                        dispatchTakePictureIntent(takePictureLauncher);
                    } else {
                        Toast.makeText(UpdateActivity.this, "Camera permission required", Toast.LENGTH_SHORT).show();
                    }
                }
        );
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
        Log.d("EquipmentTambahActivity", "URI: " + uri);

        // Memeriksa apakah URI gambar tidak null
        if (uri == null) {
            Toast.makeText(UpdateActivity.this, "Gambar wajib diperbarui", Toast.LENGTH_SHORT).show();
            return; // Menghentikan proses jika URI gambar kosong
        }

        storageReference = FirebaseStorage.getInstance().getReference().child("Images Server").child(uri.getLastPathSegment());
        AlertDialog.Builder builder = new AlertDialog.Builder(UpdateActivity.this);
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
                imageUrl = urlImage.toString();
                updateData();
                dialog.dismiss();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                dialog.dismiss();
            }
        });
    }

    public void updateData(){
        kodeQr = tvQR.getText().toString().trim();

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
            // Mengambil UID pengguna saat ini
            String userId = users.getUid();

            // Mengambil referensi ke data pengguna di Realtime Database
            DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("users").child(userId);

            // Mendengarkan satu kali untuk mendapatkan data pengguna
            userRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        // Mengambil data pengguna dari snapshot
                        UserClass userData = snapshot.getValue(UserClass.class);
                        if (userData != null) {
                            // Mengambil nama pengguna dari data pengguna
                            finalUser[0] = userData.getUsername();

                            // Sekarang Anda bisa menggunakan finalUser di sini
                            // Lanjutkan dengan kode uploadData() di sini
                            String currentDate = DateFormat.getDateTimeInstance().format(Calendar.getInstance().getTime());

                            if (kodeQr.isEmpty()) {
                                Toast.makeText(UpdateActivity.this, "Kode QR tidak boleh kosong", Toast.LENGTH_SHORT).show();
                                return; // Menghentikan proses upload jika kodeQR kosong
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

                            if (jenisAPAR.equals("Carbondioxide")) {
                                kesesuaianString = kesesuaian ? "Cukup" : "Kurang";
                            } else {
                                kesesuaianString = "N/A";
                            }

                            DataClass dataClass = new DataClass(kodeQr, lokasi, MerkAPAR, berat, JenisAPAR, isiString, tekananString, kesesuaianString,
                                    kondisiString, selangString, pinString, keterangan, imageUrl, currentDate, finalUser[0], nozzleString, posisiString); //signatureUrl
                            databaseReference.setValue(dataClass).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        StorageReference reference = FirebaseStorage.getInstance().getReferenceFromUrl(oldImageURL);
                                        reference.delete();
                                        Toast.makeText(UpdateActivity.this, "Updated", Toast.LENGTH_SHORT).show();
                                        // Panggil intent untuk memulai MainActivity di sini setelah data berhasil disimpan
                                        Intent intent = new Intent(UpdateActivity.this, MainActivity.class);
                                        startActivity(intent);
                                        finish(); // Tambahkan ini jika Anda ingin menutup UpdateActivity setelah memulai MainActivity
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
            takePictureLauncher.launch(takePictureIntent);
        } else {
            Toast.makeText(this, "No camera app available", Toast.LENGTH_SHORT).show();
        }
    }
}