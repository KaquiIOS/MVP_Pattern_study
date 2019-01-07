package com.example.boostcampmvp.adapter;


import com.example.boostcampmvp.data.Movie;

import java.util.List;

public interface MovieAdapterContractor {

    interface View {
        void setOnItemClickListener(OnItemClickListener onItemClickListener);
        void notifyAdapter();
        void notifyAdapterFromTo(int from, int to);
    }

    interface Model {

        void clearMovies();
        void addMovies(List<Movie> movies);

        int getSize();

        Movie getMovieItem(int position);
    }

}
