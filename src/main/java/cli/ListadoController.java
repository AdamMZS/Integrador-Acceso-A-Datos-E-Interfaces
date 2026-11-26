package cli;

import dao.CandidaturaDAO;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import model.Candidatura;
import model.EstadoCandidatura;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

public class ListadoController {

    // --- Navegación lateral ---
    @FXML private Button btnNavInicio;
    @FXML private Button btnNavCandidaturas;
    @FXML private Button btnNavAnadir;

    // --- Botones superiores ---
    @FXML private Button btnImportarCSV;
    @FXML private Button btnAnadirCandidatura;

    // --- Filtros ---
    @FXML private TextField txtBuscar;
    @FXML private ComboBox<String> cmbEstado;
    @FXML private ComboBox<String> cmbEmpresa;

    // --- Tabla y columnas ---
    @FXML private TableView<Candidatura> tablaCandidaturas;
    @FXML private TableColumn<Candidatura, Long> colId;
    @FXML private TableColumn<Candidatura, String> colTitulo;
    @FXML private TableColumn<Candidatura, String> colEmpresa;
    @FXML private TableColumn<Candidatura, String> colEstado;
    @FXML private TableColumn<Candidatura, String> colFecha;
    @FXML private TableColumn<Candidatura, Void> colAcciones;

    // --- Pie de tabla y paginación ---
    @FXML private Label lblContador;
    @FXML private Button btnPaginaAnterior;
    @FXML private Button btnPagina1;
    @FXML private Button btnPagina2;
    @FXML private Button btnPaginaSiguiente;

    private final CandidaturaDAO candidaturaDAO = new CandidaturaDAO();
    private final ObservableList<Candidatura> masterData = FXCollections.observableArrayList();
    private FilteredList<Candidatura> filteredData;

    private int paginaActual = 1;
    private static final int FILAS_POR_PAGINA = 10;

    @FXML
    public void initialize() {
        configurarColumnas();
        configurarBotonesAccionFila();
        cargarDatosDesdeBD();
        configurarFiltros();
        configurarNavegacion();
    }

