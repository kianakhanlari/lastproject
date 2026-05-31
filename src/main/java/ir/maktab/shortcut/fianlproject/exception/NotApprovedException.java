package ir.maktab.shortcut.fianlproject.exception;

public class NotApprovedException extends RuntimeException {
    public NotApprovedException() {
        super("Specialist is not approved");
    }
    public NotApprovedException(String message) {
        super(message);
    }
}