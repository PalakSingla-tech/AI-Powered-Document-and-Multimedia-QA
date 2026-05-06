package com.assignment.AI_Powered.Document.Multimedia.Q.A;

import com.assignment.AI_Powered.Document.Multimedia.Q.A.exception.GlobalExceptionHandler;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class GlobalExceptionHandlerTest {
    private final GlobalExceptionHandler handler = new GlobalExceptionHandler();

    @Test
    void testHandleBadRequest()
    {
        ResponseEntity<String> response = handler.handleBadRequest(new IllegalArgumentException("Invalid input"));
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Invalid input", response.getBody());
    }
    @Test
    void testHandleGeneral() {
        ResponseEntity<String> response = handler.handleGeneral(new Exception("System failure"));
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertEquals("Something went wrong", response.getBody());
    }
}
