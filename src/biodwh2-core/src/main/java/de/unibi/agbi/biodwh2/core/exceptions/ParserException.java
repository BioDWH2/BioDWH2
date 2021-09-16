package de.unibi.agbi.biodwh2.core.exceptions;

public abstract class ParserException extends Exception {
    private static final long serialVersionUID = -1217000798823977025L;

    public ParserException() {
        super();
    }

    public ParserException(final String message) {
        super(message);
    }

    public ParserException(final String message, final Throwable cause) {
        super(message, cause);
    }

    public ParserException(final Throwable cause) {
        super(cause);
    }

    public ParserException(final String message, final Throwable cause, final boolean enableSuppression,
                           final boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
