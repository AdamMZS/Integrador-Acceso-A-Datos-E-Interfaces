package cli;

import dao.CandidaturaDAO;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import javafx.event.ActionEvent;
import model.Candidatura;
import model.EstadoCandidatura;

import java.io.IOException;
import java.time.LocalDate;

public class FormularioController {
    private final CandidaturaDAO candidaturaDAO = new CandidaturaDAO();
    private Candidatura candidaturaEditar;
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
    public void initialize() {
        btnNavInicio.setOnAction(e -> cambiarVista("/inicio.fxml", e));
        btnNavCandidaturas.setOnAction(e -> cambiarVista("/listado.fxml", e));
        btnNavAnadir.setOnAction(e -> cambiarVista("/formulario.fxml", e));
        btnCancelar.setOnAction(e -> cambiarVista("/listado.fxml", e));
        btnGuardar.setOnAction(e -> guardar(e));
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
    private void ConfigurarNav(){
        btnNavInicio.setOnAction(e -> cambiarVista("/inicio.fxml",e ));
        btnNavCandidaturas.setOnAction(e -> cambiarVista("/listado.fxml", e));
        btnCancelar.setOnAction(e -> cambiarVista("/listado.fxml", e));
    }
    public void setCandidatura(Candidatura c){
        this.candidaturaEditar=c;
        if (c!=null){
            lblBreadcrumbActual.setText("Editar Candidatura");
            lblTituloPagina.setText("Editar Candidatura");
            lblSubtituloPagina.setText("Modifica los datos");
            txtTitulo.setText(c.titulo());
            txtTitulo.setText(c.titulo());
            txtEmpresa.setText(c.empresa());
            dpFecha.setValue(c.fechaPostulacion());
            txtEnlace.setText(c.enlace() != null ? c.enlace() : "");
            txtNotas.setText(c.notas() != null ? c.notas() : "");
            if (c.estadoCandidatura()!=null){
                switch (c.estadoCandidatura()){
                    case PENDIENTE -> btnEstadoPendiente.setSelected(true);
                    case ENTREVISTA -> btnEstadoEntrevista.setSelected(true);
                    case RECHAZADO -> btnEstadoRechazado.setSelected(true);
                    case ACEPTADO -> btnEstadoAceptado.setSelected(true);
                }
            }
        }
    }
    private void guardar(ActionEvent event) {
        ocultarErrores();

        String titulo = txtTitulo.getText() != null ? txtTitulo.getText().trim() : "";
        String empresa = txtEmpresa.getText() != null ? txtEmpresa.getText().trim() : "";
        LocalDate fecha = dpFecha.getValue();

        boolean valido = true;

        if (titulo.isEmpty()) {
            mostrarErrorCampo(lblErrorTitulo, "El título es obligatorio.");
            valido = false;
        }
        if (empresa.isEmpty()) {
            mostrarErrorCampo(lblErrorEmpresa, "La empresa es obligatoria.");
            valido = false;
        }
        if (fecha == null) {
            mostrarErrorCampo(lblErrorFecha, "Selecciona una fecha válida.");
            valido = false;
        }

        if (!valido) {
            bannerErrorGeneral.setVisible(true);
            bannerErrorGeneral.setManaged(true);
            return;
        }

        EstadoCandidatura estado = obtenerEstadoSeleccionado();
        String enlace = txtEnlace.getText() != null ? txtEnlace.getText().trim() : "";
        String notas = txtNotas.getText() != null ? txtNotas.getText().trim() : "";

        try {
            if (candidaturaEditar == null) {
                Candidatura nueva = new Candidatura(0L, titulo, empresa, estado, fecha, enlace, notas);
                candidaturaDAO.anhadir(nueva);
            } else {
                Candidatura actualizada = new Candidatura(candidaturaEditar.id(), titulo, empresa, estado, fecha, enlace, notas);
                candidaturaDAO.modificar(actualizada);
            }
            cambiarVista("/listado.fxml", event);
        } catch (Exception e) {
            e.printStackTrace();
            bannerErrorGeneral.setVisible(true);
            bannerErrorGeneral.setManaged(true);
        }
    }

    private EstadoCandidatura obtenerEstadoSeleccionado() {
        Toggle seleccionado = grupoEstado.getSelectedToggle();
        if (seleccionado == btnEstadoEntrevista) return EstadoCandidatura.ENTREVISTA;
        if (seleccionado == btnEstadoRechazado) return EstadoCandidatura.RECHAZADO;
        if (seleccionado == btnEstadoAceptado) return EstadoCandidatura.ACEPTADO;
        return EstadoCandidatura.PENDIENTE;
    }

    private void ocultarErrores() {
        bannerErrorGeneral.setVisible(false);
        bannerErrorGeneral.setManaged(false);

        lblErrorTitulo.setVisible(false);
        lblErrorTitulo.setManaged(false);

        lblErrorEmpresa.setVisible(false);
        lblErrorEmpresa.setManaged(false);

        lblErrorFecha.setVisible(false);
        lblErrorFecha.setManaged(false);
    }
    private void mostrarErrorCampo(Label label, String mensaje) {
        label.setText(mensaje);
        label.setVisible(true);
        label.setManaged(true);
    }
}
