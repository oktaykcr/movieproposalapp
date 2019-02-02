package com.apriori.movieproposal.service;

import com.apriori.movieproposal.model.DetailedMovie;
import com.apriori.movieproposal.model.Movie;
import com.apriori.movieproposal.model.OmdbResponse;
import com.apriori.movieproposal.repository.DetailedMovieRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.client.RestClientTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.client.MockRestServiceServer;

import static org.hamcrest.CoreMatchers.is;

import java.util.Arrays;
import java.util.List;

import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;
import static org.assertj.core.api.Assertions.*;


@RunWith(SpringRunner.class)
@RestClientTest(MovieService.class)
public class MovieServiceTest {

    @MockBean
    private DetailedMovieRepository detailedMovieRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private MovieService movieService;

    @Autowired
    private MockRestServiceServer server;

    private final String movie_api_url ="http://www.omdbapi.com/";
    private final String movie_api_key = "thewdb";

    private OmdbResponse omdbResponse;

    @Before
    public void setUp() {
        Movie movie = new Movie("Test", "2004", "tt0111111", "movie", "http://test.com");
        Movie movie2 = new Movie("Test two", "2005", "tt0222222", "movie", "http://test.com");
        List<Movie> movieList = Arrays.asList(movie, movie2);
        omdbResponse = new OmdbResponse();
        omdbResponse.setTotalResults("2");
        omdbResponse.setCurrentPage(1);
        omdbResponse.setResponse("True");
        omdbResponse.setSearch(movieList);
        omdbResponse.setSearchedMovieName("Test");
    }

    private DetailedMovie createFullDetailedMovie() {
        DetailedMovie detailedMovie = new DetailedMovie();
        detailedMovie.setId(1l);
        detailedMovie.setTitle("title");
        detailedMovie.setPlot("plot");
        detailedMovie.setGenre("genre");
        detailedMovie.setDirector("director");
        detailedMovie.setYear("2000");
        detailedMovie.setPoster("http://localhost:8080");
        detailedMovie.setImdbRating("7");
        detailedMovie.setImdbId("tt0111111");
        return detailedMovie;
    }

    @Test
    public void getOmbdbResponseFromPage_AllFieldValid_ShouldBeSuccess(){
        try {
            String mockOmdbResponseJson = objectMapper.writeValueAsString(omdbResponse);
            String movieName = omdbResponse.getSearchedMovieName();
            Integer page = omdbResponse.getCurrentPage();

            server.expect(requestTo(movie_api_url + "?s=" + movieName + "&type=movie&page="+ page +"&apikey=" + movie_api_key))
                    .andRespond(withSuccess(mockOmdbResponseJson, MediaType.APPLICATION_JSON));

            OmdbResponse omdbResponse = movieService.getOmbdbResponseFromPage(movieName, page);

            String omdbResponseJson = objectMapper.writeValueAsString(omdbResponse);

            assertThat(omdbResponseJson).isEqualTo(mockOmdbResponseJson);

        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void getDetailedMovieFromImdbId_AllFieldValid_ShouldBeSuccess() {
        DetailedMovie mockDetailedMovie = createFullDetailedMovie();
        String imdbId = mockDetailedMovie.getImdbId();

        try {
            String mockDetailedMovieJson = objectMapper.writeValueAsString(mockDetailedMovie);
            server.expect(requestTo(movie_api_url + "?i=" + imdbId +"&apikey=" + movie_api_key))
                    .andRespond(withSuccess(mockDetailedMovieJson, MediaType.APPLICATION_JSON));

            DetailedMovie detailedMovie = movieService.getDetailedMovieFromImdbId(imdbId);

            assertThat(objectMapper.writeValueAsString(detailedMovie)).isEqualTo(mockDetailedMovieJson);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void getFavoriteMovies_AllFieldValid_ShouldBeSuccess() {
        DetailedMovie mockDetailedMovie = createFullDetailedMovie();
        mockDetailedMovie.setFavorite(true);

        DetailedMovie mockDetailedMovie2 = createFullDetailedMovie();
        mockDetailedMovie.setFavorite(true);

        List<DetailedMovie> mockFavoriteMovies = Arrays.asList(mockDetailedMovie, mockDetailedMovie2);
        Mockito.when(detailedMovieRepository.getDetailedMoviesByIsFavoriteTrue()).thenReturn(mockFavoriteMovies);

        List<DetailedMovie> favoriteMovies = movieService.getFavoriteMovies();

        assertThat(favoriteMovies).isEqualTo(mockFavoriteMovies);
    }

    @Test
    public void getRecommendedMovies_AllFieldValid_ShouldBeSuccess() {
        DetailedMovie mockDetailedMovie = createFullDetailedMovie();
        mockDetailedMovie.setRecommended(true);

        DetailedMovie mockDetailedMovie2 = createFullDetailedMovie();
        mockDetailedMovie.setRecommended(true);

        List<DetailedMovie> mockRecommendedMovies = Arrays.asList(mockDetailedMovie, mockDetailedMovie2);
        Mockito.when(detailedMovieRepository.getDetailedMoviesByIsRecommendedTrue()).thenReturn(mockRecommendedMovies);

        List<DetailedMovie> recommendedMovies = movieService.getRecommendedMovies();

        assertThat(mockRecommendedMovies).isEqualTo(recommendedMovies);
    }

}
