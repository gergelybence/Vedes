package hu.nye.progtech;

import java.io.File;
import java.io.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

public class WumpusGame {
    private static final String FILE_PATH = "C:\\2023-24\\progtech_vedes\\wumpus\\src\\main\\java\\hu\\nye\\progtech\\wumpuszinputTest.txt";
    private static final String HIGH_SCORES_FILE_PATH = "C:\\2023-24\\progtech_vedes\\wumpus\\src\\main\\java\\hu\\nye\\progtech\\highscores.txt";
    private static final String XML_FILE_PATH = "C:\\2023-24\\progtech_vedes\\wumpus\\src\\main\\java\\hu\\nye\\progtech\\savegame.xml";
    private static char[][] gameBoard;
    private static int heroRow;
    private static int heroColumn;
    private static char heroFacing;
    private static int arrowCount;
    private static int stepCount;
    private static Map<String, Integer> scores = new HashMap<>();


    public static void main(String[] args) {
        loadHighScores();

        Scanner scanner = new Scanner(System.in);

        System.out.print("Enter your username: ");
        String username = scanner.nextLine();

        while (true) {
            System.out.println("\nWelcome, " + username + "!");
            System.out.println("\nMain Menu:");
            System.out.println("1. Play");
            System.out.println("2. Load Game Board from File");
            System.out.println("3. View High Scores");
            System.out.println("4. Save Game to XML");
            System.out.println("5. Load Game from XML");
            System.out.println("6. Exit");

            int choice = scanner.nextInt();
            scanner.nextLine();

            switch (choice) {
                case 1 -> {
                    if (gameBoard != null) {
                        playGame(username);
                    } else {
                        System.err.println("Please load the game board first.");
                    }
                }

                case 2 -> loadGameBoardFromFile();
                case 3 -> viewHighScores();
                case 4 -> saveGameToXML();
                case 5 -> loadGameFromXML();
                case 6 -> {
                    saveHighScores();
                    System.out.println("Goodbye, " + username + "!");
                    System.exit(0);
                }
                default -> System.err.println("Invalid choice. Please select a valid option.");
            }
        }
    }

    private static void loadGameFromXML() {
        try {
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(new File(XML_FILE_PATH));

            doc.getDocumentElement().normalize();

            // Get game state elements from XML
            Element rootElement = doc.getDocumentElement();
            int heroRow = Integer.parseInt(getElementValue(rootElement, "HeroRow"));
            int heroColumn = Integer.parseInt(getElementValue(rootElement, "HeroColumn"));
            char heroFacing = getElementValue(rootElement, "HeroFacing").charAt(0);
            int arrowCount = Integer.parseInt(getElementValue(rootElement, "ArrowCount"));
            int stepCount = Integer.parseInt(getElementValue(rootElement, "StepCount"));

            // Update game state
            WumpusGame.heroRow = heroRow;
            WumpusGame.heroColumn = heroColumn;
            WumpusGame.heroFacing = heroFacing;
            WumpusGame.arrowCount = arrowCount;
            WumpusGame.stepCount = stepCount;

            System.out.println("Game state loaded from XML file: " + XML_FILE_PATH);
        } catch (Exception e) {
            System.err.println("Error loading game state from XML: " + e.getMessage());
        }
    }

    private static String getElementValue(Element element, String tagName) {
        NodeList nodeList = element.getElementsByTagName(tagName).item(0).getChildNodes();
        return nodeList.item(0).getNodeValue();
    }

    private static void loadGameBoardFromFile() {
        try {
            BufferedReader reader = new BufferedReader(new FileReader(FILE_PATH));

            String[] info = reader.readLine().split(" ");
            int N = Integer.parseInt(info[0]);

            gameBoard = new char[N][N];

            for (int i = 0; i < N; i++) {
                String line = reader.readLine();
                for (int j = 0; j < N; j++) {
                    gameBoard[i][j] = line.charAt(j);
                    if (gameBoard[i][j] == 'H') {
                        heroRow = i;
                        heroColumn = j;
                        heroFacing = 'N';
                    } else if (gameBoard[i][j] == 'U') {
                        arrowCount++;
                    } else if (gameBoard[i][j] == 'G') {
                        stepCount = 0;
                    }
                }
            }

            System.out.println("\nGame board loaded. You are the hero (H).\n");
            displayGameBoard();
        } catch (IOException e) {
            System.err.println("\nError reading the file: " + e.getMessage());
        }
    }

    private static void displayGameBoard() {
        for (int i = 0; i < gameBoard.length; i++) {
            for (int j = 0; j < gameBoard[i].length; j++) {
                System.out.print(gameBoard[i][j]);
            }
            System.out.println();
        }

        System.out.println("Hero Facing: " + heroFacing + " | Arrows: " + arrowCount + " | Steps: " + stepCount);
    }

