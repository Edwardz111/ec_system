package com.ec.config;

import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;

/**
 * 页面（Thymeleaf）全局异常 —— 原 ec 模块
 */
@ControllerAdvice
public class PageExceptionHandler {

    @ExceptionHandler(Exception.class)
    public ModelAndView handlePageException(Exception ex, HttpServletRequest request) {
        System.err.println("页面异常: " + request.getRequestURI() + " - " + ex.getMessage());
        ex.printStackTrace();
        ModelAndView mv = new ModelAndView("error");
        String msg = ex.getMessage();
        if (msg == null || msg.isEmpty()) {
            msg = ex.getClass().getSimpleName();
        }
        mv.addObject("error", msg);
        return mv;
    }
}
