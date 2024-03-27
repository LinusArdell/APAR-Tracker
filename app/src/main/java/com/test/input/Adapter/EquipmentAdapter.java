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
import com.test.input.DataClass;
import com.test.input.DetailActivity;
import com.test.input.R;

import java.util.ArrayList;
import java.util.List;

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
        Glide.with(context).load(dataList.get(position).getDataImage()).into(holder.recImage);
        holder.recQr.setText(dataList.get(position).getKodeQR());
        holder.recUser.setText(dataList.get(position).getUser());
        holder.recDate.setText(dataList.get(position).getDataDate());

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
}

class MyViewHolder extends RecyclerView.ViewHolder{
    ImageView recImage;
    TextView recQr, recUser, recDate;
    CardView recCard;

    TextView lokasi, merk, berat, jenis, isitabung, tekanan, kesesuaian, kondisiTabung, kondisiSelang, kondisiPin, keterangan;

    public MyViewHolder(@NonNull View itemView) {
        super(itemView);
        recImage = itemView.findViewById(R.id.recImage);
        recCard = itemView.findViewById(R.id.recycler_equipment_card);
        recQr = itemView.findViewById(R.id.recQr);
        recUser = itemView.findViewById(R.id.tv_user);
        recDate = itemView.findViewById(R.id.tv_date);
    }
}
