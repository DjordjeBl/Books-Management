import models.Book;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class BookDALTest {
    private DataSource dataSource;
    private BookDAL bookDAL;

    @BeforeEach
    public void setUp() {
        dataSource = mock(DataSource.class); // Use a mock DataSource for testing
        bookDAL = new BookDAL(dataSource);
    }

    @Test
    public void testListAllBooks() throws SQLException {
        // Create a list of books that you expect to be returned from the database
        List<Book> expectedBooks = new ArrayList<>();
        expectedBooks.add(new Book(1, "Book1", "Author1", 19.99f));
        expectedBooks.add(new Book(2, "Book2", "Author2", 24.99f));

        // Mock the behavior of the DataSource and Connection
        Connection connection = mock(Connection.class);
        when(dataSource.getConnection()).thenReturn(connection);

        // Mock the behavior of PreparedStatement and ResultSet
        PreparedStatement preparedStatement = mock(PreparedStatement.class);
        ResultSet resultSet = mock(ResultSet.class);
        when(connection.prepareStatement(anyString())).thenReturn(preparedStatement);
        when(preparedStatement.executeQuery()).thenReturn(resultSet);

        // Mock the ResultSet to return data when next() is called
        when(resultSet.next()).thenReturn(true, true, false); // Two rows
        when(resultSet.getInt("book_id")).thenReturn(1, 2);
        when(resultSet.getString("title")).thenReturn("Book1", "Book2");
        when(resultSet.getString("author")).thenReturn("Author1", "Author2");
        when(resultSet.getFloat("price")).thenReturn(19.99f, 24.99f);

        // Perform the test
        List<Book> actualBooks = bookDAL.listAllBooks();

        // Verify that the actual result matches the expected result
        assertEquals(expectedBooks, actualBooks);
    }

    @Test
    public void testInsertBook_Success() throws SQLException {
        // Mock the necessary objects
        Connection connection = mock(Connection.class);
        PreparedStatement preparedStatement = mock(PreparedStatement.class);

        // Configure the dataSource to return the mock connection
        when(dataSource.getConnection()).thenReturn(connection);
        when(connection.prepareStatement(anyString())).thenReturn(preparedStatement);
        when(preparedStatement.executeUpdate()).thenReturn(1);

        // Create a sample book
        Book book = new Book("Sample title", "Sample Author", 10.22f);

        // Call the insertBook method
        boolean result = bookDAL.insertBook(book);

        // Verify that the SQL query was executed with the correct parameters
        verify(preparedStatement).setString(1, "Sample title");
        verify(preparedStatement).setString(2, "Sample Author");
        verify(preparedStatement).setFloat(3, 10.22f);

        // Verify that the connection and prepared statement were closed
        verify(preparedStatement).close();
        verify(connection).close();

        // Check if the assertion was successful
        assertTrue(result);
    }

    @Test
    public void testInsertBook_Failure() throws SQLException {
        Connection connection = mock(Connection.class);
        PreparedStatement preparedStatement = mock(PreparedStatement.class);

        when(dataSource.getConnection()).thenReturn(connection);
        when(connection.prepareStatement(anyString())).thenReturn(preparedStatement);
        when(preparedStatement.executeUpdate()).thenThrow(new SQLException("Insertion failed"));

        Book book = new Book("Sample title", "Sample author", 10.22f);

        boolean result = bookDAL.insertBook(book);

        verify(preparedStatement).setString(1, "Sample title");
        verify(preparedStatement).setString(2, "Sample author");
        verify(preparedStatement).setFloat(3, 10.22f);

        verify(preparedStatement).close();
        verify(connection).close();

        assertFalse(result);
    }

}
