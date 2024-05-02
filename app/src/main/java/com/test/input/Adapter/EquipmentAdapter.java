package com.test.input.Adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.test.input.Class.DataClass;
import com.test.input.Activity.DetailActivity;
import com.test.input.Experimental.DateHelper;
import com.test.input.R;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class EquipmentAdapter extends RecyclerView.Adapter<MyViewHolder> {

    private Context context;
    private List<DataClass> dataList;

    public EquipmentAdapter(@NonNull Context context, List<DataClass> dataList) {
        this.context = context;
        this.dataList = dataList;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_equipment, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        String dateString = dataList.get(position).getDataDate();
        int backgroundColor = getBackgroundColor(dateString);
        holder.itemView.setBackgroundColor(backgroundColor);

        int textColor = getTextColor(backgroundColor);
        holder.recQr.setTextColor(textColor);
        holder.recUser.setTextColor(textColor);
        holder.recDate.setTextColor(textColor);
        holder.recLokasi.setTextColor(textColor);
        holder.tvSpace.setTextColor(textColor);

        Glide.with(context).load(dataList.get(position).getDataImage()).into(holder.recImage);
        holder.recQr.setText(dataList.get(position).getKodeQR());
        holder.recUser.setText(dataList.get(position).getUser());
        holder.recLokasi.setText(dataList.get(position).getLokasiTabung());
        String formattedDate = DateHelper.convertToRelativeDate(dataList.get(position).getDataDate());
        holder.recDate.setText(formattedDate);

        holder.recCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent =new Intent(context, DetailActivity.class);

                Toast.makeText(context, dataList.get(holder.getAdapterPosition()).getKey(), Toast.LENGTH_SHORT).show();
                intent.putExtra("Image", dataList.get(holder.getAdapterPosition()).getDataImage());
                intent.putExtra("KodeQR", dataList.get(holder.getAdapterPosition()).getKodeQR());
                intent.putExtra("Tanggal", dataList.get(holder.getAdapterPosition()).getDataDate());
                intent.putExtra("User", dataList.get(holder.getAdapterPosition()).getUser());
                intent.putExtra("Lokasi", dataList.get(holder.getAdapterPosition()).getLokasiTabung());
                intent.putExtra("Merk", dataList.get(holder.getAdapterPosition()).getMerkAPAR());
                intent.putExtra("Berat", dataList.get(holder.getAdapterPosition()).getBeratTabung());
                intent.putExtra("Jenis", dataList.get(holder.getAdapterPosition()).getJenisAPAR());
                intent.putExtra("IsiTabung", dataList.get(holder.getAdapterPosition()).getIsiTabung());
                intent.putExtra("Tekanan", dataList.get(holder.getAdapterPosition()).getTekananTabung());
                intent.putExtra("Kesesuaian", dataList.get(holder.getAdapterPosition()).getKesesuaianBerat());
                intent.putExtra("KondisiTabung", dataList.get(holder.getAdapterPosition()).getKondisiTabung());
                intent.putExtra("KondisiSelang", dataList.get(holder.getAdapterPosition()).getKondisiSelang());
                intent.putExtra("KondisiPin", dataList.get(holder.getAdapterPosition()).getKondisiPin());
                intent.putExtra("Keterangan", dataList.get(holder.getAdapterPosition()).getKeterangan());

                intent.putExtra("Nozzle", dataList.get(holder.getAdapterPosition()).getKondisiNozzle());
                intent.putExtra("Posisi", dataList.get(holder.getAdapterPosition()).getPosisiTabung());

                intent.putExtra("Key", dataList.get(holder.getAdapterPosition()).getKey());
                intent.putExtra("Satuan", dataList.get(holder.getAdapterPosition()).getSatuanBerat());

                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }

    public void searchDataList(ArrayList<DataClass> searchList){
        dataList = searchList;
        notifyDataSetChanged();
    }

    private int getBackgroundColor(String dateString) {
        SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yyyy HH.mm.ss", Locale.US);
        try {
            Date date = sdf.parse(dateString);
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(date);

            Calendar today = Calendar.getInstance();
            long diffInMillis = today.getTimeInMillis() - calendar.getTimeInMillis();
            long diffInDays = diffInMillis / (1000 * 60 * 60 * 24);

            if (diffInDays <= 14) {
                return Color.parseColor("#68ed5c"); // Warna hijau (#5dcb84) jika tanggal tidak lebih dari 2 minggu yang lalu
            } else if (diffInDays <= 28) {
                return Color.WHITE; // Warna default (putih) jika tanggal antara 2 dan 4 minggu yang lalu
            } else {
                return Color.parseColor("#f44336"); // Warna merah (#f55d46) jika tanggal lebih dari 4 minggu yang lalu
            }
        } catch (ParseException e) {
            e.printStackTrace();
            return Color.parseColor("#68ed5c"); // Jika parsing gagal, kembalikan warna default (hijau)
        }
    }

    private int getTextColor(int backgroundColor) {
        if (backgroundColor == Color.parseColor("#68ed5c") || backgroundColor == Color.WHITE) {
            return Color.BLACK; // Jika latar belakang hijau atau putih, warna teksnya hitam
        } else {
            return Color.WHITE; // Jika latar belakang merah, warna teksnya putih
        }
    }

}

class MyViewHolder extends RecyclerView.ViewHolder{
    ImageView recImage;
    TextView recQr, recUser, recDate, recLokasi, tvSpace;
    CardView recCard;

    public MyViewHolder(@NonNull View itemView) {
        super(itemView);
        recImage = itemView.findViewById(R.id.recImage);
        recCard = itemView.findViewById(R.id.recycler_equipment_card);
        recQr = itemView.findViewById(R.id.recQr);
        recUser = itemView.findViewById(R.id.tv_user);
        recDate = itemView.findViewById(R.id.tv_date);
        recLokasi = itemView.findViewById(R.id.recLokasi);
        tvSpace = itemView.findViewById(R.id.recSpace);
    }
}
