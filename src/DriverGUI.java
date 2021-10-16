import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Stop;
import javafx.stage.Stage;


import java.io.FileInputStream;
import java.io.InputStream;

public class DriverGUI extends Application
{


    @Override
    public void start(Stage primaryStage) throws Exception {

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

        }


        Button hostButton = new Button("Host Game");
        hostButton.setPrefSize(400, 100);
        menuBox.getChildren().add(hostButton);

        Button joinButton = new Button("Join Game");
        joinButton.setPrefSize(400, 100);
        menuBox.getChildren().add(joinButton);



        primaryStage.setScene(menuScene);



        // Set title and display
        primaryStage.setTitle("Connect4Connected");
        primaryStage.show();

    }
}
