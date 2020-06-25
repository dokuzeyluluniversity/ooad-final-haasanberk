package oop.libapp.genre;

import oop.libapp.exception.FailedFieldValidationException;
import oop.libapp.exception.ResourceNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
public class GenreController {

    private IGenreService genreService;

    public GenreController(IGenreService genreService) {
        this.genreService = genreService;
    }

    @RequestMapping(value = "api/genres", method = RequestMethod.GET)
    public ResponseEntity<List<Genre>> getGenres() {
        List<Genre> genres = genreService.findAll();
        return new ResponseEntity<>(genres, HttpStatus.OK);
    }

    @RequestMapping(value = "api/genres/{id}", method = RequestMethod.GET)
    public ResponseEntity<Genre> getGenre(@PathVariable Long id) throws ResourceNotFoundException {
        Genre genre = genreService.findById(id);
        return new ResponseEntity<>(genre, HttpStatus.OK);
    }

    @RequestMapping(value = "api/genres", method = RequestMethod.POST)
    public ResponseEntity<Genre> postGenre(@Valid @RequestBody GenreDto genreDto, BindingResult result) throws FailedFieldValidationException {
        if (result.hasErrors()) {
            throw new FailedFieldValidationException(result.getFieldErrors());
        }

        // New Design Pattern Implementation
        FactoryGenre factoryGenre = new FactoryGenre();
        Genre genre = factoryGenre.getGenre(genreDto.getName(), genreDto.getDescription());


        Genre savedGenre = genreService.save(genre);
        return new ResponseEntity<>(savedGenre, HttpStatus.CREATED);
    }

    @RequestMapping(value = "api/genres/{id}", method = RequestMethod.PUT)
    public ResponseEntity<Genre> putGenre(@PathVariable Long id, @Valid @RequestBody GenreDto genreDto, BindingResult result)
            throws ResourceNotFoundException, FailedFieldValidationException {
        if (result.hasErrors()) {
            throw new FailedFieldValidationException(result.getFieldErrors());
        }
        Genre genre = genreService.findById(id);
        genre.setName(genreDto.getName());
        genre.setDescription(genreDto.getDescription());
        Genre savedGenre = genreService.save(genre);
        return new ResponseEntity<>(savedGenre, HttpStatus.OK);
    }

    @RequestMapping(value = "api/genres/{id}", method = RequestMethod.DELETE)
    public ResponseEntity<Map> deleteGenre(@PathVariable Long id) throws ResourceNotFoundException {
        Map<String, Boolean> response = new HashMap<>();
        boolean deleted = genreService.deleteById(id);
        if (deleted) {
            response.put("deleted", true);
        } else {
            response.put("deleted", false);
        }
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

}
