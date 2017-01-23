package debo.win.popmovies;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

interface MoviesAPI {
    @GET("movie/{sort_order}")
    Call<MovieListResponse> getPopularMovies(@Path("sort_order") String sortOrder, @Query("api_key") String apiKey, @Query("region") String region,
                                             @Query("langauge") String language, @Query("page") String page);
}
