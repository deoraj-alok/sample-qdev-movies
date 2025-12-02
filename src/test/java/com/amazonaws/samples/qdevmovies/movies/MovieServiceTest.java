package com.amazonaws.samples.qdevmovies.movies;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Ahoy matey! Comprehensive test suite for the MovieService treasure hunting functionality.
 * These tests ensure our search methods work ship-shape and Bristol fashion!
 */
public class MovieServiceTest {

    private MovieService movieService;

    @BeforeEach
    public void setUp() {
        movieService = new MovieService();
    }

    // ===== BASIC FUNCTIONALITY TESTS =====

    @Test
    public void testGetAllMovies() {
        List<Movie> movies = movieService.getAllMovies();
        assertNotNull(movies);
        assertEquals(12, movies.size()); // Should load all 12 movies from JSON
    }

    @Test
    public void testGetMovieById() {
        Optional<Movie> movie = movieService.getMovieById(1L);
        assertTrue(movie.isPresent());
        assertEquals("The Prison Escape", movie.get().getMovieName());
    }

    @Test
    public void testGetMovieByIdNotFound() {
        Optional<Movie> movie = movieService.getMovieById(999L);
        assertFalse(movie.isPresent());
    }

    @Test
    public void testGetMovieByIdNull() {
        Optional<Movie> movie = movieService.getMovieById(null);
        assertFalse(movie.isPresent());
    }

    @Test
    public void testGetMovieByIdZero() {
        Optional<Movie> movie = movieService.getMovieById(0L);
        assertFalse(movie.isPresent());
    }

    @Test
    public void testGetMovieByIdNegative() {
        Optional<Movie> movie = movieService.getMovieById(-1L);
        assertFalse(movie.isPresent());
    }

    // ===== SEARCH BY NAME TESTS =====

    @Test
    public void testSearchMoviesByNameExactMatch() {
        List<Movie> results = movieService.searchMoviesByName("The Prison Escape");
        assertEquals(1, results.size());
        assertEquals("The Prison Escape", results.get(0).getMovieName());
    }

    @Test
    public void testSearchMoviesByNamePartialMatch() {
        List<Movie> results = movieService.searchMoviesByName("Prison");
        assertEquals(1, results.size());
        assertEquals("The Prison Escape", results.get(0).getMovieName());
    }

    @Test
    public void testSearchMoviesByNameCaseInsensitive() {
        List<Movie> results = movieService.searchMoviesByName("PRISON");
        assertEquals(1, results.size());
        assertEquals("The Prison Escape", results.get(0).getMovieName());
    }

    @Test
    public void testSearchMoviesByNameMultipleResults() {
        List<Movie> results = movieService.searchMoviesByName("The");
        assertTrue(results.size() > 1); // Multiple movies start with "The"
    }

    @Test
    public void testSearchMoviesByNameNoResults() {
        List<Movie> results = movieService.searchMoviesByName("NonExistentMovie");
        assertEquals(0, results.size());
    }

    @Test
    public void testSearchMoviesByNameEmpty() {
        List<Movie> results = movieService.searchMoviesByName("");
        assertEquals(12, results.size()); // Should return all movies
    }

    @Test
    public void testSearchMoviesByNameNull() {
        List<Movie> results = movieService.searchMoviesByName(null);
        assertEquals(12, results.size()); // Should return all movies
    }

    @Test
    public void testSearchMoviesByNameWhitespace() {
        List<Movie> results = movieService.searchMoviesByName("   ");
        assertEquals(12, results.size()); // Should return all movies
    }

    // ===== SEARCH BY GENRE TESTS =====

    @Test
    public void testSearchMoviesByGenreExactMatch() {
        List<Movie> results = movieService.searchMoviesByGenre("Drama");
        assertTrue(results.size() > 0);
        assertTrue(results.stream().allMatch(m -> m.getGenre().toLowerCase().contains("drama")));
    }

