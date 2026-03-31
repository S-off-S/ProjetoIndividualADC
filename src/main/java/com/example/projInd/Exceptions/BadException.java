package com.example.projInd.Exceptions;

import lombok.Getter;

@Getter
public class BadException extends RuntimeException{
    private final String code;

    public BadException(InvalidError error) {
        super(error.getMessage());
        this.code = error.getCode();
    }
}
