package de.unibi.agbi.biodwh2.core.exceptions;

public class WorkspaceException extends RuntimeException {
    private static final long serialVersionUID = 1294422219627527896L;

    public WorkspaceException() {
        super();
    }

    public WorkspaceException(String message) {
        super(message);
    }

    public WorkspaceException(String message, Throwable cause) {
        super(message, cause);
    }

    public WorkspaceException(Throwable cause) {
        super(cause);
    }

    public WorkspaceException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
