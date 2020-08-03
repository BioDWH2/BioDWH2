package de.unibi.agbi.biodwh2.core.exceptions;

public abstract class UpdaterException extends Exception {
    private static final long serialVersionUID = -3419111947147578768L;

    public UpdaterException() {
        super();
    }

    public UpdaterException(String message) {
        super(message);
    }

    public UpdaterException(String message, Throwable cause) {
        super(message, cause);
    }

    public UpdaterException(Throwable cause) {
        super(cause);
    }

    public UpdaterException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
