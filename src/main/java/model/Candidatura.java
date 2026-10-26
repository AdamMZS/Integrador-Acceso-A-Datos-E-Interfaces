package model;
import java.time.LocalDate;
public  record Candidatura(
    long id,
    String titulo,
    String empresa,
    EstadoCandidatura estadoCandidatura,
    LocalDate fechaPostulacion,
    String enlace,
    String notas){}

