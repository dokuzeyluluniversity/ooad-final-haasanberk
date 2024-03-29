package oop.libapp.entry;

import oop.libapp.book.Book;
import oop.libapp.register.User;

import javax.persistence.*;
import java.util.Date;

@Entity
public class Entry {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "book_id")
    private Book bookBorrowed;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User userBorrowing;

    private Date dateStarted;
    private Date dateFinished;
    private Boolean returned;

    public Entry() {}
    public Entry(Book bookBorrowed, User userBorrowing) {
        this.bookBorrowed = bookBorrowed;
        this.userBorrowing = userBorrowing;
        this.dateStarted = new Date();
        this.dateFinished = null;
        this.returned = false;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Book getBookBorrowed() {
        return bookBorrowed;
    }

    public void setBookBorrowed(Book bookBorrowed) {
        this.bookBorrowed = bookBorrowed;
    }

    public User getUserBorrowing() {
        return userBorrowing;
    }

    public void setUserBorrowing(User userBorrowing) {
        this.userBorrowing = userBorrowing;
    }

    public Date getDateStarted() {
        return dateStarted;
    }

    public void setDateStarted(Date dateStarted) {
        this.dateStarted = dateStarted;
    }

    public Date getDateFinished() {
        return dateFinished;
    }

    public void setDateFinished(Date dateFinished) {
        this.dateFinished = dateFinished;
    }

    public Boolean getReturned() {
        return returned;
    }

    public void setReturned(Boolean returned) {
        this.returned = returned;
    }

    public void returnBook() {
        setReturned(true);
        setDateFinished(new Date());
    }
}
