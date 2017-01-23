package debo.win.popmovies;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

public class MoviesFragment extends Fragment implements LoaderManager.LoaderCallbacks<List<MovieDetails>> {

    private MovieGridAdapter mMovieAdapter;
    private RecyclerView mMovieRecyclerView;
    private String[] sortOrders = {"Top Rated", "Most Popular"};
    private int currentSortOrder;
    private int currentPage;
    private static int maxPage;
    private int itemsPerPage = 20;

    public static void setMaxPage(int maxPage) {
        MoviesFragment.maxPage = maxPage;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mMovieAdapter = new MovieGridAdapter(new ArrayList());
        View rootView = inflater.inflate(R.layout.fragment_movies, container, false);
        // Get a reference to the RecyclerView, and attach this adapter to it.
        mMovieRecyclerView = (RecyclerView) rootView.findViewById(R.id.movie_grid);
        mMovieRecyclerView.setHasFixedSize(true);
        final GridLayoutManager layoutManager = new GridLayoutManager(getContext(), 2);
        mMovieRecyclerView.setLayoutManager(layoutManager);
        mMovieRecyclerView.setAdapter(mMovieAdapter);
        mMovieRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                int visibleItemCount = layoutManager.getChildCount();
                int totalItemCount = layoutManager.getItemCount();
                int firstVisibleItem = layoutManager.findFirstCompletelyVisibleItemPosition();
                if(firstVisibleItem+visibleItemCount == totalItemCount
                        && currentPage * itemsPerPage == totalItemCount && currentPage < maxPage) {
                    currentPage++;
                    updateMovieList();
                }
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
                .setSingleChoiceItems(sortOrders, currentSortOrder, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int item) {
                        switch (item) {
                            case 0:
                                mMovieAdapter.clear();
                                currentPage = 1;
                                currentSortOrder = item;
                                mMovieRecyclerView.scrollToPosition(0);
                                sharedPrefs.edit().putInt(getString(R.string.pref_sort_key), item).apply();
                                updateMovieList();
                                break;
                            case 1:
                                mMovieAdapter.clear();
                                currentPage = 1;
                                currentSortOrder = item;
                                mMovieRecyclerView.scrollToPosition(0);
                                sharedPrefs.edit().putInt(getString(R.string.pref_sort_key), item).apply();
                                updateMovieList();
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
        if(mMovieAdapter.getItemCount()==0){
            currentSortOrder = sharedPrefs.getInt(getString(R.string.pref_sort_key), 0);
            mMovieAdapter.clear();
            currentPage = 1;
            updateMovieList();
        }
    }

    public void updateMovieList() {
        if(!isOnline()){
            AlertDialog.Builder builder =
                    new AlertDialog.Builder(this.getContext());
            builder.setTitle(getResources().getString(R.string.app_name));
            builder.setMessage("Check your internet connection...");
            builder.setPositiveButton("Try Again", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    updateMovieList();
                }
            });
            builder.setOnKeyListener(new DialogInterface.OnKeyListener() {
                @Override
                public boolean onKey(DialogInterface dialogInterface, int i, KeyEvent keyEvent) {
                    getActivity().finish();
                    System.exit(0);
                    return false;
                }
            });
            builder.setCancelable(false);
            builder.show();
        }
        else {
            getLoaderManager().restartLoader(1, null, this).forceLoad();
        }
    }

    private boolean isOnline() {
        ConnectivityManager cm =
                (ConnectivityManager) getContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }

    @Override
    public Loader<List<MovieDetails>> onCreateLoader(int id, Bundle bundle) {
        return new FetchMoviesLoader(this.getContext(),
                currentSortOrder, Integer.toString(currentPage));
    }

    @Override
    public void onLoadFinished(Loader<List<MovieDetails>> loader, List<MovieDetails> data) {
        mMovieAdapter.addAll(data);
        mMovieAdapter.notifyDataSetChanged();
    }

    @Override
    public void onLoaderReset(Loader<List<MovieDetails>> loader) {

    }
}
