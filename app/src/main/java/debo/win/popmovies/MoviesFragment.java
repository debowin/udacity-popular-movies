package debo.win.popmovies;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.GridView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MoviesFragment extends Fragment {

    private static MovieGridAdapter mMovieAdapter;
    private enum sortOrders{
        TOP_RATED,
        POPULAR
    }
    private String[] choices = {"Top Rated", "Most Popular"};
    private int currentChoice;
    private sortOrders currentSortOrder;
    private int currentPage;
    private int maxPage;
    private int itemsPerPage = 20;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mMovieAdapter = new MovieGridAdapter(this.getActivity(), R.id.movie_grid, new ArrayList());
        View rootView = inflater.inflate(R.layout.fragment_movies, container, false);
        // Get a reference to the ListView, and attach this adapter to it.
        GridView movieGridView = (GridView) rootView.findViewById(R.id.movie_grid);
        movieGridView.setAdapter(mMovieAdapter);
        movieGridView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView absListView, int i) {
            }

            @Override
            public void onScroll(AbsListView absListView, int firstVisibleItem,
                                 int visibleItemCount, int totalItemCount) {
                if(firstVisibleItem+visibleItemCount == totalItemCount
                        && currentPage * itemsPerPage == totalItemCount && currentPage < maxPage) {
                    currentPage++;
                    updateMovieList(currentPage, currentSortOrder);
                }
            }
        });

        movieGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                MovieDetails movieDetails = mMovieAdapter.getItem(position);
                Intent intent = new Intent(getActivity(), DetailActivity.class)
                        .putExtra("movieDetails", movieDetails);
                startActivity(intent);
            }
        });
        return rootView;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Add this line in order for this fragment to handle menu events.
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.movies_fragment, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_change_sort_order) {
            showChoices();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void showChoices() {
        final SharedPreferences sharedPrefs =
                PreferenceManager.getDefaultSharedPreferences(this.getContext());
        new AlertDialog.Builder(getContext())
                .setSingleChoiceItems(choices, currentChoice, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int item) {
                        switch (item) {
                            case 0:
                                mMovieAdapter.clear();
                                currentPage = 1;
                                currentChoice = item;
                                sharedPrefs.edit().putInt(getString(R.string.pref_sort_key), item).apply();
                                currentSortOrder = sortOrders.TOP_RATED;
                                updateMovieList(currentPage, sortOrders.TOP_RATED);
                                break;
                            case 1:
                                mMovieAdapter.clear();
                                currentPage = 1;
                                currentChoice = item;
                                sharedPrefs.edit().putInt(getString(R.string.pref_sort_key), item).apply();
                                currentSortOrder = sortOrders.POPULAR;
                                updateMovieList(currentPage, sortOrders.POPULAR);
                                break;
                        }
                        dialog.dismiss();
                    }
                }).setTitle("View").show();
    }

    @Override
    public void onStart() {
        super.onStart();
        SharedPreferences sharedPrefs =
                PreferenceManager.getDefaultSharedPreferences(this.getContext());
        currentSortOrder = sortOrders.values()[sharedPrefs.getInt(getString(R.string.pref_sort_key),
                sortOrders.TOP_RATED.ordinal())];
        mMovieAdapter.clear();
        currentChoice =  sharedPrefs.getInt(getString(R.string.pref_sort_key),
                sortOrders.TOP_RATED.ordinal());
        currentPage = 1;
        updateMovieList(currentPage, currentSortOrder);
    }

    private void updateMovieList(final int page, final sortOrders sortOrder) {
        if(!isOnline()){
            AlertDialog.Builder builder =
                    new AlertDialog.Builder(this.getContext());
            builder.setTitle(getResources().getString(R.string.app_name));
            builder.setMessage("Check your internet connection...");
            builder.setPositiveButton("Try Again", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    updateMovieList(page, sortOrder);
                }
            });
            builder.setNegativeButton("Exit", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    getActivity().finish();
                    System.exit(0);
                }
            });
            builder.setCancelable(false);
            builder.show();
        }
        FetchMoviesTask fetchMoviesTask = new FetchMoviesTask();
        fetchMoviesTask.execute(sortOrder.name(), Integer.toString(page));
    }

    public boolean isOnline() {
        ConnectivityManager cm =
                (ConnectivityManager) getContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }



    class FetchMoviesTask extends AsyncTask<String, Void, List<MovieDetails>> {

        private final String LOG_TAG = FetchMoviesTask.class.getSimpleName();

        /**
         * Take the String representing the complete forecast in JSON Format and
         * pull out the data we need to construct the Strings needed for the wireframes.
         * <p>
         * Fortunately parsing is easy:  constructor takes the JSON string and converts it
         * into an Object hierarchy for us.
         */
        private List<MovieDetails> getMoviesListFromJson(String movieListJSONString)
                throws JSONException {

            // These are the names of the JSON objects that need to be extracted.
            final String TMDB_LIST = "results";
            final String TMDB_PAGE = "page";
            final String TMDB_TOTAL_PAGES = "total_pages";
            final String TMDB_POSTER_PATH = "poster_path";
            final String TMDB_MOVIE_ID = "id";
            final String TMDB_ADULT = "adult";
            final String TMDB_RELEASE_DATE = "release_date";
            final String TMDB_ORIGINAL_LANGUAGE = "original_language";
            final String TMDB_OVERVIEW = "overview";
            final String TMDB_VOTE_AVERAGE = "vote_average";
            final String TMDB_POPULARITY = "popularity";
            final String TMDB_TITLE = "title";

            JSONObject movieListJSON = new JSONObject(movieListJSONString);
            JSONArray movieArray = movieListJSON.getJSONArray(TMDB_LIST);
            List<MovieDetails> movieListEntries = new ArrayList<>();
            int current_page = movieListJSON.getInt(TMDB_PAGE);
            int total_pages = movieListJSON.getInt(TMDB_TOTAL_PAGES);
            maxPage = total_pages;
            Log.i(LOG_TAG, String.format("%d/%d", current_page, total_pages));

            for (int i = 0; i < movieArray.length(); i++) {

                MovieDetails movieDetails = new MovieDetails();

                // Get the JSON object representing the movie
                JSONObject movie = movieArray.getJSONObject(i);

                String POSTER_URL_PREFIX = "http://image.tmdb.org/t/p/w342";
                movieDetails.setPosterURL(POSTER_URL_PREFIX + movie.getString(TMDB_POSTER_PATH));
                movieDetails.setMovieID(movie.getString(TMDB_MOVIE_ID));
                movieDetails.setTitle(movie.getString(TMDB_TITLE));
                movieDetails.setAdult(movie.getString(TMDB_ADULT));
                movieDetails.setOriginalLanguage(movie.getString(TMDB_ORIGINAL_LANGUAGE));
                movieDetails.setOverview(movie.getString(TMDB_OVERVIEW));
                movieDetails.setVoteAverage(movie.getString(TMDB_VOTE_AVERAGE));
                movieDetails.setReleaseDate(movie.getString(TMDB_RELEASE_DATE));
                movieDetails.setPopularity(movie.getString(TMDB_POPULARITY));

                movieListEntries.add(movieDetails);
            }
            return movieListEntries;
        }

        @Override
        protected List<MovieDetails> doInBackground(String... params) {

            // If there's no currentSortOrder, there's nothing to order by.  Verify size of params.
            if (params.length == 0) {
                return null;
            }

            // These two need to be declared outside the try/catch
            // so that they can be closed in the finally block.
            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;

            // Will contain the raw JSON response as a string.
            String moviesListJSONString;

            String sortOrder = params[0];
            String language = "en-US";
            String region = "";
            String page = params[1];

            try {
                final String MOVIE_LIST_BASE_URL =
                        "https://api.themoviedb.org/3/movie/";
                final String REGION_PARAM = "region";
                final String PAGE_PARAM = "page";
                final String LANG_PARAM = "language";
                final String API_KEY_PARAM = "api_key";

                Uri builtUri = Uri.parse(MOVIE_LIST_BASE_URL).buildUpon()
                        .appendPath(sortOrder.toLowerCase())
                        .appendQueryParameter(REGION_PARAM, region)
                        .appendQueryParameter(LANG_PARAM, language)
                        .appendQueryParameter(PAGE_PARAM, page)
                        .appendQueryParameter(API_KEY_PARAM, BuildConfig.TMDB_API_KEY)
                        .build();

                URL url = new URL(builtUri.toString());

                // Create the request to OpenWeatherMap, and open the connection
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                // Read the input stream into a String
                InputStream inputStream = urlConnection.getInputStream();
                StringBuffer buffer = new StringBuffer();
                if (inputStream == null) {
                    // Nothing to do.
                    return null;
                }
                reader = new BufferedReader(new InputStreamReader(inputStream));

                String line;
                while ((line = reader.readLine()) != null) {
                    // Since it's JSON, adding a newline isn't necessary (it won't affect parsing)
                    // But it does make debugging a *lot* easier if you print out the completed
                    // buffer for debugging.
                    buffer.append(line + "\n");
                }

                if (buffer.length() == 0) {
                    // Stream was empty.  No point in parsing.
                    return null;
                }
                moviesListJSONString = buffer.toString();
            } catch (IOException e) {
                Log.e(LOG_TAG, "Error ", e);
                // If the code didn't successfully get the weather data, there's no point in attemping
                // to parse it.
                return null;
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (final IOException e) {
                        Log.e(LOG_TAG, "Error closing stream", e);
                    }
                }
            }
            try {
                return getMoviesListFromJson(moviesListJSONString);
            } catch (JSONException e) {
                Log.e(LOG_TAG, e.getMessage(), e);
                e.printStackTrace();
            }
            // This will only happen if there was an error getting or parsing the forecast.
            return null;
        }

        @Override
        protected void onPostExecute(List<MovieDetails> result) {
            if (result != null) {
                for(MovieDetails movie : result) {
                    mMovieAdapter.add(movie);
                }
                // New data is back from the server.  Hooray!
            }
        }
    }
}
