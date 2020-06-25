package oop.libapp.entry;

import oop.libapp.book.Book;
import oop.libapp.register.User;


public abstract class AbstractFactoryEntry {
    abstract Entry getEntry();
    abstract Entry getEntry(Book bookBorrowed, User userBorrowing);
}
