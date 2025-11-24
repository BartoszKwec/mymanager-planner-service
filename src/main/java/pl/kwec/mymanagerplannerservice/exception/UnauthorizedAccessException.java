package pl.kwec.mymanagerplannerservice.exception;

public class UnauthorizedAccessException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public UnauthorizedAccessException(final String message) {
        super(message);
    }

    public UnauthorizedAccessException(final String message, final Throwable cause) {
        super(message, cause);
    }
}
