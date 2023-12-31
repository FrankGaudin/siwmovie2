package it.uniroma3.siw.siwmovie2.controller;

import it.uniroma3.siw.siwmovie2.controller.validator.ReviewValidator;
import it.uniroma3.siw.siwmovie2.model.Movie;
import it.uniroma3.siw.siwmovie2.model.Review;
import it.uniroma3.siw.siwmovie2.repository.ReviewRepository;
import it.uniroma3.siw.siwmovie2.service.MovieService;
import it.uniroma3.siw.siwmovie2.service.ReviewService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.List;

@Controller
public class ReviewController {

    @Autowired
    private ReviewService reviewService;

    @Autowired
    private MovieService movieService;

    @Autowired
    private ReviewValidator reviewValidator;

    @PostMapping("/review/{movieId}")
    public String newReview(@Valid @ModelAttribute Review review, BindingResult bindingResult,
                            @PathVariable("movieId") Long movieId, Model model) {
        this.reviewValidator.validate(review, bindingResult);
        Review newReview = this.reviewService.newReview(review, movieId);
        if (!bindingResult.hasErrors() && newReview != null) {
            Movie movie = this.movieService.addReviewToMovie(movieId, newReview.getId());
            model.addAttribute(newReview);
            model.addAttribute(movie);
            return "movie.html";
        } else {
            model.addAttribute("movie", this.movieService.getMovieById(movieId));
            return "movie.html";
        }
    }

    @GetMapping("/admin/deleteReview/{reviewId}/{movieId}")
    public String deleteReview(@PathVariable("reviewId")Long reviewId, @PathVariable("movieId") Long movieId,
                               Model model) {
        this.reviewService.deleteReview(reviewId);
        Movie movie = this.movieService.getMovieById(movieId);
        model.addAttribute("movie", movie );
        model.addAttribute("reviews", movie.getReviews());
        return "admin/manageReviews.html";
    }

    @GetMapping("/reviews/reviewsOrdered/{id}")
    public String orderReviews(@PathVariable("id") Long id, Model model) {
        List<Review> reviews = this.reviewService.getReviewsByRateAsc();
        Movie movie = this.movieService.getMovieById(id);
        model.addAttribute("movie", movie);
        model.addAttribute("reviews", reviews);
        return "reviews.html";
    }
}
