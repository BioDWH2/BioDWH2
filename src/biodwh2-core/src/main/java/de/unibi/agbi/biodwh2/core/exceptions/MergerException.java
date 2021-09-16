package de.unibi.agbi.biodwh2.core.exceptions;

public class MergerException extends Exception {
    private static final long serialVersionUID = 4042964056603443556L;

    public MergerException() {
        super();
    }

    public MergerException(final String message) {
        super(message);
    }

    public MergerException(final String message, final Throwable cause) {
        super(message, cause);
    }

    public MergerException(final Throwable cause) {
        super(cause);
    }

    public MergerException(final String message, final Throwable cause, final boolean enableSuppression,
                           final boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
