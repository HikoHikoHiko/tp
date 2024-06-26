package ProcessCommand;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import processusercommands.TimetableParser;
import timetable.TimetableList;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

public class TimetableParserTest {

    private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();
    private final PrintStream originalOut = System.out;

    @BeforeEach
    public void setUp() {
        System.setOut(new PrintStream(outContent));
        new TimetableList();
    }

    @AfterEach
    public void tearDown() {
        System.setOut(originalOut);
    }

    @Test
    void testParseTimetableAdd() {
        TimetableParser.parseTimetable("tt/ add/description here");
        assertFalse(outContent.toString().contains("Adding class logic"), "Output should indicate that " +
                "adding class logic was called");
    }

    @Test
    void testParseTimetableDelete() {
        TimetableParser.parseTimetable("tt/ del/description here");
        assertFalse(outContent.toString().contains("Deleting class logic"), "Output should " +
                "indicate that deleting class logic was called");
    }

    @Test
    void testParseTimetableList() {
        TimetableParser.parseTimetable("tt/ list");
        assertFalse(outContent.toString().contains("Listing timetable"), "Output should indicate that " +
                "listing timetable logic was called");
    }

    @Test
    void testParseTimetableListByDay() {
        TimetableParser.parseTimetable("tt/ list -d/ Monday");
        assertFalse(outContent.toString().contains("Listing by day"), "Output should indicate that " +
                "listing by day logic was called");
    }

    @Test
    void testParseTimetableUnknownCommand() {
        TimetableParser.parseTimetable("tt/ foo/description here");
        assertTrue(outContent.toString().contains("Unknown command description here"), "Output " +
                "should indicate unknown command was encountered");
    }

    @Test
    void testParseTimetableIncompleteCommand() {
        TimetableParser.parseTimetable("tt/ add");
        assertTrue(outContent.toString().contains("Incomplete command!"), "Output should indicate " +
                "incomplete command was encountered");
    }

    @Test
    public void testAddAndList() {
        System.setOut(new PrintStream(outContent));

        TimetableParser.parseTimetable("tt/ add/ day/1 code/MA1508E time/10 duration/2 location/E1-06-10");
        assertEquals("Class added successfully.", outContent.toString().trim());
        outContent.reset();

        TimetableParser.parseTimetable("tt/ list/");
        assertTrue(outContent.toString().contains("- Code: MA1508E, Class Time: 10, " +
                "Duration: 2, Location: E1-06-10"));

        System.setOut(originalOut);
    }

    @Test
    public void testInvalidAddAndList() {
        System.setOut(new PrintStream(outContent));

        // Test adding a class with invalid day format
        TimetableParser.parseTimetable("tt/ add/ day/XYZ code/MA1508E time/10 duration/2 location/E1-06-10");
        assertEquals("For input string: \"XYZ\" Must be an integer.", outContent.toString().trim());
        outContent.reset();

        // Test adding a class with invalid time
        TimetableParser.parseTimetable("tt/ add/ day/1 code/MA1508E time/25 duration/2 location/E1-06-10");
        assertTrue(outContent.toString().contains("Time of the day does not exist."));
        outContent.reset();

        // Test adding a class with invalid duration
        TimetableParser.parseTimetable("tt/ add/ day/1 code/MA1508E time/10 duration/0 location/E1-06-10");
        assertTrue(outContent.toString().contains("Invalid class duration."));
        outContent.reset();

        // Test listing timetable with invalid command
        TimetableParser.parseTimetable("tt/ list");
        assertEquals("Incomplete command!", outContent.toString().trim());
        outContent.reset();

        // Test adding a class with missing day
        TimetableParser.parseTimetable("tt/ add/ code/MA1508E time/10 duration/2 location/E1-06-10");
        assertEquals("Incomplete command. Please refer to the help message " +
                "for format.", outContent.toString().trim());
        outContent.reset();

        // Test adding a class with missing code
        TimetableParser.parseTimetable("tt/ add/ day/1 time/10 duration/2 location/E1-06-10");
        assertEquals("Incomplete command. Please refer to the help message for format.",
                outContent.toString().trim());
        outContent.reset();

        // Test adding a class with missing time
        TimetableParser.parseTimetable("tt/ add/ day/1 code/MA1508E duration/2 location/E1-06-10");
        assertEquals("Incomplete command. Please refer to the help message " +
                "for format.", outContent.toString().trim());
        outContent.reset();

        // Test adding a class with missing duration
        TimetableParser.parseTimetable("tt/ add/ day/1 code/MA1508E time/10 location/E1-06-10");
        assertEquals("Incomplete command. Please refer to the help message " +
                "for format.", outContent.toString().trim());
        outContent.reset();

        // Test adding a class with missing location
        TimetableParser.parseTimetable("tt/ add/ day/1 code/MA1508E time/10 duration/2");
        assertEquals("Incomplete command. Please refer to the help message " +
                "for format.", outContent.toString().trim());
        outContent.reset();

        // Test adding a class with long location
        TimetableParser.parseTimetable("tt/ add/ day/1 code/MA1508E time/10 duration/2 " +
                "location/ThisIsAVeryLongLocationNameThatExceedsTheLimit");
        assertEquals("Class location details should be within 20 characters.", outContent.toString().trim());
        outContent.reset();

        // Reset System.out
        System.setOut(originalOut);
    }


}
