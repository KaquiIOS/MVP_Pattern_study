package com.example.boostcampmvp.data;

import android.content.Context;

import java.util.List;

public interface MovieSource {

    interface MovieLoadCallback {
        void onMovieLoaded(List<Movie> items);
    }

    void getMovieData(Context context, String title, int startPos, MovieLoadCallback callback);
}
