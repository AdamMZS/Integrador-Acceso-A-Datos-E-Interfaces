package model;

/**
 * Estados posibles de una candidatura.
 */
public enum EstadoCandidatura {
    /** La candidatura está pendiente de respuesta. */
    PENDIENTE,
    /** La candidatura está en fase de entrevista. */
    ENTREVISTA,
    /** La candidatura ha sido aceptada. */
    ACEPTADO,
    /** La candidatura ha sido rechazada. */
    RECHAZADO;
}
