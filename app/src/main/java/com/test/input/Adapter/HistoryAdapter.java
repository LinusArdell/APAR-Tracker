package com.test.input.Adapter;

import android.content.Context;
import android.content.Intent;
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
import com.test.input.Activity.DetailActivity;
import com.test.input.Activity.DetailHistory;
import com.test.input.Class.DataClass;
import com.test.input.Class.HistoryClass;
import com.test.input.Experimental.DateHelper;
import com.test.input.R;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class HistoryAdapter extends RecyclerView.Adapter<MyHistoryHolder>{

    private Context ctx;
    private List<HistoryClass> dataList;

    public HistoryAdapter(@NonNull Context ctx, List<HistoryClass> dataList){
        this.ctx = ctx;
        this.dataList = dataList;
    }

    @NonNull
    @Override
    public MyHistoryHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_history, parent, false);
        return new MyHistoryHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyHistoryHolder holder, int position) {
        Glide.with(ctx).load(dataList.get(position).getDataImage()).into(holder.recImage);
        holder.recQr.setText(dataList.get(position).getKodeQR());
        holder.recUser.setText(dataList.get(position).getUser());
        holder.recLokasi.setText(dataList.get(position).getLokasiTabung());

        SimpleDateFormat sdfInput = new SimpleDateFormat("dd MMM yyyy HH.mm.ss", Locale.US);
        SimpleDateFormat sdfOutput = new SimpleDateFormat("dd MMM yyyy", Locale.US);

        try {
            Date date = sdfInput.parse(dataList.get(position).getDataDate());
            String formattedDate = sdfOutput.format(date);
            holder.recDate.setText(formattedDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }

//        holder.recDate.setText(dataList.get(position).getDataDate());

        holder.recCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent =new Intent(ctx, DetailHistory.class);

                Toast.makeText(ctx, dataList.get(holder.getAdapterPosition()).getKey(), Toast.LENGTH_SHORT).show();
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
                intent.putExtra("Satuan", dataList.get(holder.getAdapterPosition()).getSatuanBerat());

                intent.putExtra("Key", dataList.get(holder.getAdapterPosition()).getKey());

                ctx.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }

    public void searchDataList(ArrayList<HistoryClass> searchList){
        dataList = searchList;
        notifyDataSetChanged();
    }
}

class MyHistoryHolder extends RecyclerView.ViewHolder{

    ImageView recImage;
    TextView recQr, recUser, recDate, recLokasi, tvSpace;
    CardView recCard;

    public MyHistoryHolder(@NonNull View itemView){
        super(itemView);
        recImage = itemView.findViewById(R.id.recImage);
        recCard = itemView.findViewById(R.id.recycler_equipment_history);
        recQr = itemView.findViewById(R.id.recQr);
        recUser = itemView.findViewById(R.id.tv_user);
        recDate = itemView.findViewById(R.id.tv_date);
        recLokasi = itemView.findViewById(R.id.recLokasi);
        tvSpace = itemView.findViewById(R.id.recSpace);
    }
}
