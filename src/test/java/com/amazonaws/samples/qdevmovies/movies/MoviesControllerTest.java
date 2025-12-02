package com.amazonaws.samples.qdevmovies.movies;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.ui.Model;
import org.springframework.ui.ExtendedModelMap;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertFalse;

public class MoviesControllerTest {

    private MoviesController moviesController;
    private Model model;
    private MovieService mockMovieService;
    private ReviewService mockReviewService;

    @BeforeEach
    public void setUp() {
        moviesController = new MoviesController();
        model = new ExtendedModelMap();
        
        // Create mock services with enhanced search functionality
        mockMovieService = new MovieService() {
            private final List<Movie> testMovies = Arrays.asList(
                new Movie(1L, "Test Movie", "Test Director", 2023, "Drama", "Test description", 120, 4.5),
                new Movie(2L, "Action Hero", "Action Director", 2022, "Action", "Action description", 110, 4.0),
                new Movie(3L, "Comedy Gold", "Comedy Director", 2021, "Comedy", "Comedy description", 95, 3.5)
            );
            
            @Override
            public List<Movie> getAllMovies() {
                return testMovies;
            }
            
            @Override
            public Optional<Movie> getMovieById(Long id) {
                return testMovies.stream().filter(m -> m.getId().equals(id)).findFirst();
            }
            
            @Override
            public List<Movie> searchMovies(String name, Long id, String genre) {
                return testMovies.stream()
                    .filter(movie -> {
                        if (name != null && !name.trim().isEmpty()) {
                            if (!movie.getMovieName().toLowerCase().contains(name.toLowerCase().trim())) {
                                return false;
                            }
                        }
                        if (id != null) {
                            if (!movie.getId().equals(id)) {
                                return false;
                            }
                        }
                        if (genre != null && !genre.trim().isEmpty()) {
                            if (!movie.getGenre().toLowerCase().contains(genre.toLowerCase().trim())) {
                                return false;
                            }
                        }
                        return true;
                    })
                    .collect(java.util.stream.Collectors.toList());
            }
            
            @Override
            public List<Movie> searchMoviesByName(String name) {
                if (name == null || name.trim().isEmpty()) {
                    return getAllMovies();
                }
                return searchMovies(name, null, null);
            }
            
            @Override
            public List<Movie> searchMoviesByGenre(String genre) {
                if (genre == null || genre.trim().isEmpty()) {
                    return getAllMovies();
                }
                return searchMovies(null, null, genre);
            }
            
            @Override
            public boolean hasValidSearchParameters(String name, Long id, String genre) {
                boolean hasName = name != null && !name.trim().isEmpty();
                boolean hasId = id != null && id > 0;
                boolean hasGenre = genre != null && !genre.trim().isEmpty();
                return hasName || hasId || hasGenre;
            }
        };
        
        mockReviewService = new ReviewService() {
            @Override
            public List<Review> getReviewsForMovie(long movieId) {
                return new ArrayList<>();
            }
        };
        
        // Inject mocks using reflection
        try {
            java.lang.reflect.Field movieServiceField = MoviesController.class.getDeclaredField("movieService");
            movieServiceField.setAccessible(true);
            movieServiceField.set(moviesController, mockMovieService);
            
            java.lang.reflect.Field reviewServiceField = MoviesController.class.getDeclaredField("reviewService");
            reviewServiceField.setAccessible(true);
            reviewServiceField.set(moviesController, mockReviewService);
        } catch (Exception e) {
            throw new RuntimeException("Failed to inject mock services", e);
        }
    }

    @Test
    public void testGetMovies() {
        String result = moviesController.getMovies(model);
        assertNotNull(result);
        assertEquals("movies", result);
    }

    @Test
    public void testGetMovieDetails() {
        String result = moviesController.getMovieDetails(1L, model);
        assertNotNull(result);
        assertEquals("movie-details", result);
    }

    @Test
    public void testGetMovieDetailsNotFound() {
        String result = moviesController.getMovieDetails(999L, model);
        assertNotNull(result);
        assertEquals("error", result);
    }

