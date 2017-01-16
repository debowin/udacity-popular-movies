package debo.win.popmovies;

import android.os.Parcel;
import android.os.Parcelable;

class MovieDetails implements Parcelable{
    private String posterURL;
    private String movieID;
    private String adult;
    private String overview;
    private String release_date;
    private String original_title;
    private String original_language;
    private String title;
    private String backdrop_path;
    private String popularity;
    private String vote_count;
    private String video;
    private String vote_average;


    private MovieDetails(Parcel in) {
        posterURL = in.readString();
        movieID = in.readString();
        adult = in.readString();
        overview = in.readString();
        release_date = in.readString();
        original_title = in.readString();
        original_language = in.readString();
        title = in.readString();
        backdrop_path = in.readString();
        popularity = in.readString();
        vote_count = in.readString();
        video = in.readString();
        vote_average = in.readString();
    }

    MovieDetails(){
    }

    public static final Creator<MovieDetails> CREATOR = new Creator<MovieDetails>() {
        @Override
        public MovieDetails createFromParcel(Parcel in) {
            return new MovieDetails(in);
        }

        @Override
        public MovieDetails[] newArray(int size) {
            return new MovieDetails[size];
        }
    };

    String getPosterURL() {
        return posterURL;
    }

    void setPosterURL(String posterURL) {
        this.posterURL = posterURL;
    }

    String getPopularity() {
        return popularity;
    }

    void setPopularity(String popularity) {
        this.popularity = popularity;
    }

    String getMovieID() {
        return movieID;
    }

    void setMovieID(String movieID) {
        this.movieID = movieID;
    }

    String getTitle() {
        return title;
    }

    void setTitle(String title) {
        this.title = title;
    }

    String getAdult() {
        return adult;
    }

    void setAdult(String adult) {
        this.adult = adult;
    }

    String getOverview() {
        return overview;
    }

    void setOverview(String overview) {
        this.overview = overview;
    }

    String getReleaseDate() {
        return release_date;
    }

    void setReleaseDate(String release_date) {
        this.release_date = release_date;
    }

    String getOriginalLanguage() {
        return original_language;
    }

    void setOriginalLanguage(String original_language) {
        this.original_language = original_language;
    }

    String getVoteAverage() {
        return vote_average;
    }

    void setVoteAverage(String vote_average) {
        this.vote_average = vote_average;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(posterURL);
        parcel.writeString(movieID);
        parcel.writeString(adult);
        parcel.writeString(overview);
        parcel.writeString(release_date);
        parcel.writeString(original_title);
        parcel.writeString(original_language);
        parcel.writeString(title);
        parcel.writeString(backdrop_path);
        parcel.writeString(popularity);
        parcel.writeString(vote_count);
        parcel.writeString(video);
        parcel.writeString(vote_average);
    }
}

