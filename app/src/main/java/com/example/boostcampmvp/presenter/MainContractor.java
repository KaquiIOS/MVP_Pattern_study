package com.example.boostcampmvp.presenter;

import android.content.Context;

import com.example.boostcampmvp.adapter.MovieAdapterContractor;
import com.example.boostcampmvp.data.MovieResourceRepository;

/*
* View와 Model을 분리하고 View와 ViewModel을 1:1 관계로 만들어주는 역할
* GoogleArchitecture 에서는 Contractor를 정의하여 내부에 View, Presenter를 정의하는 방식 사용
* View와 Presenter는 서로의 reference를 가지고 있어서 View는 Presenter에게 이벤트를 Notify 하고
* Presenter는 View에게 이벤트에 맞는 적절한 처리를 수행한다.
 */
public interface MainContractor {

    interface View {

        void showToastMessage(String message);
        void showSnackBarMessage(String message);

        void startProgressDialog(String message);
        void endProgressDialog();

        void closeKeypad();
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
