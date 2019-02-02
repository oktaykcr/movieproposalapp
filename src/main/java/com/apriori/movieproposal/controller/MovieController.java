package com.apriori.movieproposal.controller;

import com.apriori.movieproposal.model.DetailedMovie;
import com.apriori.movieproposal.model.Movie;
import com.apriori.movieproposal.model.OmdbResponse;
import com.apriori.movieproposal.service.MovieService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequestMapping("/movies")
public class MovieController {

    private MovieService movieService;

    @Autowired
    public MovieController(MovieService movieService) {
        this.movieService = movieService;
    }

    @GetMapping({"/", ""})
    public String home() {
        return "home";
    }

    @GetMapping("/search/{movieName}/{pageNumber}")
    public String searchMovieFromPage(@PathVariable("movieName") String movieName, @PathVariable("pageNumber") Integer pageNumber, Model model) {
        OmdbResponse response = movieService.getOmbdbResponseFromPage(movieName, pageNumber);
        model.addAttribute("omdb", response);
        return "home";
    }

    @PostMapping("/favorites/{searchedMovieName}/{pageNumber}/{imdbId}")
    public String markFavorite(@PathVariable("imdbId") String imdbId, @PathVariable("searchedMovieName") String searchedMovieName, @PathVariable("pageNumber") Integer pageNumber) {
        movieService.markAsFavoriteMovie(imdbId);
        return "redirect:/movies/search/"+ searchedMovieName + "/" + pageNumber;
    }

    @GetMapping("/favorites")
    public String getFavorites(Model model) {
        List<DetailedMovie> list = movieService.getFavoriteMovies();
        if(list.size() > 0) {
            model.addAttribute("favMovies", list);
        }
        return "favorites";
    }

    @PostMapping("/favorites/recommend")
    public String recommendMovies(Model model){
        movieService.recommendMovies();
        return "redirect:/movies/recommended/";
    }

    @GetMapping("/recommended")
    public String getRecommendedMovies(Model model){
        List<DetailedMovie> recommendedMovies = movieService.getRecommendedMovies();
        if(recommendedMovies.size() > 1){
           model.addAttribute("recMovies", recommendedMovies);
        }
        return "recommended";
    }

}
