package com.example.boostcampmvp.data;

import android.content.Context;

import com.example.boostcampmvp.util.MovieGetAsynTask;

public class MovieRemoteDataSource implements MovieSource {

    @Override
    public void getMovieData(Context context, String title, int startPos, MovieLoadCallback callback) {
        new MovieGetAsynTask(context, (items)-> {
            if(callback != null)
                callback.onMovieLoaded(items);
        }).execute(new String[]{title, Integer.toString(startPos)});
    }
}
