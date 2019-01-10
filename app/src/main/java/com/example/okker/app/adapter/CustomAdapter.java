package com.example.okker.app.adapter;


import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.okker.app.R;
import com.example.okker.app.activity.MainActivity;
import com.example.okker.app.activity.ViewImageActivity;
import com.example.okker.app.model.RetroPhoto;
import com.jakewharton.picasso.OkHttp3Downloader;
import com.squareup.picasso.Picasso;

import java.util.List;

public class CustomAdapter extends RecyclerView.Adapter<CustomAdapter.CustomViewHolder> {

    private List<RetroPhoto> dataList;
    private Context context;

    public CustomAdapter(Context context,List<RetroPhoto> dataList){
        this.context = context;
        this.dataList = dataList;
    }

    class CustomViewHolder extends RecyclerView.ViewHolder {

        public final View mView;

        private TextView txtTitle;
        private ImageView coverImage;
        private TextView imageId;

        CustomViewHolder(final View itemView) {
            super(itemView);
            mView = itemView;
            //mView.findViewById(imageId);

            txtTitle = mView.findViewById(R.id.title);
            coverImage = mView.findViewById(R.id.coverImage);
            imageId = mView.findViewById(R.id.id);

            coverImage.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v){
                    Intent viewImageIntent = new Intent(context, ViewImageActivity.class);
                    viewImageIntent.putExtra("id", imageId.getText().toString());
                    context.startActivity(viewImageIntent);
                }
            });
        }
    }

    @Override
    public CustomViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.custom_row, parent, false);
        return new CustomViewHolder(view);
    }

    @Override
    public void onBindViewHolder(CustomViewHolder holder, int position) {
        holder.txtTitle.setText(dataList.get(position).getTitle());
        holder.imageId.setText(dataList.get(position).getId().toString());

        Picasso.Builder builder = new Picasso.Builder(context);
        builder.downloader(new OkHttp3Downloader(context));
        builder.build().load(dataList.get(position).getImg())
                .placeholder((R.drawable.ic_launcher_background))
                .error(R.drawable.ic_launcher_background)
                .into(holder.coverImage);

        holder.getItemId();
    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }
}

