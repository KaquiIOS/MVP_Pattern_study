package com.example.boostcampmvp.presenter;

import android.content.Context;

import com.example.boostcampmvp.adapter.MovieAdapterContractor;
import com.example.boostcampmvp.data.MovieResourceRepository;

public interface MainContractor {

    interface View {

        void showToastMessage(String message);
        void showSnackBarMessage(String message);

        void startProgressDialog(String message);
        void endProgressDialog();
    }

    interface Presenter {

        void attachView(View view);
        void detachView();

        void setMovieRepository(MovieResourceRepository movieRepository);

        void setMovieAdapterView(MovieAdapterContractor.View view);
        void setMovieAdapterModel(MovieAdapterContractor.Model model);

        void searchMovie(Context context, String keyword);
        void loadMoreMovies(Context context, int startPos);
    }
}
