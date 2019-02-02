package com.apriori.movieproposal.service;


import com.apriori.movieproposal.model.DetailedMovie;
import com.apriori.movieproposal.model.Movie;
import com.apriori.movieproposal.model.OmdbResponse;
import com.apriori.movieproposal.repository.DetailedMovieRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

@Service
public class MovieServiceImpl implements MovieService {

    @Value("${movie.api.url}")
    private String movie_api_url;

    @Value("${movie.api.key}")
    private String movie_api_key;

    @Autowired
    private DetailedMovieRepository detailedMovieRepository;

    private final RestTemplate restTemplate;


    public MovieServiceImpl(RestTemplateBuilder restTemplateBuilder) {
        restTemplate = restTemplateBuilder.build();
    }

    /**
     * using for pagination
     * @param movieName
     * @param page
     * @return
     */
    @Override
    public OmdbResponse getOmbdbResponseFromPage(String movieName, Integer page) {

        String apiSearchURL = movie_api_url + "?s=" + movieName + "&type=movie&page="+ page +"&apikey=" + movie_api_key;
        ResponseEntity<OmdbResponse> response = restTemplate.getForEntity(apiSearchURL, OmdbResponse.class);

        if(response != null) {

            // remove favoriteMovies from search list
            List<DetailedMovie> favoriteMovieList = getFavoriteMovies();
            List<Movie> movieList = response.getBody().getSearch();
            if(favoriteMovieList != null && movieList != null) {
                for(DetailedMovie detailedMovie : favoriteMovieList) {
                    for(Movie movie : movieList) {
                        if(movie.getImdbID().equals(detailedMovie.getImdbId())) {
                            movieList.remove(movie);
                            break;
                        }
                    }
                }
            }
            // update search list
            response.getBody().setSearch(movieList);

            response.getBody().setCurrentPage(page);
            response.getBody().setSearchedMovieName(movieName);
            return response.getBody();
        }

        return null;
    }

    @Override
    public DetailedMovie getDetailedMovieFromImdbId(String imdbId) {

        String apiSearchURL = movie_api_url + "?i=" + imdbId +"&apikey=" + movie_api_key;
        ResponseEntity<DetailedMovie> response = restTemplate.getForEntity(apiSearchURL, DetailedMovie.class);

        if(response != null) {
            return response.getBody();
        }

        return null;
    }

    @Override
    public DetailedMovie markAsFavoriteMovie(String imdbId) {
        DetailedMovie detailedMovie = getDetailedMovieFromImdbId(imdbId);
        if(detailedMovie != null){
            detailedMovie.setFavorite(true);
            DetailedMovie favoriteMovie = detailedMovieRepository.save(detailedMovie);
            return favoriteMovie;
        }

        return null;
    }

    @Override
    public List<DetailedMovie> getFavoriteMovies() {
        List<DetailedMovie> list = detailedMovieRepository.getDetailedMoviesByIsFavoriteTrue();
        if(list != null) {
            return list;
        }
        return new ArrayList<DetailedMovie>();
    }

    @Override
    @Transactional
    public void recommendMovies() {
        resetRecommendedList();

        List<DetailedMovie> favoriteMovieList = detailedMovieRepository.getDetailedMoviesByIsFavoriteTrue();

        if(favoriteMovieList != null) {
            // Create command for execute apriori algorithm
            File apriori_script = new File("src/main/resources/apriori/apriori_script.py");
            StringBuilder builder = new StringBuilder();
            builder.append("python.exe");
            builder.append(" \"");
            builder.append(apriori_script.getAbsolutePath());
            builder.append("\" ");
            for(DetailedMovie movie : favoriteMovieList) {
                builder.append(movie.getImdbId().substring(2));
                builder.append(" ");
            }
            builder.deleteCharAt(builder.length()-1); // remove last space

            // execute command and get outputs
            Process p;
            BufferedReader reader = null;
            try {
                p = Runtime.getRuntime().exec(builder.toString());
                p.waitFor();
                reader = new BufferedReader(new InputStreamReader(p.getInputStream()));
                String line = "";
                boolean isFavoriteMovie = false;
                while ((line = reader.readLine()) != null) {
                    DetailedMovie recommendedMovie = getDetailedMovieFromImdbId(convertValidImdbId(line));
                    // if the recommended movie is not favorite movie then save to db
                    for(DetailedMovie favoriteMovie : favoriteMovieList) {
                        if(recommendedMovie.getImdbId().equals(favoriteMovie.getImdbId())) {
                            isFavoriteMovie = true;
                            break;
                        }
                    }
                    if(isFavoriteMovie) {
                        isFavoriteMovie = false;
                    } else {
                        recommendedMovie.setRecommended(true);
                        detailedMovieRepository.save(recommendedMovie);
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } finally {
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    public List<DetailedMovie> getRecommendedMovies() {
        List<DetailedMovie> list = detailedMovieRepository.getDetailedMoviesByIsRecommendedTrue();
        if(list != null) {
            return list;
        }
        return new ArrayList<DetailedMovie>();
    }

    /**
     * Converts valid imdbId to get detailed information about movie on omdb api
     * 172495 --> tt0172495
     * @param invalidImdbId
     * @return valid imdbId
     */
    private String convertValidImdbId(String invalidImdbId){
        String validImdbId = invalidImdbId;
        if(validImdbId.length() < 7) {
            while(validImdbId.length() <=6) {
                validImdbId = "0" + validImdbId;
            }
            validImdbId = "tt" + validImdbId;
        }
        return validImdbId;
    }


    /**
     * Removes all recommended from database
     */
    private void resetRecommendedList() {
        // removes all recommended movies before recommends movies
        List<DetailedMovie> recommendedMovieList = detailedMovieRepository.getDetailedMoviesByIsRecommendedTrue();
        if(recommendedMovieList != null && recommendedMovieList.size() > 0) {
            detailedMovieRepository.deleteAllByIsRecommendedIsTrue();
        }
    }


}
