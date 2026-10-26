package mapper;

import Exception.TrackerException;
import model.Candidatura;
import model.EstadoCandidatura;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Locale;


public class GestionCandidaturas {

    public static final DateTimeFormatter Form_fecha = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    private static final String SEPARADOR = ";";
    private static final int N_CAMPOS = 7;

    private GestionCandidaturas() {
    }


    public static Candidatura desdeLinea(String linea) {
        String[] campos = linea.split(SEPARADOR, -1);
        if (campos.length != N_CAMPOS) {
            throw new TrackerException("Se esperaban " + N_CAMPOS + " campos y llegaron "
                    + campos.length + " en la línea: " + linea);
        }
        try {
            long id = Long.parseLong(campos[0].trim());
            String titulo = campos[1].trim();
            String empresa = campos[2].trim();
            EstadoCandidatura estado = parsearEstado(campos[3]);
            LocalDate fecha = LocalDate.parse(campos[4].trim(), Form_fecha);
            String enlace = campos[5].trim();
            String notas = campos[6].trim();
            return new Candidatura(id, titulo, empresa, estado, fecha, enlace, notas);
        } catch (NumberFormatException | DateTimeParseException e) {
            throw new TrackerException("Formato no válido en la línea: " + linea, e);
        }
    }


    public static EstadoCandidatura parsearEstado(String texto) {
        try {
            return EstadoCandidatura.valueOf(texto.trim().toUpperCase(Locale.ROOT));
        } catch (IllegalArgumentException e) {
            throw new TrackerException("Estado no válido: '" + texto
                    + "'. Valores: pendiente, entrevista, rechazado, aceptado", e);
        }
    }



    public static String aLinea(Candidatura c) {
        return String.join(SEPARADOR,
                String.valueOf(c.id()),
                sanear(c.titulo()),
                sanear(c.empresa()),
                c.estadoCandidatura().name().toLowerCase(Locale.ROOT),
                c.fechaPostulacion().format(Form_fecha),
                sanear(c.enlace()),
                sanear(c.notas()));
    }

    private static String sanear(String texto) {
        return texto == null ? "" : texto.replace(SEPARADOR, ",");
    }
}