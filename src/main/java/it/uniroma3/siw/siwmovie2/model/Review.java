package it.uniroma3.siw.siwmovie2.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Entity
public class Review {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @NotBlank
    private String title;

    @NotNull
    @Min(1)
    @Max(5)
    private Integer rate;

    @NotBlank
    private String comment;

    @ManyToOne
    //@JoinColumn(name="movie_id")
    private Movie movie;

    @ManyToOne
    //@JoinColumn(name="reviewer_id")
    private User reviewer;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Integer getRate() {
        return rate;
    }

    public void setRate(Integer rate) {
        this.rate = rate;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public Movie getMovie() {
        return movie;
    }

    public void setMovie(Movie movie) {
        this.movie = movie;
    }

    public User getReviewer() {
        return reviewer;
    }

    public String getReviewerName(){
        return reviewer.getName();
    }
    public void setReviewer(User reviewer) {
        this.reviewer = reviewer;
    }
}
