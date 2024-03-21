package com.test.input.Adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.test.input.Class.DataClass;
import com.test.input.Class.FacilityClass;
import com.test.input.EquipmentActivity;
import com.test.input.EquipmentFacility;
import com.test.input.R;

import java.util.ArrayList;
import java.util.List;

public class FacilityAdapter extends RecyclerView.Adapter<FaciltyViewHolder>{
    private Context context;
    private List<FacilityClass> facilityList;

    public FacilityAdapter(@NonNull Context context, List<FacilityClass> facilityList) {
        this.context =context;
        this.facilityList = facilityList;
    }

    @NonNull
    @Override
    public FaciltyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_facility, parent, false);
        return new FaciltyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FaciltyViewHolder holder, int position) {
//        Glide.with(context).load(dataList.get(position).getDataImage()).into(holder.recImage);
        holder.recNama.setText(facilityList.get(position).getArea());
        holder.recAlamat.setText(facilityList.get(position).getAlamat());

        holder.recCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, EquipmentActivity.class);
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return facilityList.size();
    }

    public void searchDataList(ArrayList<FacilityClass> searchList){
        facilityList = searchList;
        notifyDataSetChanged();
    }
}

class FaciltyViewHolder extends RecyclerView.ViewHolder{
    TextView recNama, recAlamat;
    CardView recCard;
    public FaciltyViewHolder(@NonNull View itemView) {
        super(itemView);
        recNama = itemView.findViewById(R.id.recNama);
        recCard = itemView.findViewById(R.id.recycler_facility_card);
        recAlamat = itemView.findViewById(R.id.rec_alamat);
    }
}
