package com.rtech.gpgram.adapters;

import android.annotation.SuppressLint;
import android.content.Context;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.rtech.gpgram.R;
import com.rtech.gpgram.models.Preview_Post_model;
import com.rtech.gpgram.interfaces.Post_fragmentSetCallback;

import java.util.ArrayList;

public class GridPostAdapter extends RecyclerView.Adapter<GridPostAdapter.ViewHolder> {
    Context context;
    ArrayList<Preview_Post_model> dataList;
    Post_fragmentSetCallback post_fragmentSetCallback;
    public GridPostAdapter(Context c, ArrayList<Preview_Post_model> data, Post_fragmentSetCallback callback){
        this.dataList=data;
        this.context=c;
        setHasStableIds(true);
        this.post_fragmentSetCallback=callback;

    }

    @Override
    public long getItemId(int position) {
        return dataList.get(position).postid;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.sample_post,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        Glide.with(context).load(dataList.get(position).image_url).placeholder(R.drawable.post_placeholder).into(holder.imageView);
        holder.imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                post_fragmentSetCallback.openPostFragment(dataList.get(position).image_url,dataList.get(position).postid);


            }
        });

    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView=itemView.findViewById(R.id.image_view);

        }
    }
}
