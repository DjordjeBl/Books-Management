import models.Book;
import org.junit.After;
import org.junit.Before;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

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

        Mockito.when(mockConnection.prepareStatement(Mockito.anyString())).thenReturn(mockPreparedStatement);
        Mockito.when(mockPreparedStatement.executeUpdate()).thenReturn(1);

        boolean insertionResult = bookDAL.insertBook(testBook);

        assertTrue(insertionResult);
    }

}
