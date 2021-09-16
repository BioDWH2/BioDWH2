package de.unibi.agbi.biodwh2.core.exceptions;

public class ExporterException extends RuntimeException {
    private static final long serialVersionUID = -5116753457000851091L;

    public ExporterException() {
        super();
    }

    public ExporterException(final String message) {
        super(message);
    }

    public ExporterException(final String message, final Throwable cause) {
        super(message, cause);
    }

    public ExporterException(final Throwable cause) {
        super(cause);
    }

    public ExporterException(final String message, final Throwable cause, final boolean enableSuppression, final boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
