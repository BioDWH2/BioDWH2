package de.unibi.agbi.biodwh2.core.exceptions;

public abstract class ExporterFormatException extends ExporterException {
    public ExporterFormatException() {
    }

    public ExporterFormatException(String message) {
        super(message);
    }

    public ExporterFormatException(String message, Throwable cause) {
        super(message, cause);
    }

    public ExporterFormatException(Throwable cause) {
        super(cause);
    }

    public ExporterFormatException(String message, Throwable cause, boolean enableSuppression,
                                   boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
