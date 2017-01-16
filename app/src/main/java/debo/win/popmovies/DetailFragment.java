package debo.win.popmovies;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.Objects;

public class DetailFragment extends Fragment {
    private MovieDetails movieDetails;

    public DetailFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_detail, container, false);

        // Inflate the layout for this fragment
        // The detail Activity called via intent.  Inspect the intent for forecast data.
        Intent intent = getActivity().getIntent();
        if (intent != null && intent.hasExtra("movieDetails")) {
            movieDetails = intent.getParcelableExtra("movieDetails");
        }
        ImageView moviePoster = (ImageView) rootView.findViewById(R.id.movie_poster);
        TextView movieTitle = (TextView) rootView.findViewById(R.id.movie_title);
        TextView movieReleaseDate = (TextView) rootView.findViewById(R.id.movie_release_date);
        TextView movieVoteAverage = (TextView) rootView.findViewById(R.id.movie_vote_average);
        TextView moviePopularity = (TextView) rootView.findViewById(R.id.movie_popularity);
        TextView movieLanguage = (TextView) rootView.findViewById(R.id.movie_language);
        TextView movieSynopsis = (TextView) rootView.findViewById(R.id.movie_synopsis);

        Picasso.with(this.getContext()).load(movieDetails.getPosterURL()).into(moviePoster);
        movieTitle.setText(movieDetails.getTitle());
        movieReleaseDate.setText(String.format("\uD83D\uDCC5 %s",movieDetails.getReleaseDate()));
        movieVoteAverage.setText(String.format("\u2605 %s",movieDetails.getVoteAverage()));
        moviePopularity.setText(String.format("\u2764 %s",movieDetails.getPopularity()));
        movieLanguage.setText(String.format("\uD83C\uDF10 %s",movieDetails.getOriginalLanguage().toUpperCase()));
        movieSynopsis.setText(movieDetails.getOverview());
        return rootView;
    }
}
