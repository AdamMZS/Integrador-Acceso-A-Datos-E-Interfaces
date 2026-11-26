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
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import model.Candidatura;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class ImportarController {

    // --- Navegación lateral ---
    @FXML private Button btnNavAnadir;
    @FXML private Button btnNavCandidaturas;
    @FXML private Button btnNavInicio;

    // --- Botones inferiores ---
    @FXML private Button btnPrincipal;   // Botón "Importar"
    @FXML private Button btnSecundario;  // Botón "Cancelar"
    @FXML private Button btnSeleccionarArchivo;

    // --- Textos y paneles ---
    @FXML private Label lblArchivoSeleccionado;
    @FXML private Label lblDetalleError;
    @FXML private Label lblMensajeError;
    @FXML private Label lblMensajeExito;
    @FXML private Label lblTituloTarjetaIzq;

    @FXML private VBox listaCandidaturasImportadas;
    @FXML private VBox panelResultadoError;
    @FXML private VBox panelResultadoExito;
    @FXML private VBox panelSeleccionInicial;

    private File archivoCsvSeleccionado;

    @FXML
    public void initialize() {
        // Desactivamos el botón Importar hasta que el usuario elija un archivo
        btnPrincipal.setDisable(true);

        btnNavInicio.setOnAction(e -> cambiarVista("/inicio.fxml", e));
        btnNavCandidaturas.setOnAction(e -> cambiarVista("/listado.fxml", e));
        btnNavAnadir.setOnAction(e -> cambiarVista("/formulario.fxml", e));
        btnSecundario.setOnAction(e -> cambiarVista("/listado.fxml", e));

        btnSeleccionarArchivo.setOnAction(this::seleccionarArchivo);
        btnPrincipal.setOnAction(this::importarCsv);
    }
    private final CandidaturaDAO candidaturaDAO = new CandidaturaDAO();
    private void seleccionarArchivo(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Selecciona tu archivo CSV");
        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("Archivos CSV (*.csv)", "*.csv")
        );

        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        File archivo = fileChooser.showOpenDialog(stage);

        if (archivo != null) {
            this.archivoCsvSeleccionado = archivo;
            lblArchivoSeleccionado.setText(archivo.getName());
            btnPrincipal.setDisable(false);
        }
    }

    private void importarCsv(ActionEvent event) {
        if (archivoCsvSeleccionado == null) return;

        try {
            List<Candidatura> importadas = candidaturaDAO.importar(archivoCsvSeleccionado.toPath());

            // Ocultamos selección/error y mostramos el panel de éxito
            mostrarPanel(panelResultadoExito);
            lblMensajeExito.setText("Se han importado " + importadas.size() + " candidatura(s) correctamente.");

            listaCandidaturasImportadas.getChildren().clear();
            for (Candidatura c : importadas) {
                Label item = new Label("• " + c.titulo() + " (" + c.empresa() + ") - " + c.estadoCandidatura().name());
                listaCandidaturasImportadas.getChildren().add(item);
            }

            // Cambiamos los botones inferiores para volver al listado
            btnPrincipal.setText("Ver candidaturas");
            btnPrincipal.setOnAction(e -> cambiarVista("/listado.fxml", e));

        } catch (Exception e) {
            mostrarPanel(panelResultadoError);
            lblMensajeError.setText("No se pudo importar el archivo CSV");
            lblDetalleError.setText(e.getMessage());
        }
    }
    private void mostrarPanel(VBox panelActivo) {
        panelSeleccionInicial.setVisible(false);
        panelSeleccionInicial.setManaged(false);

        panelResultadoExito.setVisible(false);
        panelResultadoExito.setManaged(false);

        panelResultadoError.setVisible(false);
        panelResultadoError.setManaged(false);

        panelActivo.setVisible(true);
        panelActivo.setManaged(true);
    }
    private void cambiarVista(String rutaFxml, ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(rutaFxml));
            Parent root = loader.load();
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            System.err.println("Error al navegar a " + rutaFxml);
            e.printStackTrace();
        }
    }
}