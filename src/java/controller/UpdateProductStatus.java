/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package controller;

import com.google.gson.Gson;
import dto.ResponseDTO;
import dto.UserDTO;
import entity.Product;
import entity.User;
import java.io.IOException;
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
@WebServlet(name = "UpdateProductStatus", urlPatterns = {"/UpdateProductStatus"})
public class UpdateProductStatus extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Gson gson = new Gson();
        ResponseDTO responseDTO = new ResponseDTO();

        String id = req.getParameter("id");
        String status = req.getParameter("status");

        if (!Validate.isInteger(id)) {
            responseDTO.setMsg("Something went wrong! Please try again.");
        } else if (status.isEmpty()) {
            responseDTO.setMsg("Something went wrong! Please try again.");
        } else {
            Session session = HibernateUtil.getSessionFactory().openSession();

            try {
                if (req.getSession().getAttribute("user") != null) {
                    UserDTO userDTO = (UserDTO) req.getSession().getAttribute("user");

                    Criteria userCriteria = session.createCriteria(User.class);
                    userCriteria.add(Restrictions.eq("id", userDTO.getId()));

                    if (!userCriteria.list().isEmpty()) {
                        User user = (User) userCriteria.list().get(0);

                        Criteria productCriteria = session.createCriteria(Product.class);
                        productCriteria.add(Restrictions.eq("user", user));
                        productCriteria.add(Restrictions.eq("id", Integer.valueOf(id)));

                        if (!productCriteria.list().isEmpty()) {
                            Product product = (Product) productCriteria.list().get(0);

                            if (status.equals("1")) {
                                product.setStatus(1);
                            } else {
                                product.setStatus(0);
                            }
                            session.update(product);
                            session.beginTransaction().commit();

                            responseDTO.setOk(true);
                        } else {
                            responseDTO.setMsg("Product not found! Please try again later.");
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
