/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package controller;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import dto.ResponseDTO;
import dto.UserDTO;
import entity.User;
import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
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

        Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();
        UserDTO userDTO = gson.fromJson(req.getReader(), UserDTO.class);

        Session session = HibernateUtil.getSessionFactory().openSession();

        Criteria criteria = session.createCriteria(User.class);
        criteria.add(Restrictions.eq("email", userDTO.getEmail()));
        criteria.add(Restrictions.eq("password", userDTO.getPassword()));

        if (!criteria.list().isEmpty()) {
            User user = (User) criteria.list().get(0);

            if (!user.getVerification().equals("Verified")) {
                req.getSession().setAttribute("email", userDTO.getEmail());
                responseDTO.setMsg("Not Verified");
            } else {
                userDTO.setId(user.getId());
                userDTO.setF_name(user.getF_name());
                userDTO.setL_name(user.getL_name());
                userDTO.setEmail(user.getEmail());
                userDTO.setPassword(null);
                HttpSession httpSession = req.getSession(true);
                httpSession.setAttribute("user", userDTO);

                resp.setHeader("Set-Cookie", "JSESSIONID=" + httpSession.getId() + "; Domain=localhost:3000; Path=/signin; HttpOnly; SameSite=None; Secure");

                responseDTO.setOk(true);
                responseDTO.setMsg("Successfully sign in");
//                responseDTO.setMsg(gson.toJson(userDTO));
            }
        } else {
            responseDTO.setMsg("Invalid credentials");
        }
        session.close();

        resp.setContentType("application/json");
        resp.getWriter().write(gson.toJson(responseDTO));
    }

}
