package workshop05code;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.NoSuchElementException;
import java.util.Scanner;
//Included for the logging exercise
import java.io.FileInputStream;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;

/**
 *
 * @author sqlitetutorial.net
 */
public class App {
    // Start code for logging exercise
    static {
        // must set before the Logger
        // loads logging.properties from the classpath
        try {// resources\logging.properties
            LogManager.getLogManager().readConfiguration(new FileInputStream("resources/logging.properties"));
        } catch (SecurityException | IOException e1) {
            e1.printStackTrace();
        }
    }

    private static final Logger logger = Logger.getLogger(App.class.getName());
    // End code for logging exercise
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        SQLiteConnectionManager wordleDatabaseConnection = new SQLiteConnectionManager("words.db");
    
        wordleDatabaseConnection.createNewDatabase("words.db");
        if (wordleDatabaseConnection.checkIfConnectionDefined()) {
            System.out.println("Wordle created and connected.");
        } else {
            System.out.println("Not able to connect. Sorry!");
            return;
        }
        if (wordleDatabaseConnection.createWordleTables()) {
            System.out.println("Wordle structures in place.");
        } else {
            System.out.println("Not able to launch. Sorry!");
            return;
        }
    
        try (BufferedReader br = new BufferedReader(new FileReader("resources/data.txt"))) {
            String line;
            int i = 1;
            while ((line = br.readLine()) != null) {
                if (line.matches("^[a-z]{4}$")) {
                    wordleDatabaseConnection.addValidWord(i, line);
                    logger.fine("Added valid word: " + line);
                    i++;
                } else {
                    System.out.println("Ignored invalid input from file: " + line);
                    logger.info("Ignored invalid input from file: " + line);
                }
            }
        } catch (IOException e) {
            System.out.println("Not able to load. Sorry!");
            logger.log(Level.WARNING, "IOException while reading word list file", e);
            return;
        }
        
    
        try (Scanner scanner = new Scanner(System.in)) {
            System.out.print("Enter a 4 letter word for a guess or q to quit: ");
            String guess = scanner.nextLine();
        
            while (!guess.equals("q")) {
                if (!guess.matches("^[a-z]{4}$")) {
                    System.out.println("Invalid input. Please enter exactly 4 lowercase letters (a-z).\n");
                    logger.info("Rejected invalid user input: " + guess);
                } else {
                    logger.fine("User guessed: " + guess);
                    System.out.println("You've guessed '" + guess + "'.");
        
                    if (wordleDatabaseConnection.isValidWord(guess)) {
                        System.out.println("Success! It is in the list.\n");
                        logger.fine("Word found in DB: " + guess);
                    } else {
                        System.out.println("Sorry. This word is NOT in the list.\n");
                        logger.fine("Word NOT found in DB: " + guess);
                    }
                }
        
                System.out.print("Enter a 4 letter word for a guess or q to quit: ");
                guess = scanner.nextLine();
            }
        } catch (NoSuchElementException | IllegalStateException e) {
            logger.log(Level.SEVERE, "Scanner failed to read user input", e);
        }        
    }    
}