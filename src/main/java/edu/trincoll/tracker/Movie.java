package edu.trincoll.tracker;

import java.time.LocalDateTime;
import java.util.Objects;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * Base entity class for your domain object.
 * <p>
 * TODO: Rename this class to match your chosen domain
 * Examples: Bookmark, Quote, Habit, Recipe, Movie
 * <p>
 * TODO: Add at least 3 meaningful fields beyond 'id'
 * Examples for different domains:
 * - Bookmark: url, title, category, tags
 * - Quote: text, author, source, category
 * - Habit: name, frequency, streak, lastCompleted
 * - Recipe: name, ingredients, instructions, prepTime
 * - Movie: title, director, year, rating, watched
 */
public class Movie {

    private Long id;

    @NotBlank(message = "Title is required")
    @Size(min = 1, max = 100, message = "Title must be between 1 and 100 characters")
    private String title;

    @Size(max = 100, message = "Director must be at most 100 characters")
    private String director;

    @Min(value = 1888, message = "Year must be >= 1888")
    @Max(value = 2100, message = "Year must be reasonable")
    private Integer year;

    @Min(value = 0, message = "Rating must be >= 0")
    @Max(value = 10, message = "Rating must be <= 10")
    private Integer rating; // 0–10 (nullable)

    private boolean watched;

    private LocalDateTime createdAt;

    public Movie() {
        this.createdAt = LocalDateTime.now();
        this.watched = false;
    }
    
    // TODO: Generate getters and setters for all your fields
    // Hint: IntelliJ can do this for you (Alt+Insert or Cmd+N)
    
   // Getters & setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDirector() { return director; }
    public void setDirector(String director) { this.director = director; }

    public Integer getYear() { return year; }
    public void setYear(Integer year) { this.year = year; }

    public Integer getRating() { return rating; }
    public void setRating(Integer rating) { this.rating = rating; }

    public boolean isWatched() { return watched; }
    public void setWatched(boolean watched) { this.watched = watched; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    
    // TODO: Consider overriding equals() and hashCode() based on your domain
    // This is important for testing and collections

     @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Movie movie = (Movie) o;
        return watched == movie.watched &&
                Objects.equals(id, movie.id) &&
                Objects.equals(title, movie.title) &&
                Objects.equals(director, movie.director) &&
                Objects.equals(year, movie.year) &&
                Objects.equals(rating, movie.rating);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, title, director, year, rating, watched);
    }

    @Override
    public String toString() {
        return "Movie{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", director='" + director + '\'' +
                ", year=" + year +
                ", rating=" + rating +
                ", watched=" + watched +
                ", createdAt=" + createdAt +
                '}';
    }
}