package hu.nye.progtech;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Scanner;

public class WumpusGame {
    private static final String FILE_PATH = "C:\\2023-24\\progtech_vedes\\wumpus\\src\\main\\java\\hu\\nye\\progtech\\wumpuszinputTest.txt";
    private static char[][] gameBoard;
    private static int heroRow;
    private static int heroColumn;

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        System.out.print("Enter your username: ");
        String username = scanner.nextLine();

        while (true) {
            System.out.println("\nWelcome, " + username + "!");
            System.out.println("\nMain Menu:");
            System.out.println("1. Play");
            System.out.println("2. Load Game Board from File");
            System.out.println("3. Exit");

            int choice = scanner.nextInt();
            scanner.nextLine(); // Consume the newline character

            switch (choice) {
                case 1 -> {
                    if (gameBoard != null){
                    playGame();
                    } else {
                        System.err.println("Please load the game board first.");
                    }
                }

                case 2 -> loadGameBoardFromFile();
                case 3 -> {
                    System.out.println("Goodbye, " + username + "!");
                    System.exit(0);
                }
                default -> System.err.println("Invalid choice. Please select a valid option.");
            }
        }
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
    }

    private static void playGame() {
        Scanner inputScanner = new Scanner(System.in);
        while (true) {
            displayGameBoard();

            System.out.print("Enter a move (W/A/S/D to move, Q to quit): ");
            String move = inputScanner.nextLine().toUpperCase();

            if (move.equals("Q")) {
                System.out.println("Game over. You quit.");
                break;
            }

            int newRow = heroRow;
            int newColumn = heroColumn;

            switch (move) {
                case "W" -> newRow--;
                case "S" -> newRow++;
                case "A" -> newColumn--;
                case "D" -> newColumn++;
                default -> {
                    System.err.println("Invalid move. Try again.");
                    continue;
                }
            }

            if (isValidMove(newRow, newColumn)) {
                char newLocation = gameBoard[newRow][newColumn];
                if (newLocation == 'P') {
                    System.out.println("You fell into a pit. Game over.");
                    break;
                } else if (newLocation == 'G') {
                    System.out.println("You found the gold. You win!");
                    break;
                } else if (newLocation == '_') {
                    gameBoard[heroRow][heroColumn] = '_';
                    heroRow = newRow;
                    heroColumn = newColumn;
                    gameBoard[heroRow][heroColumn] = 'H';
                } else {
                    System.err.println("You cannot move there. Try again.");
                }
            }
        }
    }

    private static boolean isValidMove(int newRow, int newColumn) {
        return newRow >= 0 && newRow < gameBoard.length && newColumn >= 0 && newColumn < gameBoard[newRow].length;
    }
}
