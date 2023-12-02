/*
import hu.nye.progtech.WumpusGame;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.io.*;
import static org.junit.jupiter.api.Assertions.assertTrue;

class WumpusGameTest {

    private final InputStream systemIn = System.in;
    private final PrintStream systemOut = System.out;
    private ByteArrayInputStream testIn;
    private ByteArrayOutputStream testOut;

    @BeforeEach
    void setUpOutput() {
        testOut = new ByteArrayOutputStream();
        System.setOut(new PrintStream(testOut));
    }

    private void provideInput(String data) {
        testIn = new ByteArrayInputStream(data.getBytes());
        System.setIn(testIn);
    }

    private String getOutput() {
        return testOut.toString();
    }

    @Test
    void testPlayGame() {

        provideInput("1\nW\nQ\n");
        ByteArrayOutputStream outContent = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outContent));
        WumpusGame.main(new String[]{});
        System.setOut(new PrintStream(new FileOutputStream(FileDescriptor.out)));
        assertTrue(outContent.toString().contains("Game over. You gave up."));
    }

    @Test
    void testLoadGameBoardFromFile() {

        provideInput("2\n");
        ByteArrayOutputStream outContent = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outContent));
        WumpusGame.main(new String[]{});
        System.setOut(new PrintStream(new FileOutputStream(FileDescriptor.out)));
        assertTrue(outContent.toString().contains("Game board loaded. You are the hero (H)."));
    }

    @Test
    void testViewHighScores() {
        provideInput("3\n");
        ByteArrayOutputStream outContent = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outContent));
        WumpusGame.main(new String[]{});
        System.setOut(new PrintStream(new FileOutputStream(FileDescriptor.out)));
        assertTrue(outContent.toString().contains("High Scores:"));
    }

    @Test
    void testSaveAndLoadGameToXML() {
        provideInput("4\n5\n");
        ByteArrayOutputStream outContent = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outContent));
        WumpusGame.main(new String[]{});
        System.setOut(new PrintStream(new FileOutputStream(FileDescriptor.out)));
        assertTrue(outContent.toString().contains("Game state saved to XML file:"));
        assertTrue(outContent.toString().contains("Game state loaded from XML file:"));
    }
}
*/
