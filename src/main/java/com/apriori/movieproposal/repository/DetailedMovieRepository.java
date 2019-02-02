package com.apriori.movieproposal.repository;

import com.apriori.movieproposal.model.DetailedMovie;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface DetailedMovieRepository extends JpaRepository<DetailedMovie, Long> {

    @Query
    List<DetailedMovie> getDetailedMoviesByIsFavoriteTrue();

    @Query
    List<DetailedMovie> getDetailedMoviesByIsRecommendedTrue();

    @Query
    DetailedMovie getDetailedMovieByImdbId(String imdbId);

    @Query
    List<DetailedMovie> deleteAllByIsRecommendedIsTrue();
}
