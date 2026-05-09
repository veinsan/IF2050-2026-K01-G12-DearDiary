package dedi;

import dedi.database.DatabaseConnection;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * JavaFX entry point for the DeDi desktop application.
 *
 * <p>Loads {@code login.fxml} on startup and releases the shared
 * {@link DatabaseConnection} on shutdown.
 */
public class Main extends Application {

    @Override
    public void start(Stage stage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("/dedi/view/login.fxml"));
        stage.setTitle("DearDiary");
        stage.setScene(new Scene(root));
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
