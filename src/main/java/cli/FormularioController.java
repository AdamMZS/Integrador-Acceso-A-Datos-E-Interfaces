package cli;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import javafx.event.ActionEvent;
import java.io.IOException;

public class FormularioController {

    @FXML
    private HBox bannerErrorGeneral;

    @FXML
    private Button btnCancelar;

    @FXML
    private ToggleButton btnEstadoAceptado;

    @FXML
    private ToggleButton btnEstadoEntrevista;

    @FXML
    private ToggleButton btnEstadoPendiente;

    @FXML
    private ToggleButton btnEstadoRechazado;

    @FXML
    private Button btnGuardar;

    @FXML
    private Button btnNavAnadir;

    @FXML
    private Button btnNavCandidaturas;

    @FXML
    private Button btnNavInicio;

    @FXML
    private DatePicker dpFecha;

    @FXML
    private ToggleGroup grupoEstado;

    @FXML
    private Label lblBreadcrumbActual;

    @FXML
    private Label lblErrorEmpresa;

    @FXML
    private Label lblErrorFecha;

    @FXML
    private Label lblErrorTitulo;

    @FXML
    private Label lblNotaInferior;

    @FXML
    private Label lblSubtituloPagina;

    @FXML
    private Label lblTituloPagina;

    @FXML
    private Label lblTituloTarjeta;

    @FXML
    private TextField txtEmpresa;

    @FXML
    private TextField txtEnlace;

    @FXML
    private TextArea txtNotas;

    @FXML
    private TextField txtTitulo;
    private void cambiarVista(String rutaFxml, ActionEvent event){
        try{
            FXMLLoader loader=new FXMLLoader(getClass().getResource(rutaFxml));
            Parent root=loader.load();
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }
    private void ConfigurarNav(){
        btnNavInicio.setOnAction(e -> cambiarVista("inicio.fxml",e ));
        btnNavCandidaturas.setOnAction(e -> cambiarVista("/listado.fxml", e));
        btnCancelar.setOnAction(e -> cambiarVista("/listado.fxml", e));
    }
}
