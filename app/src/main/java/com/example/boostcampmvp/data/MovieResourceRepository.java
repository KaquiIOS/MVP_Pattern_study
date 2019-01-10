package com.example.boostcampmvp.data;

import android.content.Context;

/*
* 실제로 데이터를 요청하는 작업을 수행한다
* Singleton Pattern 으로 작성되어 있다.
* Databinding 을 사용하지 않기때문에 Callback 의 연속이다.
 */
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
        movieRemoteDataSource.getMovieData(context, title, startPos, (items, isEnd)->{
            // 데이터를 다 가져오면 callback을 통해 넘겨주기
            if(callback != null) {
                callback.onMovieLoaded(items, isEnd);
            }
        });
    }
}
