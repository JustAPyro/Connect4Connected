// necessary imports
import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Stop;
import javafx.stage.Stage;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.Objects;

/**
 * Driver GUI is in charge of launching the initial application. It provides a small GUI that
 * will allow the player to select either "Host" (Server) or "Join" (Client), collects input
 * (Name and ip:port) before forwarding the information to either Connect4Server.java or
 * Connect4Client.java as required.
 */
public class DriverGUI extends Application
{

    // Logger used for debugging and collecting error information
    private static final Logger logger = LogManager.getLogger(DriverGUI.class);

    // Some variables declared for the sake of readability
    private final static boolean SERVER = true;
    private final static boolean CLIENT = false;

    // Main window (stage)
    private Stage primaryStage;

    // Server connection object
    SubThread connection;

    // This is the loop timer that handles wait time
    AnimationTimer waitLoop;

    // Object in/out streams for network communication, and listing thread to manage them
    ObjectStreamListener listener;
    ObjectOutputStream objectOut;
    ObjectInputStream objectIn;

    // The actual gamestate and the column pointer
    Connect4 game;
    int colPointer;

    /**
     * Provides the entry point to the program,
     * overridden from the Application class
     * @param primaryStage The main window provided
     */
    @Override
    public void start(Stage primaryStage) {

        // Save the stage for use later
        this.primaryStage = primaryStage;

        // Create a box to hold all the menu icons
        VBox menuBox = new VBox();

        // Create a new scene with that vbox
        Scene menuScene = new Scene(menuBox, 600, 600, Color.GREEN);

        // Set the back-fill for the menu
        menuScene.setFill(new LinearGradient(
                0, 0, 1, 1, true, //sizing
                CycleMethod.NO_CYCLE,                              //cycling
                new Stop(0, Color.LIGHTBLUE),                //colors
                new Stop(1, Color.CORNFLOWERBLUE))
        );

        // Set menu box to transparent background
        menuBox.setBackground(new Background(new BackgroundFill(Color.TRANSPARENT, CornerRadii.EMPTY, Insets.EMPTY)));

        // Set spacing in the menu-box
        menuBox.setSpacing(30);

        // Set for items to align in the center
        menuBox.setAlignment(Pos.CENTER);

        // Load the header image / logo
        Image image = new Image("images/Connect4.png");
        ImageView logoView = new ImageView(image);
        menuBox.getChildren().add(logoView);

        // Host game button
        Button hostButton = new Button("Host Game");
        hostButton.setPrefSize(400, 100);
        menuBox.getChildren().add(hostButton);

        // Join game button
        Button joinButton = new Button("Join Game");
        joinButton.setPrefSize(400, 100);
        menuBox.getChildren().add(joinButton);

        // Set the buttons to call the gameLauncher with appropriate client/server parameter
        joinButton.setOnAction(e -> gameLauncher(CLIENT));  // Pass client to game launcher
        hostButton.setOnAction(e -> gameLauncher(SERVER) ); // Pass server to game launcher

        // Set stage to scene
        primaryStage.setScene(menuScene);

        // Set title and display
        primaryStage.setTitle("Connect4Connected");
        primaryStage.show();

        // Notify the logger we've finished creating the launcher GUI
        logger.info("Created Launcher GUI.");

    }

    /**
     * Collects all the information required to launch the game.
     * Opens a separate window to get info about port/IP/name
     * Launches server thread and waits for connection
     *
     * @param hostOption DriverGUI.SERVER or DriverGUI.CLIENT represents if this will be a client or server
     */
    private void gameLauncher(boolean hostOption) {

        // Log that the game launcher has been called and which parameter was provided
        logger.info("Calling game launcher with \""
                + ((hostOption) ? "server" : "client")
                + "\" parameter.");

        // Create a new popup window
        Stage popup = new Stage();

        // Create a grid pane and configure it to look... pretty.
        GridPane loginGrid = new GridPane();
        loginGrid.setPadding(new Insets(20, 20, 20, 20));
        loginGrid.setHgap(5);
        loginGrid.setVgap(10);

        // Add a label and text box to get "username"
        Label nameLabel = new Label("Enter screen name: ");
        GridPane.setHalignment(nameLabel, HPos.RIGHT);
        loginGrid.add(nameLabel, 0, 0);

        // Get the name
        TextField nameField = new TextField();
        loginGrid.add(nameField, 1, 0);

        // Create two strings to be displayed depending on host option
        String serverInfo = "Please enter a port to host on: ";
        String clientInfo = "Please enter a IP address to join: ";

        // Create a new label and configure it based on hosting option
        Label enterLabel = new Label();
        if (hostOption == CLIENT)
            enterLabel.setText(clientInfo);
        if (hostOption == SERVER)
            enterLabel.setText(serverInfo);
        GridPane.setHalignment(enterLabel, HPos.RIGHT);
        loginGrid.add(enterLabel, 0, 1);

        // Add a text field to collect configuration options
        TextField infoField = new TextField();
        loginGrid.add(infoField, 1, 1);

        // And associated label
        Label checkLabel = new Label("Press Confirm when Done");
        GridPane.setHalignment(checkLabel, HPos.CENTER);
        loginGrid.add(checkLabel, 0, 2, 2, 1);

        // Button to cancel with on-action of closing menu
        Button cancelButton = new Button("Cancel");
        cancelButton.setOnAction(e -> popup.close());
        GridPane.setHalignment(cancelButton, HPos.CENTER);
        loginGrid.add(cancelButton, 0, 3);

        // Button to confirm launches the connect method
        Button confirmButton = new Button("Confirm");
        confirmButton.setOnAction(e -> {

            // Get the name and info they would like to use
            String name = nameField.getText();
            String info = infoField.getText();

            // Check to see if it's valid
            String isValid = checkValid(name, info, hostOption);

            // If the string returned is "valid" then start connection
            if (Objects.equals(isValid, "valid"))
                connect(popup, hostOption, name, info);
            else {
                // Otherwise, set the label to display the reason it failed and set color to red
                checkLabel.setText(isValid); // Other-wise set the label to the reason it's not valid
                checkLabel.setTextFill(Color.RED);
            }

        });

        // Set the alignment of the confirm button and add it
        GridPane.setHalignment(confirmButton, HPos.CENTER);
        loginGrid.add(confirmButton, 1, 3);

        // Set the scene, title and show
        popup.setScene(new Scene(loginGrid));
        popup.setTitle("Enter your information");
        popup.show();

        // Log that the dialog was created
        logger.info("Created input dialog popup.");

    }

