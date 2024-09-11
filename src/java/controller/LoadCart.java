/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package controller;

import com.google.gson.Gson;
import dto.CartDTO;
import dto.UserDTO;
import entity.Cart;
import entity.Product;
import entity.User;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import model.HibernateUtill;
import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;

/**
 *
 * @author SINGER
 */
@WebServlet(name = "LoadCart", urlPatterns = {"/LoadCart"})
public class LoadCart extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Gson gson = new Gson();
        HttpSession httpSession = req.getSession();
        Session session = HibernateUtill.getSessionFactory().openSession();
        ArrayList<CartDTO> cartDTOList = new ArrayList<>();

        try {
            if (httpSession.getAttribute("user") != null) {
                UserDTO userDTO = (UserDTO) httpSession.getAttribute("user");

                Criteria userCriteria = session.createCriteria(User.class);
                userCriteria.add(Restrictions.eq("id", userDTO.getId()));
                User user = (User) userCriteria.uniqueResult();

                Criteria cartCriteria = session.createCriteria(Cart.class);
                cartCriteria.add(Restrictions.eq("user", user));

                List<Cart> cartList = cartCriteria.list();

                for (Cart cart : cartList) {
                    CartDTO cartDTO = new CartDTO();
                    Product product = cart.getProduct();

                    product.setUser(null);
                    cartDTO.setProduct(product);
                    cartDTO.setQty(cart.getQty());
                    cartDTOList.add(cartDTO);
                }
            } else {
                if (httpSession.getAttribute("sessionCart") != null) {

                    cartDTOList = (ArrayList<CartDTO>) httpSession.getAttribute("sessionCart");

                    for (CartDTO cart_DTO : cartDTOList) {
                        cart_DTO.getProduct().setUser(null);
                    }
                }
            }
        } catch (HibernateException e) {
            System.out.println(e.getMessage());
        }
        session.close();

        resp.setContentType("application/json");
        resp.getWriter().write(gson.toJson(cartDTOList));
    }
}
