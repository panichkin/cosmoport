package com.space.controller.status;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.NOT_FOUND, reason = "Ship not found")
public class NotFoundShipException extends RuntimeException {
    public NotFoundShipException() {
        super();
    }

    public NotFoundShipException(String msg) {
        super(msg);
    }
}
