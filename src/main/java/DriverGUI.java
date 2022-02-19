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
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;


import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.Objects;


public class DriverGUI extends Application
{

    private static Logger logger = LogManager.getLogger(DriverGUI.class);

    Stage primaryStage;
    Stage popup;

    Label updateLabel;
    SubThread connection;
    AnimationTimer waitLoop;

    ObjectOutputStream objectOut;
    ObjectInputStream objectIn;

    final static boolean SERVER = true;
    final static boolean CLIENT = false;

    @Override
    public void start(Stage primaryStage) {
        logger.error("HEY!");

        // Save the stage for use later
        this.primaryStage = primaryStage;

        // Create a box to hold all the menu icons
        VBox menuBox = new VBox();

        // Create a new scene with that vbox
        Scene menuScene = new Scene(menuBox, 600, 600, Color.GREEN);

        menuScene.setFill(new LinearGradient(
                0, 0, 1, 1, true,                      //sizing
                CycleMethod.NO_CYCLE,                  //cycling
                new Stop(0, Color.LIGHTBLUE),     //colors
                new Stop(1, Color.CORNFLOWERBLUE))
        );

        // Set menubox to transparent background
        menuBox.setBackground(new Background(new BackgroundFill(Color.TRANSPARENT, CornerRadii.EMPTY, Insets.EMPTY)));

        // Set spacing in the menubox
        menuBox.setSpacing(30);

        // Set for items to align in the cneter
        menuBox.setAlignment(Pos.CENTER);

        // Load the header image
        Image image = new Image("Images/Connect4.png");
        ImageView logoView = new ImageView(image);
        menuBox.getChildren().add(logoView);

        // Host game button
        Button hostButton = new Button("Host Game");
        hostButton.setOnAction(e -> gameLauncher(SERVER) );
        hostButton.setPrefSize(400, 100);
        menuBox.getChildren().add(hostButton);

        // Join game button
        Button joinButton = new Button("Join Game");
        joinButton.setOnAction(e -> gameLauncher(CLIENT));
        joinButton.setPrefSize(400, 100);
        menuBox.getChildren().add(joinButton);

        // Set stage to scene
        primaryStage.setScene(menuScene);

        // Set title and display
        primaryStage.setTitle("Connect4Connected");
        primaryStage.show();

    }

    /**
     * Collects all the information required to launch the game.
     * Opens a separate window to get info about port/IP/name
     * Launches server thread and waits for connection
     *
     * @param hostOption DriverGUI.SERVER or DriverGUI.CLIENT represents if this will be a client or server
     */
    private void gameLauncher(boolean hostOption) {

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
                // Otherwise, set the label to display the reson it failed and set color to red
                checkLabel.setText(isValid); // Otherwise set the label to the reason it's not valid
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

    }

    private void connect(Stage popup, boolean hostOption, String name, String info) {

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

        popup.setTitle("");
        popup.getScene().setRoot(box);
        popup.sizeToScene();

        if (hostOption == SERVER) {
            connection = new Connect4Server(Integer.parseInt(info));
            Thread connectionThread = new Thread(connection);
            connectionThread.start();
        }

        if (hostOption == CLIENT) {
            System.out.println("Client not configured");
        }

        waitLoop = new AnimationTimer() {
            @Override
            public void handle(long now) {
                if (connection.isConnected()) {
                    popup.close();
                    waitLoop.stop();
                    try {
                        System.out.println(connection.getConnection());
                        openGame(connection.getConnection(), name);
                    } catch (IOException | ClassNotFoundException e) {
                        e.printStackTrace();
                    }
                }
            }
        };
        waitLoop.start();

    }

    public void openGame(Socket connection, String name) throws IOException, ClassNotFoundException {

        objectIn = new ObjectInputStream(connection.getInputStream());
        objectOut = new ObjectOutputStream(connection.getOutputStream());


        // Read in the opponents screen name and write back ours to complete init
        String opponentName = objectIn.readObject().toString();
        objectOut.writeObject(name);

        System.out.println(opponentName);
        Connect4 game = new Connect4(name, opponentName);

        VBox root = new VBox();
        primaryStage.getScene().setRoot(root);

        Canvas canvas = new Canvas(650, 650);
        root.getChildren().add(new Label(name + " versus " + opponentName));
        root.getChildren().add(canvas);

        GraphicsContext gc = canvas.getGraphicsContext2D();
        gc.strokeRect(0, 0, canvas.getWidth(), canvas.getHeight());
        game.draw(gc);

    }

    private static String checkValid(String name, String info, boolean hostOption) {

        // TODO: This should check name and info for validity based on host option and return "valid" if valid, else error

        return "valid";
    }



}
