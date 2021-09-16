package de.unibi.agbi.biodwh2.core.exceptions;

public class WorkspaceException extends RuntimeException {
    private static final long serialVersionUID = 1294422219627527896L;

    public WorkspaceException() {
        super();
    }

    public WorkspaceException(final String message) {
        super(message);
    }

    public WorkspaceException(final String message, final Throwable cause) {
        super(message, cause);
    }

    public WorkspaceException(final Throwable cause) {
        super(cause);
    }

    public WorkspaceException(final String message, final Throwable cause, final boolean enableSuppression,
                              final boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
