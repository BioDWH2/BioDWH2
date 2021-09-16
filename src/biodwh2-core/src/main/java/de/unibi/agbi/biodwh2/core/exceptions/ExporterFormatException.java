package de.unibi.agbi.biodwh2.core.exceptions;

public class ExporterFormatException extends ExporterException {
    private static final long serialVersionUID = -3234100921721868307L;

    public ExporterFormatException() {
        super();
    }

    public ExporterFormatException(final String message) {
        super(message);
    }

    public ExporterFormatException(final String message, final Throwable cause) {
        super(message, cause);
    }

    public ExporterFormatException(final Throwable cause) {
        super(cause);
    }

    public ExporterFormatException(final String message, final Throwable cause, final boolean enableSuppression,
                                   final boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
