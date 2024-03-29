package oop.libapp.book;

import oop.libapp.exception.FailedFieldValidationException;
import oop.libapp.exception.ResourceNotFoundException;
import oop.libapp.author.Author;
import oop.libapp.genre.Genre;
import oop.libapp.author.IAuthorService;
import oop.libapp.genre.IGenreService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.*;

@RestController
public class BookController {

    private IBookService bookService;
    private IAuthorService authorService;
    private IGenreService genreService;
    private IBookInfoService bookInfoService;

    // New Design Pattern Implementation
    private FactoryBook factoryBook;

    @Autowired
    public BookController(IBookService bookService, IAuthorService authorService,
                          IGenreService genreService, IBookInfoService bookInfoService) {
        this.bookService = bookService;
        this.authorService = authorService;
        this.genreService = genreService;
        this.bookInfoService = bookInfoService;

        // New Design Pattern Implementation
        this.factoryBook = FactoryBook.getFactoryBook();
    }

    @RequestMapping(value = "api/books", method = RequestMethod.GET)
    public ResponseEntity<List<Book>> getBooks(@RequestParam(value = "title", required = false) String title,
                                               @RequestParam(value = "author", required = false) String author,
                                               @RequestParam(value = "genre", required = false) String genre) {
        List<Book> books;
        if (title != null) {
            books = bookService.findAllByTitleContaining(title);
        } else if (author != null) {
            books = bookService.findAllByAuthorsContainingName(author);
        } else if (genre != null) {
            books = bookService.findAllByGenresContainingName(genre);
        } else {
            books = bookService.findAll();
        }
        return new ResponseEntity<>(books , HttpStatus.OK);
    }

    @RequestMapping(value = "api/books", method = RequestMethod.POST)
    public ResponseEntity<Book> postBook(@Valid @RequestBody NewBookDto newBookDto, BindingResult result)
            throws FailedFieldValidationException, ResourceNotFoundException {
        if (result.hasErrors()) {
            throw new FailedFieldValidationException(result.getFieldErrors());
        }
        Set<Author> bookAuthors = new HashSet<>();
        List<Genre> bookGenres = new ArrayList<>();

        for (Long authorId : newBookDto.getAuthorIds()) {
            bookAuthors.add(authorService.findById(authorId));
        }

        for (Long genreId : newBookDto.getGenreIds()) {
            bookGenres.add(genreService.findById(genreId));
        }

        // New Design Pattern Implementation
        Book book = factoryBook.getBook(newBookDto.getTitle(), bookAuthors, bookGenres);

        Book savedBook = bookService.save(book);
        return new ResponseEntity<>(savedBook, HttpStatus.CREATED);
    }

    @RequestMapping(value = "api/books/{id}", method = RequestMethod.GET)
    public ResponseEntity<Book> getBook(@PathVariable Long id) throws ResourceNotFoundException {
        Book book = bookService.findById(id);
        return new ResponseEntity<>(book, HttpStatus.OK);
    }

    @RequestMapping(value = "api/books/{id}", method = RequestMethod.PATCH)
    public ResponseEntity<Book> patchBook(@PathVariable Long id, @Valid @RequestBody PatchBookDto patchBookDto, BindingResult result)
        throws FailedFieldValidationException, ResourceNotFoundException {
        if (result.hasErrors()) {
            throw new FailedFieldValidationException(result.getFieldErrors());
        }

        String newTitle = patchBookDto.getTitle();
        List<Long> newAuthorIds = patchBookDto.getAuthorIds();
        List<Long> newGenreIds = patchBookDto.getGenreIds();

        Book book = bookService.findById(id);

        if (newTitle != null) {
            book.setTitle(newTitle);
        }

        if (newAuthorIds != null) {
            Set<Author> newAuthors = new HashSet<>();
            for (Long authorId : newAuthorIds) {
                newAuthors.add(authorService.findById(authorId));
            }
            book.setAuthors(newAuthors);
        }

        if (newGenreIds != null) {
            List<Genre> newGenres = new ArrayList<>();
            for (Long genreId : newGenreIds) {
                newGenres.add(genreService.findById(genreId));
            }
            book.setGenres(newGenres);
        }

        Book patchedBook = bookService.save(book);
        return new ResponseEntity<>(patchedBook, HttpStatus.OK);
    }

    @RequestMapping(value = "api/books/{id}", method = RequestMethod.DELETE)
    public ResponseEntity<Map> deleteBook(@PathVariable Long id) throws ResourceNotFoundException {
        Map<String, Boolean> response = new HashMap<>();
        boolean deleted = bookService.deleteById(id);
        if (deleted) {
            response.put("deleted", true);
        } else {
            response.put("deleted", false);
        }
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @RequestMapping(value = "api/books/{id}/bookInfo", method = RequestMethod.GET)
    public ResponseEntity<BookInfo> getBookInfo(@PathVariable Long id) throws ResourceNotFoundException {
        BookInfo bookInfo = bookInfoService.findById(id);
        return new ResponseEntity<>(bookInfo, HttpStatus.OK);
    }

    @RequestMapping(value = "api/books/{id}/bookInfo", method = RequestMethod.PATCH)
    public ResponseEntity<BookInfo> patchBookInfo(@PathVariable Long id,
                                                  @Valid @RequestBody PatchBookInfoDto patchBookInfoDto,
                                                  BindingResult result) throws ResourceNotFoundException, FailedFieldValidationException {
        if (result.hasErrors()) {
            throw new FailedFieldValidationException(result.getFieldErrors());
        }

        BookInfo bookInfo = bookInfoService.findById(id);

        Integer newNumberOfPages = patchBookInfoDto.getNumberOfPages();
        String newLanguage = patchBookInfoDto.getLanguage();
        Integer newPublicationYear = patchBookInfoDto.getPublicationYear();
        String newDescription = patchBookInfoDto.getDescription();
        String newISBN = patchBookInfoDto.getIsbn();

        if (newNumberOfPages != null) {
            bookInfo.setNumberOfPages(newNumberOfPages);
        }
        if (newLanguage != null) {
            bookInfo.setLanguage(newLanguage);
        }
        if (newPublicationYear != null) {
            bookInfo.setPublicationYear(newPublicationYear);
        }
        if (newDescription != null) {
            bookInfo.setDescription(newDescription);
        }
        if (newISBN != null) {
            bookInfo.setIsbn(newISBN);
        }

        BookInfo savedBookInfo = bookInfoService.save(bookInfo);
        return new ResponseEntity<>(savedBookInfo, HttpStatus.OK);
    }
}