    /**
     * This method records the screen name entered and then uses the popup stage provided
     * to show a loading bar while we either host or attempt connection.
     *
     * @param popup The dialog popup.
     * @param hostOption true for server connection or false for client connection.
     * @param name The screen name.
     * @param info if client this is an ip and port, otherwise just port to host on
     */
    private void connect(Stage popup, boolean hostOption, String name, String info) {

        // Log information about attempted connection
        logger.info("Attempting to create "
                + ((hostOption) ? "server" : "client")
                + " connection at "
                + info);

        // Create a new box and set it to be the scene
        VBox box = new VBox();

        // Align to center and add padding/spacing around edges
        box.setAlignment(Pos.CENTER);
        box.setPadding(new Insets(15, 15, 15, 15));
        box.setSpacing(15);

        // Add label
        Label waitingLabel = new Label("Waiting for client to connect...");
        box.getChildren().add(waitingLabel);

        // Add indicator
        ProgressBar pb = new ProgressBar();
        pb.setPrefSize(150, 20);
        box.getChildren().add(pb);

        // Set the title of the popup, and configure size
        popup.setTitle("");
        popup.getScene().setRoot(box);
        popup.sizeToScene();

        // If we are hosting a server
        if (hostOption == SERVER) {

            // Log that we're following server path
            logger.info("Executing server logic.");

            connection = new Connect4Server(Integer.parseInt(info));
            Thread connectionThread = new Thread(connection);
            connectionThread.start();
        }

        // If we are a client looking for connection
        if (hostOption == CLIENT) {

            // Log that we're following client path
            logger.info("Executing client logic.");

            connection = new Connect4Client(info);
            Thread connectionThread = new Thread(connection);
            connectionThread.start();
        }

        logger.info("Searching for connections.");
        waitLoop = new AnimationTimer() {
            @Override
            public void handle(long now) {

                if (connection.isConnected()) {

                    popup.close();
                    waitLoop.stop();
                    try {

                        // Open game using that connection and the screen name we picked
                        openGame(connection.getConnection(), hostOption, name);

                    }
                    catch (IOException | ClassNotFoundException e) {

                        // Log the error
                        logger.error(e.getMessage());

                    }
                }
            }
        };
        waitLoop.start();

    }

