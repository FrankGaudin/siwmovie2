package it.uniroma3.siw.siwmovie2.controller;

import it.uniroma3.siw.siwmovie2.controller.validator.MovieValidator;
import it.uniroma3.siw.siwmovie2.model.*;
import it.uniroma3.siw.siwmovie2.service.ArtistService;
import it.uniroma3.siw.siwmovie2.service.CredentialsService;
import it.uniroma3.siw.siwmovie2.service.MovieService;
import jakarta.annotation.Nullable;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;

@Controller
public class MovieController {
    @Autowired
    private MovieService movieService;

    @Autowired
    private ArtistService artistService;

    @Autowired
    private MovieValidator movieValidator;


    @Autowired
    private CredentialsService credentialsService;

    @GetMapping(value="/admin/formNewMovie")
    public String formNewMovie(Model model) {
        model.addAttribute("movie", new Movie());
        return "admin/formNewMovie.html";
    }

    @GetMapping(value="/admin/formUpdateMovie/{id}")
    public String formUpdateMovie(@PathVariable("id") Long id, Model model) {
        model.addAttribute("movie", movieService.getMovieById(id));
        return "admin/formUpdateMovie.html";
    }

    @GetMapping(value="/admin/indexMovie")
    public String indexMovie() {
        return "admin/indexMovie.html";
    }

    @GetMapping(value="/admin/adminMovie/{id}")
    public String adminMovie(@PathVariable("id") Long id, Model model) {
        model.addAttribute("movie", movieService.getMovieById(id));
        return "admin/movie.html";
    }

    @GetMapping(value="/admin/manageMovies")
    public String manageMovies(Model model) {
        model.addAttribute("movies", this.movieService.getMovies());
        return "admin/manageMovies.html";
    }

    @GetMapping(value="/admin/setDirectorToMovie/{directorId}/{movieId}")
    public String setDirectorToMovie(@PathVariable("directorId") Long directorId, @PathVariable("movieId") Long movieId, Model model) {

        Movie movie = this.movieService.setDirectorToMovie(directorId, movieId);

        model.addAttribute("movie", movie);
        return "admin/formUpdateMovie.html";
    }


    @GetMapping(value="/admin/addDirector/{id}")
    public String addDirector(@PathVariable("id") Long id, Model model) {
        model.addAttribute("artists", artistService.getAllArtists());
        model.addAttribute("movie", movieService.getMovieById(id));
        return "admin/directorsToAdd.html";
    }

    @PostMapping("/admin/movie")
    public String newMovie(@Valid @ModelAttribute("movie") Movie movie, BindingResult bindingResult, Model model,
                           @RequestParam("fileImage") @NotNull MultipartFile image) throws IOException {

        this.movieValidator.validate(movie, bindingResult);
        if (!bindingResult.hasErrors()) {
            this.movieService.createNewMovie(movie, image);
            model.addAttribute("movie", movie);
            return "movie.html";
        } else {
            return "admin/formNewMovie.html";
        }
    }

    @GetMapping("admin/updateMovie/{id}")
    public String getMovieToUpdate(@PathVariable("id") Long id, Model model){
        model.addAttribute("movie", movieService.getMovieById(id));
        return "admin/updateMovie";
    }
    @PostMapping("/admin/updatedMovie/{id}")
    public String updateMovie(@PathVariable("id") Long id,
                              @RequestParam("title") String title,
                              @RequestParam("year") Integer year,
                              @RequestParam(value = "fileImage", required = false) MultipartFile imageFile,
                              Model model) throws IOException {
        Movie movie = movieService.getMovieById(id);
        movieService.updateMovie(id, title, year, imageFile);
        model.addAttribute("movie", movie);
        return "admin/movie.html";
    }

    @GetMapping("/movie/{id}")
    public String getMovie(@PathVariable("id") Long id, Model model) {
        Movie movie = this.movieService.getMovieById(id);
        byte[] photo = movie.getImage();
        if(photo != null) {
            String image = java.util.Base64.getEncoder().encodeToString(photo);
            model.addAttribute("image", image);
        }
        model.addAttribute("movie", this.movieService.getMovieById(id));
        return "movie.html";
    }

    @GetMapping("/movie")
    public String getMovies(Model model) {
        List<Movie> movies = new ArrayList<>();
        for(Movie m: this.movieService.getAllMoviesByAsc()){
            byte[] photo = m.getImage();
            if(photo != null) {
                String image = java.util.Base64.getEncoder().encodeToString(photo);
                model.addAttribute("image", image);
            }
            movies.add(m);
        }
        model.addAttribute("movies", movies);
        return "movies.html";
    }


    @PostMapping("/searchMovies")
    public String searchMovies(Model model, @RequestParam String title) {
        model.addAttribute("movies", this.movieService.getMoviesByTitle(title));
        return "foundMovies.html";
    }

    @GetMapping("/admin/updateActors/{id}")
    public String updateActors(@PathVariable("id") Long id, Model model) {

        List<Artist> actorsToAdd = this.movieService.findActorsNotInMovie(id);
        model.addAttribute("actorsToAdd", actorsToAdd);
        model.addAttribute("movie", this.movieService.getMovieById(id));

        return "admin/actorsToAdd.html";
    }

    @GetMapping(value="/admin/addActorToMovie/{actorId}/{movieId}")
    public String addActorToMovie(@PathVariable("actorId") Long actorId, @PathVariable("movieId") Long movieId, Model model) {
        Movie movie = this.movieService.addActorToMovie(movieId, actorId);

        List<Artist> actorsToAdd = this.movieService.findActorsNotInMovie(movieId);

        model.addAttribute("movie", movie);
        model.addAttribute("actorsToAdd", actorsToAdd);

        return "admin/actorsToAdd.html";
    }

    @GetMapping(value="/admin/removeActorFromMovie/{actorId}/{movieId}")
    public String removeActorFromMovie(@PathVariable("actorId") Long actorId, @PathVariable("movieId") Long movieId, Model model) {
        Movie movie = this.movieService.removeActorFromMovie(movieId, actorId);

        List<Artist> actorsToAdd = this.movieService.findActorsNotInMovie(movieId);

        model.addAttribute("movie", movie);
        model.addAttribute("actorsToAdd", actorsToAdd);

        return "admin/actorsToAdd.html";
    }


    @GetMapping("/addReview/{id}")
    public String addReview(@PathVariable("id") Long id, Model model) {
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Credentials credentials = credentialsService.getCredentials(userDetails.getUsername());

        model.addAttribute("user", credentials.getUser());
        model.addAttribute("movie", this.movieService.getMovieById(id));
        model.addAttribute("review", new Review());
        return "formNewReview.html";
    }

    @GetMapping("/admin/manageReviews/{id}")
    public String deleteReview(@PathVariable("id") Long id, Model model) {
        Movie movie = this.movieService.getMovieById(id);
        model.addAttribute("movie", movie);
        return "admin/manageReviews.html";
    }


    @GetMapping("/movie/reviews/{reviewId}")
    public String getReviews(@PathVariable("id")Long id, Model model) {
        Movie movie = this.movieService.getMovieById(id);
        List<Review> reviews = movie.getReviews();
        model.addAttribute("reviews", reviews);
        model.addAttribute("movie", movie);
        return "reviews.html";
    }

    @GetMapping(value="/admin/deleteMovie/{movieId}")
    public String deleteMovie(@PathVariable("movieId")Long movieId, Model model) {
        this.movieService.deleteMovie(movieId);
        model.addAttribute("movies", this.movieService.getAllMoviesByAsc());
        return "admin/manageMovies.html";
    }
}