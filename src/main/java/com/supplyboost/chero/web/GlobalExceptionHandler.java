package com.supplyboost.chero.web;

import com.supplyboost.chero.user.model.User;
import com.supplyboost.chero.user.service.UserService;
import com.supplyboost.chero.web.dto.GameCharacterHeaderResponse;
import com.supplyboost.chero.web.mapper.DtoMapper;
import org.springframework.beans.TypeMismatchException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.nio.file.AccessDeniedException;

@ControllerAdvice
public class GlobalExceptionHandler {
    private final UserService userService;

    @Autowired
    public GlobalExceptionHandler(UserService userService) {
        this.userService = userService;
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(IllegalArgumentException.class)
    public ModelAndView handleIllegalArgumentException() {

        ModelAndView modelAndView = new ModelAndView("error-page");
        modelAndView.addObject("errorMessage", "Error for that action");

        return modelAndView;
    }

    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler({AccessDeniedException.class,
            NoResourceFoundException.class,
            TypeMismatchException.class})
    public ModelAndView handleNotFoundException() {

        ModelAndView modelAndView = new ModelAndView("error-page");
        modelAndView.addObject("errorMessage", "Not found");

        return modelAndView;
    }


    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(Exception.class)
    public ModelAndView handleAnyException(Exception e){

        ModelAndView modelAndView = new ModelAndView("error-page");
        modelAndView.addObject("errorMessage", e.getMessage());

        return modelAndView;
    }
}
