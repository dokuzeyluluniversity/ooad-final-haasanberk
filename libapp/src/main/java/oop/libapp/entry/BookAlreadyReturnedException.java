package oop.libapp.entry;

public class BookAlreadyReturnedException extends Exception {

    public BookAlreadyReturnedException(String msg) {
        super(msg);
    }
}
