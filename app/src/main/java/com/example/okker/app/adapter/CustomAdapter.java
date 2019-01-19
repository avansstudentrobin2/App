package com.example.okker.app.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.okker.app.R;
import com.example.okker.app.activity.ViewImageActivity;
import com.example.okker.app.model.RetroPost;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class CustomAdapter extends RecyclerView.Adapter<CustomAdapter.CustomViewHolder> {

    private List<RetroPost> dataList;
    private Context context;

    public CustomAdapter(Context context,List<RetroPost> dataList){
        this.context = context;
        this.dataList = dataList;
    }

    /**
     * Set data from itemView in layout
     * Add onClickListener when user click on image. Putextra content ID
     */
    class CustomViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        private TextView txtTitle;
        private ImageView coverImage;
        private TextView imageId;
        private TextView date;
        private TextView place;

        CustomViewHolder(final View itemView) {
            super(itemView);
            mView = itemView;
            txtTitle = mView.findViewById(R.id.title);
            coverImage = mView.findViewById(R.id.coverImage);
            imageId = mView.findViewById(R.id.id);
            date = mView.findViewById(R.id.date);
            place = mView.findViewById(R.id.place);
            coverImage.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v){
                    Intent viewImageIntent = new Intent(context, ViewImageActivity.class);
                    viewImageIntent.putExtra("id", imageId.getText().toString());
                    context.startActivity(viewImageIntent);
                }
            });
        }
    }

    /**
     * Make layout CustomRow layout
     * @param parent
     * @param viewType
     * @return
     */
    @Override
    public CustomViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.custom_row, parent, false);
        return new CustomViewHolder(view);
    }

    /**
     * Set data to CustomRow layout
     * @param holder
     * @param position
     */
    @Override
    public void onBindViewHolder(CustomViewHolder holder, int position) {
        holder.txtTitle.setText(dataList.get(position).getTitle());
        holder.imageId.setText(dataList.get(position).getId().toString());
        holder.date.setText(convertToShortDate(dataList.get(position).getUpdated_at()));
        holder.place.setText(dataList.get(position).getPlace());
        Picasso.get()
                .load(dataList.get(position).getImg())
                .placeholder(R.drawable.placeholder)
                .error(R.drawable.placeholder)
                .into(holder.coverImage);
    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }

    public void clear() {
        dataList.clear();
        notifyDataSetChanged();
    }
    public void addAll(List<RetroPost> list) {
        dataList.addAll(list);
        notifyDataSetChanged();
    }

    private String convertToShortDate(String dateString) {
        try {
            return dateString.substring(0, 10);
        } catch(Exception ex) {
            return "Date Error";
        }
    }

    /**
     * Update list from newList, call function notifyDataSetChanged
     * @param newList
     */
    public void updateList(List<RetroPost> newList) {
        dataList = new ArrayList<>();
        dataList.addAll(newList);
        notifyDataSetChanged();
    }
}