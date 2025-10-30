package forumApp;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * JavaFX entry point that delegates to {@link MainWithGUI}. Keeping the Application subclass
 * separate avoids reflective construction issues when other parts of the system instantiate
 * {@code MainWithGUI} directly.
 */
public class ForumAppLauncher extends Application {

    @Override
    public void start(Stage stage) {
        MainWithGUI forum = new MainWithGUI();
        stage.setTitle("Student Forum");
        stage.setScene(new Scene(forum.getRootView(), 960, 640));
        stage.show();
    }

    /**
     * Opens a standalone window using a fresh instance of {@link MainWithGUI}. Safe to call from
     * any thread while the JavaFX runtime is active.
     */
    public static void showStandaloneWindow() {
        Platform.runLater(() -> {
            Stage stage = new Stage();
            MainWithGUI forum = new MainWithGUI();
            stage.setTitle("Student Forum");
            stage.setScene(new Scene(forum.getRootView(), 960, 640));
            stage.show();
        });
    }

    public static void main(String[] args) {
        launch(args);
    }
}
