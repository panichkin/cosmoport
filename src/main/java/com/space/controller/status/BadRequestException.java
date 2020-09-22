package com.space.controller.status;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.BAD_REQUEST, reason = "Bad url format")
public class BadRequestException extends RuntimeException {
    public BadRequestException() {
        super();
    }
}