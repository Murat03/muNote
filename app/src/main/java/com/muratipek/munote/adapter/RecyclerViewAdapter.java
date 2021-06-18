package com.muratipek.munote.adapter;

import android.content.Intent;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.muratipek.munote.NoteActivity;
import com.muratipek.munote.R;

import java.util.ArrayList;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.RowHolder> {

    private ArrayList<String> noteList;
    private String[] colors = {"#E2BBFF","#BCAAE8","#C8C7FF","#AAB9E8","#BBDEFF"};

    public RecyclerViewAdapter(ArrayList<String> noteList) {
        this.noteList = noteList;
    }

    @NonNull
    @Override
    public RowHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.recycler_row, parent, false);
        return new RowHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RowHolder holder, int position) {
        holder.bind(noteList.get(position), colors, position);

        holder.itemView.setOnClickListener(v -> {

            Intent intent = new Intent(v.getContext(), NoteActivity.class);
            intent.putExtra("title", noteList.get(position));
            v.getContext().startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return noteList.size();
    }
    public class RowHolder extends RecyclerView.ViewHolder {
        TextView textTitle;
        public RowHolder(@NonNull View itemView) {
            super(itemView);


        }
        public void bind(String title, String[] colors, Integer position){
            itemView.setBackgroundColor(Color.parseColor(colors[position%5]));
            textTitle = itemView.findViewById(R.id.textTitle);
            textTitle.setText(title);
        }
    }
}
