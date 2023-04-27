package jpabook.jpashop.exception;

public class NotenoughStockException extends RuntimeException{
    public NotenoughStockException() {
        super();
    }

    public NotenoughStockException(String message) {
        super(message);
    }

    public NotenoughStockException(String message, Throwable cause) {
        super(message, cause);
    }

    public NotenoughStockException(Throwable cause) {
        super(cause);
    }

    protected NotenoughStockException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
