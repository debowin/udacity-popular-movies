package debo.win.popmovies;

import java.util.List;

public class MovieListResponse {
    private int total_pages;
    private List<MovieDetails> results;
    private int page;

    public int getTotalPages() {
        return total_pages;
    }

    public int getPage() {
        return page;
    }

    public List<MovieDetails> getResults() {
        return results;
    }
}
