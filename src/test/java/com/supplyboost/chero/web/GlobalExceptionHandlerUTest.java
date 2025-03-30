package com.supplyboost.chero.web;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.servlet.ModelAndView;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@ExtendWith(MockitoExtension.class)
public class GlobalExceptionHandlerUTest {

    @Test
    void givenIllegalArgumentException_whenHandleIllegalArgumentException_thenReturnModelAndView() {
        GlobalExceptionHandler handler = new GlobalExceptionHandler();

        ModelAndView modelAndView = handler.handleIllegalArgumentException();

        assertNotNull(modelAndView);
        assertEquals("error-page", modelAndView.getViewName());
        assertEquals("Error for that action", modelAndView.getModel().get("errorMessage"));
    }

    @Test
    void givenNoArgs_whenHandleNotFoundException_thenReturnModelAndView() {
        GlobalExceptionHandler handler = new GlobalExceptionHandler();

        ModelAndView modelAndView = handler.handleNotFoundException();

        assertNotNull(modelAndView);
        assertEquals("error-page", modelAndView.getViewName());
        assertEquals("Not found", modelAndView.getModel().get("errorMessage"));
    }

    @Test
    void givenException_whenHandleAnyException_thenReturnModelAndViewWithMessage() {
        GlobalExceptionHandler handler = new GlobalExceptionHandler();

        Exception exception = new Exception("Unexpected error occurred");

        ModelAndView modelAndView = handler.handleAnyException(exception);

        assertNotNull(modelAndView);
        assertEquals("error-page", modelAndView.getViewName());
        assertEquals("Unexpected error occurred", modelAndView.getModel().get("errorMessage"));
    }

}
