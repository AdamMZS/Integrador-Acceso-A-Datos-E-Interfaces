package Exception;

public class TrackerException extends RuntimeException {
public TrackerException(String mensaje){
    super(mensaje);
}

public TrackerException(String mensaje,Throwable cause){
    super(mensaje,cause);
}
}