    @Test
    public void testSearchMoviesByGenrePartialMatch() {
        List<Movie> results = movieService.searchMoviesByGenre("Action");
        assertTrue(results.size() > 0);
        assertTrue(results.stream().allMatch(m -> m.getGenre().toLowerCase().contains("action")));
    }

    @Test
    public void testSearchMoviesByGenreCaseInsensitive() {
        List<Movie> results = movieService.searchMoviesByGenre("DRAMA");
        assertTrue(results.size() > 0);
        assertTrue(results.stream().allMatch(m -> m.getGenre().toLowerCase().contains("drama")));
    }

    @Test
    public void testSearchMoviesByGenreCompound() {
        List<Movie> results = movieService.searchMoviesByGenre("Crime");
        assertTrue(results.size() > 0);
        // Should find movies with "Crime/Drama" genre
        assertTrue(results.stream().anyMatch(m -> m.getGenre().contains("Crime")));
    }

    @Test
    public void testSearchMoviesByGenreNoResults() {
        List<Movie> results = movieService.searchMoviesByGenre("Horror");
        assertEquals(0, results.size());
    }

    @Test
    public void testSearchMoviesByGenreEmpty() {
        List<Movie> results = movieService.searchMoviesByGenre("");
        assertEquals(12, results.size()); // Should return all movies
    }

    @Test
    public void testSearchMoviesByGenreNull() {
        List<Movie> results = movieService.searchMoviesByGenre(null);
        assertEquals(12, results.size()); // Should return all movies
    }

    // ===== COMPREHENSIVE SEARCH TESTS =====

    @Test
    public void testSearchMoviesWithNameOnly() {
        List<Movie> results = movieService.searchMovies("Prison", null, null);
        assertEquals(1, results.size());
        assertEquals("The Prison Escape", results.get(0).getMovieName());
    }

    @Test
    public void testSearchMoviesWithIdOnly() {
        List<Movie> results = movieService.searchMovies(null, 1L, null);
        assertEquals(1, results.size());
        assertEquals("The Prison Escape", results.get(0).getMovieName());
    }

    @Test
    public void testSearchMoviesWithGenreOnly() {
        List<Movie> results = movieService.searchMovies(null, null, "Drama");
        assertTrue(results.size() > 0);
        assertTrue(results.stream().allMatch(m -> m.getGenre().toLowerCase().contains("drama")));
    }

    @Test
    public void testSearchMoviesWithNameAndId() {
        List<Movie> results = movieService.searchMovies("Prison", 1L, null);
        assertEquals(1, results.size());
        assertEquals("The Prison Escape", results.get(0).getMovieName());
    }

    @Test
    public void testSearchMoviesWithNameAndGenre() {
        List<Movie> results = movieService.searchMovies("Prison", null, "Drama");
        assertEquals(1, results.size());
        assertEquals("The Prison Escape", results.get(0).getMovieName());
    }

    @Test
    public void testSearchMoviesWithIdAndGenre() {
        List<Movie> results = movieService.searchMovies(null, 1L, "Drama");
        assertEquals(1, results.size());
        assertEquals("The Prison Escape", results.get(0).getMovieName());
    }

    @Test
    public void testSearchMoviesWithAllParameters() {
        List<Movie> results = movieService.searchMovies("Prison", 1L, "Drama");
        assertEquals(1, results.size());
        assertEquals("The Prison Escape", results.get(0).getMovieName());
    }

    @Test
    public void testSearchMoviesConflictingCriteria() {
        // Search for "Prison" with ID 2 (which is "The Family Boss")
        List<Movie> results = movieService.searchMovies("Prison", 2L, null);
        assertEquals(0, results.size()); // Should return no results due to conflict
    }

    @Test
    public void testSearchMoviesAllNull() {
        List<Movie> results = movieService.searchMovies(null, null, null);
        assertEquals(12, results.size()); // Should return all movies
    }

    @Test
    public void testSearchMoviesAllEmpty() {
        List<Movie> results = movieService.searchMovies("", null, "");
        assertEquals(12, results.size()); // Should return all movies
    }

