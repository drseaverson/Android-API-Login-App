package edu.uncc.midtermapp;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class PageRecyclerAdapter extends RecyclerView.Adapter<PageRecyclerAdapter.PageViewHolder>{
    int pages;

    public PageRecyclerAdapter(int pages) {
        this.pages = pages;
    }

    @NonNull
    @Override
    public PageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.pages_row_item, parent, false);
        PageRecyclerAdapter.PageViewHolder pageViewHolder = new PageRecyclerAdapter.PageViewHolder(view);
        return pageViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull PageViewHolder holder, @SuppressLint("RecyclerView") int position) {
        holder.buttonPageNum.setText(String.valueOf(position + 1));
        holder.position = position;
        holder.buttonPageNum.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //still can't figure out how to pass info to other adapter
            }
        });
    }

    @Override
    public int getItemCount() {
        return pages;
    }

    public static class PageViewHolder extends RecyclerView.ViewHolder {
        Button buttonPageNum;
        View rootView;
        int position;

        public PageViewHolder(@NonNull View itemView) {
            super(itemView);
            rootView = itemView;
            buttonPageNum = rootView.findViewById(R.id.buttonPageNum);
        }
    }
}


