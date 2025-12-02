package com.amazonaws.samples.qdevmovies.movies;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.stereotype.Service;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Scanner;
import java.util.stream.Collectors;

@Service
public class MovieService {
    private static final Logger logger = LogManager.getLogger(MovieService.class);
    private final List<Movie> movies;
    private final Map<Long, Movie> movieMap;

    public MovieService() {
        this.movies = loadMoviesFromJson();
        this.movieMap = new HashMap<>();
        for (Movie movie : movies) {
            movieMap.put(movie.getId(), movie);
        }
    }

    private List<Movie> loadMoviesFromJson() {
        List<Movie> movieList = new ArrayList<>();
        try {
            InputStream inputStream = getClass().getClassLoader().getResourceAsStream("movies.json");
            if (inputStream != null) {
                Scanner scanner = new Scanner(inputStream, StandardCharsets.UTF_8.name());
                String jsonContent = scanner.useDelimiter("\\A").next();
                scanner.close();
                
                JSONArray moviesArray = new JSONArray(jsonContent);
                for (int i = 0; i < moviesArray.length(); i++) {
                    JSONObject movieObj = moviesArray.getJSONObject(i);
                    movieList.add(new Movie(
                        movieObj.getLong("id"),
                        movieObj.getString("movieName"),
                        movieObj.getString("director"),
                        movieObj.getInt("year"),
                        movieObj.getString("genre"),
                        movieObj.getString("description"),
                        movieObj.getInt("duration"),
                        movieObj.getDouble("imdbRating")
                    ));
                }
            }
        } catch (Exception e) {
            logger.error("Failed to load movies from JSON: {}", e.getMessage());
        }
        return movieList;
    }

    public List<Movie> getAllMovies() {
        return movies;
    }

    public Optional<Movie> getMovieById(Long id) {
        if (id == null || id <= 0) {
            return Optional.empty();
        }
        return Optional.ofNullable(movieMap.get(id));
    }

    /**
     * Ahoy matey! Search for treasure (movies) in our vast collection!
     * This method filters movies based on name, id, and genre parameters.
     * 
     * @param name Movie name to search for (case-insensitive partial match)
     * @param id Movie ID to search for (exact match)
     * @param genre Movie genre to search for (case-insensitive partial match)
     * @return List of movies matching the search criteria, or empty list if no treasure found
     */
    public List<Movie> searchMovies(String name, Long id, String genre) {
        logger.info("Arrr! Starting treasure hunt with parameters - name: '{}', id: {}, genre: '{}'", 
                   name, id, genre);
        
        List<Movie> searchResults = movies.stream()
            .filter(movie -> matchesSearchCriteria(movie, name, id, genre))
            .collect(Collectors.toList());
        
        logger.info("Shiver me timbers! Found {} pieces of treasure matching yer search criteria", 
                   searchResults.size());
        
        return searchResults;
    }

    /**
     * Batten down the hatches! This method checks if a movie matches our search criteria.
     * 
     * @param movie The movie to examine
     * @param name Name criteria (null or empty means ignore this criterion)
     * @param id ID criteria (null means ignore this criterion)
     * @param genre Genre criteria (null or empty means ignore this criterion)
     * @return true if the movie matches all provided criteria, false otherwise
     */
    private boolean matchesSearchCriteria(Movie movie, String name, Long id, String genre) {
        // Check name criteria - case-insensitive partial match
        if (name != null && !name.trim().isEmpty()) {
            if (!movie.getMovieName().toLowerCase().contains(name.toLowerCase().trim())) {
                return false;
            }
        }
        
        // Check ID criteria - exact match
        if (id != null) {
            if (!movie.getId().equals(id)) {
                return false;
            }
        }
        
        // Check genre criteria - case-insensitive partial match
        if (genre != null && !genre.trim().isEmpty()) {
            if (!movie.getGenre().toLowerCase().contains(genre.toLowerCase().trim())) {
                return false;
            }
        }
        
        return true;
    }

    /**
     * Arrr! Search movies by name only, ye landlubber!
     * 
     * @param name Movie name to search for (case-insensitive partial match)
     * @return List of movies with names containing the search term
     */
    public List<Movie> searchMoviesByName(String name) {
        if (name == null || name.trim().isEmpty()) {
            logger.warn("Blimey! Empty name provided for search, returning all movies");
            return getAllMovies();
        }
        
        logger.info("Searching for movies with name containing: '{}'", name);
        return searchMovies(name, null, null);
    }

    /**
     * Yo ho ho! Search movies by genre, me hearty!
     * 
     * @param genre Movie genre to search for (case-insensitive partial match)
     * @return List of movies with genres containing the search term
     */
    public List<Movie> searchMoviesByGenre(String genre) {
        if (genre == null || genre.trim().isEmpty()) {
            logger.warn("Avast! Empty genre provided for search, returning all movies");
            return getAllMovies();
        }
        
        logger.info("Searching for movies with genre containing: '{}'", genre);
        return searchMovies(null, null, genre);
    }

    /**
     * Chart a course to validate search parameters, matey!
     * 
     * @param name Movie name parameter
     * @param id Movie ID parameter
     * @param genre Movie genre parameter
     * @return true if at least one valid search parameter is provided
     */
    public boolean hasValidSearchParameters(String name, Long id, String genre) {
        boolean hasName = name != null && !name.trim().isEmpty();
        boolean hasId = id != null && id > 0;
        boolean hasGenre = genre != null && !genre.trim().isEmpty();
        
        return hasName || hasId || hasGenre;
    }
}
