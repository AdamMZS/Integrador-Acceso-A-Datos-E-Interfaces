package dao;

import mapper.GestionCandidaturas;
import model.Candidatura;
import model.EstadoCandidatura;
import Exception.TrackerException;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.format.ResolverStyle;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * Lee, guarda y importa candidaturas desde archivos.
 */
public class GestorArchivos {

    private static final DateTimeFormatter FORM_FECHA =
            DateTimeFormatter.ofPattern("dd/MM/uuuu").withResolverStyle(ResolverStyle.STRICT);
    private static final String SEPARADOR = ";";
    private static final int N_CAMPOS = 7;

    private final Path rutaArchivo;

    /**
     * Crea el gestor usando {@code candidaturas.txt} en el directorio actual.
     */
    public GestorArchivos() {
        this(Paths.get("candidaturas.txt"));
    }

    /**
     * Crea el gestor para la ruta indicada.
     *
     * @param rutaArchivo archivo donde se almacenan las candidaturas
     */
    public GestorArchivos(Path rutaArchivo) {
        this.rutaArchivo = rutaArchivo;
    }

    /**
     * Lee todas las candidaturas almacenadas.
     *
     * @return candidaturas leídas, o una lista vacía si el archivo no existe
     * @throws IOException si no se puede leer el archivo
     */
    public List<Candidatura> leerTodas() throws IOException {
        List<Candidatura> candidaturas = new ArrayList<>();
        if (!Files.exists(rutaArchivo)) {
            return candidaturas;
        }

        List<String> lineas = Files.readAllLines(rutaArchivo, StandardCharsets.UTF_8);
        for (int i = 0; i < lineas.size(); i++) {
            String linea = lineas.get(i);
            if (!linea.isBlank()) {
                try {
                    candidaturas.add(desdeLinea(linea));
                } catch (IllegalArgumentException e) {
                    throw new IllegalArgumentException(
                            "Error en " + rutaArchivo + ", línea " + (i + 1) + ": " + e.getMessage(), e);
                }
            }
        }
        return candidaturas;
    }

    /**
     * Sustituye el contenido del archivo por la lista indicada.
     *
     * @param candidaturas candidaturas que se guardarán
     * @throws IOException si no se puede escribir el archivo
     */
    public void guardarTodas(List<Candidatura> candidaturas) throws IOException {
        List<String> lineas = candidaturas.stream().map(this::aLinea).toList();
        Files.write(rutaArchivo, lineas, StandardCharsets.UTF_8);
    }

    /**
     * Calcula el siguiente identificador disponible.
     *
     * @return identificador posterior al mayor almacenado
     * @throws IOException si no se puede leer el archivo
     */
    public long siguienteId() throws IOException {
        return leerTodas().stream().mapToLong(Candidatura::id).max().orElse(0) + 1;
    }

    /**
     * Añade una candidatura si su identificador no está en uso.
     *
     * @param candidatura candidatura que se guardará
     * @throws IOException si no se puede leer o escribir el archivo
     * @throws IllegalArgumentException si ya existe el identificador
     */
    public void añadir(Candidatura candidatura) throws IOException {
        List<Candidatura> candidaturas = leerTodas();
        if (candidaturas.stream().anyMatch(c -> c.id() == candidatura.id())) {
            throw new IllegalArgumentException("Ya existe una candidatura con el ID " + candidatura.id() + ".");
        }
        candidaturas.add(candidatura);
        guardarTodas(candidaturas);
    }

    /**
     * Busca una candidatura por su identificador.
     *
     * @param id identificador que se busca
     * @return candidatura encontrada, o {@code null} si no existe
     * @throws IOException si no se puede leer el archivo
     */
    public Candidatura buscarPorId(long id) throws IOException {
        return leerTodas().stream()
                .filter(candidatura -> candidatura.id() == id)
                .findFirst()
                .orElse(null);
    }

    /**
     * Elimina la candidatura con el identificador indicado.
     *
     * @param id identificador de la candidatura
     * @return {@code true} si se eliminó una candidatura
     * @throws IOException si no se puede leer o escribir el archivo
     */
    public boolean borrar(long id) throws IOException {
        List<Candidatura> candidaturas = leerTodas();
        boolean eliminada = candidaturas.removeIf(candidatura -> candidatura.id() == id);
        if (eliminada) {
            guardarTodas(candidaturas);
        }
        return eliminada;
    }

