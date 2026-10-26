package Mapper;

import model.Candidatura;
import model.EstadoCandidatura;
import Exception.TrackerException;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class GestionCandidaturas {

    public static final DateTimeFormatter Form_fecha = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private static final String separador = ";";
    private static final int nCampos = 7;

    public static Candidatura desdeLinea(String linea) {
        String[] campos = linea.split(separador, -1);
        if (campos.length != nCampos) {
            throw new TrackerException("Se esperaban:" + nCampos + " y llegaron:" + campos);
        }
        try {
                    long id=Long.parseLong(campos[0].trim());
                    String titulo=campos[1].trim();
                    String empresa=campos[2].trim();
                    EstadoCandidatura estado=parsearEstado(campos[3].trim());
                    LocalDate fecha=LocalDate.parse(campos[4].trim());
                    String enlace=campos[5].trim();
                    String notas=campos[6].trim();
                    return new Candidatura(id,titulo,empresa,estado,fecha,enlace,notas);
        }catch (NumberFormatException|TrackerException e){
            throw new TrackerException("Formato no validos en la linea"+linea);
        }
    }

    private static EstadoCandidatura parsearEstado(String texto) {
        try {
            return EstadoCandidatura.valueOf(texto.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new TrackerException("Estado no válido: " + texto);
        }
    }

    private static String sanear
}