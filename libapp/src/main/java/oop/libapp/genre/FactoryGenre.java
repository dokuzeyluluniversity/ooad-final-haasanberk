package oop.libapp.genre;

public class FactoryGenre extends AbstractFactoryGenre {

    @Override
    Genre getGenre() {
        return new Genre();
    }

    @Override
    Genre getGenre(String name, String description) {
        return new Genre(name, description);
    }
}
