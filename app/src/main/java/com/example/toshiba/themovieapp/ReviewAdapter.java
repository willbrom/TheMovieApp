package com.example.toshiba.themovieapp;


import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class ReviewAdapter extends RecyclerView.Adapter<ReviewAdapter.ReviewViewHolder>{

    private String reviewData[];

    @Override
    public ReviewViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.review_item_layout, parent, false);
        return new ReviewViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ReviewViewHolder holder, int position) {
        holder.reviewTextView.setText(reviewData[position]);
    }

    @Override
    public int getItemCount() {
        if (reviewData == null) return 0;
        return reviewData.length;
    }

    class ReviewViewHolder extends RecyclerView.ViewHolder {

        TextView reviewTextView;

        public ReviewViewHolder(View itemView) {
            super(itemView);
            reviewTextView = itemView.findViewById(R.id.review_text);
        }
    }

    public void setReviewData(String[] reviewData) {
        this.reviewData = reviewData;
        notifyDataSetChanged();
    }

}
