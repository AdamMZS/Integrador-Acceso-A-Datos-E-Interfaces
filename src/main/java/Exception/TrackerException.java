package Exception;

/**
 * Indica un error al procesar datos de candidaturas.
 */
public class TrackerException extends RuntimeException {
    /**
     * Crea una excepción con un mensaje.
     *
     * @param mensaje descripción del error
     */
    public TrackerException(String mensaje) {
        super(mensaje);
    }

    /**
     * Crea una excepción con un mensaje y su causa.
     *
     * @param mensaje descripción del error
     * @param cause causa original del error
     */
    public TrackerException(String mensaje, Throwable cause) {
        super(mensaje, cause);
    }
}
