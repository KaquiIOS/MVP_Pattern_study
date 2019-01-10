package com.example.boostcampmvp.data;

import android.content.Context;

import java.util.List;
/*
* Google Architecture 방식을 따르게 되면
* Presenter 는 Repository 를 통하여 Local, Remote Data 에 접근하게 된다
* 각 각 데이터를 가져오는 방식을 추상화하여 정의했다.
 */
public interface MovieSource {

    interface MovieLoadCallback {
        void onMovieLoaded(List<Movie> items, boolean isEnd);
    }

    void getMovieData(Context context, String title, int startPos, MovieLoadCallback callback);
}
