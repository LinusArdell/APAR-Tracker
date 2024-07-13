package com.test.input.Adapter;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.test.input.Class.DraftClass;
import com.test.input.R;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class DraftAdapter extends RecyclerView.Adapter<DraftViewHolder>{

    private Context contexts;
    private List<DraftClass> dataLists;
    String imageURL, historyImageURl;
    private ProgressDialog progressDialog;


    public DraftAdapter (@NonNull Context contexts, List<DraftClass> dataLists) {
        this.contexts = contexts;
        this.dataLists = dataLists;
    }

    @NonNull
    @Override
    public DraftViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_draft, parent, false);
        return new DraftViewHolder(view);
    }

    @SuppressLint("RecyclerView")
    @Override
    public void onBindViewHolder(@NonNull DraftViewHolder holder, int position) {
//        Glide.with(context).load(dataList.get(position).getDataImage()).into(holder.recImage);

        DraftClass dataList = dataLists.get(position);
        String KodeQr = dataLists.get(position).getKodeQR();

        Glide.with(contexts)
                .load(dataList.getDataImage())
                .into(holder.recImage12);

        holder.recQr12.setText(dataLists.get(position).getKodeQR());
        holder.recUser12.setText(dataLists.get(position).getUser());
        holder.recLokasi12.setText(dataLists.get(position).getLokasiTabung());

        holder.tvMerk12.setText(dataLists.get(position).getMerkAPAR());
        holder.tvBerat12.setText(dataLists.get(position).getBeratTabung());
        holder.satuan12.setText(dataLists.get(position).getSatuanBerat());
        holder.tvJenis12.setText(dataLists.get(position).getJenisAPAR());
        holder.tvTanggal12.setText(dataLists.get(position).getDataDate());
        holder.tvKondisiTabung12.setText(dataLists.get(position).getKondisiTabung());
        holder.tvPosisi12.setText(dataLists.get(position).getPosisiTabung());
        holder.tvKondisiSelang12.setText(dataLists.get(position).getKondisiSelang());
        holder.tvPin12.setText(dataLists.get(position).getKondisiPin());
        holder.tvNozzle12.setText(dataLists.get(position).getKondisiNozzle());
        holder.tvJarum12.setText(dataLists.get(position).getTekananTabung());
        holder.tvPowder12.setText(dataLists.get(position).getIsiTabung());
        holder.tvSelisih12.setText(dataLists.get(position).getKesesuaianBerat());
        holder.tvKeterangan12.setText(dataLists.get(position).getKeterangan());

        holder.btnUploadDraft.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(contexts);
                builder.setCancelable(false);
                builder.setView(R.layout.progress_layout);
                AlertDialog dialog = builder.create();
                dialog.show();
                DraftClass draft = dataLists.get(holder.getAdapterPosition());

                String kodeQR = draft.getKodeQR();
                String lokasi = draft.getLokasiTabung();
                String berat = draft.getBeratTabung();
                String keterangan = draft.getKeterangan();

                String MerkAPAR = draft.getMerkAPAR();
                String jenisAPAR = draft.getJenisAPAR();
                String satuanBerat = draft.getSatuanBerat();

                String isitabung = draft.getIsiTabung();
                String tekanan = draft.getTekananTabung();
                String kesesuaian = draft.getKesesuaianBerat();
                String kondisi = draft.getKondisiTabung();
                String selang = draft.getKondisiSelang();
                String pin = draft.getKondisiPin();
                String nozzle = draft.getKondisiNozzle();
                String posisi = draft.getPosisiTabung();

                String user = draft.getUser();
                String tanggal = draft.getDataDate();

                Uri uri = Uri.parse(draft.getDataImage());

                ContentResolver contentResolver = view.getContext().getContentResolver();

                    SimpleDateFormat dateFormat = new SimpleDateFormat("MM dd yy", Locale.US);
                    Calendar calendar = Calendar.getInstance();
                    String currentDate = dateFormat.format(calendar.getTime());
                    String childKey = currentDate + kodeQR;
                    String fileName = "image_" + childKey;
                    StorageReference historyStorageReference = FirebaseStorage.getInstance().getReference("History Images").child(fileName);
//                    StorageReference storageReference = FirebaseStorage.getInstance().getReference().child("Image Test")
//                    StorageReference storageReference = FirebaseStorage.getInstance().getReference().child("Android Images").child(uri.getLastPathSegment());

                    historyStorageReference.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            Task<Uri> uriTask = taskSnapshot.getStorage().getDownloadUrl();
                            while (!uriTask.isComplete());
                            Uri urlImage = uriTask.getResult();
                            historyImageURl = urlImage.toString();

                            SimpleDateFormat months = new SimpleDateFormat("MMM", Locale.US);
                            String currentMonth = months.format(Calendar.getInstance().getTime());

                            SimpleDateFormat sdfs = new SimpleDateFormat("dd MMM yyyy", Locale.US);
                            String currentDates = sdfs.format(Calendar.getInstance().getTime());

                            DraftClass historyDataClass = new DraftClass(kodeQR, lokasi, MerkAPAR, berat, jenisAPAR, isitabung, tekanan, kesesuaian,
                                    kondisi, selang, pin, keterangan, historyImageURl, tanggal, user, nozzle, posisi, satuanBerat, currentDates, currentMonth);

                            SimpleDateFormat dateFormat = new SimpleDateFormat("MM dd yy", Locale.US);
                            Calendar calendar = Calendar.getInstance();
                            String currentDate = dateFormat.format(calendar.getTime());

                            String childKey = currentDate + KodeQr;
                            FirebaseDatabase.getInstance().getReference("History").child(kodeQR).child(childKey).setValue(historyDataClass);

                            FirebaseDatabase.getInstance().getReference("Database").child(kodeQR).setValue(historyDataClass).addOnCompleteListener(new OnCompleteListener<Void>() {
                                //                            FirebaseDatabase.getInstance().getReference("Draft").child(kodeQR).setValue(dataClass).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    dialog.dismiss();
                                    if (task.isSuccessful()) {
                                        Toast.makeText(view.getContext(), "Data berhasil diupload", Toast.LENGTH_SHORT).show();
                                        deleteItem(position);
                                    } else {
                                        Toast.makeText(view.getContext(), "Data berhasil diupload", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });

                        }
                    });
            }
        });

        holder.btnDeleteDraft.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                removeItem(position);
                deleteItem(position);
            }
        });
    }

    private void logSharedPreferencesData() {
        SharedPreferences sharedPreferences = contexts.getSharedPreferences("data_offline", Context.MODE_PRIVATE);
        Map<String, ?> allEntries = sharedPreferences.getAll();
        for (Map.Entry<String, ?> entry : allEntries.entrySet()) {
            Log.d("SharedPreferencesData", entry.getKey() + ": " + entry.getValue().toString());
        }
    }

    @Override
    public int getItemCount() {
        return dataLists.size();
    }

    private void deleteItem(int position) {
        DraftClass dataToDelete = dataLists.get(position);
        String uniqueKey = dataToDelete.getKodeQR();

        String keys = "imageUri_" + uniqueKey + "_";

        String reJenis = keys +"Jenis";
        String reLokasi = keys + "Lokasi";
        String reKesesuaian = keys + "Kesesuaian";
        String reTekanan = keys + "Tekanan";
        String reNozzle = keys + "Nozzle";
        String reKondisiSelang = keys + "KondisiSelang";
        String reKeterangan = keys + "Keterangan";
        String reKodeQR = keys + "KodeQR";
        String reIsiTabung = keys + "IsiTabung";
        String reBerat = keys + "Berat";
        String reKondisiPin = keys + "KondisiPin";
        String reMerk = keys + "Merk";
        String rePosisi = keys + "Posisi";
        String reTanggal = keys + "Tanggal";
        String reKondisi = keys + "KondisiTabung";
        String reUser = keys + "User";
        String reSatuanBerat = keys + "Satuan";

        // Cetak kunci unik sebelum penghapusan
        Log.d("UniqueKeyBeforeDelete", uniqueKey);
        Log.d("keyBeforeDelete", keys);

        // Hapus data dari shared preferences
        SharedPreferences sharedPreferences = contexts.getSharedPreferences("data_offline", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.remove(keys).commit();
        editor.remove(reJenis).commit();
        editor.remove(reLokasi).commit();
        editor.remove(reKesesuaian).commit();
        editor.remove(reTekanan).commit();
        editor.remove(reNozzle).commit();
        editor.remove(reKondisiSelang).commit();
        editor.remove(reKeterangan).commit();
        editor.remove(reKodeQR).commit();
        editor.remove(reIsiTabung).commit();
        editor.remove(reBerat).commit();
        editor.remove(reKondisiPin).commit();
        editor.remove(reMerk).commit();
        editor.remove(rePosisi).commit();
        editor.remove(reTanggal).commit();
        editor.remove(reKondisi).commit();
        editor.remove(reUser).commit();
        editor.remove(reSatuanBerat).commit();

        // Hapus data dari list RecyclerView
        dataLists.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, dataLists.size());

        // Cetak kunci unik setelah penghapusan
        Log.d("UniqueKeyAfterDelete", uniqueKey);
        Log.d("keyAfterDelete", keys);
        logSharedPreferencesData();
    }
}