    @Test
    public void testMovieServiceIntegration() {
        List<Movie> movies = mockMovieService.getAllMovies();
        assertEquals(3, movies.size());
        assertEquals("Test Movie", movies.get(0).getMovieName());
    }

    // ===== NEW SEARCH FUNCTIONALITY TESTS =====

    @Test
    public void testSearchMoviesWithNoParameters() {
        String result = moviesController.searchMovies(null, null, null, model);
        assertEquals("movies", result);
        
        @SuppressWarnings("unchecked")
        List<Movie> movies = (List<Movie>) model.getAttribute("movies");
        assertEquals(3, movies.size()); // Should return all movies
        assertFalse((Boolean) model.getAttribute("isSearchPerformed"));
    }

    @Test
    public void testSearchMoviesByName() {
        String result = moviesController.searchMovies("Test", null, null, model);
        assertEquals("movies", result);
        
        @SuppressWarnings("unchecked")
        List<Movie> movies = (List<Movie>) model.getAttribute("movies");
        assertEquals(1, movies.size());
        assertEquals("Test Movie", movies.get(0).getMovieName());
        assertTrue((Boolean) model.getAttribute("isSearchPerformed"));
        assertNotNull(model.getAttribute("searchMessage"));
    }

    @Test
    public void testSearchMoviesByNameCaseInsensitive() {
        String result = moviesController.searchMovies("action", null, null, model);
        assertEquals("movies", result);
        
        @SuppressWarnings("unchecked")
        List<Movie> movies = (List<Movie>) model.getAttribute("movies");
        assertEquals(1, movies.size());
        assertEquals("Action Hero", movies.get(0).getMovieName());
    }

    @Test
    public void testSearchMoviesById() {
        String result = moviesController.searchMovies(null, 2L, null, model);
        assertEquals("movies", result);
        
        @SuppressWarnings("unchecked")
        List<Movie> movies = (List<Movie>) model.getAttribute("movies");
        assertEquals(1, movies.size());
        assertEquals("Action Hero", movies.get(0).getMovieName());
        assertTrue((Boolean) model.getAttribute("isSearchPerformed"));
    }

    @Test
    public void testSearchMoviesByGenre() {
        String result = moviesController.searchMovies(null, null, "Comedy", model);
        assertEquals("movies", result);
        
        @SuppressWarnings("unchecked")
        List<Movie> movies = (List<Movie>) model.getAttribute("movies");
        assertEquals(1, movies.size());
        assertEquals("Comedy Gold", movies.get(0).getMovieName());
        assertTrue((Boolean) model.getAttribute("isSearchPerformed"));
    }

    @Test
    public void testSearchMoviesWithMultipleParameters() {
        String result = moviesController.searchMovies("Action", 2L, "Action", model);
        assertEquals("movies", result);
        
        @SuppressWarnings("unchecked")
        List<Movie> movies = (List<Movie>) model.getAttribute("movies");
        assertEquals(1, movies.size());
        assertEquals("Action Hero", movies.get(0).getMovieName());
    }

    @Test
    public void testSearchMoviesNoResults() {
        String result = moviesController.searchMovies("NonExistent", null, null, model);
        assertEquals("movies", result);
        
        @SuppressWarnings("unchecked")
        List<Movie> movies = (List<Movie>) model.getAttribute("movies");
        assertEquals(0, movies.size());
        assertTrue((Boolean) model.getAttribute("isSearchPerformed"));
        
        String searchMessage = (String) model.getAttribute("searchMessage");
        assertNotNull(searchMessage);
        assertTrue(searchMessage.contains("Blimey"));
    }

    @Test
    public void testSearchMoviesWithEmptyStrings() {
        String result = moviesController.searchMovies("", null, "", model);
        assertEquals("movies", result);
        
        @SuppressWarnings("unchecked")
        List<Movie> movies = (List<Movie>) model.getAttribute("movies");
        assertEquals(3, movies.size()); // Should return all movies when empty strings provided
        assertFalse((Boolean) model.getAttribute("isSearchPerformed"));
    }

