package com.example.projekkhayalan.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.projekkhayalan.activities.LoginActivity;
import com.example.projekkhayalan.models.DisabilityType;
import com.example.projekkhayalan.R;

import java.util.List;

public class DisabilityTypeAdapter extends RecyclerView.Adapter<DisabilityTypeAdapter.ViewHolder> {

    private List<DisabilityType> disabilityTypes;
    private Context context;
    private int selectedPosition = -1;
    private OnItemClickListener listener;

    public DisabilityTypeAdapter(LoginActivity context, List<DisabilityType> disabilityTypes) {
    }

    public interface OnItemClickListener {
        void onItemClick(int position, DisabilityType disabilityType);
    }

    public DisabilityTypeAdapter(Context context, List<DisabilityType> disabilityTypes) {
        this.context = context;
        this.disabilityTypes = disabilityTypes;
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    public void setSelectedPosition(int position) {
        this.selectedPosition = position;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.card_disability_type, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        DisabilityType disabilityType = disabilityTypes.get(position);

        holder.textViewDisabilityName.setText(disabilityType.getName());
        holder.imageViewDisabilityIcon.setImageResource(disabilityType.getIconResourceId());

        // Highlight selected card
        if (selectedPosition == position) {
            holder.cardDisabilityType.setCardBackgroundColor(ContextCompat.getColor(context, R.color.colorAccent));
            holder.textViewDisabilityName.setTextColor(ContextCompat.getColor(context, android.R.color.white));
        } else {
            holder.cardDisabilityType.setCardBackgroundColor(ContextCompat.getColor(context, android.R.color.white));
            holder.textViewDisabilityName.setTextColor(ContextCompat.getColor(context, android.R.color.black));
        }

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onItemClick(position, disabilityType);
            }
        });
    }

    @Override
    public int getItemCount() {
        return disabilityTypes.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imageViewDisabilityIcon;
        TextView textViewDisabilityName;
        CardView cardDisabilityType;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imageViewDisabilityIcon = itemView.findViewById(R.id.imageViewDisabilityIcon);
            textViewDisabilityName = itemView.findViewById(R.id.textViewDisabilityName);
            cardDisabilityType = itemView.findViewById(R.id.cardDisabilityType);
        }
    }
}