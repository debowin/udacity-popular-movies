package debo.win.popmovies;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import butterknife.BindView;
import butterknife.ButterKnife;

public class DetailFragment extends Fragment {
    private MovieDetails movieDetails;

    @BindView(R.id.movie_poster)
    ImageView moviePoster;
    @BindView(R.id.movie_title)
    TextView movieTitle;
    @BindView(R.id.movie_release_date)
    TextView movieReleaseDate;
    @BindView(R.id.movie_vote_average)
    TextView movieVoteAverage;
    @BindView(R.id.movie_popularity)
    TextView moviePopularity;
    @BindView(R.id.movie_language)
    TextView movieLanguage;
    @BindView(R.id.movie_synopsis)
    TextView movieSynopsis;

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
        ButterKnife.bind(this, rootView);

        // Inflate the layout for this fragment
        // The detail Activity called via intent.  Inspect the intent for forecast data.
        Intent intent = getActivity().getIntent();
        if (intent != null && intent.hasExtra("movieDetails")) {
            movieDetails = intent.getParcelableExtra("movieDetails");
        }
        Picasso.with(this.getContext()).load(MovieDetails.getPosterUrlPrefix() + movieDetails.getPosterURL())
                .placeholder(R.drawable.icon).into(moviePoster);
        movieTitle.setText(movieDetails.getTitle());
        movieReleaseDate.setText(String.format("\uD83D\uDCC5 %s",movieDetails.getReleaseDate()));
        movieVoteAverage.setText(String.format("\u2605 %s",movieDetails.getVoteAverage()));
        moviePopularity.setText(String.format("\u2764 %s",movieDetails.getPopularity()));
        movieLanguage.setText(String.format("\uD83C\uDF10 %s",movieDetails.getOriginalLanguage().toUpperCase()));
        movieSynopsis.setText(movieDetails.getOverview());
        return rootView;
    }
}
