package oop.libapp.author;

import oop.libapp.exception.ResourceNotFoundException;

import java.util.List;

public interface IAuthorService {
    List<Author> findAll();
    List<Author> findAllByNameContaining(String name);
    Author save(Author author);
    Author findById(Long id) throws ResourceNotFoundException;
    boolean deleteById(Long id) throws ResourceNotFoundException;
}

