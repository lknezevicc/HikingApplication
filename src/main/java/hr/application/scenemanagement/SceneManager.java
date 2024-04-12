package hr.application.scenemanagement;

import hr.application.entities.SharedObject;
import hr.application.exceptions.SceneLoadingException;
import hr.application.hikingapplication.Main;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URL;
import java.util.Properties;

public class SceneManager {
    private static final Logger logger = LoggerFactory.getLogger(SceneManager.class);
    private static final URL STYLE_URL = SceneManager.class.getResource("/style.css");
    private static SceneManager instance;
    private final Properties scenesPath;
    private Stage stage;
    private SceneType currentScene;
    private SceneType previousScene;
    private SharedObject<?> sharedObject;

    private SceneManager() {
        scenesPath = new Properties();
        try {
            scenesPath.load(Main.class.getResourceAsStream("/scenes.properties"));
        } catch (IOException e) {
            logger.error("Error while loading scenes properties!, [{}]", e.getMessage());
        }
    }

    public static SceneManager getInstance() {
        if (instance == null) {
            instance = new SceneManager();
        }
        return instance;
    }

    public void setScene(SceneType sceneType) {
        if (stage == null) {
            logger.error("Stage is not set!");
            return;
        }

        if (currentScene != null) previousScene = currentScene;
        currentScene = sceneType;

        String scenePath = scenesPath.getProperty(sceneType.name());
        if (scenePath != null) {
            try {
                Scene scene = loadScene(scenePath);
                scene.getStylesheets().add(STYLE_URL.toExternalForm());
                stage.setScene(scene);
            } catch (IOException e) {
                logger.error("Error while loading scene!, [{}]", e.getMessage());
                throw new SceneLoadingException("Error while loading scene!");
            }
        }
    }

    public void show() {
        stage.show();
    }

    private Scene loadScene(String fxmlPath) throws IOException {
        FXMLLoader loader = new FXMLLoader(Main.class.getResource(fxmlPath));
        return new Scene(loader.load());
    }

    public void setStage(Stage stage) {
        this.stage = stage;
        this.stage.setTitle("Hiking Manager");
    }

    public void setResizable(boolean resizable) {
        stage.setResizable(resizable);
    }

    public <T> void setSharedObject(T object) {
        this.sharedObject = new SharedObject<>(object);
    }

    public <T> T getSharedObject(Class<T> type) {
        return type.cast(sharedObject.get());
    }

    public void clearSharedObject() {
        this.sharedObject = null;
    }

    public void setPreviousScene() {
        if (previousScene != null) setScene(previousScene);
    }

}
