package pl.kwec.mymanagerplannerservice.exception;

public class TaskNotFoundException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public TaskNotFoundException(final String message) {
        super(message);
    }

    public TaskNotFoundException(final String message, final Throwable cause) {
        super(message, cause);
    }
}
