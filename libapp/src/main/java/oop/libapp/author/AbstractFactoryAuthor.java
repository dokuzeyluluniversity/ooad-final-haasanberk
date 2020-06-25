package oop.libapp.author;

public abstract class AbstractFactoryAuthor {
    abstract Author getAuthor();
    abstract Author getAuthor(String name, String description);
}
