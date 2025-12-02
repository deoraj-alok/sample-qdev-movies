# Movie Service - Spring Boot Demo Application 🏴‍☠️

A simple movie catalog web application built with Spring Boot, demonstrating Java application development best practices with a swashbuckling pirate theme!

## Features

- **Movie Catalog**: Browse 12 classic movies with detailed information
- **Movie Details**: View comprehensive information including director, year, genre, duration, and description
- **🏴‍☠️ Treasure Hunt (Search)**: Search for cinematic treasure using movie name, ID, or genre with our pirate-themed search interface
- **Advanced Filtering**: Combine multiple search criteria to find exactly what ye be lookin' for, matey!
- **Customer Reviews**: Each movie includes authentic customer reviews with ratings and avatars
- **Responsive Design**: Mobile-first design that works on all devices
- **Modern UI**: Dark theme with gradient backgrounds, smooth animations, and pirate flair

## Technology Stack

- **Java 8**
- **Spring Boot 2.0.5**
- **Maven** for dependency management
- **Thymeleaf** for server-side templating
- **Log4j 2.20.0**
- **JUnit 5.8.2**

## Quick Start

### Prerequisites

- Java 8 or higher
- Maven 3.6+

### Run the Application

```bash
git clone https://github.com/<youruser>/sample-qdev-movies.git
cd sample-qdev-movies
mvn spring-boot:run
```

The application will start on `http://localhost:8080`

### Access the Application

- **Movie List**: http://localhost:8080/movies
- **Movie Search**: http://localhost:8080/movies/search
- **Movie Details**: http://localhost:8080/movies/{id}/details (where {id} is 1-12)

## Building for Production

```bash
mvn clean package
java -jar target/sample-qdev-movies-0.1.0.jar
```

## Project Structure

```
src/
├── main/
│   ├── java/
│   │   └── com/amazonaws/samples/qdevmovies/
│   │       ├── MoviesApplication.java    # Main Spring Boot application
│   │       ├── MoviesController.java     # REST controller for movie endpoints
│   │       ├── MovieService.java         # Business logic with search functionality
│   │       ├── Movie.java                # Movie data model
│   │       ├── Review.java               # Review data model
│   │       └── utils/
│   │           ├── MovieIconUtils.java   # Movie icon utilities
│   │           └── MovieUtils.java       # Movie validation utilities
│   └── resources/
│       ├── templates/
│       │   ├── movies.html               # Enhanced movie list with search form
│       │   └── movie-details.html        # Movie details page
│       ├── static/css/
│       │   ├── movies.css                # Enhanced styles with pirate theme
│       │   └── movie-details.css         # Movie details styling
│       ├── application.yml               # Application configuration
│       ├── movies.json                   # Movie data (12 movies)
│       ├── mock-reviews.json             # Mock review data
│       └── log4j2.xml                    # Logging configuration
└── test/                                 # Comprehensive unit tests
```

## API Endpoints

### Get All Movies
```
GET /movies
```
Returns an HTML page displaying all movies with ratings and basic information, plus a search form for treasure hunting!

### 🏴‍☠️ Search for Cinematic Treasure
```
GET /movies/search
```
Ahoy! This be the main treasure hunting endpoint, matey! Search for movies using various criteria.

**Query Parameters:**
- `name` (optional): Movie name to search for (case-insensitive partial match)
- `id` (optional): Movie ID to search for (exact match, 1-12)
- `genre` (optional): Movie genre to search for (case-insensitive partial match)

**Examples:**
```bash
# Search by movie name
http://localhost:8080/movies/search?name=prison

# Search by movie ID
http://localhost:8080/movies/search?id=1

# Search by genre
http://localhost:8080/movies/search?genre=action

# Combine multiple criteria (all must match)
http://localhost:8080/movies/search?name=hero&genre=action

# Show search form (no parameters)
http://localhost:8080/movies/search
```

**Search Features:**
- **Case-insensitive matching**: Search for "ACTION" or "action" - both work!
- **Partial matching**: Search "Hero" to find "The Masked Hero"
- **Multiple criteria**: Combine name, ID, and genre for precise results
- **Pirate-themed messages**: Get swashbuckling feedback on your search results
- **Empty result handling**: Helpful suggestions when no treasure is found

### Get Movie Details
```
GET /movies/{id}/details
```
Returns an HTML page with detailed movie information and customer reviews.

**Parameters:**
- `id` (path parameter): Movie ID (1-12)

**Example:**
```
http://localhost:8080/movies/1/details
```

## Search Examples & Use Cases

### Basic Searches
```bash
# Find all drama movies
/movies/search?genre=drama

# Find movies with "space" in the title
/movies/search?name=space

# Get a specific movie by ID
/movies/search?id=5
```

### Advanced Searches
```bash
# Find action movies with "hero" in the title
/movies/search?name=hero&genre=action

# Search for crime dramas
/movies/search?genre=crime

# Find movies from a specific year (search in description)
/movies/search?name=1994
```

### Edge Cases Handled
- **Empty searches**: Returns all movies with helpful message
- **No results**: Pirate-themed "empty treasure chest" message with suggestions
- **Invalid IDs**: Graceful handling with appropriate feedback
- **Whitespace-only input**: Treated as empty search
- **Case variations**: All searches are case-insensitive

## Pirate Theme Features 🏴‍☠️

The application includes a fun pirate theme throughout:

- **Search Interface**: "Search for Cinematic Treasure" with pirate emojis and language
- **Success Messages**: "Shiver me timbers! Found X pieces of treasure!"
- **Error Messages**: "Blimey! No treasure found matching yer criteria, ye scurvy dog!"
- **Empty Results**: Detailed "empty treasure chest" page with helpful suggestions
- **Logging**: Pirate-themed log messages for debugging ("Arrr! Starting treasure hunt...")
- **UI Elements**: Treasure chest styling, pirate color schemes, and nautical buttons

## Troubleshooting

### Port 8080 already in use

Run on a different port:
```bash
mvn spring-boot:run -Dspring-boot.run.arguments=--server.port=8081
```

### Build failures

Clean and rebuild:
```bash
mvn clean compile
```

### Search not working

1. Check that you're using the correct endpoint: `/movies/search`
2. Verify parameter names: `name`, `id`, `genre`
3. Check server logs for pirate-themed debug messages
4. Try searching without parameters to see the search form

### No search results

1. Try partial matches (e.g., "Action" instead of "Action/Crime")
2. Check spelling and case (searches are case-insensitive)
3. Use the "Show All Movies" button to browse available titles
4. Check the movie data in `src/main/resources/movies.json`

## Testing

Run the comprehensive test suite:
```bash
mvn test
```

The test suite includes:
- **Controller tests**: All search endpoints and edge cases
- **Service tests**: Search functionality and parameter validation
- **Integration tests**: End-to-end search workflows
- **Edge case tests**: Empty results, invalid parameters, whitespace handling

## Contributing

This project is designed as a demonstration application. Feel free to:
- Add more movies to the catalog (`movies.json`)
- Enhance the pirate theme with more nautical elements
- Improve search functionality (fuzzy matching, advanced filters)
- Add new features like movie ratings or user favorites
- Enhance the responsive design for better mobile experience
- Extend the test coverage

## License

This sample code is licensed under the MIT-0 License. See the LICENSE file.

---

*Arrr! May fair winds fill yer sails as ye navigate this cinematic treasure trove, me hearty! 🏴‍☠️⚓*
