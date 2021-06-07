package de.unibi.agbi.biodwh2.core.io.mvstore;

public class MVStoreIndexException extends RuntimeException {
    private static final long serialVersionUID = -214953597637206495L;

    public MVStoreIndexException() {
    }

    public MVStoreIndexException(String message) {
        super(message);
    }

    public MVStoreIndexException(String message, Throwable cause) {
        super(message, cause);
    }

    public MVStoreIndexException(Throwable cause) {
        super(cause);
    }

    public MVStoreIndexException(String message, Throwable cause, boolean enableSuppression,
                                 boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
