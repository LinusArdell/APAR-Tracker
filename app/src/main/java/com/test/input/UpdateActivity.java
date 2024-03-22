package com.test.input;

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
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.switchmaterial.SwitchMaterial;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.text.DateFormat;
import java.util.Calendar;

public class UpdateActivity extends AppCompatActivity {

    ImageView updateImage;
    Button updateButton;
    ImageButton btnCapture;
    EditText updateQr;
    String kodeQr, lang;
    String imageUrl;
    String key, oldImageURL;
    Uri uri;
    DatabaseReference databaseReference;
    StorageReference storageReference;

    private FirebaseAuth firebaseAuth;
    SwitchMaterial switchKondisi;
    private ActivityResultLauncher<String> requestPermissionLauncher;

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

        firebaseAuth = FirebaseAuth.getInstance();

        updateButton = findViewById(R.id.btn_update);

        updateImage = findViewById(R.id.update_img);
        switchKondisi = findViewById(R.id.update_kondisi);
        updateQr = findViewById(R.id.update_qr);

        btnCapture = findViewById(R.id.btn_capture);

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

        // Registrasi launcher untuk mengambil gambar dari kamera
        ActivityResultLauncher<Intent> takePictureLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
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
            Glide.with(UpdateActivity.this).load(bundle.getString("Image")).into(updateImage);
            updateQr.setText(bundle.getString("KodeQR"));
            String kondisiString = bundle.getString("Kondisi");
            boolean kondisiBoolean = kondisiString.equals("baik");

            switchKondisi.setChecked(kondisiBoolean);

            key = bundle.getString("Key");
            oldImageURL = bundle.getString("Image");
        }

        databaseReference = FirebaseDatabase.getInstance().getReference("Test").child(key);

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

        // Inisialisasi ActivityResultLauncher untuk izin kamera
        requestPermissionLauncher = registerForActivityResult(
                new ActivityResultContracts.RequestPermission(),
                isGranted -> {
                    if (isGranted) {
                        Toast.makeText(UpdateActivity.this, "Access Granted", Toast.LENGTH_SHORT).show();
//                        dispatchTakePictureIntent(takePictureLauncher);
                    } else {
                        Toast.makeText(UpdateActivity.this, "Camera permission required", Toast.LENGTH_SHORT).show();
                    }
                }
        );
    }

    public void saveData(){
        Log.d("EquipmentTambahActivity", "URI: " + uri);

        // Memeriksa apakah URI gambar tidak null
        if (uri == null) {
            Toast.makeText(UpdateActivity.this, "Gambar wajib diperbarui", Toast.LENGTH_SHORT).show();
            return; // Menghentikan proses jika URI gambar kosong
        }

        storageReference = FirebaseStorage.getInstance().getReference().child("Android Images").child(uri.getLastPathSegment());
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
        kodeQr = updateQr.getText().toString().trim();
        Boolean kondisi = switchKondisi.isChecked();

        final FirebaseUser users = firebaseAuth.getCurrentUser();
        String finalUser = users.getEmail();

        String currentDate = DateFormat.getDateTimeInstance().format(Calendar.getInstance().getTime());

        // Memeriksa apakah kodeQR tidak kosong
        if (kodeQr.isEmpty()) {
            Toast.makeText(UpdateActivity.this, "Kode QR tidak boleh kosong", Toast.LENGTH_SHORT).show();
            return; // Menghentikan proses upload jika kodeQR kosong
        }

        if (uri == null) {
            Toast.makeText(UpdateActivity.this, "Gambar harus di pilih", Toast.LENGTH_SHORT).show();
            return;
        }

        String kondisiString = kondisi ? "baik" : "rusak";

        DataClass dataClass = new DataClass(kodeQr, kondisiString, imageUrl, currentDate, finalUser);
        databaseReference.setValue(dataClass).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()){
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

    // Metode untuk mengirimkan intent untuk mengambil gambar dari kamera
    private void dispatchTakePictureIntent(ActivityResultLauncher<Intent> takePictureLauncher) {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            takePictureLauncher.launch(takePictureIntent);
        } else {
            Toast.makeText(this, "No camera app available", Toast.LENGTH_SHORT).show();
        }
    }
}