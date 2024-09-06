/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package controller;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import dto.ResponseDTO;
import dto.UserDTO;
import entity.User;
import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import model.HibernateUtil;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;

/**
 *
 * @author SINGER
 */
@WebServlet(name = "UserVerification", urlPatterns = {"/UserVerification"})
public class UserVerification extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        ResponseDTO responseDTO = new ResponseDTO();

        Gson gson = new Gson();
        JsonObject reqObject = gson.fromJson(req.getReader(), JsonObject.class);
        String otp = reqObject.get("otp").getAsString();
        
        if (req.getSession().getAttribute("email") != null) {
            String email = req.getSession().getAttribute("email").toString();
            
            Session session = HibernateUtil.getSessionFactory().openSession();
            Criteria criteria = session.createCriteria(User.class);
            criteria.add(Restrictions.eq("email", email));
            criteria.add(Restrictions.eq("verification", otp));
            
            if (!criteria.list().isEmpty()) {
                User user = (User) criteria.list().get(0);
                user.setVerification("Verified");
                
                session.update(user);
                session.beginTransaction().commit();
                
                UserDTO userDTO = new UserDTO();
                userDTO.setId(user.getId());
                userDTO.setF_name(user.getF_name());
                userDTO.setL_name(user.getL_name());
                userDTO.setEmail(email);
                req.getSession().removeAttribute("email");
                req.getSession().setAttribute("user", userDTO);
                
                responseDTO.setOk(true);
            } else {
                responseDTO.setMsg("Invalid verification code");
            }
        } else {
            responseDTO.setMsg("Verification unavailable please sign in");
        }
        
        resp.setContentType("application/json");
        resp.getWriter().write(gson.toJson(responseDTO));
    }

}
