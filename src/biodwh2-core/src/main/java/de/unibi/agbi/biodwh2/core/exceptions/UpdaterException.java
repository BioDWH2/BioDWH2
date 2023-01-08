package de.unibi.agbi.biodwh2.core.exceptions;

public class UpdaterException extends Exception {
    private static final long serialVersionUID = -3419111947147578768L;

    public UpdaterException() {
        super();
    }

    public UpdaterException(final String message) {
        super(message);
    }

    public UpdaterException(final String message, final Throwable cause) {
        super(message, cause);
    }

    public UpdaterException(final Throwable cause) {
        super(cause);
    }

    public UpdaterException(final String message, final Throwable cause, final boolean enableSuppression,
                            final boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
