package de.unibi.agbi.biodwh2.core.exceptions;

public class UpdaterOnlyManuallyException extends UpdaterException {
    public UpdaterOnlyManuallyException() {
    }

    public UpdaterOnlyManuallyException(String message) {
        super(message);
    }

    public UpdaterOnlyManuallyException(String message, Throwable cause) {
        super(message, cause);
    }

    public UpdaterOnlyManuallyException(Throwable cause) {
        super(cause);
    }

    public UpdaterOnlyManuallyException(String message, Throwable cause, boolean enableSuppression,
                                        boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
