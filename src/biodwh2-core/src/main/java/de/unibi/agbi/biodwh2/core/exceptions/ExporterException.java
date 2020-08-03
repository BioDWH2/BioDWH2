package de.unibi.agbi.biodwh2.core.exceptions;

public class ExporterException extends Exception {
    private static final long serialVersionUID = -5116753457000851091L;

    public ExporterException() {
        super();
    }

    public ExporterException(String message) {
        super(message);
    }

    public ExporterException(String message, Throwable cause) {
        super(message, cause);
    }

    public ExporterException(Throwable cause) {
        super(cause);
    }

    public ExporterException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
