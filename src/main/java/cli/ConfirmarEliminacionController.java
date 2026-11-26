package cli;

import dao.CandidaturaDAO;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import model.Candidatura;

import java.io.IOException;

public class ConfirmarEliminacionController {
    private Candidatura candidatura;
    private CandidaturaDAO candidaturaDAO = new CandidaturaDAO();
    @FXML
    private Button btnCancelar;

    @FXML
    private Button btnConfirmarEliminar;

    @FXML
    private Label iconPapelera;

    @FXML
    private Label lblSubtituloEliminar;

    @FXML
    private Label lblTituloEliminar;

    public void initialize() {

    }

    public void setCandidatura(Candidatura candidatura) {
        this.candidatura = candidatura;
        if (candidatura!=null){
            lblTituloEliminar.setText(candidatura.titulo());
            lblSubtituloEliminar.setText(candidatura.empresa() + " - Candidatura #" + candidatura.id());
        }
    }

    @FXML
    private void eliminar(ActionEvent event) {
        System.out.println("Candidatura: " + candidatura);
        if (candidatura != null) {
            candidaturaDAO.eliminar(candidatura.id());
            cambiarVista("/listado.fxml", event);
        }
    }

    @FXML
    private void cancelar(ActionEvent event) {
        cambiarVista("/listado.fxml", event);
    }

    private void cambiarVista(String rutaFxml, ActionEvent event) {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource(rutaFxml));
                Parent root = loader.load();

                Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
                stage.setScene(new Scene(root));
                stage.show();
            } catch (IOException e) {
                e.printStackTrace();
        }
    }
}