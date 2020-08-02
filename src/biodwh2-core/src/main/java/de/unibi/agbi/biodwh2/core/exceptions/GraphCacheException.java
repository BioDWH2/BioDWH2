package de.unibi.agbi.biodwh2.core.exceptions;

public class GraphCacheException extends RuntimeException {
    private static final long serialVersionUID = -7886311185762613615L;

    public GraphCacheException() {
    }

    public GraphCacheException(String s) {
        super(s);
    }

    public GraphCacheException(String s, Throwable throwable) {
        super(s, throwable);
    }

    public GraphCacheException(Throwable throwable) {
        super(throwable);
    }

    public GraphCacheException(String s, Throwable throwable, boolean b, boolean b1) {
        super(s, throwable, b, b1);
    }
}
