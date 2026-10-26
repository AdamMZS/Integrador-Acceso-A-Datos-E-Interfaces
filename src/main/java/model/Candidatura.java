package model;
import java.time.LocalDate;
import Exception.TrackerException;
import java.time.format.DateTimeFormatter;
public  record Candidatura(
    long id,
    String titulo,
    String empresa,
    EstadoCandidatura estadoCandidatura,
    LocalDate fechaPostulacion,
    String enlace,
    String notas){}

