package com.example.demo.exceptions;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor

public class ErrorResponse {
    private String message;
    private HttpStatus status;
    private final LocalDateTime timestamp = LocalDateTime.now();
}
