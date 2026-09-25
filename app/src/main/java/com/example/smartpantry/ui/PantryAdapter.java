package com.example.smartpantry.ui;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.smartpantry.R;
import com.example.smartpantry.model.PantryItem;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class PantryAdapter extends RecyclerView.Adapter<PantryAdapter.PantryViewHolder> {
    public interface PantryActionListener {
        void onEdit(PantryItem item);
        void onDelete(PantryItem item);
    }

    private final PantryActionListener listener;
    private final List<PantryItem> items = new ArrayList<>();

    public PantryAdapter(PantryActionListener listener) {
        this.listener = listener;
    }

    public void setItems(List<PantryItem> newItems) {
        items.clear();
        items.addAll(newItems);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public PantryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_pantry, parent, false);
        return new PantryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PantryViewHolder holder, int position) {
        PantryItem item = items.get(position);
        holder.name.setText(item.getName());
        holder.quantity.setText(String.format(Locale.getDefault(), "%.2f %s",
                item.getQuantity(), item.getUnit()));
        String expiry = item.getExpiryDate().isEmpty()
                ? "No expiry date"
                : "Expires: " + item.getExpiryDate();
        holder.expiry.setText(expiry);
        holder.edit.setOnClickListener(v -> listener.onEdit(item));
        holder.delete.setOnClickListener(v -> listener.onDelete(item));
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    static class PantryViewHolder extends RecyclerView.ViewHolder {
        TextView name;
        TextView quantity;
        TextView expiry;
        Button edit;
        Button delete;

        PantryViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.textPantryName);
            quantity = itemView.findViewById(R.id.textPantryQuantity);
            expiry = itemView.findViewById(R.id.textPantryExpiry);
            edit = itemView.findViewById(R.id.buttonEditPantry);
            delete = itemView.findViewById(R.id.buttonDeletePantry);
        }
    }
}
