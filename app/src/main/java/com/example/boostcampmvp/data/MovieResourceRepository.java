package com.example.boostcampmvp.data;

import android.content.Context;

public class MovieResourceRepository implements MovieSource {

    private static MovieResourceRepository INSTANCE;
    private MovieRemoteDataSource movieRemoteDataSource;

    private MovieResourceRepository() {
        this.movieRemoteDataSource = new MovieRemoteDataSource();
    }

    public static MovieResourceRepository getInstance() {
        if(INSTANCE == null)
            INSTANCE = new MovieResourceRepository();
        return INSTANCE;
    }

    @Override
    public void getMovieData(Context context, String title, int startPos, MovieLoadCallback callback) {
        movieRemoteDataSource.getMovieData(context, title, startPos, (items)->{
            // 데이터를 다 가져오면 callback을 통해 넘겨주기
            if(callback != null) {
                callback.onMovieLoaded(items);
            }
        });
    }
}
