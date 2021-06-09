package de.unibi.agbi.biodwh2.core.exceptions;

public class UpdaterOnlyManuallyException extends UpdaterException {
    private static final long serialVersionUID = -8800238787366251549L;

    public UpdaterOnlyManuallyException() {
        super();
    }

    public UpdaterOnlyManuallyException(final String message) {
        super(message);
    }

    public UpdaterOnlyManuallyException(final String message, final Throwable cause) {
        super(message, cause);
    }

    public UpdaterOnlyManuallyException(final Throwable cause) {
        super(cause);
    }

    public UpdaterOnlyManuallyException(final String message, final Throwable cause, final boolean enableSuppression,
                                        final boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
