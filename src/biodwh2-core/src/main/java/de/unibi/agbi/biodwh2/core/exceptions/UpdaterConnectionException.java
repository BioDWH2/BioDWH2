package de.unibi.agbi.biodwh2.core.exceptions;

public class UpdaterConnectionException extends UpdaterException {
    private static final long serialVersionUID = 2832708193353784642L;

    public UpdaterConnectionException() {
        super();
    }

    public UpdaterConnectionException(final String message) {
        super(message);
    }

    public UpdaterConnectionException(final String message, final Throwable cause) {
        super(message, cause);
    }

    public UpdaterConnectionException(final Throwable cause) {
        super(cause);
    }

    public UpdaterConnectionException(final String message, final Throwable cause, final boolean enableSuppression,
                                      final boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
