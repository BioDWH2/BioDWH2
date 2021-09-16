package de.unibi.agbi.biodwh2.core.exceptions;

public class DataSourceException extends Exception {
    private static final long serialVersionUID = 4049753259769090069L;

    public DataSourceException() {
        super();
    }

    public DataSourceException(final String message) {
        super(message);
    }

    public DataSourceException(final String message, final Throwable cause) {
        super(message, cause);
    }

    public DataSourceException(final Throwable cause) {
        super(cause);
    }

    public DataSourceException(final String message, final Throwable cause, final boolean enableSuppression,
                               final boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
