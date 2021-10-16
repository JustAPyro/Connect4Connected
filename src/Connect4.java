public class Connect4
{

    // Width and height of the board (Standard is 6x7)
    private final int boardx = 7; private final int boardy = 6;

    // Create strings to save the players name in this game
    private String playerOne, playerTwo;

    // Create a boolean to track who's turn it is (if true then player one's turn)
    boolean p1Turn;

    // Declaring the variable for the game board
    private int[][] board;

    // Constructor Class
    public Connect4(String p1, String p2) {

        // Set it to player one's turn
        p1Turn = true;

        // Set the players names
        this.playerOne = p1;
        this.playerTwo = p2;

        // Initializing the board
        board = new int[boardx][boardy];

    }

    /**
     * Allows you to add pieces to the game board to try to 'Connect Four'
     *
     * @param location The location you want to set the piece (0-start indexed)
     * @throws IndexOutOfBoundsException if you try to insert a piece at an invalid point
     */
    public void insert(int location) {

        // First, check if the given location is out of bounds
        if (location < 0 || location > boardx)

            // Throw an IndexOutOfBoundsException
            throw new IndexOutOfBoundsException("Tried to insert piece at invalid point!");

        // Create a variable to track the correct insertion height
        int insertHeight = boardy - 1;

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
        for (int y = 0; y < boardy; y++) {

            // Create an element to store the line
            StringBuilder line = new StringBuilder();

            // For each column item of each row
            for (int x = 0; x < boardx; x++) {

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

}
