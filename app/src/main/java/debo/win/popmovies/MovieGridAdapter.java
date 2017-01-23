package debo.win.popmovies;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import butterknife.BindView;
import butterknife.ButterKnife;


class MovieGridAdapter extends RecyclerView.Adapter<MovieGridAdapter.ViewHolder> {

    private List<MovieDetails> movieListEntries;

    MovieGridAdapter(List<MovieDetails> objects) {
        movieListEntries = objects;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.movie_list_item, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        MovieDetails movieItem = movieListEntries.get(position);

        Pattern p = Pattern.compile("^(\\d+)-");
        Matcher m = p.matcher(movieItem.getReleaseDate());
        String releaseYear = "";
        if(m.find())
            releaseYear = m.group(1);
        holder.movieTitle.setText(String.format("%s (%s)", movieItem.getTitle(), releaseYear));
        holder.movieScore.setText(String.format("\u2605 %s", movieItem.getVoteAverage()));
        p = Pattern.compile("(^\\d+\\.\\d)\\d+");
        m = p.matcher(movieItem.getPopularity());
        String popularity = "";
        if(m.find())
            popularity = m.group(1);
        holder.moviePopularity.setText(String.format("\u2764 %s", popularity));
        Context context = holder.moviePoster.getContext();
        Picasso.with(context).load(MovieDetails.getPosterUrlPrefix() + movieItem.getPosterURL())
                .placeholder(R.drawable.icon).into(holder.moviePoster);
    }

    void clear(){
        movieListEntries.clear();
    }

    void addAll(List<MovieDetails> items){
        movieListEntries.addAll(items);
    }

    @Override
    public int getItemCount() {
        return movieListEntries.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder{
        @BindView(R.id.movie_list_item_poster)
        ImageView moviePoster;
        @BindView(R.id.movie_list_item_title)
        TextView movieTitle;
        @BindView(R.id.movie_list_item_score)
        TextView movieScore;
        @BindView(R.id.movie_list_item_popularity)
        TextView moviePopularity;

        ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Context context = view.getContext();
                    int position = getAdapterPosition();
                    MovieDetails movieDetails = movieListEntries.get(position);
                    Intent intent = new Intent(context, DetailActivity.class)
                            .putExtra("movieDetails", movieDetails);
                    context.startActivity(intent);
                }
            });
        }
    }
}