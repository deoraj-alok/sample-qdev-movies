package com.amazonaws.samples.qdevmovies.movies;

import com.amazonaws.samples.qdevmovies.utils.MovieIconUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;
import java.util.Optional;

@Controller
public class MoviesController {
    private static final Logger logger = LogManager.getLogger(MoviesController.class);

    @Autowired
    private MovieService movieService;

    @Autowired
    private ReviewService reviewService;

    @GetMapping("/movies")
    public String getMovies(org.springframework.ui.Model model) {
        logger.info("Fetching movies");
        model.addAttribute("movies", movieService.getAllMovies());
        return "movies";
    }

    @GetMapping("/movies/{id}/details")
    public String getMovieDetails(@PathVariable("id") Long movieId, org.springframework.ui.Model model) {
        logger.info("Fetching details for movie ID: {}", movieId);
        
        Optional<Movie> movieOpt = movieService.getMovieById(movieId);
        if (!movieOpt.isPresent()) {
            logger.warn("Movie with ID {} not found", movieId);
            model.addAttribute("title", "Movie Not Found");
            model.addAttribute("message", "Movie with ID " + movieId + " was not found.");
            return "error";
        }
        
        Movie movie = movieOpt.get();
        model.addAttribute("movie", movie);
        model.addAttribute("movieIcon", MovieIconUtils.getMovieIcon(movie.getMovieName()));
        model.addAttribute("allReviews", reviewService.getReviewsForMovie(movie.getId()));
        
        return "movie-details";
    }

    /**
     * Ahoy there, matey! This endpoint handles the treasure hunt for movies.
     * Accepts search parameters and returns filtered results with a search form.
     * 
     * @param name Movie name to search for (optional)
     * @param id Movie ID to search for (optional) 
     * @param genre Movie genre to search for (optional)
     * @param model Spring model for template rendering
     * @return movies template with search results or all movies if no search performed
     */
    @GetMapping("/movies/search")
    public String searchMovies(
            @RequestParam(value = "name", required = false) String name,
            @RequestParam(value = "id", required = false) Long id,
            @RequestParam(value = "genre", required = false) String genre,
            org.springframework.ui.Model model) {
        
        logger.info("Arrr! Captain's orders received for movie search - name: '{}', id: {}, genre: '{}'", 
                   name, id, genre);

        List<Movie> movies;
        String searchMessage = null;
        boolean isSearchPerformed = false;

        // Check if any search parameters were provided
        if (movieService.hasValidSearchParameters(name, id, genre)) {
            isSearchPerformed = true;
            movies = movieService.searchMovies(name, id, genre);
            
            if (movies.isEmpty()) {
                searchMessage = "Blimey! No treasure found matching yer search criteria, ye scurvy dog! " +
                              "Try adjusting yer search terms and set sail again!";
                logger.info("No movies found for search criteria");
            } else {
                searchMessage = String.format("Shiver me timbers! Found %d piece%s of cinematic treasure!", 
                                            movies.size(), movies.size() == 1 ? "" : "s");
                logger.info("Found {} movies matching search criteria", movies.size());
            }
        } else {
            // No search parameters provided, show all movies
            movies = movieService.getAllMovies();
            logger.info("No search parameters provided, showing all movies in the treasure chest");
        }

        // Add attributes for template rendering
        model.addAttribute("movies", movies);
        model.addAttribute("searchMessage", searchMessage);
        model.addAttribute("isSearchPerformed", isSearchPerformed);
        model.addAttribute("searchName", name != null ? name : "");
        model.addAttribute("searchId", id != null ? id.toString() : "");
        model.addAttribute("searchGenre", genre != null ? genre : "");

        return "movies";
    }
}