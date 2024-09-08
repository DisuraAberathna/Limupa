/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package controller;

import com.google.gson.Gson;
import dto.ResponseDTO;
import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 *
 * @author SINGER
 */
@WebServlet(name = "Signout", urlPatterns = {"/Signout"})
public class Signout extends HttpServlet {
    
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        ResponseDTO responseDTO = new ResponseDTO();
        Gson gson = new Gson();
        
        req.getSession().invalidate();
        responseDTO.setOk(true);
        
        resp.setContentType("application/json");
        resp.getWriter().write(gson.toJson(responseDTO));
    }
    
}
