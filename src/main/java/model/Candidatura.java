package model;

import java.time.LocalDate;

/**
 * Representa una candidatura de empleo.
 *
 * @param id identificador único
 * @param titulo puesto al que se postula
 * @param empresa empresa que ofrece el puesto
 * @param estadoCandidatura estado actual de la candidatura
 * @param fechaPostulacion fecha de la postulación
 * @param enlace enlace relacionado con la oferta
 * @param notas notas adicionales
 */
public  record Candidatura(
    long id,
    String titulo,
    String empresa,
    EstadoCandidatura estadoCandidatura,
    LocalDate fechaPostulacion,
    String enlace,
    String notas){}
