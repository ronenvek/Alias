package com.ronen.alias;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


import java.util.List;

public class ListAdapter extends RecyclerView.Adapter<ListAdapter.ViewHolder> {

    private final List<String> items;
    private final OnItemClickListener onItemClickListener;

    private final boolean isButton;

    public ListAdapter(List<String> items, boolean isButton, OnItemClickListener onItemClickListener) {
        this.items = items;
        this.onItemClickListener = onItemClickListener;
        this.isButton = isButton;
    }

    public interface OnItemClickListener {
        void onItemClick(String item);
    }


    public static class ViewHolder extends RecyclerView.ViewHolder {
        Button button;
        TextView textView;
        public View view;

        public ViewHolder(View view, boolean isButton, OnItemClickListener onItemClickListener) {
            super(view);
            this.view = view;
            button = view.findViewById(R.id.button);
            textView = view.findViewById(R.id.textView);

            if (isButton) {
                button.setVisibility(View.VISIBLE);
                textView.setVisibility(View.GONE);
                button.setOnClickListener(v -> onItemClickListener.onItemClick(button.getText().toString()));

            } else {
                textView.setVisibility(View.VISIBLE);
                button.setVisibility(View.GONE);
                textView.setOnClickListener(v -> onItemClickListener.onItemClick(textView.getText().toString()));

            }
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewTypeInt) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_item, parent, false);
        return new ViewHolder(view, isButton, onItemClickListener);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        String text = items.get(position);
        String show = text;


        if (text.length() > 8 && text.endsWith("NODELETE"))
            show = text.substring(0, text.length() - 8);
        if (isButton) {
            holder.button.setText(show);
            holder.button.setTextColor(Util.resolveAttrColor(holder.view.getContext()));
        } else {
            holder.textView.setText(show);
            holder.textView.setTextColor(Util.resolveAttrColor(holder.view.getContext()));
        }

    }

    @Override
    public int getItemCount() {
        return items.size();
    }
}