    /**
     * Importa candidaturas desde un archivo CSV.
     *
     * @return número de candidaturas importadas
     * @throws IOException si no se puede leer el archivo
     * @throws IllegalArgumentException si alguna fila no es válida
     */
    public int importar(Path csv) throws IOException {
        if (!Files.exists(csv)) {
            throw new IllegalArgumentException("No se encontró el archivo: " + csv);
        }
        List<String> lineas = Files.readAllLines(csv, StandardCharsets.UTF_8);
        List<Candidatura> todas = new ArrayList<>(leerTodas());
        long siguiente = todas.stream().mapToLong(Candidatura::id).max().orElse(0L) + 1;
        int importadas = 0;

        for (int i = 0; i < lineas.size(); i++) {
            String linea = lineas.get(i).replace("\uFEFF", "").trim();
            if (linea.isEmpty() || (i == 0 && esCabecera(linea))) {
                continue;
            }
            try {
                Candidatura c = GestionCandidaturas.desdeLinea(linea);
                todas.add(new Candidatura(siguiente++, c.titulo(), c.empresa(), c.estadoCandidatura(),
                        c.fechaPostulacion(), c.enlace(), c.notas()));
                importadas++;
            } catch (TrackerException e) {
                throw new IllegalArgumentException("Error en " + csv + ", línea " + (i + 1) + ": " + e.getMessage());
            }
        }

        guardarTodas(todas);
        return importadas;
    }

    private boolean esCabecera(String linea) {
        return linea.toLowerCase(Locale.ROOT).startsWith("id;");
    }
    private Candidatura desdeLinea(String linea) {
        String[] campos = linea.split(SEPARADOR, -1);
        if (campos.length != N_CAMPOS) {
            throw new IllegalArgumentException("se esperaban " + N_CAMPOS + " campos y hay " + campos.length);
        }
        try {
            long id = Long.parseLong(campos[0].trim());
            String titulo = campos[1].trim();
            String empresa = campos[2].trim();
            EstadoCandidatura estado = parsearEstado(campos[3]);
            LocalDate fecha = LocalDate.parse(campos[4].trim(), FORM_FECHA);
            String enlace = campos[5].trim();
            String notas = campos[6].trim();
            return new Candidatura(id, titulo, empresa, estado, fecha, enlace, notas);
        } catch (NumberFormatException | DateTimeParseException e) {
            throw new IllegalArgumentException("formato de datos no válido", e);
        }
    }

    private String aLinea(Candidatura candidatura) {
        return String.join(SEPARADOR,
                String.valueOf(candidatura.id()),
                sanear(candidatura.titulo()),
                sanear(candidatura.empresa()),
                candidatura.estadoCandidatura().name().toLowerCase(Locale.ROOT),
                candidatura.fechaPostulacion().format(FORM_FECHA),
                sanear(candidatura.enlace()),
                sanear(candidatura.notas()));
    }

    private EstadoCandidatura parsearEstado(String texto) {
        try {
            return EstadoCandidatura.valueOf(texto.trim().toUpperCase(Locale.ROOT));
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("estado no válido: '" + texto
                    + "'. Valores: pendiente, entrevista, aceptado, rechazado", e);
        }
    }

