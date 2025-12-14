package com.stefanini.desafio.todolistapi.application.exception;

import com.stefanini.desafio.todolistapi.domain.service.TaskNotFoundException;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Manipulador de exceções global para a aplicação.
 * Captura exceções específicas e as formata em uma resposta JSON padronizada.
 */
@ControllerAdvice
public class CustomExceptionHandler extends ResponseEntityExceptionHandler {

    private final MessageSource messageSource;

    public CustomExceptionHandler(MessageSource messageSource) {
        this.messageSource = messageSource;
    }

    /**
     * Manipula a exceção {@link TaskNotFoundException} lançada quando uma tarefa não é encontrada.
     * Retorna uma resposta HTTP 404 (Not Found) com uma mensagem clara obtida do messages.properties.
     * @param ex A exceção capturada.
     * @return Um ResponseEntity contendo o corpo do erro e o status HTTP 404.
     */
    @ExceptionHandler(TaskNotFoundException.class)
    public ResponseEntity<Object> handleTaskNotFound(TaskNotFoundException ex) {
        String message = messageSource.getMessage(
                "task.not.found",
                new Object[]{ex.getTaskId()},
                LocaleContextHolder.getLocale()
        );

        Map<String, Object> body = new LinkedHashMap<>();
        body.put("timestamp", LocalDateTime.now());
        body.put("status", HttpStatus.NOT_FOUND.value());
        body.put("error", "Not Found");
        body.put("message", message);

        return new ResponseEntity<>(body, HttpStatus.NOT_FOUND);
    }

    /**
     * Manipula exceções de conversão de tipo de argumento (ex: String inválida para UUID).
     * Retorna uma resposta HTTP 400 (Bad Request) com uma mensagem personalizada em português.
     * @param ex A exceção capturada.
     * @return Um ResponseEntity contendo o erro formatado.
     */
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<Object> handleMethodArgumentTypeMismatch(MethodArgumentTypeMismatchException ex) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("timestamp", LocalDateTime.now());
        body.put("status", HttpStatus.BAD_REQUEST.value());
        body.put("error", "Bad Request");

        String valorEnviado = Objects.toString(ex.getValue(), "null");
        String nomeParametro = ex.getName();
        
        // Mensagem personalizada: "Falha ao converter 'id' com o valor: 'valor enviado'"
        String message = String.format("Falha ao converter '%s' com o valor: '%s'", nomeParametro, valorEnviado);
        
        body.put("message", message);

        return new ResponseEntity<>(body, HttpStatus.BAD_REQUEST);
    }

    /**
     * Manipula exceções de validação de argumentos de método, como as geradas pela anotação {@code @Valid}.
     * Retorna uma resposta HTTP 400 (Bad Request) com detalhes sobre os campos inválidos.
     * @param ex A exceção {@link MethodArgumentNotValidException} capturada.
     * @param headers Os cabeçalhos da resposta.
     * @param status O status HTTP.
     * @param request O contexto da requisição web.
     * @return Um ResponseEntity contendo os detalhes da falha de validação e o status HTTP 400.
     */
    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(
            MethodArgumentNotValidException ex, HttpHeaders headers, HttpStatusCode status, WebRequest request) {

        Map<String, Object> body = new LinkedHashMap<>();
        body.put("timestamp", LocalDateTime.now());
        body.put("status", status.value());
        body.put("error", "Bad Request");

        // Captura todos os erros de campo e os lista
        List<String> errors = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(error -> {
                    String fieldName = error.getField();
                    // Tenta resolver a mensagem do erro usando o MessageSource
                    String errorMessage = messageSource.getMessage(error, LocaleContextHolder.getLocale());
                    return fieldName + ": " + errorMessage;
                })
                .collect(Collectors.toList());

        body.put("message", "Validation Failed");
        body.put("details", errors);

        return new ResponseEntity<>(body, status);
    }
}
