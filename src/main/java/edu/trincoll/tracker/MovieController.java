package edu.trincoll.tracker;

import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

/**
 * AI Collaboration Report:
 * - AI Tool Used: Chatgpt 5
 * - Most Helpful Prompt: Explain pull push commit
 * - AI Mistake We Fixed: None
 * - Time Saved: A lot, hours at least
 * - Team Members: Varvara Esina, Taha Moula, Daniel Simon
 */
@RestController
@RequestMapping(value = "/api/movies", produces = MediaType.APPLICATION_JSON_VALUE)
public class MovieController {

    // In-memory store (placeholder for a real database)
    private static final Map<Long, Movie> STORE = new ConcurrentHashMap<>();
    private static final AtomicLong ID_SEQ = new AtomicLong(1);

    /**
     * GET /api/movies
     * Returns all movies
     */
    @GetMapping
    public ResponseEntity<List<Movie>> getAll() {
        List<Movie> movies = STORE.values()
                .stream()
                .sorted(Comparator.comparing(Movie::getId))
                .collect(Collectors.toList());
        return ResponseEntity.ok(movies);
    }

    /**
     * GET /api/movies/{id}
     * Returns a movie by ID (404 if not found)
     */
    @GetMapping("/{id}")
    public ResponseEntity<Movie> getById(@PathVariable Long id) {
        Movie movie = STORE.get(id);
        if (movie == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        return ResponseEntity.ok(movie);
    }

    /**
     * POST /api/movies
     * Creates a new movie
     * - Validate required fields (title)
     * - Reject duplicates by title (409 Conflict)
     * - Ignore client-provided id/createdAt
     */
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Movie> create(@RequestBody Movie body) {
        // Validate title
        if (body.getTitle() == null || body.getTitle().isBlank()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }

        // Enforce uniqueness by title (case-sensitive to match starter style; change to equalsIgnoreCase if desired)
        boolean duplicate = STORE.values().stream()
                .anyMatch(existing -> Objects.equals(existing.getTitle(), body.getTitle()));
        if (duplicate) {
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }

        // Assign new ID and build the server object
        long id = ID_SEQ.getAndIncrement();
        Movie toSave = new Movie();
        toSave.setId(id);
        toSave.setTitle(body.getTitle());
        toSave.setDirector(body.getDirector());
        toSave.setYear(body.getYear());
        toSave.setRating(body.getRating());
        toSave.setWatched(body.isWatched());
        // Keep Movie()'s server-side createdAt

        STORE.put(id, toSave);
        return ResponseEntity.status(HttpStatus.CREATED).body(toSave);
    }

    /**
     * PUT /api/movies/{id}
     * Updates an existing movie
     * - Validate required fields (title)
     * - Return 404 if movie doesn't exist
     * - Reject duplicates by title (409 Conflict) if changing to an existing title
     * - Preserve original createdAt
     */
    @PutMapping(value = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Movie> update(@PathVariable Long id, @RequestBody Movie update) {
        Movie existing = STORE.get(id);
        if (existing == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        if (update.getTitle() == null || update.getTitle().isBlank()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }

        boolean duplicateTitle = STORE.values().stream()
                .anyMatch(other -> !Objects.equals(other.getId(), id)
                        && Objects.equals(other.getTitle(), update.getTitle()));
        if (duplicateTitle) {
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }

        existing.setTitle(update.getTitle());
        existing.setDirector(update.getDirector());
        existing.setYear(update.getYear());
        existing.setRating(update.getRating());
        existing.setWatched(update.isWatched());
        // createdAt stays as-is

        return ResponseEntity.ok(existing);
    }

    /**
     * DELETE /api/movies/{id}
     * Deletes a movie
     * - Return 204 No Content on success
     * - Return 404 if not found
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        Movie removed = STORE.remove(id);
        if (removed == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        return ResponseEntity.noContent().build();
    }

    /**
     * GET /api/movies/search?title=value
     * Searches movies by title (case-insensitive contains)
     * BONUS endpoint
     */
    @GetMapping("/search")
    public ResponseEntity<List<Movie>> searchByTitle(@RequestParam("title") String title) {
        if (title == null) {
            return ResponseEntity.badRequest().build();
        }
        String query = title.toLowerCase(Locale.ROOT);
        List<Movie> results = STORE.values().stream()
                .filter(m -> m.getTitle() != null && m.getTitle().toLowerCase(Locale.ROOT).contains(query))
                .sorted(Comparator.comparing(Movie::getId))
                .collect(Collectors.toList());
        return ResponseEntity.ok(results);
    }

    // Test helper method - only for testing purposes
    static void clearStore() {
        STORE.clear();
        ID_SEQ.set(1);
    }
}