    private Candidatura desdeCsv(List<String> campos, Map<String, Integer> columnas, long id) {
        String titulo;
        String empresa;
        String estado;
        String fecha;
        String enlace = "";
        String notas = "";

        if (columnas == null) {
            int desplazamiento = campos.size() == 7 && esNumero(campos.get(0)) ? 1 : 0;
            if (campos.size() - desplazamiento < 4 || campos.size() - desplazamiento > 6) {
                throw new IllegalArgumentException(
                        "el CSV sin cabecera debe tener titulo, empresa, estado, fecha y, opcionalmente, enlace y notas");
            }
            titulo = campos.get(desplazamiento);
            empresa = campos.get(desplazamiento + 1);
            estado = campos.get(desplazamiento + 2);
            fecha = campos.get(desplazamiento + 3);
            if (campos.size() > desplazamiento + 4) {
                enlace = campos.get(desplazamiento + 4);
            }
            if (campos.size() > desplazamiento + 5) {
                notas = campos.get(desplazamiento + 5);
            }
        } else {
            titulo = valorCsv(campos, columnas, "titulo");
            empresa = valorCsv(campos, columnas, "empresa");
            estado = valorCsv(campos, columnas, "estado");
            fecha = valorCsv(campos, columnas, "fecha");
            enlace = valorCsvOpcional(campos, columnas, "enlace");
            notas = valorCsvOpcional(campos, columnas, "notas");
        }

        try {
            return new Candidatura(id, titulo.trim(), empresa.trim(), parsearEstado(estado),
                    LocalDate.parse(fecha.trim(), FORM_FECHA), enlace.trim(), notas.trim());
        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException("fecha no válida '" + fecha + "'; usa dd/MM/yyyy", e);
        }
    }

    private List<String> parsearCsv(String linea) {
        List<String> campos = new ArrayList<>();
        StringBuilder campo = new StringBuilder();
        boolean entreComillas = false;

        for (int i = 0; i < linea.length(); i++) {
            char caracter = linea.charAt(i);
            if (caracter == '"') {
                if (entreComillas && i + 1 < linea.length() && linea.charAt(i + 1) == '"') {
                    campo.append('"');
                    i++;
                } else {
                    entreComillas = !entreComillas;
                }
            } else if (caracter == ',' && !entreComillas) {
                campos.add(campo.toString());
                campo.setLength(0);
            } else {
                campo.append(caracter);
            }
        }
        if (entreComillas) {
            throw new IllegalArgumentException("el CSV contiene un campo entrecomillado sin cerrar");
        }
        campos.add(campo.toString());
        return campos;
    }

    private boolean esCabecera(List<String> campos) {
        return campos.stream().map(this::normalizarCabecera)
                .anyMatch(campo -> campo.equals("titulo") || campo.equals("title"));
    }

    private Map<String, Integer> mapearCabecera(List<String> campos) {
        Map<String, Integer> columnas = new HashMap<>();
        for (int i = 0; i < campos.size(); i++) {
            String columna = normalizarCabecera(campos.get(i));
            switch (columna) {
                case "titulo", "title" -> columnas.put("titulo", i);
                case "empresa", "company" -> columnas.put("empresa", i);
                case "estado", "status" -> columnas.put("estado", i);
                case "fecha", "fecha_postulacion", "date", "application_date" ->
                        columnas.put("fecha", i);
                case "enlace", "link", "url" -> columnas.put("enlace", i);
                case "notas", "notes" -> columnas.put("notas", i);
                default -> {
                }
            }
        }
        return columnas;
    }

    private void validarCabecera(Map<String, Integer> columnas, Path archivo, int linea) {
        for (String requerida : List.of("titulo", "empresa", "estado", "fecha")) {
            if (!columnas.containsKey(requerida)) {
                throw new IllegalArgumentException("Cabecera incompleta en " + archivo + ", línea " + linea
                        + ": falta la columna '" + requerida + "'.");
            }
        }
    }

    private String valorCsv(List<String> campos, Map<String, Integer> columnas, String nombre) {
        int indice = columnas.get(nombre);
        if (indice >= campos.size()) {
            throw new IllegalArgumentException("faltan columnas en la fila CSV");
        }
        return campos.get(indice);
    }

    private String valorCsvOpcional(List<String> campos, Map<String, Integer> columnas, String nombre) {
        Integer indice = columnas.get(nombre);
        return indice == null || indice >= campos.size() ? "" : campos.get(indice);
    }

    private String normalizarCabecera(String texto) {
        return texto.trim().toLowerCase(Locale.ROOT).replace(" ", "_");
    }

    private boolean esNumero(String texto) {
        try {
            Long.parseLong(texto.trim());
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    private String quitarBOM(String texto) {
        return texto.startsWith("\uFEFF") ? texto.substring(1) : texto;
    }

    private String sanear(String texto) {
        return texto == null ? "" : texto.replace(SEPARADOR, ",").replace("\r", " ").replace("\n", " ");
    }
}