class DraftViewHolder extends RecyclerView.ViewHolder {

    ImageView recImage12;
    TextView recQr12, recUser12, recLokasi12, tvMerk12, tvBerat12, tvTanggal12, tvKondisiTabung12, tvPosisi12, tvKondisiSelang12, tvPin12,
            tvNozzle12, tvJarum12, tvPowder12, tvSelisih12, tvKeterangan12;
    CardView recCard12;
    TextView tvJenis12, satuan12;
    Button btnUploadDraft, btnDeleteDraft;

    public DraftViewHolder(@NonNull View itemView){
        super(itemView);
        recCard12 = itemView.findViewById(R.id.recycler_equipment_draft12);
        recImage12 = itemView.findViewById(R.id.Image12);
        recQr12 = itemView.findViewById(R.id.KodeQR12);
        recUser12 = itemView.findViewById(R.id.User12);
        recLokasi12 = itemView.findViewById(R.id.Lokasi12);
        tvMerk12 = itemView.findViewById(R.id.Merk12);
        tvBerat12 = itemView.findViewById(R.id.Berat12);
        tvJenis12 = itemView.findViewById(R.id.Jenis12);
        tvTanggal12 = itemView.findViewById(R.id.Tanggal12);
        tvKondisiTabung12 = itemView.findViewById(R.id.KondisiTabung12);
        tvPosisi12 = itemView.findViewById(R.id.Posisi12);
        tvKondisiSelang12 = itemView.findViewById(R.id.KondisiSelang12);
        tvPin12 = itemView.findViewById(R.id.KondisiPin12);
        tvNozzle12 = itemView.findViewById(R.id.Nozzle12);
        tvJarum12 = itemView.findViewById(R.id.Tekanan12);
        tvPowder12 = itemView.findViewById(R.id.IsiTabung12);
        tvSelisih12 = itemView.findViewById(R.id.Kesesuaian12);
        tvKeterangan12 = itemView.findViewById(R.id.Keterangan12);
        btnUploadDraft = itemView.findViewById(R.id.btn_upload_draft);
        btnDeleteDraft = itemView.findViewById(R.id.btn_delete_draft);
        satuan12 = itemView.findViewById(R.id.satuan);
    }
}