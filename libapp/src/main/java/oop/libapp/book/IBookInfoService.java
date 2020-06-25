package oop.libapp.book;

import oop.libapp.exception.ResourceNotFoundException;

public interface IBookInfoService {
    BookInfo findById(Long id) throws ResourceNotFoundException;
    BookInfo save(BookInfo bookInfo);
}
