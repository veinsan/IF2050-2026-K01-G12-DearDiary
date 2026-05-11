package dedi;

import dedi.database.DatabaseConnection;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {

    private static Stage primaryStage;

    /**
     * Swaps the scene's root without touching the Stage — window size and
     * maximized state are preserved automatically across every page transition.
     */
    public static void switchRoot(Parent newRoot) {
        primaryStage.getScene().setRoot(newRoot);
    }

    /** Returns the primary stage (for logout resize-to-login). */
    public static Stage getPrimaryStage() {
        return primaryStage;
    }

    @Override
    public void start(Stage stage) throws Exception {
        primaryStage = stage;

        Parent root = FXMLLoader.load(
                getClass().getResource("/dedi/view/login.fxml"));

        // login.fxml declares stylesheets="@../css/login.css" on its root node,
        // so the Scene itself carries no stylesheet — when the root is swapped the
        // login styles leave with it and each screen's own styles take over.
        Scene scene = new Scene(root, 880, 560);

        stage.setTitle("DearDiary");
        stage.setResizable(true);
        stage.centerOnScreen();
        stage.setScene(scene);
        stage.show();
    }

    @Override
    public void stop() {
        DatabaseConnection.closeConnection();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
