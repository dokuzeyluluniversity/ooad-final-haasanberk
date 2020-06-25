package oop.libapp.book;

import oop.libapp.author.Author;
import oop.libapp.genre.Genre;
import java.util.List;
import java.util.Set;

public class FactoryBook extends AbstractFactoryBook{

    private static FactoryBook factoryBook = new FactoryBook();

    private FactoryBook(){};

    @Override
    Book getBook() {
        return new Book();
    }

    @Override
    Book getBook(String title, Set<Author> authors, List<Genre> genres) {
        return new Book(title, authors, genres);
    }

    public static FactoryBook getFactoryBook(){
        return factoryBook;
    }
}
