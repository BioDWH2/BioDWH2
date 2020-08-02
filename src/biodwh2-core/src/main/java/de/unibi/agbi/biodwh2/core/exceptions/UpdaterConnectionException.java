package de.unibi.agbi.biodwh2.core.exceptions;

public class UpdaterConnectionException extends UpdaterException {
    private static final long serialVersionUID = 2832708193353784642L;

    public UpdaterConnectionException() {
    }

    public UpdaterConnectionException(String message) {
        super(message);
    }

    public UpdaterConnectionException(String message, Throwable cause) {
        super(message, cause);
    }

    public UpdaterConnectionException(Throwable cause) {
        super(cause);
    }

    public UpdaterConnectionException(String message, Throwable cause, boolean enableSuppression,
                                      boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
