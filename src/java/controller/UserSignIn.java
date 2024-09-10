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
import javax.servlet.http.Cookie;
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
@WebServlet(name = "UserSignIn", urlPatterns = {"/UserSignIn"})
public class UserSignIn extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        ResponseDTO responseDTO = new ResponseDTO();

        Gson gson = new Gson();
        JsonObject reqObject = gson.fromJson(req.getReader(), JsonObject.class);

        String email = reqObject.get("email").getAsString();
        String password = reqObject.get("password").getAsString();
        boolean remember_me = reqObject.get("remember_me").getAsBoolean();

        if (email.isEmpty()) {
            responseDTO.setMsg("Please enter your email!");
        } else if (password.isEmpty()) {
            responseDTO.setMsg("Please enter your password!");
        } else {
            Session session = HibernateUtil.getSessionFactory().openSession();

            Criteria criteria = session.createCriteria(User.class);
            criteria.add(Restrictions.eq("email", email));
            criteria.add(Restrictions.eq("password", password));

            if (!criteria.list().isEmpty()) {
                User user = (User) criteria.list().get(0);

                if (user.getStatus() == 1) {
                    if (!user.getVerification().equals("Verified")) {
                        req.getSession().setAttribute("email", email);
                        responseDTO.setMsg("Not Verified");
                    } else {
                        UserDTO userDTO = new UserDTO();
                        userDTO.setId(user.getId());
                        userDTO.setF_name(user.getF_name());
                        userDTO.setL_name(user.getL_name());
                        userDTO.setEmail(email);
                        userDTO.setPassword(null);

                        req.getSession(true).setAttribute("user", userDTO);

                        if (remember_me) {
                            Cookie emailCookie = new Cookie("email", email);
                            Cookie passwordCookie = new Cookie("password", password);

                            emailCookie.setMaxAge(60 * 60 * 24 * 365);

                            resp.addCookie(emailCookie);
                            resp.addCookie(passwordCookie);
                        }

                        responseDTO.setOk(true);
                    }
                } else {
                    responseDTO.setMsg("Your account was suspended!");
                }
            } else {
                responseDTO.setMsg("Invalid credentials!");
            }
            session.close();
        }

        resp.setContentType("application/json");
        resp.getWriter().write(gson.toJson(responseDTO));
    }

}
