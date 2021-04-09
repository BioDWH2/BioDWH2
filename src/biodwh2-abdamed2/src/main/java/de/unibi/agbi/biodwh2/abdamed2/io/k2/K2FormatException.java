package de.unibi.agbi.biodwh2.abdamed2.io.k2;

public class K2FormatException extends RuntimeException {
    private static final long serialVersionUID = -4221488740065079778L;

    public K2FormatException() {
    }

    public K2FormatException(String message) {
        super(message);
    }

    public K2FormatException(String message, Throwable cause) {
        super(message, cause);
    }

    public K2FormatException(Throwable cause) {
        super(cause);
    }

    public K2FormatException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
