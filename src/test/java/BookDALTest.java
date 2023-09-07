import models.Book;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

import java.sql.*;

public class BookDALTest {

    @Mock
    private Connection mockConnection;

    @Mock
    private PreparedStatement mockStatement;

    private BookDAL bookDAL;

    @BeforeEach
    public void setUp() throws SQLException {
        MockitoAnnotations.openMocks(this);
        bookDAL = new BookDAL(mockConnection);
    }

    @Test
    public void testInsertBook_Success() throws SQLException {
        when(mockConnection.prepareStatement(anyString())).thenReturn(mockStatement);
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


}
