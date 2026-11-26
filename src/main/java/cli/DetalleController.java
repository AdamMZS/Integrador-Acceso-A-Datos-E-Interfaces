package cli;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import model.Candidatura;

import java.io.IOException;
import java.time.format.DateTimeFormatter;

public class DetalleController {

    @FXML
    private Button btnEditar;

    @FXML
    private Button btnEliminar;

    @FXML
    private Button btnNavAnadir;

    @FXML
    private Button btnNavCandidaturas;

    @FXML
    private Button btnNavInicio;

    @FXML
    private Button btnVolver;

    @FXML
    private Label lblBadgeEstadoTop;

    @FXML
    private Label lblEmpresa;

    @FXML
    private Label lblEstado;

    @FXML
    private Label lblFecha;

    @FXML
    private Label lblId;

    @FXML
    private Label lblNotas;

    @FXML
    private Label lblSubtituloEmpresa;

    @FXML
    private Label lblTitulo;

    @FXML
    private Label lblTituloPrincipal;
    private Candidatura candidatura;
    @FXML
    private Hyperlink linkOferta;
    public void initialize() {
        ConfigurarNav();
        btnVolver.setOnAction(event -> cambiarVista("/listado.fxml",event));
    }
    private void ConfigurarNav(){
        btnNavInicio.setOnAction(e -> cambiarVista("/inicio.fxml",e ));
        btnNavCandidaturas.setOnAction(e -> cambiarVista("/listado.fxml", e));
        btnNavAnadir.setOnAction(e -> cambiarVista("/formulario.fxml", e));
    }
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
    private void eliminar(Candidatura c){

    }
    public void setCandidatura(Candidatura c) {
        this.candidatura = c;
        if (c == null) return;

        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        String estadoTexto = c.estadoCandidatura() != null ? c.estadoCandidatura().name() : "-";

        lblTituloPrincipal.setText(c.titulo());
        lblSubtituloEmpresa.setText(c.empresa() + " - Candidatura #" + c.id());
        lblBadgeEstadoTop.setText(estadoTexto);

        lblId.setText("#" + c.id());
        lblTitulo.setText(c.titulo());
        lblEmpresa.setText(c.empresa());
        lblEstado.setText(estadoTexto);
        lblFecha.setText(c.fechaPostulacion() != null ? c.fechaPostulacion().format(fmt) : "-");
        lblNotas.setText(c.notas() != null && !c.notas().isBlank() ? c.notas() : "Sin notas adicionales.");

        if (c.enlace() != null && !c.enlace().isBlank()) {
            linkOferta.setText(c.enlace());
            linkOferta.setDisable(false);
        } else {
            linkOferta.setText("Sin enlace disponible");
            linkOferta.setDisable(true);
        }
    }
}
