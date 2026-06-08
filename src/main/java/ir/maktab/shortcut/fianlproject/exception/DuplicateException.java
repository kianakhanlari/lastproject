package ir.maktab.shortcut.fianlproject.exception;

public class DuplicateException extends RuntimeException {

    public DuplicateException(String emailAlreadyExists) {
        super("Email already exists");
    }
}