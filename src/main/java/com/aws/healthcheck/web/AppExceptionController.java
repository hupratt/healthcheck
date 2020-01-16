package com.aws.healthcheck.web;

import javax.validation.ConstraintViolationException;

import com.jb.commissions.exceptions.ErrorResponse;

import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingPathVariableException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.async.AsyncRequestTimeoutException;
import org.springframework.web.servlet.NoHandlerFoundException;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestControllerAdvice
public class AppExceptionController {

    @ExceptionHandler(value = { HttpMessageNotReadableException.class, MethodArgumentNotValidException.class,
            ConstraintViolationException.class })
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse badRequest(Exception e) {
        log.debug("CLIENT ERROR: The request sent to the website server is incorrect or corrupted msg={}", e);
        return new ErrorResponse(400, HttpStatus.BAD_REQUEST.getReasonPhrase());
    }

    @ExceptionHandler(value = { AuthenticationCredentialsNotFoundException.class })
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public ErrorResponse authenticationIsRequired(Exception e) {
        log.debug("CLIENT ERROR: The user is not logged in so the task cannot be accomplished msg={}", e);
        return new ErrorResponse(401, HttpStatus.UNAUTHORIZED.getReasonPhrase());
    }

    @ExceptionHandler(value = { InsufficientAuthenticationException.class })
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public ErrorResponse missingPermissions(Exception e) {
        log.debug("CLIENT ERROR: User is logged in but has insufficient permissions to perform the task msg={}", e);
        return new ErrorResponse(403, HttpStatus.FORBIDDEN.getReasonPhrase());
    }

    @ExceptionHandler(value = { NoHandlerFoundException.class })
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse resourceNotFound(Exception e) {
        log.debug("CLIENT ERROR: A resource could not be found msg={}", e);
        return new ErrorResponse(404, HttpStatus.NOT_FOUND.getReasonPhrase());
    }

    @ExceptionHandler(value = AsyncRequestTimeoutException.class)
    @ResponseStatus(HttpStatus.SERVICE_UNAVAILABLE)
    public ErrorResponse unableToHandleRequest(Exception e) {
        log.error("SERVER ERROR: server is probably temporarily overloaded or is under maintenance, msg={}", e);
        return new ErrorResponse(503, HttpStatus.SERVICE_UNAVAILABLE.getReasonPhrase());
    }

    @ExceptionHandler(value = MissingPathVariableException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponse handleServercrashes(Exception e) {
        log.error("SERVER ERROR: Unknown error: server crashed msg={}", e);
        return new ErrorResponse(500, HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase());
    }

}
