/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fr.ensimag.loupsgarous.controller;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 *
 * @author baplar
 */
public class ErrorManager {
    public static void errorPage(HttpServletRequest request,
            HttpServletResponse response, String message) throws ServletException, IOException {
        request.setAttribute("message", message);
        request.getRequestDispatcher("/WEB-INF/error.jsp").forward(request, response);        
    }

    public static void errorPage(HttpServletRequest request,
            HttpServletResponse response, Exception e) throws ServletException, IOException {
        request.setAttribute("message", e.getMessage());
        request.setAttribute("exception", e);
        request.getRequestDispatcher("/WEB-INF/error.jsp").forward(request, response);        
    }
}
