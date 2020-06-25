package oop.libapp.author;

public class FactoryAuthor extends AbstractFactoryAuthor{

    private static FactoryAuthor factoryAuthor = new FactoryAuthor();

    private FactoryAuthor(){};

    @Override
    Author getAuthor() {
        return new Author();
    }

    @Override
    Author getAuthor(String name, String description) {
        return new Author(name, description);
    }

    public static FactoryAuthor getFactoryAuthor() {
        return factoryAuthor;
    }
}
