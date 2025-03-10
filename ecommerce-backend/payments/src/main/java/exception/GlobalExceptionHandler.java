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

	/**
	 * Método auxiliar para construir el objeto ErrorResponse.
	 */
	private ErrorResponse buildErrorResponse(HttpStatus status, String message, Exception ex) {
		List<String> details = new ArrayList<>();
		// Puedes agregar más detalles si lo deseas.
		details.add(ex.getMessage());
		return new ErrorResponse(status.value(), message, LocalDateTime.now(), details);
	}

	// Manejo para métodos HTTP no soportados
	@ExceptionHandler(HttpRequestMethodNotSupportedException.class)
	public ResponseEntity<ErrorResponse> handleMethodNotSupported(HttpRequestMethodNotSupportedException ex) {
		String message = "Método HTTP no permitido: " + ex.getMethod();
		ErrorResponse error = buildErrorResponse(HttpStatus.METHOD_NOT_ALLOWED, message, ex);
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
		ErrorResponse error = new ErrorResponse(HttpStatus.BAD_REQUEST.value(), "Error de validación",
				LocalDateTime.now(), details);
		log.error("Validation error: {}", details, ex);
		return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
	}

	// Manejo de excepciones cuando no se encuentra un elemento
	@ExceptionHandler(NoSuchElementException.class)
	public ResponseEntity<ErrorResponse> handleNoSuchElement(NoSuchElementException ex) {
		ErrorResponse error = buildErrorResponse(HttpStatus.NOT_FOUND, "Elemento no encontrado", ex);
		log.error("No such element: {}", ex.getMessage(), ex);
		return new ResponseEntity<>(error, HttpStatus.NOT_FOUND);
	}

	// Manejo de argumentos inválidos
	@ExceptionHandler(IllegalArgumentException.class)
	public ResponseEntity<ErrorResponse> handleIllegalArgument(IllegalArgumentException ex) {
		ErrorResponse error = buildErrorResponse(HttpStatus.BAD_REQUEST, "Argumento inválido", ex);
		log.error("Illegal argument: {}", ex.getMessage(), ex);
		return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
	}

	// Manejo genérico para todas las demás excepciones
	@ExceptionHandler(Exception.class)
	public ResponseEntity<ErrorResponse> handleGenericException(Exception ex) {
		ErrorResponse error = buildErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, "Error interno del servidor", ex);
		log.error("Internal server error: {}", ex.getMessage(), ex);
		return new ResponseEntity<>(error, HttpStatus.INTERNAL_SERVER_ERROR);
	}
}
