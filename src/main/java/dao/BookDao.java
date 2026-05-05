package dao;

import model.Book;

import java.util.List;
import java.util.Optional;

public interface BookDao {
    Book save(Book book) throws Exception;

    boolean update(Book book) throws Exception;

    boolean delete(int id) throws Exception;

    Optional<Book> findById(int id) throws Exception;

    List<Book> findAll() throws Exception;

    List<Book> search(String q) throws Exception;

    boolean adjustAvailableQuantity(int bookId, int delta) throws Exception;
}

