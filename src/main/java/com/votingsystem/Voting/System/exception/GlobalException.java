package com.votingsystem.Voting.System.exception;

import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalException {
    @ExceptionHandler(AgeException.class)
    public String ageexception(AgeException exception, Model model){
        model.addAttribute("error",exception.getMessage());
        return "error";

    }
    @ExceptionHandler(UserNotFoundException.class)
    public String ageexception(UserNotFoundException exception, Model model){
        model.addAttribute("error",exception.getMessage());
        return "error";

    }

    @ExceptionHandler(Exception.class)
    public String handleGeneralException(Exception exception, Model model) {
        // Exception message ko clean karke bhej rahe hain
        model.addAttribute("errorMessage", exception.getMessage());
        model.addAttribute("errorType", exception.getClass().getSimpleName());
        return "error"; // Ye error.html ko call karega
    }
}
