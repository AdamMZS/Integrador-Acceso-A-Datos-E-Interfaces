package cli;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;

import java.io.IOException;

public class InicioController {

    @FXML
    private ImageView imgLogo;

    @FXML
    private Button btnEntrar;

    @FXML
    private Button btnSalir;

    @FXML
    private void entrar(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/listado.fxml"));
            Parent root = loader.load();

            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();

        } catch (IOException e) {
            System.err.println("Error al cargar listado.fxml:");
            e.printStackTrace();
        }
    }

    @FXML
    private void salir(ActionEvent event) {
        Platform.exit();
    }
}