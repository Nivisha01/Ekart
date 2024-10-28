package com.reljicd.controller;

import org.springframework.boot.web.error.ErrorAttributeOptions;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

@RestController
@RequestMapping("/error")
public class CustomErrorController implements ErrorController {

    /**
     * Handles the default /error route and returns the error view.
     */
    @RequestMapping
    public ModelAndView handleError(HttpServletRequest request) {
        return new ModelAndView("error");
    }

    /**
     * Handles 403 Forbidden errors and returns a 403 error view.
     */
    @GetMapping("/403")
    public ModelAndView handle403() {
        return new ModelAndView("403");
    }

    /**
     * Optional: Overrides the error path for custom behavior (no longer required in newer Spring Boot versions).
     */
    @Deprecated
    public String getErrorPath() {
        return "/error";
    }
}
