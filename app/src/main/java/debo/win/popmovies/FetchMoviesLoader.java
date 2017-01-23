package debo.win.popmovies;

import android.content.Context;
import android.support.v4.content.AsyncTaskLoader;
import android.util.Log;

import java.io.IOException;
import java.util.List;

import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

class FetchMoviesLoader extends AsyncTaskLoader<List<MovieDetails>> {

    private final String LOG_TAG = FetchMoviesLoader.class.getSimpleName();
    private final String[] API_PATHS = {"top_rated", "popular"};
    private String page;
    private int sortOrder;

    FetchMoviesLoader(Context context, int sortOrder, String page) {
        super(context);
        this.sortOrder = sortOrder;
        this.page = page;
    }

    @Override
    public List<MovieDetails> loadInBackground() {
        String language = "en-US";
        String region = "";

        try {
            String MOVIE_LIST_BASE_URL = "https://api.themoviedb.org/3/";
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(MOVIE_LIST_BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();

            // prepare call in Retrofit 2.0
            MoviesAPI moviesAPI = retrofit.create(MoviesAPI.class);

            Call<MovieListResponse> call = moviesAPI.getPopularMovies(API_PATHS[sortOrder], BuildConfig.TMDB_API_KEY, region, language, page);
            Log.i(LOG_TAG, call.request().url().toString());

            MovieListResponse movieListResponse = call.execute().body();

            int current_page = movieListResponse.getPage();
            int total_pages = movieListResponse.getTotalPages();
            MoviesFragment.setMaxPage(total_pages);
            Log.i(LOG_TAG, String.format("%d/%d", current_page, total_pages));

            return movieListResponse.getResults();
        } catch (IOException e) {
            Log.e(LOG_TAG, "Error ", e);
            e.printStackTrace();
            // If the code didn't successfully get the data, there's no point in attempting
            // to parse it.
            return null;
        }
    }
}