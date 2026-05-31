package ir.maktab.shortcut.fianlproject.exception;

public class DuplicateException extends RuntimeException {

    public DuplicateException() {
        super("Email already exists");
    }
}