package com.example.projInd.controller;

import com.example.projInd.DTO.UserDTOS;
import com.example.projInd.Exceptions.BadException;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ExceptionHandlerController {
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<UserDTOS.Response<String>> handleInvalidInput(MethodArgumentNotValidException exp) {
        //could have just put bad requests but this is better
        return ResponseEntity.ok().body(new UserDTOS.Response<>("9906", "The call is using input data not following the correct specification"));
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<UserDTOS.Response<String>> handleInvalidInput() {
        //could have just put bad requests but this is better
        return ResponseEntity.ok().body(new UserDTOS.Response<>("9906", "The call is using input data not following the correct specification"));
    }

    @ExceptionHandler(BadException.class)
    public ResponseEntity<UserDTOS.Response<String>> handleInvalidInput(BadException exp) {
        //could have just put bad requests but this is better
        return ResponseEntity.ok().body(new UserDTOS.Response<>(exp.getCode(), exp.getMessage()));
    }
}