    // ===== PARAMETER VALIDATION TESTS =====

    @Test
    public void testHasValidSearchParametersWithName() {
        assertTrue(movieService.hasValidSearchParameters("test", null, null));
    }

    @Test
    public void testHasValidSearchParametersWithId() {
        assertTrue(movieService.hasValidSearchParameters(null, 1L, null));
    }

    @Test
    public void testHasValidSearchParametersWithGenre() {
        assertTrue(movieService.hasValidSearchParameters(null, null, "genre"));
    }

    @Test
    public void testHasValidSearchParametersWithMultiple() {
        assertTrue(movieService.hasValidSearchParameters("name", 1L, "genre"));
    }

    @Test
    public void testHasValidSearchParametersAllNull() {
        assertFalse(movieService.hasValidSearchParameters(null, null, null));
    }

    @Test
    public void testHasValidSearchParametersEmptyStrings() {
        assertFalse(movieService.hasValidSearchParameters("", null, ""));
    }

    @Test
    public void testHasValidSearchParametersWhitespace() {
        assertFalse(movieService.hasValidSearchParameters("   ", null, "   "));
    }

    @Test
    public void testHasValidSearchParametersZeroId() {
        assertFalse(movieService.hasValidSearchParameters(null, 0L, null));
    }

    @Test
    public void testHasValidSearchParametersNegativeId() {
        assertFalse(movieService.hasValidSearchParameters(null, -1L, null));
    }

    // ===== EDGE CASE TESTS =====

    @Test
    public void testSearchMoviesWithSpecialCharacters() {
        // Test searching for movies with special characters in names
        List<Movie> results = movieService.searchMovies(":", null, null);
        assertTrue(results.size() > 0); // Should find movies with colons in titles
    }

    @Test
    public void testSearchMoviesWithNumbers() {
        // Test searching for movies with numbers
        List<Movie> results = movieService.searchMovies("1994", null, null);
        assertTrue(results.size() > 0); // Should find movies from 1994 (in description)
    }

    @Test
    public void testSearchMoviesCaseSensitivityComprehensive() {
        // Test various case combinations
        List<Movie> lowerCase = movieService.searchMovies("prison", null, null);
        List<Movie> upperCase = movieService.searchMovies("PRISON", null, null);
        List<Movie> mixedCase = movieService.searchMovies("PrIsOn", null, null);
        
        assertEquals(lowerCase.size(), upperCase.size());
        assertEquals(upperCase.size(), mixedCase.size());
        assertEquals(1, lowerCase.size());
    }

    @Test
    public void testSearchMoviesGenreSlash() {
        // Test searching for compound genres with slashes
        List<Movie> results = movieService.searchMovies(null, null, "Crime/Drama");
        assertTrue(results.size() > 0);
        
        // Also test partial match on compound genre
        List<Movie> partialResults = movieService.searchMovies(null, null, "Crime");
        assertTrue(partialResults.size() >= results.size());
    }

    // ===== PERFORMANCE AND BOUNDARY TESTS =====

    @Test
    public void testSearchMoviesPerformance() {
        // Simple performance test - should complete quickly
        long startTime = System.currentTimeMillis();
        
        for (int i = 0; i < 100; i++) {
            movieService.searchMovies("The", null, null);
        }
        
        long endTime = System.currentTimeMillis();
        long duration = endTime - startTime;
        
        // Should complete 100 searches in under 1 second
        assertTrue(duration < 1000, "Search performance test failed: " + duration + "ms");
    }

    @Test
    public void testSearchMoviesVeryLongString() {
        // Test with very long search string
        String longString = "a".repeat(1000);
        List<Movie> results = movieService.searchMovies(longString, null, null);
        assertEquals(0, results.size()); // Should handle gracefully
    }

    @Test
    public void testSearchMoviesUnicodeCharacters() {
        // Test with unicode characters
        List<Movie> results = movieService.searchMovies("🎬", null, null);
        assertEquals(0, results.size()); // Should handle gracefully
    }
}