    private static void playGame(String username) {
        Scanner inputScanner = new Scanner(System.in);
        while (true) {
            displayGameBoard();

            System.out.print("Enter a move (W/A/S/D to move, Q to give up, F to fire arrow): ");
            String move = inputScanner.nextLine().toUpperCase();

            if (move.equals("Q")) {
                System.out.println("Game over. You gave up.");
                break;
            } else if (move.equals("F")) {
                fireArrow();
                continue;
            }

            int newRow = heroRow;
            int newColumn = heroColumn;

            switch (move) {
                case "W" -> {
                    newRow--;
                    heroFacing = 'N';
                }
                case "S" -> {
                    newRow++;
                    heroFacing = 'S';
                }
                case "A" -> {
                    newColumn--;
                    heroFacing = 'W';
                }
                case "D" -> {
                    newColumn++;
                    heroFacing = 'E';
                }
                default -> {
                    System.err.println("Invalid move. Try again.");
                    continue;
                }
            }

            if (isValidMove(newRow, newColumn)) {
                char newLocation = gameBoard[newRow][newColumn];
                if (newLocation == 'P') {
                    System.err.println("You fell into a pit. You lost an arrow.");
                    arrowCount--;
                } else if (newLocation == 'U') {
                    System.err.println("You stepped on a wumpus. You died.");
                    displayGameBoard();
                    return;
                } else if (newLocation == 'G') {
                    System.out.println("You found the gold. You win!");
                    stepCount++;
                    scores.put(username, scores.getOrDefault(username, 0) + 1); // Increment the high score
                    break;
                } else if (newLocation == '_') {
                    gameBoard[heroRow][heroColumn] = '_';
                    heroRow = newRow;
                    heroColumn = newColumn;
                    gameBoard[heroRow][heroColumn] = 'H';
                    stepCount++;
                } else {
                    System.err.println("You cannot move there. Try again.");
                }
            }
        }
    }

    private static void fireArrow() {
        if (arrowCount > 0) {
            arrowCount--;

            int arrowRow = heroRow;
            int arrowColumn = heroColumn;

            switch (heroFacing) {
                case 'N' -> arrowRow--;
                case 'S' -> arrowRow++;
                case 'W' -> arrowColumn--;
                case 'E' -> arrowColumn++;
            }

            while (isValidMove(arrowRow, arrowColumn)) {
                char target = gameBoard[arrowRow][arrowColumn];
                if (target == 'W') {
                    System.err.println("Arrow hit a wall. Arrow lost.");
                    break;
                } else if (target == 'U') {
                    gameBoard[arrowRow][arrowColumn] = '_';
                    break;
                } else {
                    arrowRow += (heroFacing == 'S') ? 1 : (heroFacing == 'N') ? -1 : 0;
                    arrowColumn += (heroFacing == 'E') ? 1 : (heroFacing == 'W') ? -1 : 0;
                }
            }

            displayGameBoard();
            System.out.println("You hit a wumpus ");
        } else {
            System.err.println("You don't have any arrows left.");
        }
    }

    private static void viewHighScores() {
        System.out.println("\nHigh Scores:");
        for (Map.Entry<String, Integer> entry : scores.entrySet()) {
            System.out.println(entry.getKey() + ": " + entry.getValue());
        }
    }

    private static boolean isValidMove(int newRow, int newColumn) {
        return newRow >= 0 && newRow < gameBoard.length && newColumn >= 0 && newColumn < gameBoard[newRow].length;
    }

    private static void loadHighScores() {
        try (BufferedReader reader = new BufferedReader(new FileReader(HIGH_SCORES_FILE_PATH))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(":");
                String username = parts[0].trim();
                int highScore = Integer.parseInt(parts[1].trim());
                scores.put(username, highScore);
            }
        } catch (IOException e) {
            System.err.println("Error loading high scores: " + e.getMessage());
        }
    }

    private static void saveHighScores() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(HIGH_SCORES_FILE_PATH))) {
            for (Map.Entry<String, Integer> entry : scores.entrySet()) {
                writer.write(entry.getKey() + ": " + entry.getValue());
                writer.newLine();
            }
        } catch (IOException e) {
            System.err.println("Error saving high scores: " + e.getMessage());
        }
    }

    private static void saveGameToXML() {
        try {
            DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder docBuilder = docFactory.newDocumentBuilder();

            // Create the root element
            Document doc = docBuilder.newDocument();
            Element rootElement = doc.createElement("WumpusGame");
            doc.appendChild(rootElement);

            // Add game state elements
            addElement(doc, rootElement, "HeroRow", String.valueOf(heroRow));
            addElement(doc, rootElement, "HeroColumn", String.valueOf(heroColumn));
            addElement(doc, rootElement, "HeroFacing", String.valueOf(heroFacing));
            addElement(doc, rootElement, "ArrowCount", String.valueOf(arrowCount));
            addElement(doc, rootElement, "StepCount", String.valueOf(stepCount));

            // Save the XML file
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");

            DOMSource source = new DOMSource(doc);
            StreamResult result = new StreamResult(new FileWriter(XML_FILE_PATH));

            transformer.transform(source, result);

            System.out.println("Game state saved to XML file: " + XML_FILE_PATH);
        } catch (ParserConfigurationException | TransformerConfigurationException e) {
            System.err.println("Error configuring XML parser or transformer: " + e.getMessage());
        } catch (TransformerException | IOException e) {
            System.err.println("Error transforming XML or writing to file: " + e.getMessage());
        }
    }

    private static void addElement(Document doc, Element parent, String name, String value) {
        Element element = doc.createElement(name);
        element.appendChild(doc.createTextNode(value));
        parent.appendChild(element);
    }
}
