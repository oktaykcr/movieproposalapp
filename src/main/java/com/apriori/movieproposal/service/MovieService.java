package com.apriori.movieproposal.service;

import com.apriori.movieproposal.model.DetailedMovie;
import com.apriori.movieproposal.model.OmdbResponse;

import java.util.List;

public interface MovieService {

    OmdbResponse getOmbdbResponseFromPage(String movieName, Integer page);

    DetailedMovie getDetailedMovieFromImdbId(String imdbId);

    DetailedMovie markAsFavoriteMovie(String imdbId);

    List<DetailedMovie> getFavoriteMovies();

    void recommendMovies();

    List<DetailedMovie> getRecommendedMovies();
}
