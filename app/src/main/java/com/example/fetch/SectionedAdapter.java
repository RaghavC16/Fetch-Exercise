package com.example.fetch;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class SectionedAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<SectionRow> rows;

    public SectionedAdapter(List<SectionRow> rows) {
        this.rows = rows;
    }

    @Override
    public int getItemViewType(int position) {
        return rows.get(position).type;
    }

    @Override
    public int getItemCount() {
        return rows.size();
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(
            @NonNull ViewGroup parent, int viewType) {

        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        if (viewType == SectionRow.TYPE_HEADER) {
            View view = inflater.inflate(
                    R.layout.layout_header, parent, false);
            return new HeaderViewHolder(view);
        } else {
            View view = inflater.inflate(
                    R.layout.layout_item, parent, false);
            return new ItemViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(
            @NonNull RecyclerView.ViewHolder holder, int position) {

        SectionRow row = rows.get(position);

        if (holder instanceof HeaderViewHolder) {
            ((HeaderViewHolder) holder).headerTitle.setText(row.headerTitle);
        } else if (holder instanceof ItemViewHolder) {
            ListItem item = row.item;
            ((ItemViewHolder) holder).tvListId.setText("List ID: " + item.getListId());
            ((ItemViewHolder) holder).tvName.setText("Name: " + item.getName());
        }
    }


    static class HeaderViewHolder extends RecyclerView.ViewHolder {
        TextView headerTitle;

        public HeaderViewHolder(@NonNull View itemView) {
            super(itemView);
            headerTitle = itemView.findViewById(R.id.headerTitle);
        }
    }


    static class ItemViewHolder extends RecyclerView.ViewHolder {
        TextView tvListId, tvName;

        public ItemViewHolder(@NonNull View itemView) {
            super(itemView);
            tvListId = itemView.findViewById(R.id.tvListId);
            tvName = itemView.findViewById(R.id.tvName);
        }
    }
}