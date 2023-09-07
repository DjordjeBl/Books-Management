import models.Book;
import org.junit.After;
import org.junit.Before;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.sql.*;

public class BookDALTest {
    @Mock
    private Connection mockConnection;

    @Mock
    private PreparedStatement mockPreparedStatement;

    private BookDAL bookDAL;

    @Before
    public void setUp() throws SQLException {
        MockitoAnnotations.initMocks(this);

        // Initialize the BookDAL with the mockConnection
        bookDAL = new BookDAL(mockConnection);
    }

    @After
    public void tearDown() throws SQLException {
        // Clean up and close any resources
    }

    @Test
    public void testInsertBook() throws SQLException {
        Book testBook = new Book("Test Title", "Test Author", 19.99f);

        when(mockConnection.prepareStatement(anyString())).thenReturn(mockPreparedStatement);
        when(mockPreparedStatement.executeUpdate()).thenReturn(1);

        boolean insertionResult = bookDAL.insertBook(testBook);

        assertTrue(insertionResult);
    }



}