    @Test
    public void testSearchMoviesWithWhitespaceOnly() {
        String result = moviesController.searchMovies("   ", null, "   ", model);
        assertEquals("movies", result);
        
        @SuppressWarnings("unchecked")
        List<Movie> movies = (List<Movie>) model.getAttribute("movies");
        assertEquals(3, movies.size()); // Should return all movies when whitespace only
        assertFalse((Boolean) model.getAttribute("isSearchPerformed"));
    }

    @Test
    public void testSearchMoviesPartialMatch() {
        String result = moviesController.searchMovies("Gold", null, null, model);
        assertEquals("movies", result);
        
        @SuppressWarnings("unchecked")
        List<Movie> movies = (List<Movie>) model.getAttribute("movies");
        assertEquals(1, movies.size());
        assertEquals("Comedy Gold", movies.get(0).getMovieName());
    }

    @Test
    public void testSearchMoviesModelAttributes() {
        String result = moviesController.searchMovies("Test", 1L, "Drama", model);
        assertEquals("movies", result);
        
        // Check all model attributes are set correctly
        assertEquals("Test", model.getAttribute("searchName"));
        assertEquals("1", model.getAttribute("searchId"));
        assertEquals("Drama", model.getAttribute("searchGenre"));
        assertTrue((Boolean) model.getAttribute("isSearchPerformed"));
        assertNotNull(model.getAttribute("searchMessage"));
        
        String searchMessage = (String) model.getAttribute("searchMessage");
        assertTrue(searchMessage.contains("Shiver me timbers"));
    }

    @Test
    public void testSearchMoviesInvalidId() {
        String result = moviesController.searchMovies(null, 999L, null, model);
        assertEquals("movies", result);
        
        @SuppressWarnings("unchecked")
        List<Movie> movies = (List<Movie>) model.getAttribute("movies");
        assertEquals(0, movies.size());
        assertTrue((Boolean) model.getAttribute("isSearchPerformed"));
    }

    // ===== MOVIE SERVICE SEARCH TESTS =====

    @Test
    public void testMovieServiceSearchByName() {
        List<Movie> results = mockMovieService.searchMoviesByName("Test");
        assertEquals(1, results.size());
        assertEquals("Test Movie", results.get(0).getMovieName());
    }

    @Test
    public void testMovieServiceSearchByNameEmpty() {
        List<Movie> results = mockMovieService.searchMoviesByName("");
        assertEquals(3, results.size()); // Should return all movies
    }

    @Test
    public void testMovieServiceSearchByGenre() {
        List<Movie> results = mockMovieService.searchMoviesByGenre("Action");
        assertEquals(1, results.size());
        assertEquals("Action Hero", results.get(0).getMovieName());
    }

    @Test
    public void testMovieServiceHasValidSearchParameters() {
        assertTrue(mockMovieService.hasValidSearchParameters("test", null, null));
        assertTrue(mockMovieService.hasValidSearchParameters(null, 1L, null));
        assertTrue(mockMovieService.hasValidSearchParameters(null, null, "genre"));
        assertTrue(mockMovieService.hasValidSearchParameters("test", 1L, "genre"));
        
        assertFalse(mockMovieService.hasValidSearchParameters(null, null, null));
        assertFalse(mockMovieService.hasValidSearchParameters("", null, ""));
        assertFalse(mockMovieService.hasValidSearchParameters("   ", null, "   "));
        assertFalse(mockMovieService.hasValidSearchParameters(null, 0L, null));
        assertFalse(mockMovieService.hasValidSearchParameters(null, -1L, null));
    }

    @Test
    public void testMovieServiceSearchMoviesComplex() {
        // Test complex search scenarios
        List<Movie> results = mockMovieService.searchMovies("Action", 2L, "Action");
        assertEquals(1, results.size());
        assertEquals("Action Hero", results.get(0).getMovieName());
        
        // Test conflicting criteria (should return empty)
        results = mockMovieService.searchMovies("Test", 2L, null);
        assertEquals(0, results.size());
        
        // Test partial matches
        results = mockMovieService.searchMovies("o", null, null);
        assertEquals(3, results.size()); // All movies contain 'o'
    }
}
