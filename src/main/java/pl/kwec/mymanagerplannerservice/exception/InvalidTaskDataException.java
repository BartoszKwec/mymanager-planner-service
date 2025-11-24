package pl.kwec.mymanagerplannerservice.exception;

public class InvalidTaskDataException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public InvalidTaskDataException(final String message) {
        super(message);
    }

    public InvalidTaskDataException(final String message, final Throwable cause) {
        super(message, cause);
    }
}
