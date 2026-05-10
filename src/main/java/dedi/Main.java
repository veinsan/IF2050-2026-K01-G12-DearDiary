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

        FXMLLoader loader = new FXMLLoader(
                getClass().getResource("/dedi/view/login.fxml")
        );

        Parent root = loader.load();

        Scene scene = new Scene(root, 880, 560);

        scene.getStylesheets().add(
                getClass()
                        .getResource("/dedi/css/login.css")
                        .toExternalForm()
        );

        stage.setTitle("DearDiary");

        stage.setResizable(false);
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
