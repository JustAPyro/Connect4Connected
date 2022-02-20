import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Stop;
import java.io.Serializable;

/**
 * The class contains all information relevant to the actual game-state, and
 * methods implementing how the game-state can be rendered.
 */
public class Connect4 implements Serializable
{

    // Width and height of the board (Standard is 6x7)
    private final int boardX = 7; private final int boardY = 6;

    // Create strings to save the players name in this game
    private final String playerOne, playerTwo;

    // Size for circles
    private static final double circleSize = 50;

    // Create a boolean to track whose turn it is (if true then player one's turn)
    boolean p1Turn;

    // Declaring the variable for the game board
    private final int[][] board;

    /**
     * Constructor creates a game, initializing it with both players names
     * @param p1 Player 1's name
     * @param p2 Player 2's name
     */
    public Connect4(String p1, String p2) {

        // Set it to player one's turn
        p1Turn = true;

        // Set the players names
        this.playerOne = p1;
        this.playerTwo = p2;

        // Initializing the board
        board = new int[boardX][boardY];

    }

    /**
     * Allows access to the size of the board in units
     * @return The number of spaces horizontally on the board
     */
    public int getBoardX() {
        return boardX;
    }


    /**
     * Allows you to add pieces to the game board to try to 'Connect Four'
     *
     * @param location The location you want to set the piece (0-start indexed)
     * @throws IndexOutOfBoundsException if you try to insert a piece at an invalid point
     */
    public void insert(int location) {

        // First, check if the given location is out of bounds
        if (location < 0 || location > boardX)

            // Throw an IndexOutOfBoundsException
            throw new IndexOutOfBoundsException("Tried to insert piece at invalid point!");

        // Create a variable to track the correct insertion height
        int insertHeight = boardY - 1;

        // Starting from the bottom of the board, as long as there's a piece at insertHeight
        while (board[location][insertHeight] != 0) {

            // Increment insertHeight to move one up
            insertHeight--;

        }

        // Get the player token by using ternary operator...
        int token = p1Turn? 1 : 2;

        // Finally, once we have the insert height set that location to belong to the given player
        board[location][insertHeight] = token;

        // Set it to the other player's turn
        p1Turn = !p1Turn;

    }

    /**
     * For use printing the gamestate.
     *
     * @return Gamestate representation as a multi-line string
     */
    @Override
    public String toString() {

        // Saving the value for the newline element from OS
        String newLine = System.getProperty("line.separator");

        // This string will represent the gamestate
        StringBuilder boardString;

        // If it's player one's turn
        if (p1Turn) {
            // Start the string with a notification of who's turn it is
            boardString = new StringBuilder(playerOne + "'s Turn: " + newLine);
        }
        else {
            // Start the string with a notification of who's turn it is using player two's name
            boardString = new StringBuilder(playerTwo + "'s Turn: " + newLine);
        }


        // For each row
        for (int y = 0; y < boardY; y++) {

            // Create an element to store the line
            StringBuilder line = new StringBuilder();

            // For each column item of each row
            for (int x = 0; x < boardX; x++) {

                // Add the correct value and a space to the line
                line.append(board[x][y]).append(" ");

            }

            // add the line to the final string
            boardString.append(line);

            // and then add the newline element
            boardString.append(newLine);

        }

        // Return the completed multiline string representing gamestate
        return boardString.toString();
    }



    /**
     * Draws the visual representation of the game with the provided graphics context.
     * This will be rendered on the associated canvas and is based on instance variables.
     * @param gc The graphics context the game will be drawn on.
     */
    public void draw(GraphicsContext gc) {

        // Colors for each player
        Color colorOne, colorTwo, colorNone;

        // Declaring the necessary colors
        colorOne = Color.YELLOW;
        colorTwo = Color.RED;
        colorNone = Color.LIGHTGRAY;

        // Get the canvas and associated measurements
        Canvas c = gc.getCanvas();
        double width = c.getWidth();
        double height = c.getHeight();

        // Calculate spacing based on board dimensions and sizes.
        double horizontalSpacing = width/(boardX +1);
        double verticalSpacing = height/(boardY +1);

        // Draws the gradient background of the game using the below local variables
        double                offset1 = .1;
        double                offset2 = .9;
        Color                 color1  = Color.rgb(0,0,255);
        Color                 color2  = Color.rgb(0,200,255);
        Stop[]                stops1  = new Stop[] {new Stop(offset1, color1), new Stop(offset2, color2)};
        gc.setFill(new LinearGradient(0, 0, .2, 1.4, true, CycleMethod.NO_CYCLE, stops1));
        gc.fillRect(0, 0, width, height);

        // Iterate through and draw the circles of the board based on their values
        // TODO: Could replace these three if statements with one hashmap.
        for (int y = 1; y <= boardY; y++) {
            for (int x = 1; x <= boardX; x++) {
                if (board[x-1][y-1] == 0)
                    drawCirc(gc, colorNone, x * horizontalSpacing, y * verticalSpacing);
                if (board[x-1][y-1] == 1)
                    drawCirc(gc, colorOne, x * horizontalSpacing, y * verticalSpacing);
                if (board[x-1][y-1] == 2)
                    drawCirc(gc, colorTwo, x * horizontalSpacing, y * verticalSpacing);
            }
        }
    }

    // Helper method draws a circle at location x, y using instance variable circleSize and color
    private void drawCirc(GraphicsContext gc, Color color, double x, double y) {

        // Set the color of graphics context
        gc.setFill(color);

        // Fill an oval using the provided parameters
        gc.fillOval(x-(circleSize/2), y-(circleSize/2), circleSize, circleSize);

    }
}