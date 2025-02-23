package exception;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

	private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

	// Manejo para método HTTP no soportado
	@ExceptionHandler(HttpRequestMethodNotSupportedException.class)
	public ResponseEntity<ErrorResponse> handleMethodNotSupported(HttpRequestMethodNotSupportedException ex) {
		List<String> details = new ArrayList<>();
		details.add("Método HTTP no soportado: " + ex.getMethod());
		ErrorResponse error = new ErrorResponse(HttpStatus.METHOD_NOT_ALLOWED.value(), "Método HTTP no permitido",
				LocalDateTime.now(), details);
		log.error("HTTP Method Not Supported: {}", ex.getMessage(), ex);
		return new ResponseEntity<>(error, HttpStatus.METHOD_NOT_ALLOWED);
	}

	// Manejo de errores de validación
	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<ErrorResponse> handleValidationErrors(MethodArgumentNotValidException ex) {
		List<String> details = new ArrayList<>();
		for (FieldError fieldError : ex.getBindingResult().getFieldErrors()) {
			details.add(fieldError.getField() + ": " + fieldError.getDefaultMessage());
		}
		ErrorResponse errorResponse = new ErrorResponse(HttpStatus.BAD_REQUEST.value(), "Error de validación",
				LocalDateTime.now(), details);
		log.error("Validation error: {}", details, ex);
		return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
	}

	// Manejo de NoSuchElementException (por ejemplo, cuando un pedido no se
	// encuentra)
	@ExceptionHandler(NoSuchElementException.class)
	public ResponseEntity<ErrorResponse> handleNoSuchElement(NoSuchElementException ex) {
		List<String> details = new ArrayList<>();
		details.add(ex.getMessage());
		ErrorResponse errorResponse = new ErrorResponse(HttpStatus.NOT_FOUND.value(), "Elemento no encontrado",
				LocalDateTime.now(), details);
		log.error("No such element: {}", ex.getMessage(), ex);
		return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
	}

	// Manejo de IllegalArgumentException (argumentos inválidos)
	@ExceptionHandler(IllegalArgumentException.class)
	public ResponseEntity<ErrorResponse> handleIllegalArgument(IllegalArgumentException ex) {
		List<String> details = new ArrayList<>();
		details.add(ex.getMessage());
		ErrorResponse errorResponse = new ErrorResponse(HttpStatus.BAD_REQUEST.value(), "Argumento inválido",
				LocalDateTime.now(), details);
		log.error("Illegal argument: {}", ex.getMessage(), ex);
		return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
	}

	// Manejo genérico para todas las demás excepciones
	@ExceptionHandler(Exception.class)
	public ResponseEntity<ErrorResponse> handleGenericException(Exception ex) {
		List<String> details = new ArrayList<>();
		details.add(ex.getMessage());
		ErrorResponse errorResponse = new ErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(),
				"Error interno del servidor", LocalDateTime.now(), details);
		log.error("Internal server error: {}", ex.getMessage(), ex);
		return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
	}
}
