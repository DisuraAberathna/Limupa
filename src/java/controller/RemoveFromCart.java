/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package controller;

import com.google.gson.Gson;
import dto.CartDTO;
import dto.ResponseDTO;
import dto.UserDTO;
import entity.Cart;
import entity.Product;
import entity.User;
import java.io.IOException;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import model.HibernateUtill;
import model.Validate;
import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.criterion.Restrictions;

/**
 *
 * @author SINGER
 */
@WebServlet(name = "RemoveFromCart", urlPatterns = {"/RemoveFromCart"})
public class RemoveFromCart extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Gson gson = new Gson();
        ResponseDTO responseDTO = new ResponseDTO();

        String id = req.getParameter("id");

        if (!Validate.isInteger(id)) {
            responseDTO.setMsg("Product not found! Please try again later.");
        } else {
            Session session = HibernateUtill.getSessionFactory().openSession();
            Transaction transaction = session.beginTransaction();

            try {
                int productId = Integer.parseInt(id);

                Product product = (Product) session.get(Product.class, productId);
                if (product != null) {
                    if (req.getSession().getAttribute("user") != null) {
                        UserDTO userDTO = (UserDTO) req.getSession().getAttribute("user");

                        Criteria userCriteria = session.createCriteria(User.class);
                        userCriteria.add(Restrictions.eq("id", userDTO.getId()));
                        User user = (User) userCriteria.uniqueResult();

                        Criteria cartCriteria = session.createCriteria(Cart.class);
                        cartCriteria.add(Restrictions.eq("user", user));
                        cartCriteria.add(Restrictions.eq("product", product));

                        if (!cartCriteria.list().isEmpty()) {
                            Cart cartItem = (Cart) cartCriteria.uniqueResult();
                            session.delete(cartItem);
                            transaction.commit();

                            responseDTO.setOk(true);
                        } else {
                            responseDTO.setMsg("Something went wrong! please try again later.");
                        }
                    } else {
                        List<CartDTO> sessionCart = (List<CartDTO>) req.getSession().getAttribute("sessionCart");

                        if (sessionCart != null) {
                            CartDTO cartItemToRemove = null;
                            for (CartDTO cartItem : sessionCart) {
                                if (cartItem.getProduct().getId() == productId) {
                                    cartItemToRemove = cartItem;
                                    break;
                                }
                            }

                            if (cartItemToRemove != null) {
                                sessionCart.remove(cartItemToRemove);
                                responseDTO.setOk(true);
                            } else {
                                responseDTO.setMsg("Product is not in the session cart!");
                            }
                        } else {
                            responseDTO.setMsg("Something went wrong! please try again later.");
                        }
                    }
                } else {
                    responseDTO.setMsg("Something went wrong! please try again later.");
                }
            } catch (NumberFormatException | HibernateException e) {
                System.out.println(e.getMessage());
                responseDTO.setMsg("Unable to process request!");

                if (transaction != null) {
                    transaction.rollback();
                }
            } finally {
                session.close();
            }
        }
        resp.setContentType("application/json");
        resp.getWriter().write(gson.toJson(responseDTO));
    }
}
