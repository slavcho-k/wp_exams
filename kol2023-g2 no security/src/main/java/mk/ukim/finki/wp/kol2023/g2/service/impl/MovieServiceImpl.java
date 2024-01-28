package mk.ukim.finki.wp.kol2023.g2.service.impl;

import mk.ukim.finki.wp.kol2023.g2.model.Genre;
import mk.ukim.finki.wp.kol2023.g2.model.Movie;
import mk.ukim.finki.wp.kol2023.g2.model.exceptions.InvalidMovieIdException;
import mk.ukim.finki.wp.kol2023.g2.repository.MovieRepository;
import mk.ukim.finki.wp.kol2023.g2.service.DirectorService;
import mk.ukim.finki.wp.kol2023.g2.service.MovieService;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class MovieServiceImpl implements MovieService {
    private final MovieRepository movieRepository;
    private final DirectorService directorService;

    public MovieServiceImpl(MovieRepository movieRepository, DirectorService directorService) {
        this.movieRepository = movieRepository;
        this.directorService = directorService;
    }

    @Override
    public List<Movie> listAllMovies() {
        return movieRepository.findAll();
    }

    @Override
    public Movie findById(Long id) {
        return movieRepository.findById(id).orElseThrow(InvalidMovieIdException::new);
    }

    @Override
    public Movie create(String name, String description, Double rating, Genre genre, Long director) {
        return movieRepository.save(new Movie(
                name, description, rating, genre, directorService.findById(director)
        ));
    }

    @Override
    public Movie update(Long id, String name, String description, Double rating, Genre genre, Long director) {
        Movie movie = movieRepository.findById(id).orElseThrow(InvalidMovieIdException::new);

        movie.setName(name);
        movie.setDescription(description);
        movie.setRating(rating);
        movie.setGenre(genre);
        movie.setDirector(directorService.findById(director));

        return movieRepository.save(movie);
    }

    @Override
    public Movie delete(Long id) {
        Movie movie = movieRepository.findById(id).orElseThrow(InvalidMovieIdException::new);
        movieRepository.delete(movie);
        return movie;
    }

    @Override
    public Movie vote(Long id) {
        Movie movie = this.findById(id);
        movie.setVotes(movie.getVotes() + 1);
        return movieRepository.save(movie);
    }

    //todo
    @Override
    public List<Movie> listMoviesWithRatingLessThenAndGenre(Double rating, Genre genre) {
        if (rating != null && genre != null) {
            return movieRepository.findAllByGenreAndRatingLessThan(genre, rating);
        } else if (rating == null && genre != null) {
            return movieRepository.findAllByGenre(genre);
        } else if (rating != null) {
            return movieRepository.findAllByRatingLessThan(rating);
        } else {
            return movieRepository.findAll();
        }
    }
}
