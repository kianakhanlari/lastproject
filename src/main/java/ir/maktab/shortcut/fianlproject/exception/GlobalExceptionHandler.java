package ir.maktab.shortcut.fianlproject.exception;

import jakarta.persistence.EntityNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {


  @ExceptionHandler(ActiveJobException.class)
  public ResponseEntity<ApiError> handleActiveJob(ActiveJobException ex) {
      return ResponseEntity
              .status(HttpStatus.BAD_REQUEST)
              .body(new ApiError("ACTIVE_JOB", ex.getMessage()));
  }

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<ApiError> handleNotFound(EntityNotFoundException ex) {
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(new ApiError("NOT_FOUND", ex.getMessage()));
    }
    @ExceptionHandler(NotApprovedException.class)
    public ResponseEntity<ApiError> handleNotApprovedException(NotApprovedException ex) {
        ApiError error = new ApiError("SPECIALIST_NOT_APPROVED", ex.getMessage());
        return new ResponseEntity<>(error, HttpStatus.FORBIDDEN);
    }
    @ExceptionHandler(DuplicateException.class)
    public ResponseEntity<ApiError> handleDuplicateException(DuplicateException ex) {
        ApiError error = new ApiError("DUPLICATE_RESOURCE", ex.getMessage());
        return ResponseEntity.status(HttpStatus.CONFLICT).body(error);
    }
    @ExceptionHandler(InvalidOrderException.class)
    public ResponseEntity<ApiError> invalidOrder(InvalidOrderException e) {
        ApiError error = new ApiError("INVALID_ORDER", e.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

}
