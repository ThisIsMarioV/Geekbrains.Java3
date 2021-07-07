package first.chat.client;

import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception { // создание окна
        FXMLLoader fxmlLoader = new FXMLLoader();
        Parent root =fxmlLoader.load(getClass().getResource("/chatSample.fxml").openStream());
        Controller controller = (Controller) fxmlLoader.getController();
        primaryStage.setTitle("Chat Window");
        primaryStage.setOnCloseRequest(event -> controller.sendCloseRequest());
        primaryStage.setScene(new Scene(root, 600, 400));
        primaryStage.show();

    }


    public static void main(String[] args) {
        launch(args);
    }
}
