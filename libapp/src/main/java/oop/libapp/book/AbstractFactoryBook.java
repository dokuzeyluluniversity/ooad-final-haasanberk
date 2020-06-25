package oop.libapp.book;

import oop.libapp.author.Author;
import oop.libapp.genre.Genre;
import java.util.List;
import java.util.Set;

public abstract class AbstractFactoryBook {
    abstract Book getBook();
    abstract Book getBook(String title, Set<Author> authors, List<Genre> genres);
}
