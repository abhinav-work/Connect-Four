package sample;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

import static sample.Controller.PlayerOne;
import static sample.Controller.PlayerTwo;


public class Main extends Application {
    private Controller controller;
    @Override
    public void start(Stage primaryStage) throws Exception
    {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("Game_Layout.fxml"));
        GridPane rootNode = loader.load();

        controller = loader.getController();
        controller.createPlaygroud();

        controller.setNames.setOnAction(event ->
                {
                    PlayerOne = controller.textFieldOne.getText();
                    PlayerTwo = controller.textFieldTwo.getText();
                    controller.playerLabel.setText(PlayerOne);
                }
        );

        MenuBar menuBar = createMenu();
        Pane menuPane = (Pane) rootNode.getChildren().get(0);
        menuBar.prefWidthProperty().bind(primaryStage.widthProperty());
        menuPane.getChildren().addAll(menuBar);

        Scene scene = new Scene(rootNode);

        primaryStage.setScene(scene);
        primaryStage.setTitle("Game Begins");
        primaryStage.show();
    }

    private MenuBar createMenu()
    {
        Menu fileMenu = new Menu("File");
        MenuItem newMenu = new MenuItem("New Game");
        newMenu.setOnAction(event ->
        {
            PlayerOne = "Player One";
            PlayerTwo = "Player Two";
            controller.playerLabel.setText(PlayerOne);
            controller.textFieldOne.clear();
            controller.textFieldTwo.clear();
            controller.reset();
        });
        MenuItem resetMenu = new MenuItem("Reset Game");
        resetMenu.setOnAction(event -> controller.reset());
        MenuItem exitMenu = new MenuItem("Exit Game");
        exitMenu.setOnAction(event -> exitApp());
        SeparatorMenuItem separatorMenuItem = new SeparatorMenuItem();
        fileMenu.getItems().addAll(newMenu, resetMenu, separatorMenuItem, exitMenu);

        Menu helpMenu = new Menu("Help");
        MenuItem aboutMenu = new MenuItem("About Connect4");
        aboutMenu.setOnAction(event -> connectDialog());
        MenuItem devMenu = new MenuItem("About Me");
        devMenu.setOnAction(event -> meDialog());

        helpMenu.getItems().addAll(aboutMenu,devMenu);


        MenuBar menuBar = new MenuBar();
        menuBar.getMenus().addAll(fileMenu, helpMenu);
        return menuBar;
    }

    private void meDialog()
    {
        Alert me = new Alert(Alert.AlertType.INFORMATION);
        me.setTitle("About the Developer");
        me.setHeaderText("Abhinav Sharma");
        me.setContentText("This is my first java based game, I hope you all like it");
        me.show();
    }

    private void connectDialog()
    {
        Alert connect = new Alert(Alert.AlertType.INFORMATION);
        connect.setTitle("About Connect Four");
        connect.setHeaderText("How to play Connect Four");
        connect.setContentText("Connect Four is a two-player connection game in which the players first choose a color and then take turns dropping colored discs from the top into a seven-column, six-row vertically suspended grid. The pieces fall straight down, occupying the next available space within the column. The objective of the game is to be the first to form a horizontal, vertical, or diagonal line of four of one's own discs. Connect Four is a solved game. The first player can always win by playing the right moves.");
        connect.show();
    }

    public void exitApp()
    {
        Platform.exit();
        System.exit(0);
    }

    public static void main(String[] args)
    {
        launch(args);
    }
}
