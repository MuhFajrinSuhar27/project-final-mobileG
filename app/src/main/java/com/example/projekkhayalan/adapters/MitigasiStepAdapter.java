package com.example.projekkhayalan.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.projekkhayalan.R;
import com.example.projekkhayalan.models.MitigasiStep;
import com.google.android.material.button.MaterialButton;

import java.util.List;

public class MitigasiStepAdapter extends RecyclerView.Adapter<MitigasiStepAdapter.MitigasiStepViewHolder> {

    private List<MitigasiStep> steps;
    private int disabilityType;
    private MitigasiStepListener listener;

    public interface MitigasiStepListener {
        void onMitigasiStepClicked(MitigasiStep step);
        void onReadMoreClicked(MitigasiStep step);
        void onListenAudioClicked(MitigasiStep step);
    }

    public MitigasiStepAdapter(List<MitigasiStep> steps, int disabilityType) {
        this.steps = steps;
        this.disabilityType = disabilityType;
    }

    public MitigasiStepAdapter(List<MitigasiStep> steps, int disabilityType, MitigasiStepListener listener) {
        this.steps = steps;
        this.disabilityType = disabilityType;
        this.listener = listener;
    }

    @NonNull
    @Override
    public MitigasiStepViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_mitigasi_step, parent, false);
        return new MitigasiStepViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MitigasiStepViewHolder holder, int position) {
        MitigasiStep step = steps.get(position);

        holder.textViewStepTitle.setText(step.getTitle());
        holder.textViewStepDescription.setText(step.getDescription());
        holder.imageViewStep.setImageResource(step.getImageResId());

        // Set step number if available
        if (step.getStepNumber() > 0) {
            holder.textViewStepNumber.setText("LANGKAH " + step.getStepNumber());
        } else {
            holder.textViewStepNumber.setText(step.getTitle().toUpperCase());
        }

        // Set click listeners
        holder.cardViewStep.setOnClickListener(v -> {
            if (listener != null) {
                listener.onMitigasiStepClicked(step);
            }
        });

        if (holder.buttonReadMore != null) {
            holder.buttonReadMore.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onReadMoreClicked(step);
                }
            });
        }

        if (holder.buttonListenAudio != null) {
            holder.buttonListenAudio.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onListenAudioClicked(step);
                }
            });
        }

        // Apply accessibility adjustments
        applyAccessibilityAdjustments(holder, position);
    }

    private void applyAccessibilityAdjustments(MitigasiStepViewHolder holder, int position) {
        Context context = holder.itemView.getContext();

        if (disabilityType == 1) { // Tunanetra (visually impaired)
            // Higher contrast for visually impaired
            holder.textViewStepTitle.setTextColor(0xFFFFFFFF);
            holder.textViewStepDescription.setTextColor(0xFF000000);
            // Bigger text
            holder.textViewStepTitle.setTextSize(24);
            holder.textViewStepDescription.setTextSize(20);

            // Show only audio button for visually impaired
            if (holder.buttonListenAudio != null) {
                holder.buttonListenAudio.setVisibility(View.VISIBLE);
            }
            if (holder.buttonReadMore != null) {
                holder.buttonReadMore.setVisibility(View.GONE);
            }

        } else if (disabilityType == 2) { // Tunarungu (hearing impaired)
            // Visual cues are important for hearing impaired
            holder.imageViewStep.getLayoutParams().height = 240;
            holder.textViewStepTitle.setTextSize(24);

            // Hide audio button for hearing impaired
            if (holder.buttonListenAudio != null) {
                holder.buttonListenAudio.setVisibility(View.GONE);
            }
            if (holder.buttonReadMore != null) {
                holder.buttonReadMore.setVisibility(View.VISIBLE);
            }

        } else if (disabilityType == 3) { // Tunadaksa (physically impaired)
            // Larger elements for easier touch
            if (holder.buttonListenAudio != null) {
                holder.buttonListenAudio.setTextSize(18);
            }
            if (holder.buttonReadMore != null) {
                holder.buttonReadMore.setTextSize(18);
                holder.buttonReadMore.setPadding(24, 24, 24, 24);
            }

        } else if (disabilityType == 4) { // Tunagrahita (intellectually disabled)
            // Simpler presentation with larger text
            holder.textViewStepTitle.setTextSize(26);
            holder.textViewStepDescription.setTextSize(22);

            // Simplify UI by removing read more button
            if (holder.buttonReadMore != null) {
                holder.buttonReadMore.setVisibility(View.GONE);
            }
        }
    }

    @Override
    public int getItemCount() {
        return steps.size();
    }

    static class MitigasiStepViewHolder extends RecyclerView.ViewHolder {
        CardView cardViewStep;
        TextView textViewStepTitle;
        TextView textViewStepDescription;
        TextView textViewStepNumber;
        ImageView imageViewStep;
        ImageView imageViewIcon;
        MaterialButton buttonReadMore;
        MaterialButton buttonListenAudio;
        View layoutBadgeContainer;

        public MitigasiStepViewHolder(@NonNull View itemView) {
            super(itemView);
            cardViewStep = itemView.findViewById(R.id.cardViewStep);
            textViewStepTitle = itemView.findViewById(R.id.textViewStepTitle);
            textViewStepDescription = itemView.findViewById(R.id.textViewStepDescription);
            textViewStepNumber = itemView.findViewById(R.id.textViewStepNumber);
            imageViewStep = itemView.findViewById(R.id.imageViewStep);
            imageViewIcon = itemView.findViewById(R.id.imageViewIcon);
            buttonReadMore = itemView.findViewById(R.id.buttonReadMore);
            buttonListenAudio = itemView.findViewById(R.id.buttonListenAudio);
            layoutBadgeContainer = itemView.findViewById(R.id.layoutBadgeContainer);
        }
    }
}