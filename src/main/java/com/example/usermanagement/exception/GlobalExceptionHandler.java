package com.example.usermanagement.exception;

import java.util.HashMap;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.bind.MissingServletRequestParameterException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RestControllerAdvice
public class GlobalExceptionHandler {

	private static final Logger log =
        LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(UserNotFoundException.class)
    public ProblemDetail handleUserNotFound(UserNotFoundException ex) {

        ProblemDetail problemDetail =
                ProblemDetail.forStatus(HttpStatus.NOT_FOUND);

        problemDetail.setTitle("User Not Found");
        problemDetail.setDetail(ex.getMessage());

        return problemDetail;
    }

    @ExceptionHandler(EmailAlreadyExistsException.class)
    public ProblemDetail handleEmailAlreadyExists(
            EmailAlreadyExistsException ex) {

        ProblemDetail problemDetail =
                ProblemDetail.forStatus(HttpStatus.CONFLICT);

        problemDetail.setTitle("Email Already Exists");
        problemDetail.setDetail(ex.getMessage());

        return problemDetail;
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ProblemDetail handleValidation(
            MethodArgumentNotValidException ex) {

        ProblemDetail problemDetail =
                ProblemDetail.forStatus(HttpStatus.BAD_REQUEST);

        problemDetail.setTitle("Validation Failed");
        problemDetail.setDetail("One or more fields are invalid.");

        Map<String, String> errors = new HashMap<>();

        ex.getBindingResult()
                .getFieldErrors()
                .forEach(error ->
                        errors.put(
                                error.getField(),
                                error.getDefaultMessage()
                        )
                );

        problemDetail.setProperty("errors", errors);

        return problemDetail;
    }

	@ExceptionHandler(Exception.class)
	public ProblemDetail handleGenericException(Exception ex) {

		log.error("Unexpected error while processing request", ex);

		ProblemDetail problemDetail =
				ProblemDetail.forStatus(
						HttpStatus.INTERNAL_SERVER_ERROR
				);

		problemDetail.setTitle("Internal Server Error");
		problemDetail.setDetail(
				"An unexpected error occurred."
		);

		return problemDetail;
	}

	@ExceptionHandler(HttpMessageNotReadableException.class)
	public ProblemDetail handleMalformedJson(
			HttpMessageNotReadableException ex) {

		ProblemDetail problemDetail =
				ProblemDetail.forStatus(HttpStatus.BAD_REQUEST);

		problemDetail.setTitle("Malformed JSON");
		problemDetail.setDetail(
				"The request body contains invalid JSON."
		);

		return problemDetail;
	}

	@ExceptionHandler(MethodArgumentTypeMismatchException.class)
	public ProblemDetail handleTypeMismatch(
			MethodArgumentTypeMismatchException ex) {

		ProblemDetail problemDetail =
				ProblemDetail.forStatus(HttpStatus.BAD_REQUEST);

		problemDetail.setTitle("Invalid Parameter");
		problemDetail.setDetail(
				"Invalid value for parameter: " + ex.getName()
		);

		return problemDetail;
	}

	@ExceptionHandler(MissingServletRequestParameterException.class)
	public ProblemDetail handleMissingParameter(
			MissingServletRequestParameterException ex) {

		ProblemDetail problemDetail =
				ProblemDetail.forStatus(HttpStatus.BAD_REQUEST);

		problemDetail.setTitle("Missing Parameter");
		problemDetail.setDetail(
				"Required parameter is missing: " + ex.getParameterName()
		);

		return problemDetail;
	}
}