package dao;

import model.Candidatura;
import model.EstadoCandidatura;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class GestorArchivos {

    private static final Path RUTA_ARCHIVO = Paths.get("candidaturas.txt");
    private static final DateTimeFormatter FORM_FECHA = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private static final String SEPARADOR = ";";
    private static final int N_CAMPOS = 7;

    // --- LÓGICA DE LECTURA Y ESCRITURA ---

    public List<Candidatura> leerTodas() {
        List<Candidatura> lista = new ArrayList<>();
        if (!Files.exists(RUTA_ARCHIVO)) {
            return lista;
        }
        try {
            List<String> lineas = Files.readAllLines(RUTA_ARCHIVO);
            for (String linea : lineas) {
                if (!linea.trim().isEmpty()) {
                    lista.add(desdeLinea(linea));
                }
            }
        } catch (IOException e) {
            System.err.println("Error al leer el archivo: " + e.getMessage());
        }
        return lista;
    }

    public void guardarTodas(List<Candidatura> candidaturas) {
        List<String> lineas = new ArrayList<>();
        for (Candidatura c : candidaturas) {
            lineas.add(aLinea(c));
        }
        try {
            Files.write(RUTA_ARCHIVO, lineas);
        } catch (IOException e) {
            System.err.println("Error al guardar en el archivo: " + e.getMessage());
        }
    }

    // --- LÓGICA DE TRANSFORMACIÓN (MAPEO) ---

    private Candidatura desdeLinea(String linea) {
        String[] campos = linea.split(SEPARADOR, -1);
        if (campos.length != N_CAMPOS) {
            throw new IllegalArgumentException("Se esperaban " + N_CAMPOS + " campos en la línea: " + linea);
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
            throw new IllegalArgumentException("Formato de datos inválido en la línea: " + linea, e);
        }
    }

    private String aLinea(Candidatura c) {
        return String.join(SEPARADOR,
                String.valueOf(c.id()),
                sanear(c.titulo()),
                sanear(c.empresa()),
                c.estadoCandidatura().name().toLowerCase(Locale.ROOT),
                c.fechaPostulacion().format(FORM_FECHA),
                sanear(c.enlace()),
                sanear(c.notas()));
    }

    private EstadoCandidatura parsearEstado(String texto) {
        try {
            return EstadoCandidatura.valueOf(texto.trim().toUpperCase(Locale.ROOT));
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Estado no válido: '" + texto + "'", e);
        }
    }

    private String sanear(String texto) {
        return texto == null ? "" : texto.replace(SEPARADOR, ",");
    }
}