package hr.application.hikingapplication;

import hr.application.scenemanagement.SceneManager;
import hr.application.scenemanagement.SceneType;
import javafx.application.Application;
import javafx.stage.Stage;


public class Main extends Application {

    @Override
    public void start(Stage stage) {
        SceneManager.getInstance().setStage(stage);
        SceneManager.getInstance().setResizable(false);
        SceneManager.getInstance().setScene(SceneType.LOGIN);
        SceneManager.getInstance().show();
    }

    public static void main(String[] args) {
        launch();
    }

}