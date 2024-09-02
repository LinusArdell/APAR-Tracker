package com.test.input.Adapter;

import android.app.DownloadManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.test.input.Class.VersionClass;
import com.test.input.R;

import java.util.List;

public class VersionAdapter extends RecyclerView.Adapter<VersionAdapter.ViewHolder>{

    private Context context;
    private List<VersionClass> fileList;

    public VersionAdapter(@NonNull Context context, List<VersionClass> fileList) {
        this.context = context;
        this.fileList = fileList;
    }

    @NonNull
    @Override
    public VersionAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.update_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull VersionAdapter.ViewHolder holder, int position) {
        VersionClass file = fileList.get(position);

        holder.tvNewver.setText(file.getVersi());
        holder.tvTanggal.setText(file.getTanggal());
        holder.tvRequirement.setText(file.getRequirement());
        holder.tvDeskripsi.setText(file.getKeterangan());
        holder.tvLink.setText(file.getLink());

        holder.btnDownload.setOnClickListener(v -> {
            String downloadLink = file.getLink();
            startDownload(context, downloadLink);
        });
    }

    private void startDownload(Context context, String downloadLink) {
        Uri uri = Uri.parse(downloadLink);

        // Membuat request untuk DownloadManager
        DownloadManager.Request request = new DownloadManager.Request(uri);
        request.setTitle("File New Version");
        request.setDescription("Mengunduh versi terbaru...");
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
        request.setDestinationInExternalFilesDir(context, Environment.DIRECTORY_DOWNLOADS, "version.apk");

        // Mendapatkan layanan DownloadManager
        DownloadManager downloadManager = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);

        // Memulai pengunduhan
        if (downloadManager != null) {
            downloadManager.enqueue(request);
            Toast.makeText(context, "Download started", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(context, "DownloadManager not available", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public int getItemCount() {
        return fileList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView tvNewver, tvTanggal, tvRequirement, tvDeskripsi, tvLink;
        Button btnDownload;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            tvNewver = itemView.findViewById(R.id.tv_newver);
            tvTanggal = itemView.findViewById(R.id.tv_tanggal);
            tvRequirement = itemView.findViewById(R.id.et_requirement);
            tvDeskripsi = itemView.findViewById(R.id.et_text_email);
            tvLink = itemView.findViewById(R.id.tv_link);
            btnDownload = itemView.findViewById(R.id.btnSend);
        }
    }
}