    /**
     * Establishes the beginning of a game, provided a connection and your name
     *
     * @param connection The connection to server/client of the other player
     * @param name Your screen name
     * @throws IOException if an error occurs during IO
     * @throws ClassNotFoundException if the server/client sends an unknown object class
     */
    public void openGame(Socket connection, boolean hostOption, String name) throws IOException, ClassNotFoundException {

        String opponentName = "";
        // If we're a client
        if (hostOption == CLIENT) {

            // If we're a client we need to send the first message, so create the out, write our name, and flush
            objectOut = new ObjectOutputStream(connection.getOutputStream());
            objectOut.writeObject(name);
            objectOut.flush();

            // Log that we've sent a request message
            logger.info("Sent a message to server providing our name and requesting a game.");

            // only THEN create the object in and read back (I don't know why it works this way, but any other way breaks)
            objectIn = new ObjectInputStream(connection.getInputStream());
            opponentName = objectIn.readObject().toString();

            // Since we're a client we can't control game state, so we need to also read back the game object
            game = (Connect4) objectIn.readObject();

            // Log successful read of data
            logger.info("Received back opponents name and initial gamestate.");

            // Create a new listener to listen for further information from server
            listener = new ObjectStreamListener(objectIn, true);

        }
        // If we're a server
        if (hostOption == SERVER) {

            // If we're a server we can initialize our streams at the same time
            objectIn = new ObjectInputStream(connection.getInputStream());
            objectOut = new ObjectOutputStream(connection.getOutputStream());

            // Let's read in the other guys name, then send ours back
            opponentName = objectIn.readObject().toString();
            objectOut.writeObject(name);
            objectOut.flush();

            // Log that we've received a request
            logger.info("Read a name and request to play.");

            // Since we're a server we also need to send back the starting game object
            game = new Connect4(name, opponentName);
            objectOut.writeObject(game);
            objectOut.reset();
            objectOut.flush();

            // Log that we've successfully returned our name and game-state
            logger.info("Sent back our screen-name and initial game-state.");

            // Create a new listener to collect future input from client
            listener = new ObjectStreamListener(objectIn, false);
        }


        // Create a special listener object to watch for incoming data while we handle the GUI here
        Thread listenThread = new Thread(listener);
        listenThread.start(); // Start that on a different thread so our GUI doesn't get blocked and crash

        // Set up the UI
        VBox root = new VBox();
        primaryStage.getScene().setRoot(root);

        // Add a canvas to the UI
        Canvas canvas = new Canvas(800, 650);
        double hSpace = (double) 650/(game.getBoardX()+1);

        // Handle calculating which column the mouse is mousing over
        canvas.setOnMouseMoved(e -> {
            double x = e.getX();
            for (int i = 0; i < game.getBoardX(); i++) {
                if (i*hSpace < x && x < (i+1)*hSpace) {
                    colPointer = i;
                }
            }
        });

        // On click, check if it's our turns (depending on if we're client or server, if so play the move
        canvas.setOnMouseClicked(e -> {

            // If it's our turn and we're the server
            if (!game.p1Turn && hostOption == SERVER) {
                try {
                    game.insert(colPointer); // Make the play
                    objectOut.writeObject(game); // Send updated game state
                    objectOut.reset();  // Reset the object stream
                    objectOut.flush();  // then flush it
                }
                catch (IOException ioException) {

                    // Log the error
                    logger.error(ioException.toString());
                }
            }

            // If it's our turn and we're the client
            if (game.p1Turn && hostOption == CLIENT) {
                try {
                    objectOut.writeInt(colPointer); // Send the column we placed it on
                    objectOut.flush();              // Flush the object afterwards
                }
                catch (IOException ex)
                {
                    // Log the IOException if it occurs
                    logger.error(ex.toString());
                }

            }
        });

        // Add a label with both names and the canvas to the stage
        //root.getChildren().add(new Label(name + " versus " + opponentName));
        root.getChildren().add(canvas);

        // Size the window to the scene
        primaryStage.setMinWidth(800);
        primaryStage.setMinHeight(650);

        // Get the graphics context and draw the current game-state
        GraphicsContext gc = canvas.getGraphicsContext2D();
        game.draw(gc);

        // Start the animation loop
        AnimationTimer gameLoop = new AnimationTimer() {
            @Override
            public void handle(long now) {

                // Draw the game
                game.draw(gc);

                // If it's our turn, draw the pointer
                if ((!game.p1Turn && hostOption == SERVER) || (game.p1Turn && hostOption == CLIENT))
                    gc.fillRect((hSpace*(colPointer+1))-5, 10, 10, 20);

                // Wait for new information
                if (listener.ready()) {
                    try {

                        // If we're the server and we get information
                        if (hostOption == SERVER) {

                            // Get the play from the listener
                            int play = listener.get();

                            // Insert it into the game
                            game.insert(play);

                            // Write back the results to the client
                            objectOut.writeObject(game);
                            objectOut.reset();
                            objectOut.flush();
                        }
                        // If we're the client and we get information
                        if (hostOption == CLIENT) {
                            game = (Connect4) listener.getObj();
                        }
                    }
                    catch (Exception e) {

                        // Log the exception
                        logger.error(e.getMessage());

                    }
                }
            }
        };

        // Start the game loop
        gameLoop.start();

    }

    /**
     * Checks to see if provided information is valid to start a game.
     * @param name The screen-name of the player
     * @param info Information about target location for connection
     * @param hostOption Server or Client
     * @return "valid" if all information is good, otherwise an error message to display
     */
    private static String checkValid(String name, String info, boolean hostOption) {

        logger.info("Called \"checkValid\" to validate input.");

        if (name.length() < 1) {
            logger.info("Entered a invalid name: name length less than 1.");
            return "Please enter a screen name.";
        }
        else if (hostOption == CLIENT) {
            if (!info.contains(":")) {
                logger.info("Entered invalid info: did not contain ':'");
                return "Please enter server info in ip:port format";
            }
        }

        return "valid";
    }

}
