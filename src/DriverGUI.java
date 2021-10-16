import javafx.application.Application;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Stop;
import javafx.stage.Stage;

import java.util.Objects;


public class DriverGUI extends Application
{


    final static boolean SERVER = true;
    final static boolean CLIENT = false;

    @Override
    public void start(Stage primaryStage) {

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



        try {
            Image image = new Image("Images/Connect4.png");
            ImageView logoView = new ImageView(image);
            menuBox.getChildren().add(logoView);
        } finally {
            System.out.println("ERROR: COULDN'T LOAD IMAGES");
        }


        Button hostButton = new Button("Host Game");
        hostButton.setOnAction(e -> gameLauncher(SERVER) );
        hostButton.setPrefSize(400, 100);
        menuBox.getChildren().add(hostButton);

        Button joinButton = new Button("Join Game");
        joinButton.setOnAction(e -> gameLauncher(CLIENT));
        joinButton.setPrefSize(400, 100);
        menuBox.getChildren().add(joinButton);



        primaryStage.setScene(menuScene);



        // Set title and display
        primaryStage.setTitle("Connect4Connected");
        primaryStage.show();

    }

    private void gameLauncher(boolean hostOption) {

        // Create a new popup window
        Stage popup = new Stage();

        // Create a grid pane and configure it to look... pretty.
        GridPane loginGrid = new GridPane();
        loginGrid.setPadding(new Insets(20, 20, 20, 20));
        loginGrid.setHgap(5);
        loginGrid.setVgap(10);

        // Add a label and textbox to get "username"
        Label nameLabel = new Label("Enter screen name: ");
        GridPane.setHalignment(nameLabel, HPos.RIGHT);
        loginGrid.add(nameLabel, 0, 0);

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
                connect(popup, name, info);
            else {
                checkLabel.setText(isValid); // Otherwise set the label to the reason it's not valid
                checkLabel.setTextFill(Color.RED);
            }



        });
        GridPane.setHalignment(confirmButton, HPos.CENTER);
        loginGrid.add(confirmButton, 1, 3);

        popup.setScene(new Scene(loginGrid));
        popup.setTitle("Sup");
        popup.show();


    }

    private void connect(Stage popup, String name, String info) {
        VBox box = new VBox();
        popup.getScene().setRoot(box);

        Connect4Server server = new Connect4Server(this, name, Integer.parseInt(info));
        Thread serverThread = new Thread(server);
        serverThread.start();
    }

    public void display(String str)
    {

    }

    private static String checkValid(String name, String info, boolean hostOption) {
        String isValid = "valid";

        if (name.isEmpty())
            isValid = "Please Enter a name.";
        else if (info.isEmpty())
            isValid = "Please enter connection information.";




        // If this is a client
        if (hostOption == CLIENT) {
            if (!isInteger(info))
                return "Hosting information must be an integer.";

            int infoInt = Integer.parseInt(info); // Convert to int now that we know it's safe

            if (infoInt < 1023|| infoInt > 65535)
                isValid = "Port must be in range 1023 = 65535";
        }


        return isValid;
    }

    // Credit to stack overview for this method
    // https://stackoverflow.com/questions/237159/whats-the-best-way-to-check-if-a-string-represents-an-integer-in-java
    public static boolean isInteger(String str) {
        if (str == null) {
            return false;
        }
        int length = str.length();
        if (length == 0) {
            return false;
        }
        int i = 0;
        if (str.charAt(0) == '-') {
            if (length == 1) {
                return false;
            }
            i = 1;
        }
        for (; i < length; i++) {
            char c = str.charAt(i);
            if (c < '0' || c > '9') {
                return false;
            }
        }
        return true;
    }
}
