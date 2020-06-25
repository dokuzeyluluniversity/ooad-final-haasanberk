package oop.libapp.genre;

public abstract class AbstractFactoryGenre {
    abstract Genre getGenre();
    abstract Genre getGenre(String name, String description);
}
