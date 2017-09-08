package com.example.toshiba.themovieapp;


import android.content.Context;
import android.os.Parcelable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

public class MovieDbAdapter extends RecyclerView.Adapter<MovieDbAdapter.ItemViewHolder> {

    private static final String IMAGE_BASE_URL = "http://image.tmdb.org/t/p/w185";
    private String movieData[][];
    private final ItemClickListener clickListener;

    interface ItemClickListener {
        Parcelable onSaveInstanceState();

        void onClick(String[] movieInformation);
    }

    MovieDbAdapter(ItemClickListener clickListener) {
        this.clickListener = clickListener;
    }

    @Override
    public ItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.movie_item_layout, parent, false);
        return new ItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ItemViewHolder holder, int position) {
        Context context = holder.moviePosterImageView.getContext();
        String posterPath = IMAGE_BASE_URL + movieData[position][1];
        Picasso.with(context)
                .load(posterPath)
                .into(holder.moviePosterImageView);
    }

    @Override
    public int getItemCount() {
        if (movieData == null) return 0;
        return movieData.length;
    }

    class ItemViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        final ImageView moviePosterImageView;

        public ItemViewHolder(View itemView) {
            super(itemView);
            moviePosterImageView = itemView.findViewById(R.id.iv_movie_posters);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            int adapterPosition = getAdapterPosition();
            String[] movieInformation = new String[6];

            for (int i = 0; i < movieInformation.length; i++) {
                movieInformation[i] = movieData[adapterPosition][i];
            }

            clickListener.onClick(movieInformation);
        }
    }

    public void setMovieData(String[][] movieData) {
        this.movieData = movieData;
        notifyDataSetChanged();
    }

}
