package debo.win.popmovies;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


class MovieGridAdapter extends ArrayAdapter<MovieDetails> {

    private List<MovieDetails> movieListEntries;

    MovieGridAdapter(Context context, int resource, List<MovieDetails> objects) {
        super(context, resource, objects);
        movieListEntries = objects;
    }

    @Override
    public int getCount() {
        return movieListEntries.size();
    }

    /*private view holder class*/
    private class ViewHolder {
        ImageView moviePoster;
        TextView movieTitle;
        TextView movieScore;
        TextView moviePopularity;
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
        ViewHolder holder;
        MovieDetails movieItem = getItem(position);

        LayoutInflater mInflater = (LayoutInflater) this.getContext()
                .getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.movie_list_item, parent, false);
            holder = new ViewHolder();
            holder.movieTitle = (TextView) convertView.findViewById(R.id.movie_list_item_title);
            holder.movieScore = (TextView) convertView.findViewById(R.id.movie_list_item_score);
            holder.moviePopularity = (TextView) convertView.findViewById(R.id.movie_list_item_popularity);
            holder.moviePoster = (ImageView) convertView.findViewById(R.id.movie_list_item_poster);
            convertView.setTag(holder);
        } else
            holder = (ViewHolder) convertView.getTag();

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
        Picasso.with(this.getContext()).load(movieItem.getPosterURL()).into(holder.moviePoster);

        return convertView;

    }

    @Nullable
    @Override
    public MovieDetails getItem(int position) {
        return movieListEntries.get(position);
    }
}