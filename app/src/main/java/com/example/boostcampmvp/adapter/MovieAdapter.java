package com.example.boostcampmvp.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.boostcampmvp.R;
import com.example.boostcampmvp.data.Movie;

import java.util.ArrayList;
import java.util.List;

public class MovieAdapter extends RecyclerView.Adapter<MovieAdapter.MovieHolder> implements MovieAdapterContractor.Model, MovieAdapterContractor.View {

    private Context context;
    private List<Movie> movieList;

    private OnItemClickListener itemClickListener;

    public MovieAdapter(Context context) {
        this.context = context;
        this.movieList = new ArrayList<>();
    }

    @NonNull
    @Override
    public MovieHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MovieHolder(LayoutInflater.from(context).inflate(R.layout.item_movie, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull MovieHolder holder, int position) {
        Movie item = movieList.get(position);

        // click event 정의
        if(itemClickListener != null) {
            holder.itemView.setOnClickListener(v -> {
                itemClickListener.onClick(position);
            });
        }

        // Glide를 이용해서 이미지 가져오기
        if(!item.getImageURL().isEmpty()) {
            Glide.with(context).load(item.getImageURL())
                    .apply(new RequestOptions().placeholder(R.drawable.ic_no_image_placeholder))
                    .into(holder.moviePoster);
        }

        holder.openDate.setText(item.getPubDate());
        holder.movieTitle.setText(Html.fromHtml(item.getTitle()));
        holder.movieDirector.setText(item.getDirector());
        holder.movieDescription.setText(item.getActors());
        holder.movieRating.setRating((float)(item.getUserRating() / 2));
    }

    @Override
    public int getItemCount() {
        return movieList.size();
    }

    @Override
    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.itemClickListener = onItemClickListener;
    }

    @Override
    public void notifyAdapter() {
        notifyDataSetChanged();
    }

    @Override
    public void clearMovies() {
        movieList.clear();
    }

    @Override
    public void addMovies(List<Movie> movies) {
        movieList.addAll(movies);
    }

    @Override
    public Movie getMovieItem(int position) {
        return movieList.get(position);
    }

    @Override
    public int getSize() {
        return movieList.size();
    }

    @Override
    public void notifyAdapterFromTo(int from, int to) {
        notifyItemMoved(from, to);
    }

    public static class MovieHolder extends RecyclerView.ViewHolder {

        public ImageView moviePoster;
        public TextView movieTitle;
        public TextView openDate;
        public TextView movieDirector;
        public TextView movieDescription;
        public RatingBar movieRating;

        public MovieHolder(View itemView) {
            super(itemView);

            moviePoster = itemView.findViewById(R.id.img_item_movie_poster);

            movieTitle = itemView.findViewById(R.id.text_item_movie_title);
            openDate = itemView.findViewById(R.id.text_view_item_movie_open_date);
            movieDirector = itemView.findViewById(R.id.text_view_item_movie_director);
            movieDescription = itemView.findViewById(R.id.text_view_item_movie_actors);

            movieRating = itemView.findViewById(R.id.ratingbar_item_movie_rating);
        }
    }
}
