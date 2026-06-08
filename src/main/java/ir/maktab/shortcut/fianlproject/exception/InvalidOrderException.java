package ir.maktab.shortcut.fianlproject.exception;

public class InvalidOrderException extends RuntimeException  {
    public  InvalidOrderException() {
        super("Proposed price cannot be less than base price");
    }
}
