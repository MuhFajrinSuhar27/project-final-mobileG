package com.example.projekkhayalan.adapters;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.projekkhayalan.R;
import com.example.projekkhayalan.database.DatabaseHelper;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class SosCallAdapter extends RecyclerView.Adapter<SosCallAdapter.ViewHolder> {

    private Context context;
    private Cursor cursor;
    private SimpleDateFormat dateFormat = new SimpleDateFormat("dd MMM yyyy, HH:mm", Locale.getDefault());

    public SosCallAdapter(Context context, Cursor cursor) {
        this.context = context;
        this.cursor = cursor;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.activity_sos_call_adapter, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        if (!cursor.moveToPosition(position)) {
            return;
        }

        long id = cursor.getLong(cursor.getColumnIndexOrThrow("id_panggilan"));
        double latitude = cursor.getDouble(cursor.getColumnIndexOrThrow("latitude"));
        double longitude = cursor.getDouble(cursor.getColumnIndexOrThrow("longitude"));
        String alamat = cursor.getString(cursor.getColumnIndexOrThrow("alamat_lokasi"));
        String waktu = cursor.getString(cursor.getColumnIndexOrThrow("waktu_panggilan"));
        String status = cursor.getString(cursor.getColumnIndexOrThrow("status_panggilan"));

        try {
            // Parse waktu dari database dan format ulang
            Date date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).parse(waktu);
            waktu = dateFormat.format(date);
        } catch (Exception e) {
            // Biarkan format asli jika gagal parse
        }

        holder.textViewWaktu.setText(waktu);
        holder.textViewLokasi.setText(alamat);
        holder.textViewStatus.setText("Status: " + status);

        // Set action untuk tombol navigasi
        holder.buttonNavigate.setOnClickListener(v -> {
            String uri = "geo:" + latitude + "," + longitude + "?q=" + latitude + "," + longitude;
            Intent mapIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
            context.startActivity(mapIntent);
        });

        // Set action untuk tombol tandai selesai
        holder.buttonMarkComplete.setOnClickListener(v -> {
            DatabaseHelper dbHelper = new DatabaseHelper(context);
            dbHelper.updateSosCallStatus(id, "selesai");

            // Refresh data
            notifyDataSetChanged();

            // Atau lebih baik, implementasikan callback ke Activity
            if (context instanceof SosCallActionListener) {
                ((SosCallActionListener) context).onSosCallCompleted(id);
            }
        });
    }

    @Override
    public int getItemCount() {
        return cursor == null ? 0 : cursor.getCount();
    }

    public void swapCursor(Cursor newCursor) {
        if (cursor != null) {
            cursor.close();
        }
        cursor = newCursor;
        notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView textViewWaktu, textViewLokasi, textViewStatus;
        Button buttonNavigate, buttonMarkComplete;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewWaktu = itemView.findViewById(R.id.textViewWaktuPanggilan);
            textViewLokasi = itemView.findViewById(R.id.textViewLokasiPanggilan);
            textViewStatus = itemView.findViewById(R.id.textViewStatusPanggilan);
            buttonNavigate = itemView.findViewById(R.id.buttonNavigate);
            buttonMarkComplete = itemView.findViewById(R.id.buttonMarkComplete);
        }
    }

    public interface SosCallActionListener {
        void onSosCallCompleted(long sosCallId);
    }
}