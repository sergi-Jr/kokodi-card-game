package kokodi.game.cardzen.handler;

import kokodi.game.cardzen.exception.DuplicateResourceException;
import kokodi.game.cardzen.exception.IllegalTurnAccept;
import kokodi.game.cardzen.exception.ResourceNotFoundException;
import kokodi.game.cardzen.exception.UnableToGameStart;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<String> handleResourceNotFound(ResourceNotFoundException exception) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(exception.getMessage());
    }

    @ExceptionHandler(DuplicateResourceException.class)
    public ResponseEntity<String> handleResourceDuplicated(DuplicateResourceException exception) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(exception.getMessage());
    }

    @ExceptionHandler(UnableToGameStart.class)
    public ResponseEntity<String> handleGameStartFailed(UnableToGameStart exception) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(exception.getMessage());
    }

    @ExceptionHandler(IllegalTurnAccept.class)
    public ResponseEntity<String> handleGameStartFailed(IllegalTurnAccept exception) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(exception.getMessage());
    }
}
