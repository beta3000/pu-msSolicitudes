package rodriguez.ciro.api.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;
import rodriguez.ciro.model.exception.EmailEnUsoException;

import java.time.LocalDateTime;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(EmailEnUsoException.class)
    public Mono<ResponseEntity<ErrorResponse>> handleEmailEnUsoException(
            EmailEnUsoException ex, ServerWebExchange exchange) {
        log.warn("Conflicto de email detectado: {}", ex.getMessage());
        
        ErrorResponse errorResponse = ErrorResponse.builder()
                .error("Conflict")
                .message(ex.getMessage())
                .status(HttpStatus.CONFLICT.value())
                .timestamp(LocalDateTime.now())
                .path(exchange.getRequest().getPath().value())
                .build();
        
        return Mono.just(ResponseEntity.status(HttpStatus.CONFLICT).body(errorResponse));
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public Mono<ResponseEntity<ErrorResponse>> handleIllegalArgumentException(
            IllegalArgumentException ex, ServerWebExchange exchange) {
        log.warn("Argumento inválido: {}", ex.getMessage());
        
        ErrorResponse errorResponse = ErrorResponse.builder()
                .error("Bad Request")
                .message(ex.getMessage())
                .status(HttpStatus.BAD_REQUEST.value())
                .timestamp(LocalDateTime.now())
                .path(exchange.getRequest().getPath().value())
                .build();
        
        return Mono.just(ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse));
    }

    @ExceptionHandler(Exception.class)
    public Mono<ResponseEntity<ErrorResponse>> handleGenericException(
            Exception ex, ServerWebExchange exchange) {
        log.error("Error interno del servidor: {}", ex.getMessage(), ex);
        
        ErrorResponse errorResponse = ErrorResponse.builder()
                .error("Internal Server Error")
                .message("Error interno del servidor")
                .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
                .timestamp(LocalDateTime.now())
                .path(exchange.getRequest().getPath().value())
                .build();
        
        return Mono.just(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse));
    }
}