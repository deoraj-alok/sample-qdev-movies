package com.amazonaws.samples.qdevmovies.movies;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Ahoy! Integration tests for the complete movie search treasure hunting functionality.
 * These tests verify the entire application stack works together like a well-oiled ship!
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class MovieSearchIntegrationTest {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private MovieService movieService;

    @Autowired
    private MoviesController moviesController;

    // ===== INTEGRATION TESTS =====

    @Test
    public void testMovieServiceLoadsCorrectly() {
        assertNotNull(movieService);
        assertEquals(12, movieService.getAllMovies().size());
    }

    @Test
    public void testMoviesControllerLoadsCorrectly() {
        assertNotNull(moviesController);
    }

    @Test
    public void testSearchEndpointExists() {
        String url = "http://localhost:" + port + "/movies/search";
        ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);
        
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().contains("Search for Cinematic Treasure"));
    }

    @Test
    public void testSearchByNameIntegration() {
        String url = "http://localhost:" + port + "/movies/search?name=Prison";
        ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);
        
        assertEquals(HttpStatus.OK, response.getStatusCode());
        String body = response.getBody();
        assertNotNull(body);
        assertTrue(body.contains("The Prison Escape"));
        assertTrue(body.contains("Shiver me timbers")); // Success message
    }

    @Test
    public void testSearchByIdIntegration() {
        String url = "http://localhost:" + port + "/movies/search?id=1";
        ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);
        
        assertEquals(HttpStatus.OK, response.getStatusCode());
        String body = response.getBody();
        assertNotNull(body);
        assertTrue(body.contains("The Prison Escape"));
        assertTrue(body.contains("Found 1 piece")); // Success message for single result
    }

    @Test
    public void testSearchByGenreIntegration() {
        String url = "http://localhost:" + port + "/movies/search?genre=Action";
        ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);
        
        assertEquals(HttpStatus.OK, response.getStatusCode());
        String body = response.getBody();
        assertNotNull(body);
        // Should find movies with Action genre
        assertTrue(body.contains("The Masked Hero") || body.contains("Dream Heist") || body.contains("The Virtual World"));
        assertTrue(body.contains("pieces of cinematic treasure")); // Success message for multiple results
    }

    @Test
    public void testSearchNoResultsIntegration() {
        String url = "http://localhost:" + port + "/movies/search?name=NonExistentMovie";
        ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);
        
        assertEquals(HttpStatus.OK, response.getStatusCode());
        String body = response.getBody();
        assertNotNull(body);
        assertTrue(body.contains("Blimey! No treasure found")); // No results message
        assertTrue(body.contains("empty-treasure-chest")); // CSS class for empty results
    }

    @Test
    public void testSearchFormDisplaysCorrectly() {
        String url = "http://localhost:" + port + "/movies/search";
        ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);
        
        assertEquals(HttpStatus.OK, response.getStatusCode());
        String body = response.getBody();
        assertNotNull(body);
        
        // Check that search form elements are present
        assertTrue(body.contains("search-treasure-chest"));
        assertTrue(body.contains("name=\"name\""));
        assertTrue(body.contains("name=\"id\""));
        assertTrue(body.contains("name=\"genre\""));
        assertTrue(body.contains("Hunt for Treasure!"));
        assertTrue(body.contains("Show All Movies"));
    }

    @Test
    public void testOriginalMoviesEndpointStillWorks() {
        String url = "http://localhost:" + port + "/movies";
        ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);
        
        assertEquals(HttpStatus.OK, response.getStatusCode());
        String body = response.getBody();
        assertNotNull(body);
        assertTrue(body.contains("Free Movies This Month"));
        assertTrue(body.contains("The Prison Escape")); // Should show all movies
    }

    @Test
    public void testMovieDetailsEndpointStillWorks() {
        String url = "http://localhost:" + port + "/movies/1/details";
        ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);
        
        assertEquals(HttpStatus.OK, response.getStatusCode());
        String body = response.getBody();
        assertNotNull(body);
        assertTrue(body.contains("The Prison Escape"));
    }

    // ===== REAL DATA VALIDATION TESTS =====

    @Test
    public void testSearchFindsKnownMovies() {
        // Test that we can find specific movies from the actual JSON data
        
        // Search for "The Prison Escape" (ID: 1)
        assertEquals(1, movieService.searchMovies("Prison", null, null).size());
        
        // Search for "The Family Boss" (ID: 2)
        assertEquals(1, movieService.searchMovies("Family", null, null).size());
        
        // Search for "The Masked Hero" (ID: 3)
        assertEquals(1, movieService.searchMovies("Masked", null, null).size());
        
        // Search for Drama movies (should find multiple)
        assertTrue(movieService.searchMovies(null, null, "Drama").size() > 1);
        
        // Search for Action movies (should find multiple)
        assertTrue(movieService.searchMovies(null, null, "Action").size() > 1);
    }

    @Test
    public void testSearchWithRealGenres() {
        // Test with actual genres from the JSON data
        assertTrue(movieService.searchMovies(null, null, "Crime/Drama").size() > 0);
        assertTrue(movieService.searchMovies(null, null, "Action/Crime").size() > 0);
        assertTrue(movieService.searchMovies(null, null, "Action/Sci-Fi").size() > 0);
        assertTrue(movieService.searchMovies(null, null, "Drama/Romance").size() > 0);
        assertTrue(movieService.searchMovies(null, null, "Adventure/Fantasy").size() > 0);
        assertTrue(movieService.searchMovies(null, null, "Adventure/Sci-Fi").size() > 0);
        assertTrue(movieService.searchMovies(null, null, "Drama/History").size() > 0);
        assertTrue(movieService.searchMovies(null, null, "Drama/Thriller").size() > 0);
    }

    @Test
    public void testSearchByRealMovieIds() {
        // Test searching by actual movie IDs from the JSON
        for (long id = 1L; id <= 12L; id++) {
            assertEquals(1, movieService.searchMovies(null, id, null).size());
        }
        
        // Test invalid ID
        assertEquals(0, movieService.searchMovies(null, 13L, null).size());
    }

    @Test
    public void testComplexSearchScenarios() {
        // Test complex real-world search scenarios
        
        // Find action movies with "Hero" in the name
        var actionHeroes = movieService.searchMovies("Hero", null, "Action");
        assertTrue(actionHeroes.size() > 0);
        
        // Find 1994 movies (should find multiple based on year in description)
        var movies1994 = movieService.searchMovies("1994", null, null);
        assertTrue(movies1994.size() > 0);
        
        // Find Chris Moviemaker movies (director search via name)
        var chrisMovies = movieService.searchMovies("Chris", null, null);
        assertTrue(chrisMovies.size() > 0);
    }
}