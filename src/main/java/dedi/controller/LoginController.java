package dedi.controller;

import dedi.model.AuthModel;
import dedi.model.Pengguna;
import dedi.Main;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

import java.io.IOException;

/**
 * Controller for {@code login.fxml}. Validates credentials via {@link AuthModel}
 * and, on success, stores the authenticated user in {@link #currentUser} and
 * swaps the stage's scene to {@code dashboard.fxml}.
 *
 * <p>{@code currentUser} is a process-wide session handle read by downstream
 * controllers to drive role-based UI; it must be cleared on logout.
 */
public class LoginController {

    /** The authenticated user for the current session, or {@code null} when no one is logged in. */
    public static Pengguna currentUser;

    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;
    @FXML private Label errorLabel;

    private final AuthModel authModel = new AuthModel();

    @FXML
    private void handleLogin() {
        String username = usernameField.getText();
        String password = passwordField.getText();

        if (username == null || username.isBlank() || password == null || password.isBlank()) {
            errorLabel.setText("Username dan password wajib diisi");
            return;
        }

        Pengguna user = authModel.getUserData(username, password);
        if (user == null) {
            errorLabel.setText("Username atau password salah.");
            return;
        }

        currentUser = user;
        navigateToDashboard();
    }

    private void navigateToDashboard() {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/dedi/view/dashboard.fxml"));
            Main.switchRoot(root);
        } catch (IOException e) {
            e.printStackTrace();
            errorLabel.setText("Gagal memuat halaman dashboard.");
        }
    }
}
