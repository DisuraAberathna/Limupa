/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package model;

import java.io.IOException;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 *
 * @author SINGER
 */
@WebFilter(urlPatterns = {"/userVerify.html"})
public class checkReadyForVerify implements Filter {

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        System.out.println("checkSignedInUser Filter Initialized");
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse resp = (HttpServletResponse) response;

        if (req.getSession().getAttribute("email") != null || req.getSession().getAttribute("id") != null) {
            chain.doFilter(request, response);
        } else {
            resp.sendRedirect("userSignin.html");
        }
    }

    @Override
    public void destroy() {
        System.out.println("checkSignedInUser Filter Destroyed");
    }

}
