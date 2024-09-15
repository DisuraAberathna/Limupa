/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package controller;

import com.google.gson.Gson;
import dto.ResponseDTO;
import dto.UserDTO;
import entity.Address;
import entity.User;
import java.io.IOException;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import model.HibernateUtil;
import model.Validate;
import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;

/**
 *
 * @author SINGER
 */
@WebServlet(name = "UpdateAddress", urlPatterns = {"/UpdateAddress"})
public class UpdateAddress extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Gson gson = new Gson();
        ResponseDTO responseDTO = new ResponseDTO();

        String id = req.getParameter("id");

        if (!Validate.isInteger(id)) {
            responseDTO.setMsg("Product not found! Please try again later.");
        } else {
            Session session = HibernateUtil.getSessionFactory().openSession();

            try {
                if (req.getSession().getAttribute("user") != null) {
                    UserDTO userDTO = (UserDTO) req.getSession().getAttribute("user");

                    Criteria userCriteria = session.createCriteria(User.class);
                    userCriteria.add(Restrictions.eq("id", userDTO.getId()));

                    if (!userCriteria.list().isEmpty()) {
                        User user = (User) userCriteria.list().get(0);

                        Criteria addressCriteria = session.createCriteria(Address.class);
                        addressCriteria.add(Restrictions.eq("user", user));

                        if (!addressCriteria.list().isEmpty()) {
                            List<Address> addressList = addressCriteria.list();

                            for (Address address : addressList) {
                                if (address.getId() == Integer.parseInt(id)) {
                                    address.setStatus(1);
                                } else {
                                    address.setStatus(0);
                                }
                                session.update(address);
                                session.beginTransaction().commit();
                            }

                            responseDTO.setOk(true);
                        } else {
                            responseDTO.setMsg("User has not added address yet!");
                        }
                    } else {
                        responseDTO.setMsg("Invalid user!");
                    }
                } else {
                    responseDTO.setMsg("Invalid sign in! Please sign in again.");
                }
            } catch (HibernateException e) {
                System.out.println(e.getMessage());
                responseDTO.setMsg("Unable to process request!");
            }
            session.close();
        }

        resp.setContentType("application/json");
        resp.getWriter().write(gson.toJson(responseDTO));
    }
}
