import exceptions.BookNotFoundException;
import exceptions.ServiceException;
import models.Book;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public class BookService {

    private BookDAL bookDAL;

    public BookService(BookDAL bookDAL) {
        this.bookDAL = bookDAL;
    }

    public boolean insertBook(Book book) {
        try {
            return bookDAL.insertBook(book);
        } catch (SQLException e) {
            throw new ServiceException("Failed to insert a book", e);
        }
    }

    public List<Book> listAllBooks() {
        try {
            return bookDAL.listAllBooks();
        } catch (SQLException e) {
            throw new ServiceException("Failed to list books", e);
        }
    }

    public boolean updateBook(Book book) {
        try {
            Optional<Book> existingBook = Optional.ofNullable(bookDAL.getBook(book.getId()));
            if (existingBook.isPresent()) {
                return bookDAL.updateBook(book);
            } else {
                throw new BookNotFoundException("Book not found");
            }
        } catch (SQLException e) {
            throw new ServiceException("Failed to update a book", e);
        }
    }

    public boolean deleteBook(int bookId) {
        try {
            Optional<Book> existingBook = Optional.ofNullable(bookDAL.getBook(bookId));
            if (existingBook.isPresent()) {
                return bookDAL.deleteBook(bookId);
            } else {
                throw new BookNotFoundException("Book not found");
            }
        } catch (SQLException e) {
            throw new ServiceException("Failed to delete book", e);
        }
    }

    public Optional<Book> getBook(int id) {
        try {
            return Optional.ofNullable(bookDAL.getBook(id));
        } catch (SQLException e) {
            throw new ServiceException("Failed to retrieve book", e);
        }
    }
}
