import models.Book;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class BookDALTest {

    @Mock
    private Connection mockConnection;

    @Mock
    private PreparedStatement mockStatement;

    private BookDAL bookDAL;

    @Mock
    private ResultSet mockResultSet;


    @BeforeEach
    public void setUp() throws SQLException {
        try (AutoCloseable mocks = MockitoAnnotations.openMocks(this)) {
            bookDAL = new BookDAL(mockConnection);

            when(mockConnection.prepareStatement(anyString())).thenReturn(mockStatement);
            when(mockConnection.createStatement()).thenReturn(mockStatement);
            when(mockStatement.executeQuery(anyString())).thenReturn(mockResultSet);
        } catch (Exception e) {
            fail("Exception occurred in test setup: " + e.getMessage());
        }
    }

    @Test
    public void testInsertBook_Success() throws SQLException {
        when(mockStatement.executeUpdate()).thenReturn(1); // Simulate a successful insert

        Book book = new Book("Sample Title", "Sample Author", 19.99f);

        boolean rowInserted = bookDAL.insertBook(book);

        assertTrue(rowInserted);

        verify(mockConnection, times(1)).prepareStatement(anyString());
        verify(mockStatement, times(1)).setString(1, book.getTitle());
        verify(mockStatement, times(1)).setString(2, book.getAuthor());
        verify(mockStatement, times(1)).setFloat(3, book.getPrice());
        verify(mockStatement, times(1)).executeUpdate();
        verify(mockStatement, times(1)).close();
        verify(mockConnection, times(1)).close();
    }

    @Test
    public void testInsertBook_Failure() throws SQLException {
        when(mockStatement.executeUpdate()).thenReturn(0); // Simulate a failed insert

        Book book = new Book("Sample Title", "Sample Author", 19.99f);

        try {
            boolean rowInserted = bookDAL.insertBook(book);
            fail("Expected SQLException"); // Fail the test if no exception is thrown
        } catch (SQLException e) {
            // Expected SQLException, test passes
        }

        verify(mockConnection, times(1)).prepareStatement(anyString());
        verify(mockStatement, times(1)).setString(1, book.getTitle());
        verify(mockStatement, times(1)).setString(2, book.getAuthor());
        verify(mockStatement, times(1)).setFloat(3, book.getPrice());
        verify(mockStatement, times(1)).executeUpdate();
        verify(mockStatement, times(1)).close();
        verify(mockConnection, times(1)).close();
    }

    @Test
    public void testListAllBooks() throws SQLException {
        List<Book> expectedBooks = new ArrayList<>();
        expectedBooks.add(new Book(1, "Sample Title 1", "Sample Author 1", 19.99f));
        expectedBooks.add(new Book(2, "Sample Title 2", "Sample Author 2", 29.99f));

        when(mockResultSet.next()).thenReturn(true, true, false);
        when(mockResultSet.getInt("book_id")).thenReturn(1, 2);
        when(mockResultSet.getString("title")).thenReturn("Sample Title 1", "Sample Title 2");
        when(mockResultSet.getString("author")).thenReturn("Sample Author 1", "Sample Author 2");
        when(mockResultSet.getFloat("price")).thenReturn(19.99f, 29.99f);

        List<Book> result = bookDAL.listAllBooks();

        assertEquals(expectedBooks.size(), result.size());
        for (int i = 0; i < expectedBooks.size(); i++) {
            Book expectedBook = expectedBooks.get(i);
            Book actualBook = result.get(i);
            assertEquals(expectedBook.getId(), actualBook.getId());
            assertEquals(expectedBook.getTitle(), actualBook.getTitle());
            assertEquals(expectedBook.getAuthor(), actualBook.getAuthor());
            assertEquals(expectedBook.getPrice(), actualBook.getPrice(), 0.001); // Use delta for float comparison
        }

        // Verify interactions with mock objects
        verify(mockConnection, times(1)).createStatement();
        verify(mockStatement, times(1)).executeQuery(anyString());
        verify(mockResultSet, times(3)).next(); // Should be called three times (two rows and one for the end)
        verify(mockResultSet, times(2)).getInt("book_id");
        verify(mockResultSet, times(2)).getString("title");
        verify(mockResultSet, times(2)).getString("author");
        verify(mockResultSet, times(2)).getFloat("price");
        verify(mockResultSet, times(1)).close();
        verify(mockStatement, times(1)).close();
    }

    @Test
    public void deleteBookTest_Success() throws SQLException {
        when(mockStatement.executeUpdate()).thenReturn(1);

        int bookIdToDelete = 1; // Replace with the actual bookId

        try {
            boolean isDeleted = bookDAL.deleteBook(bookIdToDelete);
            assertTrue(isDeleted);
        } catch (SQLException e) {
            fail("Exception should not be thrown during the test.");
        }
    }

    @Test
    public void deleteBookTest_Failure() throws SQLException {
        when(mockStatement.executeUpdate()).thenReturn(0);

        int bookIdToDelete = 1;

        boolean isDeleted = bookDAL.deleteBook(bookIdToDelete);
        assertFalse(isDeleted);
    }

    @Test
    public void updateBookTest_Success() throws SQLException {
        Book bookToUpdate = new Book();
        bookToUpdate.setId(1);
        bookToUpdate.setTitle("Updated Title");
        bookToUpdate.setAuthor("Updated Author");
        bookToUpdate.setPrice(19.99f);

        Mockito.when(mockStatement.executeUpdate()).thenReturn(1);
        boolean isUpdated = bookDAL.updateBook(bookToUpdate);
        assertTrue(isUpdated);
    }

    @Test
    public void updateBookTest_Failure() throws SQLException {
        Book bookToUpdate = new Book();
        bookToUpdate.setId(1);
        bookToUpdate.setTitle("Updated Title");
        bookToUpdate.setAuthor("Updated Author");
        bookToUpdate.setPrice(19.99f);

        Mockito.when(mockStatement.executeUpdate()).thenReturn(0);
        boolean isUpdated = bookDAL.updateBook(bookToUpdate);
        assertFalse(isUpdated);
    }

    @Test
    public void getBookTest_Success() throws SQLException {
        int bookIdToRetrieve = 1;
        String expectedTitle = "Sample Title";
        String expectedAuthor = "Sample Author";
        float expectedPrice = 29.99f;

        Mockito.when(mockStatement.executeQuery()).thenReturn(mockResultSet);
        Mockito.when(mockResultSet.next()).thenReturn(true);
        Mockito.when(mockResultSet.getString("title")).thenReturn(expectedTitle);
        Mockito.when(mockResultSet.getString("author")).thenReturn(expectedAuthor);
        Mockito.when(mockResultSet.getFloat("price")).thenReturn(expectedPrice);

        Book retrievedBook = bookDAL.getBook(bookIdToRetrieve);

        assertNotNull(retrievedBook);
        assertEquals(bookIdToRetrieve, retrievedBook.getId());
        assertEquals(expectedTitle, retrievedBook.getTitle());
        assertEquals(expectedAuthor, retrievedBook.getAuthor());
        assertEquals(expectedPrice, retrievedBook.getPrice(), 0.01);
    }

    @Test
    public void getBookTest_Failure() throws SQLException {
        int bookIdToRetrieve = 1;

        Mockito.when(mockStatement.executeQuery()).thenReturn(mockResultSet);
        Mockito.when(mockResultSet.next()).thenReturn(false);

        Book retrievedBook = bookDAL.getBook(bookIdToRetrieve);

        assertNull(retrievedBook);
    }
}
