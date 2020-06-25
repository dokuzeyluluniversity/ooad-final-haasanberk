package oop.libapp.genre;

import oop.libapp.exception.ResourceNotFoundException;

import java.util.List;

public interface IGenreService {
    List<Genre> findAll();
    Genre save(Genre genre);
    Genre findById(Long id) throws ResourceNotFoundException;
    boolean deleteById(Long id) throws ResourceNotFoundException;
}
