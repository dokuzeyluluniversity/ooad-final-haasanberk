package oop.libapp.entry;

import oop.libapp.book.Book;
import oop.libapp.register.User;

public class FactoryEntry extends AbstractFactoryEntry {

    public static FactoryEntry factoryEntry = new FactoryEntry();

    private FactoryEntry() {

    }

    @Override
    Entry getEntry() {
        return new Entry();
    }

    @Override
    Entry getEntry(Book bookBorrowed, User userBorrowing) {
        return new Entry(bookBorrowed, userBorrowing);
    }

    public static FactoryEntry getFactoryEntry(){
        return factoryEntry;
    }
}