    private void configurarColumnas() {
        colId.setCellValueFactory(cell -> new SimpleObjectProperty<>(cell.getValue().id()));
        colTitulo.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().titulo()));
        colEmpresa.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().empresa()));
        colEstado.setCellValueFactory(cell -> new SimpleStringProperty(
                cell.getValue().estadoCandidatura() != null ? cell.getValue().estadoCandidatura().name() : ""
        ));
        colFecha.setCellValueFactory(cell -> new SimpleStringProperty(
                cell.getValue().fechaPostulacion() != null ? cell.getValue().fechaPostulacion().toString() : ""
        ));
    }

    private void configurarBotonesAccionFila() {
        colAcciones.setCellFactory(param -> new TableCell<>() {
            private final Button btnVer = new Button("Ver");
            private final Button btnEditar = new Button("Editar");
            private final Button btnEliminar = new Button("Borrar");
            private final HBox contenedor = new HBox(6, btnVer, btnEditar, btnEliminar);

            {
                contenedor.setAlignment(Pos.CENTER);
                btnVer.setStyle("-fx-background-color: #EEF2FF; -fx-text-fill: #4F46E5; -fx-font-size: 11px; -fx-cursor: hand;");
                btnEditar.setStyle("-fx-background-color: #F3F4F6; -fx-text-fill: #374151; -fx-font-size: 11px; -fx-cursor: hand;");
                btnEliminar.setStyle("-fx-background-color: #FEE2E2; -fx-text-fill: #DC2626; -fx-font-size: 11px; -fx-cursor: hand;");

                btnVer.setOnAction(e -> abrirVistaConCandidatura("/detalles.fxml", getTableView().getItems().get(getIndex()), e));
                btnEditar.setOnAction(e -> abrirVistaConCandidatura("/formulario.fxml", getTableView().getItems().get(getIndex()), e));
                btnEliminar.setOnAction(e -> abrirVistaConCandidatura("/confirmar_eliminacion.fxml", getTableView().getItems().get(getIndex()), e));
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : contenedor);
            }
        });
    }

    private void cargarDatosDesdeBD() {
        try {
            List<Candidatura> lista = candidaturaDAO.leerTodas();
            masterData.setAll(lista);
        } catch (Exception e) {
            System.err.println("Error cargando candidaturas desde BD: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void configurarFiltros() {
        // Combo Estado
        cmbEstado.getItems().add("Estado: Todos");
        for (EstadoCandidatura estado : EstadoCandidatura.values()) {
            cmbEstado.getItems().add(estado.name());
        }
        cmbEstado.getSelectionModel().selectFirst();

        // Combo Empresa
        cmbEmpresa.getItems().add("Empresa: Todas");
        List<String> empresas = masterData.stream()
                .map(Candidatura::empresa)
                .filter(emp -> emp != null && !emp.isBlank())
                .distinct()
                .sorted()
                .collect(Collectors.toList());
        cmbEmpresa.getItems().addAll(empresas);
        cmbEmpresa.getSelectionModel().selectFirst();

        filteredData = new FilteredList<>(masterData, p -> true);

        txtBuscar.textProperty().addListener((obs, oldVal, newVal) -> aplicarFiltros());
        cmbEstado.valueProperty().addListener((obs, oldVal, newVal) -> aplicarFiltros());
        cmbEmpresa.valueProperty().addListener((obs, oldVal, newVal) -> aplicarFiltros());

        actualizarTablaYPaginacion();
    }

    private void aplicarFiltros() {
        String texto = txtBuscar.getText() == null ? "" : txtBuscar.getText().toLowerCase().trim();
        String estadoSel = cmbEstado.getValue();
        String empresaSel = cmbEmpresa.getValue();

        filteredData.setPredicate(c -> {
            boolean coincideTexto = texto.isEmpty()
                    || (c.titulo() != null && c.titulo().toLowerCase().contains(texto))
                    || (c.empresa() != null && c.empresa().toLowerCase().contains(texto))
                    || String.valueOf(c.id()).contains(texto);

            boolean coincideEstado = estadoSel == null || estadoSel.equals("Estado: Todos")
                    || (c.estadoCandidatura() != null && c.estadoCandidatura().name().equals(estadoSel));

            boolean coincideEmpresa = empresaSel == null || empresaSel.equals("Empresa: Todas")
                    || (c.empresa() != null && c.empresa().equals(empresaSel));

            return coincideTexto && coincideEstado && coincideEmpresa;
        });

        paginaActual = 1;
        actualizarTablaYPaginacion();
    }

    private void actualizarTablaYPaginacion() {
        int total = filteredData.size();
        int totalPaginas = Math.max(1, (int) Math.ceil((double) total / FILAS_POR_PAGINA));
        if (paginaActual > totalPaginas) paginaActual = totalPaginas;

        int desde = (paginaActual - 1) * FILAS_POR_PAGINA;
        int hasta = Math.min(desde + FILAS_POR_PAGINA, total);

        tablaCandidaturas.setItems(FXCollections.observableArrayList(filteredData.subList(desde, hasta)));
        lblContador.setText("Mostrando candidaturas: " + (total == 0 ? 0 : desde + 1) + "-" + hasta + " de " + total);

        btnPaginaAnterior.setDisable(paginaActual <= 1);
        btnPaginaSiguiente.setDisable(paginaActual >= totalPaginas);
        btnPagina2.setVisible(totalPaginas >= 2);
    }

    private void configurarNavegacion() {
        btnNavInicio.setOnAction(e -> cambiarVista("/inicio.fxml", e));
        btnNavAnadir.setOnAction(e -> cambiarVista("/formulario.fxml", e));
        btnAnadirCandidatura.setOnAction(e -> cambiarVista("/formulario.fxml", e));
        btnImportarCSV.setOnAction(e -> cambiarVista("/importar.fxml", e));

        btnPaginaAnterior.setOnAction(e -> { paginaActual--; actualizarTablaYPaginacion(); });
        btnPagina1.setOnAction(e -> { paginaActual = 1; actualizarTablaYPaginacion(); });
        btnPagina2.setOnAction(e -> { paginaActual = 2; actualizarTablaYPaginacion(); });
        btnPaginaSiguiente.setOnAction(e -> { paginaActual++; actualizarTablaYPaginacion(); });
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

    private void abrirVistaConCandidatura(String rutaFxml, Candidatura candidatura, ActionEvent event) {
        // Cuando tengas los controladores de detalles, formulario y eliminar,
        // aquí les pasarás el objeto `candidatura` seleccionado.
        cambiarVista(rutaFxml, event);
    }
}