package com.apriori.movieproposal.controller;

import com.apriori.movieproposal.model.DetailedMovie;
import com.apriori.movieproposal.model.Movie;
import com.apriori.movieproposal.model.OmdbResponse;
import com.apriori.movieproposal.service.MovieService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@RunWith(SpringRunner.class)
@WebMvcTest(MovieController.class)
public class MovieControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private MovieService movieService;


    private OmdbResponse createOmdbResponse() {
        OmdbResponse omdbResponse = new OmdbResponse();
        omdbResponse.setSearchedMovieName("matrix");
        omdbResponse.setSearch(new ArrayList<Movie>());
        omdbResponse.setResponse("True");
        omdbResponse.setTotalResults("87");
        omdbResponse.setCurrentPage(1);
        return omdbResponse;
    }

    @Test
    public void home_HttpStatusShouldOk() throws Exception {
        mockMvc.perform(get("/movies").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    public void searchMovieFromPage_HttpStatusShouldOk() throws Exception {
        String movieName = "matrix";
        Integer pageNumber = 1;

        OmdbResponse omdbResponse = createOmdbResponse();

        Mockito.when(movieService.getOmbdbResponseFromPage(movieName, pageNumber)).thenReturn(omdbResponse);

        mockMvc.perform(get("/movies/search/{movieName}/{pageNumber}", movieName, pageNumber).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(model().attribute("omdb", omdbResponse));
    }

    @Test
    public void markFavorite_ShouldRedirectToURL() throws Exception {
        String imdbId = "tt0111111";
        String searchedMovieName = "matrix";
        Integer pageNumber = 1;

        Mockito.when(movieService.markAsFavoriteMovie(imdbId)).thenReturn(new DetailedMovie());

        mockMvc.perform(post("/movies/favorites/{searchedMovieName}/{pageNumber}/{imdbId}",searchedMovieName, pageNumber, imdbId)
        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(redirectedUrl("/movies/search/"+ searchedMovieName + "/" + pageNumber));
    }

    @Test
    public void getFavorites_HttpStatusShouldOk() throws Exception {
        List<DetailedMovie> favoriteMovies = Arrays.asList(new DetailedMovie(),
                new DetailedMovie());

        Mockito.when(movieService.getFavoriteMovies()).thenReturn(favoriteMovies);

        mockMvc.perform(get("/movies/favorites").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(model().attribute("favMovies", favoriteMovies));

    }

    @Test
    public void recommendMovies_HttpStatusShouldOk() throws Exception {
        mockMvc.perform(post("/movies/favorites/recommend").contentType(MediaType.APPLICATION_JSON))
                .andExpect(redirectedUrl("/movies/recommended/"));
    }

    @Test
    public void getRecommendedMovies_HttpStatusShouldOk() throws Exception {
        List<DetailedMovie> recommendedMovies = Arrays.asList(new DetailedMovie(),
                new DetailedMovie());

        Mockito.when(movieService.getRecommendedMovies()).thenReturn(recommendedMovies);

        mockMvc.perform(get("/movies/recommended").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(model().attribute("recMovies", recommendedMovies));

    }

}
