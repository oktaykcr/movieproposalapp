package com.apriori.movieproposal.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class OmdbResponse {

    @JsonProperty("Search")
    private List<Movie> search;

    @JsonProperty("totalResults")
    private String totalResults;

    @JsonProperty("Response")
    private String response;

    private Integer currentPage;
    private String searchedMovieName;

    public OmdbResponse() {
    }

    public List<Movie> getSearch() {
        return search;
    }

    public void setSearch(List<Movie> search) {
        this.search = search;
    }

    public String getTotalResults() {
        return totalResults;
    }

    public void setTotalResults(String totalResults) {
        this.totalResults = totalResults;
    }

    public String getResponse() {
        return response;
    }

    public void setResponse(String response) {
        this.response = response;
    }

    public Integer getCurrentPage() {
        return currentPage;
    }

    public void setCurrentPage(Integer currentPage) {
        this.currentPage = currentPage;
    }

    public String getSearchedMovieName() {
        return searchedMovieName;
    }

    public void setSearchedMovieName(String searchedMovieName) {
        this.searchedMovieName = searchedMovieName;
    }

    @Override
    public String toString() {
        return "OmdbResponse{" +
                "search=" + search +
                ", totalResults='" + totalResults + '\'' +
                ", response='" + response + '\'' +
                '}';
    }
}
