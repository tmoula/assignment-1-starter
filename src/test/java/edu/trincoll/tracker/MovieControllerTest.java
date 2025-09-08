package edu.trincoll.tracker;

import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.hamcrest.Matchers.hasSize;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Test suite for the Movie API.
 * ALL TESTS MUST PASS for full credit.
 */
@SpringBootTest
@AutoConfigureMockMvc
@DisplayName("Movie Controller Tests")
class MovieControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() throws Exception {
        MovieController.clearStore();
    }

    @Nested
    @DisplayName("GET /api/movies")
    class GetAllMovies {

        @Test
        @DisplayName("should return empty list when no movies exist")
        void shouldReturnEmptyList() throws Exception {
            mockMvc.perform(get("/api/movies"))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$", hasSize(greaterThanOrEqualTo(0))));
        }

        @Test
        @DisplayName("should return all movies when movies exist")
        void shouldReturnAllMovies() throws Exception {
            Movie testMovie = new Movie();
            testMovie.setTitle("Test Movie");
            testMovie.setDirector("Test Director");

            mockMvc.perform(post("/api/movies")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(testMovie)))
                    .andExpect(status().isCreated());

            mockMvc.perform(get("/api/movies"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$", hasSize(greaterThanOrEqualTo(1))))
                    .andExpect(jsonPath("$[?(@.title == 'Test Movie')]").exists());
        }
    }

    @Nested
    @DisplayName("GET /api/movies/{id}")
    class GetMovieById {

        @Test
        @DisplayName("should return movie when it exists")
        void shouldReturnMovieWhenExists() throws Exception {
            Movie testMovie = new Movie();
            testMovie.setTitle("Specific Movie");
            testMovie.setDirector("Specific Director");

            String response = mockMvc.perform(post("/api/movies")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(testMovie)))
                    .andExpect(status().isCreated())
                    .andReturn()
                    .getResponse()
                    .getContentAsString();

            Movie createdMovie = objectMapper.readValue(response, Movie.class);

            mockMvc.perform(get("/api/movies/{id}", createdMovie.getId()))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.title").value("Specific Movie"))
                    .andExpect(jsonPath("$.director").value("Specific Director"));
        }

        @Test
        @DisplayName("should return 404 when movie doesn't exist")
        void shouldReturn404WhenMovieDoesNotExist() throws Exception {
            mockMvc.perform(get("/api/movies/{id}", 999999))
                    .andExpect(status().isNotFound());
        }
    }

    @Nested
    @DisplayName("POST /api/movies")
    class CreateMovie {

        @Test
        @DisplayName("should create new movie with valid data")
        void shouldCreateNewMovie() throws Exception {
            Movie newMovie = new Movie();
            newMovie.setTitle("New Movie");
            newMovie.setDirector("New Director");

            mockMvc.perform(post("/api/movies")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(newMovie)))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.id").exists())
                    .andExpect(jsonPath("$.title").value("New Movie"))
                    .andExpect(jsonPath("$.director").value("New Director"));
        }

        @Test
        @DisplayName("should return 400 when title is missing")
        void shouldReturn400WhenTitleMissing() throws Exception {
            String invalidJson = """
                    {"director":"No title provided"}""";

            mockMvc.perform(post("/api/movies")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(invalidJson))
                    .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("should return 400 when title is blank")
        void shouldReturn400WhenTitleBlank() throws Exception {
            Movie invalidMovie = new Movie();
            invalidMovie.setTitle(""); 
            invalidMovie.setDirector("Valid Director");

            mockMvc.perform(post("/api/movies")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(invalidMovie)))
                    .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("should not allow duplicate movies with same title")
        void shouldNotAllowDuplicates() throws Exception {
            Movie firstMovie = new Movie();
            firstMovie.setTitle("Unique Title");
            firstMovie.setDirector("Director A");

            mockMvc.perform(post("/api/movies")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(firstMovie)))
                    .andExpect(status().isCreated());

            Movie duplicateMovie = new Movie();
            duplicateMovie.setTitle("Unique Title");
            duplicateMovie.setDirector("Director B");

            mockMvc.perform(post("/api/movies")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(duplicateMovie)))
                    .andExpect(status().isConflict());
        }
    }

    @Nested
    @DisplayName("PUT /api/movies/{id}")
    class UpdateMovie {

        @Test
        @DisplayName("should update existing movie")
        void shouldUpdateExistingMovie() throws Exception {
            Movie initialMovie = new Movie();
            initialMovie.setTitle("Original Title");
            initialMovie.setDirector("Original Director");

            String response = mockMvc.perform(post("/api/movies")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(initialMovie)))
                    .andExpect(status().isCreated())
                    .andReturn()
                    .getResponse()
                    .getContentAsString();

            Movie createdMovie = objectMapper.readValue(response, Movie.class);

            Movie updatedMovie = new Movie();
            updatedMovie.setTitle("Updated Title");
            updatedMovie.setDirector("Updated Director");
            updatedMovie.setWatched(true);

            mockMvc.perform(put("/api/movies/{id}", createdMovie.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(updatedMovie)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.title").value("Updated Title"))
                    .andExpect(jsonPath("$.director").value("Updated Director"))
                    .andExpect(jsonPath("$.watched").value(true));
        }

        @Test
        @DisplayName("should return 404 when updating non-existent movie")
        void shouldReturn404WhenUpdatingNonExistent() throws Exception {
            Movie updateMovie = new Movie();
            updateMovie.setTitle("Update Title");
            updateMovie.setDirector("Update Director");

            mockMvc.perform(put("/api/movies/{id}", 999999)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(updateMovie)))
                    .andExpect(status().isNotFound());
        }

        @Test
        @DisplayName("should validate required fields on update")
        void shouldValidateRequiredFieldsOnUpdate() throws Exception {
            Movie initialMovie = new Movie();
            initialMovie.setTitle("Original Title");
            initialMovie.setDirector("Original Director");

            String response = mockMvc.perform(post("/api/movies")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(initialMovie)))
                    .andExpect(status().isCreated())
                    .andReturn()
                    .getResponse()
                    .getContentAsString();

            Movie createdMovie = objectMapper.readValue(response, Movie.class);

            String invalidUpdate = "{\"title\":\"\",\"director\":\"Valid Director\"}";

            mockMvc.perform(put("/api/movies/{id}", createdMovie.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(invalidUpdate))
                    .andExpect(status().isBadRequest());
        }
    }

    @Nested
    @DisplayName("DELETE /api/movies/{id}")
    class DeleteMovie {

        @Test
        @DisplayName("should delete existing movie")
        void shouldDeleteExistingMovie() throws Exception {
            Movie movieToDelete = new Movie();
            movieToDelete.setTitle("Delete Me");
            movieToDelete.setDirector("To Be Deleted");

            String response = mockMvc.perform(post("/api/movies")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(movieToDelete)))
                    .andExpect(status().isCreated())
                    .andReturn()
                    .getResponse()
                    .getContentAsString();

            Movie createdMovie = objectMapper.readValue(response, Movie.class);

            mockMvc.perform(delete("/api/movies/{id}", createdMovie.getId()))
                    .andExpect(status().isNoContent());

            mockMvc.perform(get("/api/movies/{id}", createdMovie.getId()))
                    .andExpect(status().isNotFound());
        }

        @Test
        @DisplayName("should return 404 when deleting non-existent movie")
        void shouldReturn404WhenDeletingNonExistent() throws Exception {
            mockMvc.perform(delete("/api/movies/{id}", 999999))
                    .andExpect(status().isNotFound());
        }
    }

    @Nested
    @DisplayName("Bonus: Search Functionality")
    class SearchMovies {

        @Test
        @DisplayName("BONUS: should search movies by title")
        void shouldSearchMoviesByTitle() throws Exception {
            Movie movie1 = new Movie();
            movie1.setTitle("Apple");
            movie1.setDirector("Director 1");

            Movie movie2 = new Movie();
            movie2.setTitle("Banana");
            movie2.setDirector("Director 2");

            Movie movie3 = new Movie();
            movie3.setTitle("Application");
            movie3.setDirector("Director 3");

            mockMvc.perform(post("/api/movies")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(movie1)))
                    .andExpect(status().isCreated());

            mockMvc.perform(post("/api/movies")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(movie2)))
                    .andExpect(status().isCreated());

            mockMvc.perform(post("/api/movies")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(movie3)))
                    .andExpect(status().isCreated());

            mockMvc.perform(get("/api/movies/search")
                    .param("title", "App"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$", hasSize(2)))
                    .andExpect(jsonPath("$[?(@.title == 'Apple')]").exists())
                    .andExpect(jsonPath("$[?(@.title == 'Application')]").exists());
        }
    }
}
