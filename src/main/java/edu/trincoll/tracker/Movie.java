package edu.trincoll.tracker;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.time.LocalDateTime;
import java.util.Objects;

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
    
    // TODO: Replace these example fields with your domain-specific fields
    @NotBlank(message = "Name is required")
    @Size(min = 1, max = 100, message = "Name must be between 1 and 100 characters")
    private String name;
    
    private String description;
    
    private LocalDateTime createdAt;
    
    private boolean completed;
    
    // Constructor
    public Movie() {
        this.createdAt = LocalDateTime.now();
        this.completed = false;
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
        if (o == null || getClass() != o.getClass()) return false;
        Movie item = (Movie) o;
        return completed == item.completed &&
               Objects.equals(id, item.id) &&
               Objects.equals(name, item.name) &&
               Objects.equals(description, item.description);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, description, completed);
    }

    @Override
    public String toString() {
        return "Item{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", createdAt=" + createdAt +
                ", completed=" + completed +
                '}';
    }
}