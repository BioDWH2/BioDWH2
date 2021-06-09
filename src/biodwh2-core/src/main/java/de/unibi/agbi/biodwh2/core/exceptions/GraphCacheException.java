package de.unibi.agbi.biodwh2.core.exceptions;

public class GraphCacheException extends RuntimeException {
    private static final long serialVersionUID = -7886311185762613615L;

    public GraphCacheException() {
        super();
    }

    public GraphCacheException(final String message) {
        super(message);
    }

    public GraphCacheException(final String message, final Throwable cause) {
        super(message, cause);
    }

    public GraphCacheException(final Throwable cause) {
        super(cause);
    }

    public GraphCacheException(final String message, final Throwable cause, final boolean enableSuppression,
                               final boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
